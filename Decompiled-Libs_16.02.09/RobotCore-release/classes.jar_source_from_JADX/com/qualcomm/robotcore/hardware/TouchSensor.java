package com.qualcomm.robotcore.hardware;

public interface TouchSensor extends HardwareDevice {
    double getValue();

    boolean isPressed();
}
