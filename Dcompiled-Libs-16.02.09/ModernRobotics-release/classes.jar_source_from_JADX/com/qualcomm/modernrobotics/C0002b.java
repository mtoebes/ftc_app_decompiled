package com.qualcomm.modernrobotics;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/* renamed from: com.qualcomm.modernrobotics.b */
class C0002b implements RobotUsbDevice {
    public final boolean f4a;
    public SerialNumber f5b;
    public String f6c;
    protected final byte[] f7d;
    protected final byte[] f8e;
    private byte[] f9f;
    private byte[] f10g;
    private BlockingQueue<byte[]> f11h;

    /* renamed from: com.qualcomm.modernrobotics.b.1 */
    class C00011 extends Thread {
        final /* synthetic */ byte[] f2a;
        final /* synthetic */ C0002b f3b;

        C00011(C0002b c0002b, byte[] bArr) {
            this.f3b = c0002b;
            this.f2a = bArr;
        }

        public void run() {
            int unsignedByteToInt = TypeConversion.unsignedByteToInt(this.f2a[3]);
            int unsignedByteToInt2 = TypeConversion.unsignedByteToInt(this.f2a[4]);
            try {
                Object obj;
                Thread.sleep(10);
                switch (this.f2a[2]) {
                    case Byte.MIN_VALUE:
                        obj = new byte[(this.f3b.f8e.length + unsignedByteToInt2)];
                        System.arraycopy(this.f3b.f8e, 0, obj, 0, this.f3b.f8e.length);
                        obj[3] = this.f2a[3];
                        obj[4] = this.f2a[4];
                        System.arraycopy(this.f3b.f9f, unsignedByteToInt, obj, this.f3b.f8e.length, unsignedByteToInt2);
                        break;
                    case (byte) 0:
                        obj = new byte[this.f3b.f7d.length];
                        System.arraycopy(this.f3b.f7d, 0, obj, 0, this.f3b.f7d.length);
                        obj[3] = this.f2a[3];
                        obj[4] = (byte) 0;
                        System.arraycopy(this.f2a, 5, this.f3b.f9f, unsignedByteToInt, unsignedByteToInt2);
                        break;
                    default:
                        obj = Arrays.copyOf(this.f2a, this.f2a.length);
                        obj[2] = (byte) -1;
                        obj[3] = this.f2a[3];
                        obj[4] = (byte) 0;
                        break;
                }
                this.f3b.f11h.put(obj);
            } catch (InterruptedException e) {
                RobotLog.w("USB mock bus interrupted during write");
            }
        }
    }

    public void setBaudRate(int rate) throws RobotCoreException {
    }

    public void setDataCharacteristics(byte dataBits, byte stopBits, byte parity) throws RobotCoreException {
    }

    public void setLatencyTimer(int latencyTimer) throws RobotCoreException {
    }

    public void purge(Channel channel) throws RobotCoreException {
        this.f11h.clear();
    }

    public void write(byte[] data) throws RobotCoreException {
        m9a(data);
    }

    public int read(byte[] data) throws RobotCoreException {
        return read(data, data.length, Integer.MAX_VALUE);
    }

    public int read(byte[] data, int length, int timeout) throws RobotCoreException {
        return m8a(data, length, timeout);
    }

    public void close() {
    }

    private void m9a(byte[] bArr) {
        if (this.f4a) {
            RobotLog.d(this.f5b + " USB recd: " + Arrays.toString(bArr));
        }
        new C00011(this, bArr).start();
    }

    private int m8a(byte[] bArr, int i, int i2) {
        Object copyOf;
        if (this.f10g != null) {
            copyOf = Arrays.copyOf(this.f10g, this.f10g.length);
            this.f10g = null;
        } else {
            try {
                byte[] bArr2 = (byte[]) this.f11h.poll((long) i2, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                RobotLog.w("USB mock bus interrupted during read");
                copyOf = null;
            }
        }
        if (copyOf == null) {
            RobotLog.w("USB mock bus read timeout");
            System.arraycopy(this.f8e, 0, bArr, 0, this.f8e.length);
            bArr[2] = (byte) -1;
            bArr[4] = (byte) 0;
        } else {
            System.arraycopy(copyOf, 0, bArr, 0, i);
        }
        if (copyOf != null && i < copyOf.length) {
            this.f10g = new byte[(copyOf.length - i)];
            System.arraycopy(copyOf, bArr.length, this.f10g, 0, this.f10g.length);
        }
        if (this.f4a) {
            RobotLog.d(this.f5b + " USB send: " + Arrays.toString(bArr));
        }
        return bArr.length;
    }
}
