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
        return size;
    }

    public void resize(int size) {
        this.size = size;
        queue.clear();
    }

    public void addNumber(int number) {
        if (queue.size() >= size) {
            last -= (long) (queue.remove());
        }
        queue.add(number);
        last += (long) number;
    }

    public int getAverage() {
        if (queue.isEmpty()) {
            return 0;
        } else {
            return (int) (last / queue.size());
        }
    }

    public void reset() {
        queue.clear();
    }
}
