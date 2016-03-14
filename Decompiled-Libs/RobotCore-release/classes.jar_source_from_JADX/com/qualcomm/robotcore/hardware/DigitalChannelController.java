package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface DigitalChannelController extends HardwareDevice {

    public enum Mode {
        INPUT,
        OUTPUT
    }

    Mode getDigitalChannelMode(int i);

    boolean getDigitalChannelState(int i);

    SerialNumber getSerialNumber();

    void setDigitalChannelMode(int i, Mode mode);

    void setDigitalChannelState(int i, boolean z);
}
