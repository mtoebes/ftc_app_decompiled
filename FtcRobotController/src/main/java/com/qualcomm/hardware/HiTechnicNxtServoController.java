package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import java.util.concurrent.locks.Lock;

class HiTechnicNxtServoController implements I2cPortReadyCallback, ServoController {
    private static final int VERSION = 1;

    private static final int I2C_ADDRESS = 2;
    private static final int MAX_SERVOS = 6;
    private static final int BUFFER_LENGTH = 7;
    private static final int START_ADDRESS = 66;
    private static final int OFFSET_PWM = 10;
    private static final int OFFSET_SERVO_POSITION = 4;
    private static final double ELAPSED_TIME_MAX = 5.0;

    private static final byte PWM_DISABLE = (byte) -1;
    private static final byte PWM_ENABLE = (byte) 0;

    private static final int SERVO_POSITION_MAX = 255;

    private final ElapsedTime elapsedTime = new ElapsedTime(0);
    private volatile boolean unknownResetCheck = true;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] writeCache;
    private final Lock writeCacheLock;
    private final int controllerPort;

    public HiTechnicNxtServoController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.legacyModule = legacyModule;
        this.controllerPort = physicalPort;
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);

        legacyModule.enableI2cWriteMode(physicalPort, I2C_ADDRESS, START_ADDRESS, BUFFER_LENGTH);
        pwmDisable();
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.writeI2cCacheToController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public String getDeviceName() {
        return "NXT Servo Controller";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.controllerPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
        pwmDisable();
    }

    private void pwmEnable(boolean enable) {
        byte state = enable ? PWM_ENABLE : PWM_DISABLE;
        try {
            this.writeCacheLock.lock();
            if (this.writeCache[OFFSET_PWM] != state) {
                this.writeCache[OFFSET_PWM] = state;
                this.unknownResetCheck = true;
            }
        } finally {
            this.writeCacheLock.unlock();
        }
    }
    public void pwmEnable() {
        pwmEnable(true);
    }

    public void pwmDisable() {
        pwmEnable(false);
    }

    public PwmStatus getPwmStatus() {
        return PwmStatus.DISABLED;
    }

    public void setServoPosition(int channel, double position) {
        validateChannel(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        byte servoPosition = (byte) ((int) (SERVO_POSITION_MAX * position));
        try {
            this.writeCacheLock.lock();
            int offsetServoPosition = getOffsetServoPosition(channel);
            if (servoPosition != this.writeCache[offsetServoPosition]) {
                this.unknownResetCheck = true;
                this.writeCache[offsetServoPosition] = servoPosition;
                this.writeCache[OFFSET_PWM] = PWM_ENABLE;
            }
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public double getServoPosition(int channel) {
        return 0.0d;
    }

    private void validateChannel(int channel) {
        if (channel < 1 || channel > MAX_SERVOS) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", channel, MAX_SERVOS));
        }
    }

    public void portIsReady(int port) {
        if (this.unknownResetCheck || this.elapsedTime.time() > ELAPSED_TIME_MAX) {
            this.legacyModule.setI2cPortActionFlag(this.controllerPort);
            this.legacyModule.writeI2cCacheToController(this.controllerPort);
            this.elapsedTime.reset();
        }
        this.unknownResetCheck = false;
    }

    private int getOffsetServoPosition(int servo) {
        return OFFSET_SERVO_POSITION + (servo - 1);
    }
}
