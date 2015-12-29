package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;

public class DcMotor implements HardwareDevice {
    private static final String DEVICE_NAME = "DC Motor";
    private static final int VERSION = 1;

    protected DcMotorController controller;
    protected DeviceMode devMode;
    protected Direction direction;
    protected RunMode mode;
    protected int portNumber;

    public enum Direction {
        FORWARD,
        REVERSE
    }

    public DcMotor(DcMotorController controller, int portNumber) {
        this(controller, portNumber, Direction.FORWARD);
    }

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

    public DcMotorController getController() {
        return this.controller;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public void setPower(double power) {
        if (this.direction == Direction.REVERSE) {
            power *= -1;
        }
        if (this.mode == RunMode.RUN_TO_POSITION) {
            power = Math.abs(power);
        }

        this.controller.setMotorPower(this.portNumber, power);
    }

    public double getPower() {
        double motorPower = this.controller.getMotorPower(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorPower : -motorPower);
    }

    public boolean isBusy() {
        return this.controller.isBusy(this.portNumber);
    }

    public void setPowerFloat() {
        this.controller.setMotorPowerFloat(this.portNumber);
    }

    public boolean getPowerFloat() {
        return this.controller.getMotorPowerFloat(this.portNumber);
    }

    public void setTargetPosition(int position) {
        if (this.direction == Direction.REVERSE) {
            position *= -1;
        }
        this.controller.setMotorTargetPosition(this.portNumber, position);
    }

    public int getTargetPosition() {
        int motorTargetPosition = this.controller.getMotorTargetPosition(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorTargetPosition : -motorTargetPosition);
    }

    public int getCurrentPosition() {
        int motorCurrentPosition = this.controller.getMotorCurrentPosition(this.portNumber);
        return ((this.direction == Direction.FORWARD) ? motorCurrentPosition : -motorCurrentPosition);
    }

    public void setMode(RunMode mode) {
        this.mode = mode;
        this.controller.setMotorChannelMode(this.portNumber, mode);
    }

    public RunMode getMode() {
        return this.controller.getMotorChannelMode(this.portNumber);
    }

    @Deprecated
    public void setChannelMode(RunMode mode) {
        setMode(mode);
    }

    @Deprecated
    public RunMode getChannelMode() {
        return getMode();
    }
}
