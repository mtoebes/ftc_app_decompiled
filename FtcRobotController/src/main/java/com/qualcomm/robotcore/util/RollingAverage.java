package com.qualcomm.robotcore.util;

import java.util.LinkedList;
import java.util.Queue;

public class RollingAverage {
    public static final int DEFAULT_SIZE = 100;
    private final Queue<Integer> f418a;
    private long f419b;
    private int f420c;

    public RollingAverage() {
        this.f418a = new LinkedList();
        resize(DEFAULT_SIZE);
    }

    public RollingAverage(int size) {
        this.f418a = new LinkedList();
        resize(size);
    }

    public int size() {
        return this.f420c;
    }

    public void resize(int size) {
        this.f420c = size;
        this.f418a.clear();
    }

    public void addNumber(int number) {
        if (this.f418a.size() >= this.f420c) {
            this.f419b -= (long) ((Integer) this.f418a.remove()).intValue();
        }
        this.f418a.add(Integer.valueOf(number));
        this.f419b += (long) number;
    }

    public int getAverage() {
        if (this.f418a.isEmpty()) {
            return 0;
        }
        return (int) (this.f419b / ((long) this.f418a.size()));
    }

    public void reset() {
        this.f418a.clear();
    }
}
