package com.qualcomm.robotcore.robocol;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class Heartbeat extends RobocolParsableBase {
    public static final short BUFFER_SIZE = (short) 16;
    public static final short PAYLOAD_SIZE = (short) 11;
    private long f309a;
    private RobotState f310b;

    /* renamed from: com.qualcomm.robotcore.robocol.Heartbeat.1 */
    static /* synthetic */ class C00431 {
        static final /* synthetic */ int[] f307a;

        static {
            f307a = new int[Token.values().length];
            try {
                f307a[Token.EMPTY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public enum Token {
        EMPTY
    }

    public Heartbeat() {
        this.f309a = System.nanoTime();
        this.f310b = RobotState.NOT_STARTED;
    }

    public Heartbeat(Token token) {
        switch (C00431.f307a[token.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                this.f309a = 0;
                this.f310b = RobotState.NOT_STARTED;
            default:
        }
    }

    public long getTimestamp() {
        return this.f309a;
    }

    public double getElapsedTime() {
        return ((double) (System.nanoTime() - this.f309a)) / ElapsedTime.dSECOND_IN_NANO;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.HEARTBEAT;
    }

    public byte getRobotState() {
        return this.f310b.asByte();
    }

    public void setRobotState(RobotState state) {
        this.f310b = state;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer writeBuffer = getWriteBuffer(11);
        try {
            writeBuffer.putLong(this.f309a);
            writeBuffer.put(this.f310b.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return writeBuffer.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < 16) {
            throw new RobotCoreException("Expected buffer of at least 16 bytes, received " + byteArray.length);
        }
        ByteBuffer readBuffer = getReadBuffer(byteArray);
        this.f309a = readBuffer.getLong();
        this.f310b = RobotState.fromByte(readBuffer.get());
    }

    public String toString() {
        return String.format("Heartbeat - seq: %4d, time: %d", new Object[]{Integer.valueOf(getSequenceNumber()), Long.valueOf(this.f309a)});
    }
}
