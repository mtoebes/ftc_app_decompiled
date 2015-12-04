package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.concurrent.locks.Lock;

public interface I2cController extends HardwareDevice {
    public static final byte I2C_BUFFER_START_ADDRESS = (byte) 4;

    public interface I2cPortReadyCallback {
        void portIsReady(int i);
    }

    void copyBufferIntoWriteBuffer(int i, byte[] bArr);

    void deregisterForPortReadyCallback(int i);

    void enableI2cReadMode(int i, int i2, int i3, int i4);

    void enableI2cWriteMode(int i, int i2, int i3, int i4);

    byte[] getCopyOfReadBuffer(int i);

    byte[] getCopyOfWriteBuffer(int i);

    byte[] getI2cReadCache(int i);

    Lock getI2cReadCacheLock(int i);

    byte[] getI2cWriteCache(int i);

    Lock getI2cWriteCacheLock(int i);

    SerialNumber getSerialNumber();

    boolean isI2cPortActionFlagSet(int i);

    boolean isI2cPortInReadMode(int i);

    boolean isI2cPortInWriteMode(int i);

    boolean isI2cPortReady(int i);

    void readI2cCacheFromController(int i);

    @Deprecated
    void readI2cCacheFromModule(int i);

    void registerForI2cPortReadyCallback(I2cPortReadyCallback i2cPortReadyCallback, int i);

    void setI2cPortActionFlag(int i);

    void writeI2cCacheToController(int i);

    @Deprecated
    void writeI2cCacheToModule(int i);

    void writeI2cPortFlagOnlyToController(int i);

    @Deprecated
    void writeI2cPortFlagOnlyToModule(int i);
}
