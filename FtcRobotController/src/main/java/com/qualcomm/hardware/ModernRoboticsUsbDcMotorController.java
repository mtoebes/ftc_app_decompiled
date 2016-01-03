package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.DifferentialControlLoopCoefficients;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;

import java.util.Arrays;
import java.util.List;

public class ModernRoboticsUsbDcMotorController extends ModernRoboticsUsbDevice implements DcMotorController, VoltageSensor {
    public static final int ADDRESS_BATTERY_VOLTAGE = 84;
    public static final int ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE = 76;
    public static final int ADDRESS_MOTOR1_D_COEFFICIENT = 89;
    public static final int ADDRESS_MOTOR1_GEAR_RATIO = 86;
    public static final int ADDRESS_MOTOR1_I_COEFFICIENT = 88;
    public static final int ADDRESS_MOTOR1_MODE = 68;
    public static final int ADDRESS_MOTOR1_POWER = 69;
    public static final int ADDRESS_MOTOR1_P_COEFFICIENT = 87;
    public static final int ADDRESS_MOTOR1_TARGET_ENCODER_VALUE = 64;
    public static final int ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE = 80;
    public static final int ADDRESS_MOTOR2_D_COEFFICIENT = 93;
    public static final int ADDRESS_MOTOR2_GEAR_RATIO = 90;
    public static final int ADDRESS_MOTOR2_I_COEFFICIENT = 92;
    public static final int ADDRESS_MOTOR2_MODE = 71;
    public static final int ADDRESS_MOTOR2_POWER = 70;
    public static final int ADDRESS_MOTOR2_P_COEFFICIENT = 91;
    public static final int ADDRESS_MOTOR2_TARGET_ENCODER_VALUE = 72;
    public static final int[] ADDRESS_MOTOR_CURRENT_ENCODER_VALUE_MAP = new int[]{ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE, ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE};
    public static final int[] ADDRESS_MOTOR_GEAR_RATIO_MAP = new int[]{ADDRESS_MOTOR1_GEAR_RATIO, ADDRESS_MOTOR2_GEAR_RATIO};
    public static final int[] ADDRESS_MOTOR_MODE_MAP = new int[]{ADDRESS_MOTOR1_MODE, ADDRESS_MOTOR2_MODE};
    public static final int[] ADDRESS_MOTOR_POWER_MAP = new int[]{ADDRESS_MOTOR1_POWER, ADDRESS_MOTOR2_POWER};
    public static final int[] ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP = new int[]{ADDRESS_MOTOR1_TARGET_ENCODER_VALUE, ADDRESS_MOTOR2_TARGET_ENCODER_VALUE};
    public static final int[] ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP = new int[]{ADDRESS_MOTOR1_P_COEFFICIENT, ADDRESS_MOTOR2_P_COEFFICIENT};

    public static final int ADDRESS_UNUSED = 255;
    public static final double BATTERY_MAX_MEASURABLE_VOLTAGE = 20.4d;
    public static final int BATTERY_MAX_MEASURABLE_VOLTAGE_INT = 1023;
    public static final byte CHANNEL_MODE_FLAG_BUSY = Byte.MIN_VALUE;
    public static final byte CHANNEL_MODE_FLAG_ERROR = (byte) 64;
    public static final byte CHANNEL_MODE_FLAG_LOCK = (byte) 4;
    public static final byte CHANNEL_MODE_FLAG_NO_TIMEOUT = (byte) 16;
    public static final byte CHANNEL_MODE_FLAG_REVERSE = (byte) 8;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 3;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED = (byte) 1;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY = (byte) 0;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 2;
    public static final byte CHANNEL_MODE_FLAG_UNUSED = (byte) 32;
    public static final int CHANNEL_MODE_MASK_BUSY = 128;
    public static final int CHANNEL_MODE_MASK_EMPTY_D5 = 32;
    public static final int CHANNEL_MODE_MASK_ERROR = 64;
    public static final int CHANNEL_MODE_MASK_LOCK = 4;
    public static final int CHANNEL_MODE_MASK_NO_TIMEOUT = 16;
    public static final int CHANNEL_MODE_MASK_REVERSE = 8;
    public static final int CHANNEL_MODE_MASK_SELECTION = 3;
    public static final boolean DEBUG_LOGGING = false;
    public static final byte DEFAULT_D_COEFFICIENT = (byte) -72;
    public static final byte DEFAULT_I_COEFFICIENT = (byte) 64;
    public static final byte DEFAULT_P_COEFFICIENT = Byte.MIN_VALUE;
    public static final int DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX = 255;
    public static final int MAX_MOTOR = 2;
    public static final int MIN_MOTOR = 1;
    public static final int MONITOR_LENGTH = 30;
    public static final byte POWER_BREAK = (byte) 0;
    public static final byte POWER_FLOAT = Byte.MIN_VALUE;
    public static final byte POWER_MAX = (byte) 100;
    public static final byte POWER_MIN = (byte) -100;
    public static final byte RATIO_MAX = Byte.MAX_VALUE;
    public static final byte RATIO_MIN = Byte.MIN_VALUE;
    public static final byte START_ADDRESS = (byte) 64;
    private HelperClass[] helperClasses;

    private static class HelperClass { // TODO figure this out
        private int[] controller1;
        private int[] controller2;
        private int counter;

        private HelperClass() {
            this.controller1 = new int[ModernRoboticsUsbDcMotorController.CHANNEL_MODE_MASK_SELECTION];
            this.controller2 = new int[ModernRoboticsUsbDcMotorController.CHANNEL_MODE_MASK_SELECTION];
            this.counter = 0;
        }

        public void unknownHelper1(int i) {
            int i2 = this.controller1[this.counter];
            this.counter = (this.counter + 1) % 3;
            this.controller2[this.counter] = Math.abs(i2 - i);
            this.controller1[this.counter] = i;
        }

        public boolean unknownHelper2() {
            int i = 0;
            for (int i2 : controller2) {
                i += i2;
            }
            return i > 6;
        }
    }

    protected ModernRoboticsUsbDcMotorController(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableBlocking(serialNumber, device, MONITOR_LENGTH, CHANNEL_MODE_MASK_ERROR, DEBUG_LOGGING));
        this.helperClasses = new HelperClass[CHANNEL_MODE_MASK_SELECTION];
        this.readWriteRunnable.setCallback(this);
        for (int i = 0; i < this.helperClasses.length; i++) {
            this.helperClasses[i] = new HelperClass();
        }
        setMotorsPowerFloat();
        writeMotors();
    }

    public String getDeviceName() {
        return "Modern Robotics USB DC Motor Controller";
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public void close() {
        setMotorsPowerFloat();
        super.close();
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return DeviceMode.READ_WRITE;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        validateMotor(motor);
        write(getModeAddress(motor), runModeToFlag(mode));
    }

    public RunMode getMotorChannelMode(int motor) {
        validateMotor(motor);
        return flagToRunMode(read(getModeAddress(motor)));
    }

    public void setMotorPower(int motor, double power) {
        validateMotor(motor);
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        int motorPowerAddress = getPowerAddress(motor);
        byte[] motorPowerBuffer = new byte[1];
        motorPowerBuffer[0] = (byte) ((int) (100.0d * power));
        write(motorPowerAddress, motorPowerBuffer);
    }

    public double getMotorPower(int motor) {
        validateMotor(motor);
        byte read = read(getPowerAddress(motor));
        if (read == -128) {
            return 0.0d;
        }
        return ((double) read) / 100.0d;
    }

    public boolean isBusy(int motor) {
        validateMotor(motor);
        return this.helperClasses[motor].unknownHelper2();
    }

    public void setMotorPowerFloat(int motor) {
        validateMotor(motor);
        int motorPowerAddress = getPowerAddress(motor);
        byte[] motorPowerBuffer = new byte[1];
        motorPowerBuffer[0] = RATIO_MIN;
        write(motorPowerAddress, motorPowerBuffer);
    }

    public boolean getMotorPowerFloat(int motor) {
        validateMotor(motor);
        return read(getPowerAddress(motor)) == -128;
    }

    public void setMotorTargetPosition(int motor, int position) {
        validateMotor(motor);
        Range.throwIfRangeIsInvalid((double) position, Integer.MIN_VALUE, Integer.MAX_VALUE);
        write(getTargetEncoderAddress(motor), TypeConversion.intToByteArray(position));
    }

    public int getMotorTargetPosition(int motor) {
        validateMotor(motor);
        return TypeConversion.byteArrayToInt(read(getTargetEncoderAddress(motor), CHANNEL_MODE_MASK_LOCK));
    }

    public int getMotorCurrentPosition(int motor) {
        validateMotor(motor);
        return TypeConversion.byteArrayToInt(read(getCurrentEncoderAddress(motor), CHANNEL_MODE_MASK_LOCK));
    }

    public double getVoltage() {
        return (((double) ((TypeConversion.byteArrayToShort(read(ADDRESS_BATTERY_VOLTAGE, MAX_MOTOR)) >> 6) & BATTERY_MAX_MEASURABLE_VOLTAGE_INT)) / 1023.0d) * BATTERY_MAX_MEASURABLE_VOLTAGE;
    }

    public void setGearRatio(int motor, double ratio) {
        validateMotor(motor);
        Range.throwIfRangeIsInvalid(ratio, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        int gearRatioAddress = getGearRatioAddress(motor);
        byte[] gearRatioBuffer = new byte[1];
        gearRatioBuffer[0] = (byte) ((int) (127.0d * ratio));
        write(gearRatioAddress, gearRatioBuffer);
    }

    public double getGearRatio(int motor) {
        validateMotor(motor);
        return ((double) read(getGearRatioAddress(motor), 1)[0]) / 127.0d;
    }

    public void setDifferentialControlLoopCoefficients(int motor, DifferentialControlLoopCoefficients pid) {
        validateMotor(motor);
        double maxValue = 255.0d;
        if (pid.p > maxValue) {
            pid.p = maxValue;
        }
        if (pid.i > maxValue) {
            pid.i = maxValue;
        }
        if (pid.d > maxValue) {
            pid.d = maxValue;
        }
        int address = getDifferentialControlLoopAddress(motor);
        byte[] pidBuffer = new byte[CHANNEL_MODE_MASK_SELECTION];
        pidBuffer[0] = (byte) ((int) pid.p);
        pidBuffer[1] = (byte) ((int) pid.i);
        pidBuffer[2] = (byte) ((int) pid.d);
        write(address, pidBuffer);
    }

    public DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor) {
        validateMotor(motor);
        DifferentialControlLoopCoefficients differentialControlLoopCoefficients = new DifferentialControlLoopCoefficients();
        byte[] read = read(getDifferentialControlLoopAddress(motor), CHANNEL_MODE_MASK_SELECTION);
        differentialControlLoopCoefficients.p = (double) read[0];
        differentialControlLoopCoefficients.i = (double) read[1];
        differentialControlLoopCoefficients.d = (double) read[2];
        return differentialControlLoopCoefficients;
    }

    public static byte runModeToFlag(RunMode mode) {
        switch (mode) {
            case RUN_USING_ENCODERS:
                return CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED;
            case RUN_WITHOUT_ENCODERS:
                return CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY;
            case RUN_TO_POSITION:
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case RESET_ENCODERS:
            default :
                return CHANNEL_MODE_FLAG_SELECT_RESET;
        }
    }

    public static RunMode flagToRunMode(byte flag) {
        switch (flag & CHANNEL_MODE_MASK_SELECTION) {
            case CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY:
                return RunMode.RUN_WITHOUT_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED:
                return RunMode.RUN_USING_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION:
                return RunMode.RUN_TO_POSITION;
            case CHANNEL_MODE_FLAG_SELECT_RESET:
            default:
                return RunMode.RESET_ENCODERS;
        }
    }

    private void setMotorsPowerFloat() {
        setMotorPowerFloat(MIN_MOTOR);
        setMotorPowerFloat(MAX_MOTOR);
    }

    private void writeMotors() {
        for (int motor = MIN_MOTOR; motor <= MAX_MOTOR; motor ++) {
            write(getDifferentialControlLoopAddress(motor), new byte[]{RATIO_MIN, START_ADDRESS, DEFAULT_D_COEFFICIENT});
        }
    }

    private void validateMotor(int i) {
        if (i < MIN_MOTOR || i > MAX_MOTOR) {
            Object[] objArr = new Object[MAX_MOTOR];
            objArr[0] = i;
            objArr[MIN_MOTOR] = MAX_MOTOR;
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", objArr));
        }
    }

    public void readComplete() throws InterruptedException {
        for (int i = MIN_MOTOR; i <= MAX_MOTOR; i += MIN_MOTOR) {
            this.helperClasses[i].unknownHelper1(getMotorCurrentPosition(i));
        }
    }

    private static int getPowerAddress(int motor) {
        return ADDRESS_MOTOR_POWER_MAP[motor-1];
    }

    private static int getModeAddress(int motor) {
        return ADDRESS_MOTOR_MODE_MAP[motor-1];
    }

    private static int getTargetEncoderAddress(int motor) {
        return ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP[motor-1];
    }

    private static int getCurrentEncoderAddress(int motor) {
        return ADDRESS_MOTOR_CURRENT_ENCODER_VALUE_MAP[motor-1];
    }

    private static int getGearRatioAddress(int motor) {
        return ADDRESS_MOTOR_GEAR_RATIO_MAP[motor-1];
    }

    private static int getDifferentialControlLoopAddress(int motor) {
        return ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP[motor-1];
    }
}
