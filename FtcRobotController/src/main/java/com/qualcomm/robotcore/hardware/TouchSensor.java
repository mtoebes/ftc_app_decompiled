package com.qualcomm.robotcore.hardware;

public abstract class TouchSensor implements HardwareDevice {
    public abstract double getValue();

    public abstract boolean isPressed();

    public String toString() {
        return String.format("Touch Sensor: %1.2f", getValue());
    }
}
