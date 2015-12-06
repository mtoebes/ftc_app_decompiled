package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;

public class DcMotor implements HardwareDevice {
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
        return "DC Motor";
    }

    public String getConnectionInfo() {
        return controller.getConnectionInfo() + "; port " + portNumber;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        setPowerFloat();
    }

    public DcMotorController getController() {
        return controller;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPower(double power) {
        if (direction == Direction.REVERSE) {
            power *= -1.0d;
        }
        if (mode == RunMode.RUN_TO_POSITION) {
            power = Math.abs(power);
        }
        controller.setMotorPower(portNumber, power);
    }

    public double getPower() {
        double motorPower = controller.getMotorPower(portNumber);
        if (direction != Direction.REVERSE || motorPower == 0.0d) {
            return motorPower;
        }
        return motorPower * -1.0d;
    }

    public boolean isBusy() {
        return controller.isBusy(portNumber);
    }

    public void setPowerFloat() {
        controller.setMotorPowerFloat(portNumber);
    }

    public boolean getPowerFloat() {
        return controller.getMotorPowerFloat(portNumber);
    }

    public void setTargetPosition(int position) {
        if (direction == Direction.REVERSE) {
            position *= -1;
        }
        controller.setMotorTargetPosition(portNumber, position);
    }

    public int getTargetPosition() {
        int motorTargetPosition = controller.getMotorTargetPosition(portNumber);
        if (direction == Direction.REVERSE) {
            return motorTargetPosition * -1;
        }
        return motorTargetPosition;
    }

    public int getCurrentPosition() {
        int motorCurrentPosition = controller.getMotorCurrentPosition(portNumber);
        if (direction == Direction.REVERSE) {
            return motorCurrentPosition * -1;
        }
        return motorCurrentPosition;
    }

    public void setMode(RunMode mode) {
        this.mode = mode;
        controller.setMotorChannelMode(portNumber, mode);
    }

    public RunMode getMode() {
        return controller.getMotorChannelMode(portNumber);
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
