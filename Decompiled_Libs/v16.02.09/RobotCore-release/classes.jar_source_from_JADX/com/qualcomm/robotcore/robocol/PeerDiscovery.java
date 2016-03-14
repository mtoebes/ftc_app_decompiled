package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class PeerDiscovery extends RobocolParsableBase {
    public static final byte ROBOCOL_VERSION = (byte) 4;
    private PeerType f314a;

    public enum PeerType {
        NOT_SET(0),
        PEER(1),
        GROUP_OWNER(2);
        
        private static final PeerType[] f311a;
        private int f313b;

        static {
            f311a = values();
        }

        public static PeerType fromByte(byte b) {
            PeerType peerType = NOT_SET;
            try {
                return f311a[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.m256w(String.format("Cannot convert %d to Peer: %s", new Object[]{Byte.valueOf(b), e.toString()}));
                return peerType;
            }
        }

        private PeerType(int type) {
            this.f313b = type;
        }

        public byte asByte() {
            return (byte) this.f313b;
        }
    }

    private PeerDiscovery() {
        this.f314a = PeerType.NOT_SET;
    }

    public static PeerDiscovery forReceive() {
        return new PeerDiscovery();
    }

    public PeerDiscovery(PeerType peerType) {
        this.f314a = peerType;
    }

    public PeerType getPeerType() {
        return this.f314a;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.PEER_DISCOVERY;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocateWholeWriteBuffer = allocateWholeWriteBuffer(13);
        try {
            allocateWholeWriteBuffer.put(getRobocolMsgType().asByte());
            allocateWholeWriteBuffer.putShort((short) 10);
            allocateWholeWriteBuffer.put(ROBOCOL_VERSION);
            allocateWholeWriteBuffer.put(this.f314a.asByte());
            allocateWholeWriteBuffer.putShort((short) this.sequenceNumber);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocateWholeWriteBuffer.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < 13) {
            throw new RobotCoreException("Expected buffer of at least %d bytes, received %d", Integer.valueOf(13), Integer.valueOf(byteArray.length));
        }
        ByteBuffer wholeReadBuffer = getWholeReadBuffer(byteArray);
        wholeReadBuffer.get();
        wholeReadBuffer.getShort();
        byte b = wholeReadBuffer.get();
        byte b2 = wholeReadBuffer.get();
        short s = wholeReadBuffer.getShort();
        if (b != ROBOCOL_VERSION) {
            throw new RobotCoreException("Incompatible apps: v%d vs v%d", Byte.valueOf(ROBOCOL_VERSION), Byte.valueOf(b));
        }
        this.f314a = PeerType.fromByte(b2);
        if (b > (byte) 1) {
            setSequenceNumber(s);
        }
    }

    public String toString() {
        return String.format("Peer Discovery - peer type: %s", new Object[]{this.f314a.name()});
    }
}
