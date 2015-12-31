package com.qualcomm.hardware;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import java.util.concurrent.locks.Lock;

public class AdafruitI2cColorSensor extends ColorSensor implements I2cPortReadyCallback {
    public static final int ADDRESS_TCS34725_ENABLE = 0;
    public static final int I2C_ADDRESS_TCS34725 = 82;
    public static final int OFFSET_ALPHA_HIGH_BYTE = 5;
    public static final int OFFSET_ALPHA_LOW_BYTE = 4;
    public static final int OFFSET_BLUE_HIGH_BYTE = 11;
    public static final int OFFSET_BLUE_LOW_BYTE = 10;
    public static final int OFFSET_GREEN_HIGH_BYTE = 9;
    public static final int OFFSET_GREEN_LOW_BYTE = 8;
    public static final int OFFSET_RED_HIGH_BYTE = 7;
    public static final int OFFSET_RED_LOW_BYTE = 6;
    public static final int TCS34725_BDATAL = 26;
    public static final int TCS34725_CDATAL = 20;
    public static final int TCS34725_COMMAND_BIT = 128;
    public static final int TCS34725_ENABLE_AEN = 2;
    public static final int TCS34725_ENABLE_AIEN = 16;
    public static final int TCS34725_ENABLE_PON = 1;
    public static final int TCS34725_GDATAL = 24;
    public static final int TCS34725_ID = 18;
    public static final int TCS34725_RDATAL = 22;
    private final DeviceInterfaceModule f0a;
    private final byte[] f1b;
    private final Lock f2c;
    private final byte[] f3d;
    private final Lock f4e;
    private final int f5f;
    private boolean f6g;
    private boolean f7h;

    public AdafruitI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.f6g = false;
        this.f7h = false;
        this.f5f = physicalPort;
        this.f0a = deviceInterfaceModule;
        this.f1b = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.f2c = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        this.f3d = deviceInterfaceModule.getI2cWriteCache(physicalPort);
        this.f4e = deviceInterfaceModule.getI2cWriteCacheLock(physicalPort);
        this.f6g = true;
        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return m0a(OFFSET_RED_HIGH_BYTE, OFFSET_RED_LOW_BYTE);
    }

    public int green() {
        return m0a(OFFSET_GREEN_HIGH_BYTE, OFFSET_GREEN_LOW_BYTE);
    }

    public int blue() {
        return m0a(OFFSET_BLUE_HIGH_BYTE, OFFSET_BLUE_LOW_BYTE);
    }

    public int alpha() {
        return m0a(OFFSET_ALPHA_HIGH_BYTE, OFFSET_ALPHA_LOW_BYTE);
    }

    private int m0a(int i, int i2) {
        try {
            this.f2c.lock();
            int i3 = (this.f1b[i] << OFFSET_GREEN_LOW_BYTE) | (this.f1b[i2] & ModernRoboticsUsbServoController.SERVO_POSITION_MAX);
            return i3;
        } finally {
            this.f2c.unlock();
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
        return this.f0a.getConnectionInfo() + "; I2C port: " + this.f5f;
    }

    public int getVersion() {
        return TCS34725_ENABLE_PON;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        if (this.f6g) {
            m2b();
            this.f6g = false;
            this.f7h = true;
        } else if (this.f7h) {
            m1a();
            this.f7h = false;
        }
        this.f0a.readI2cCacheFromController(this.f5f);
        this.f0a.setI2cPortActionFlag(this.f5f);
        this.f0a.writeI2cPortFlagOnlyToController(this.f5f);
    }

    private void m1a() {
        this.f0a.enableI2cReadMode(this.f5f, I2C_ADDRESS_TCS34725, 148, OFFSET_GREEN_LOW_BYTE);
        this.f0a.writeI2cCacheToController(this.f5f);
    }

    private void m2b() {
        this.f0a.enableI2cWriteMode(this.f5f, I2C_ADDRESS_TCS34725, TCS34725_COMMAND_BIT, TCS34725_ENABLE_PON);
        try {
            this.f4e.lock();
            this.f3d[OFFSET_ALPHA_LOW_BYTE] = (byte) 3;
            this.f0a.setI2cPortActionFlag(this.f5f);
            this.f0a.writeI2cCacheToController(this.f5f);
        } finally {
            this.f4e.unlock();
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }
}
