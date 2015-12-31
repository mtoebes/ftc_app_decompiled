package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

import java.util.concurrent.locks.Lock;

/**
 * Interface for working with Digital Channel Controllers
 * <p/>
 * Different digital channel controllers will implement this interface.
 */
public interface I2cController extends HardwareDevice {
    byte I2C_BUFFER_START_ADDRESS = 4;

    interface I2cPortReadyCallback {
        void portIsReady(int port);
    }

    /**
     * Copy a byte array into the buffer that is set to be written out to the device
     *
     * @param physicalPort the port the device is attached to
     * @param buffer       buffer to copy
     */
    void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer);

    /**
     * De-register for port ready notifications.
     *
     * @param port port to no longer being monitored.
     */
    void deregisterForPortReadyCallback(int port);

    /**
     * Enable read mode for a particular I2C device
     *
     * @param physicalPort the port the device is attached to
     * @param i2cAddress   the i2c address of the device
     * @param memAddress   mem address at which to start reading
     * @param length       number of bytes to read
     */
    void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length);

    /**
     * Enable write mode for a particular I2C device
     *
     * @param physicalPort the port the device is attached to
     * @param i2cAddress   the i2c address of the device
     * @param memAddress   mem address at which to start writing
     * @param length       number of bytes to write
     */
    void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length);

    /**
     * Get a copy of the most recent data read in from the device
     *
     * @param physicalPort the port the device is attached to
     * @return a copy of the most recent data read in from the device
     */
    byte[] getCopyOfReadBuffer(int physicalPort);

    /**
     * Get a copy of the data that is set to be written out to the device
     *
     * @param physicalPort the port the device is attached to
     * @return a copy of the data set to be written out to the device
     */
    byte[] getCopyOfWriteBuffer(int physicalPort);

    /**
     * Get direct access to the cache that I2C reads will be populated into
     * <p/>
     * Please lock the cache before accessing it.
     *
     * @param port physical port number on the device
     * @return byte array
     */
    byte[] getI2cReadCache(int port);

    /**
     * Get access to the read cache lock.
     * <p/>
     * This is needed if you are accessing the read cache directly. The read cache lock needs to be acquired before attempting to interact with the read cache
     *
     * @param port physical port number on the device
     * @return lock
     */
    Lock getI2cReadCacheLock(int port);

    /**
     * Get direct access to the cache that I2C writes will be populated into
     * <p/>
     * Please lock the cache before accessing it.
     *
     * @param port physical port number on the device
     * @return byte array
     */
    byte[] getI2cWriteCache(int port);

    /**
     * Get access to the write cache lock.
     * <p/>
     * This is needed if you ace accessing the write cache directly. The write cache lock needs to be acquired before attempting to interact with the write cache
     *
     * @param port physical port number on the device
     * @return lock
     */
    Lock getI2cWriteCacheLock(int port);

    /**
     * Get the USB serial number of this device
     *
     * @return serial number
     */
    SerialNumber getSerialNumber();

    /**
     * Get the port action flag; this flag is set if the particular port is busy.
     *
     * @param port physical port number on the device
     * @return true if port is busy; otherwise false
     */
    boolean isI2cPortActionFlagSet(int port);

    /**
     * Is the port in read mode?
     *
     * @param port physical port number on the device
     * @return true if in read mode; otherwise false
     */
    boolean isI2cPortInReadMode(int port);

    /**
     * Is the port in write mode?
     *
     * @param port physical port number on the device
     * @return true if in write mode; otherwise false
     */
    boolean isI2cPortInWriteMode(int port);

    /**
     * Determine if a physical port is ready
     *
     * @param port physical port number on the device
     * @return true if ready for command; false otherwise
     */
    boolean isI2cPortReady(int port);

    /**
     * Read the local cache in from the I2C Controller NOTE: unless this method is called the internal cache isn't updated
     *
     * @param port physical port number on the device
     */
    void readI2cCacheFromController(int port);

    /**
     * Deprecated, use readI2cCacheFromController(port)
     *
     * @param port physical port number on the device
     */
    @Deprecated
    void readI2cCacheFromModule(int port);

    /**
     * Register to be notified when a given I2C port is ready. The callback method will be called after the latest data has been read from the I2C Controller. Only one callback can be registered for a given port. Last to register wins.
     *
     * @param callback register a callback
     * @param port     port to be monitored
     */
    void registerForI2cPortReadyCallback(I2cPortReadyCallback callback, int port);

    /**
     * Set the port action flag; this flag tells the controller to send the current data in its buffer to the I2C device
     *
     * @param port physical port number on the device
     */
    void setI2cPortActionFlag(int port);

    /**
     * Write the local cache to the I2C Controller NOTE: unless this method is called the internal cache isn't updated
     *
     * @param port physical port number on the device
     */
    void writeI2cCacheToController(int port);

    /**
     * Deprecated, use writeI2cCacheToController(port)
     *
     * @param port physical port number on the device
     */
    @Deprecated
    void writeI2cCacheToModule(int port);

    /**
     * Write just the port action flag in the local cache to the I2C controller
     *
     * @param port physical port number on the device
     */
    void writeI2cPortFlagOnlyToController(int port);

    /**
     * Deprecated, use writeI2cPortFlagOnlyToController(port)
     *
     * @param port physical port number on the device
     */
    @Deprecated
    void writeI2cPortFlagOnlyToModule(int port);
}
