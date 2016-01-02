package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
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
    private final ModernRoboticsUsbLegacyModule f45a;
    private final byte[] f46b;
    private final Lock f47c;
    private final byte[] f48d;
    private final Lock f49e;
    private final int f50f;
    private final ElapsedTime f51g;
    private volatile DeviceMode f52h;
    private volatile boolean f53i;

    /* renamed from: com.qualcomm.hardware.HiTechnicNxtDcMotorController.1 */
    static /* synthetic */ class C00041 {
        static final /* synthetic */ int[] f43a;
        static final /* synthetic */ int[] f44b;

        static {
            f44b = new int[RunMode.values().length];
            try {
                f44b[RunMode.RUN_USING_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.MIN_MOTOR;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f44b[RunMode.RUN_WITHOUT_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.MAX_MOTOR;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f44b[RunMode.RUN_TO_POSITION.ordinal()] = HiTechnicNxtDcMotorController.CHANNEL_MODE_MASK_SELECTION;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f44b[RunMode.RESET_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.OFFSET_MOTOR1_TARGET_ENCODER_VALUE;
            } catch (NoSuchFieldError ignored) {
            }
            f43a = new int[DeviceMode.values().length];
            try {
                f43a[DeviceMode.READ_ONLY.ordinal()] = HiTechnicNxtDcMotorController.MIN_MOTOR;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f43a[DeviceMode.WRITE_ONLY.ordinal()] = HiTechnicNxtDcMotorController.MAX_MOTOR;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f43a[DeviceMode.SWITCHING_TO_READ_MODE.ordinal()] = HiTechnicNxtDcMotorController.CHANNEL_MODE_MASK_SELECTION;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f43a[DeviceMode.SWITCHING_TO_WRITE_MODE.ordinal()] = HiTechnicNxtDcMotorController.OFFSET_MOTOR1_TARGET_ENCODER_VALUE;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }

    static {
        OFFSET_MAP_MOTOR_POWER = new byte[]{(byte) -1, (byte) 9, (byte) 10};
        OFFSET_MAP_MOTOR_MODE = new byte[]{(byte) -1, (byte) 8, (byte) 11};
        OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE = new byte[]{(byte) -1, (byte) 4, (byte) 12};
        OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE = new byte[]{(byte) -1, 16, (byte) 20};
    }

    public HiTechnicNxtDcMotorController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f51g = new ElapsedTime(0);
        this.f53i = true;
        this.f45a = legacyModule;
        this.f50f = physicalPort;
        this.f46b = legacyModule.getI2cReadCache(physicalPort);
        this.f47c = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f48d = legacyModule.getI2cWriteCache(physicalPort);
        this.f49e = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.f52h = DeviceMode.WRITE_ONLY;
        legacyModule.enableI2cWriteMode(physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
        try {
            this.f49e.lock();
            this.f48d[OFFSET_MOTOR1_POWER] = POWER_FLOAT;
            this.f48d[OFFSET_MOTOR2_POWER] = POWER_FLOAT;
            legacyModule.writeI2cCacheToController(physicalPort);
            legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
        } finally {
            this.f49e.unlock();
        }
    }

    public String getDeviceName() {
        return "NXT DC Motor Controller";
    }

    public String getConnectionInfo() {
        return this.f45a.getConnectionInfo() + "; port " + this.f50f;
    }

    public int getVersion() {
        return MIN_MOTOR;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        if (this.f52h != mode) {
            switch (C00041.f43a[mode.ordinal()]) {
                case MIN_MOTOR /*1*/:
                    this.f52h = DeviceMode.SWITCHING_TO_READ_MODE;
                    this.f45a.enableI2cReadMode(this.f50f, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                    break;
                case MAX_MOTOR /*2*/:
                    this.f52h = DeviceMode.SWITCHING_TO_WRITE_MODE;
                    this.f45a.enableI2cWriteMode(this.f50f, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                    break;
            }
            this.f53i = true;
        }
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.f52h;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        m41a(motor);
        m40a();
        byte runModeToFlagNXT = runModeToFlagNXT(mode);
        try {
            this.f49e.lock();
            if (this.f48d[OFFSET_MAP_MOTOR_MODE[motor]] != runModeToFlagNXT) {
                this.f48d[OFFSET_MAP_MOTOR_MODE[motor]] = runModeToFlagNXT;
                this.f53i = true;
            }
            this.f49e.unlock();
        } catch (Throwable th) {
            this.f49e.unlock();
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        m41a(motor);
        m42b();
        try {
            this.f47c.lock();
            byte b = this.f46b[OFFSET_MAP_MOTOR_MODE[motor]];
            return flagToRunModeNXT(b);
        } finally {
            this.f47c.unlock();
        }
    }

    public void setMotorPower(int motor, double power) {
        m41a(motor);
        m40a();
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        byte b = (byte) ((int) (100.0d * power));
        try {
            this.f49e.lock();
            if (b != this.f48d[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.f48d[OFFSET_MAP_MOTOR_POWER[motor]] = b;
                this.f53i = true;
            }
            this.f49e.unlock();
        } catch (Throwable th) {
            this.f49e.unlock();
        }
    }

    public double getMotorPower(int motor) {
        m41a(motor);
        m42b();
        try {
            this.f47c.lock();
            int i = this.f46b[OFFSET_MAP_MOTOR_POWER[motor]];
            if (i == -128) {
                return 0.0d;
            }
            return ((double) i) / 100.0d;
        } finally {
            this.f47c.unlock();
        }
    }

    public boolean isBusy(int motor) {
        m41a(motor);
        m42b();
        try {
            this.f47c.lock();
            boolean z = (this.f46b[OFFSET_MAP_MOTOR_MODE[motor]] & CHANNEL_MODE_MASK_BUSY) == CHANNEL_MODE_MASK_BUSY;
            this.f47c.unlock();
            return z;
        } catch (Throwable th) {
            this.f47c.unlock();
        }
        return false; //TOOD originally had no return statement. why?
    }

    public void setMotorPowerFloat(int motor) {
        m41a(motor);
        m40a();
        try {
            this.f49e.lock();
            if (-128 != this.f48d[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.f48d[OFFSET_MAP_MOTOR_POWER[motor]] = POWER_FLOAT;
                this.f53i = true;
            }
            this.f49e.unlock();
        } catch (Throwable th) {
            this.f49e.unlock();
        }
    }

    public boolean getMotorPowerFloat(int motor) {
        m41a(motor);
        m42b();
        try {
            this.f47c.lock();
            boolean z = this.f46b[OFFSET_MAP_MOTOR_POWER[motor]] == -128;
            this.f47c.unlock();
            return z;
        } catch (Throwable th) {
            this.f47c.unlock();
        }
        return false; //TODO originally had no return statement. why?
    }

    public void setMotorTargetPosition(int motor, int position) {
        m41a(motor);
        m40a();
        byte[] intToByteArray = TypeConversion.intToByteArray(position);
        try {
            this.f49e.lock();
            System.arraycopy(intToByteArray, 0, this.f48d, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], intToByteArray.length);
            this.f53i = true;
        } finally {
            this.f49e.unlock();
        }
    }

    public int getMotorTargetPosition(int motor) {
        m41a(motor);
        m42b();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.f47c.lock();
            System.arraycopy(this.f46b, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.f47c.unlock();
        }
    }

    public int getMotorCurrentPosition(int motor) {
        m41a(motor);
        m42b();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.f47c.lock();
            System.arraycopy(this.f46b, OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.f47c.unlock();
        }
    }

    public void close() {
        if (this.f52h == DeviceMode.WRITE_ONLY) {
            setMotorPowerFloat(MIN_MOTOR);
            setMotorPowerFloat(MAX_MOTOR);
        }
    }

    private void m40a() {
        if (this.f52h != DeviceMode.SWITCHING_TO_WRITE_MODE) {
            if (this.f52h == DeviceMode.READ_ONLY || this.f52h == DeviceMode.SWITCHING_TO_READ_MODE) {
                String str = "Cannot write while in this mode: " + this.f52h;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void m42b() {
        if (this.f52h != DeviceMode.SWITCHING_TO_READ_MODE) {
            if (this.f52h == DeviceMode.WRITE_ONLY || this.f52h == DeviceMode.SWITCHING_TO_WRITE_MODE) {
                String str = "Cannot read while in this mode: " + this.f52h;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void m41a(int i) {
        if (i < MIN_MOTOR || i > MAX_MOTOR) {
            Object[] objArr = new Object[MAX_MOTOR];
            objArr[0] = i;
            objArr[MIN_MOTOR] = MAX_MOTOR;
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", objArr));
        }
    }

    public static RunMode flagToRunModeNXT(byte flag) {
        switch (flag & CHANNEL_MODE_MASK_SELECTION) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_PULSE_OUTPUT_TIME /*0*/:
                return RunMode.RUN_WITHOUT_ENCODERS;
            case MIN_MOTOR /*1*/:
                return RunMode.RUN_USING_ENCODERS;
            case MAX_MOTOR /*2*/:
                return RunMode.RUN_TO_POSITION;
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
                return RunMode.RESET_ENCODERS;
            default:
                return RunMode.RUN_WITHOUT_ENCODERS;
        }
    }

    public static byte runModeToFlagNXT(RunMode mode) {
        switch (C00041.f44b[mode.ordinal()]) {
            case MAX_MOTOR /*2*/:
                return POWER_BREAK;
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case OFFSET_MOTOR1_TARGET_ENCODER_VALUE /*4*/:
                return CHANNEL_MODE_FLAG_SELECT_RESET;
            default:
                return CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED_NXT;
        }
    }

    public void portIsReady(int port) {
        switch (C00041.f43a[this.f52h.ordinal()]) {
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
                if (this.f45a.isI2cPortInReadMode(port)) {
                    this.f52h = DeviceMode.READ_ONLY;
                    break;
                }
                break;
            case OFFSET_MOTOR1_TARGET_ENCODER_VALUE /*4*/:
                if (this.f45a.isI2cPortInWriteMode(port)) {
                    this.f52h = DeviceMode.WRITE_ONLY;
                    break;
                }
                break;
        }
        if (this.f52h == DeviceMode.READ_ONLY) {
            this.f45a.setI2cPortActionFlag(this.f50f);
            this.f45a.writeI2cPortFlagOnlyToController(this.f50f);
        } else {
            if (this.f53i || this.f51g.time() > 2.0d) {
                this.f45a.setI2cPortActionFlag(this.f50f);
                this.f45a.writeI2cCacheToController(this.f50f);
                this.f51g.reset();
            }
            this.f53i = false;
        }
        this.f45a.readI2cCacheFromController(this.f50f);
    }
}
