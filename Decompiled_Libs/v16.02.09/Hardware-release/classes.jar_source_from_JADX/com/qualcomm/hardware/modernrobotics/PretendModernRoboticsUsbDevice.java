package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;

public class PretendModernRoboticsUsbDevice implements RobotUsbDevice {
    byte f173a;

    public PretendModernRoboticsUsbDevice() {
        this.f173a = (byte) 0;
    }

    public void close() {
    }

    public void setBaudRate(int i) throws RobotCoreException {
    }

    public void setDataCharacteristics(byte b, byte b1, byte b2) throws RobotCoreException {
    }

    public void setLatencyTimer(int i) throws RobotCoreException {
    }

    public void purge(Channel channel) throws RobotCoreException {
    }

    public int read(byte[] bytes) throws RobotCoreException {
        return read(bytes, bytes.length, 0);
    }

    public void write(byte[] bytes) throws RobotCoreException {
        this.f173a = bytes[2] == null ? (byte) 0 : bytes[4];
    }

    public int read(byte[] bytes, int cbReadExpected, int timeout) throws RobotCoreException {
        try {
            Thread.sleep(3, 500000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        bytes[0] = (byte) 51;
        bytes[1] = (byte) -52;
        bytes[4] = this.f173a;
        return cbReadExpected;
    }
}
