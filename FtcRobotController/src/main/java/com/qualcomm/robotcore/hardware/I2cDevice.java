package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants;
import java.util.concurrent.locks.Lock;

public class I2cDevice implements HardwareDevice {
    private I2cController controller;
    private int port;

    public I2cDevice(I2cController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cReadMode(this.port, i2cAddress, memAddress, length);
    }

    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cWriteMode(this.port, i2cAddress, memAddress, length);
    }

    public byte[] getCopyOfReadBuffer() {
        return this.controller.getCopyOfReadBuffer(this.port);
    }

    public byte[] getCopyOfWriteBuffer() {
        return this.controller.getCopyOfWriteBuffer(this.port);
    }

    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        this.controller.copyBufferIntoWriteBuffer(this.port, buffer);
    }

    public void setI2cPortActionFlag() {
        this.controller.setI2cPortActionFlag(this.port);
    }

    public boolean isI2cPortActionFlagSet() {
        return this.controller.isI2cPortActionFlagSet(this.port);
    }

    public void readI2cCacheFromController() {
        this.controller.readI2cCacheFromController(this.port);
    }

    public void writeI2cCacheToController() {
        this.controller.writeI2cCacheToController(this.port);
    }

    public void writeI2cPortFlagOnlyToController() {
        this.controller.writeI2cPortFlagOnlyToController(this.port);
    }

    public boolean isI2cPortInReadMode() {
        return this.controller.isI2cPortInReadMode(this.port);
    }

    public boolean isI2cPortInWriteMode() {
        return this.controller.isI2cPortInWriteMode(this.port);
    }

    public boolean isI2cPortReady() {
        return this.controller.isI2cPortReady(this.port);
    }

    public Lock getI2cReadCacheLock() {
        return this.controller.getI2cReadCacheLock(this.port);
    }

    public Lock getI2cWriteCacheLock() {
        return this.controller.getI2cWriteCacheLock(this.port);
    }

    public byte[] getI2cReadCache() {
        return this.controller.getI2cReadCache(this.port);
    }

    public byte[] getI2cWriteCache() {
        return this.controller.getI2cWriteCache(this.port);
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback) {
        this.controller.registerForI2cPortReadyCallback(callback, this.port);
    }

    public void deregisterForPortReadyCallback() {
        this.controller.deregisterForPortReadyCallback(this.port);
    }

    public String getDeviceName() {
        return XMLConfigurationConstants.I2C_DEVICE;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.port;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    @Deprecated
    public void readI2cCacheFromModule() {
        readI2cCacheFromController();
    }

    @Deprecated
    public void writeI2cCacheToModule() {
        writeI2cCacheToController();
    }

    @Deprecated
    public void writeI2cPortFlagOnlyToModule() {
        writeI2cPortFlagOnlyToController();
    }
}
