package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.exception.RobotCoreException;
import java.util.concurrent.ExecutorService;

public interface ReadWriteRunnable extends SyncdDevice, Runnable {
    public static final int MAX_BUFFER_SIZE = 256;

    public interface Callback {
        void readComplete() throws InterruptedException;

        void shutdownComplete() throws InterruptedException;

        void startupComplete() throws InterruptedException;

        void writeComplete() throws InterruptedException;
    }

    public enum BlockingState {
        BLOCKING,
        WAITING
    }

    public static class EmptyCallback implements Callback {
        public void startupComplete() throws InterruptedException {
        }

        public void readComplete() throws InterruptedException {
        }

        public void writeComplete() throws InterruptedException {
        }

        public void shutdownComplete() throws InterruptedException {
        }
    }

    void blockUntilReady() throws RobotCoreException, InterruptedException;

    void close();

    ReadWriteRunnableSegment createSegment(int i, int i2, int i3);

    void destroySegment(int i);

    void drainPendingWrites();

    void executeUsing(ExecutorService executorService);

    boolean getAcceptingWrites();

    ReadWriteRunnableSegment getSegment(int i);

    void queueSegmentRead(int i);

    void queueSegmentWrite(int i);

    byte[] read(int i, int i2);

    byte[] readFromWriteCache(int i, int i2);

    void resetWriteNeeded();

    void run();

    void setAcceptingWrites(boolean z);

    void setCallback(Callback callback);

    void startBlockingWork();

    void write(int i, byte[] bArr);

    boolean writeNeeded();
}
