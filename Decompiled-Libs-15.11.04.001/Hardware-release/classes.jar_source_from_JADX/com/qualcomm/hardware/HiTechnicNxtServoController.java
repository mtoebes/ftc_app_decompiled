package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoController.PwmStatus;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtServoController implements I2cPortReadyCallback, ServoController {
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
    private final ModernRoboticsUsbLegacyModule f67a;
    private final byte[] f68b;
    private final Lock f69c;
    private final int f70d;
    private ElapsedTime f71e;
    private volatile boolean f72f;

    static {
        OFFSET_SERVO_MAP = new byte[]{PWM_DISABLE, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9};
    }

    public HiTechnicNxtServoController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f71e = new ElapsedTime(0);
        this.f72f = true;
        this.f67a = legacyModule;
        this.f70d = physicalPort;
        this.f68b = legacyModule.getI2cWriteCache(physicalPort);
        this.f69c = legacyModule.getI2cWriteCacheLock(physicalPort);
        legacyModule.enableI2cWriteMode(physicalPort, I2C_ADDRESS, MEM_START_ADDRESS, OFFSET_SERVO4_POSITION);
        pwmDisable();
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.writeI2cCacheToController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public String getDeviceName() {
        return "NXT Servo Controller";
    }

    public String getConnectionInfo() {
        return this.f67a.getConnectionInfo() + "; port " + this.f70d;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        pwmDisable();
    }

    public void pwmEnable() {
        try {
            this.f69c.lock();
            if (this.f68b[OFFSET_PWM] != null) {
                this.f68b[OFFSET_PWM] = PWM_ENABLE;
                this.f72f = true;
            }
            this.f69c.unlock();
        } catch (Throwable th) {
            this.f69c.unlock();
        }
    }

    public void pwmDisable() {
        try {
            this.f69c.lock();
            if (OFFSET_UNUSED != this.f68b[OFFSET_PWM]) {
                this.f68b[OFFSET_PWM] = PWM_DISABLE;
                this.f72f = true;
            }
            this.f69c.unlock();
        } catch (Throwable th) {
            this.f69c.unlock();
        }
    }

    public PwmStatus getPwmStatus() {
        return PwmStatus.DISABLED;
    }

    public void setServoPosition(int channel, double position) {
        m45a(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        byte b = (byte) ((int) (255.0d * position));
        try {
            this.f69c.lock();
            if (b != this.f68b[OFFSET_SERVO_MAP[channel]]) {
                this.f72f = true;
                this.f68b[OFFSET_SERVO_MAP[channel]] = b;
                this.f68b[OFFSET_PWM] = PWM_ENABLE;
            }
            this.f69c.unlock();
        } catch (Throwable th) {
            this.f69c.unlock();
        }
    }

    public double getServoPosition(int channel) {
        return 0.0d;
    }

    private void m45a(int i) {
        if (i < 1 || i > OFFSET_SERVO_MAP.length) {
            Object[] objArr = new Object[I2C_ADDRESS];
            objArr[0] = Integer.valueOf(i);
            objArr[1] = Integer.valueOf(OFFSET_SERVO3_POSITION);
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", objArr));
        }
    }

    public void portIsReady(int port) {
        if (this.f72f || this.f71e.time() > 5.0d) {
            this.f67a.setI2cPortActionFlag(this.f70d);
            this.f67a.writeI2cCacheToController(this.f70d);
            this.f71e.reset();
        }
        this.f72f = false;
    }
}
