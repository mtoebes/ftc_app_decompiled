package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.ServoController;
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
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] writeCache;
    private final Lock readCache;
    private final int controllerPort;
    private ElapsedTime elapsedTime;
    private volatile boolean unknownResetCheck;

    static {
        OFFSET_SERVO_MAP = new byte[]{PWM_DISABLE, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9};
    }

    public HiTechnicNxtServoController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.elapsedTime = new ElapsedTime(0);
        this.unknownResetCheck = true;
        this.legacyModule = legacyModule;
        this.controllerPort = physicalPort;
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.readCache = legacyModule.getI2cWriteCacheLock(physicalPort);
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
        return this.legacyModule.getConnectionInfo() + "; port " + this.controllerPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        pwmDisable();
    }

    public void pwmEnable() {
        try {
            this.readCache.lock();
            if (this.writeCache[OFFSET_PWM] != 0) { //TODO originally was comparing to null. why
                this.writeCache[OFFSET_PWM] = PWM_ENABLE;
                this.unknownResetCheck = true;
            }
            this.readCache.unlock();
        } catch (Throwable th) {
            this.readCache.unlock();
        }
    }

    public void pwmDisable() {
        try {
            this.readCache.lock();
            if (OFFSET_UNUSED != this.writeCache[OFFSET_PWM]) {
                this.writeCache[OFFSET_PWM] = PWM_DISABLE;
                this.unknownResetCheck = true;
            }
            this.readCache.unlock();
        } catch (Throwable th) {
            this.readCache.unlock();
        }
    }

    public PwmStatus getPwmStatus() {
        return PwmStatus.DISABLED;
    }

    public void setServoPosition(int channel, double position) {
        validateChannel(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        byte b = (byte) ((int) (255.0d * position));
        try {
            this.readCache.lock();
            if (b != this.writeCache[OFFSET_SERVO_MAP[channel]]) {
                this.unknownResetCheck = true;
                this.writeCache[OFFSET_SERVO_MAP[channel]] = b;
                this.writeCache[OFFSET_PWM] = PWM_ENABLE;
            }
            this.readCache.unlock();
        } catch (Throwable th) {
            this.readCache.unlock();
        }
    }

    public double getServoPosition(int channel) {
        return 0.0d;
    }

    private void validateChannel(int channel) {
        if (channel < 1 || channel > OFFSET_SERVO_MAP.length) {
            Object[] objArr = new Object[I2C_ADDRESS];
            objArr[0] = channel;
            objArr[1] = OFFSET_SERVO3_POSITION;
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", objArr));
        }
    }

    public void portIsReady(int port) {
        if (this.unknownResetCheck || this.elapsedTime.time() > 5.0d) {
            this.legacyModule.setI2cPortActionFlag(this.controllerPort);
            this.legacyModule.writeI2cCacheToController(this.controllerPort);
            this.elapsedTime.reset();
        }
        this.unknownResetCheck = false;
    }
}
