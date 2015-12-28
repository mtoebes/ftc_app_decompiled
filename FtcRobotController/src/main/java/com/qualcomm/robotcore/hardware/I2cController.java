package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;
import java.util.concurrent.locks.Lock;

public interface I2cController extends HardwareDevice {
    byte I2C_BUFFER_START_ADDRESS = 4;

    interface I2cPortReadyCallback {
        void portIsReady(int port);
    }

    void copyBufferIntoWriteBuffer(int physicalPort, byte[] bArr);

    void deregisterForPortReadyCallback(int port);

    void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length);

    void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length);

    byte[] getCopyOfReadBuffer(int physicalPort);

    byte[] getCopyOfWriteBuffer(int physicalPort);

    byte[] getI2cReadCache(int port);

    Lock getI2cReadCacheLock(int port);

    byte[] getI2cWriteCache(int port);

    Lock getI2cWriteCacheLock(int port);

    SerialNumber getSerialNumber();

    boolean isI2cPortActionFlagSet(int port);

    boolean isI2cPortInReadMode(int port);

    boolean isI2cPortInWriteMode(int port);

    boolean isI2cPortReady(int port);

    void readI2cCacheFromController(int port);

    @Deprecated
    void readI2cCacheFromModule(int port);

    void registerForI2cPortReadyCallback(I2cPortReadyCallback i2cPortReadyCallback, int port);

    void setI2cPortActionFlag(int port);

    void writeI2cCacheToController(int port);

    @Deprecated
    void writeI2cCacheToModule(int port);

    void writeI2cPortFlagOnlyToController(int port);

    @Deprecated
    void writeI2cPortFlagOnlyToModule(int port);
}
