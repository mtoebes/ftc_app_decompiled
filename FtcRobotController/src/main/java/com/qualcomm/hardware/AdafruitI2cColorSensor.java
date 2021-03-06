package com.qualcomm.hardware;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import java.util.concurrent.locks.Lock;

class AdafruitI2cColorSensor extends ColorSensor implements I2cPortReadyCallback {
    private static final int VERSION = 1;

    private static final int I2C_ADDRESS = 82;
    private static final int WRITE_ACTION_ADDRESS = 128;
    private static final int WRITE_ACTION_BUFFER_SIZE = 1;
    private static final int READ_ACTION_ADDRESS = 148;
    private static final int READ_ACTION_BUFFER_SIZE = 8;

    private static final int OFFSET_ALPHA_LOW_BYTE = 4;
    private static final int OFFSET_ALPHA_HIGH_BYTE = 5;
    private static final int OFFSET_RED_LOW_BYTE = 6;
    private static final int OFFSET_RED_HIGH_BYTE = 7;
    private static final int OFFSET_GREEN_LOW_BYTE = 8;
    private static final int OFFSET_GREEN_HIGH_BYTE = 9;
    private static final int OFFSET_BLUE_LOW_BYTE = 10;
    private static final int OFFSET_BLUE_HIGH_BYTE = 11;

    private boolean performWrite = true;
    private boolean performRead = false;

    private final DeviceInterfaceModule deviceInterfaceModule;
    private final int pyhsicalPort;

    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;

    public AdafruitI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.pyhsicalPort = physicalPort;
        this.deviceInterfaceModule = deviceInterfaceModule;

        this.readCache = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.readCacheLock = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = deviceInterfaceModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = deviceInterfaceModule.getI2cWriteCacheLock(physicalPort);

        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return getColor(OFFSET_RED_HIGH_BYTE, OFFSET_RED_LOW_BYTE);
    }

    public int green() {
        return getColor(OFFSET_GREEN_HIGH_BYTE, OFFSET_GREEN_LOW_BYTE);
    }

    public int blue() {
        return getColor(OFFSET_BLUE_HIGH_BYTE, OFFSET_BLUE_LOW_BYTE);
    }

    public int alpha() {
        return getColor(OFFSET_ALPHA_HIGH_BYTE, OFFSET_ALPHA_LOW_BYTE);
    }

    private int getColor(int highByte, int lowByte) {
        try {
            this.readCacheLock.lock();
            return (this.readCache[highByte] << 8) | (this.readCache[lowByte] & 255);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public void enableLed(boolean enable) {
        throw new UnsupportedOperationException("enableLed is not implemented.");
    }

    public String getDeviceName() {
        return "Adafruit I2C Color Sensor";
    }

    public String getConnectionInfo() {
        return this.deviceInterfaceModule.getConnectionInfo() + "; I2C port: " + this.pyhsicalPort;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        if (this.performWrite) {
            writeAction();
            this.performWrite = false;
            this.performRead = true;
        } else if (this.performRead) {
            readAction();
            this.performRead = false;
        }
        this.deviceInterfaceModule.readI2cCacheFromController(this.pyhsicalPort);
        this.deviceInterfaceModule.setI2cPortActionFlag(this.pyhsicalPort);
        this.deviceInterfaceModule.writeI2cPortFlagOnlyToController(this.pyhsicalPort);
    }

    private void readAction() {
        this.deviceInterfaceModule.enableI2cReadMode(this.pyhsicalPort, I2C_ADDRESS, READ_ACTION_ADDRESS, READ_ACTION_BUFFER_SIZE);
        this.deviceInterfaceModule.writeI2cCacheToController(this.pyhsicalPort);
    }

    private void writeAction() {
        this.deviceInterfaceModule.enableI2cWriteMode(this.pyhsicalPort, I2C_ADDRESS, WRITE_ACTION_ADDRESS, WRITE_ACTION_BUFFER_SIZE);
        try {
            this.writeCacheLock.lock();
            this.writeCache[4] = (byte) 3;
            this.deviceInterfaceModule.setI2cPortActionFlag(this.pyhsicalPort);
            this.deviceInterfaceModule.writeI2cCacheToController(this.pyhsicalPort);
        } finally {
            this.writeCacheLock.unlock();
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }
}
