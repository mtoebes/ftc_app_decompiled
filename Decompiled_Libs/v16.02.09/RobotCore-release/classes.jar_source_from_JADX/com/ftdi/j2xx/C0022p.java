package com.ftdi.j2xx;

import android.util.Log;

/* renamed from: com.ftdi.j2xx.p */
class C0022p implements Runnable {
    int f134a;
    private C0021o f135b;

    C0022p(C0021o c0021o) {
        this.f135b = c0021o;
        this.f134a = this.f135b.m149b().getBufferNumber();
    }

    public void run() {
        int i = 0;
        do {
            try {
                C0020n c = this.f135b.m152c(i);
                if (c.m134b() > 0) {
                    this.f135b.m147a(c);
                    c.m137c();
                }
                this.f135b.m154d(i);
                i = (i + 1) % this.f134a;
            } catch (InterruptedException e) {
                Log.d("ProcessRequestThread::", "Device has been closed.");
                e.printStackTrace();
                return;
            } catch (Exception e2) {
                Log.e("ProcessRequestThread::", "Fatal error!");
                e2.printStackTrace();
                return;
            }
        } while (!Thread.interrupted());
        throw new InterruptedException();
    }
}
