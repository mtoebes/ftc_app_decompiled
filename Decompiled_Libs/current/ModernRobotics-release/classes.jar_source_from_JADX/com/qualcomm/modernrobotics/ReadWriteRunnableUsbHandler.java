package com.qualcomm.modernrobotics;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice.Channel;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Util;
import java.util.Arrays;

public class ReadWriteRunnableUsbHandler {
    protected final int MAX_SEQUENTIAL_USB_ERROR_COUNT;
    protected final int USB_MSG_TIMEOUT;
    protected RobotUsbDevice device;
    protected byte[] readCmd;
    protected final byte[] respHeader;
    protected int usbSequentialReadErrorCount;
    protected int usbSequentialWriteErrorCount;
    protected byte[] writeCmd;

    public ReadWriteRunnableUsbHandler(RobotUsbDevice device) {
        this.MAX_SEQUENTIAL_USB_ERROR_COUNT = 10;
        this.USB_MSG_TIMEOUT = 100;
        this.usbSequentialReadErrorCount = 0;
        this.usbSequentialWriteErrorCount = 0;
        this.respHeader = new byte[5];
        this.writeCmd = new byte[]{(byte) 85, (byte) -86, (byte) 0, (byte) 0, (byte) 0};
        this.readCmd = new byte[]{(byte) 85, (byte) -86, Byte.MIN_VALUE, (byte) 0, (byte) 0};
        this.device = device;
    }

    public void throwIfUsbErrorCountIsTooHigh() throws RobotCoreException {
        if (this.usbSequentialReadErrorCount > 10 || this.usbSequentialWriteErrorCount > 10) {
            throw new RobotCoreException("Too many sequential USB errors on device");
        }
    }

    public void read(int address, byte[] buffer) throws RobotCoreException, InterruptedException {
        m4a(address, buffer);
    }

    private void m4a(int i, byte[] bArr) throws RobotCoreException, InterruptedException {
        this.readCmd[3] = (byte) i;
        this.readCmd[4] = (byte) bArr.length;
        this.device.write(this.readCmd);
        Arrays.fill(this.respHeader, (byte) 0);
        int read = this.device.read(this.respHeader, this.respHeader.length, 100);
        if (!C0000a.m7a(this.respHeader, bArr.length)) {
            this.usbSequentialReadErrorCount++;
            if (read == this.respHeader.length) {
                Thread.sleep(100);
                m5a(this.readCmd, "comm error");
            } else {
                m5a(this.readCmd, "comm timeout");
            }
        }
        if (this.device.read(bArr, bArr.length, 100) != bArr.length) {
            m5a(this.readCmd, "comm timeout on payload");
        }
        this.usbSequentialReadErrorCount = 0;
    }

    public void write(int address, byte[] buffer) throws RobotCoreException, InterruptedException {
        m6b(address, buffer);
    }

    private void m6b(int i, byte[] bArr) throws RobotCoreException, InterruptedException {
        this.writeCmd[3] = (byte) i;
        this.writeCmd[4] = (byte) bArr.length;
        this.device.write(Util.concatenateByteArrays(this.writeCmd, bArr));
        Arrays.fill(this.respHeader, (byte) 0);
        int read = this.device.read(this.respHeader, this.respHeader.length, 100);
        if (!C0000a.m7a(this.respHeader, 0)) {
            this.usbSequentialWriteErrorCount++;
            if (read == this.respHeader.length) {
                Thread.sleep(100);
                m5a(this.writeCmd, "comm error");
            } else {
                m5a(this.writeCmd, "comm timeout");
            }
        }
        this.usbSequentialWriteErrorCount = 0;
    }

    public void purge(Channel channel) throws RobotCoreException {
        this.device.purge(channel);
    }

    public void close() {
        this.device.close();
    }

    private void m5a(byte[] bArr, String str) throws RobotCoreException {
        RobotLog.w(bufferToString(bArr) + " -> " + bufferToString(this.respHeader));
        this.device.purge(Channel.BOTH);
        throw new RobotCoreException(str);
    }

    protected static String bufferToString(byte[] buffer) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        if (buffer.length > 0) {
            stringBuilder.append(String.format("%02x", new Object[]{Byte.valueOf(buffer[0])}));
        }
        for (int i = 1; i < buffer.length; i++) {
            stringBuilder.append(String.format(" %02x", new Object[]{Byte.valueOf(buffer[i])}));
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
