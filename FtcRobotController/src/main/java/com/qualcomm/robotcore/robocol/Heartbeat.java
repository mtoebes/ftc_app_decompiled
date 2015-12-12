package com.qualcomm.robotcore.robocol;

import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class Heartbeat implements RobocolParsable {
    public static final short BUFFER_SIZE = (short) 14;
    public static final short MAX_SEQUENCE_NUMBER = (short) 10000;
    public static final short PAYLOAD_SIZE = (short) 11;
    private static short f314a;
    private long f315b;
    private short f316c;
    private RobotState f317d;

    /* renamed from: com.qualcomm.robotcore.robocol.Heartbeat.1 */
    static /* synthetic */ class C00381 {
        static final /* synthetic */ int[] f312a;

        static {
            f312a = new int[Token.values().length];
            try {
                f312a[Token.EMPTY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public enum Token {
        EMPTY
    }

    static {
        f314a = (short) 0;
    }

    public Heartbeat() {
        this.f316c = m207a();
        this.f315b = System.nanoTime();
        this.f317d = RobotState.NOT_STARTED;
    }

    public Heartbeat(Token token) {
        switch (C00381.f312a[token.ordinal()]) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                this.f316c = (short) 0;
                this.f315b = 0;
                this.f317d = RobotState.NOT_STARTED;
            default:
        }
    }

    public long getTimestamp() {
        return this.f315b;
    }

    public double getElapsedTime() {
        return ((double) (System.nanoTime() - this.f315b)) / 1.0E9d;
    }

    public short getSequenceNumber() {
        return this.f316c;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.HEARTBEAT;
    }

    public byte getRobotState() {
        return this.f317d.asByte();
    }

    public void setRobotState(RobotState state) {
        this.f317d = state;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(14);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.putShort(this.f316c);
            allocate.putLong(this.f315b);
            allocate.put(this.f317d.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < 14) {
            throw new RobotCoreException("Expected buffer of at least 14 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, 11);
        this.f316c = wrap.getShort();
        this.f315b = wrap.getLong();
        this.f317d = RobotState.fromByte(wrap.get());
    }

    public String toString() {
        return String.format("Heartbeat - seq: %4d, time: %d", new Object[]{Short.valueOf(this.f316c), Long.valueOf(this.f315b)});
    }

    private static synchronized short m207a() {
        short s;
        synchronized (Heartbeat.class) {
            s = f314a;
            f314a = (short) (f314a + 1);
            if (f314a > MAX_SEQUENCE_NUMBER) {
                f314a = (short) 0;
            }
        }
        return s;
    }
}
