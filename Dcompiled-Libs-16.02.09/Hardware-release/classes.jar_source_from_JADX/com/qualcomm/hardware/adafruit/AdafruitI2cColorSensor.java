package com.qualcomm.hardware.adafruit;

import android.graphics.Color;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import java.util.concurrent.locks.Lock;

public class AdafruitI2cColorSensor extends I2cControllerPortDeviceImpl implements ColorSensor, I2cPortReadyCallback {
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
    private byte[] f19a;
    private Lock f20b;
    private byte[] f21c;
    private Lock f22d;
    private boolean f23e;
    private boolean f24f;

    public AdafruitI2cColorSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f23e = false;
        this.f24f = false;
        this.f23e = true;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f19a = this.controller.getI2cReadCache(this.physicalPort);
        this.f20b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f21c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f22d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        Object[] objArr = new Object[TCS34725_ENABLE_PON];
        objArr[ADDRESS_TCS34725_ENABLE] = Integer.valueOf(argb());
        return String.format("argb: %d", objArr);
    }

    public int red() {
        return m35a(OFFSET_RED_HIGH_BYTE, OFFSET_RED_LOW_BYTE);
    }

    public int green() {
        return m35a(OFFSET_GREEN_HIGH_BYTE, OFFSET_GREEN_LOW_BYTE);
    }

    public int blue() {
        return m35a(OFFSET_BLUE_HIGH_BYTE, OFFSET_BLUE_LOW_BYTE);
    }

    public int alpha() {
        return m35a(OFFSET_ALPHA_HIGH_BYTE, OFFSET_ALPHA_LOW_BYTE);
    }

    private int m35a(int i, int i2) {
        try {
            this.f20b.lock();
            int i3 = (this.f19a[i] << OFFSET_GREEN_LOW_BYTE) | (this.f19a[i2] & ModernRoboticsUsbServoController.SERVO_POSITION_MAX);
            return i3;
        } finally {
            this.f20b.unlock();
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
        return this.controller.getConnectionInfo() + "; I2C port: " + this.physicalPort;
    }

    public int getVersion() {
        return TCS34725_ENABLE_PON;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        if (this.f23e) {
            m37b();
            this.f23e = false;
            this.f24f = true;
        } else if (this.f24f) {
            m36a();
            this.f24f = false;
        }
        this.controller.readI2cCacheFromController(this.physicalPort);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    private void m36a() {
        this.controller.enableI2cReadMode(this.physicalPort, I2C_ADDRESS_TCS34725, 148, OFFSET_GREEN_LOW_BYTE);
        this.controller.writeI2cCacheToController(this.physicalPort);
    }

    private void m37b() {
        this.controller.enableI2cWriteMode(this.physicalPort, I2C_ADDRESS_TCS34725, TCS34725_COMMAND_BIT, TCS34725_ENABLE_PON);
        try {
            this.f22d.lock();
            this.f21c[OFFSET_ALPHA_LOW_BYTE] = (byte) 3;
            this.controller.setI2cPortActionFlag(this.physicalPort);
            this.controller.writeI2cCacheToController(this.physicalPort);
        } finally {
            this.f22d.unlock();
        }
    }

    public void setI2cAddress(int newAddress) {
        throw new UnsupportedOperationException("setI2cAddress is not supported.");
    }

    public int getI2cAddress() {
        throw new UnsupportedOperationException("getI2cAddress is not supported.");
    }
}
