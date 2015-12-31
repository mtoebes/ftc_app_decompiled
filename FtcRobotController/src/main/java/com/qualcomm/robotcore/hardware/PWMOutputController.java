package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

/**
 * Interface for working with PWM Input Controllers
 * <p/>
 * Different analog input controllers will implement this interface.
 */
public interface PWMOutputController extends HardwareDevice {
    /**
     * Gets the pulse width for the channel output in units of 1 microsecond.
     *
     * @param port port this device is attached to
     * @return time pulse width for the channel in microseconds.
     */
    int getPulseWidthOutputTime(int port);

    /**
     * Gets the pulse repetition period for the channel output in units of 1 microsecond.
     *
     * @param port port this device is attached to
     * @return period pulse repetition period in microseconds.
     */
    int getPulseWidthPeriod(int port);

    /**
     * Get the USB serial number of this device
     *
     * @return serial number
     */
    SerialNumber getSerialNumber();

    /**
     * Set the pulse width output time for this channel. Typically set to a value between 750 and 2,250 to control a servo.
     *
     * @param port port this device is attached to
     * @param time pulse width for the port in microseconds.
     */
    void setPulseWidthOutputTime(int port, int time);

    /**
     * Set the pulse width output period. Typically set to 20,000 to control servo.
     *
     * @param port   port this device is attached to
     * @param period pulse repetition period in microseconds.
     */
    void setPulseWidthPeriod(int port, int period);
}
