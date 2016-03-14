package com.qualcomm.robotcore.hardware;

public abstract class IrSeekerSensor implements HardwareDevice {
    public static final int MAX_NEW_I2C_ADDRESS = 126;
    public static final int MIN_NEW_I2C_ADDRESS = 16;

    public static class IrSeekerIndividualSensor {
        private double f248a;
        private double f249b;

        public IrSeekerIndividualSensor() {
            this(0.0d, 0.0d);
        }

        public IrSeekerIndividualSensor(double angle, double strength) {
            this.f248a = 0.0d;
            this.f249b = 0.0d;
            this.f248a = angle;
            this.f249b = strength;
        }

        public double getSensorAngle() {
            return this.f248a;
        }

        public double getSensorStrength() {
            return this.f249b;
        }

        public String toString() {
            return String.format("IR Sensor: %3.1f degrees at %3.1f%% power", new Object[]{Double.valueOf(this.f248a), Double.valueOf(this.f249b * 100.0d)});
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

    public abstract void setI2cAddress(int i);

    public abstract void setMode(Mode mode);

    public abstract void setSignalDetectedThreshold(double d);

    public abstract boolean signalDetected();

    public String toString() {
        if (!signalDetected()) {
            return "IR Seeker:  --% signal at  ---.- degrees";
        }
        return String.format("IR Seeker: %3.0f%% signal at %6.1f degrees", new Object[]{Double.valueOf(getStrength() * 100.0d), Double.valueOf(getAngle())});
    }

    public static void throwIfModernRoboticsI2cAddressIsInvalid(int newAddress) {
        if (newAddress < MIN_NEW_I2C_ADDRESS || newAddress > MAX_NEW_I2C_ADDRESS) {
            throw new IllegalArgumentException(String.format("New I2C address %d is invalid; valid range is: %d..%d", new Object[]{Integer.valueOf(newAddress), Integer.valueOf(MIN_NEW_I2C_ADDRESS), Integer.valueOf(MAX_NEW_I2C_ADDRESS)}));
        } else if (newAddress % 2 != 0) {
            throw new IllegalArgumentException(String.format("New I2C address %d is invalid; the address must be even.", new Object[]{Integer.valueOf(newAddress)}));
        }
    }
}
