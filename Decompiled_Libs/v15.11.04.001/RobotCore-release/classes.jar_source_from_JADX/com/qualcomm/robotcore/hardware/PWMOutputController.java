package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface PWMOutputController extends HardwareDevice {
    int getPulseWidthOutputTime(int i);

    int getPulseWidthPeriod(int i);

    SerialNumber getSerialNumber();

    void setPulseWidthOutputTime(int i, int i2);

    void setPulseWidthPeriod(int i, int i2);
}
