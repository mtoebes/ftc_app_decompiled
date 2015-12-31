package com.qualcomm.robotcore.hardware;

public abstract class UltrasonicSensor implements HardwareDevice {
    /**
     * Get the Ultrasonic levels from this sensor
     *
     * @return Get the Ultrasonic levels from this sensor
     */
    public abstract double getUltrasonicLevel();

    /**
     * Status of this sensor, in string form
     *
     * @return status
     */
    public abstract String status();

    @Override
    public String toString() {
        return String.format("Ultrasonic: %6.1f", getUltrasonicLevel());
    }
}
