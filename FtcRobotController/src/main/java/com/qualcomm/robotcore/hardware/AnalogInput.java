package com.qualcomm.robotcore.hardware;

public class AnalogInput implements HardwareDevice {
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
        return "Analog Input";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; analog port " + this.channel;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
