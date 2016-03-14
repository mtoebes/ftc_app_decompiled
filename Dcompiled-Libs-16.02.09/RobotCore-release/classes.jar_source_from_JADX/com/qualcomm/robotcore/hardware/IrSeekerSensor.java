package com.qualcomm.robotcore.hardware;

public interface IrSeekerSensor extends HardwareDevice {

    public static class IrSeekerIndividualSensor {
        private double f241a;
        private double f242b;

        public IrSeekerIndividualSensor() {
            this(0.0d, 0.0d);
        }

        public IrSeekerIndividualSensor(double angle, double strength) {
            this.f241a = 0.0d;
            this.f242b = 0.0d;
            this.f241a = angle;
            this.f242b = strength;
        }

        public double getSensorAngle() {
            return this.f241a;
        }

        public double getSensorStrength() {
            return this.f242b;
        }

        public String toString() {
            return String.format("IR Sensor: %3.1f degrees at %3.1f%% power", new Object[]{Double.valueOf(this.f241a), Double.valueOf(this.f242b * 100.0d)});
        }
    }

    public enum Mode {
        MODE_600HZ,
        MODE_1200HZ
    }

    double getAngle();

    int getI2cAddress();

    IrSeekerIndividualSensor[] getIndividualSensors();

    Mode getMode();

    double getSignalDetectedThreshold();

    double getStrength();

    void setI2cAddress(int i);

    void setMode(Mode mode);

    void setSignalDetectedThreshold(double d);

    boolean signalDetected();
}
