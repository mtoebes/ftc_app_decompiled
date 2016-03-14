package com.ftdi.j2xx;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.D2xxManager.DriverParameters;
import com.qualcomm.robotcore.robocol.Telemetry;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* renamed from: com.ftdi.j2xx.o */
class C0021o {
    private Semaphore[] f114a;
    private Semaphore[] f115b;
    private C0020n[] f116c;
    private ByteBuffer f117d;
    private ByteBuffer[] f118e;
    private Pipe f119f;
    private SinkChannel f120g;
    private SourceChannel f121h;
    private int f122i;
    private int f123j;
    private Object f124k;
    private FT_Device f125l;
    private DriverParameters f126m;
    private Lock f127n;
    private Condition f128o;
    private boolean f129p;
    private Lock f130q;
    private Condition f131r;
    private Object f132s;
    private int f133t;

    public C0021o(FT_Device fT_Device) {
        int i = 0;
        this.f125l = fT_Device;
        this.f126m = this.f125l.m29d();
        this.f122i = this.f126m.getBufferNumber();
        int maxBufferSize = this.f126m.getMaxBufferSize();
        this.f133t = this.f125l.m30e();
        this.f114a = new Semaphore[this.f122i];
        this.f115b = new Semaphore[this.f122i];
        this.f116c = new C0020n[this.f122i];
        this.f118e = new ByteBuffer[256];
        this.f127n = new ReentrantLock();
        this.f128o = this.f127n.newCondition();
        this.f129p = false;
        this.f130q = new ReentrantLock();
        this.f131r = this.f130q.newCondition();
        this.f124k = new Object();
        this.f132s = new Object();
        m143h();
        this.f117d = ByteBuffer.allocateDirect(maxBufferSize);
        try {
            this.f119f = Pipe.open();
            this.f120g = this.f119f.sink();
            this.f121h = this.f119f.source();
        } catch (IOException e) {
            Log.d("ProcessInCtrl", "Create mMainPipe failed!");
            e.printStackTrace();
        }
        while (i < this.f122i) {
            this.f116c[i] = new C0020n(maxBufferSize);
            this.f115b[i] = new Semaphore(1);
            this.f114a[i] = new Semaphore(1);
            try {
                m152c(i);
            } catch (Exception e2) {
                Log.d("ProcessInCtrl", "Acquire read buffer " + i + " failed!");
                e2.printStackTrace();
            }
            i++;
        }
    }

    boolean m148a() {
        return this.f129p;
    }

    DriverParameters m149b() {
        return this.f126m;
    }

    C0020n m146a(int i) {
        C0020n c0020n = null;
        synchronized (this.f116c) {
            if (i >= 0) {
                if (i < this.f122i) {
                    c0020n = this.f116c[i];
                }
            }
        }
        return c0020n;
    }

    C0020n m150b(int i) throws InterruptedException {
        this.f114a[i].acquire();
        C0020n a = m146a(i);
        if (a.m136c(i) == null) {
            return null;
        }
        return a;
    }

    C0020n m152c(int i) throws InterruptedException {
        this.f115b[i].acquire();
        return m146a(i);
    }

    public void m154d(int i) throws InterruptedException {
        synchronized (this.f116c) {
            this.f116c[i].m139d(i);
        }
        this.f114a[i].release();
    }

    public void m156e(int i) throws InterruptedException {
        this.f115b[i].release();
    }

    public void m147a(C0020n c0020n) throws D2xxException {
        try {
            int b = c0020n.m134b();
            if (b < 2) {
                c0020n.m132a().clear();
                return;
            }
            int d;
            synchronized (this.f132s) {
                d = m153d();
                b -= 2;
                if (d < b) {
                    Log.d("ProcessBulkIn::", " Buffer is full, waiting for read....");
                    m144a(false, (short) 0, (short) 0);
                    this.f127n.lock();
                    this.f129p = true;
                }
            }
            if (d < b) {
                this.f128o.await();
                this.f127n.unlock();
            }
            m140b(c0020n);
        } catch (InterruptedException e) {
            this.f127n.unlock();
            Log.e("ProcessInCtrl", "Exception in Full await!");
            e.printStackTrace();
        } catch (Exception e2) {
            Log.e("ProcessInCtrl", "Exception in ProcessBulkIN");
            e2.printStackTrace();
            throw new D2xxException("Fatal error in BulkIn.");
        }
    }

    private void m140b(C0020n c0020n) throws InterruptedException {
        boolean z = true;
        ByteBuffer a = c0020n.m132a();
        int b = c0020n.m134b();
        if (b > 0) {
            int i = (b / this.f133t) + (b % this.f133t > 0 ? 1 : 0);
            int i2 = 0;
            short s = (short) 0;
            short s2 = (short) 0;
            int i3 = 0;
            while (i2 < i) {
                int i4;
                int i5;
                if (i2 == i - 1) {
                    a.limit(b);
                    int i6 = this.f133t * i2;
                    a.position(i6);
                    byte b2 = a.get();
                    s2 = (short) (this.f125l.f20g.modemStatus ^ ((short) (b2 & 240)));
                    this.f125l.f20g.modemStatus = (short) (b2 & 240);
                    this.f125l.f20g.lineStatus = (short) (a.get() & Telemetry.cbTagMax);
                    i4 = i6 + 2;
                    if (a.hasRemaining()) {
                        s = (short) (this.f125l.f20g.lineStatus & 30);
                        i5 = i4;
                        i4 = b;
                    } else {
                        s = (short) 0;
                        i5 = i4;
                        i4 = b;
                    }
                } else {
                    i4 = (i2 + 1) * this.f133t;
                    a.limit(i4);
                    i5 = (this.f133t * i2) + 2;
                    a.position(i5);
                }
                i5 = i3 + (i4 - i5);
                this.f118e[i2] = a.slice();
                i2++;
                i3 = i5;
            }
            if (i3 != 0) {
                try {
                    long write = this.f120g.write(this.f118e, 0, i);
                    if (write != ((long) i3)) {
                        Log.d("extractReadData::", "written != totalData, written= " + write + " totalData=" + i3);
                    }
                    m141f((int) write);
                    this.f130q.lock();
                    this.f131r.signalAll();
                    this.f130q.unlock();
                } catch (Exception e) {
                    Log.d("extractReadData::", "Write data to sink failed!!");
                    e.printStackTrace();
                }
            } else {
                z = false;
            }
            a.clear();
            m144a(z, s2, s);
        }
    }

    public int m145a(byte[] bArr, int i, long j) {
        this.f126m.getMaxBufferSize();
        long currentTimeMillis = System.currentTimeMillis();
        ByteBuffer wrap = ByteBuffer.wrap(bArr, 0, i);
        if (j == 0) {
            j = (long) this.f126m.getReadTimeout();
        }
        while (this.f125l.isOpen()) {
            if (m151c() >= i) {
                synchronized (this.f121h) {
                    try {
                        this.f121h.read(wrap);
                        m142g(i);
                    } catch (Exception e) {
                        Log.d("readBulkInData::", "Cannot read data from Source!!");
                        e.printStackTrace();
                    }
                }
                synchronized (this.f132s) {
                    if (this.f129p) {
                        Log.i("FTDI debug::", "buffer is full , and also re start buffer");
                        this.f127n.lock();
                        this.f128o.signalAll();
                        this.f129p = false;
                        this.f127n.unlock();
                    }
                }
                return i;
            }
            try {
                this.f130q.lock();
                this.f131r.await(System.currentTimeMillis() - currentTimeMillis, TimeUnit.MILLISECONDS);
                this.f130q.unlock();
            } catch (InterruptedException e2) {
                Log.d("readBulkInData::", "Cannot wait to read data!!");
                e2.printStackTrace();
                this.f130q.unlock();
            }
            if (System.currentTimeMillis() - currentTimeMillis >= j) {
                return 0;
            }
        }
        return 0;
    }

    private int m141f(int i) {
        int i2;
        synchronized (this.f124k) {
            this.f123j += i;
            i2 = this.f123j;
        }
        return i2;
    }

    private int m142g(int i) {
        int i2;
        synchronized (this.f124k) {
            this.f123j -= i;
            i2 = this.f123j;
        }
        return i2;
    }

    private void m143h() {
        synchronized (this.f124k) {
            this.f123j = 0;
        }
    }

    public int m151c() {
        int i;
        synchronized (this.f124k) {
            i = this.f123j;
        }
        return i;
    }

    public int m153d() {
        return (this.f126m.getMaxBufferSize() - m151c()) - 1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int m155e() {
        /*
        r7 = this;
        r1 = 0;
        r0 = r7.f126m;
        r2 = r0.getBufferNumber();
        r3 = r7.f117d;
        monitor-enter(r3);
    L_0x000a:
        r0 = r7.f121h;	 Catch:{ Exception -> 0x0027 }
        r4 = 0;
        r0.configureBlocking(r4);	 Catch:{ Exception -> 0x0027 }
        r0 = r7.f121h;	 Catch:{ Exception -> 0x0027 }
        r4 = r7.f117d;	 Catch:{ Exception -> 0x0027 }
        r0 = r0.read(r4);	 Catch:{ Exception -> 0x0027 }
        r4 = r7.f117d;	 Catch:{ Exception -> 0x0027 }
        r4.clear();	 Catch:{ Exception -> 0x0027 }
        if (r0 != 0) goto L_0x000a;
    L_0x001f:
        r7.m143h();	 Catch:{ all -> 0x002c }
        r0 = r1;
    L_0x0023:
        if (r0 < r2) goto L_0x002f;
    L_0x0025:
        monitor-exit(r3);	 Catch:{ all -> 0x002c }
        return r1;
    L_0x0027:
        r0 = move-exception;
        r0.printStackTrace();	 Catch:{ all -> 0x002c }
        goto L_0x001f;
    L_0x002c:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x002c }
        throw r0;
    L_0x002f:
        r4 = r7.m146a(r0);	 Catch:{ all -> 0x002c }
        r5 = r4.m138d();	 Catch:{ all -> 0x002c }
        if (r5 == 0) goto L_0x0043;
    L_0x0039:
        r5 = r4.m134b();	 Catch:{ all -> 0x002c }
        r6 = 2;
        if (r5 <= r6) goto L_0x0043;
    L_0x0040:
        r4.m137c();	 Catch:{ all -> 0x002c }
    L_0x0043:
        r0 = r0 + 1;
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ftdi.j2xx.o.e():int");
    }

    public int m144a(boolean z, short s, short s2) throws InterruptedException {
        C0024q c0024q = new C0024q();
        c0024q.f171a = this.f125l.f22i.f171a;
        if (z && (c0024q.f171a & 1) != 0 && (this.f125l.f14a ^ 1) == 1) {
            FT_Device fT_Device = this.f125l;
            fT_Device.f14a |= 1;
            Intent intent = new Intent("FT_EVENT_RXCHAR");
            intent.putExtra("message", "FT_EVENT_RXCHAR");
            LocalBroadcastManager.getInstance(this.f125l.f23j).sendBroadcast(intent);
        }
        if (!(s == (short) 0 || (c0024q.f171a & 2) == 0 || (this.f125l.f14a ^ 2) != 2)) {
            fT_Device = this.f125l;
            fT_Device.f14a |= 2;
            intent = new Intent("FT_EVENT_MODEM_STATUS");
            intent.putExtra("message", "FT_EVENT_MODEM_STATUS");
            LocalBroadcastManager.getInstance(this.f125l.f23j).sendBroadcast(intent);
        }
        if (!(s2 == (short) 0 || (c0024q.f171a & 4) == 0 || (this.f125l.f14a ^ 4) != 4)) {
            FT_Device fT_Device2 = this.f125l;
            fT_Device2.f14a |= 4;
            Intent intent2 = new Intent("FT_EVENT_LINE_STATUS");
            intent2.putExtra("message", "FT_EVENT_LINE_STATUS");
            LocalBroadcastManager.getInstance(this.f125l.f23j).sendBroadcast(intent2);
        }
        return 0;
    }

    public void m157f() throws InterruptedException {
        int bufferNumber = this.f126m.getBufferNumber();
        for (int i = 0; i < bufferNumber; i++) {
            if (m146a(i).m138d()) {
                m154d(i);
            }
        }
    }

    void m158g() {
        int i = 0;
        for (int i2 = 0; i2 < this.f122i; i2++) {
            try {
                m156e(i2);
            } catch (Exception e) {
                Log.d("ProcessInCtrl", "Acquire read buffer " + i2 + " failed!");
                e.printStackTrace();
            }
            this.f116c[i2] = null;
            this.f115b[i2] = null;
            this.f114a[i2] = null;
        }
        while (i < 256) {
            this.f118e[i] = null;
            i++;
        }
        this.f114a = null;
        this.f115b = null;
        this.f116c = null;
        this.f118e = null;
        this.f117d = null;
        if (this.f129p) {
            this.f127n.lock();
            this.f128o.signalAll();
            this.f127n.unlock();
        }
        this.f130q.lock();
        this.f131r.signalAll();
        this.f130q.unlock();
        this.f127n = null;
        this.f128o = null;
        this.f124k = null;
        this.f130q = null;
        this.f131r = null;
        try {
            this.f120g.close();
            this.f120g = null;
            this.f121h.close();
            this.f121h = null;
            this.f119f = null;
        } catch (IOException e2) {
            Log.d("ProcessInCtrl", "Close mMainPipe failed!");
            e2.printStackTrace();
        }
        this.f125l = null;
        this.f126m = null;
    }
}
