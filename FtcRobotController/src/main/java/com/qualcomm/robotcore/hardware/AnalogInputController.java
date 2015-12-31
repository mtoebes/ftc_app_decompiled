package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Interface for working with Analog Controllers
 * <p/>
 * Different analog input controllers will implement this interface.
 */
public interface AnalogInputController extends HardwareDevice {
    /**
     * Get the value of this analog input Return the current ADC results from the A0-A7 channel input pins.
     *
     * @param channel which analog channel to read
     * @return current ADC results
     */
    int getAnalogInputValue(int channel);

    /**
     * Serial Number
     *
     * @return return the USB serial number of this device
     */
    SerialNumber getSerialNumber();
}
