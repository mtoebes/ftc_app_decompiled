package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class LED implements HardwareDevice {
    private DigitalChannelController controller;
    private int physicalPort;

    public LED(DigitalChannelController controller, int physicalPort) {
        this.controller = controller;
        this.physicalPort = physicalPort;
        controller.setDigitalChannelMode(physicalPort, Mode.OUTPUT);
    }

    public void enable(boolean set) {
        controller.setDigitalChannelState(physicalPort, set);
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
