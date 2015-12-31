package com.qualcomm.robotcore.hardware;

/**
 * Interface for working with Servo Controllers
 * <p/>
 * Different servo controllers will implement this interface.
 */
public interface ServoController extends HardwareDevice {

    /**
     * PWM Status - is pwm enabled?
     */
    enum PwmStatus {
        ENABLED,
        DISABLED
    }

    /**
     * Get the PWM status
     *
     * @return status
     */
    PwmStatus getPwmStatus();

    /**
     * Get the position of a servo at a given channel
     *
     * @param channel channel of servo
     * @return position, scaled from 0.0 to 1.0
     */
    double getServoPosition(int channel);

    /**
     * PWM enable
     */
    void pwmDisable();

    /**
     * PWM disable
     */
    void pwmEnable();

    /**
     * Set the position of a servo at the given channel
     *
     * @param channel  channel of servo
     * @param position from 0.0 to 1.0
     */
    void setServoPosition(int channel, double position);
}
