package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;

public interface RobocolParsable {
    public static final int HEADER_LENGTH = 5;

    public enum MsgType {
        EMPTY(0),
        HEARTBEAT(1),
        GAMEPAD(2),
        PEER_DISCOVERY(3),
        COMMAND(4),
        TELEMETRY(RobocolParsable.HEADER_LENGTH);
        
        private static final MsgType[] f330a;
        private final int f332b;

        static {
            f330a = values();
        }

        public static MsgType fromByte(byte b) {
            MsgType msgType = EMPTY;
            try {
                return f330a[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.m256w(String.format("Cannot convert %d to MsgType: %s", new Object[]{Byte.valueOf(b), e.toString()}));
                return msgType;
            }
        }

        private MsgType(int type) {
            this.f332b = type;
        }

        public byte asByte() {
            return (byte) this.f332b;
        }
    }

    void fromByteArray(byte[] bArr) throws RobotCoreException;

    MsgType getRobocolMsgType();

    int getSequenceNumber();

    void setSequenceNumber();

    boolean shouldTransmit(long j);

    byte[] toByteArray() throws RobotCoreException;

    byte[] toByteArrayForTransmission() throws RobotCoreException;
}
