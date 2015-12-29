package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class DigitalChannel implements HardwareDevice {
    private static final String DEVICE_NAME = "Digital Channel";
    private static final int VERSION = 1;

    private DigitalChannelController controller;
    private int channel;

    public DigitalChannel(DigitalChannelController controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public Mode getMode() {
        return this.controller.getDigitalChannelMode(this.channel);
    }

    public void setMode(Mode mode) {
        this.controller.setDigitalChannelMode(this.channel, mode);
    }

    public boolean getState() {
        return this.controller.getDigitalChannelState(this.channel);
    }

    public void setState(boolean state) {
        this.controller.setDigitalChannelState(this.channel, state);
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; digital port " + this.channel;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }
}
