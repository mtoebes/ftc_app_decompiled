package com.qualcomm.robotcore.hardware;

/**
 * Light Sensor
 */
public abstract class ColorSensor implements HardwareDevice {
    public abstract int alpha();

    /**
     * Get the "hue"
     *
     * @return hue
     */
    public abstract int argb();

    /**
     * Get the Blue values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int blue();

    /**
     * Enable the LED light
     *
     * @param enable true to enable; false to disable
     */
    public abstract void enableLed(boolean enable);

    /**
     * Get the current I2C Address of this object. Not necessarily the same as the I2C address of the actual device. Return the current I2C address.
     *
     * @return current I2C address
     */
    public abstract int getI2cAddress();

    /**
     * Get the Green values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int green();

    /**
     * Get the Red values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int red();

    /**
     * Set the I2C address to a new value.
     *
     * @param newAddress value of new I2C address
     */
    public abstract void setI2cAddress(int newAddress);

    public String toString() {
        return String.format("argb: %d", argb());
    }
}
