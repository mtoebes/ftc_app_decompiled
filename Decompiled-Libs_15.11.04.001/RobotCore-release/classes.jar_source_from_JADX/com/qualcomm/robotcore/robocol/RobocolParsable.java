package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

public interface RobocolParsable {
    public static final byte[] EMPTY_HEADER_BUFFER;
    public static final int HEADER_LENGTH = 3;

    public enum MsgType {
        EMPTY(0),
        HEARTBEAT(1),
        GAMEPAD(2),
        PEER_DISCOVERY(RobocolParsable.HEADER_LENGTH),
        COMMAND(4),
        TELEMETRY(5);
        
        private static final MsgType[] f335a;
        private final int f337b;

        static {
            f335a = values();
        }

        public static MsgType fromByte(byte b) {
            MsgType msgType = EMPTY;
            try {
                return f335a[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.m234w(String.format("Cannot convert %d to MsgType: %s", new Object[]{Byte.valueOf(b), e.toString()}));
                return msgType;
            }
        }

        private MsgType(int type) {
            this.f337b = type;
        }

        public byte asByte() {
            return (byte) this.f337b;
        }
    }

    void fromByteArray(byte[] bArr) throws RobotCoreException;

    MsgType getRobocolMsgType();

    byte[] toByteArray() throws RobotCoreException;

    static {
        EMPTY_HEADER_BUFFER = new byte[HEADER_LENGTH];
    }
}
