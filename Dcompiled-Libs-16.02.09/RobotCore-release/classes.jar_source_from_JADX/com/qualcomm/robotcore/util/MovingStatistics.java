package com.qualcomm.robotcore.util;

import java.util.LinkedList;
import java.util.Queue;

public class MovingStatistics {
    final Statistics f407a;
    final int f408b;
    final Queue<Double> f409c;

    public MovingStatistics(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("MovingStatistics capacity must be positive");
        }
        this.f407a = new Statistics();
        this.f408b = capacity;
        this.f409c = new LinkedList();
    }

    public int getCount() {
        return this.f407a.getCount();
    }

    public double getMean() {
        return this.f407a.getMean();
    }

    public double getVariance() {
        return this.f407a.getVariance();
    }

    public double getStandardDeviation() {
        return this.f407a.getStandardDeviation();
    }

    public void clear() {
        this.f407a.clear();
        this.f409c.clear();
    }

    public void add(double x) {
        this.f407a.add(x);
        this.f409c.add(Double.valueOf(x));
        if (this.f409c.size() > this.f408b) {
            this.f407a.remove(((Double) this.f409c.remove()).doubleValue());
        }
    }
}
