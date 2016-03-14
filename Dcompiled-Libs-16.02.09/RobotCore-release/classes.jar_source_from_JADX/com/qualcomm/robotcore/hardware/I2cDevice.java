package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyBeginEndNotifications;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants;
import java.util.concurrent.locks.Lock;

public class I2cDevice extends I2cControllerPortDeviceImpl implements HardwareDevice {
    public I2cDevice(I2cController controller, int port) {
        super(controller, port);
    }

    protected void controllerNowArmedOrPretending() {
    }

    public I2cController getController() {
        return this.controller;
    }

    public int getPort() {
        return this.physicalPort;
    }

    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cReadMode(this.physicalPort, i2cAddress, memAddress, length);
    }

    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cWriteMode(this.physicalPort, i2cAddress, memAddress, length);
    }

    public byte[] getCopyOfReadBuffer() {
        return this.controller.getCopyOfReadBuffer(this.physicalPort);
    }

    public byte[] getCopyOfWriteBuffer() {
        return this.controller.getCopyOfWriteBuffer(this.physicalPort);
    }

    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        this.controller.copyBufferIntoWriteBuffer(this.physicalPort, buffer);
    }

    public void setI2cPortActionFlag() {
        this.controller.setI2cPortActionFlag(this.physicalPort);
    }

    public boolean isI2cPortActionFlagSet() {
        return this.controller.isI2cPortActionFlagSet(this.physicalPort);
    }

    public void readI2cCacheFromController() {
        this.controller.readI2cCacheFromController(this.physicalPort);
    }

    public void writeI2cCacheToController() {
        this.controller.writeI2cCacheToController(this.physicalPort);
    }

    public void writeI2cPortFlagOnlyToController() {
        this.controller.writeI2cPortFlagOnlyToController(this.physicalPort);
    }

    public boolean isI2cPortInReadMode() {
        return this.controller.isI2cPortInReadMode(this.physicalPort);
    }

    public boolean isI2cPortInWriteMode() {
        return this.controller.isI2cPortInWriteMode(this.physicalPort);
    }

    public boolean isI2cPortReady() {
        return this.controller.isI2cPortReady(this.physicalPort);
    }

    public Lock getI2cReadCacheLock() {
        return this.controller.getI2cReadCacheLock(this.physicalPort);
    }

    public Lock getI2cWriteCacheLock() {
        return this.controller.getI2cWriteCacheLock(this.physicalPort);
    }

    public byte[] getI2cReadCache() {
        return this.controller.getI2cReadCache(this.physicalPort);
    }

    public byte[] getI2cWriteCache() {
        return this.controller.getI2cWriteCache(this.physicalPort);
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback) {
        this.controller.registerForI2cPortReadyCallback(callback, this.physicalPort);
    }

    public I2cPortReadyCallback getI2cPortReadyCallback() {
        return this.controller.getI2cPortReadyCallback(this.physicalPort);
    }

    public void deregisterForPortReadyCallback() {
        this.controller.deregisterForPortReadyCallback(this.physicalPort);
    }

    public void registerForPortReadyBeginEndCallback(I2cPortReadyBeginEndNotifications callback) {
        this.controller.registerForPortReadyBeginEndCallback(callback, this.physicalPort);
    }

    public I2cPortReadyBeginEndNotifications getPortReadyBeginEndCallback() {
        return this.controller.getPortReadyBeginEndCallback(this.physicalPort);
    }

    public void deregisterForPortReadyBeginEndCallback() {
        this.controller.deregisterForPortReadyBeginEndCallback(this.physicalPort);
    }

    public String getDeviceName() {
        return XMLConfigurationConstants.I2C_DEVICE;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.physicalPort;
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
