package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtDcMotorController implements DcMotorController, I2cPortReadyCallback {
    public static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 3;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED_NXT = (byte) 1;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY_NXT = (byte) 0;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 2;
    public static final int CHANNEL_MODE_MASK_BUSY = 128;
    public static final int CHANNEL_MODE_MASK_EMPTY_D5 = 32;
    public static final int CHANNEL_MODE_MASK_ERROR = 64;
    public static final int CHANNEL_MODE_MASK_LOCK = 4;
    public static final int CHANNEL_MODE_MASK_NO_TIMEOUT = 16;
    public static final int CHANNEL_MODE_MASK_REVERSE = 8;
    public static final int CHANNEL_MODE_MASK_SELECTION = 3;
    public static final int I2C_ADDRESS = 2;
    public static final int MAX_MOTOR = 2;
    public static final int MEM_READ_LENGTH = 20;
    public static final int MEM_START_ADDRESS = 64;
    public static final int MIN_MOTOR = 1;
    public static final int NUM_BYTES = 20;
    public static final byte[] OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE;
    public static final byte[] OFFSET_MAP_MOTOR_MODE;
    public static final byte[] OFFSET_MAP_MOTOR_POWER;
    public static final byte[] OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE;
    public static final int OFFSET_MOTOR1_CURRENT_ENCODER_VALUE = 16;
    public static final int OFFSET_MOTOR1_MODE = 8;
    public static final int OFFSET_MOTOR1_POWER = 9;
    public static final int OFFSET_MOTOR1_TARGET_ENCODER_VALUE = 4;
    public static final int OFFSET_MOTOR2_CURRENT_ENCODER_VALUE = 20;
    public static final int OFFSET_MOTOR2_MODE = 11;
    public static final int OFFSET_MOTOR2_POWER = 10;
    public static final int OFFSET_MOTOR2_TARGET_ENCODER_VALUE = 12;
    public static final int OFFSET_UNUSED = -1;
    public static final byte POWER_BREAK = (byte) 0;
    public static final byte POWER_FLOAT = Byte.MIN_VALUE;
    public static final byte POWER_MAX = (byte) 100;
    public static final byte POWER_MIN = (byte) -100;
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;
    private final int controllerPort;
    private final ElapsedTime elapsedTime;
    private volatile DeviceMode controllerMode;
    private volatile boolean unknownResetCheck;

    static {
        OFFSET_MAP_MOTOR_POWER = new byte[]{(byte) -1, (byte) 9, (byte) 10};
        OFFSET_MAP_MOTOR_MODE = new byte[]{(byte) -1, (byte) 8, (byte) 11};
        OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE = new byte[]{(byte) -1, (byte) 4, (byte) 12};
        OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE = new byte[]{(byte) -1, 16, (byte) 20};
    }

    public HiTechnicNxtDcMotorController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.elapsedTime = new ElapsedTime(0);
        this.unknownResetCheck = true;
        this.legacyModule = legacyModule;
        this.controllerPort = physicalPort;
        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.controllerMode = DeviceMode.WRITE_ONLY;
        legacyModule.enableI2cWriteMode(physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
        try {
            this.writeCacheLock.lock();
            this.writeCache[OFFSET_MOTOR1_POWER] = POWER_FLOAT;
            this.writeCache[OFFSET_MOTOR2_POWER] = POWER_FLOAT;
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
        return this.legacyModule.getConnectionInfo() + "; port " + this.controllerPort;
    }

    public int getVersion() {
        return MIN_MOTOR;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        if (this.controllerMode != mode) {
            switch (mode) {
                case READ_ONLY:
                    this.controllerMode = DeviceMode.SWITCHING_TO_READ_MODE;
                    this.legacyModule.enableI2cReadMode(this.controllerPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                    break;
                case WRITE_ONLY :
                    this.controllerMode = DeviceMode.SWITCHING_TO_WRITE_MODE;
                    this.legacyModule.enableI2cWriteMode(this.controllerPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
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
            if (this.writeCache[OFFSET_MAP_MOTOR_MODE[motor]] != runModeToFlagNXT) {
                this.writeCache[OFFSET_MAP_MOTOR_MODE[motor]] = runModeToFlagNXT;
                this.unknownResetCheck = true;
            }
            this.writeCacheLock.unlock();
        } catch (Throwable th) {
            this.writeCacheLock.unlock();
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        validateMotor(motor);
        validateReadMode();
        try {
            this.readCacheLock.lock();
            byte b = this.readCache[OFFSET_MAP_MOTOR_MODE[motor]];
            return flagToRunModeNXT(b);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public void setMotorPower(int motor, double power) {
        validateMotor(motor);
        validateWriteMode();
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        byte b = (byte) ((int) (100.0d * power));
        try {
            this.writeCacheLock.lock();
            if (b != this.writeCache[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.writeCache[OFFSET_MAP_MOTOR_POWER[motor]] = b;
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
            int i = this.readCache[OFFSET_MAP_MOTOR_POWER[motor]];
            if (i == -128) {
                return 0.0d;
            }
            return ((double) i) / 100.0d;
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public boolean isBusy(int motor) {
        validateMotor(motor);
        validateReadMode();
        try {
            this.readCacheLock.lock();
            boolean z = (this.readCache[OFFSET_MAP_MOTOR_MODE[motor]] & CHANNEL_MODE_MASK_BUSY) == CHANNEL_MODE_MASK_BUSY;
            this.readCacheLock.unlock();
            return z;
        } catch (Throwable th) {
            this.readCacheLock.unlock();
        }
        return false; //TOOD originally had no return statement. why?
    }

    public void setMotorPowerFloat(int motor) {
        validateMotor(motor);
        validateWriteMode();
        try {
            this.writeCacheLock.lock();
            if (-128 != this.writeCache[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.writeCache[OFFSET_MAP_MOTOR_POWER[motor]] = POWER_FLOAT;
                this.unknownResetCheck = true;
            }
            this.writeCacheLock.unlock();
        } catch (Throwable th) {
            this.writeCacheLock.unlock();
        }
    }

    public boolean getMotorPowerFloat(int motor) {
        validateMotor(motor);
        validateReadMode();
        try {
            this.readCacheLock.lock();
            boolean z = this.readCache[OFFSET_MAP_MOTOR_POWER[motor]] == -128;
            this.readCacheLock.unlock();
            return z;
        } catch (Throwable th) {
            this.readCacheLock.unlock();
        }
        return false; //TODO originally had no return statement. why?
    }

    public void setMotorTargetPosition(int motor, int position) {
        validateMotor(motor);
        validateWriteMode();
        byte[] intToByteArray = TypeConversion.intToByteArray(position);
        try {
            this.writeCacheLock.lock();
            System.arraycopy(intToByteArray, 0, this.writeCache, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], intToByteArray.length);
            this.unknownResetCheck = true;
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public int getMotorTargetPosition(int motor) {
        validateMotor(motor);
        validateReadMode();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.readCacheLock.lock();
            System.arraycopy(this.readCache, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public int getMotorCurrentPosition(int motor) {
        validateMotor(motor);
        validateReadMode();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.readCacheLock.lock();
            System.arraycopy(this.readCache, OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public void close() {
        if (this.controllerMode == DeviceMode.WRITE_ONLY) {
            setMotorPowerFloat(MIN_MOTOR);
            setMotorPowerFloat(MAX_MOTOR);
        }
    }

    private void validateWriteMode() {
        if (this.controllerMode != DeviceMode.SWITCHING_TO_WRITE_MODE) {
            if (this.controllerMode == DeviceMode.READ_ONLY || this.controllerMode == DeviceMode.SWITCHING_TO_READ_MODE) {
                String str = "Cannot write while in this mode: " + this.controllerMode;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void validateReadMode() {
        if (this.controllerMode != DeviceMode.SWITCHING_TO_READ_MODE) {
            if (this.controllerMode == DeviceMode.WRITE_ONLY || this.controllerMode == DeviceMode.SWITCHING_TO_WRITE_MODE) {
                String str = "Cannot read while in this mode: " + this.controllerMode;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void validateMotor(int motor) {
        if (motor < MIN_MOTOR || motor > MAX_MOTOR) {
            Object[] objArr = new Object[MAX_MOTOR];
            objArr[0] = motor;
            objArr[MIN_MOTOR] = MAX_MOTOR;
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", objArr));
        }
    }

    public static RunMode flagToRunModeNXT(byte flag) {
        switch (flag & CHANNEL_MODE_MASK_SELECTION) {
            case 0 :
                return RunMode.RUN_WITHOUT_ENCODERS;
            case 1 :
                return RunMode.RUN_USING_ENCODERS;
            case 2 :
                return RunMode.RUN_TO_POSITION;
            case 3 :
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
                return POWER_BREAK;
            case RUN_TO_POSITION :
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case  RESET_ENCODERS :
            default:
                return CHANNEL_MODE_FLAG_SELECT_RESET;
        }
    }

    public void portIsReady(int port) {
        switch (this.controllerMode) {
            case SWITCHING_TO_READ_MODE :
                if (this.legacyModule.isI2cPortInReadMode(port)) {
                    this.controllerMode = DeviceMode.READ_ONLY;
                    break;
                }
                break;
            case SWITCHING_TO_WRITE_MODE :
                if (this.legacyModule.isI2cPortInWriteMode(port)) {
                    this.controllerMode = DeviceMode.WRITE_ONLY;
                    break;
                }
                break;
        }
        if (this.controllerMode == DeviceMode.READ_ONLY) {
            this.legacyModule.setI2cPortActionFlag(this.controllerPort);
            this.legacyModule.writeI2cPortFlagOnlyToController(this.controllerPort);
        } else {
            if (this.unknownResetCheck || this.elapsedTime.time() > 2.0d) {
                this.legacyModule.setI2cPortActionFlag(this.controllerPort);
                this.legacyModule.writeI2cCacheToController(this.controllerPort);
                this.elapsedTime.reset();
            }
            this.unknownResetCheck = false;
        }
        this.legacyModule.readI2cCacheFromController(this.controllerPort);
    }
}
