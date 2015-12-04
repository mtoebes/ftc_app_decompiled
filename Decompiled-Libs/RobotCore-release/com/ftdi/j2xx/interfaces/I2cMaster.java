package com.ftdi.j2xx.interfaces;

public interface I2cMaster {
    int init(int i);

    int read(int i, byte[] bArr, int i2, int[] iArr);

    int reset();

    int write(int i, byte[] bArr, int i2, int[] iArr);
}
