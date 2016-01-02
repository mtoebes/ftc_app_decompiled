package com.qualcomm.hardware;

import com.qualcomm.hardware.ReadWriteRunnable.Callback;
import com.qualcomm.hardware.ReadWriteRunnable.EmptyCallback;
import com.qualcomm.modernrobotics.ReadWriteRunnableUsbHandler;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:18:0x0066 in [B:13:0x0062, B:18:0x0066, B:16:0x0067, B:17:0x0067, B:15:0x0067]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:62)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r4 = this;
        r3 = 0;
        r4.blockUntilReady();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r4.startBlockingWork();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r4.running = r3;
    L_0x0009:
        r0 = r4.shutdownComplete;
        if (r0 != 0) goto L_0x0067;
    L_0x000d:
        java.lang.Thread.yield();
        goto L_0x0009;
    L_0x0011:
        r0 = move-exception;
        r1 = new java.lang.StringBuilder;	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r1.<init>();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r2 = "Exception while closing USB device: ";	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r1 = r1.append(r2);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r0.getMessage();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r1.append(r0);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r0.toString();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r4.running = r3;
    L_0x002e:
        r0 = r4.shutdownComplete;
        if (r0 != 0) goto L_0x0067;
    L_0x0032:
        java.lang.Thread.yield();
        goto L_0x002e;
    L_0x0036:
        r0 = move-exception;
        r1 = new java.lang.StringBuilder;	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r1.<init>();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r2 = "Exception while closing USB device: ";	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r1 = r1.append(r2);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r0.getMessage();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r1.append(r0);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r0 = r0.toString();	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ InterruptedException -> 0x0011, RobotCoreException -> 0x0036, all -> 0x005b }
        r4.running = r3;
    L_0x0053:
        r0 = r4.shutdownComplete;
        if (r0 != 0) goto L_0x0067;
    L_0x0057:
        java.lang.Thread.yield();
        goto L_0x0053;
    L_0x005b:
        r0 = move-exception;
        r4.running = r3;
    L_0x005e:
        r1 = r4.shutdownComplete;
        if (r1 != 0) goto L_0x0066;
    L_0x0062:
        java.lang.Thread.yield();
        goto L_0x005e;
    L_0x0066:
        throw r0;
    L_0x0067:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.ReadWriteRunnableStandard.close():void");
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r13 = this;
        r6 = 1;
        r5 = 0;
        r0 = r13.monitorLength;
        r1 = r13.startAddress;
        r0 = r0 + r1;
        r2 = new byte[r0];
        r7 = new com.qualcomm.robotcore.util.ElapsedTime;
        r7.<init>();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Device ";
        r0 = r0.append(r1);
        r1 = r13.serialNumber;
        r1 = r1.toString();
        r0 = r0.append(r1);
        r8 = r0.toString();
        r13.running = r6;
        r0 = "starting read/write loop for device %s";
        r1 = new java.lang.Object[r6];
        r3 = r13.serialNumber;
        r1[r5] = r3;
        r0 = java.lang.String.format(r0, r1);
        com.qualcomm.robotcore.util.RobotLog.v(r0);
        r0 = r13.usbHandler;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r1 = com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel.RX;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0.purge(r1);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r3 = r5;
        r4 = r6;
    L_0x0041:
        r0 = r13.running;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        if (r0 == 0) goto L_0x01f3;
    L_0x0045:
        r0 = r13.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        if (r0 == 0) goto L_0x004f;
    L_0x0049:
        r7.log(r8);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r7.reset();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x004f:
        r0 = r13.usbHandler;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0.read(r3, r2);	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
    L_0x0054:
        r0 = r13.segmentReadQueue;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = r0.isEmpty();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        if (r0 != 0) goto L_0x00b0;
    L_0x005c:
        r0 = r13.segments;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r13.segmentReadQueue;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r1.remove();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = r0.get(r1);	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = (com.qualcomm.hardware.ReadWriteRunnableSegment) r0;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r0.getReadBuffer();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r1.length;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = new byte[r1];	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9 = r13.usbHandler;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r10 = r0.getAddress();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9.read(r10, r1);	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9 = r0.getReadLock();	 Catch:{ all -> 0x0191 }
        r9.lock();	 Catch:{ all -> 0x0191 }
        r9 = 0;
        r10 = r0.getReadBuffer();	 Catch:{ all -> 0x0191 }
        r11 = 0;
        r12 = r0.getReadBuffer();	 Catch:{ all -> 0x0191 }
        r12 = r12.length;	 Catch:{ all -> 0x0191 }
        java.lang.System.arraycopy(r1, r9, r10, r11, r12);	 Catch:{ all -> 0x0191 }
        r0 = r0.getReadLock();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0.unlock();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        goto L_0x0054;
    L_0x0097:
        r0 = move-exception;
        r1 = "could not read from device %s: %s";
        r9 = 2;
        r9 = new java.lang.Object[r9];	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r10 = 0;
        r11 = r13.serialNumber;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r9[r10] = r11;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r10 = 1;
        r0 = r0.getMessage();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r9[r10] = r0;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = java.lang.String.format(r1, r9);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x00b0:
        r1 = r13.localDeviceReadCache;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        monitor-enter(r1);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = 0;
        r9 = r13.localDeviceReadCache;	 Catch:{ all -> 0x01b6 }
        r10 = r2.length;	 Catch:{ all -> 0x01b6 }
        java.lang.System.arraycopy(r2, r0, r9, r3, r10);	 Catch:{ all -> 0x01b6 }
        monitor-exit(r1);	 Catch:{ all -> 0x01b6 }
        r0 = r13.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        if (r0 == 0) goto L_0x00c6;
    L_0x00bf:
        r0 = "read";
        r1 = r13.localDeviceReadCache;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r13.dumpBuffers(r0, r1);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x00c6:
        r0 = r13.callback;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0.readComplete();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r13.waitForSyncdEvents();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        if (r4 == 0) goto L_0x00d9;
    L_0x00d0:
        r1 = r13.startAddress;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = r13.monitorLength;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = new byte[r0];	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r2 = r0;
        r3 = r1;
        r4 = r5;
    L_0x00d9:
        r1 = r13.localDeviceWriteCache;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        monitor-enter(r1);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = r13.localDeviceWriteCache;	 Catch:{ all -> 0x01dc }
        r9 = 0;
        r10 = r2.length;	 Catch:{ all -> 0x01dc }
        java.lang.System.arraycopy(r0, r3, r2, r9, r10);	 Catch:{ all -> 0x01dc }
        monitor-exit(r1);	 Catch:{ all -> 0x01dc }
        r0 = r13.writeNeeded();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        if (r0 == 0) goto L_0x00f3;
    L_0x00ea:
        r0 = r13.usbHandler;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0.write(r3, r2);	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = 0;
        r13.setWriteNeeded(r0);	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
    L_0x00f3:
        r0 = r13.segmentWriteQueue;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = r0.isEmpty();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        if (r0 != 0) goto L_0x0147;
    L_0x00fb:
        r0 = r13.segments;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r13.segmentWriteQueue;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r1.remove();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = r0.get(r1);	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = (com.qualcomm.hardware.ReadWriteRunnableSegment) r0;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r1 = r0.getWriteLock();	 Catch:{ all -> 0x01ea }
        r1.lock();	 Catch:{ all -> 0x01ea }
        r1 = r0.getWriteBuffer();	 Catch:{ all -> 0x01ea }
        r9 = r0.getWriteBuffer();	 Catch:{ all -> 0x01ea }
        r9 = r9.length;	 Catch:{ all -> 0x01ea }
        r1 = java.util.Arrays.copyOf(r1, r9);	 Catch:{ all -> 0x01ea }
        r9 = r0.getWriteLock();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9.unlock();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9 = r13.usbHandler;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0 = r0.getAddress();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r9.write(r0, r1);	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        goto L_0x00f3;
    L_0x012e:
        r0 = move-exception;
        r1 = "could not write to device %s: %s";
        r9 = 2;
        r9 = new java.lang.Object[r9];	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r10 = 0;
        r11 = r13.serialNumber;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r9[r10] = r11;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r10 = 1;
        r0 = r0.getMessage();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r9[r10] = r0;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = java.lang.String.format(r1, r9);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x0147:
        r0 = r13.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        if (r0 == 0) goto L_0x0152;
    L_0x014b:
        r0 = "write";
        r1 = r13.localDeviceWriteCache;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r13.dumpBuffers(r0, r1);	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x0152:
        r0 = r13.callback;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0.writeComplete();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0 = r13.usbHandler;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        r0.throwIfUsbErrorCountIsTooHigh();	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
        goto L_0x0041;
    L_0x015e:
        r0 = move-exception;
        r1 = "could not write to device %s: FTDI Null Pointer Exception";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x01df }
        r3 = 0;
        r4 = r13.serialNumber;	 Catch:{ all -> 0x01df }
        r2[r3] = r4;	 Catch:{ all -> 0x01df }
        r1 = java.lang.String.format(r1, r2);	 Catch:{ all -> 0x01df }
        com.qualcomm.robotcore.util.RobotLog.w(r1);	 Catch:{ all -> 0x01df }
        com.qualcomm.robotcore.util.RobotLog.logStacktrace(r0);	 Catch:{ all -> 0x01df }
        r0 = "There was a problem communicating with a Modern Robotics USB device";
        com.qualcomm.robotcore.util.RobotLog.setGlobalErrorMsg(r0);	 Catch:{ all -> 0x01df }
        r0 = r13.usbHandler;
        r0.close();
        r13.running = r5;
        r13.shutdownComplete = r6;
    L_0x0181:
        r0 = "stopped read/write loop for device %s";
        r1 = new java.lang.Object[r6];
        r2 = r13.serialNumber;
        r1[r5] = r2;
        r0 = java.lang.String.format(r0, r1);
        com.qualcomm.robotcore.util.RobotLog.v(r0);
        return;
    L_0x0191:
        r1 = move-exception;
        r0 = r0.getReadLock();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0.unlock();	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        throw r1;	 Catch:{ RobotCoreException -> 0x0097, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
    L_0x019a:
        r0 = move-exception;
        r0 = "could not write to device %s: Interrupted Exception";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x01df }
        r2 = 0;
        r3 = r13.serialNumber;	 Catch:{ all -> 0x01df }
        r1[r2] = r3;	 Catch:{ all -> 0x01df }
        r0 = java.lang.String.format(r0, r1);	 Catch:{ all -> 0x01df }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x01df }
        r0 = r13.usbHandler;
        r0.close();
        r13.running = r5;
        r13.shutdownComplete = r6;
        goto L_0x0181;
    L_0x01b6:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x01b6 }
        throw r0;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x01b9:
        r0 = move-exception;
        r0 = r0.getMessage();	 Catch:{ all -> 0x01df }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x01df }
        r0 = "There was a problem communicating with a Modern Robotics USB device %s";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x01df }
        r2 = 0;
        r3 = r13.serialNumber;	 Catch:{ all -> 0x01df }
        r1[r2] = r3;	 Catch:{ all -> 0x01df }
        r0 = java.lang.String.format(r0, r1);	 Catch:{ all -> 0x01df }
        com.qualcomm.robotcore.util.RobotLog.setGlobalErrorMsg(r0);	 Catch:{ all -> 0x01df }
        r0 = r13.usbHandler;
        r0.close();
        r13.running = r5;
        r13.shutdownComplete = r6;
        goto L_0x0181;
    L_0x01dc:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x01dc }
        throw r0;	 Catch:{ NullPointerException -> 0x015e, InterruptedException -> 0x019a, RobotCoreException -> 0x01b9 }
    L_0x01df:
        r0 = move-exception;
        r1 = r13.usbHandler;
        r1.close();
        r13.running = r5;
        r13.shutdownComplete = r6;
        throw r0;
    L_0x01ea:
        r1 = move-exception;
        r0 = r0.getWriteLock();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        r0.unlock();	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
        throw r1;	 Catch:{ RobotCoreException -> 0x012e, NullPointerException -> 0x015e, InterruptedException -> 0x019a }
    L_0x01f3:
        r0 = r13.usbHandler;
        r0.close();
        r13.running = r5;
        r13.shutdownComplete = r6;
        goto L_0x0181;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.ReadWriteRunnableStandard.run():void");
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
