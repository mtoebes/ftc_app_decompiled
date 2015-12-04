package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class RobocolDatagram {
    private DatagramPacket f328a;

    public RobocolDatagram(RobocolParsable message) throws RobotCoreException {
        setData(message.toByteArray());
    }

    public RobocolDatagram(byte[] message) {
        setData(message);
    }

    protected RobocolDatagram(DatagramPacket packet) {
        this.f328a = packet;
    }

    protected RobocolDatagram() {
        this.f328a = null;
    }

    public MsgType getMsgType() {
        return MsgType.fromByte(this.f328a.getData()[0]);
    }

    public int getLength() {
        return this.f328a.getLength();
    }

    public int getPayloadLength() {
        return this.f328a.getLength() - 3;
    }

    public byte[] getData() {
        return this.f328a.getData();
    }

    public void setData(byte[] data) {
        this.f328a = new DatagramPacket(data, data.length);
    }

    public InetAddress getAddress() {
        return this.f328a.getAddress();
    }

    public void setAddress(InetAddress address) {
        this.f328a.setAddress(address);
    }

    public String toString() {
        int i;
        String str = "NONE";
        String str2 = null;
        if (this.f328a == null || this.f328a.getAddress() == null || this.f328a.getLength() <= 0) {
            i = 0;
        } else {
            str = MsgType.fromByte(this.f328a.getData()[0]).name();
            i = this.f328a.getLength();
            str2 = this.f328a.getAddress().getHostAddress();
        }
        return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", new Object[]{str, str2, Integer.valueOf(i)});
    }

    protected DatagramPacket getPacket() {
        return this.f328a;
    }

    protected void setPacket(DatagramPacket packet) {
        this.f328a = packet;
    }
}
