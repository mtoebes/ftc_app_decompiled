package com.qualcomm.robotcore.hardware;

/**
 * Legacy Module for working with NXT devices
 */
public interface LegacyModule extends HardwareDevice, I2cController {
    /**
     * Enable or disable 9V power on a port
     *
     * @param physicalPort physical port number on the device
     * @param enable       true to enable; false to disable
     */
    void enable9v(int physicalPort, boolean enable);

    /**
     * Enable a physical port in analog read mode
     *
     * @param physicalPort physical port number on the device
     */
    void enableAnalogReadMode(int physicalPort);

    /**
     * Read an analog value from a device; only works in analog read mode
     *
     * @param physicalPort physical port number on the device
     * @return byte[] containing the two analog values; low byte first, high byte second
     */
    byte[] readAnalog(int physicalPort);

    /**
     * Set the value of digital line 0 or 1 while in analog mode.
     * <p/>
     * These are port pins 5 and 6.
     *
     * @param physicalPort physical port number on the device
     * @param line         line 0 or 1
     * @param set          true to set; otherwise false
     */
    void setDigitalLine(int physicalPort, int line, boolean set);
}
