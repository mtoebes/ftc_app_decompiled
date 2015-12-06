package com.qualcomm.robotcore.hardware;

public abstract class AccelerationSensor implements HardwareDevice {

    public static class Acceleration {
        public double x;
        public double y;
        public double z;

        public Acceleration() {
            this(0.0d, 0.0d, 0.0d);
        }

        public Acceleration(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String toString() {
            return String.format("Acceleration - x: %5.2f, y: %5.2f, z: %5.2f", x, y, z);
        }
    }

    public abstract Acceleration getAcceleration();

    public abstract String status();

    public String toString() {
        return getAcceleration().toString();
    }
}
