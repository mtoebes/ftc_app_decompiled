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
        controller.enableI2cReadMode(port, i2cAddress, memAddress, length);
    }

    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        controller.enableI2cWriteMode(port, i2cAddress, memAddress, length);
    }

    public byte[] getCopyOfReadBuffer() {
        return controller.getCopyOfReadBuffer(port);
    }

    public byte[] getCopyOfWriteBuffer() {
        return controller.getCopyOfWriteBuffer(port);
    }

    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        controller.copyBufferIntoWriteBuffer(port, buffer);
    }

    public void setI2cPortActionFlag() {
        controller.setI2cPortActionFlag(port);
    }

    public boolean isI2cPortActionFlagSet() {
        return controller.isI2cPortActionFlagSet(port);
    }

    public void readI2cCacheFromController() {
        controller.readI2cCacheFromController(port);
    }

    public void writeI2cCacheToController() {
        controller.writeI2cCacheToController(port);
    }

    public void writeI2cPortFlagOnlyToController() {
        controller.writeI2cPortFlagOnlyToController(port);
    }

    public boolean isI2cPortInReadMode() {
        return controller.isI2cPortInReadMode(port);
    }

    public boolean isI2cPortInWriteMode() {
        return controller.isI2cPortInWriteMode(port);
    }

    public boolean isI2cPortReady() {
        return controller.isI2cPortReady(port);
    }

    public Lock getI2cReadCacheLock() {
        return controller.getI2cReadCacheLock(port);
    }

    public Lock getI2cWriteCacheLock() {
        return controller.getI2cWriteCacheLock(port);
    }

    public byte[] getI2cReadCache() {
        return controller.getI2cReadCache(port);
    }

    public byte[] getI2cWriteCache() {
        return controller.getI2cWriteCache(port);
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback) {
        controller.registerForI2cPortReadyCallback(callback, port);
    }

    public void deregisterForPortReadyCallback() {
        controller.deregisterForPortReadyCallback(port);
    }

    public String getDeviceName() {
        return XMLConfigurationConstants.I2C_DEVICE;
    }

    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; port " + port;
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
