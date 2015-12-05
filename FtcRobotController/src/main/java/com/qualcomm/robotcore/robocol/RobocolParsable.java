package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

public interface RobocolParsable {
    public static final int HEADER_LENGTH = 3;
    public static final byte[] EMPTY_HEADER_BUFFER = new byte[HEADER_LENGTH];;

    public enum MsgType {
        EMPTY(0),
        HEARTBEAT(1),
        GAMEPAD(2),
        PEER_DISCOVERY(RobocolParsable.HEADER_LENGTH),
        COMMAND(4),
        TELEMETRY(5);
        
        private static final MsgType[] messageTypes = values();
        private final int type;

        public static MsgType fromByte(byte b) {
            MsgType msgType = EMPTY;
            try {
                return messageTypes[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to MsgType: %s", b, e.toString()));
                return msgType;
            }
        }

        private MsgType(int type) {
            this.type = type;
        }

        public byte asByte() {
            return (byte) this.type;
        }
    }

    void fromByteArray(byte[] bArr) throws RobotCoreException;

    MsgType getRobocolMsgType();

    byte[] toByteArray() throws RobotCoreException;
}
