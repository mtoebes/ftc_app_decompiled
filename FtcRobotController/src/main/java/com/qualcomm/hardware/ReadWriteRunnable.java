package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.exception.RobotCoreException;

interface ReadWriteRunnable extends SyncdDevice, Runnable {

    interface Callback {
        void readComplete() throws InterruptedException;

        void writeComplete() throws InterruptedException;
    }

    enum BlockingState {
        BLOCKING,
        WAITING
    }

    class EmptyCallback implements Callback {
        public void readComplete() throws InterruptedException {
        }

        public void writeComplete() throws InterruptedException {
        }
    }

    void blockUntilReady() throws RobotCoreException, InterruptedException;

    void close();

    ReadWriteRunnableSegment createSegment(int key, int address, int size);

    void queueSegmentRead(int key);

    void queueSegmentWrite(int key);

    byte[] read(int address, int size);

    byte[] readFromWriteCache(int address, int size);

    void setCallback(Callback callback);

    void write(int address, byte[] data);
}
