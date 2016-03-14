package com.qualcomm.robotcore.hardware;

public interface LightSensor extends HardwareDevice {
    void enableLed(boolean z);

    double getLightDetected();

    int getLightDetectedRaw();

    String status();
}
