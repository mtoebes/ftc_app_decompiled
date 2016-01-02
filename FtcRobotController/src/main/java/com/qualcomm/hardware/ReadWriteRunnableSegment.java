package com.qualcomm.hardware;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteRunnableSegment {
    final Lock f192a;
    final Lock f193b;
    private int f194c;
    private final byte[] f195d;
    private final byte[] f196e;

    public ReadWriteRunnableSegment(int address, int size) {
        this.f194c = address;
        this.f192a = new ReentrantLock();
        this.f195d = new byte[size];
        this.f193b = new ReentrantLock();
        this.f196e = new byte[size];
    }

    public int getAddress() {
        return this.f194c;
    }

    public void setAddress(int address) {
        this.f194c = address;
    }

    public Lock getReadLock() {
        return this.f192a;
    }

    public byte[] getReadBuffer() {
        return this.f195d;
    }

    public Lock getWriteLock() {
        return this.f193b;
    }

    public byte[] getWriteBuffer() {
        return this.f196e;
    }

    public String toString() {
        return String.format("Segment - address:%d read:%d write:%d", this.f194c, this.f195d.length, this.f196e.length);
    }
}
