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

public class ModernRoboticsUsbDcMotorController extends ModernRoboticsUsbDevice implements DcMotorController, VoltageSensor {
    private static final int ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE = 76;
    private static final int ADDRESS_MOTOR1_GEAR_RATIO = 86;
    private static final int ADDRESS_MOTOR1_MODE = 68;
    private static final int ADDRESS_MOTOR1_POWER = 69;
    private static final int ADDRESS_MOTOR1_P_COEFFICIENT = 87;
    private static final int ADDRESS_MOTOR1_TARGET_ENCODER_VALUE = 64;

    private static final int ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE = 80;
    private static final int ADDRESS_MOTOR2_GEAR_RATIO = 90;
    private static final int ADDRESS_MOTOR2_MODE = 71;
    private static final int ADDRESS_MOTOR2_POWER = 70;
    private static final int ADDRESS_MOTOR2_P_COEFFICIENT = 91;
    private static final int ADDRESS_MOTOR2_TARGET_ENCODER_VALUE = 72;

    private static final int ADDRESS_BATTERY_VOLTAGE = 84;
    private static final double BATTERY_MAX_MEASURABLE_VOLTAGE = 20.4d;
    private static final int BATTERY_MAX_MEASURABLE_VOLTAGE_INT = 1023;

    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY = (byte) 0;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED = (byte) 1;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 2;
    private static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 3;
    private static final int CHANNEL_MODE_MASK_SELECTION = 3;


    private static final byte DEFAULT_D_COEFFICIENT = (byte) -72;
    private static final byte DEFAULT_I_COEFFICIENT = (byte) 64;
    private static final byte DEFAULT_P_COEFFICIENT = Byte.MIN_VALUE;
    private static final int DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX = 255;

    private static final int MIN_MOTOR = 1;
    private static final int MAX_MOTOR = 2;

    private static final int NUM_OF_MOTORS = 2;

    private static final byte POWER_BREAK = (byte) 0;
    private static final byte POWER_FLOAT = Byte.MIN_VALUE;
    private static final byte POWER_MAX = (byte) 100;

    private static final byte START_ADDRESS = (byte) 64;
    private static final int MONITOR_LENGTH = 30;
    private static final boolean DEBUG_LOGGING = false;

    private motorPosition[] motorPositions;

    private static class motorPosition {
        private final int[] positions =  new int[3];
        private final int[] positionsDiff =  new int[3];
        private int index = 0;

        private motorPosition() {
        }

        public void addPosition(int newPosition) {
            int oldPosition = this.positions[this.index];
            int positionDiff = Math.abs(oldPosition - newPosition);

            this.index = (this.index + 1) % 3;
            this.positions[this.index] = newPosition;
            this.positionsDiff[this.index] = positionDiff;
        }

        public boolean hasMoved() {
            int totalDiff = 0;
            for (int diff : positionsDiff) {
                totalDiff += diff;
            }
            return totalDiff > 6;
        }
    }

    protected ModernRoboticsUsbDcMotorController(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableBlocking(serialNumber, device, MONITOR_LENGTH, START_ADDRESS, DEBUG_LOGGING));
        this.motorPositions = new motorPosition[NUM_OF_MOTORS];
        this.readWriteRunnable.setCallback(this);
        for (int index = 0; index < this.motorPositions.length; index++) {
            this.motorPositions[index] = new motorPosition();
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
        Range.throwIfRangeIsInvalid(power, -1.0d, 1.0d);
        byte[] motorPowerBuffer = {(byte) ((int) (POWER_MAX * power))};
        write(getPowerAddress(motor), motorPowerBuffer);
    }

    public double getMotorPower(int motor) {
        validateMotor(motor);
        byte motorPower = read(getPowerAddress(motor));
        if (motorPower == POWER_FLOAT) {
            return POWER_BREAK;
        } else {
            return ((double) motorPower) / POWER_MAX;
        }
    }

    public boolean isBusy(int motor) {
        validateMotor(motor);
        return this.motorPositions[motor-MIN_MOTOR].hasMoved();
    }

    public void setMotorPowerFloat(int motor) {
        validateMotor(motor);
        byte[] motorPowerBuffer = {POWER_FLOAT};
        write(getPowerAddress(motor), motorPowerBuffer);
    }

    public boolean getMotorPowerFloat(int motor) {
        validateMotor(motor);
        return read(getPowerAddress(motor)) == POWER_FLOAT;
    }

    public void setMotorTargetPosition(int motor, int position) {
        validateMotor(motor);
        Range.throwIfRangeIsInvalid((double) position, Integer.MIN_VALUE, Integer.MAX_VALUE);
        write(getTargetEncoderAddress(motor), TypeConversion.intToByteArray(position));
    }

    public int getMotorTargetPosition(int motor) {
        validateMotor(motor);
        return TypeConversion.byteArrayToInt(read(getTargetEncoderAddress(motor), 4));
    }

    public int getMotorCurrentPosition(int motor) {
        validateMotor(motor);
        return TypeConversion.byteArrayToInt(read(getCurrentEncoderAddress(motor), 4));
    }

    public double getVoltage() {
        // battery voltage is store in the first 10 bytes of the byte array
        double readVoltage = (TypeConversion.byteArrayToShort(read(ADDRESS_BATTERY_VOLTAGE, 2)) >> 6) & BATTERY_MAX_MEASURABLE_VOLTAGE_INT;
        return BATTERY_MAX_MEASURABLE_VOLTAGE * readVoltage/BATTERY_MAX_MEASURABLE_VOLTAGE_INT;
    }

    public void setGearRatio(int motor, double ratio) {
        validateMotor(motor);
        Range.throwIfRangeIsInvalid(ratio, -1.0d, 1.0d);
        byte[] gearRatioBuffer = {(byte) ((int) (Byte.MAX_VALUE * ratio))};
        write(getGearRatioAddress(motor), gearRatioBuffer);
    }

    public double getGearRatio(int motor) {
        validateMotor(motor);
        return ((double) read(getGearRatioAddress(motor), 1)[0]) / Byte.MAX_VALUE;
    }

    public void setDifferentialControlLoopCoefficients(int motor, DifferentialControlLoopCoefficients pid) {
        validateMotor(motor);
        byte[] pidBuffer = new byte[3];
        pidBuffer[0] = (byte) ((int) Math.min(pid.p, DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX));
        pidBuffer[1] = (byte) ((int) Math.min(pid.i, DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX));
        pidBuffer[2] = (byte) ((int) Math.min(pid.d, DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX));
        write(getDifferentialControlLoopAddress(motor), pidBuffer);
    }

    public DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor) {
        validateMotor(motor);
        byte[] read = read(getDifferentialControlLoopAddress(motor), 3);
        return new DifferentialControlLoopCoefficients(read[0], read[1], read[2]);
    }

    private static byte runModeToFlag(RunMode mode) {
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

    private static RunMode flagToRunMode(byte flag) {
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
        for(int motor = MAX_MOTOR; motor <= MAX_MOTOR; motor++) {
            setMotorPowerFloat(motor);
        }
    }

    private void writeMotors() {
        for (int motor = MIN_MOTOR; motor <= MAX_MOTOR; motor++) {
            write(getDifferentialControlLoopAddress(motor), new byte[]{DEFAULT_P_COEFFICIENT, DEFAULT_I_COEFFICIENT, DEFAULT_D_COEFFICIENT});
        }
    }

    private void validateMotor(int motor) {
        if (motor < 1 || motor > NUM_OF_MOTORS) {
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are %d..%d", motor, MIN_MOTOR, MAX_MOTOR));
        }
    }

    public void readComplete() throws InterruptedException {
        for (int motor = MIN_MOTOR; motor <= MAX_MOTOR; motor++) {
            this.motorPositions[motor].addPosition(getMotorCurrentPosition(motor));
        }
    }

    private static int getPowerAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_POWER;
        } else {
            return ADDRESS_MOTOR2_POWER;
        }
    }

    private static int getModeAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_MODE;
        } else {
            return ADDRESS_MOTOR2_MODE;
        }
    }

    private static int getTargetEncoderAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_TARGET_ENCODER_VALUE;
        } else {
            return ADDRESS_MOTOR2_TARGET_ENCODER_VALUE;
        }
    }

    private static int getCurrentEncoderAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE;
        } else {
            return ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE;
        }
    }

    private static int getGearRatioAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_GEAR_RATIO;
        } else {
            return ADDRESS_MOTOR2_GEAR_RATIO;
        }
    }

    private static int getDifferentialControlLoopAddress(int motor) {
        if(motor == 1) {
            return ADDRESS_MOTOR1_P_COEFFICIENT;
        } else {
            return ADDRESS_MOTOR2_P_COEFFICIENT;
        }
    }
}
