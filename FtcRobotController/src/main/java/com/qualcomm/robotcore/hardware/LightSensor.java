package com.qualcomm.robotcore.hardware;

/**
 * Light Sensor
 */
public abstract class LightSensor implements HardwareDevice {
    /**
     * Enable the LED light
     *
     * @param enable true to enable; false to disable
     */
    public abstract void enableLed(boolean enable);

    /**
     * Get the amount of light detected by the sensor. 1.0 is max possible light, 0.0 is least possible light.
     *
     * @return amount of light, on a scale of 0 to 1
     */
    public abstract double getLightDetected();

    /**
     * Get the amount of light detected by the sensor as an int.
     *
     * @return amount of light, unscaled.
     */
    public abstract int getLightDetectedRaw();

    /**
     * Status of this sensor, in string form
     *
     * @return status
     */
    public abstract String status();

    @Override
    public String toString() {
        return String.format("Light Level: %1.2f", getLightDetected());
    }
}
