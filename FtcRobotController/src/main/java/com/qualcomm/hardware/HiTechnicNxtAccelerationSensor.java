package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AccelerationSensor.Acceleration;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtAccelerationSensor extends AccelerationSensor implements I2cPortReadyCallback {
    public static final int ACCEL_LENGTH = 6;
    public static final int ADDRESS_ACCEL_START = 66;
    public static final byte I2C_ADDRESS = (byte) 2;
    private final ModernRoboticsUsbLegacyModule f18a;
    private final byte[] f19b;
    private final Lock f20c;
    private final int f21d;

    public HiTechnicNxtAccelerationSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        legacyModule.enableI2cReadMode(physicalPort, 2, ADDRESS_ACCEL_START, ACCEL_LENGTH);
        this.f18a = legacyModule;
        this.f19b = legacyModule.getI2cReadCache(physicalPort);
        this.f20c = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f21d = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public Acceleration getAcceleration() {
        Acceleration acceleration = new Acceleration();
        try {
            this.f20c.lock();
            acceleration.x = m36a((double) this.f19b[4], (double) this.f19b[7]);
            acceleration.y = m36a((double) this.f19b[5], (double) this.f19b[8]);
            acceleration.z = m36a((double) this.f19b[ACCEL_LENGTH], (double) this.f19b[9]);
            return acceleration;
        } finally {
            this.f20c.unlock();
        }
    }

    public String status() {
        return String.format("NXT Acceleration Sensor, connected via device %s, port %d", this.f18a.getSerialNumber().toString(), this.f21d);
    }

    private double m36a(double d, double d2) {
        return ((4.0d * d) + d2) / 200.0d;
    }

    public void portIsReady(int port) {
        this.f18a.setI2cPortActionFlag(this.f21d);
        this.f18a.writeI2cPortFlagOnlyToController(this.f21d);
        this.f18a.readI2cCacheFromController(this.f21d);
    }

    public String getDeviceName() {
        return "NXT Acceleration Sensor";
    }

    public String getConnectionInfo() {
        return this.f18a.getConnectionInfo() + "; port " + this.f21d;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
