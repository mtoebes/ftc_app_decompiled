package com.qualcomm.robotcore.util;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;

public class ElapsedTime {
    public static final double dMILLIS_IN_NANO = 1000000.0d;
    public static final double dSECOND_IN_NANO = 1.0E9d;
    public static final long lMILLIS_IN_NANO = 1000000;
    public static final long lSECOND_IN_NANO = 1000000000;
    private long f373a;
    private double f374b;

    /* renamed from: com.qualcomm.robotcore.util.ElapsedTime.1 */
    static /* synthetic */ class C00521 {
        static final /* synthetic */ int[] f371a;

        static {
            f371a = new int[Resolution.values().length];
            try {
                f371a[Resolution.SECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f371a[Resolution.MILLISECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum Resolution {
        SECONDS,
        MILLISECONDS
    }

    public ElapsedTime() {
        this.f373a = 0;
        this.f374b = dSECOND_IN_NANO;
        reset();
    }

    public ElapsedTime(long startTime) {
        this.f373a = 0;
        this.f374b = dSECOND_IN_NANO;
        this.f373a = startTime;
    }

    public ElapsedTime(Resolution resolution) {
        this.f373a = 0;
        this.f374b = dSECOND_IN_NANO;
        reset();
        switch (C00521.f371a[resolution.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                this.f374b = dSECOND_IN_NANO;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                this.f374b = dMILLIS_IN_NANO;
            default:
        }
    }

    public void reset() {
        this.f373a = System.nanoTime();
    }

    public double startTime() {
        return ((double) this.f373a) / this.f374b;
    }

    public double time() {
        return ((double) (System.nanoTime() - this.f373a)) / this.f374b;
    }

    private String m237a() {
        if (this.f374b == dSECOND_IN_NANO) {
            return "seconds";
        }
        if (this.f374b == dMILLIS_IN_NANO) {
            return "milliseconds";
        }
        return "Unknown units";
    }

    public void log(String label) {
        RobotLog.m254v(String.format("TIMER: %20s - %1.3f %s", new Object[]{label, Double.valueOf(time()), m237a()}));
    }

    public String toString() {
        return String.format("%1.4f %s", new Object[]{Double.valueOf(time()), m237a()});
    }
}
