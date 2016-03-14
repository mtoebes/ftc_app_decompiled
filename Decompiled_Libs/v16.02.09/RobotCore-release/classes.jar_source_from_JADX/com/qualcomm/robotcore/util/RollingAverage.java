package com.qualcomm.robotcore.util;

import java.util.LinkedList;
import java.util.Queue;

public class RollingAverage {
    public static final int DEFAULT_SIZE = 100;
    private final Queue<Integer> f420a;
    private long f421b;
    private int f422c;

    public RollingAverage() {
        this.f420a = new LinkedList();
        resize(DEFAULT_SIZE);
    }

    public RollingAverage(int size) {
        this.f420a = new LinkedList();
        resize(size);
    }

    public int size() {
        return this.f422c;
    }

    public void resize(int size) {
        this.f422c = size;
        this.f420a.clear();
    }

    public void addNumber(int number) {
        if (this.f420a.size() >= this.f422c) {
            this.f421b -= (long) ((Integer) this.f420a.remove()).intValue();
        }
        this.f420a.add(Integer.valueOf(number));
        this.f421b += (long) number;
    }

    public int getAverage() {
        if (this.f420a.isEmpty()) {
            return 0;
        }
        return (int) (this.f421b / ((long) this.f420a.size()));
    }

    public void reset() {
        this.f420a.clear();
    }
}
