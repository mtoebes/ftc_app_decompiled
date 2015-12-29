package com.qualcomm.robotcore.hardware;

public class PWMOutput implements HardwareDevice {
    private static final String DEVICE_NAME = "PWM Output";
    private static final int VERSION = 1;

    private PWMOutputController controller;
    private int port;

    public PWMOutput(PWMOutputController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    public void setPulseWidthOutputTime(int time) {
        this.controller.setPulseWidthOutputTime(this.port, time);
    }

    public int getPulseWidthOutputTime() {
        return this.controller.getPulseWidthOutputTime(this.port);
    }

    public void setPulseWidthPeriod(int period) {
        this.controller.setPulseWidthPeriod(this.port, period);
    }

    public int getPulseWidthPeriod() {
        return this.controller.getPulseWidthPeriod(this.port);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.port;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
