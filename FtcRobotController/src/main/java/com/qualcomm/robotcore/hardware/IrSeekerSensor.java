package com.qualcomm.robotcore.hardware;

public abstract class IrSeekerSensor implements HardwareDevice {
    public static final int MAX_NEW_I2C_ADDRESS = 126;
    public static final int MIN_NEW_I2C_ADDRESS = 16;

    public static class IrSeekerIndividualSensor {
        private double angle;
        private double strength;

        public IrSeekerIndividualSensor() {
            this(0.0d, 0.0d);
        }

        public IrSeekerIndividualSensor(double angle, double strength) {
            this.angle = angle;
            this.strength = strength;
        }

        public double getSensorAngle() {
            return this.angle;
        }

        public double getSensorStrength() {
            return this.strength;
        }

        public String toString() {
            return String.format("IR Sensor: %3.1f degrees at %3.1f%% power", this.angle, this.strength * 100.0);
        }
    }

    public enum Mode {
        MODE_600HZ,
        MODE_1200HZ
    }

    public abstract double getAngle();

    public abstract int getI2cAddress();

    public abstract IrSeekerIndividualSensor[] getIndividualSensors();

    public abstract Mode getMode();

    public abstract double getSignalDetectedThreshold();

    public abstract double getStrength();

    public abstract void setI2cAddress(int newAddress);

    public abstract void setMode(Mode mode);

    public abstract void setSignalDetectedThreshold(double threshold);

    public abstract boolean signalDetected();

    public String toString() {
        if (!signalDetected()) {
            return "IR Seeker:  --% signal at  ---.- degrees";
        }
        return String.format("IR Seeker: %3.0f%% signal at %6.1f degrees", getStrength() * 100.0d, getAngle());
    }

    public static void throwIfModernRoboticsI2cAddressIsInvalid(int newAddress) {
        if ((newAddress < MIN_NEW_I2C_ADDRESS) || (newAddress > MAX_NEW_I2C_ADDRESS)) {
            throw new IllegalArgumentException(String.format("New I2C address %d is invalid; valid range is: %d..%d", newAddress, MIN_NEW_I2C_ADDRESS, MAX_NEW_I2C_ADDRESS));
        } else if ((newAddress % 2) != 0) {
            throw new IllegalArgumentException(String.format("New I2C address %d is invalid; the address must be even.", newAddress));
        }
    }
}
