package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.hardware.modernrobotics.ReadWriteRunnable.Callback;
import com.qualcomm.hardware.modernrobotics.ReadWriteRunnable.EmptyCallback;
import com.qualcomm.modernrobotics.ReadWriteRunnableUsbHandler;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.util.GlobalWarningSource;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;
import com.qualcomm.robotcore.util.Util;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class ReadWriteRunnableStandard implements ReadWriteRunnable {
    protected final boolean DEBUG_LOGGING;
    private volatile boolean f175a;
    protected volatile boolean acceptingWrites;
    protected final Object acceptingWritesLock;
    protected Callback callback;
    protected final Context context;
    protected final byte[] localDeviceReadCache;
    protected final byte[] localDeviceWriteCache;
    protected int monitorLength;
    protected RobotUsbModule owner;
    protected RobotUsbDevice robotUsbDevice;
    protected volatile boolean running;
    protected CountDownLatch runningInterlock;
    protected ConcurrentLinkedQueue<Integer> segmentReadQueue;
    protected ConcurrentLinkedQueue<Integer> segmentWriteQueue;
    protected Map<Integer, ReadWriteRunnableSegment> segments;
    protected final SerialNumber serialNumber;
    protected volatile boolean shutdownAbnormally;
    protected volatile boolean shutdownComplete;
    protected int startAddress;
    protected ReadWriteRunnableUsbHandler usbHandler;

    /* renamed from: com.qualcomm.hardware.modernrobotics.ReadWriteRunnableStandard.1 */
    class C00241 implements Runnable {
        final /* synthetic */ ReadWriteRunnableStandard f181a;

        C00241(ReadWriteRunnableStandard readWriteRunnableStandard) {
            this.f181a = readWriteRunnableStandard;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r13 = this;
            r6 = 1;
            r5 = 0;
            r0 = r13.f181a;
            r0 = r0.monitorLength;
            r1 = r13.f181a;
            r1 = r1.startAddress;
            r0 = r0 + r1;
            r2 = new byte[r0];
            r7 = new com.qualcomm.robotcore.util.ElapsedTime;
            r7.<init>();
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r1 = "Device ";
            r0 = r0.append(r1);
            r1 = r13.f181a;
            r1 = r1.serialNumber;
            r1 = r1.toString();
            r0 = r0.append(r1);
            r8 = r0.toString();
            r0 = r13.f181a;
            r0.running = r6;
            r0 = r13.f181a;	 Catch:{ InterruptedException -> 0x0202 }
            r0 = r0.callback;	 Catch:{ InterruptedException -> 0x0202 }
            r0.startupComplete();	 Catch:{ InterruptedException -> 0x0202 }
        L_0x0038:
            r0 = r13.f181a;
            r0 = r0.runningInterlock;
            r0.countDown();
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.usbHandler;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel.RX;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.purge(r1);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r3 = r5;
            r4 = r6;
        L_0x004a:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.running;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            if (r0 == 0) goto L_0x02af;
        L_0x0050:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            if (r0 == 0) goto L_0x005c;
        L_0x0056:
            r7.log(r8);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r7.reset();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x005c:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.usbHandler;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.read(r3, r2);	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
        L_0x0063:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.segmentReadQueue;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.isEmpty();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            if (r0 != 0) goto L_0x00cd;
        L_0x006d:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.segments;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r13.f181a;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r1.segmentReadQueue;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r1.remove();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.get(r1);	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = (com.qualcomm.hardware.modernrobotics.ReadWriteRunnableSegment) r0;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r0.getReadBuffer();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r1.length;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = new byte[r1];	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9 = r13.f181a;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9 = r9.usbHandler;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r10 = r0.getAddress();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9.read(r10, r1);	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9 = r0.getReadLock();	 Catch:{ all -> 0x0209 }
            r9.lock();	 Catch:{ all -> 0x0209 }
            r9 = 0;
            r10 = r0.getReadBuffer();	 Catch:{ all -> 0x0209 }
            r11 = 0;
            r12 = r0.getReadBuffer();	 Catch:{ all -> 0x0209 }
            r12 = r12.length;	 Catch:{ all -> 0x0209 }
            java.lang.System.arraycopy(r1, r9, r10, r11, r12);	 Catch:{ all -> 0x0209 }
            r0 = r0.getReadLock();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.unlock();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            goto L_0x0063;
        L_0x00ae:
            r0 = move-exception;
            r1 = "could not read %s: %s";
            r9 = 2;
            r9 = new java.lang.Object[r9];	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r10 = 0;
            r11 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r11 = r11.serialNumber;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r11 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r11);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9[r10] = r11;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r10 = 1;
            r0 = r0.getMessage();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9[r10] = r0;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = java.lang.String.format(r1, r9);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x00cd:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = r0.localDeviceReadCache;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            monitor-enter(r1);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = 0;
            r9 = r13.f181a;	 Catch:{ all -> 0x0246 }
            r9 = r9.localDeviceReadCache;	 Catch:{ all -> 0x0246 }
            r10 = r2.length;	 Catch:{ all -> 0x0246 }
            java.lang.System.arraycopy(r2, r0, r9, r3, r10);	 Catch:{ all -> 0x0246 }
            monitor-exit(r1);	 Catch:{ all -> 0x0246 }
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            if (r0 == 0) goto L_0x00ed;
        L_0x00e2:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = "read";
            r9 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9 = r9.localDeviceReadCache;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.dumpBuffers(r1, r9);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x00ed:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.callback;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.readComplete();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.waitForSyncdEvents();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            if (r4 == 0) goto L_0x0108;
        L_0x00fb:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = r0.startAddress;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.monitorLength;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = new byte[r0];	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r2 = r0;
            r3 = r1;
            r4 = r5;
        L_0x0108:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = r0.localDeviceWriteCache;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            monitor-enter(r1);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r13.f181a;	 Catch:{ all -> 0x028b }
            r0 = r0.localDeviceWriteCache;	 Catch:{ all -> 0x028b }
            r9 = 0;
            r10 = r2.length;	 Catch:{ all -> 0x028b }
            java.lang.System.arraycopy(r0, r3, r2, r9, r10);	 Catch:{ all -> 0x028b }
            monitor-exit(r1);	 Catch:{ all -> 0x028b }
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.writeNeeded();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            if (r0 == 0) goto L_0x012b;
        L_0x011f:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.usbHandler;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.write(r3, r2);	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.resetWriteNeeded();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
        L_0x012b:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.segmentWriteQueue;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.isEmpty();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            if (r0 != 0) goto L_0x018d;
        L_0x0135:
            r0 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.segments;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r1.segmentWriteQueue;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r1.remove();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.get(r1);	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = (com.qualcomm.hardware.modernrobotics.ReadWriteRunnableSegment) r0;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r1 = r0.getWriteLock();	 Catch:{ all -> 0x02a6 }
            r1.lock();	 Catch:{ all -> 0x02a6 }
            r1 = r0.getWriteBuffer();	 Catch:{ all -> 0x02a6 }
            r9 = r0.getWriteBuffer();	 Catch:{ all -> 0x02a6 }
            r9 = r9.length;	 Catch:{ all -> 0x02a6 }
            r1 = java.util.Arrays.copyOf(r1, r9);	 Catch:{ all -> 0x02a6 }
            r9 = r0.getWriteLock();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9.unlock();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9 = r13.f181a;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9 = r9.usbHandler;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0 = r0.getAddress();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r9.write(r0, r1);	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            goto L_0x012b;
        L_0x016e:
            r0 = move-exception;
            r1 = "could not write to %s: %s";
            r9 = 2;
            r9 = new java.lang.Object[r9];	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r10 = 0;
            r11 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r11 = r11.serialNumber;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r11 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r11);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9[r10] = r11;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r10 = 1;
            r0 = r0.getMessage();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9[r10] = r0;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = java.lang.String.format(r1, r9);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x018d:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.DEBUG_LOGGING;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            if (r0 == 0) goto L_0x019e;
        L_0x0193:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r1 = "write";
            r9 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r9 = r9.localDeviceWriteCache;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.dumpBuffers(r1, r9);	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x019e:
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.callback;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.writeComplete();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r13.f181a;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0 = r0.usbHandler;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            r0.throwIfUsbErrorCountIsTooHigh();	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
            goto L_0x004a;
        L_0x01ae:
            r0 = move-exception;
            r1 = "could not write to %s: FTDI Null Pointer Exception";
            r2 = 1;
            r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x028e }
            r3 = 0;
            r4 = r13.f181a;	 Catch:{ all -> 0x028e }
            r4 = r4.serialNumber;	 Catch:{ all -> 0x028e }
            r4 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r4);	 Catch:{ all -> 0x028e }
            r2[r3] = r4;	 Catch:{ all -> 0x028e }
            r1 = java.lang.String.format(r1, r2);	 Catch:{ all -> 0x028e }
            com.qualcomm.robotcore.util.RobotLog.w(r1);	 Catch:{ all -> 0x028e }
            com.qualcomm.robotcore.util.RobotLog.logStacktrace(r0);	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = r1.context;	 Catch:{ all -> 0x028e }
            r2 = com.qualcomm.hardware.R.string.warningProblemCommunicatingWithUSBDevice;	 Catch:{ all -> 0x028e }
            r1 = r1.getString(r2);	 Catch:{ all -> 0x028e }
            r2 = 1;
            r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x028e }
            r3 = 0;
            r4 = r13.f181a;	 Catch:{ all -> 0x028e }
            r4 = r4.serialNumber;	 Catch:{ all -> 0x028e }
            r4 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r4);	 Catch:{ all -> 0x028e }
            r2[r3] = r4;	 Catch:{ all -> 0x028e }
            r0.m72a(r1, r2);	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = 1;
            r0.shutdownAbnormally = r1;	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;
            r0 = r0.usbHandler;
            r0.close();
            r0 = r13.f181a;
            r0.running = r5;
            r0 = r13.f181a;	 Catch:{ InterruptedException -> 0x02ce }
            r0 = r0.callback;	 Catch:{ InterruptedException -> 0x02ce }
            r0.shutdownComplete();	 Catch:{ InterruptedException -> 0x02ce }
        L_0x01fd:
            r0 = r13.f181a;
            r0.shutdownComplete = r6;
        L_0x0201:
            return;
        L_0x0202:
            r0 = move-exception;
            r0 = r13.f181a;
            r0.running = r5;
            goto L_0x0038;
        L_0x0209:
            r1 = move-exception;
            r0 = r0.getReadLock();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.unlock();	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            throw r1;	 Catch:{ RobotCoreException -> 0x00ae, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
        L_0x0212:
            r0 = move-exception;
            r0 = "thread interrupt: could not write to %s";
            r1 = 1;
            r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x028e }
            r2 = 0;
            r3 = r13.f181a;	 Catch:{ all -> 0x028e }
            r3 = r3.serialNumber;	 Catch:{ all -> 0x028e }
            r3 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r3);	 Catch:{ all -> 0x028e }
            r1[r2] = r3;	 Catch:{ all -> 0x028e }
            r0 = java.lang.String.format(r0, r1);	 Catch:{ all -> 0x028e }
            com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = 1;
            r0.shutdownAbnormally = r1;	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;
            r0 = r0.usbHandler;
            r0.close();
            r0 = r13.f181a;
            r0.running = r5;
            r0 = r13.f181a;	 Catch:{ InterruptedException -> 0x02cb }
            r0 = r0.callback;	 Catch:{ InterruptedException -> 0x02cb }
            r0.shutdownComplete();	 Catch:{ InterruptedException -> 0x02cb }
        L_0x0241:
            r0 = r13.f181a;
            r0.shutdownComplete = r6;
            goto L_0x0201;
        L_0x0246:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0246 }
            throw r0;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x0249:
            r0 = move-exception;
            r0 = r0.getMessage();	 Catch:{ all -> 0x028e }
            com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = r1.context;	 Catch:{ all -> 0x028e }
            r2 = com.qualcomm.hardware.R.string.warningProblemCommunicatingWithUSBDevice;	 Catch:{ all -> 0x028e }
            r1 = r1.getString(r2);	 Catch:{ all -> 0x028e }
            r2 = 1;
            r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x028e }
            r3 = 0;
            r4 = r13.f181a;	 Catch:{ all -> 0x028e }
            r4 = r4.serialNumber;	 Catch:{ all -> 0x028e }
            r4 = com.qualcomm.hardware.HardwareFactory.getSerialNumberDisplayName(r4);	 Catch:{ all -> 0x028e }
            r2[r3] = r4;	 Catch:{ all -> 0x028e }
            r0.m72a(r1, r2);	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;	 Catch:{ all -> 0x028e }
            r1 = 1;
            r0.shutdownAbnormally = r1;	 Catch:{ all -> 0x028e }
            r0 = r13.f181a;
            r0 = r0.usbHandler;
            r0.close();
            r0 = r13.f181a;
            r0.running = r5;
            r0 = r13.f181a;	 Catch:{ InterruptedException -> 0x02c9 }
            r0 = r0.callback;	 Catch:{ InterruptedException -> 0x02c9 }
            r0.shutdownComplete();	 Catch:{ InterruptedException -> 0x02c9 }
        L_0x0285:
            r0 = r13.f181a;
            r0.shutdownComplete = r6;
            goto L_0x0201;
        L_0x028b:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x028b }
            throw r0;	 Catch:{ NullPointerException -> 0x01ae, InterruptedException -> 0x0212, RobotCoreException -> 0x0249 }
        L_0x028e:
            r0 = move-exception;
            r1 = r13.f181a;
            r1 = r1.usbHandler;
            r1.close();
            r1 = r13.f181a;
            r1.running = r5;
            r1 = r13.f181a;	 Catch:{ InterruptedException -> 0x02c7 }
            r1 = r1.callback;	 Catch:{ InterruptedException -> 0x02c7 }
            r1.shutdownComplete();	 Catch:{ InterruptedException -> 0x02c7 }
        L_0x02a1:
            r1 = r13.f181a;
            r1.shutdownComplete = r6;
            throw r0;
        L_0x02a6:
            r1 = move-exception;
            r0 = r0.getWriteLock();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            r0.unlock();	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
            throw r1;	 Catch:{ RobotCoreException -> 0x016e, NullPointerException -> 0x01ae, InterruptedException -> 0x0212 }
        L_0x02af:
            r0 = r13.f181a;
            r0 = r0.usbHandler;
            r0.close();
            r0 = r13.f181a;
            r0.running = r5;
            r0 = r13.f181a;	 Catch:{ InterruptedException -> 0x02d1 }
            r0 = r0.callback;	 Catch:{ InterruptedException -> 0x02d1 }
            r0.shutdownComplete();	 Catch:{ InterruptedException -> 0x02d1 }
        L_0x02c1:
            r0 = r13.f181a;
            r0.shutdownComplete = r6;
            goto L_0x0201;
        L_0x02c7:
            r1 = move-exception;
            goto L_0x02a1;
        L_0x02c9:
            r0 = move-exception;
            goto L_0x0285;
        L_0x02cb:
            r0 = move-exception;
            goto L_0x0241;
        L_0x02ce:
            r0 = move-exception;
            goto L_0x01fd;
        L_0x02d1:
            r0 = move-exception;
            goto L_0x02c1;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.modernrobotics.ReadWriteRunnableStandard.1.run():void");
        }
    }

    public ReadWriteRunnableStandard(Context context, SerialNumber serialNumber, RobotUsbDevice device, int monitorLength, int startAddress, boolean debug) {
        this.localDeviceReadCache = new byte[ReadWriteRunnable.MAX_BUFFER_SIZE];
        this.localDeviceWriteCache = new byte[ReadWriteRunnable.MAX_BUFFER_SIZE];
        this.segments = new HashMap();
        this.segmentReadQueue = new ConcurrentLinkedQueue();
        this.segmentWriteQueue = new ConcurrentLinkedQueue();
        this.runningInterlock = new CountDownLatch(1);
        this.running = false;
        this.shutdownAbnormally = false;
        this.shutdownComplete = false;
        this.f175a = false;
        this.acceptingWritesLock = new Object();
        this.acceptingWrites = false;
        this.context = context;
        this.serialNumber = serialNumber;
        this.startAddress = startAddress;
        this.monitorLength = monitorLength;
        this.DEBUG_LOGGING = debug;
        this.callback = new EmptyCallback();
        this.owner = null;
        this.robotUsbDevice = device;
        this.usbHandler = new ReadWriteRunnableUsbHandler(device);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setOwner(RobotUsbModule owner) {
        this.owner = owner;
    }

    public RobotUsbModule getOwner() {
        return this.owner;
    }

    public void blockUntilReady() throws RobotCoreException, InterruptedException {
    }

    public void startBlockingWork() {
    }

    public boolean writeNeeded() {
        return this.f175a;
    }

    public void resetWriteNeeded() {
        this.f175a = false;
    }

    public void write(int address, byte[] data) {
        synchronized (this.acceptingWritesLock) {
            if (this.acceptingWrites) {
                synchronized (this.localDeviceWriteCache) {
                    System.arraycopy(data, 0, this.localDeviceWriteCache, address, data.length);
                    this.f175a = true;
                }
            }
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

    public void executeUsing(ExecutorService service) {
        synchronized (this) {
            service.execute(this);
            awaitRunning();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() {
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = r3.running;	 Catch:{ all -> 0x0016 }
        if (r0 == 0) goto L_0x0051;
    L_0x0005:
        r0 = 0;
        r3.running = r0;	 Catch:{ all -> 0x0016 }
        r3.blockUntilReady();	 Catch:{ InterruptedException -> 0x0019, RobotCoreException -> 0x0035 }
        r3.startBlockingWork();	 Catch:{ InterruptedException -> 0x0019, RobotCoreException -> 0x0035 }
    L_0x000e:
        r0 = r3.shutdownComplete;	 Catch:{ all -> 0x0016 }
        if (r0 != 0) goto L_0x0051;
    L_0x0012:
        java.lang.Thread.yield();	 Catch:{ all -> 0x0016 }
        goto L_0x000e;
    L_0x0016:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        throw r0;
    L_0x0019:
        r0 = move-exception;
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0016 }
        r1.<init>();	 Catch:{ all -> 0x0016 }
        r2 = "Exception while closing USB device: ";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0016 }
        r0 = r0.getMessage();	 Catch:{ all -> 0x0016 }
        r0 = r1.append(r0);	 Catch:{ all -> 0x0016 }
        r0 = r0.toString();	 Catch:{ all -> 0x0016 }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x0016 }
        goto L_0x000e;
    L_0x0035:
        r0 = move-exception;
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0016 }
        r1.<init>();	 Catch:{ all -> 0x0016 }
        r2 = "Exception while closing USB device: ";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0016 }
        r0 = r0.getMessage();	 Catch:{ all -> 0x0016 }
        r0 = r1.append(r0);	 Catch:{ all -> 0x0016 }
        r0 = r0.toString();	 Catch:{ all -> 0x0016 }
        com.qualcomm.robotcore.util.RobotLog.w(r0);	 Catch:{ all -> 0x0016 }
        goto L_0x000e;
    L_0x0051:
        monitor-exit(r3);	 Catch:{ all -> 0x0016 }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.modernrobotics.ReadWriteRunnableStandard.close():void");
    }

    public ReadWriteRunnableSegment createSegment(int key, int address, int size) {
        ReadWriteRunnableSegment readWriteRunnableSegment = new ReadWriteRunnableSegment(address, size);
        this.segments.put(Integer.valueOf(key), readWriteRunnableSegment);
        return readWriteRunnableSegment;
    }

    public void destroySegment(int key) {
        this.segments.remove(Integer.valueOf(key));
    }

    public ReadWriteRunnableSegment getSegment(int key) {
        return (ReadWriteRunnableSegment) this.segments.get(Integer.valueOf(key));
    }

    public void queueSegmentRead(int key) {
        queueIfNotAlreadyQueued(key, this.segmentReadQueue);
    }

    public void queueSegmentWrite(int key) {
        synchronized (this.acceptingWritesLock) {
            if (this.acceptingWrites) {
                queueIfNotAlreadyQueued(key, this.segmentWriteQueue);
            }
        }
    }

    protected void awaitRunning() {
        try {
            this.runningInterlock.await();
        } catch (InterruptedException e) {
            while (this.runningInterlock.getCount() != 0) {
                Thread.yield();
            }
            Thread.currentThread().interrupt();
        }
    }

    public void run() {
        if (!this.shutdownComplete) {
            Util.logThreadLifeCycle(String.format("r/w loop: %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(this.serialNumber)}), new C00241(this));
        }
    }

    void m72a(String str, Object... objArr) {
        String format = String.format(str, objArr);
        if (this.owner == null || !(this.owner instanceof GlobalWarningSource)) {
            RobotLog.setGlobalWarningMessage(format);
        } else {
            ((GlobalWarningSource) this.owner).setGlobalWarning(format);
        }
    }

    public boolean hasShutdownAbnormally() {
        return this.shutdownAbnormally;
    }

    boolean m73a() {
        return this.f175a || !this.segmentWriteQueue.isEmpty();
    }

    public void drainPendingWrites() {
        while (this.running && m73a()) {
            startBlockingWork();
            Thread.yield();
        }
    }

    public void setAcceptingWrites(boolean acceptingWrites) {
        synchronized (this.acceptingWritesLock) {
            this.acceptingWrites = acceptingWrites;
        }
    }

    public boolean getAcceptingWrites() {
        return this.acceptingWrites;
    }

    protected void waitForSyncdEvents() throws RobotCoreException, InterruptedException {
    }

    protected void dumpBuffers(String name, byte[] byteArray) {
        RobotLog.v("Dumping " + name + " buffers for " + this.serialNumber);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (int i = 0; i < this.startAddress + this.monitorLength; i++) {
            stringBuilder.append(String.format(" %02x", new Object[]{Integer.valueOf(TypeConversion.unsignedByteToInt(byteArray[i]))}));
            if ((i + 1) % 16 == 0) {
                stringBuilder.append("\n");
            }
        }
        RobotLog.v(stringBuilder.toString());
    }

    protected void queueIfNotAlreadyQueued(int key, ConcurrentLinkedQueue<Integer> queue) {
        if (!queue.contains(Integer.valueOf(key))) {
            queue.add(Integer.valueOf(key));
        }
    }
}
