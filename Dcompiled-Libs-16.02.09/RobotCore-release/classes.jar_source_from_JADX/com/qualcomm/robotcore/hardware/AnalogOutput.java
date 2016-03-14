package com.qualcomm.robotcore.hardware;

public class AnalogOutput implements HardwareDevice {
    private AnalogOutputController f220a;
    private int f221b;

    public AnalogOutput(AnalogOutputController controller, int channel) {
        this.f220a = null;
        this.f221b = -1;
        this.f220a = controller;
        this.f221b = channel;
    }

    public void setAnalogOutputVoltage(int voltage) {
        this.f220a.setAnalogOutputVoltage(this.f221b, voltage);
    }

    public void setAnalogOutputFrequency(int freq) {
        this.f220a.setAnalogOutputFrequency(this.f221b, freq);
    }

    public void setAnalogOutputMode(byte mode) {
        this.f220a.setAnalogOutputMode(this.f221b, mode);
    }

    public String getDeviceName() {
        return "Analog Output";
    }

    public String getConnectionInfo() {
        return this.f220a.getConnectionInfo() + "; analog port " + this.f221b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
