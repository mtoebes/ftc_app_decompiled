package com.qualcomm.robotcore.hardware;

public class AnalogInput implements HardwareDevice {
    private AnalogInputController f218a;
    private int f219b;

    public AnalogInput(AnalogInputController controller, int channel) {
        this.f218a = null;
        this.f219b = -1;
        this.f218a = controller;
        this.f219b = channel;
    }

    public int getValue() {
        return this.f218a.getAnalogInputValue(this.f219b);
    }

    public String getDeviceName() {
        return "Analog Input";
    }

    public String getConnectionInfo() {
        return this.f218a.getConnectionInfo() + "; analog port " + this.f219b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
