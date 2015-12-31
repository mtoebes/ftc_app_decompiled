package com.qualcomm.robotcore.hardware;

/**
 * Gyro Sensor
 */
public abstract class GyroSensor implements HardwareDevice {
    /**
     * Calibrate the gyro. For the Modern Robotics device this will null, or reset, the Z axis heading.
     */
    public abstract void calibrate();

    /**
     * Return the integrated Z axis as a cartesian heading.
     *
     * @return heading between 0-360.
     */
    public abstract int getHeading();

    /**
     * Return the rotation of this sensor
     *
     * @return rotation
     */
    public abstract double getRotation();

    /**
     * Is the gyro performing a calibration operation?
     *
     * @return true if yes, false otherwise
     */
    public abstract boolean isCalibrating();

    /**
     * Return the gyro's raw X value.
     *
     * @return X value
     */
    public abstract int rawX();

    /**
     * Return the gyro's raw Y value.
     *
     * @return Y value
     */
    public abstract int rawY();

    /**
     * Return the gyro's raw Z value.
     *
     * @return Z value
     */
    public abstract int rawZ();

    /**
     * Set the integrated Z axis to zero.
     */
    public abstract void resetZAxisIntegrator();

    /**
     * Status of this sensor, in string form
     *
     * @return status
     */
    public abstract String status();

    @Override
    public String toString() {
        return String.format("Gyro: %3.1f", getRotation());
    }

    public void notSupported() {
        throw new UnsupportedOperationException("This method is not supported for " + getDeviceName());
    }
}
