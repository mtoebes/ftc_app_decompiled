package com.qualcomm.robotcore.hardware;

/**
 * Acceleration Sensor
 */
public abstract class AccelerationSensor implements HardwareDevice {

    /**
     * Acceleration in the x, y, and z axis
     */
    public static class Acceleration {
        public double x;
        public double y;
        public double z;

        public Acceleration() {
            this(0.0, 0.0, 0.0);
        }

        public Acceleration(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String toString() {
            return String.format("Acceleration - x: %5.2f, y: %5.2f, z: %5.2f", this.x, this.y, this.z);
        }
    }

    /**
     * Acceleration, measured in g's
     *
     * @return acceleration in g's
     */
    public abstract Acceleration getAcceleration();

    /**
     * Status of this sensor, in string form
     *
     * @return status
     */
    public abstract String status();

    public String toString() {
        return getAcceleration().toString();
    }
}
