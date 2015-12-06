package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class DigitalChannel implements HardwareDevice {
    private DigitalChannelController controller;
    private int channel;

    public DigitalChannel(DigitalChannelController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public Mode getMode() {
        return controller.getDigitalChannelMode(channel);
    }

    public void setMode(Mode mode) {
        controller.setDigitalChannelMode(channel, mode);
    }

    public boolean getState() {
        return controller.getDigitalChannelState(channel);
    }

    public void setState(boolean state) {
        controller.setDigitalChannelState(channel, state);
    }

    public String getDeviceName() {
        return "Digital Channel";
    }

    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; digital port " + channel;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
