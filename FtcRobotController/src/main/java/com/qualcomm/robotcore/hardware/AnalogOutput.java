package com.qualcomm.robotcore.hardware;

public class AnalogOutput implements HardwareDevice {
    private static final String DEVICE_NAME = "Analog Output";
    private static final int VERSION = 1;

    private AnalogOutputController controller;
    private int channel;

    public AnalogOutput(AnalogOutputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public void setAnalogOutputVoltage(int voltage) {
        this.controller.setAnalogOutputVoltage(this.channel, voltage);
    }

    public void setAnalogOutputFrequency(int freq) {
        this.controller.setAnalogOutputFrequency(this.channel, freq);
    }

    public void setAnalogOutputMode(byte mode) {
        this.controller.setAnalogOutputMode(this.channel, mode);
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
