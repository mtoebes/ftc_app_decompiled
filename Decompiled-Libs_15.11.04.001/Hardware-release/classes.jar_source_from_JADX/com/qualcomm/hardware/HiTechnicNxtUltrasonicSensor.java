package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtUltrasonicSensor extends UltrasonicSensor implements I2cPortReadyCallback {
    public static final int ADDRESS_DISTANCE = 66;
    public static final int I2C_ADDRESS = 2;
    public static final int MAX_PORT = 5;
    public static final int MIN_PORT = 4;
    Lock f78a;
    byte[] f79b;
    private final ModernRoboticsUsbLegacyModule f80c;
    private final int f81d;

    HiTechnicNxtUltrasonicSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        m48a(physicalPort);
        this.f80c = legacyModule;
        this.f81d = physicalPort;
        this.f78a = legacyModule.getI2cReadCacheLock(physicalPort);
        this.f79b = legacyModule.getI2cReadCache(physicalPort);
        legacyModule.enableI2cReadMode(physicalPort, I2C_ADDRESS, ADDRESS_DISTANCE, 1);
        legacyModule.enable9v(physicalPort, true);
        legacyModule.setI2cPortActionFlag(physicalPort);
        legacyModule.readI2cCacheFromController(physicalPort);
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public double getUltrasonicLevel() {
        try {
            this.f78a.lock();
            byte b = this.f79b[MIN_PORT];
            return TypeConversion.unsignedByteToDouble(b);
        } finally {
            this.f78a.unlock();
        }
    }

    public void portIsReady(int port) {
        this.f80c.setI2cPortActionFlag(this.f81d);
        this.f80c.writeI2cCacheToController(this.f81d);
        this.f80c.readI2cCacheFromController(this.f81d);
    }

    public String status() {
        Object[] objArr = new Object[I2C_ADDRESS];
        objArr[0] = this.f80c.getSerialNumber().toString();
        objArr[1] = Integer.valueOf(this.f81d);
        return String.format("NXT Ultrasonic Sensor, connected via device %s, port %d", objArr);
    }

    public String getDeviceName() {
        return "NXT Ultrasonic Sensor";
    }

    public String getConnectionInfo() {
        return this.f80c.getConnectionInfo() + "; port " + this.f81d;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    private void m48a(int i) {
        if (i < MIN_PORT || i > MAX_PORT) {
            throw new IllegalArgumentException(String.format("Port %d is invalid for " + getDeviceName() + "; valid ports are %d or %d", new Object[]{Integer.valueOf(i), Integer.valueOf(MIN_PORT), Integer.valueOf(MAX_PORT)}));
        }
    }
}
