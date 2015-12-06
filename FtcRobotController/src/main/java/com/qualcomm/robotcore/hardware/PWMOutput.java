package com.qualcomm.robotcore.hardware;

public class PWMOutput implements HardwareDevice {
    private PWMOutputController controller;
    private int port;

    public PWMOutput(PWMOutputController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    public void setPulseWidthOutputTime(int time) {
        controller.setPulseWidthOutputTime(port, time);
    }

    public int getPulseWidthOutputTime() {
        return controller.getPulseWidthOutputTime(port);
    }

    public void setPulseWidthPeriod(int period) {
        controller.setPulseWidthPeriod(port, period);
    }

    public int getPulseWidthPeriod() {
        return controller.getPulseWidthPeriod(port);
    }

    public String getDeviceName() {
        return "PWM Output";
    }

    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; port " + port;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
