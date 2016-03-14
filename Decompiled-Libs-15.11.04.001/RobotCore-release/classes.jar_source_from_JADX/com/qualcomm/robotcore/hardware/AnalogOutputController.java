package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface AnalogOutputController extends HardwareDevice {
    SerialNumber getSerialNumber();

    void setAnalogOutputFrequency(int i, int i2);

    void setAnalogOutputMode(int i, byte b);

    void setAnalogOutputVoltage(int i, int i2);
}
