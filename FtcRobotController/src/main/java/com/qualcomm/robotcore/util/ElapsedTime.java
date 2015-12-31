package com.qualcomm.robotcore.util;

/**
 * Measure elapsed time
 * <p/>
 * Does not measure deep sleep. Nanosecond accuracy.
 */
//TODO check if setting resolution correctly (may be backwards?)
public class ElapsedTime {
    private static final double TIME_IN_MILLI = 1.0E6d;
    private static final double TIME_IN_NANO = 1.0E9d;

    private long startTime;
    private double timeResolution = TIME_IN_NANO;

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    /**
     * Constructor
     * <p/>
     * Starts the timer
     */
    public ElapsedTime() {
        reset();
    }

    /**
     * Constructor
     * <p/>
     * Starts timer with a pre-set time
     *
     * @param startTime pre-set time
     */
    public ElapsedTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * /**
     * Constructor
     * <p/>
     * Starts timer with a given time resolution
     *
     * @param resolution time resolution
     */
    public ElapsedTime(Resolution resolution) {
        reset();
        switch (resolution) {
            case SECONDS:
                this.timeResolution = TIME_IN_NANO;
            case MILLISECONDS:
                this.timeResolution = TIME_IN_MILLI;
            default:
        }
    }

    /**
     * Reset the start time to now
     */
    public void reset() {
        this.startTime = System.nanoTime();
    }

    /**
     * Get the relative start time
     * <p/>
     * Nanosecond accuracy.
     *
     * @return relative start time
     */
    public double startTime() {
        return (double) this.startTime / this.timeResolution;
    }

    /**
     * How many seconds since the start time.
     * <p/>
     * Nanosecond accuracy.
     *
     * @return time
     */
    public double time() {
        return (double) (System.nanoTime() - this.startTime) / this.timeResolution;
    }

    private String timeResolutionToString() {
        if (this.timeResolution == TIME_IN_NANO) {
            return "seconds";
        }
        if (this.timeResolution == TIME_IN_MILLI) {
            return "milliseconds";
        }
        return "Unknown units";
    }

    /**
     * Log a message stating how long the timer has been running
     *
     * @param label message to log
     */
    public void log(String label) {
        RobotLog.v(String.format("TIMER: %20s - %1.3f %s", label, time(), timeResolutionToString()));
    }

    @Override
    /**
     Return a string stating the number of seconds that have passed
     * @return string
     */
    public String toString() {
        return String.format("%1.4f %s", time(), timeResolutionToString());
    }
}
