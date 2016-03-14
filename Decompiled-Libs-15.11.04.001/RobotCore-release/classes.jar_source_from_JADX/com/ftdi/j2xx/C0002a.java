package com.ftdi.j2xx;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

/* renamed from: com.ftdi.j2xx.a */
class C0002a implements Runnable {
    UsbDeviceConnection f35a;
    UsbEndpoint f36b;
    C0021o f37c;
    FT_Device f38d;
    int f39e;
    int f40f;
    int f41g;
    Semaphore f42h;
    boolean f43i;

    C0002a(FT_Device fT_Device, C0021o c0021o, UsbDeviceConnection usbDeviceConnection, UsbEndpoint usbEndpoint) {
        this.f38d = fT_Device;
        this.f36b = usbEndpoint;
        this.f35a = usbDeviceConnection;
        this.f37c = c0021o;
        this.f39e = this.f37c.m142b().getBufferNumber();
        this.f40f = this.f37c.m142b().getMaxTransferSize();
        this.f41g = this.f38d.m29d().getReadTimeout();
        this.f42h = new Semaphore(1);
        this.f43i = false;
    }

    void m31a() throws InterruptedException {
        this.f42h.acquire();
        this.f43i = true;
    }

    void m32b() {
        this.f43i = false;
        this.f42h.release();
    }

    boolean m33c() {
        return this.f43i;
    }

    public void run() {
        int i = 0;
        do {
            try {
                if (this.f43i) {
                    this.f42h.acquire();
                    this.f42h.release();
                }
                C0020n b = this.f37c.m143b(i);
                if (b.m127b() == 0) {
                    ByteBuffer a = b.m125a();
                    a.clear();
                    b.m126a(i);
                    int bulkTransfer = this.f35a.bulkTransfer(this.f36b, a.array(), this.f40f, this.f41g);
                    if (bulkTransfer > 0) {
                        a.position(bulkTransfer);
                        a.flip();
                        b.m128b(bulkTransfer);
                        this.f37c.m149e(i);
                    }
                }
                i = (i + 1) % this.f39e;
            } catch (InterruptedException e) {
                try {
                    this.f37c.m150f();
                    this.f37c.m148e();
                    return;
                } catch (Exception e2) {
                    Log.d("BulkIn::", "Stop BulkIn thread");
                    e2.printStackTrace();
                    return;
                }
            } catch (Exception e22) {
                e22.printStackTrace();
                Log.e("BulkIn::", "Fatal error in BulkIn thread");
                return;
            }
        } while (!Thread.interrupted());
        throw new InterruptedException();
    }
}
