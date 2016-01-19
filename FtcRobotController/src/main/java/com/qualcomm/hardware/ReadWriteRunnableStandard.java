package com.qualcomm.hardware;

import com.qualcomm.modernrobotics.ReadWriteRunnableUsbHandler;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;

public class ReadWriteRunnableStandard implements ReadWriteRunnable {
    protected final boolean DEBUG_LOGGING;
    private volatile boolean writeNeeded = false;
    protected Callback callback = new EmptyCallback();
    protected final byte[] localDeviceReadCache = new byte[256];
    protected final byte[] localDeviceWriteCache = new byte[256];
    protected int monitorLength;
    protected volatile boolean running = false;
    protected ConcurrentLinkedQueue<Integer> segmentReadQueue = new ConcurrentLinkedQueue<Integer>();
    protected ConcurrentLinkedQueue<Integer> segmentWriteQueue = new ConcurrentLinkedQueue<Integer>();
    protected Map<Integer, ReadWriteRunnableSegment> segments =  new HashMap<Integer, ReadWriteRunnableSegment>();
    protected final SerialNumber serialNumber;
    protected volatile boolean shutdownComplete = false;
    protected int startAddress;
    protected final ReadWriteRunnableUsbHandler usbHandler;

    public void close() {
        try {
            this.blockUntilReady();
            this.startBlockingWork();
        } catch (InterruptedException e) {
            RobotLog.w("Exception while closing USB device: " + e.getMessage());
        } catch (RobotCoreException e) {
            RobotLog.w("Exception while closing USB device: " + e.getMessage());
        } finally {
            this.running = false;
            while(!this.shutdownComplete) {
                Thread.yield();
            }
        }
    }

    public ReadWriteRunnableStandard(SerialNumber serialNumber, RobotUsbDevice device, int monitorLength, int startAddress, boolean debug) {
        this.serialNumber = serialNumber;
        this.startAddress = startAddress;
        this.monitorLength = monitorLength;
        this.DEBUG_LOGGING = debug;
        this.usbHandler = new ReadWriteRunnableUsbHandler(device);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void blockUntilReady() throws RobotCoreException, InterruptedException {
        if (this.shutdownComplete) {
            RobotLog.w("sync device block requested, but device is shut down - " + this.serialNumber);
            RobotLog.setGlobalErrorMsg("There were problems communicating with a Modern Robotics USB device for an extended period of time.");
            throw new RobotCoreException("cannot block, device is shut down");
        }
    }

    public void startBlockingWork() {
    }

    public boolean writeNeeded() {
        return this.writeNeeded;
    }

    public void setWriteNeeded(boolean set) {
        this.writeNeeded = set;
    }

    public void write(int address, byte[] data) {
        synchronized (this.localDeviceWriteCache) {
            System.arraycopy(data, 0, this.localDeviceWriteCache, address, data.length);
            this.writeNeeded = true;
        }
    }

    public byte[] readFromWriteCache(int address, int size) {
        byte[] copyOfRange;
        synchronized (this.localDeviceWriteCache) {
            copyOfRange = Arrays.copyOfRange(this.localDeviceWriteCache, address, address + size);
        }
        return copyOfRange;
    }

    public byte[] read(int address, int size) {
        byte[] copyOfRange;
        synchronized (this.localDeviceReadCache) {
            copyOfRange = Arrays.copyOfRange(this.localDeviceReadCache, address, address + size);
        }
        return copyOfRange;
    }

    public ReadWriteRunnableSegment createSegment(int key, int address, int size) {
        ReadWriteRunnableSegment readWriteRunnableSegment = new ReadWriteRunnableSegment(address, size);
        this.segments.put(key, readWriteRunnableSegment);
        return readWriteRunnableSegment;
    }

    public void destroySegment(int key) {
        this.segments.remove(key);
    }

    public ReadWriteRunnableSegment getSegment(int key) {
        return this.segments.get(key);
    }

    public void queueSegmentRead(int key) {
        queueIfNotAlreadyQueued(key, this.segmentReadQueue);
    }

    public void queueSegmentWrite(int key) {
        queueIfNotAlreadyQueued(key, this.segmentWriteQueue);
    }

    public void run() {

        boolean var1 = true;
        int tempStartAddress = 0;
        byte[] tempBuffer = new byte[this.monitorLength + this.startAddress];
        ElapsedTime var4 = new ElapsedTime();
        String var5 = "Device " + this.serialNumber.toString();
        this.running = true;
        RobotLog.v(String.format("starting read/write loop for device %s", this.serialNumber));

        try {
            this.usbHandler.purge(Channel.RX);

            while(this.running) {
                if(this.DEBUG_LOGGING) {
                    var4.log(var5);
                    var4.reset();
                }

                ReadWriteRunnableSegment var6;
                byte[] var7;
                try {
                    this.usbHandler.read(tempStartAddress, tempBuffer);

                    while(!this.segmentReadQueue.isEmpty()) {
                        var6 = (ReadWriteRunnableSegment)this.segments.get(this.segmentReadQueue.remove());
                        var7 = new byte[var6.getReadBuffer().length];
                        this.usbHandler.read(var6.getAddress(), var7);

                        try {
                            var6.getReadLock().lock();
                            System.arraycopy(var7, 0, var6.getReadBuffer(), 0, var6.getReadBuffer().length);
                        } finally {
                            var6.getReadLock().unlock();
                        }
                    }
                } catch (RobotCoreException var47) {
                    RobotLog.w(String.format("could not read from device %s: %s", new Object[]{this.serialNumber, var47.getMessage()}));
                }

                byte[] var53 = this.localDeviceReadCache;
                synchronized(this.localDeviceReadCache) {
                    System.arraycopy(tempBuffer, 0, this.localDeviceReadCache, tempStartAddress, tempBuffer.length);
                }

                if(this.DEBUG_LOGGING) {
                    this.dumpBuffers("read", this.localDeviceReadCache);
                }

                this.callback.readComplete();
                this.waitForSyncdEvents();
                if(var1) {
                    tempStartAddress = this.startAddress;
                    tempBuffer = new byte[this.monitorLength];
                    var1 = false;
                }

                var53 = this.localDeviceWriteCache;
                synchronized(this.localDeviceWriteCache) {
                    System.arraycopy(this.localDeviceWriteCache, tempStartAddress, tempBuffer, 0, tempBuffer.length);
                }

                try {
                    if(this.writeNeeded()) {
                        this.usbHandler.write(tempStartAddress, tempBuffer);
                        this.setWriteNeeded(false);
                    }

                    for(; !this.segmentWriteQueue.isEmpty(); this.usbHandler.write(var6.getAddress(), var7)) {
                        var6 = (ReadWriteRunnableSegment)this.segments.get(this.segmentWriteQueue.remove());

                        try {
                            var6.getWriteLock().lock();
                            var7 = Arrays.copyOf(var6.getWriteBuffer(), var6.getWriteBuffer().length);
                        } finally {
                            var6.getWriteLock().unlock();
                        }
                    }
                } catch (RobotCoreException var48) {
                    RobotLog.w(String.format("could not write to device %s: %s", this.serialNumber, var48.getMessage()));
                }

                if(this.DEBUG_LOGGING) {
                    this.dumpBuffers("write", this.localDeviceWriteCache);
                }

                this.callback.writeComplete();
                this.usbHandler.throwIfUsbErrorCountIsTooHigh();
            }
        } catch (NullPointerException var49) {
            RobotLog.w(String.format("could not write to device %s: FTDI Null Pointer Exception", this.serialNumber));
            RobotLog.logStacktrace(var49);
            RobotLog.setGlobalErrorMsg("There was a problem communicating with a Modern Robotics USB device");
        } catch (InterruptedException var50) {
            RobotLog.w(String.format("could not write to device %s: Interrupted Exception", this.serialNumber));
        } catch (RobotCoreException var51) {
            RobotLog.w(var51.getMessage());
            RobotLog.setGlobalErrorMsg(String.format("There was a problem communicating with a Modern Robotics USB device %s", this.serialNumber));
        } finally {
            this.usbHandler.close();
            this.running = false;
            this.shutdownComplete = true;
        }

        RobotLog.v(String.format("stopped read/write loop for device %s", this.serialNumber));
    }

    protected void waitForSyncdEvents() throws RobotCoreException, InterruptedException {
    }

    protected void dumpBuffers(String name, byte[] byteArray) {
        RobotLog.v("Dumping " + name + " buffers for " + this.serialNumber);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (int i = 0; i < this.startAddress + this.monitorLength; i++) {
            stringBuilder.append(String.format(" %02x", new Object[]{TypeConversion.unsignedByteToInt(byteArray[i])}));
            if ((i + 1) % 16 == 0) {
                stringBuilder.append("\n");
            }
        }
        RobotLog.v(stringBuilder.toString());
    }

    protected void queueIfNotAlreadyQueued(int key, ConcurrentLinkedQueue<Integer> queue) {
        if (!queue.contains(Integer.valueOf(key))) {
            queue.add(key);
        }
    }
}
