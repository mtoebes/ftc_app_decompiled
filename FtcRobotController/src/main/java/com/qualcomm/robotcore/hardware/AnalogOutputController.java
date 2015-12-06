package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface AnalogOutputController extends HardwareDevice {
    SerialNumber getSerialNumber();

    void setAnalogOutputFrequency(int port, int freq);

    void setAnalogOutputMode(int port, byte mode);

    void setAnalogOutputVoltage(int port, int voltage);
}
