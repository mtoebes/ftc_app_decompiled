package com.ftdi.j2xx;

import java.nio.ByteBuffer;

/* renamed from: com.ftdi.j2xx.n */
class C0020n {
    private int f110a;
    private ByteBuffer f111b;
    private int f112c;
    private boolean f113d;

    public C0020n(int i) {
        this.f111b = ByteBuffer.allocate(i);
        m128b(0);
    }

    void m126a(int i) {
        this.f110a = i;
    }

    ByteBuffer m125a() {
        return this.f111b;
    }

    int m127b() {
        return this.f112c;
    }

    void m128b(int i) {
        this.f112c = i;
    }

    synchronized void m130c() {
        this.f111b.clear();
        m128b(0);
    }

    synchronized boolean m131d() {
        return this.f113d;
    }

    synchronized ByteBuffer m129c(int i) {
        ByteBuffer byteBuffer;
        byteBuffer = null;
        if (!this.f113d) {
            this.f113d = true;
            this.f110a = i;
            byteBuffer = this.f111b;
        }
        return byteBuffer;
    }

    synchronized boolean m132d(int i) {
        boolean z = false;
        synchronized (this) {
            if (this.f113d && i == this.f110a) {
                this.f113d = false;
                z = true;
            }
        }
        return z;
    }
}
