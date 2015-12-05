package com.qualcomm.robotcore.hardware;

public abstract class UltrasonicSensor implements HardwareDevice {
    public abstract double getUltrasonicLevel();

    public abstract String status();

    public String toString() {
        return String.format("Ultrasonic: %6.1f", new Object[]{Double.valueOf(getUltrasonicLevel())});
    }
}
