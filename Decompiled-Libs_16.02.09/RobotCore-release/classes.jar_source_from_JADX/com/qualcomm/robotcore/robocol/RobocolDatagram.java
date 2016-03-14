package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RobocolDatagram {
    static Queue<byte[]> f321a;
    private DatagramPacket f322b;
    private byte[] f323c;

    static {
        f321a = new ConcurrentLinkedQueue();
    }

    public RobocolDatagram(RobocolParsable message) throws RobotCoreException {
        this.f323c = null;
        setData(message.toByteArrayForTransmission());
    }

    public RobocolDatagram(byte[] message) {
        this.f323c = null;
        setData(message);
    }

    public static RobocolDatagram forReceive() {
        byte[] bArr = (byte[]) f321a.poll();
        if (bArr == null) {
            bArr = new byte[RobocolConfig.MAX_PACKET_SIZE];
        }
        DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length);
        RobocolDatagram robocolDatagram = new RobocolDatagram();
        robocolDatagram.f322b = datagramPacket;
        robocolDatagram.f323c = bArr;
        return robocolDatagram;
    }

    protected RobocolDatagram() {
        this.f323c = null;
        this.f322b = null;
    }

    public void close() {
        if (this.f323c != null) {
            f321a.add(this.f323c);
            this.f323c = null;
        }
        this.f322b = null;
    }

    public MsgType getMsgType() {
        return MsgType.fromByte(this.f322b.getData()[0]);
    }

    public int getLength() {
        return this.f322b.getLength();
    }

    public int getPayloadLength() {
        return this.f322b.getLength() - 5;
    }

    public byte[] getData() {
        return this.f322b.getData();
    }

    public void setData(byte[] data) {
        this.f322b = new DatagramPacket(data, data.length);
    }

    public InetAddress getAddress() {
        return this.f322b.getAddress();
    }

    public void setAddress(InetAddress address) {
        this.f322b.setAddress(address);
    }

    public String toString() {
        int i;
        String str = "NONE";
        String str2 = null;
        if (this.f322b == null || this.f322b.getAddress() == null || this.f322b.getLength() <= 0) {
            i = 0;
        } else {
            str = MsgType.fromByte(this.f322b.getData()[0]).name();
            i = this.f322b.getLength();
            str2 = this.f322b.getAddress().getHostAddress();
        }
        return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", new Object[]{str, str2, Integer.valueOf(i)});
    }

    protected DatagramPacket getPacket() {
        return this.f322b;
    }

    protected void setPacket(DatagramPacket packet) {
        this.f322b = packet;
    }
}
