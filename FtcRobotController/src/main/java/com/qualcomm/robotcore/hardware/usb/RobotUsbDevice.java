package com.qualcomm.robotcore.hardware.usb;

import com.qualcomm.robotcore.exception.RobotCoreException;

public interface RobotUsbDevice {

    enum Channel {
        RX,
        TX,
        NONE,
        BOTH
    }

    void close();

    void purge(Channel channel) throws RobotCoreException;

    int read(byte[] data) throws RobotCoreException;

    int read(byte[] data, int length, int timeout) throws RobotCoreException;

    void setBaudRate(int i) throws RobotCoreException;

    void setDataCharacteristics(byte dataBits, byte stopBits, byte parity) throws RobotCoreException;

    void setLatencyTimer(int latencyTimer) throws RobotCoreException;

    void write(byte[] data) throws RobotCoreException;
}
