package com.qualcomm.robotcore.hardware.usb.ftdi;

import com.ftdi.j2xx.FT_Device;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;

public class RobotUsbDeviceFtdi implements RobotUsbDevice {
    private FT_Device ft_device;

    public RobotUsbDeviceFtdi(FT_Device device) {
        this.ft_device = device;
    }

    public void setBaudRate(int rate) throws RobotCoreException {
        if (!this.ft_device.setBaudRate(rate)) {
            throw new RobotCoreException("FTDI driver failed to set baud rate to " + rate);
        }
    }

    public void setDataCharacteristics(byte dataBits, byte stopBits, byte parity) throws RobotCoreException {
        if (!this.ft_device.setDataCharacteristics(dataBits, stopBits, parity)) {
            throw new RobotCoreException("FTDI driver failed to set data characteristics");
        }
    }

    public void setLatencyTimer(int latencyTimer) throws RobotCoreException {
        if (!this.ft_device.setLatencyTimer((byte) latencyTimer)) {
            throw new RobotCoreException("FTDI driver failed to set latency timer to " + latencyTimer);
        }
    }

    public void purge(Channel channel) throws RobotCoreException {
        byte b = 0;
        switch (channel) {
            case NONE :
                b = 0;
                break;
            case RX :
                b = 1;
                break;
            case TX :
                b = 2;
                break;
            case BOTH :
                b = 3;
                break;
        }

        this.ft_device.purge(b);
    }

    public void write(byte[] data) throws RobotCoreException {
        this.ft_device.write(data);
    }

    public int read(byte[] data) throws RobotCoreException {
        return this.ft_device.read(data);
    }

    public int read(byte[] data, int length, int timeout) throws RobotCoreException {
        return this.ft_device.read(data, length, (long) timeout);
    }

    public void close() {
        this.ft_device.close();
    }
}
