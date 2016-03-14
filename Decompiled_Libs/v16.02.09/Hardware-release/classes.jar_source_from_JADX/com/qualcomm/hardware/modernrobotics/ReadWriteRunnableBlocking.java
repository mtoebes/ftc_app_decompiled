package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.hardware.modernrobotics.ReadWriteRunnable.BlockingState;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteRunnableBlocking extends ReadWriteRunnableStandard {
    protected final Condition blockingCondition;
    protected final Lock blockingLock;
    protected BlockingState blockingState;
    protected final Condition waitingCondition;
    protected final Lock waitingLock;

    public ReadWriteRunnableBlocking(Context context, SerialNumber serialNumber, RobotUsbDevice device, int monitorLength, int startAddress, boolean debug) {
        super(context, serialNumber, device, monitorLength, startAddress, debug);
        this.blockingLock = new ReentrantLock();
        this.waitingLock = new ReentrantLock();
        this.blockingCondition = this.blockingLock.newCondition();
        this.waitingCondition = this.waitingLock.newCondition();
        this.blockingState = BlockingState.BLOCKING;
    }

    public void blockUntilReady() throws RobotCoreException, InterruptedException {
        try {
            this.blockingLock.lock();
            while (this.blockingState == BlockingState.BLOCKING && !this.shutdownComplete) {
                this.blockingCondition.await(100, TimeUnit.MILLISECONDS);
            }
            this.blockingLock.unlock();
        } catch (Throwable th) {
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
                        RobotLog.w("wait for sync'd events requested, but device is shut down: %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(this.serialNumber)});
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
