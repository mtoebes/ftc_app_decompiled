package com.qualcomm.robotcore.hardware;

public class AnalogInput implements HardwareDevice {
    private AnalogInputController controller;
    private int channel;

    public AnalogInput(AnalogInputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public int getValue() {
        return controller.getAnalogInputValue(channel);
    }

    public String getDeviceName() {
        return "Analog Input";
    }

    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; analog port " + channel;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
