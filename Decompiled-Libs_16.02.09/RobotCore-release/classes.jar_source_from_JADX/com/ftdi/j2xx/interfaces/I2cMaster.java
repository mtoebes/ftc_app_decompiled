package com.ftdi.j2xx.interfaces;

public interface I2cMaster {
    int getStatus(int i, byte[] bArr);

    int init(int i);

    int read(int i, byte[] bArr, int i2, int[] iArr);

    int readEx(int i, int i2, byte[] bArr, int i3, int[] iArr);

    int reset();

    int write(int i, byte[] bArr, int i2, int[] iArr);

    int writeEx(int i, int i2, byte[] bArr, int i3, int[] iArr);
}
