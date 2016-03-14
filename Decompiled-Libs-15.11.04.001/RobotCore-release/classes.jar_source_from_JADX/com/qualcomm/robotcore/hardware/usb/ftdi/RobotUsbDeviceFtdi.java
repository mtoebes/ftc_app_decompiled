package com.qualcomm.robotcore.hardware.usb.ftdi;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class RobotUsbDeviceFtdi implements RobotUsbDevice {
    private FT_Device f301a;

    /* renamed from: com.qualcomm.robotcore.hardware.usb.ftdi.RobotUsbDeviceFtdi.1 */
    static /* synthetic */ class C00371 {
        static final /* synthetic */ int[] f300a;

        static {
            f300a = new int[Channel.values().length];
            try {
                f300a[Channel.RX.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f300a[Channel.TX.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f300a[Channel.BOTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public RobotUsbDeviceFtdi(FT_Device device) {
        this.f301a = device;
    }

    public void setBaudRate(int rate) throws RobotCoreException {
        if (!this.f301a.setBaudRate(rate)) {
            throw new RobotCoreException("FTDI driver failed to set baud rate to " + rate);
        }
    }

    public void setDataCharacteristics(byte dataBits, byte stopBits, byte parity) throws RobotCoreException {
        if (!this.f301a.setDataCharacteristics(dataBits, stopBits, parity)) {
            throw new RobotCoreException("FTDI driver failed to set data characteristics");
        }
    }

    public void setLatencyTimer(int latencyTimer) throws RobotCoreException {
        if (!this.f301a.setLatencyTimer((byte) latencyTimer)) {
            throw new RobotCoreException("FTDI driver failed to set latency timer to " + latencyTimer);
        }
    }

    public void purge(Channel channel) throws RobotCoreException {
        byte b = (byte) 0;
        switch (C00371.f300a[channel.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                b = (byte) 1;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                b = (byte) 2;
                break;
            case RobocolConfig.TTL /*3*/:
                b = (byte) 3;
                break;
        }
        this.f301a.purge(b);
    }

    public void write(byte[] data) throws RobotCoreException {
        this.f301a.write(data);
    }

    public int read(byte[] data) throws RobotCoreException {
        return this.f301a.read(data);
    }

    public int read(byte[] data, int length, int timeout) throws RobotCoreException {
        return this.f301a.read(data, length, (long) timeout);
    }

    public void close() {
        this.f301a.close();
    }
}
