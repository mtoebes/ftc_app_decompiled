package com.qualcomm.robotcore.hardware;

public class AnalogOutput implements HardwareDevice {
    private AnalogOutputController f226a;
    private int f227b;

    public AnalogOutput(AnalogOutputController controller, int channel) {
        this.f226a = null;
        this.f227b = -1;
        this.f226a = controller;
        this.f227b = channel;
    }

    public void setAnalogOutputVoltage(int voltage) {
        this.f226a.setAnalogOutputVoltage(this.f227b, voltage);
    }

    public void setAnalogOutputFrequency(int freq) {
        this.f226a.setAnalogOutputFrequency(this.f227b, freq);
    }

    public void setAnalogOutputMode(byte mode) {
        this.f226a.setAnalogOutputMode(this.f227b, mode);
    }

    public String getDeviceName() {
        return "Analog Output";
    }

    public String getConnectionInfo() {
        return this.f226a.getConnectionInfo() + "; analog port " + this.f227b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
