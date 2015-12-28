package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class PeerDiscovery implements RobocolParsable {
    public static final short PAYLOAD_SIZE = (short) 10;
    public static final byte ROBOCOL_VERSION = (byte) 1;
    public static final short BUFFER_SIZE = PAYLOAD_SIZE + HEADER_LENGTH;

    private PeerType peerType;

    public enum PeerType {
        NOT_SET(0),
        PEER(1),
        GROUP_OWNER(2);
        
        private static final PeerType[] peerTypes = values();
        private final int type;

        public static PeerType fromByte(byte b) {
            try {
                return peerTypes[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to Peer: %s", b, e.toString()));
                return NOT_SET;
            }
        }

        PeerType(int type) {
            this.type = type;
        }

        public byte asByte() {
            return (byte) this.type;
        }
    }

    public PeerDiscovery(PeerType peerType) {
        this.peerType = peerType;
    }

    public PeerType getPeerType() {
        return this.peerType;
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
            allocate.put(this.peerType.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least 13 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);

        if((PeerType.fromByte(wrap.get())) == PeerType.PEER) {
            this.peerType = PeerType.fromByte(wrap.get());
        }
    }

    public String toString() {
        return String.format("Peer Discovery - peer type: %s", this.peerType.name());
    }
}
