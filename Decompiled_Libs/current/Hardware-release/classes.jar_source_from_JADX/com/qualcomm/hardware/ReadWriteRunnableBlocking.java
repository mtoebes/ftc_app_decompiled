package com.qualcomm.hardware;

import com.qualcomm.hardware.ReadWriteRunnable.BlockingState;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteRunnableBlocking extends ReadWriteRunnableStandard {
    private volatile boolean f191a;
    protected final Condition blockingCondition;
    protected final Lock blockingLock;
    protected BlockingState blockingState;
    protected final Condition waitingCondition;
    protected final Lock waitingLock;

    public ReadWriteRunnableBlocking(SerialNumber serialNumber, RobotUsbDevice device, int monitorLength, int startAddress, boolean debug) {
        super(serialNumber, device, monitorLength, startAddress, debug);
        this.blockingLock = new ReentrantLock();
        this.waitingLock = new ReentrantLock();
        this.blockingCondition = this.blockingLock.newCondition();
        this.waitingCondition = this.waitingLock.newCondition();
        this.blockingState = BlockingState.BLOCKING;
        this.f191a = false;
    }

    public void blockUntilReady() throws RobotCoreException, InterruptedException {
        try {
            this.blockingLock.lock();
            while (this.blockingState == BlockingState.BLOCKING) {
                this.blockingCondition.await(100, TimeUnit.MILLISECONDS);
                if (this.shutdownComplete) {
                    RobotLog.w("sync device block requested, but device is shut down - " + this.serialNumber);
                    RobotLog.setGlobalErrorMsg("There were problems communicating with a Modern Robotics USB device for an extended period of time.");
                    throw new RobotCoreException("cannot block, device is shut down");
                }
            }
        } finally {
            this.blockingLock.unlock();
        }
    }

    public void startBlockingWork() {
        try {
            this.waitingLock.lock();
            this.blockingState = BlockingState.BLOCKING;
            this.waitingCondition.signalAll();
        } finally {
            this.waitingLock.unlock();
        }
    }

    public void write(int address, byte[] data) {
        synchronized (this.localDeviceWriteCache) {
            System.arraycopy(data, 0, this.localDeviceWriteCache, address, data.length);
            this.f191a = true;
        }
    }

    public boolean writeNeeded() {
        return this.f191a;
    }

    public void setWriteNeeded(boolean set) {
        this.f191a = set;
    }

    protected void waitForSyncdEvents() throws RobotCoreException, InterruptedException {
        try {
            this.blockingLock.lock();
            this.blockingState = BlockingState.WAITING;
            this.blockingCondition.signalAll();
            try {
                this.waitingLock.lock();
                while (this.blockingState == BlockingState.WAITING) {
                    this.waitingCondition.await();
                    if (this.shutdownComplete) {
                        RobotLog.w("wait for sync'd events requested, but device is shut down - " + this.serialNumber);
                        throw new RobotCoreException("cannot block, device is shut down");
                    }
                }
            } finally {
                this.waitingLock.unlock();
            }
        } finally {
            this.blockingLock.unlock();
        }
    }
}
