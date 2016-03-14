package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.configuration.XMLConfigurationConstants;
import java.util.concurrent.locks.Lock;

public class I2cDevice implements HardwareDevice {
    private I2cController f244a;
    private int f245b;

    public I2cDevice(I2cController controller, int port) {
        this.f244a = null;
        this.f245b = -1;
        this.f244a = controller;
        this.f245b = port;
    }

    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        this.f244a.enableI2cReadMode(this.f245b, i2cAddress, memAddress, length);
    }

    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        this.f244a.enableI2cWriteMode(this.f245b, i2cAddress, memAddress, length);
    }

    public byte[] getCopyOfReadBuffer() {
        return this.f244a.getCopyOfReadBuffer(this.f245b);
    }

    public byte[] getCopyOfWriteBuffer() {
        return this.f244a.getCopyOfWriteBuffer(this.f245b);
    }

    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        this.f244a.copyBufferIntoWriteBuffer(this.f245b, buffer);
    }

    public void setI2cPortActionFlag() {
        this.f244a.setI2cPortActionFlag(this.f245b);
    }

    public boolean isI2cPortActionFlagSet() {
        return this.f244a.isI2cPortActionFlagSet(this.f245b);
    }

    public void readI2cCacheFromController() {
        this.f244a.readI2cCacheFromController(this.f245b);
    }

    public void writeI2cCacheToController() {
        this.f244a.writeI2cCacheToController(this.f245b);
    }

    public void writeI2cPortFlagOnlyToController() {
        this.f244a.writeI2cPortFlagOnlyToController(this.f245b);
    }

    public boolean isI2cPortInReadMode() {
        return this.f244a.isI2cPortInReadMode(this.f245b);
    }

    public boolean isI2cPortInWriteMode() {
        return this.f244a.isI2cPortInWriteMode(this.f245b);
    }

    public boolean isI2cPortReady() {
        return this.f244a.isI2cPortReady(this.f245b);
    }

    public Lock getI2cReadCacheLock() {
        return this.f244a.getI2cReadCacheLock(this.f245b);
    }

    public Lock getI2cWriteCacheLock() {
        return this.f244a.getI2cWriteCacheLock(this.f245b);
    }

    public byte[] getI2cReadCache() {
        return this.f244a.getI2cReadCache(this.f245b);
    }

    public byte[] getI2cWriteCache() {
        return this.f244a.getI2cWriteCache(this.f245b);
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback) {
        this.f244a.registerForI2cPortReadyCallback(callback, this.f245b);
    }

    public void deregisterForPortReadyCallback() {
        this.f244a.deregisterForPortReadyCallback(this.f245b);
    }

    public String getDeviceName() {
        return XMLConfigurationConstants.I2C_DEVICE;
    }

    public String getConnectionInfo() {
        return this.f244a.getConnectionInfo() + "; port " + this.f245b;
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
