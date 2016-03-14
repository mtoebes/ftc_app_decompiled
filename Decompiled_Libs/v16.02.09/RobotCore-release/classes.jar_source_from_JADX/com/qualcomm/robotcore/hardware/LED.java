package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class LED implements HardwareDevice {
    private DigitalChannelController f244a;
    private int f245b;

    public LED(DigitalChannelController controller, int physicalPort) {
        this.f244a = null;
        this.f245b = -1;
        this.f244a = controller;
        this.f245b = physicalPort;
        controller.setDigitalChannelMode(physicalPort, Mode.OUTPUT);
    }

    public void enable(boolean set) {
        this.f244a.setDigitalChannelState(this.f245b, set);
    }

    public String getDeviceName() {
        return "LED";
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", new Object[]{this.f244a.getConnectionInfo(), Integer.valueOf(this.f245b)});
    }

    public int getVersion() {
        return 0;
    }

    public void close() {
    }
}
