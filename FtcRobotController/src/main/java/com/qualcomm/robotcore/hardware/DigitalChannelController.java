package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Interface for working with Digital Channel Controllers
 * <p/>
 * Different digital channel controllers will implement this interface.
 */
public interface DigitalChannelController extends HardwareDevice {

    /**
     * Digital channel mode - input or output
     */
    enum Mode {
        INPUT,
        OUTPUT
    }

    /**
     * Get the state of a digital channel If it's in OUTPUT mode, this will return the output bit. If the channel is in INPUT mode, this will return the input bit.
     *
     * @param channel channel
     * @return true if set; otherwise false
     */
    Mode getDigitalChannelMode(int channel);

    /**
     * Get the mode of a digital channel
     *
     * @param channel channel
     * @return INPUT or OUTPUT
     */
    boolean getDigitalChannelState(int channel);

    /**
     * Get the USB serial number of this device
     *
     * @return serial number
     */
    SerialNumber getSerialNumber();

    /**
     * Set the mode of a digital channel
     *
     * @param channel channel
     * @param mode    INPUT or OUTPUT
     */
    void setDigitalChannelMode(int channel, Mode mode);

    /**
     * Set the state of a digital channel
     * <p/>
     * The behavior of this method is undefined for digital channels in INPUT mode.
     *
     * @param channel channel
     * @param state   true to set; false to unset
     */
    void setDigitalChannelState(int channel, boolean state);
}
