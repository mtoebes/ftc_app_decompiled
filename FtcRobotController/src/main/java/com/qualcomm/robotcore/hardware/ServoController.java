package com.qualcomm.robotcore.hardware;

public interface ServoController extends HardwareDevice {

    enum PwmStatus {
        ENABLED,
        DISABLED
    }

    PwmStatus getPwmStatus();

    double getServoPosition(int channel);

    void pwmDisable();

    void pwmEnable();

    void setServoPosition(int channel, double position);
}
