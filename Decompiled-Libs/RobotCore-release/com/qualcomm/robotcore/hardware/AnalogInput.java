package com.qualcomm.robotcore.hardware;

public class AnalogInput implements HardwareDevice {
    private AnalogInputController f224a;
    private int f225b;

    public AnalogInput(AnalogInputController controller, int channel) {
        this.f224a = null;
        this.f225b = -1;
        this.f224a = controller;
        this.f225b = channel;
    }

    public int getValue() {
        return this.f224a.getAnalogInputValue(this.f225b);
    }

    public String getDeviceName() {
        return "Analog Input";
    }

    public String getConnectionInfo() {
        return this.f224a.getConnectionInfo() + "; analog port " + this.f225b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
