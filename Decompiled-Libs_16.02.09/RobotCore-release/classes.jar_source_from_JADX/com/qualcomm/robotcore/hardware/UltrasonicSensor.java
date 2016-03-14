package com.qualcomm.robotcore.hardware;

public interface UltrasonicSensor extends HardwareDevice {
    double getUltrasonicLevel();

    String status();
}
