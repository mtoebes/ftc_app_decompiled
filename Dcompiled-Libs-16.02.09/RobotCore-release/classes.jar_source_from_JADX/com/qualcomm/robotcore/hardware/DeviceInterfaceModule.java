package com.qualcomm.robotcore.hardware;

public interface DeviceInterfaceModule extends AnalogInputController, AnalogOutputController, DigitalChannelController, I2cController, PWMOutputController {
    byte getDigitalIOControlByte();

    int getDigitalInputStateByte();

    byte getDigitalOutputStateByte();

    boolean getLEDState(int i);

    void setDigitalIOControlByte(byte b);

    void setDigitalOutputByte(byte b);

    void setLED(int i, boolean z);
}
