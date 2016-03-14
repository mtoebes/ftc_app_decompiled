package com.qualcomm.robotcore.hardware;

public interface CompassSensor extends HardwareDevice {

    public enum CompassMode {
        MEASUREMENT_MODE,
        CALIBRATION_MODE
    }

    boolean calibrationFailed();

    double getDirection();

    void setMode(CompassMode compassMode);

    String status();
}
