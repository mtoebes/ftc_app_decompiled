package com.qualcomm.robotcore.robocol;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class PeerDiscovery implements RobocolParsable {
    public static final short BUFFER_SIZE = (short) 13;
    public static final short PAYLOAD_SIZE = (short) 10;
    public static final byte ROBOCOL_VERSION = (byte) 1;
    private PeerType f321a;

    public enum PeerType {
        NOT_SET(0),
        PEER(1),
        GROUP_OWNER(2);
        
        private static final PeerType[] f318a;
        private int f320b;

        static {
            f318a = values();
        }

        public static PeerType fromByte(byte b) {
            PeerType peerType = NOT_SET;
            try {
                return f318a[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.m234w(String.format("Cannot convert %d to Peer: %s", new Object[]{Byte.valueOf(b), e.toString()}));
                return peerType;
            }
        }

        private PeerType(int type) {
            this.f320b = type;
        }

        public byte asByte() {
            return (byte) this.f320b;
        }
    }

    public PeerDiscovery(PeerType peerType) {
        this.f321a = peerType;
    }

    public PeerType getPeerType() {
        return this.f321a;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.PEER_DISCOVERY;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(13);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.put(ROBOCOL_VERSION);
            allocate.put(this.f321a.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < 13) {
            throw new RobotCoreException("Expected buffer of at least 13 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, 10);
        switch (wrap.get()) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                this.f321a = PeerType.fromByte(wrap.get());
            default:
        }
    }

    public String toString() {
        return String.format("Peer Discovery - peer type: %s", new Object[]{this.f321a.name()});
    }
}
