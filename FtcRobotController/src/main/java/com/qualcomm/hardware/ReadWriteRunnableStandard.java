package com.qualcomm.hardware;

import com.qualcomm.hardware.ReadWriteRunnable.Callback;
import com.qualcomm.hardware.ReadWriteRunnable.EmptyCallback;
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
    private volatile boolean f190a;
    protected Callback callback;
    protected final byte[] localDeviceReadCache;
    protected final byte[] localDeviceWriteCache;
    protected int monitorLength;
    protected volatile boolean running;
    protected ConcurrentLinkedQueue<Integer> segmentReadQueue;
    protected ConcurrentLinkedQueue<Integer> segmentWriteQueue;
    protected Map<Integer, ReadWriteRunnableSegment> segments;
    protected final SerialNumber serialNumber;
    protected volatile boolean shutdownComplete;
    protected int startAddress;
    protected final ReadWriteRunnableUsbHandler usbHandler;

    public void close() {
        try {
            this.blockUntilReady();
            this.startBlockingWork();
        } catch (InterruptedException var6) {
            RobotLog.w("Exception while closing USB device: " + var6.getMessage());
        } catch (RobotCoreException var7) {
            RobotLog.w("Exception while closing USB device: " + var7.getMessage());
        } finally {
            this.running = false;

            while(!this.shutdownComplete) {
                Thread.yield();
            }

        }
    }

    public ReadWriteRunnableStandard(SerialNumber serialNumber, RobotUsbDevice device, int monitorLength, int startAddress, boolean debug) {
        this.localDeviceReadCache = new byte[256];
        this.localDeviceWriteCache = new byte[256];
        this.segments = new HashMap<Integer, ReadWriteRunnableSegment>();
        this.segmentReadQueue = new ConcurrentLinkedQueue<Integer>();
        this.segmentWriteQueue = new ConcurrentLinkedQueue<Integer>();
        this.running = false;
        this.shutdownComplete = false;
        this.f190a = false;
        this.serialNumber = serialNumber;
        this.startAddress = startAddress;
        this.monitorLength = monitorLength;
        this.DEBUG_LOGGING = debug;
        this.callback = new EmptyCallback();
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
        return this.f190a;
    }

    public void setWriteNeeded(boolean set) {
        this.f190a = set;
    }

    public void write(int address, byte[] data) {
        synchronized (this.localDeviceWriteCache) {
            System.arraycopy(data, 0, this.localDeviceWriteCache, address, data.length);
            this.f190a = true;
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
        int var2 = 0;
        byte[] var3 = new byte[this.monitorLength + this.startAddress];
        ElapsedTime var4 = new ElapsedTime();
        String var5 = "Device " + this.serialNumber.toString();
        this.running = true;
        RobotLog.v(String.format("starting read/write loop for device %s", new Object[]{this.serialNumber}));

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
                    this.usbHandler.read(var2, var3);

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
                    System.arraycopy(var3, 0, this.localDeviceReadCache, var2, var3.length);
                }

                if(this.DEBUG_LOGGING) {
                    this.dumpBuffers("read", this.localDeviceReadCache);
                }

                this.callback.readComplete();
                this.waitForSyncdEvents();
                if(var1) {
                    var2 = this.startAddress;
                    var3 = new byte[this.monitorLength];
                    var1 = false;
                }

                var53 = this.localDeviceWriteCache;
                synchronized(this.localDeviceWriteCache) {
                    System.arraycopy(this.localDeviceWriteCache, var2, var3, 0, var3.length);
                }

                try {
                    if(this.writeNeeded()) {
                        this.usbHandler.write(var2, var3);
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
                    RobotLog.w(String.format("could not write to device %s: %s", new Object[]{this.serialNumber, var48.getMessage()}));
                }

                if(this.DEBUG_LOGGING) {
                    this.dumpBuffers("write", this.localDeviceWriteCache);
                }

                this.callback.writeComplete();
                this.usbHandler.throwIfUsbErrorCountIsTooHigh();
            }
        } catch (NullPointerException var49) {
            RobotLog.w(String.format("could not write to device %s: FTDI Null Pointer Exception", new Object[]{this.serialNumber}));
            RobotLog.logStacktrace(var49);
            RobotLog.setGlobalErrorMsg("There was a problem communicating with a Modern Robotics USB device");
        } catch (InterruptedException var50) {
            RobotLog.w(String.format("could not write to device %s: Interrupted Exception", new Object[]{this.serialNumber}));
        } catch (RobotCoreException var51) {
            RobotLog.w(var51.getMessage());
            RobotLog.setGlobalErrorMsg(String.format("There was a problem communicating with a Modern Robotics USB device %s", new Object[]{this.serialNumber}));
        } finally {
            this.usbHandler.close();
            this.running = false;
            this.shutdownComplete = true;
        }

        RobotLog.v(String.format("stopped read/write loop for device %s", new Object[]{this.serialNumber}));
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
