package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.Range;

public class Servo implements HardwareDevice {
    public static final double MAX_POSITION = 1.0d;
    public static final double MIN_POSITION = 0.0d;
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
        return controller.getConnectionInfo() + "; port " + portNumber;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }

    public ServoController getController() {
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

    public void setPosition(double position) {
        if (direction == Direction.REVERSE) {
            position = getReversePosition(position);
        }
        controller.setServoPosition(portNumber, Range.scale(position, MIN_POSITION, MAX_POSITION, minPosition, maxPosition));
    }

    public double getPosition() {
        double servoPosition = controller.getServoPosition(portNumber);
        if (direction == Direction.REVERSE) {
            servoPosition = getReversePosition(servoPosition);
        }
        return Range.clip(Range.scale(servoPosition, minPosition, maxPosition, MIN_POSITION, MAX_POSITION), MIN_POSITION, MAX_POSITION);
    }

    public void scaleRange(double min, double max) throws IllegalArgumentException {
        Range.throwIfRangeIsInvalid(min, MIN_POSITION, MAX_POSITION);
        Range.throwIfRangeIsInvalid(max, MIN_POSITION, MAX_POSITION);
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        minPosition = min;
        maxPosition = max;
    }

    private double getReversePosition(double position) {
        return (MAX_POSITION - position) + 0.0d;
    }
}
