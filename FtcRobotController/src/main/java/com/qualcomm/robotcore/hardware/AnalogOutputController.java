package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Interface for working with Analog Controllers
 * <p/>
 * Different analog input controllers will implement this interface.
 */
public interface AnalogOutputController extends HardwareDevice {
    /**
     * Get the USB serial number of this device
     *
     * @return Serial Number
     */
    SerialNumber getSerialNumber();

    /**
     * Sets the channel output frequency in the range 1-5,000 Hz in mode 1, 2 or 3. If mode 0 is selected, this field will be over-written to 0.
     *
     * @param port channel 1 or 0
     * @param freq output frequency in the range1-5,000Hz
     */
    void setAnalogOutputFrequency(int port, int freq);

    /**
     * Sets the channel operating mode. Mode 0: Voltage output. Range: -4V - 4V Mode 1: Sine wave output. Range: 0 - 8V Mode 2: Square wave output. Range: 0 - 8V Mode 3: Triangle wave output. Range: 0 - 8V
     *
     * @param port 0 or 1
     * @param mode voltage, sine, square, or triangle
     */
    void setAnalogOutputMode(int port, byte mode);

    /**
     * Sets the channel output voltage. If mode == 0: takes input from -1023-1023, output in the range -4 to +4 volts. If mode == 1, 2, or 3: takes input from 0-1023, output in the range 0 to 8 volts.
     *
     * @param port    channel 0 or 1
     * @param voltage voltage value in the correct range.
     */
    void setAnalogOutputVoltage(int port, int voltage);
}
