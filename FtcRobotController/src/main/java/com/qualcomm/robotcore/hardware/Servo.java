package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.Range;

/**
 * Control a single servo
 */
public class Servo implements HardwareDevice {
    private static final String DEVICE_NAME = "Servo";
    private static final int VERSION = 1;

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

    /**
     * Constructor
     *
     * @param controller Servo controller that this servo is attached to
     * @param portNumber physical port number on the servo controller
     */
    public Servo(ServoController controller, int portNumber) {
        this(controller, portNumber, Direction.FORWARD);
    }

    /**
     * Constructor
     *
     * @param controller Servo controller that this servo is attached to
     * @param portNumber physical port number on the servo controller
     * @param direction  FORWARD for normal operation, REVERSE to reverse operation
     */
    public Servo(ServoController controller, int portNumber, Direction direction) {
        this.direction = direction;
        this.controller = controller;
        this.portNumber = portNumber;
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
    }

    /**
     * Get Servo Controller
     *
     * @return servo controller
     */
    public ServoController getController() {
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
     * Get Channel
     *
     * @return channel
     */
    public int getPortNumber() {
        return this.portNumber;
    }

    /**
     * Set the position of the servo
     *
     * @param position from 0.0 to 1.0
     */
    public void setPosition(double position) {
        if (this.direction == Direction.REVERSE) {
            position = reversePosition(position);
        }
        this.controller.setServoPosition(this.portNumber, Range.scale(position, 0, MAX_POSITION, this.minPosition, this.maxPosition));
    }

    /**
     * Get the position of the servo
     *
     * @return position, scaled from 0.0 to 1.0
     */
    public double getPosition() {
        double servoPosition = this.controller.getServoPosition(this.portNumber);
        if (this.direction == Direction.REVERSE) {
            servoPosition = reversePosition(servoPosition);
        }
        return Range.clip(Range.scale(servoPosition, this.minPosition, this.maxPosition, 0, MAX_POSITION), 0, MAX_POSITION);
    }

    /**
     * Automatically scale the position of the servo.
     * <p/>
     * For example, if scaleRange(0.2, 0.8) is set; then servo positions will be scaled to fit in that range.
     * <ul>
     * <li> setPosition(0.0) scales to 0.2
     * <li> setPosition(1.0) scales to 0.8
     * <li> setPosition(0.5) scales to 0.5
     * <li> setPosition(0.25) scales to 0.35
     * <li> setPosition(0.75) scales to 0.65
     * </ul>
     * This is useful if you don't want the servo to move past a given position, but don't want to manually scale the input to setPosition each time. getPosition() will scale the value back to a value between 0.0 and 1.0. If you need to know the actual position use Servo.getController().getServoPosition(Servo.getChannel()).
     *
     * @param min minimum position of the servo from 0.0 to 1.0
     * @param max maximum position of the servo from 0.0 to 1.0
     * @throws IllegalArgumentException if out of bounds, or min >= max
     */
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
