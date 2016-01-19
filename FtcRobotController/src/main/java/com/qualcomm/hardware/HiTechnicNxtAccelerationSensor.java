package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtAccelerationSensor extends AccelerationSensor implements I2cPortReadyCallback {
    private static final int VERSION = 1;

    public static final byte I2C_ADDRESS = (byte) 2;
    public static final int START_ADDRESS = 66;
    public static final int BUFFER_LENGTH = 6;


    private static final byte ACCEL_X = 4;
    private static final byte ACCEL_Y = 5;
    private static final byte ACCEL_Z = 6;
    private static final byte ACCEL_OFFSET = 3;

    private final ModernRoboticsUsbLegacyModule legacyModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final int physicalPort;

    public HiTechnicNxtAccelerationSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableI2cReadMode(physicalPort, I2C_ADDRESS, START_ADDRESS, BUFFER_LENGTH);
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

    private double readCoord(byte lowByte, byte highByte) {
        return ((4.0d * ((double) lowByte)) + ((double) highByte)) / 200.0d; //TODO understand this logic
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
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
