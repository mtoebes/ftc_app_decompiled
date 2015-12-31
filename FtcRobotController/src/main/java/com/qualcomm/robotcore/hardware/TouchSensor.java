package com.qualcomm.robotcore.hardware;

/**
 * Touch Sensor
 */
public abstract class TouchSensor implements HardwareDevice {
    /**
     * Represents how much force is applied to the touch sensor; for some touch sensors this value will only ever be 0 or 1.
     *
     * @return a number between 0 and 1
     */
    public abstract double getValue();

    /**
     * Return true if the touch sensor is being pressed
     *
     * @return true if the touch sensor is being pressed
     */
    public abstract boolean isPressed();

    @Override
    public String toString() {
        return String.format("Touch Sensor: %1.2f", getValue());
    }
}
