package com.qualcomm.robotcore.hardware;

public abstract class AccelerationSensor implements HardwareDevice {

    public static class Acceleration {
        public double f221x;
        public double f222y;
        public double f223z;

        public Acceleration() {
            this(0.0d, 0.0d, 0.0d);
        }

        public Acceleration(double x, double y, double z) {
            this.f221x = x;
            this.f222y = y;
            this.f223z = z;
        }

        public String toString() {
            return String.format("Acceleration - x: %5.2f, y: %5.2f, z: %5.2f", new Object[]{Double.valueOf(this.f221x), Double.valueOf(this.f222y), Double.valueOf(this.f223z)});
        }
    }

    public abstract Acceleration getAcceleration();

    public abstract String status();

    public String toString() {
        return getAcceleration().toString();
    }
}
