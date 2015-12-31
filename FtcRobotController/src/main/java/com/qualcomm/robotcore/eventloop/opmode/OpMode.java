package com.qualcomm.robotcore.eventloop.opmode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.concurrent.TimeUnit;

/**
 * Base class for user defined operation modes (op modes).
 */
public abstract class OpMode {
    private long startTime = System.nanoTime();
    /**
     * Gamepad 1
     */
    public Gamepad gamepad1 = new Gamepad();
    /**
     * Gamepad 2
     */
    public Gamepad gamepad2 = new Gamepad();
    /**
     * Hardware Mappings
     */
    public HardwareMap hardwareMap = new HardwareMap();
    /**
     * Telemetry Data
     */
    public Telemetry telemetry = new Telemetry();
    /**
     * number of seconds this op mode has been running, this is updated before every call to loop.
     */
    public double time;

    /**
     * User defined init method
     * <p/>
     * This method will be called once when the INIT button is pressed.
     */
    public abstract void init();

    /**
     * User defined loop method
     * <p/>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public abstract void loop();

    /**
     * OpMode constructor
     * <p/>
     * The op mode name should be unique. It will be the name displayed on the driver station. If multiple op modes have the same name, only one will be available.
     */
    public OpMode() {
    }

    /**
     * User defined init_loop method
     * <p/>
     * This method will be called repeatedly when the INIT button is pressed. This method is optional. By default this method takes no action.
     */
    public void init_loop() {
    }


    /**
     * User defined start method.
     * <p/>
     * This method will be called once when the PLAY button is first pressed. This method is optional. By default this method takes not action. Example usage: Starting another thread.
     */
    public void start() {
    }

    /**
     * User defined stop method
     * <p/>
     * This method will be called when this op mode is first disabled The stop method is optional. By default this method takes no action.
     */
    public void stop() {
    }

    /**
     * Get the number of seconds this op mode has been running
     * <p/>
     * This method has sub millisecond accuracy.
     *
     * @return number of seconds this op mode has been running
     */
    public double getRuntime() {
        return ((double) (System.nanoTime() - this.startTime)) / ((double) TimeUnit.SECONDS.toNanos(1));
    }

    /**
     * Reset the start time to zero.
     */
    public void resetStartTime() {
        this.startTime = System.nanoTime();
    }
}
