package com.qualcomm.robotcore.hardware;

public class AnalogInput implements HardwareDevice {
    private static final String DEVICE_NAME = "Analog Input";
    private static final int VERSION = 1;

    private AnalogInputController controller;
    private int channel;

    public AnalogInput(AnalogInputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

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
