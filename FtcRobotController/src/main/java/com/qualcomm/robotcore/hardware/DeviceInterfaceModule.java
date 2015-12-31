package com.qualcomm.robotcore.hardware;

/**
 * DeviceInterfaceModule for working with various devices
 */
public interface DeviceInterfaceModule extends AnalogInputController, AnalogOutputController, DigitalChannelController, I2cController, PWMOutputController {
    /**
     * Get the digital IO control byte
     *
     * @return control byte
     */

    byte getDigitalIOControlByte();

    /**
     * A byte containing the current logic levels present in the D7-D0 channel pins. If a particular pin is in output mode, the current output state will be reported.
     *
     * @return input state byte
     */
    int getDigitalInputStateByte();

    /**
     * The D7-D0 output set field is a byte containing the required I/O output of the D7-D0 channel pins. If the corresponding Dy-D0 I/O control field bit is set to one, the channel pin will be in output mode and will reflect the value of the corresponding D7-D0 output set field bit.
     *
     * @return D7-D0 output set field.
     */
    byte getDigitalOutputStateByte();

    /**
     * Indicates whether the LED on the given channel is on or not
     *
     * @param channel int indicating the ID of the LED.
     * @return true for ON, false for OFF
     */
    boolean getLEDState(int channel);

    /**
     * If a particular bit is set to one, the corresponding channel pin will be in output mode. Else it will be in input mode.
     *
     * @param input the desired setting for each channel pin.
     */
    void setDigitalIOControlByte(byte input);

    /**
     * If a a particular control field bit is set to one, the channel pin will be in output mode and will reflect the value of the corresponding field bit.
     *
     * @param input with output state of the digital pins.
     */
    void setDigitalOutputByte(byte input);

    /**
     * Turn on or off a particular LED
     *
     * @param channel int indicating the ID of the LED.
     * @param state   true for ON, false for OFF
     */
    void setLED(int channel, boolean state);
}
