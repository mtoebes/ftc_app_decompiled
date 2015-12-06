package com.qualcomm.robotcore.hardware;

public class AnalogOutput implements HardwareDevice {
    private AnalogOutputController controller;
    private int channel;

    public AnalogOutput(AnalogOutputController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public void setAnalogOutputVoltage(int voltage) {
        controller.setAnalogOutputVoltage(channel, voltage);
    }

    public void setAnalogOutputFrequency(int freq) {
        controller.setAnalogOutputFrequency(channel, freq);
    }

    public void setAnalogOutputMode(byte mode) {
        controller.setAnalogOutputMode(channel, mode);
    }

    public String getDeviceName() {
        return "Analog Output";
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
