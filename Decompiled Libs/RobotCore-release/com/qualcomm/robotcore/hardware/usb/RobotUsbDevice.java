package com.qualcomm.robotcore.hardware.usb;

import com.qualcomm.robotcore.exception.RobotCoreException;

public interface RobotUsbDevice {

    public enum Channel {
        RX,
        TX,
        NONE,
        BOTH
    }

    void close();

    void purge(Channel channel) throws RobotCoreException;

    int read(byte[] bArr) throws RobotCoreException;

    int read(byte[] bArr, int i, int i2) throws RobotCoreException;

    void setBaudRate(int i) throws RobotCoreException;

    void setDataCharacteristics(byte b, byte b2, byte b3) throws RobotCoreException;

    void setLatencyTimer(int i) throws RobotCoreException;

    void write(byte[] bArr) throws RobotCoreException;
}
