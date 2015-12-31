package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;

/**
 * Control a DC Motor attached to a DC Motor Controller
 */
public class DcMotor implements HardwareDevice {
    private static final String DEVICE_NAME = "DC Motor";
    private static final int VERSION = 1;

    protected DcMotorController controller;
    protected DeviceMode devMode;
    protected Direction direction;
    protected RunMode mode;
    protected int portNumber;

    /**
     * Motor direction
     */
    public enum Direction {
        FORWARD,
        REVERSE
    }

    /**
     * Constructor
     *
     * @param controller DC motor controller this motor is attached to
     * @param portNumber portNumber position on the controller
     */
    public DcMotor(DcMotorController controller, int portNumber) {
        this(controller, portNumber, Direction.FORWARD);
    }

    /**
     * Constructor
     *
     * @param controller DC motor controller this motor is attached to
     * @param portNumber portNumber position on the controller
     * @param direction  direction this motor should spin
     */
    public DcMotor(DcMotorController controller, int portNumber, Direction direction) {
        this.mode = RunMode.RUN_WITHOUT_ENCODERS;
        this.devMode = DeviceMode.WRITE_ONLY;
        this.controller = controller;
        this.portNumber = portNumber;
        this.direction = direction;
    }

    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.portNumber;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
        setPowerFloat();
    }

    /**
     * Get DC motor controller
     *
     * @return controller
     */
    public DcMotorController getController() {
        return this.controller;
    }

    /**
     * Set the direction
     *
     * @param direction direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the direction
     *
     * @return direction
     */
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Get port number
     *
     * @return portNumber
     */
    public int getPortNumber() {
        return this.portNumber;
    }

    /**
     * Set the current motor power
     *
     * @param power - from -1.0 to 1.0
     */
    public void setPower(double power) {
        if (this.direction == Direction.REVERSE) {
            power *= -1;
        }
        if (this.mode == RunMode.RUN_TO_POSITION) {
            power = Math.abs(power);
        }

        this.controller.setMotorPower(this.portNumber, power);
    }

    /**
     * Get the current motor power
     *
     * @return scaled from -1.0 to 1.0
     */
    public double getPower() {
        double motorPower = this.controller.getMotorPower(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorPower : -motorPower);
    }

    /**
     * Is the motor busy?
     *
     * @return true if the motor is busy
     */
    public boolean isBusy() {
        return this.controller.isBusy(this.portNumber);
    }

    /**
     * Allow motor to float
     */
    public void setPowerFloat() {
        this.controller.setMotorPowerFloat(this.portNumber);
    }

    /**
     * Is motor power set to float?
     *
     * @return true of motor is set to float
     */
    public boolean getPowerFloat() {
        return this.controller.getMotorPowerFloat(this.portNumber);
    }

    /**
     * Set the motor target position, using an integer. If this motor has been set to REVERSE, the passed-in "position" value will be multiplied by -1.
     *
     * @param position range from Integer.MIN_VALUE to Integer.MAX_VALUE
     */
    public void setTargetPosition(int position) {
        if (this.direction == Direction.REVERSE) {
            position *= -1;
        }
        this.controller.setMotorTargetPosition(this.portNumber, position);
    }

    /**
     * Get the current motor target position. If this motor has been set to REVERSE, the returned "position" will be multiplied by -1.
     *
     * @return integer, unscaled
     */
    public int getTargetPosition() {
        int motorTargetPosition = this.controller.getMotorTargetPosition(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorTargetPosition : -motorTargetPosition);
    }

    /**
     * Get the current encoder value. If this motor has been set to REVERSE, the returned "position" will be multiplied by -1.
     *
     * @return double indicating current position
     */
    public int getCurrentPosition() {
        int motorCurrentPosition = this.controller.getMotorCurrentPosition(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorCurrentPosition : -motorCurrentPosition);
    }

    /**
     * Set the current mode
     *
     * @param mode run mode
     */
    public void setMode(RunMode mode) {
        this.mode = mode;
        this.controller.setMotorChannelMode(this.portNumber, mode);
    }

    /**
     * Get the current mode
     *
     * @return run mode
     */
    public RunMode getMode() {
        return this.controller.getMotorChannelMode(this.portNumber);
    }

    /**
     * Set the current mode Deprecated; use setMode()
     *
     * @param mode run mode
     */
    @Deprecated
    public void setChannelMode(RunMode mode) {
        setMode(mode);
    }

    /**
     * Get the current mode Deprecated; use getMode()
     *
     * @return run mode
     */
    @Deprecated
    public RunMode getChannelMode() {
        return getMode();
    }
}
