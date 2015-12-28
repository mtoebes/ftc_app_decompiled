package com.qualcomm.robotcore.hardware;

public interface DeviceInterfaceModule extends AnalogInputController, AnalogOutputController, DigitalChannelController, I2cController, PWMOutputController {
    byte getDigitalIOControlByte();

    int getDigitalInputStateByte();

    byte getDigitalOutputStateByte();

    boolean getLEDState(int channel);

    void setDigitalIOControlByte(byte input);

    void setDigitalOutputByte(byte input);

    void setLED(int channel, boolean state);
}
