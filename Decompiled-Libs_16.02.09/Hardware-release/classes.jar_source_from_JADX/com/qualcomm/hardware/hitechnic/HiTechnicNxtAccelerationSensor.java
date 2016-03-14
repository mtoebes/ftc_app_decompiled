package com.qualcomm.hardware.hitechnic;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AccelerationSensor.Acceleration;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import java.util.concurrent.locks.Lock;

public class HiTechnicNxtAccelerationSensor extends I2cControllerPortDeviceImpl implements AccelerationSensor, I2cPortReadyCallback {
    public static final int ACCEL_LENGTH = 6;
    public static final int ADDRESS_ACCEL_START = 66;
    public static final byte I2C_ADDRESS = (byte) 2;
    private byte[] f25a;
    private Lock f26b;

    public HiTechnicNxtAccelerationSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.controller.enableI2cReadMode(this.physicalPort, 2, ADDRESS_ACCEL_START, ACCEL_LENGTH);
        this.f25a = this.controller.getI2cReadCache(this.physicalPort);
        this.f26b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        return getAcceleration().toString();
    }

    public Acceleration getAcceleration() {
        Acceleration acceleration = new Acceleration();
        try {
            this.f26b.lock();
            acceleration.x = m38a((double) this.f25a[4], (double) this.f25a[7]);
            acceleration.y = m38a((double) this.f25a[5], (double) this.f25a[8]);
            acceleration.z = m38a((double) this.f25a[ACCEL_LENGTH], (double) this.f25a[9]);
            return acceleration;
        } finally {
            this.f26b.unlock();
        }
    }

    public String status() {
        return String.format("NXT Acceleration Sensor, connected via device %s, port %d", new Object[]{this.controller.getSerialNumber().toString(), Integer.valueOf(this.physicalPort)});
    }

    private double m38a(double d, double d2) {
        return ((4.0d * d) + d2) / 200.0d;
    }

    public void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
        this.controller.readI2cCacheFromController(this.physicalPort);
    }

    public String getDeviceName() {
        return "NXT Acceleration Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
