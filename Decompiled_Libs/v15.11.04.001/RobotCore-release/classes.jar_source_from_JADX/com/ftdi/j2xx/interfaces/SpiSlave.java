package com.ftdi.j2xx.interfaces;

public interface SpiSlave {
    int getRxStatus(int[] iArr);

    int init();

    int read(byte[] bArr, int i, int[] iArr);

    int reset();

    int write(byte[] bArr, int i, int[] iArr);
}
