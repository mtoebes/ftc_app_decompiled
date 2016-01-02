package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtAccelerationSensor extends AccelerationSensor implements I2cPortReadyCallback {
    public static final int ACCEL_LENGTH = 6;
    public static final int ADDRESS_ACCEL_START = 66;
    public static final byte I2C_ADDRESS = (byte) 2;

    private static final byte ACCEL_X = 4;
    private static final byte ACCEL_Y = 5;
    private static final byte ACCEL_Z = 6;
    private static final byte ACCEL_OFFSET = 3;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final int physicalPort;

    public HiTechnicNxtAccelerationSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableI2cReadMode(physicalPort, I2C_ADDRESS, ADDRESS_ACCEL_START, ACCEL_LENGTH);
        this.legacyModule = legacyModule;
        this.readCache = legacyModule.getI2cReadCache(physicalPort);
        this.readCacheLock = legacyModule.getI2cReadCacheLock(physicalPort);
        this.physicalPort = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public Acceleration getAcceleration() {
        Acceleration acceleration = new Acceleration();
        try {
            this.readCacheLock.lock();
            acceleration.x = readCoord(this.readCache[ACCEL_X], this.readCache[ACCEL_X + ACCEL_OFFSET]);
            acceleration.y = readCoord(this.readCache[ACCEL_Y], this.readCache[ACCEL_Y + ACCEL_OFFSET]);
            acceleration.z = readCoord(this.readCache[ACCEL_Z], this.readCache[ACCEL_Z + ACCEL_OFFSET]);
            return acceleration;
        } finally {
            this.readCacheLock.unlock();
        }
    }

    public String status() {
        return String.format("NXT Acceleration Sensor, connected via device %s, port %d", this.legacyModule.getSerialNumber().toString(), this.physicalPort);
    }

    private double readCoord(byte b1, byte b2) {
        return ((4.0d * ((double) b1)) + ((double) b2)) / 200.0d;
    }

    public void portIsReady(int port) {
        this.legacyModule.setI2cPortActionFlag(this.physicalPort);
        this.legacyModule.writeI2cPortFlagOnlyToController(this.physicalPort);
        this.legacyModule.readI2cCacheFromController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Acceleration Sensor";
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
