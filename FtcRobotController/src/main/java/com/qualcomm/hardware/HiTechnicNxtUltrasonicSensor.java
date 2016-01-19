package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

class HiTechnicNxtUltrasonicSensor extends UltrasonicSensor implements I2cPortReadyCallback {
    private static final int START_ADDRESS = 66;
    private static final int BUFFER_LENGTH = 1;
    private static final int VERSION = 1;
    private static final int I2C_ADDRESS = 2;
    private static final int MAX_PORT = 5;
    private static final int MIN_PORT = 4;
    private static final int OFFSET_ULTRASONIC_LEVEL = 4;
    private final Lock readCacheLock;
    private final byte[] readCache;
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final int physicalPort;

    HiTechnicNxtUltrasonicSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        validatePort(physicalPort);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);

        legacyModule.enableI2cReadMode(physicalPort, I2C_ADDRESS, START_ADDRESS, BUFFER_LENGTH);
        legacyModule.enable9v(physicalPort, true);
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.readI2cCacheFromController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public double getUltrasonicLevel() {
        byte ultrasonicLevel;
        try {
            this.readCacheLock.lock();
            ultrasonicLevel =  this.readCache[OFFSET_ULTRASONIC_LEVEL];
        } finally {
            this.readCacheLock.unlock();
        }
        return  TypeConversion.unsignedByteToDouble(ultrasonicLevel);
    }

    public void portIsReady(int port) {
        this.legacyModule.setI2cPortActionFlag(this.physicalPort);
        this.legacyModule.writeI2cCacheToController(this.physicalPort);
        this.legacyModule.readI2cCacheFromController(this.physicalPort);
    }

    public String status() {
        return String.format("NXT Ultrasonic Sensor, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Ultrasonic Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    private void validatePort(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException(String.format("Port %d is invalid for " + getDeviceName() + "; valid ports are %d or %d", new Object[]{port, MIN_PORT, MAX_PORT}));
        }
    }
}
