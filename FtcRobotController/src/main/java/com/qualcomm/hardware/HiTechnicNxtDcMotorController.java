package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtDcMotorController implements DcMotorController, I2cPortReadyCallback {
    public static final int VERSION = 1;

    public static final int I2C_ADDRESS = 2;
    public static final int START_ADDRESS = 64;
    public static final int BUFFER_SIZE = 20;

    public static final int MAX_MOTOR = 2;
    public static final int MIN_MOTOR = 1;
    public static final int POSITION_BUFFER_SIZE = 4;

    public static final int POWER_FLOAT_VALUE = -128;

    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY_NXT = (byte) 0;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED_NXT = (byte) 1;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 2;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 3;

    public static final int CHANNEL_MODE_MASK_BUSY = 128;
    public static final int CHANNEL_MODE_MASK_SELECTION = 3;

    public static final int OFFSET_MOTOR1_CURRENT_ENCODER_VALUE = 16;
    public static final int OFFSET_MOTOR1_MODE = 8;
    public static final int OFFSET_MOTOR1_POWER = 9;
    public static final int OFFSET_MOTOR1_TARGET_ENCODER_VALUE = 4;
    public static final int OFFSET_MOTOR2_CURRENT_ENCODER_VALUE = 20;
    public static final int OFFSET_MOTOR2_MODE = 11;
    public static final int OFFSET_MOTOR2_POWER = 10;
    public static final int OFFSET_MOTOR2_TARGET_ENCODER_VALUE = 12;

    public static final byte POWER_BREAK = (byte) 0;
    public static final byte POWER_FLOAT = Byte.MIN_VALUE;
    public static final byte POWER_MAX = (byte) 100;

    public static final double ELAPSED_TIME_MAX = 2.0;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;
    private final int controllerPort;
    private final ElapsedTime elapsedTime = new ElapsedTime(0);
    private volatile DeviceMode controllerMode = DeviceMode.WRITE_ONLY;
    private volatile boolean unknownResetCheck = true;


    public HiTechnicNxtDcMotorController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.legacyModule = legacyModule;
        this.controllerPort = physicalPort;

        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);

        legacyModule.enableI2cWriteMode(physicalPort, I2C_ADDRESS, START_ADDRESS, BUFFER_SIZE);
        try {
            this.writeCacheLock.lock();
            this.writeCache[getOffsetMotorPower(MIN_MOTOR)] = POWER_FLOAT;
            this.writeCache[getOffsetMotorPower(MAX_MOTOR)] = POWER_FLOAT;
            legacyModule.writeI2cCacheToController(physicalPort);
            legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public String getDeviceName() {
        return "NXT DC Motor Controller";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.controllerPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        if (this.controllerMode != mode) {
            switch (mode) {
                case READ_ONLY:
                    this.controllerMode = DeviceMode.SWITCHING_TO_READ_MODE;
                    this.legacyModule.enableI2cReadMode(this.controllerPort, I2C_ADDRESS, START_ADDRESS, BUFFER_SIZE);
                    break;
                case WRITE_ONLY :
                    this.controllerMode = DeviceMode.SWITCHING_TO_WRITE_MODE;
                    this.legacyModule.enableI2cWriteMode(this.controllerPort, I2C_ADDRESS, START_ADDRESS, BUFFER_SIZE);
                    break;
            }
            this.unknownResetCheck = true;
        }
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.controllerMode;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        validateMotor(motor);
        validateWriteMode();
        byte runModeToFlagNXT = runModeToFlagNXT(mode);
        try {
            this.writeCacheLock.lock();
            int offsetMotorMode = getOffsetMotorMode(motor);
            if (this.writeCache[offsetMotorMode] != runModeToFlagNXT) {
                this.writeCache[offsetMotorMode] = runModeToFlagNXT;
                this.unknownResetCheck = true;
            }
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        validateMotor(motor);
        validateReadMode();
        try {
            this.readCacheLock.lock();
            byte b = this.readCache[getOffsetMotorMode(motor)];
            return flagToRunModeNXT(b);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public void setMotorPower(int motor, double power) {
        validateMotor(motor);
        validateWriteMode();
        Range.throwIfRangeIsInvalid(power, -1.0, 1.0d);
        byte b = (byte) ((int) (POWER_MAX * power));
        try {
            this.writeCacheLock.lock();
            int offsetMotorPower = getOffsetMotorPower(motor);
            if (b != this.writeCache[offsetMotorPower]) {
                this.writeCache[offsetMotorPower] = b;
                this.unknownResetCheck = true;
            }
            this.writeCacheLock.unlock();
        } catch (Throwable th) {
            this.writeCacheLock.unlock();
        }
    }

    public double getMotorPower(int motor) {
        validateMotor(motor);
        validateReadMode();
        try {
            this.readCacheLock.lock();
            int i = this.readCache[getOffsetMotorPower(motor)];
            if (i == POWER_FLOAT_VALUE) {
                return POWER_BREAK;
            }
            return ((double) i) / POWER_MAX;
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public boolean isBusy(int motor) {
        validateMotor(motor);
        validateReadMode();
        boolean isBusy;
        try {
            this.readCacheLock.lock();
            isBusy = (this.readCache[getOffsetMotorMode(motor)] & CHANNEL_MODE_MASK_BUSY) == CHANNEL_MODE_MASK_BUSY;
        } finally {
            this.readCacheLock.unlock();
        }
        return isBusy;
    }

    public void setMotorPowerFloat(int motor) {
        validateMotor(motor);
        validateWriteMode();
        try {
            this.writeCacheLock.lock();
            int offsetMotorPower = getOffsetMotorPower(motor);
            if (POWER_FLOAT_VALUE != this.writeCache[offsetMotorPower]) {
                this.writeCache[offsetMotorPower] = POWER_FLOAT;
                this.unknownResetCheck = true;
            }
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public boolean getMotorPowerFloat(int motor) {
        validateMotor(motor);
        validateReadMode();
        boolean isFloat;
        try {
            this.readCacheLock.lock();
            isFloat = this.readCache[getOffsetMotorPower(motor)] == POWER_FLOAT_VALUE;
        } finally {
            this.readCacheLock.unlock();
        }
        return isFloat;
    }

    public void setMotorTargetPosition(int motor, int position) {
        validateMotor(motor);
        validateWriteMode();
        byte[] targetPosBuffer = TypeConversion.intToByteArray(position);
        try {
            this.writeCacheLock.lock();
            System.arraycopy(targetPosBuffer, 0, this.writeCache, getOffsetMotorTargetEncoderValue(motor), POSITION_BUFFER_SIZE);
            this.unknownResetCheck = true;
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public int getMotorTargetPosition(int motor) {
        validateMotor(motor);
        validateReadMode();
        byte[] targetPosBuffer = new byte[POSITION_BUFFER_SIZE];
        try {
            this.readCacheLock.lock();
            System.arraycopy(this.readCache, getOffsetMotorTargetEncoderValue(motor), targetPosBuffer, 0, POSITION_BUFFER_SIZE);
        } finally {
            this.readCacheLock.unlock();
        }
        return TypeConversion.byteArrayToInt(targetPosBuffer);
    }

    public int getMotorCurrentPosition(int motor) {
        validateMotor(motor);
        validateReadMode();
        byte[] currentPosBuffer = new byte[POSITION_BUFFER_SIZE];
        try {
            this.readCacheLock.lock();
            System.arraycopy(this.readCache, getOffsetMotorCurrentEncoderValue(motor), currentPosBuffer, 0, POSITION_BUFFER_SIZE);
        } finally {
            this.readCacheLock.unlock();
        }
        return TypeConversion.byteArrayToInt(currentPosBuffer);
    }

    public void close() {
        if (this.controllerMode == DeviceMode.WRITE_ONLY) {
            setMotorPowerFloat(MIN_MOTOR);
            setMotorPowerFloat(MAX_MOTOR);
        }
    }

    private void validateWriteMode() {
            if (this.controllerMode == DeviceMode.READ_ONLY || this.controllerMode == DeviceMode.SWITCHING_TO_READ_MODE) {
                String stackTraceMessage = "";
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > 3) {
                    stackTraceMessage = "\n from method: " + stackTrace[3].getMethodName();
                }
                throw new IllegalArgumentException("Cannot write while in this mode: " + this.controllerMode + stackTraceMessage);
            }
    }

    private void validateReadMode() {
            if (this.controllerMode == DeviceMode.WRITE_ONLY || this.controllerMode == DeviceMode.SWITCHING_TO_WRITE_MODE) {
                String stackTraceMessage = "";
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > 3) {
                    stackTraceMessage = "\n from method: " + stackTrace[3].getMethodName();
                }
                throw new IllegalArgumentException("Cannot read while in this mode: " + this.controllerMode + stackTraceMessage);
            }
    }

    private void validateMotor(int motor) {
        if (motor < MIN_MOTOR || motor > MAX_MOTOR) {
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", motor, MAX_MOTOR));
        }
    }

    public static RunMode flagToRunModeNXT(byte flag) {
        switch (flag & CHANNEL_MODE_MASK_SELECTION) {
            case CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY_NXT :
                return RunMode.RUN_WITHOUT_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED_NXT :
                return RunMode.RUN_USING_ENCODERS;
            case CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION :
                return RunMode.RUN_TO_POSITION;
            case CHANNEL_MODE_FLAG_SELECT_RESET :
                return RunMode.RESET_ENCODERS;
            default:
                return RunMode.RUN_WITHOUT_ENCODERS;
        }
    }

    public static byte runModeToFlagNXT(RunMode mode) {
        switch (mode) {
            case RUN_USING_ENCODERS :
                return CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED_NXT;
            case RUN_WITHOUT_ENCODERS :
                return CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY_NXT;
            case RUN_TO_POSITION :
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case  RESET_ENCODERS :
            default:
                return CHANNEL_MODE_FLAG_SELECT_RESET;
        }
    }

    public void portIsReady(int port) {
        if(this.controllerMode ==DeviceMode.SWITCHING_TO_READ_MODE &&
                this.legacyModule.isI2cPortInReadMode(port)) {
                this.controllerMode = DeviceMode.READ_ONLY;
        } else if(this.controllerMode ==DeviceMode.SWITCHING_TO_WRITE_MODE &&
                this.legacyModule.isI2cPortInWriteMode(port)) {
                    this.controllerMode = DeviceMode.WRITE_ONLY;
        }

        if (this.controllerMode == DeviceMode.READ_ONLY) {
            this.legacyModule.setI2cPortActionFlag(this.controllerPort);
            this.legacyModule.writeI2cPortFlagOnlyToController(this.controllerPort);
        } else {
            if (this.unknownResetCheck || this.elapsedTime.time() > ELAPSED_TIME_MAX) {
                this.legacyModule.setI2cPortActionFlag(this.controllerPort);
                this.legacyModule.writeI2cCacheToController(this.controllerPort);
                this.elapsedTime.reset();
            }
            this.unknownResetCheck = false;
        }
        this.legacyModule.readI2cCacheFromController(this.controllerPort);
    }

    private int getOffsetMotorMode(int motor) {
        if(motor == MIN_MOTOR) {
            return OFFSET_MOTOR1_MODE;
        } else {
            return OFFSET_MOTOR2_MODE;
        }
    }
    private int getOffsetMotorTargetEncoderValue(int motor) {
        if(motor == MIN_MOTOR) {
            return OFFSET_MOTOR1_TARGET_ENCODER_VALUE;
        } else {
            return OFFSET_MOTOR2_TARGET_ENCODER_VALUE;
        }
    }

    private int getOffsetMotorCurrentEncoderValue(int motor) {
        if(motor == MIN_MOTOR) {
            return OFFSET_MOTOR1_CURRENT_ENCODER_VALUE;
        } else {
            return OFFSET_MOTOR2_CURRENT_ENCODER_VALUE;
        }
    }

    private int getOffsetMotorPower(int motor) {
        if(motor == MIN_MOTOR) {
            return OFFSET_MOTOR1_POWER;
        } else {
            return OFFSET_MOTOR2_POWER;
        }
    }
}
