package com.qualcomm.robotcore.hardware;

/**
 * Interface used by Hardware Devices
 */
public interface HardwareDevice {
    /**
     * Close this device
     */
    void close();

    /**
     * Get connection information about this device in a human readable format
     *
     * @return connection info
     */
    String getConnectionInfo();

    /**
     * Get device manufacturer and name
     *
     * @return device manufacturer and name
     */
    String getDeviceName();

    /**
     * Get the version of this device
     *
     * @return version
     */
    int getVersion();
}
