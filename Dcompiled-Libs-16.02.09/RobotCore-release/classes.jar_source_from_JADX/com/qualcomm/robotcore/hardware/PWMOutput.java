package com.qualcomm.robotcore.hardware;

public class PWMOutput implements HardwareDevice {
    private PWMOutputController f247a;
    private int f248b;

    public PWMOutput(PWMOutputController controller, int port) {
        this.f247a = null;
        this.f248b = -1;
        this.f247a = controller;
        this.f248b = port;
    }

    public void setPulseWidthOutputTime(int time) {
        this.f247a.setPulseWidthOutputTime(this.f248b, time);
    }

    public int getPulseWidthOutputTime() {
        return this.f247a.getPulseWidthOutputTime(this.f248b);
    }

    public void setPulseWidthPeriod(int period) {
        this.f247a.setPulseWidthPeriod(this.f248b, period);
    }

    public int getPulseWidthPeriod() {
        return this.f247a.getPulseWidthPeriod(this.f248b);
    }

    public String getDeviceName() {
        return "PWM Output";
    }

    public String getConnectionInfo() {
        return this.f247a.getConnectionInfo() + "; port " + this.f248b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
