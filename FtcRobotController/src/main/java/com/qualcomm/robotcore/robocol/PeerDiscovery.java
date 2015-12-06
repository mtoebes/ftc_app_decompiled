package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class PeerDiscovery implements RobocolParsable {
    public static final short BUFFER_SIZE = (short) 13;
    public static final short PAYLOAD_SIZE = (short) 10;
    public static final byte ROBOCOL_VERSION = (byte) 1;
    private PeerType peerType;

    public enum PeerType {
        NOT_SET(0),
        PEER(1),
        GROUP_OWNER(2);
        
        private static final PeerType[] peerTypes;
        private int type;

        static {
            peerTypes = values();
        }

        public static PeerType fromByte(byte b) {
            PeerType peerType = NOT_SET;
            try {
                return peerTypes[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to Peer: %s", b, e.toString()));
                return peerType;
            }
        }

        PeerType(int type) {
            this.type = type;
        }

        public byte asByte() {
            return (byte) type;
        }
    }

    public PeerDiscovery(PeerType peerType) {
        this.peerType = peerType;
    }

    public PeerType getPeerType() {
        return peerType;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.PEER_DISCOVERY;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.put(ROBOCOL_VERSION);
            allocate.put(peerType.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least " + BUFFER_SIZE + " bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);

        switch (PeerType.fromByte(wrap.get())) {
            case PEER :
                peerType = PeerType.fromByte(wrap.get());
                break;
            default:
        }
    }

    public String toString() {
        return String.format("Peer Discovery - peer type: %s", peerType.name());
    }
}
