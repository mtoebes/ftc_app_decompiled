package com.qualcomm.hardware.hitechnic;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.LegacyModulePortDeviceImpl;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtUltrasonicSensor extends LegacyModulePortDeviceImpl implements I2cPortReadyCallback, UltrasonicSensor {
    public static final int ADDRESS_DISTANCE = 66;
    public static final int I2C_ADDRESS = 2;
    public static final int MAX_PORT = 5;
    public static final int MIN_PORT = 4;
    Lock f65a;
    byte[] f66b;

    public HiTechnicNxtUltrasonicSensor(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
        m50a(physicalPort);
        finishConstruction();
    }

    protected void moduleNowArmedOrPretending() {
        this.f65a = this.module.getI2cReadCacheLock(this.physicalPort);
        this.f66b = this.module.getI2cReadCache(this.physicalPort);
        this.module.enableI2cReadMode(this.physicalPort, I2C_ADDRESS, ADDRESS_DISTANCE, 1);
        this.module.enable9v(this.physicalPort, true);
        this.module.setI2cPortActionFlag(this.physicalPort);
        this.module.readI2cCacheFromController(this.physicalPort);
        this.module.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        return String.format("Ultrasonic: %6.1f", new Object[]{Double.valueOf(getUltrasonicLevel())});
    }

    public double getUltrasonicLevel() {
        try {
            this.f65a.lock();
            byte b = this.f66b[MIN_PORT];
            return TypeConversion.unsignedByteToDouble(b);
        } finally {
            this.f65a.unlock();
        }
    }

    public void portIsReady(int port) {
        this.module.setI2cPortActionFlag(this.physicalPort);
        this.module.writeI2cCacheToController(this.physicalPort);
        this.module.readI2cCacheFromController(this.physicalPort);
    }

    public String status() {
        Object[] objArr = new Object[I2C_ADDRESS];
        objArr[0] = this.module.getSerialNumber().toString();
        objArr[1] = Integer.valueOf(this.physicalPort);
        return String.format("NXT Ultrasonic Sensor, connected via device %s, port %d", objArr);
    }

    public String getDeviceName() {
        return "NXT Ultrasonic Sensor";
    }

    public String getConnectionInfo() {
        return this.module.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    private void m50a(int i) {
        if (i < MIN_PORT || i > MAX_PORT) {
            throw new IllegalArgumentException(String.format("Port %d is invalid for " + getDeviceName() + "; valid ports are %d or %d", new Object[]{Integer.valueOf(i), Integer.valueOf(MIN_PORT), Integer.valueOf(MAX_PORT)}));
        }
    }
}
