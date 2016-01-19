package com.qualcomm.hardware;

import com.qualcomm.hardware.ReadWriteRunnable.Callback;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ModernRoboticsUsbDevice implements Callback {
    protected ReadWriteRunnable readWriteRunnable;
    protected ExecutorService readWriteService;
    protected SerialNumber serialNumber;

    public abstract String getDeviceName();

    public ModernRoboticsUsbDevice(SerialNumber serialNumber, EventLoopManager manager, ReadWriteRunnable readWriteRunnable) throws RobotCoreException, InterruptedException {
        this.readWriteService = Executors.newSingleThreadExecutor();
        this.serialNumber = serialNumber;
        this.readWriteRunnable = readWriteRunnable;
        RobotLog.v("Starting up device " + serialNumber.toString());
        this.readWriteService.execute(readWriteRunnable);
        readWriteRunnable.blockUntilReady();
        readWriteRunnable.setCallback(this);
        manager.registerSyncdDevice(readWriteRunnable);
    }

    public SerialNumber getSerialNumber() {
        return this.serialNumber;
    }

    public int getVersion() {
        return read(0);
    }

    public void close() {
        RobotLog.v("Shutting down device " + this.serialNumber.toString());
        this.readWriteService.shutdown();
        this.readWriteRunnable.close();
    }

    public void write(int address, byte data) {
        write(address, new byte[]{data});
    }

    public void write(int address, int data) {
        write(address, new byte[]{(byte) data});
    }

    public void write(int address, double data) {
        write(address, new byte[]{(byte) ((int) data)});
    }

    public void write(int address, byte[] data) {
        this.readWriteRunnable.write(address, data);
    }

    public byte readFromWriteCache(int address) {
        return readFromWriteCache(address, 1)[0];
    }

    public byte[] readFromWriteCache(int address, int size) {
        return this.readWriteRunnable.readFromWriteCache(address, size);
    }

    public byte read(int address) {
        return read(address, 1)[0];
    }

    public byte[] read(int address, int size) {
        return this.readWriteRunnable.read(address, size);
    }

    public void readComplete() throws InterruptedException {
    }

    public void writeComplete() throws InterruptedException {
    }
}
