package com.qualcomm.robotcore.util;

public class ElapsedTime {
    private static final double TIME_IN_MILLI = 1.0E6d;
    private static final double TIME_IN_NANO = 1.0E9d;

    private long startTime;
    private double timeResolution = TIME_IN_NANO;

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    public ElapsedTime() {
        reset();
    }

    public ElapsedTime(long startTime) {
        this.startTime = startTime;
    }

    public ElapsedTime(Resolution resolution) {
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
        return (double) this.startTime / this.timeResolution;
    }

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

    public void log(String label) {
        RobotLog.v(String.format("TIMER: %20s - %1.3f %s", label, time(), timeResolutionToString()));
    }

    public String toString() {
        return String.format("%1.4f %s", time(), timeResolutionToString());
    }
}
