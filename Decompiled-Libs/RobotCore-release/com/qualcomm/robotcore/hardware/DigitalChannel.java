package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DigitalChannelController.Mode;

public class DigitalChannel implements HardwareDevice {
    private DigitalChannelController f233a;
    private int f234b;

    public DigitalChannel(DigitalChannelController controller, int channel) {
        this.f233a = null;
        this.f234b = -1;
        this.f233a = controller;
        this.f234b = channel;
    }

    public Mode getMode() {
        return this.f233a.getDigitalChannelMode(this.f234b);
    }

    public void setMode(Mode mode) {
        this.f233a.setDigitalChannelMode(this.f234b, mode);
    }

    public boolean getState() {
        return this.f233a.getDigitalChannelState(this.f234b);
    }

    public void setState(boolean state) {
        this.f233a.setDigitalChannelState(this.f234b, state);
    }

    public String getDeviceName() {
        return "Digital Channel";
    }

    public String getConnectionInfo() {
        return this.f233a.getConnectionInfo() + "; digital port " + this.f234b;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
