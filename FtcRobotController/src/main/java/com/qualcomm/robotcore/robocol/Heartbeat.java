package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class Heartbeat implements RobocolParsable {
    private static final double TIME_IN_NANO = 1.0E9d;

    public static final short MAX_SEQUENCE_NUMBER = (short) 10000;
    public static final short PAYLOAD_SIZE = (short) 11;
    public static final short BUFFER_SIZE = (short) PAYLOAD_SIZE + HEADER_LENGTH;
    private static short numberGen;
    private long timestamp;
    private short sequenceNumber;
    private RobotState state = RobotState.NOT_STARTED;

    public enum Token {
        EMPTY
    }

    public Heartbeat() {
        this.sequenceNumber = getNextSequenceNumber();
        this.timestamp = System.nanoTime();
        this.state = RobotState.NOT_STARTED;
    }

    public Heartbeat(Token token) {
        switch (token) {
            case EMPTY :
                this.sequenceNumber = (short) 0;
            default:
        }
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getElapsedTime() {
        return ((double) (System.nanoTime() - this.timestamp)) / TIME_IN_NANO;
    }

    public short getSequenceNumber() {
        return this.sequenceNumber;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.HEARTBEAT;
    }

    public byte getRobotState() {
        return this.state.asByte();
    }

    public void setRobotState(RobotState state) {
        this.state = state;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.putShort(this.sequenceNumber);
            allocate.putLong(this.timestamp);
            allocate.put(this.state.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least 14 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);
        this.sequenceNumber = wrap.getShort();
        this.timestamp = wrap.getLong();
        this.state = RobotState.fromByte(wrap.get());
    }

    public String toString() {
        return String.format("Heartbeat - seq: %4d, time: %d", new Object[]{Short.valueOf(this.sequenceNumber), Long.valueOf(this.timestamp)});
    }

    private static synchronized short getNextSequenceNumber() {
        short seqNum;
        synchronized (Heartbeat.class) {
            seqNum = numberGen;
            numberGen = (short) (numberGen + 1);
            if (numberGen > MAX_SEQUENCE_NUMBER) {
                numberGen = (short) 0;
            }
        }
        return seqNum;
    }
}
