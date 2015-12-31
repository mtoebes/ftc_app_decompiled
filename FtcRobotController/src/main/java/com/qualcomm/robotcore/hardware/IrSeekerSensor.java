/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.hardware;

/**
 * IR Seeker Sensor
 * <p/>
 * Determine the location of an IR source
 */
public abstract class IrSeekerSensor implements HardwareDevice {
    public static final int MAX_NEW_I2C_ADDRESS = 126;
    public static final int MIN_NEW_I2C_ADDRESS = 16;

    /**
     * IR Sensor attached to an IR Seeker
     * <p/>
     * Get the angle of this sensor, along with signal strength
     */
    public static class IrSeekerIndividualSensor {
        private double angle;
        private double strength;

        /**
         * Constructor
         */
        public IrSeekerIndividualSensor() {
            this(0.0d, 0.0d);
        }

        /**
         * Constructor
         *
         * @param angle    sensor angle
         * @param strength IR strength
         */
        public IrSeekerIndividualSensor(double angle, double strength) {
            this.angle = angle;
            this.strength = strength;
        }

        /**
         * Get the angle at which this sensor is mounted
         *
         * @return sensor angle
         */
        public double getSensorAngle() {
            return this.angle;
        }

        /**
         * Get the strength of the IR signal detected by this sensor
         *
         * @return IR strength, scaled from 0 to 1
         */
        public double getSensorStrength() {
            return this.strength;
        }

        @Override
        public String toString() {
            return String.format("IR Sensor: %3.1f degrees at %3.1f%% power", this.angle, this.strength * 100.0);
        }
    }

    /**
     * Enumeration of device modes
     */
    public enum Mode {
        MODE_600HZ,
        MODE_1200HZ
    }

    /**
     * Estimated angle in which the signal is coming from
     * <p/>
     * If the signal is estimated to be directly ahead, 0 will be returned. If the signal is to the left a negative angle will be returned. If the signal is to the right a positive angle will be returned. If no signal is detected, a 0 will be returned.
     * <p/>
     * NOTE: not all sensors give an accurate angle.
     *
     * @return angle to IR signal
     */
    public abstract double getAngle();

    /**
     * Get the current I2C Address of this object. Not necessarily the same as the I2C address of the actual device. Return the current I2C address.
     *
     * @return current I2C address
     */
    public abstract int getI2cAddress();

    /**
     * Get a list of all IR sensors attached to this seeker. The list will include the angle at which the sensor is mounted, and the signal strength.
     *
     * @return array of IrSensors
     */
    public abstract IrSeekerIndividualSensor[] getIndividualSensors();

    /**
     * Get the device mode
     *
     * @return device mode
     */
    public abstract Mode getMode();

    /**
     * Get the minimum threshold for a signal to be considered detected
     *
     * @return minimum threshold
     */
    public abstract double getSignalDetectedThreshold();

    /**
     * IR Signal strength
     * <p/>
     * Detected IR signal strength, on a scale of 0.0 to 1.0, where 0 is no signal detected and 1 is max IR signal detected.
     *
     * @return signal strength, scaled from 0 to 1
     */
    public abstract double getStrength();

    /**
     * Set the I2C address to a new value.
     *
     * @param newAddress new I2C address
     */
    public abstract void setI2cAddress(int newAddress);

    /**
     * Set the device mode
     *
     * @param mode sample rate
     */
    public abstract void setMode(Mode mode);

    /**
     * Set the minimum threshold for a signal to be considered detected
     *
     * @param threshold minimum threshold
     */
    public abstract void setSignalDetectedThreshold(double threshold);

    /**
     * Returns true if an IR signal is detected
     *
     * @return true if signal is detected; otherwise false
     */
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
