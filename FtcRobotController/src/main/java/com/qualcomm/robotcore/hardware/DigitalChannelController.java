package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface DigitalChannelController extends HardwareDevice {

    enum Mode {
        INPUT,
        OUTPUT
    }

    Mode getDigitalChannelMode(int channel);

    boolean getDigitalChannelState(int channel);

    SerialNumber getSerialNumber();

    void setDigitalChannelMode(int channel, Mode mode);

    void setDigitalChannelState(int channel, boolean state);
}
