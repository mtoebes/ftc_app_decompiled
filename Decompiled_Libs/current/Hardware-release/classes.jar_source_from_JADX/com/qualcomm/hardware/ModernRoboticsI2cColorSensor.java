package com.qualcomm.hardware;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cColorSensor extends ColorSensor implements I2cPortReadyCallback {
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
    private final DeviceInterfaceModule f127a;
    private final byte[] f128b;
    private final Lock f129c;
    private final byte[] f130d;
    private final Lock f131e;
    private C0011a f132f;
    private volatile int f133g;
    private final int f134h;

    /* renamed from: com.qualcomm.hardware.ModernRoboticsI2cColorSensor.a */
    private enum C0011a {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    ModernRoboticsI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.I2C_ADDRESS = 60;
        this.f132f = C0011a.READING_ONLY;
        this.f133g = COMMAND_ACTIVE_LED;
        this.f127a = deviceInterfaceModule;
        this.f134h = physicalPort;
        this.f128b = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.f129c = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        this.f130d = deviceInterfaceModule.getI2cWriteCache(physicalPort);
        this.f131e = deviceInterfaceModule.getI2cWriteCacheLock(physicalPort);
        deviceInterfaceModule.enableI2cReadMode(physicalPort, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
        deviceInterfaceModule.setI2cPortActionFlag(physicalPort);
        deviceInterfaceModule.writeI2cCacheToController(physicalPort);
        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return m51a(OFFSET_RED_READING);
    }

    public int green() {
        return m51a(OFFSET_GREEN_READING);
    }

    public int blue() {
        return m51a(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return m51a(OFFSET_ALPHA_VALUE);
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public void enableLed(boolean enable) {
        byte b = (byte) 1;
        if (enable) {
            b = (byte) 0;
        }
        if (this.f133g != b) {
            this.f133g = b;
            this.f132f = C0011a.PERFORMING_WRITE;
            try {
                this.f131e.lock();
                this.f130d[OFFSET_COMMAND] = b;
            } finally {
                this.f131e.unlock();
            }
        }
    }

    private int m51a(int i) {
        try {
            this.f129c.lock();
            byte b = this.f128b[i];
            return TypeConversion.unsignedByteToInt(b);
        } finally {
            this.f129c.unlock();
        }
    }

    public String getDeviceName() {
        return "Modern Robotics I2C Color Sensor";
    }

    public String getConnectionInfo() {
        return this.f127a.getConnectionInfo() + "; I2C port: " + this.f134h;
    }

    public int getVersion() {
        return COMMAND_PASSIVE_LED;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        this.f127a.setI2cPortActionFlag(this.f134h);
        this.f127a.readI2cCacheFromController(this.f134h);
        if (this.f132f == C0011a.PERFORMING_WRITE) {
            this.f127a.enableI2cWriteMode(this.f134h, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
            this.f127a.writeI2cCacheToController(this.f134h);
            this.f132f = C0011a.SWITCHING_TO_READ;
        } else if (this.f132f == C0011a.SWITCHING_TO_READ) {
            this.f127a.enableI2cReadMode(this.f134h, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
            this.f127a.writeI2cCacheToController(this.f134h);
            this.f132f = C0011a.READING_ONLY;
        } else {
            this.f127a.writeI2cPortFlagOnlyToController(this.f134h);
        }
    }

    public void setI2cAddress(int newAddress) {
        IrSeekerSensor.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(getDeviceName() + ", just changed I2C address. Original address: " + this.I2C_ADDRESS + ", new address: " + newAddress);
        this.I2C_ADDRESS = newAddress;
        this.f127a.enableI2cReadMode(this.f134h, this.I2C_ADDRESS, ADDRESS_COMMAND, OFFSET_RED_READING);
        this.f127a.setI2cPortActionFlag(this.f134h);
        this.f127a.writeI2cCacheToController(this.f134h);
        this.f127a.registerForI2cPortReadyCallback(this, this.f134h);
    }

    public int getI2cAddress() {
        return this.I2C_ADDRESS;
    }
}
