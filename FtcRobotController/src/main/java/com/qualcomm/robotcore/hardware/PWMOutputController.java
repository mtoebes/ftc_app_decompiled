package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface PWMOutputController extends HardwareDevice {
    int getPulseWidthOutputTime(int port);

    int getPulseWidthPeriod(int port);

    SerialNumber getSerialNumber();

    void setPulseWidthOutputTime(int port, int time);

    void setPulseWidthPeriod(int port, int period);
}
