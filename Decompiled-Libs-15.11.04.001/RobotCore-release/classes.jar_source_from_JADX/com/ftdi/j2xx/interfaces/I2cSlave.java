package com.ftdi.j2xx.interfaces;

public interface I2cSlave {
    int getAddress(int[] iArr);

    int init();

    int read(byte[] bArr, int i, int[] iArr);

    int reset();

    int setAddress(int i);

    int write(byte[] bArr, int i, int[] iArr);
}
