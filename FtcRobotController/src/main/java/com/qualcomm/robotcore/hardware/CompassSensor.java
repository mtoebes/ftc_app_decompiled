package com.qualcomm.robotcore.hardware;

public abstract class CompassSensor implements HardwareDevice {

    public enum CompassMode {
        MEASUREMENT_MODE,
        CALIBRATION_MODE
    }

    public abstract boolean calibrationFailed();

    public abstract double getDirection();

    public abstract void setMode(CompassMode compassMode);

    public abstract String status();

    public String toString() {
        return String.format("Compass: %3.1f", getDirection());
    }
}
