package com.qualcomm.robotcore.hardware;

public class AnalogOutput implements HardwareDevice {
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
        return "Analog Output";
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
