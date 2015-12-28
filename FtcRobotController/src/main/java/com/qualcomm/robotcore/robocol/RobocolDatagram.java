package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class RobocolDatagram {
    private DatagramPacket packet;

    public RobocolDatagram(RobocolParsable message) throws RobotCoreException {
        setData(message.toByteArray());
    }

    public RobocolDatagram(byte[] message) {
        setData(message);
    }

    protected RobocolDatagram(DatagramPacket packet) {
        this.packet = packet;
    }

    protected RobocolDatagram() {
    }

    public MsgType getMsgType() {
        return MsgType.fromByte(this.packet.getData()[0]);
    }

    public int getLength() {
        return this.packet.getLength();
    }

    public int getPayloadLength() {
        return this.packet.getLength() - RobocolParsable.HEADER_LENGTH;
    }

    public byte[] getData() {
        return this.packet.getData();
    }

    public void setData(byte[] data) {
        this.packet = new DatagramPacket(data, data.length);
    }

    public InetAddress getAddress() {
        return this.packet.getAddress();
    }

    public void setAddress(InetAddress address) {
        this.packet.setAddress(address);
    }

    public String toString() {
        int length;
        String messageType = "NONE";
        String hostAddress = null;
        if ((this.packet == null) || (this.packet.getAddress() == null) || (this.packet.getLength() <= 0)) {
            length = 0;
        } else {
            messageType = MsgType.fromByte(this.packet.getData()[0]).name();
            length = this.packet.getLength();
            hostAddress = this.packet.getAddress().getHostAddress();
        }
        return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", new Object[]{messageType, hostAddress, length});
    }

    protected DatagramPacket getPacket() {
        return this.packet;
    }

    protected void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }
}
