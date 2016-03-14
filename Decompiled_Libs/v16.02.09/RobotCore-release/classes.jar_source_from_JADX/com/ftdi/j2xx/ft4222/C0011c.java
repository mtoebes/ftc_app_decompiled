package com.ftdi.j2xx.ft4222;

/* renamed from: com.ftdi.j2xx.ft4222.c */
class C0011c {
    byte f95a;
    byte f96b;
    byte[] f97c;

    public C0011c(char[] cArr) {
        if (cArr[0] < 'B') {
            this.f97c = new byte[3];
        } else {
            this.f97c = new byte[1];
        }
    }
}
