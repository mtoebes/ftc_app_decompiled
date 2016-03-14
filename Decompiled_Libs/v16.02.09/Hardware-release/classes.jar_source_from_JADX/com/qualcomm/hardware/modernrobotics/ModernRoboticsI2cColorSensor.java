package com.qualcomm.hardware.modernrobotics;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cColorSensor extends I2cControllerPortDeviceImpl implements ColorSensor, I2cPortReadyCallback {
    public static final int ADDRESS_COLOR_NUMBER = 4;
    public static final int ADDRESS_COMMAND = 3;
    public static final int BUFFER_LENGTH = 6;
    public static final int COMMAND_ACTIVE_LED = 0;
    public static final int COMMAND_PASSIVE_LED = 1;
    public static final int OFFSET_ALPHA_VALUE = 9;
    public static final int OFFSET_BLUE_READING = 8;
    public static final int OFFSET_COLOR_NUMBER = 5;
    public static final int OFFSET_COMMAND = 4;
    public static final int OFFSET_GREEN_READING = 7;
    public static final int OFFSET_RED_READING = 6;
    public volatile int I2C_ADDRESS;
    private byte[] f112a;
    private Lock f113b;
    private byte[] f114c;
    private Lock f115d;
    private C0015a f116e;
    private int f117f;

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor.a */
    private enum C0015a {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    public ModernRoboticsI2cColorSensor(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.I2C_ADDRESS = 60;
        this.f116e = C0015a.READING_ONLY;
        this.f117f = COMMAND_ACTIVE_LED;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f112a = this.controller.getI2cReadCache(this.physicalPort);
        this.f113b = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f114c = this.controller.getI2cWriteCache(this.physicalPort);
        this.f115d = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        Object[] objArr = new Object[COMMAND_PASSIVE_LED];
        objArr[COMMAND_ACTIVE_LED] = Integer.valueOf(argb());
        return String.format("argb: %d", objArr);
    }

    public int red() {
        return m54a(OFFSET_RED_READING);
    }

    public int green() {
        return m54a(OFFSET_GREEN_READING);
    }

    public int blue() {
        return m54a(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return m54a(OFFSET_ALPHA_VALUE);
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public synchronized void enableLed(boolean enable) {
        byte b = (byte) 1;
        if (enable) {
            b = (byte) 0;
        }
        if (this.f117f != b) {
            this.f117f = b;
            this.f116e = C0015a.PERFORMING_WRITE;
            try {
                this.f115d.lock();
                this.f114c[OFFSET_COMMAND] = b;
            } finally {
                this.f115d.unlock();
            }
        }
    }

    private int m54a(int i) {
        try {
            this.f113b.lock();
            byte b = this.f112a[i];
            return TypeConversion.unsignedByteToInt(b);
        } finally {
            this.f113b.unlock();
        }
    }

    public String getDeviceName() {
        return "Modern Robotics I2C Color Sensor";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; I2C port: " + this.physicalPort;
    }

    public int getVersion() {
        return COMMAND_PASSIVE_LED;
    }

    public void close() {
    }

    public synchronized void portIsReady(int port) {
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.readI2cCacheFromController(this.physicalPort);
        if (this.f116e == C0015a.PERFORMING_WRITE) {
            this.controller.enableI2cWriteMode(this.physicalPort, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f116e = C0015a.SWITCHING_TO_READ;
        } else if (this.f116e == C0015a.SWITCHING_TO_READ) {
            this.controller.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
            this.controller.writeI2cCacheToController(this.physicalPort);
            this.f116e = C0015a.READING_ONLY;
        } else {
            this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
        }
    }

    public void setI2cAddress(int newAddress) {
        ModernRoboticsUsbDeviceInterfaceModule.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(getDeviceName() + ", just changed I2C address. Original address: " + this.I2C_ADDRESS + ", new address: " + newAddress);
        this.I2C_ADDRESS = newAddress;
        this.controller.enableI2cReadMode(this.physicalPort, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public int getI2cAddress() {
        return this.I2C_ADDRESS;
    }
}
