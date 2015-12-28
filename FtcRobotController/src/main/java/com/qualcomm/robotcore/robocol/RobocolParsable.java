package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.charset.Charset;

public interface RobocolParsable {
    int HEADER_LENGTH = 3;
    byte[] EMPTY_HEADER_BUFFER = new byte[HEADER_LENGTH];

    Charset CHARSET = Charset.forName("UTF-8");

    enum MsgType {
        EMPTY(0),
        HEARTBEAT(1),
        GAMEPAD(2),
        PEER_DISCOVERY(RobocolParsable.HEADER_LENGTH),
        COMMAND(4),
        TELEMETRY(5);
        
        private static final MsgType[] MESSAGE_TYPES;
        private final int f337b;

        static {
            MESSAGE_TYPES = values();
        }

        public static MsgType fromByte(byte b) {
            MsgType msgType = EMPTY;
            try {
                return MESSAGE_TYPES[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to MsgType: %s", new Object[]{b, e.toString()}));
                return msgType;
            }
        }

        MsgType(int type) {
            this.f337b = type;
        }

        public byte asByte() {
            return (byte) this.f337b;
        }
    }

    void fromByteArray(byte[] bArr) throws RobotCoreException;

    MsgType getRobocolMsgType();

    byte[] toByteArray() throws RobotCoreException;
}
