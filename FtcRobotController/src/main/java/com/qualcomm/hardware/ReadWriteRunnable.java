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

    ReadWriteRunnableSegment createSegment(int i, int i2, int i3);

    void queueSegmentRead(int i);

    void queueSegmentWrite(int i);

    byte[] read(int i, int i2);

    byte[] readFromWriteCache(int i, int i2);

    void setCallback(Callback callback);

    void write(int i, byte[] bArr);
}
