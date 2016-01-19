package com.qualcomm.hardware;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtColorSensor extends ColorSensor implements I2cPortReadyCallback {
    private static final int VERSION = 2;

    public static final int ADDRESS_I2C = 2;
    public static final int BUFFER_LENGTH = 5;
    public static final int START_ADDRESS = 65;

    public static final int COMMAND_ACTIVE_LED = 0;
    public static final int COMMAND_PASSIVE_LED = 1;

    public static final int OFFSET_LED_MODE = 4;
    public static final int OFFSET_RED_READING = 6;
    public static final int OFFSET_GREEN_READING = 7;
    public static final int OFFSET_BLUE_READING = 8;

    private ColorSensorMode mode = ColorSensorMode.READING_ONLY;
    private volatile int ledMode = COMMAND_ACTIVE_LED;

    private final LegacyModule legacyModule;
    private final int physicalPort;

    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;

    private enum ColorSensorMode {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    HiTechnicNxtColorSensor(LegacyModule legacyModule, int physicalPort) {
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;

        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = legacyModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = legacyModule.getI2cWriteCacheLock(physicalPort);

        legacyModule.enableI2cReadMode(physicalPort, ADDRESS_I2C, START_ADDRESS, BUFFER_LENGTH);
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.writeI2cCacheToController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return getChannel(OFFSET_RED_READING);
    }

    public int green() {
        return getChannel(OFFSET_GREEN_READING);
    }

    public int blue() {
        return getChannel(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return 0;
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public void enableLed(boolean enable) {
        int newMode = enable ? COMMAND_ACTIVE_LED : COMMAND_PASSIVE_LED;

        if (this.ledMode != newMode) {
            this.ledMode = newMode;
            this.mode = ColorSensorMode.PERFORMING_WRITE;
            try {
                this.writeCacheLock.lock();
                this.writeCache[OFFSET_LED_MODE] = (byte) newMode;
            } finally {
                this.writeCacheLock.unlock();
            }
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }

    private int getChannel(int channelIndex) {
        byte colorByte;
        try {
            this.readCacheLock.lock();
            colorByte = this.readCache[channelIndex];
        } finally {
            this.readCacheLock.unlock();
        }
        return TypeConversion.unsignedByteToInt(colorByte);

    }

    public String getDeviceName() {
        return "NXT Color Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        this.legacyModule.setI2cPortActionFlag(this.physicalPort);
        this.legacyModule.readI2cCacheFromController(this.physicalPort);
        if (this.mode == ColorSensorMode.PERFORMING_WRITE) {
            this.legacyModule.enableI2cWriteMode(this.physicalPort, ADDRESS_I2C, START_ADDRESS, BUFFER_LENGTH);
            this.legacyModule.writeI2cCacheToController(this.physicalPort);
            this.mode = ColorSensorMode.SWITCHING_TO_READ;
        } else if (this.mode == ColorSensorMode.SWITCHING_TO_READ) {
            this.legacyModule.enableI2cReadMode(this.physicalPort, ADDRESS_I2C, START_ADDRESS, BUFFER_LENGTH);
            this.legacyModule.writeI2cCacheToController(this.physicalPort);
            this.mode = ColorSensorMode.READING_ONLY;
        } else {
            this.legacyModule.writeI2cPortFlagOnlyToController(this.physicalPort);
        }
    }
}
