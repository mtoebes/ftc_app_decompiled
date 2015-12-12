package com.qualcomm.robotcore.util;

public class ElapsedTime {
    private static final double TIME_IN_MILLI = 1.0E6d;
    private static final double TIME_IN_NANO = 1.0E9d;

    private long startTime;
    private double timeResolution;

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    public ElapsedTime() {
        this.startTime = 0;
        this.timeResolution = TIME_IN_NANO;
        reset();
    }

    public ElapsedTime(long startTime) {
        this.startTime = 0;
        this.timeResolution = TIME_IN_NANO;
        this.startTime = startTime;
    }

    public ElapsedTime(Resolution resolution) {
        this.startTime = 0;
        this.timeResolution = TIME_IN_NANO;
        reset();
        switch (resolution) {
            case SECONDS :
                this.timeResolution = TIME_IN_NANO;
            case MILLISECONDS:
                this.timeResolution = TIME_IN_MILLI;
            default:
        }
    }

    public void reset() {
        this.startTime = System.nanoTime();
    }

    public double startTime() {
        return ((double) this.startTime) / this.timeResolution;
    }

    public double time() {
        return ((double) (System.nanoTime() - this.startTime)) / this.timeResolution;
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

    public void log(String label) {
        RobotLog.v(String.format("TIMER: %20s - %1.3f %s", new Object[]{label, Double.valueOf(time()), timeResolutionToString()}));
    }

    public String toString() {
        return String.format("%1.4f %s", new Object[]{Double.valueOf(time()), timeResolutionToString()});
    }
}
