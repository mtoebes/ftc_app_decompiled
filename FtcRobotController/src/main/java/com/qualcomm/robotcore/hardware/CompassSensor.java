package com.qualcomm.robotcore.hardware;

/**
 * Compass Sensor
 */
public abstract class CompassSensor implements HardwareDevice {

    public enum CompassMode {
        MEASUREMENT_MODE,
        CALIBRATION_MODE
    }

    /**
     * Check to see whether calibration was successful. After attempting a calibration, the hardware will (eventually) indicate whether or not it was unsuccessful. The default is "success", even when the calibration is not guaranteed to have completed successfully. A user should monitor this field for (at least) several seconds to determine success.
     *
     * @return failure
     */
    public abstract boolean calibrationFailed();

    /**
     * Get the current direction, in degrees
     *
     * @return current direction, in degrees
     */
    public abstract double getDirection();

    /**
     * Change to calibration or measurement mode
     *
     * @param compassMode new mode
     */
    public abstract void setMode(CompassMode compassMode);

    /**
     * Status of this sensor, in string form
     *
     * @return status
     */
    public abstract String status();

    public String toString() {
        return String.format("Compass: %3.1f", getDirection());
    }
}
