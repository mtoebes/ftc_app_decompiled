package com.qualcomm.hardware.modernrobotics;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteRunnableSegment {
    final Lock f176a;
    final Lock f177b;
    private int f178c;
    private final byte[] f179d;
    private final byte[] f180e;

    public ReadWriteRunnableSegment(int address, int size) {
        this.f178c = address;
        this.f176a = new ReentrantLock();
        this.f179d = new byte[size];
        this.f177b = new ReentrantLock();
        this.f180e = new byte[size];
    }

    public int getAddress() {
        return this.f178c;
    }

    public void setAddress(int address) {
        this.f178c = address;
    }

    public Lock getReadLock() {
        return this.f176a;
    }

    public byte[] getReadBuffer() {
        return this.f179d;
    }

    public Lock getWriteLock() {
        return this.f177b;
    }

    public byte[] getWriteBuffer() {
        return this.f180e;
    }

    public String toString() {
        return String.format("Segment - address:%d read:%d write:%d", new Object[]{Integer.valueOf(this.f178c), Integer.valueOf(this.f179d.length), Integer.valueOf(this.f180e.length)});
    }
}
