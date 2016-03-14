package com.qualcomm.robotcore.util;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;

public class ElapsedTime {
    private long f377a;
    private double f378b;

    /* renamed from: com.qualcomm.robotcore.util.ElapsedTime.1 */
    static /* synthetic */ class C00471 {
        static final /* synthetic */ int[] f375a;

        static {
            f375a = new int[Resolution.values().length];
            try {
                f375a[Resolution.SECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f375a[Resolution.MILLISECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    public ElapsedTime() {
        this.f377a = 0;
        this.f378b = 1.0E9d;
        reset();
    }

    public ElapsedTime(long startTime) {
        this.f377a = 0;
        this.f378b = 1.0E9d;
        this.f377a = startTime;
    }

    public ElapsedTime(Resolution resolution) {
        this.f377a = 0;
        this.f378b = 1.0E9d;
        reset();
        switch (C00471.f375a[resolution.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                this.f378b = 1.0E9d;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                this.f378b = 1000000.0d;
            default:
        }
    }

    public void reset() {
        this.f377a = System.nanoTime();
    }

    public double startTime() {
        return ((double) this.f377a) / this.f378b;
    }

    public double time() {
        return ((double) (System.nanoTime() - this.f377a)) / this.f378b;
    }

    private String m219a() {
        if (this.f378b == 1.0E9d) {
            return "seconds";
        }
        if (this.f378b == 1000000.0d) {
            return "milliseconds";
        }
        return "Unknown units";
    }

    public void log(String label) {
        RobotLog.m233v(String.format("TIMER: %20s - %1.3f %s", new Object[]{label, Double.valueOf(time()), m219a()}));
    }

    public String toString() {
        return String.format("%1.4f %s", new Object[]{Double.valueOf(time()), m219a()});
    }
}
