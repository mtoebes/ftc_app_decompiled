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
    public static final int DEFAULT_I2C_ADDRESS = 60;
    public static final int VERSION = 1;

    public static final int BUFFER_LENGTH = 6;
    public static final int START_ADDRESS = 3;

    public static final int COMMAND_ACTIVE_LED = 0;
    public static final int COMMAND_PASSIVE_LED = 1;

    public static final int OFFSET_COMMAND = 4;
    public static final int OFFSET_COLOR_NUMBER = 5;
    public static final int OFFSET_RED_READING = 6;
    public static final int OFFSET_GREEN_READING = 7;
    public static final int OFFSET_BLUE_READING = 8;
    public static final int OFFSET_ALPHA_READING = 9;

    public volatile int i2cAddress = DEFAULT_I2C_ADDRESS;
    private SensorMode mode = SensorMode.READING_ONLY;
    private volatile int command = COMMAND_ACTIVE_LED;;

    private final DeviceInterfaceModule deviceInterfaceModule;
    private final int physicalPort;

    private final byte[] readCache;
    private final Lock readCacheLock;
    private final byte[] writeCache;
    private final Lock writeCacheLock;

    private enum SensorMode {
        READING_ONLY,
        PERFORMING_WRITE,
        SWITCHING_TO_READ
    }

    ModernRoboticsI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.deviceInterfaceModule = deviceInterfaceModule;
        this.physicalPort = physicalPort;
        this.readCache = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.readCacheLock = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        this.writeCache = deviceInterfaceModule.getI2cWriteCache(physicalPort);
        this.writeCacheLock = deviceInterfaceModule.getI2cWriteCacheLock(physicalPort);

        deviceInterfaceModule.enableI2cReadMode(physicalPort, this.i2cAddress, START_ADDRESS, BUFFER_LENGTH);
        deviceInterfaceModule.setI2cPortActionFlag(physicalPort);
        deviceInterfaceModule.writeI2cCacheToController(physicalPort);
        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public int red() {
        return readCacheAt(OFFSET_RED_READING);
    }

    public int green() {
        return readCacheAt(OFFSET_GREEN_READING);
    }

    public int blue() {
        return readCacheAt(OFFSET_BLUE_READING);
    }

    public int alpha() {
        return readCacheAt(OFFSET_ALPHA_READING);
    }

    public int argb() {
        return Color.argb(alpha(), red(), green(), blue());
    }

    public void enableLed(boolean enable) {
        int newCommand = enable ? COMMAND_ACTIVE_LED : COMMAND_PASSIVE_LED;

        if (this.command != newCommand) {
            this.command = newCommand;
            this.mode = SensorMode.PERFORMING_WRITE;
            try {
                this.writeCacheLock.lock();
                this.writeCache[OFFSET_COMMAND] = (byte) newCommand;
            } finally {
                this.writeCacheLock.unlock();
            }
        }
    }

    private int readCacheAt(int index) {
        byte b = 0;
        try {
            this.readCacheLock.lock();
            b = this.readCache[index];
        } finally {
            this.readCacheLock.unlock();
        }
        return TypeConversion.unsignedByteToInt(b);
    }

    public String getDeviceName() {
        return "Modern Robotics I2C Color Sensor";
    }

    public String getConnectionInfo() {
        return String.format("%s; I2C port %d", this.deviceInterfaceModule.getConnectionInfo(), this.physicalPort);
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    public void portIsReady(int port) {
        this.deviceInterfaceModule.setI2cPortActionFlag(this.physicalPort);
        this.deviceInterfaceModule.readI2cCacheFromController(this.physicalPort);
        if (this.mode == SensorMode.PERFORMING_WRITE) {
            this.deviceInterfaceModule.enableI2cWriteMode(this.physicalPort, this.i2cAddress, START_ADDRESS, BUFFER_LENGTH);
            this.deviceInterfaceModule.writeI2cCacheToController(this.physicalPort);
            this.mode = SensorMode.SWITCHING_TO_READ;
        } else if (this.mode == SensorMode.SWITCHING_TO_READ) {
            this.deviceInterfaceModule.enableI2cReadMode(this.physicalPort, this.i2cAddress, START_ADDRESS, BUFFER_LENGTH);
            this.deviceInterfaceModule.writeI2cCacheToController(this.physicalPort);
            this.mode = SensorMode.READING_ONLY;
        } else {
            this.deviceInterfaceModule.writeI2cPortFlagOnlyToController(this.physicalPort);
        }
    }

    public void setI2cAddress(int newAddress) {
        IrSeekerSensor.throwIfModernRoboticsI2cAddressIsInvalid(newAddress);
        RobotLog.i(String.format("%s, just changed the I2C address. Original address: %d, new address: %d", getDeviceName(), this.i2cAddress, newAddress));
        this.i2cAddress = newAddress;
        this.deviceInterfaceModule.enableI2cReadMode(this.physicalPort, this.i2cAddress, START_ADDRESS, BUFFER_LENGTH);
        this.deviceInterfaceModule.setI2cPortActionFlag(this.physicalPort);
        this.deviceInterfaceModule.writeI2cCacheToController(this.physicalPort);
        this.deviceInterfaceModule.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public int getI2cAddress() {
        return this.i2cAddress;
    }
}
