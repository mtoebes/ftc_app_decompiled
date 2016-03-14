package com.qualcomm.hardware.hitechnic;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtDcMotorController extends HiTechnicNxtController implements DcMotorController, I2cPortReadyCallback {
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
    private byte[] f46a;
    private Lock f47b;
    private byte[] f48c;
    private Lock f49d;
    private final ElapsedTime f50e;
    private volatile DeviceMode f51f;
    private volatile boolean f52g;

    /* renamed from: com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController.1 */
    static /* synthetic */ class C00081 {
        static final /* synthetic */ int[] f44a;
        static final /* synthetic */ int[] f45b;

        static {
            f45b = new int[RunMode.values().length];
            try {
                f45b[RunMode.RUN_USING_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.MIN_MOTOR;
            } catch (NoSuchFieldError e) {
            }
            try {
                f45b[RunMode.RUN_WITHOUT_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.MAX_MOTOR;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f45b[RunMode.RUN_TO_POSITION.ordinal()] = HiTechnicNxtDcMotorController.CHANNEL_MODE_MASK_SELECTION;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f45b[RunMode.RESET_ENCODERS.ordinal()] = HiTechnicNxtDcMotorController.OFFSET_MOTOR1_TARGET_ENCODER_VALUE;
            } catch (NoSuchFieldError e4) {
            }
            f44a = new int[DeviceMode.values().length];
            try {
                f44a[DeviceMode.READ_ONLY.ordinal()] = HiTechnicNxtDcMotorController.MIN_MOTOR;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f44a[DeviceMode.SWITCHING_TO_READ_MODE.ordinal()] = HiTechnicNxtDcMotorController.MAX_MOTOR;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f44a[DeviceMode.WRITE_ONLY.ordinal()] = HiTechnicNxtDcMotorController.CHANNEL_MODE_MASK_SELECTION;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f44a[DeviceMode.SWITCHING_TO_WRITE_MODE.ordinal()] = HiTechnicNxtDcMotorController.OFFSET_MOTOR1_TARGET_ENCODER_VALUE;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f44a[DeviceMode.READ_WRITE.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    static {
        OFFSET_MAP_MOTOR_POWER = new byte[]{(byte) -1, (byte) 9, (byte) 10};
        OFFSET_MAP_MOTOR_MODE = new byte[]{(byte) -1, (byte) 8, (byte) 11};
        OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE = new byte[]{(byte) -1, (byte) 4, (byte) 12};
        OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE = new byte[]{(byte) -1, ModernRoboticsUsbLegacyModule.BUFFER_FLAG_S4, (byte) 20};
    }

    public HiTechnicNxtDcMotorController(LegacyModule module, int physicalPort) {
        super(module, physicalPort);
        this.f50e = new ElapsedTime(0);
        this.f51f = DeviceMode.WRITE_ONLY;
        this.f52g = true;
        finishConstruction();
        initializeHardware();
    }

    protected void controllerNowArmedOrPretending() {
        this.f46a = this.controller.getI2cReadCache(this.physicalPort);
        this.f47b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f48c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f49d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        adjustHookingToMatchEngagement();
    }

    protected void doHook() {
        switch (C00081.f44a[this.f51f.ordinal()]) {
            case MIN_MOTOR /*1*/:
            case MAX_MOTOR /*2*/:
                this.f51f = DeviceMode.SWITCHING_TO_READ_MODE;
                this.controller.enableI2cReadMode(this.physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                break;
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
            case OFFSET_MOTOR1_TARGET_ENCODER_VALUE /*4*/:
            case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
                this.f51f = DeviceMode.SWITCHING_TO_WRITE_MODE;
                this.controller.enableI2cWriteMode(this.physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                break;
        }
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public void initializeHardware() {
        try {
            this.f49d.lock();
            this.f48c[OFFSET_MOTOR1_POWER] = POWER_FLOAT;
            this.f48c[OFFSET_MOTOR2_POWER] = POWER_FLOAT;
            this.f52g = true;
        } finally {
            this.f49d.unlock();
        }
    }

    protected void doUnhook() {
        this.controller.deregisterForPortReadyCallback(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT DC Motor Controller";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return MIN_MOTOR;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        if (this.f51f != mode) {
            switch (C00081.f44a[mode.ordinal()]) {
                case MIN_MOTOR /*1*/:
                    this.f51f = DeviceMode.SWITCHING_TO_READ_MODE;
                    this.controller.enableI2cReadMode(this.physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                    break;
                case CHANNEL_MODE_MASK_SELECTION /*3*/:
                    this.f51f = DeviceMode.SWITCHING_TO_WRITE_MODE;
                    this.controller.enableI2cWriteMode(this.physicalPort, MAX_MOTOR, MEM_START_ADDRESS, OFFSET_MOTOR2_CURRENT_ENCODER_VALUE);
                    break;
            }
            this.f52g = true;
        }
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.f51f;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        m43a(motor);
        m42a();
        byte runModeToFlagNXT = runModeToFlagNXT(mode);
        try {
            this.f49d.lock();
            if (this.f48c[OFFSET_MAP_MOTOR_MODE[motor]] != runModeToFlagNXT) {
                this.f48c[OFFSET_MAP_MOTOR_MODE[motor]] = runModeToFlagNXT;
                this.f52g = true;
            }
            this.f49d.unlock();
        } catch (Throwable th) {
            this.f49d.unlock();
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        m43a(motor);
        m44b();
        try {
            this.f47b.lock();
            byte b = this.f46a[OFFSET_MAP_MOTOR_MODE[motor]];
            return flagToRunModeNXT(b);
        } finally {
            this.f47b.unlock();
        }
    }

    public void setMotorPower(int motor, double power) {
        m43a(motor);
        m42a();
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        byte b = (byte) ((int) (100.0d * power));
        try {
            this.f49d.lock();
            if (b != this.f48c[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.f48c[OFFSET_MAP_MOTOR_POWER[motor]] = b;
                this.f52g = true;
            }
            this.f49d.unlock();
        } catch (Throwable th) {
            this.f49d.unlock();
        }
    }

    public double getMotorPower(int motor) {
        m43a(motor);
        m44b();
        try {
            this.f47b.lock();
            int i = this.f46a[OFFSET_MAP_MOTOR_POWER[motor]];
            if (i == -128) {
                return 0.0d;
            }
            return ((double) i) / 100.0d;
        } finally {
            this.f47b.unlock();
        }
    }

    public boolean isBusy(int motor) {
        m43a(motor);
        m44b();
        try {
            this.f47b.lock();
            boolean z = (this.f46a[OFFSET_MAP_MOTOR_MODE[motor]] & CHANNEL_MODE_MASK_BUSY) == CHANNEL_MODE_MASK_BUSY;
            this.f47b.unlock();
            return z;
        } catch (Throwable th) {
            this.f47b.unlock();
        }
    }

    public void setMotorPowerFloat(int motor) {
        m43a(motor);
        m42a();
        try {
            this.f49d.lock();
            if (-128 != this.f48c[OFFSET_MAP_MOTOR_POWER[motor]]) {
                this.f48c[OFFSET_MAP_MOTOR_POWER[motor]] = POWER_FLOAT;
                this.f52g = true;
            }
            this.f49d.unlock();
        } catch (Throwable th) {
            this.f49d.unlock();
        }
    }

    public boolean getMotorPowerFloat(int motor) {
        m43a(motor);
        m44b();
        try {
            this.f47b.lock();
            boolean z = this.f46a[OFFSET_MAP_MOTOR_POWER[motor]] == -128;
            this.f47b.unlock();
            return z;
        } catch (Throwable th) {
            this.f47b.unlock();
        }
    }

    public void setMotorTargetPosition(int motor, int position) {
        m43a(motor);
        m42a();
        Object intToByteArray = TypeConversion.intToByteArray(position);
        try {
            this.f49d.lock();
            System.arraycopy(intToByteArray, 0, this.f48c, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], intToByteArray.length);
            this.f52g = true;
        } finally {
            this.f49d.unlock();
        }
    }

    public int getMotorTargetPosition(int motor) {
        m43a(motor);
        m44b();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.f47b.lock();
            System.arraycopy(this.f46a, OFFSET_MAP_MOTOR_TARGET_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.f47b.unlock();
        }
    }

    public int getMotorCurrentPosition(int motor) {
        m43a(motor);
        m44b();
        byte[] bArr = new byte[OFFSET_MOTOR1_TARGET_ENCODER_VALUE];
        try {
            this.f47b.lock();
            System.arraycopy(this.f46a, OFFSET_MAP_MOTOR_CURRENT_ENCODER_VALUE[motor], bArr, 0, bArr.length);
            return TypeConversion.byteArrayToInt(bArr);
        } finally {
            this.f47b.unlock();
        }
    }

    public void close() {
        if (this.f51f == DeviceMode.WRITE_ONLY) {
            setMotorPowerFloat(MIN_MOTOR);
            setMotorPowerFloat(MAX_MOTOR);
        }
    }

    private void m42a() {
        if (this.f51f != DeviceMode.SWITCHING_TO_WRITE_MODE) {
            if (this.f51f == DeviceMode.READ_ONLY || this.f51f == DeviceMode.SWITCHING_TO_READ_MODE) {
                String str = "Cannot write while in this mode: " + this.f51f;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void m44b() {
        if (this.f51f != DeviceMode.SWITCHING_TO_READ_MODE) {
            if (this.f51f == DeviceMode.WRITE_ONLY || this.f51f == DeviceMode.SWITCHING_TO_WRITE_MODE) {
                String str = "Cannot read while in this mode: " + this.f51f;
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace != null && stackTrace.length > CHANNEL_MODE_MASK_SELECTION) {
                    str = str + "\n from method: " + stackTrace[CHANNEL_MODE_MASK_SELECTION].getMethodName();
                }
                throw new IllegalArgumentException(str);
            }
        }
    }

    private void m43a(int i) {
        if (i < MIN_MOTOR || i > MAX_MOTOR) {
            Object[] objArr = new Object[MAX_MOTOR];
            objArr[0] = Integer.valueOf(i);
            objArr[MIN_MOTOR] = Integer.valueOf(MAX_MOTOR);
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
        switch (C00081.f45b[mode.ordinal()]) {
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
        switch (C00081.f44a[this.f51f.ordinal()]) {
            case MAX_MOTOR /*2*/:
                if (this.controller.isI2cPortInReadMode(port)) {
                    this.f51f = DeviceMode.READ_ONLY;
                    break;
                }
                break;
            case OFFSET_MOTOR1_TARGET_ENCODER_VALUE /*4*/:
                if (this.controller.isI2cPortInWriteMode(port)) {
                    this.f51f = DeviceMode.WRITE_ONLY;
                    break;
                }
                break;
        }
        if (this.f51f == DeviceMode.READ_ONLY) {
            this.controller.setI2cPortActionFlag(this.physicalPort);
            this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
        } else {
            if (this.f52g || this.f50e.time() > 2.0d) {
                this.controller.setI2cPortActionFlag(this.physicalPort);
                this.controller.writeI2cCacheToController(this.physicalPort);
                this.f50e.reset();
            }
            this.f52g = false;
        }
        this.controller.readI2cCacheFromController(this.physicalPort);
    }
}
