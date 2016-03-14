package com.qualcomm.robotcore.hardware;

public interface AccelerationSensor extends HardwareDevice {

    public static class Acceleration {
        public double f215x;
        public double f216y;
        public double f217z;

        public Acceleration() {
            this(0.0d, 0.0d, 0.0d);
        }

        public Acceleration(double x, double y, double z) {
            this.f215x = x;
            this.f216y = y;
            this.f217z = z;
        }

        public String toString() {
            return String.format("Acceleration - x: %5.2f, y: %5.2f, z: %5.2f", new Object[]{Double.valueOf(this.f215x), Double.valueOf(this.f216y), Double.valueOf(this.f217z)});
        }
    }

    Acceleration getAcceleration();

    String status();
}
