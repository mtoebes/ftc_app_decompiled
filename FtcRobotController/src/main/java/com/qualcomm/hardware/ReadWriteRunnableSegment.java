package com.qualcomm.hardware;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteRunnableSegment {
    final Lock readLock;
    final Lock writeLock;
    private int address;
    private final byte[] readBuffer;
    private final byte[] writeBuffer;

    public ReadWriteRunnableSegment(int address, int size) {
        this.address = address;
        this.readLock = new ReentrantLock();
        this.readBuffer = new byte[size];
        this.writeLock = new ReentrantLock();
        this.writeBuffer = new byte[size];
    }

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public Lock getReadLock() {
        return this.readLock;
    }

    public byte[] getReadBuffer() {
        return this.readBuffer;
    }

    public Lock getWriteLock() {
        return this.writeLock;
    }

    public byte[] getWriteBuffer() {
        return this.writeBuffer;
    }

    public String toString() {
        return String.format("Segment - address:%d read:%d write:%d", this.address, this.readBuffer.length, this.writeBuffer.length);
    }
}
