package com.qualcomm.robotcore.hardware;

public abstract class OpticalDistanceSensor extends LightSensor {
    @Override
    public String toString() {
        return String.format("OpticalDistanceSensor: %f", getLightDetected());
    }
}
