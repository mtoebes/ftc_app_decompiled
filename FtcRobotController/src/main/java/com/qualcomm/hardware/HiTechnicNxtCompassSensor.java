package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtCompassSensor extends CompassSensor implements I2cPortReadyCallback {
    public static final byte CALIBRATION = (byte) 67;
    public static final byte CALIBRATION_FAILURE = (byte) 70;
    public static final int COMPASS_BUFFER = 65;
    public static final int COMPASS_BUFFER_SIZE = 5;
    public static final byte DIRECTION_END = (byte) 9;
    public static final byte DIRECTION_START = (byte) 7;
    public static final byte HEADING_IN_TWO_DEGREE_INCREMENTS = (byte) 66;
    public static final int HEADING_WORD_LENGTH = 2;
    public static final byte I2C_ADDRESS = (byte) 2;
    public static final double INVALID_DIRECTION = -1.0d;
    public static final byte MEASUREMENT = (byte) 0;
    public static final byte MODE_CONTROL_ADDRESS = (byte) 65;
    public static final byte ONE_DEGREE_HEADING_ADDER = (byte) 67;

    private static final byte OFFSET_COMMAND = 3;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;
    private final int physicalPort;
    private CompassMode compassMode = CompassMode.MEASUREMENT_MODE;
    private boolean settingMode;

    public HiTechnicNxtCompassSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableI2cReadMode(physicalPort, I2C_ADDRESS, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
        this.legacyModule = legacyModule;
        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);
        this.physicalPort = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public double getDirection() {
        if (this.settingMode || this.compassMode == CompassMode.CALIBRATION_MODE) {
            return INVALID_DIRECTION;
        }
        try {
            this.readCacheLock.lock();
            byte[] copyOfRange = Arrays.copyOfRange(this.readCache, DIRECTION_START, DIRECTION_END);
            return (double) TypeConversion.byteArrayToShort(copyOfRange, ByteOrder.LITTLE_ENDIAN);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public String status() {
        Object[] stateBuffer = new Object[HEADING_WORD_LENGTH];
        stateBuffer[0] = this.legacyModule.getSerialNumber().toString();
        stateBuffer[1] = this.physicalPort;
        return String.format("NXT Compass Sensor, connected via device %s, port %d", stateBuffer);
    }

    public void setMode(CompassMode mode) {
        if (this.compassMode != mode) {
            this.compassMode = mode;
            this.settingMode = true;

            byte compassModeByte = (this.compassMode == CompassMode.CALIBRATION_MODE ? CALIBRATION : MEASUREMENT);
            this.legacyModule.enableI2cWriteMode(this.physicalPort, I2C_ADDRESS, COMPASS_BUFFER, 1);
            try {
                this.writeCacheLock.lock();
                this.writeCache[OFFSET_COMMAND] = compassModeByte;
            } finally {
                this.writeCacheLock.unlock();
            }
        }
    }

    public boolean calibrationFailed() {
        boolean calibrationFailed = false;
        if (!(this.compassMode == CompassMode.CALIBRATION_MODE || this.settingMode)) {
            try {
                this.readCacheLock.lock();
                if (this.readCache[OFFSET_COMMAND] == CALIBRATION_FAILURE) {
                    calibrationFailed = true;
                }
                this.readCacheLock.unlock();
            } catch (Throwable th) {
                this.readCacheLock.unlock();
            }
        }
        return calibrationFailed;
    }

    public void portIsReady(int port) {
        this.legacyModule.setI2cPortActionFlag(this.physicalPort);
        this.legacyModule.readI2cCacheFromController(this.physicalPort);
        if (this.settingMode) {
            if (this.compassMode == CompassMode.MEASUREMENT_MODE) {
                this.legacyModule.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, COMPASS_BUFFER, COMPASS_BUFFER_SIZE);
            }
            this.settingMode = false;
            this.legacyModule.writeI2cCacheToController(this.physicalPort);
            return;
        }
        this.legacyModule.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Compass Sensor";
    }

    public String getConnectionInfo() {
        return this.legacyModule.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
