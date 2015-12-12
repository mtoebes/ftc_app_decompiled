package com.qualcomm.robotcore.util;

import java.util.LinkedList;
import java.util.Queue;

public class RollingAverage {
    public static final int DEFAULT_SIZE = 100;
    private final Queue<Integer> queue = new LinkedList<Integer>();
    private long last;
    private int size;

    public RollingAverage() {
        resize(DEFAULT_SIZE);
    }

    public RollingAverage(int size) {
        resize(size);
    }

    public int size() {
        return this.size;
    }

    public void resize(int size) {
        this.size = size;
        this.queue.clear();
    }

    public void addNumber(int number) {
        if (this.queue.size() >= this.size) {
            this.last -= (long) this.queue.remove();
        }
        this.queue.add(number);
        this.last += (long) number;
    }

    public int getAverage() {
        if (this.queue.isEmpty()) {
            return 0;
        } else {
            return (int) (this.last / (long) this.queue.size());
        }
    }

    public void reset() {
        this.queue.clear();
    }
}
