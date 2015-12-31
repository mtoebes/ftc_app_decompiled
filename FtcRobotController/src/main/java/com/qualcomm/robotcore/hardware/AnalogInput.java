package com.qualcomm.robotcore.hardware;

/**
 * Control a single analog device
 */
public class AnalogInput implements HardwareDevice {
    private static final String DEVICE_NAME = "Analog Input";
    private static final int VERSION = 1;

    private AnalogInputController controller;
    private int channel;

    /**
     * Constructor
     *
     * @param controller AnalogInput controller this channel is attached to
     * @param channel    channel on the analog input controller
     */
    public AnalogInput(AnalogInputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    /**
     * Return the current ADC results from the A0-A7 channel input pins.
     *
     * @return current ADC results
     */
    public int getValue() {
        return this.controller.getAnalogInputValue(this.channel);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; analog port " + this.channel;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
