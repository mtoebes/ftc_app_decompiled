package com.qualcomm.robotcore.hardware;

/**
 * Interface for working with DC Motor Controllers
 * <p/>
 * Different DC motor controllers will implement this interface.
 */
public interface DcMotorController extends HardwareDevice {

    enum DeviceMode {
        SWITCHING_TO_READ_MODE,
        SWITCHING_TO_WRITE_MODE,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE
    }

    enum RunMode {
        RUN_USING_ENCODERS,
        RUN_WITHOUT_ENCODERS,
        RUN_TO_POSITION,
        RESET_ENCODERS
    }

    /**
     * Get the current channel mode. Returns the current "run mode".
     *
     * @param motor port of motor
     * @return run mode
     */
    RunMode getMotorChannelMode(int motor);

    /**
     * Get the current device mode (read, write, or read/write) Note: on USB devices, this will always return "READ_WRITE" mode. On Nxt devices, it may return "READ_ONLY", "WRITE_ONLY", "SWITCHING_TO_READ_MODE", or "SWITCHING_TO_WRITE_MODE". This is because of the delay between asking the hardware to switch modes, and the modes actually being switched. Both "SWITCHING" modes simply communicate that delay to the user. The only modes the user should set are "READ_ONLY" and "WRITE_ONLY."
     *
     * @return current device mode
     */
    DeviceMode getMotorControllerDeviceMode();

    /**
     * Get the current motor position
     *
     * @param motor port of motor
     * @return integer, unscaled
     */
    int getMotorCurrentPosition(int motor);

    /**
     * Get the current motor power
     *
     * @param motor port of motor
     * @return scaled from -1.0 to 1.0
     */
    double getMotorPower(int motor);

    /**
     * Is motor power set to float?
     *
     * @param motor port of motor
     * @return true of motor is set to float
     */
    boolean getMotorPowerFloat(int motor);

    /**
     * Get the current motor target position
     *
     * @param motor port of motor
     * @return integer, unscaled
     */
    int getMotorTargetPosition(int motor);

    /**
     * Is the motor busy?
     *
     * @param motor port of motor
     * @return true if the motor is busy
     */
    boolean isBusy(int motor);

    /**
     * Set the current channel mode
     *
     * @param motor   port of motor
     * @param runMode run mode
     */
    void setMotorChannelMode(int motor, RunMode runMode);

    /**
     * Set the device into read, write, or read/write modes Note: If you are using the NxtDcMotorController, you need to switch the controller into "read" mode before doing a read, and into "write" mode before doing a write. This is because the NxtDcMotorController is on the I2C interface, and can only do one at a time. If you are using the USBDcMotorController, there is no need to switch, because USB can handle reads and writes without changing modes. The NxtDcMotorControllers start up in "write" mode. This method does nothing on USB devices, but is needed on Nxt devices. The only modes the user should set are "READ_ONLY" and "WRITE_ONLY."
     *
     * @param deviceMode device mode
     */
    void setMotorControllerDeviceMode(DeviceMode deviceMode);

    /**
     * Set the current motor power
     *
     * @param motor port of motor
     * @param power form -1.0 to 1.0
     */
    void setMotorPower(int motor, double power);

    /**
     * Allow motor to float
     *
     * @param motor port of motor
     */
    void setMotorPowerFloat(int motor);

    /**
     * Set the motor target position. This takes in an integer, which is not scaled. Motor power should be positive if using run to position
     *
     * @param motor    port of motor
     * @param position range from Integer.MIN_VALUE to Integer.MAX_VALUE
     */
    void setMotorTargetPosition(int motor, int position);
}
