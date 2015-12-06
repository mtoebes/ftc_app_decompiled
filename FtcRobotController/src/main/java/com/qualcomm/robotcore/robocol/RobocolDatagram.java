package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class RobocolDatagram {
    private DatagramPacket packet = null;

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
        this.packet = null;
    }

    public MsgType getMsgType() {
        return MsgType.fromByte(packet.getData()[0]);
    }

    public int getLength() {
        return packet.getLength();
    }

    public int getPayloadLength() {
        return packet.getLength() - RobocolParsable.HEADER_LENGTH;
    }

    public byte[] getData() {
        return packet.getData();
    }

    public void setData(byte[] data) {
        packet = new DatagramPacket(data, data.length);
    }

    public InetAddress getAddress() {
        return packet.getAddress();
    }

    public void setAddress(InetAddress address) {
        packet.setAddress(address);
    }

    public String toString() {
        int size;
        String type = "NONE";
        String address = null;
        if (packet == null || packet.getAddress() == null || packet.getLength() <= 0) {
            size = 0;
        } else {
            type = MsgType.fromByte(packet.getData()[0]).name();
            size = packet.getLength();
            address = packet.getAddress().getHostAddress();
        }
        return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", type, address, size);
    }

    protected DatagramPacket getPacket() {
        return packet;
    }

    protected void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }
}
