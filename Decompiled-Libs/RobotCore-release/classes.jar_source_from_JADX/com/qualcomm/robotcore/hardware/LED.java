package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class LED implements HardwareDevice {
    private DigitalChannelController f251a;
    private int f252b;

    public LED(DigitalChannelController controller, int physicalPort) {
        this.f251a = null;
        this.f252b = -1;
        this.f251a = controller;
        this.f252b = physicalPort;
        controller.setDigitalChannelMode(physicalPort, Mode.OUTPUT);
    }

    public void enable(boolean set) {
        this.f251a.setDigitalChannelState(this.f252b, set);
    }

    public String getDeviceName() {
        return null;
    }

    public String getConnectionInfo() {
        return null;
    }

    public int getVersion() {
        return 0;
    }

    public void close() {
    }
}
