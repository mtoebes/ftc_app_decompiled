package com.qualcomm.robotcore.hardware;

public class PWMOutput implements HardwareDevice {
    private PWMOutputController f253a;
    private int f254b;

    public PWMOutput(PWMOutputController controller, int port) {
        this.f253a = null;
        this.f254b = -1;
        this.f253a = controller;
        this.f254b = port;
    }

    public void setPulseWidthOutputTime(int time) {
        this.f253a.setPulseWidthOutputTime(this.f254b, time);
    }

    public int getPulseWidthOutputTime() {
        return this.f253a.getPulseWidthOutputTime(this.f254b);
    }

    public void setPulseWidthPeriod(int period) {
        this.f253a.setPulseWidthPeriod(this.f254b, period);
    }

    public int getPulseWidthPeriod() {
        return this.f253a.getPulseWidthPeriod(this.f254b);
    }

    public String getDeviceName() {
        return "PWM Output";
    }

    public String getConnectionInfo() {
        return this.f253a.getConnectionInfo() + "; port " + this.f254b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
