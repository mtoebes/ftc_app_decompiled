package com.qualcomm.robotcore.hardware;

public interface LegacyModule extends HardwareDevice, I2cController {
    void enable9v(int i, boolean z);

    void enableAnalogReadMode(int i);

    byte[] readAnalog(int i);

    void setDigitalLine(int i, int i2, boolean z);
}
