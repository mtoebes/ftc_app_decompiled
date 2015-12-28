package com.qualcomm.robotcore.hardware;

public abstract class LightSensor implements HardwareDevice {
    public abstract void enableLed(boolean enable);

    public abstract double getLightDetected();

    public abstract int getLightDetectedRaw();

    public abstract String status();

    public String toString() {
        return String.format("Light Level: %1.2f", getLightDetected());
    }
}
