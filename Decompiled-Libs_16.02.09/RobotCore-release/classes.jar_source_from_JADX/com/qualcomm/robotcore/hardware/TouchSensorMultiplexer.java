package com.qualcomm.robotcore.hardware;

public interface TouchSensorMultiplexer extends HardwareDevice {
    int getSwitches();

    boolean isTouchSensorPressed(int i);
}
