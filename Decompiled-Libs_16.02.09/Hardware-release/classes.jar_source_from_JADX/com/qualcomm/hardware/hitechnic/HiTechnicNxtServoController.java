package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoController.PwmStatus;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtServoController extends HiTechnicNxtController implements I2cPortReadyCallback, ServoController {
    public static final int I2C_ADDRESS = 2;
    public static final int MAX_SERVOS = 6;
    public static final int MEM_READ_LENGTH = 7;
    public static final int MEM_START_ADDRESS = 66;
    public static final int OFFSET_PWM = 10;
    public static final int OFFSET_SERVO1_POSITION = 4;
    public static final int OFFSET_SERVO2_POSITION = 5;
    public static final int OFFSET_SERVO3_POSITION = 6;
    public static final int OFFSET_SERVO4_POSITION = 7;
    public static final int OFFSET_SERVO5_POSITION = 8;
    public static final int OFFSET_SERVO6_POSITION = 9;
    public static final byte[] OFFSET_SERVO_MAP;
    public static final int OFFSET_UNUSED = -1;
    public static final byte PWM_DISABLE = (byte) -1;
    public static final byte PWM_ENABLE = (byte) 0;
    public static final byte PWM_ENABLE_WITHOUT_TIMEOUT = (byte) -86;
    public static final int SERVO_POSITION_MAX = 255;
    private byte[] f60a;
    private Lock f61b;
    private ElapsedTime f62c;
    private volatile boolean f63d;

    static {
        OFFSET_SERVO_MAP = new byte[]{PWM_DISABLE, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9};
    }

    public HiTechnicNxtServoController(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f62c = new ElapsedTime(0);
        this.f63d = true;
        finishConstruction();
        initializeHardware();
    }

    protected void controllerNowArmedOrPretending() {
        this.f60a = this.controller.getI2cWriteCache(this.physicalPort);
        this.f61b = this.controller.getI2cWriteCacheLock(this.physicalPort);
        adjustHookingToMatchEngagement();
    }

    protected void doHook() {
        this.controller.enableI2cWriteMode(this.physicalPort, I2C_ADDRESS, MEM_START_ADDRESS, OFFSET_SERVO4_POSITION);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public void initializeHardware() {
        pwmDisable();
        this.f63d = true;
    }

    protected void doUnhook() {
        this.controller.deregisterForPortReadyCallback(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Servo Controller";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        pwmDisable();
    }

    public void pwmEnable() {
        try {
            this.f61b.lock();
            if (this.f60a[OFFSET_PWM] != null) {
                this.f60a[OFFSET_PWM] = PWM_ENABLE;
                this.f63d = true;
            }
            this.f61b.unlock();
        } catch (Throwable th) {
            this.f61b.unlock();
        }
    }

    public void pwmDisable() {
        try {
            this.f61b.lock();
            if (OFFSET_UNUSED != this.f60a[OFFSET_PWM]) {
                this.f60a[OFFSET_PWM] = PWM_DISABLE;
                this.f63d = true;
            }
            this.f61b.unlock();
        } catch (Throwable th) {
            this.f61b.unlock();
        }
    }

    public PwmStatus getPwmStatus() {
        return PwmStatus.DISABLED;
    }

    public void setServoPosition(int channel, double position) {
        m47a(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        byte b = (byte) ((int) (255.0d * position));
        try {
            this.f61b.lock();
            if (b != this.f60a[OFFSET_SERVO_MAP[channel]]) {
                this.f63d = true;
                this.f60a[OFFSET_SERVO_MAP[channel]] = b;
                this.f60a[OFFSET_PWM] = PWM_ENABLE;
            }
            this.f61b.unlock();
        } catch (Throwable th) {
            this.f61b.unlock();
        }
    }

    public double getServoPosition(int channel) {
        return 0.0d;
    }

    private void m47a(int i) {
        if (i < 1 || i > OFFSET_SERVO_MAP.length) {
            Object[] objArr = new Object[I2C_ADDRESS];
            objArr[0] = Integer.valueOf(i);
            objArr[1] = Integer.valueOf(OFFSET_SERVO3_POSITION);
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", objArr));
        }
    }

    public void portIsReady(int port) {
        if (this.f63d || this.f62c.time() > 5.0d) {
            this.controller.setI2cPortActionFlag(this.physicalPort);
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f62c.reset();
        }
        this.f63d = false;
    }
}
