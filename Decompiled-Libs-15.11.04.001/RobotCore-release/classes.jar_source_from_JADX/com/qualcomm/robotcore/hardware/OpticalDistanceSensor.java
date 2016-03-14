package com.qualcomm.robotcore.hardware;

public abstract class OpticalDistanceSensor extends LightSensor {
    public String toString() {
        return String.format("OpticalDistanceSensor: %d", new Object[]{Double.valueOf(getLightDetected())});
    }
}
