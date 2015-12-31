package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

import java.util.concurrent.locks.Lock;

/**
 * Control a single I2C Device
 */
public class I2cDevice implements HardwareDevice {
    private static final String DEVICE_NAME = "I2cDevice";
    private static final int VERSION = 1;

    private I2cController controller;
    private int port;

    /**
     * Constructor
     *
     * @param controller I2C controller this channel is attached to
     * @param port       port on the I2C controller
     */
    public I2cDevice(I2cController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    /**
     * Enable read mode for this I2C device
     *
     * @param i2cAddress
     * @param memAddress mem address at which to start reading
     * @param length     number of bytes to read
     */
    public void enableI2cReadMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cReadMode(this.port, i2cAddress, memAddress, length);
    }

    /**
     * Enable write mode for this I2C device
     *
     * @param i2cAddress
     * @param memAddress mem address at which to start reading
     * @param length     number of bytes to write
     */
    public void enableI2cWriteMode(int i2cAddress, int memAddress, int length) {
        this.controller.enableI2cWriteMode(this.port, i2cAddress, memAddress, length);
    }

    /**
     * Get a copy of the most recent data read in from the device
     *
     * @return a copy of the most recent data read in from the device
     */
    public byte[] getCopyOfReadBuffer() {
        return this.controller.getCopyOfReadBuffer(this.port);
    }

    /**
     * Get a copy of the data that is set to be written out to the device
     *
     * @return a copy of the data set to be written out to the device
     */
    public byte[] getCopyOfWriteBuffer() {
        return this.controller.getCopyOfWriteBuffer(this.port);
    }

    /**
     * Copy a byte array into the buffer that is set to be written out to the device
     *
     * @param buffer buffer to copy
     */
    public void copyBufferIntoWriteBuffer(byte[] buffer) {
        this.controller.copyBufferIntoWriteBuffer(this.port, buffer);
    }

    /**
     * Set the port action flag; this flag tells the controller to send the current data in its buffer to the I2C device
     */
    public void setI2cPortActionFlag() {
        this.controller.setI2cPortActionFlag(this.port);
    }

    /**
     * Check whether or not the action flag is set for this I2C port
     *
     * @return a boolean indicating whether or not the flag is set
     */
    public boolean isI2cPortActionFlagSet() {
        return this.controller.isI2cPortActionFlagSet(this.port);
    }

    /**
     * Trigger a read of the I2C cache
     */
    public void readI2cCacheFromController() {
        this.controller.readI2cCacheFromController(this.port);
    }

    /**
     * Trigger a write of the I2C cache
     */
    public void writeI2cCacheToController() {
        this.controller.writeI2cCacheToController(this.port);
    }

    /**
     * Write only the action flag
     */
    public void writeI2cPortFlagOnlyToController() {
        this.controller.writeI2cPortFlagOnlyToController(this.port);
    }

    /**
     * Query whether or not the port is in Read mode
     *
     * @return whether or not this port is in read mode
     */
    public boolean isI2cPortInReadMode() {
        return this.controller.isI2cPortInReadMode(this.port);
    }

    /**
     * Query whether or not this port is in write mode
     *
     * @return whether or not this port is in write mode
     */
    public boolean isI2cPortInWriteMode() {
        return this.controller.isI2cPortInWriteMode(this.port);
    }

    /**
     * Query whether or not this I2c port is ready
     *
     * @return boolean indicating I2c port readiness
     */
    public boolean isI2cPortReady() {
        return this.controller.isI2cPortReady(this.port);
    }

    /**
     * Get access to the read cache lock.
     * <p/>
     * This is needed if you are accessing the read cache directly. The read cache lock needs to be acquired before attempting to interact with the read cache
     *
     * @return the read cache lock
     */
    public Lock getI2cReadCacheLock() {
        return this.controller.getI2cReadCacheLock(this.port);
    }

    /**
     * Get access to the write cache lock.
     * <p/>
     * This is needed if you ace accessing the write cache directly. The write cache lock needs to be acquired before attempting to interact with the write cache
     *
     * @return write cache lock
     */
    public Lock getI2cWriteCacheLock() {
        return this.controller.getI2cWriteCacheLock(this.port);
    }

    /**
     * Get direct access to the read cache used by this I2C device
     * <p/>
     * Please lock the cache before accessing it.
     *
     * @return the read cache
     */
    public byte[] getI2cReadCache() {
        return this.controller.getI2cReadCache(this.port);
    }

    /**
     * Get direct access to the write cache used by this I2C device
     * <p/>
     * Please lock the cache before accessing it.
     *
     * @return the write cache
     */
    public byte[] getI2cWriteCache() {
        return this.controller.getI2cWriteCache(this.port);
    }

    /**
     * The method used to register for a port-ready callback
     *
     * @param callback pass in the I2C callback that will be called when the device is ready
     */
    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback) {
        this.controller.registerForI2cPortReadyCallback(callback, this.port);
    }

    /**
     * Unregister for a port-ready callback
     */
    public void deregisterForPortReadyCallback() {
        this.controller.deregisterForPortReadyCallback(this.port);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.port;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    /**
     * Deprecated, use readI2cCacheFromController()
     */
    @Deprecated
    public void readI2cCacheFromModule() {
        readI2cCacheFromController();
    }

    /**
     * Deprecated, use writeI2cCacheToController()
     */
    @Deprecated
    public void writeI2cCacheToModule() {
        writeI2cCacheToController();
    }

    /**
     * Deprecated, use writeI2cPortFlagOnlyToController()
     */
    @Deprecated
    public void writeI2cPortFlagOnlyToModule() {
        writeI2cPortFlagOnlyToController();
    }
}
