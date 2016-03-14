package com.ftdi.j2xx.ft4222;

/* renamed from: com.ftdi.j2xx.ft4222.b */
class C0010b {
    byte f84a;
    byte f85b;
    byte f86c;
    byte f87d;
    byte f88e;
    byte f89f;
    byte f90g;
    byte f91h;
    byte f92i;
    byte f93j;
    byte[] f94k;

    public C0010b() {
        this.f94k = new byte[3];
    }

    void m94a(byte[] bArr) {
        this.f84a = bArr[0];
        this.f85b = bArr[1];
        this.f86c = bArr[2];
        this.f87d = bArr[3];
        this.f88e = bArr[4];
        this.f89f = bArr[5];
        this.f90g = bArr[6];
        this.f91h = bArr[7];
        this.f92i = bArr[8];
        this.f93j = bArr[9];
        this.f94k[0] = bArr[10];
        this.f94k[1] = bArr[11];
        this.f94k[2] = bArr[12];
    }
}
