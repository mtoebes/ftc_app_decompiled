package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.Range;

public class Servo implements HardwareDevice {
    public static final double MAX_POSITION = 1;
    public static final double MIN_POSITION = 0;
    protected ServoController controller;
    protected Direction direction;
    protected double maxPosition = MAX_POSITION;
    protected double minPosition = MIN_POSITION;
    protected int portNumber;

    public enum Direction {
        FORWARD,
        REVERSE
    }

    public Servo(ServoController controller, int portNumber) {
        this(controller, portNumber, Direction.FORWARD);
    }

    public Servo(ServoController controller, int portNumber, Direction direction) {
        this.direction = direction;
        this.controller = controller;
        this.portNumber = portNumber;
    }

    public String getDeviceName() {
        return "Servo";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; port " + this.portNumber;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    public ServoController getController() {
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

    public void setPosition(double position) {
        if (this.direction == Direction.REVERSE) {
            position = reversePosition(position);
        }
        this.controller.setServoPosition(this.portNumber, Range.scale(position, 0, MAX_POSITION, this.minPosition, this.maxPosition));
    }

    public double getPosition() {
        double servoPosition = this.controller.getServoPosition(this.portNumber);
        if (this.direction == Direction.REVERSE) {
            servoPosition = reversePosition(servoPosition);
        }
        return Range.clip(Range.scale(servoPosition, this.minPosition, this.maxPosition, 0, MAX_POSITION), 0, MAX_POSITION);
    }

    public void scaleRange(double min, double max) throws IllegalArgumentException {
        Range.throwIfRangeIsInvalid(min, 0, MAX_POSITION);
        Range.throwIfRangeIsInvalid(max, 0, MAX_POSITION);
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        this.minPosition = min;
        this.maxPosition = max;
    }

    private double reversePosition(double d) {
        return MAX_POSITION - d;
    }
}
