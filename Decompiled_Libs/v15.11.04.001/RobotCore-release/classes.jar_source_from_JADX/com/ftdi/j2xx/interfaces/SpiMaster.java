package com.ftdi.j2xx.interfaces;

public interface SpiMaster {
    int init(int i, int i2, int i3, int i4, byte b);

    int multiReadWrite(byte[] bArr, byte[] bArr2, int i, int i2, int i3, int[] iArr);

    int reset();

    int setLines(int i);

    int singleRead(byte[] bArr, int i, int[] iArr, boolean z);

    int singleReadWrite(byte[] bArr, byte[] bArr2, int i, int[] iArr, boolean z);

    int singleWrite(byte[] bArr, int i, int[] iArr, boolean z);
}
