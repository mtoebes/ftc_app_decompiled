package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class DigitalChannel implements HardwareDevice {
    private DigitalChannelController f227a;
    private int f228b;

    public DigitalChannel(DigitalChannelController controller, int channel) {
        this.f227a = null;
        this.f228b = -1;
        this.f227a = controller;
        this.f228b = channel;
    }

    public Mode getMode() {
        return this.f227a.getDigitalChannelMode(this.f228b);
    }

    public void setMode(Mode mode) {
        this.f227a.setDigitalChannelMode(this.f228b, mode);
    }

    public boolean getState() {
        return this.f227a.getDigitalChannelState(this.f228b);
    }

    public void setState(boolean state) {
        this.f227a.setDigitalChannelState(this.f228b, state);
    }

    public String getDeviceName() {
        return "Digital Channel";
    }

    public String getConnectionInfo() {
        return this.f227a.getConnectionInfo() + "; digital port " + this.f228b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
