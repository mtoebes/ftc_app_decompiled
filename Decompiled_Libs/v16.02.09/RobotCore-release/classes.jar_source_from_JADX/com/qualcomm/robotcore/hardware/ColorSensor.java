package com.qualcomm.robotcore.hardware;

public interface ColorSensor extends HardwareDevice {
    int alpha();

    int argb();

    int blue();

    void enableLed(boolean z);

    int getI2cAddress();

    int green();

    int red();

    void setI2cAddress(int i);
}
