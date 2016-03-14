package com.qualcomm.robotcore.hardware;

public interface GyroSensor extends HardwareDevice {
    void calibrate();

    int getHeading();

    double getRotation();

    boolean isCalibrating();

    int rawX();

    int rawY();

    int rawZ();

    void resetZAxisIntegrator();

    String status();
}
