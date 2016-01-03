package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cProperties;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.Direction;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;
import java.util.Set;

public class MatrixDcMotorController implements DcMotorController {
    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY = (byte) 1;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED = (byte) 2;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 3;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 4;

    private static final int NUMBER_OF_MOTORS = 4;

    private static final byte BUSY_BIT_MASK = (byte) (1<<7);
    private static final byte UNKNOWN_BIT_MASK = (byte) (1<<3); //TODO what is this?

    private static final int BYTES_IN_INT = 4;
    private static final int START_ADDRESS = 4;

    private static final double POWER_MIN = -1.0d;
    private static final double POWER_MAX = 1.0d;
    private static final int POWER_RATIO = (byte) 100;
    private static final int BATTERY_RATIO = (byte) 40;

    private int battery;
    private DeviceMode controllerDeviceMode = DeviceMode.READ_ONLY;
    private final MatrixMasterController master;

    private final MotorInfo[] motorInfoList = new MotorInfo[NUMBER_OF_MOTORS];

    private class MotorInfo {
        public final int motor;
        public RunMode runMode = RunMode.RESET_ENCODERS;
        public boolean powerFloat;
        public int targetPosition;
        public int position;
        public byte deviceModeInfo;
        public double power;

        public MotorInfo(int motor) {
            validateMotor(motor);
            this.motor = motor;
            this.runMode = RunMode.RUN_WITHOUT_ENCODERS;
            this.powerFloat = true;
            motorInfoList[getIndex(motor)] = this;
        }

        public boolean isBusy() {
            return (deviceModeInfo & BUSY_BIT_MASK) == BUSY_BIT_MASK;
        }
    }

    private MotorInfo getMotorInfo(int motor) {
        validateMotor(motor);

        return motorInfoList[getIndex(motor)];
    }

    private int getIndex(int motor) {
        return motor - 1;
    }

    public MatrixDcMotorController(MatrixMasterController master) {
        this.master = master;
        master.registerMotorController(this);
        for (int motor = 0; motor < NUMBER_OF_MOTORS; motor++) {
            master.queueTransaction(new MatrixI2cTransaction((byte) motor, (byte) 0, 0, (byte) 0));
            new MotorInfo(motor);
        }
    }

    private byte runModeToFlagMatrix(RunMode mode) {
        switch (mode) {
            case RUN_USING_ENCODERS :
                return CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED;
            case RUN_WITHOUT_ENCODERS :
                return CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY;
            case RUN_TO_POSITION :
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case RESET_ENCODERS :
            default:
                return CHANNEL_MODE_FLAG_SELECT_RESET;
        }
    }

    protected RunMode flagMatrixToRunMode(byte flag) {
        switch (flag) {
            case CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY :
                return RunMode.RUN_WITHOUT_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED :
                return RunMode.RUN_USING_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION :
                return RunMode.RUN_TO_POSITION;
            case CHANNEL_MODE_FLAG_SELECT_RESET :
                return RunMode.RESET_ENCODERS;
            default:
                RobotLog.e("Invalid run mode flag " + flag);
                return RunMode.RUN_WITHOUT_ENCODERS;
        }
    }

    public boolean isBusy(int motor) {
        MatrixI2cTransaction matrixI2cTransaction = new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_MODE);
        this.master.queueTransaction(matrixI2cTransaction);
        this.master.waitOnRead();
        return getMotorInfo(matrixI2cTransaction.motor).isBusy();
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        this.controllerDeviceMode = mode;
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.controllerDeviceMode;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        MotorInfo motorInfo = getMotorInfo(motor);
        if (motorInfo.powerFloat || mode != motorInfo.runMode) {
            this.master.queueTransaction(
                    new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_MODE, runModeToFlagMatrix(mode)));
            motorInfo.runMode = mode;
            motorInfo.powerFloat = (mode == RunMode.RESET_ENCODERS);
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        return getMotorInfo(motor).runMode;
    }

    public void setMotorPowerFloat(int motor) {
        if (!getMotorInfo(motor).powerFloat) {
            getMotorInfo(motor).powerFloat = true;
            this.master.queueTransaction(
                    new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_MODE, 4));
        }
    }

    public boolean getMotorPowerFloat(int motor) {
        return getMotorInfo(motor).powerFloat;
    }

    public void setMotorPower(Set<DcMotor> motors, double power) {
        Range.throwIfRangeIsInvalid(power, POWER_MIN, POWER_MAX);
        for (DcMotor dcMotor : motors) {
            byte speed = (byte) (POWER_RATIO * power);

            if(dcMotor.getDirection() == Direction.REVERSE) {
                speed = (byte) -speed;
            }

            int motor = dcMotor.getPortNumber();
            MotorInfo motorInfo = getMotorInfo(motor);
            byte mode = (byte) (runModeToFlagMatrix(motorInfo.runMode) | UNKNOWN_BIT_MASK);

            this.master.queueTransaction(
                    new MatrixI2cTransaction((byte) motor, speed, motorInfo.targetPosition, mode));
        }
        this.master.queueTransaction(new MatrixI2cTransaction((byte) 0, MatrixI2cProperties.PROPERTY_START, 1));
    }

    public void setMotorPower(int motor, double power) {
        Range.throwIfRangeIsInvalid(power, POWER_MIN, POWER_MAX);
        MotorInfo motorInfo = getMotorInfo(motor);
        motorInfo.power = power;

        byte speed = (byte) ((int) (POWER_RATIO * power));
        byte mode = runModeToFlagMatrix(motorInfo.runMode);

        this.master.queueTransaction(
                new MatrixI2cTransaction((byte) motor, speed, motorInfo.targetPosition, mode));
    }

    public double getMotorPower(int motor) {
        return getMotorInfo(motor).power;
    }

    public void setMotorTargetPosition(int motor, int position) {
        this.getMotorInfo(motor).targetPosition = position;
        this.master.queueTransaction(
                new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_TARGET, position));
    }

    public int getMotorTargetPosition(int motor) {
        MotorInfo motorInfo = getMotorInfo(motor);
        if (this.master.queueTransaction(
                new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_TARGET))) {
            this.master.waitOnRead();
        }
        return motorInfo.targetPosition;
    }

    public int getMotorCurrentPosition(int motor) {
        MotorInfo motorInfo = getMotorInfo(motor);
        if (this.master.queueTransaction(
                new MatrixI2cTransaction((byte) motor, MatrixI2cProperties.PROPERTY_POSITION))) {
            this.master.waitOnRead();
        }
        return motorInfo.position;
    }

    public int getBattery() {
        if (this.master.queueTransaction(
                new MatrixI2cTransaction((byte) 0, MatrixI2cProperties.PROPERTY_BATTERY))) {
            this.master.waitOnRead();
        }
        return this.battery;
    }

    public String getDeviceName() {
        return "Matrix Motor Controller";
    }

    public String getConnectionInfo() {
        return this.master.getConnectionInfo();
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        for(int i = 1; i <= NUMBER_OF_MOTORS; i++) {
            setMotorPowerFloat(i);
        }
    }

    public void handleReadBattery(byte[] buffer) {
        this.battery = BATTERY_RATIO * TypeConversion.unsignedByteToInt(buffer[START_ADDRESS]);
        RobotLog.v("Battery voltage: " + this.battery + "mV");
    }

    public void handleReadPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        MotorInfo motorInfo = getMotorInfo(transaction.motor);
        motorInfo.position = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, START_ADDRESS, START_ADDRESS + BYTES_IN_INT));
        RobotLog.v("Position motor: " + transaction.motor + " " + motorInfo.position);
    }

    public void handleReadTargetPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        MotorInfo motorInfo = getMotorInfo(transaction.motor);
        motorInfo.targetPosition = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, START_ADDRESS, START_ADDRESS + BYTES_IN_INT));
        RobotLog.v("Target motor: " + transaction.motor + " " + motorInfo.targetPosition);
    }

    public void handleReadMode(MatrixI2cTransaction transaction, byte[] buffer) {
        MotorInfo motorInfo = getMotorInfo(transaction.motor);
        motorInfo.deviceModeInfo = buffer[START_ADDRESS];
        RobotLog.v("Mode: " + motorInfo.deviceModeInfo);
    }

    private void validateMotor(int motor) {
        if (motor < 1 || motor > NUMBER_OF_MOTORS) {
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", motor, NUMBER_OF_MOTORS));
        }
    }
}
