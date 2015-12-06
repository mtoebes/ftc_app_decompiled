package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;

public class Heartbeat implements RobocolParsable {
    public static final short BUFFER_SIZE = 14;

    public static final short MAX_SEQUENCE_NUMBER = 10000;
    public static final short PAYLOAD_SIZE = 11;
    private static short sequenceNumberGen = 0;

    private long timestamp;
    private short sequenceNumber;
    private RobotState state = RobotState.NOT_STARTED;


    public enum Token {
        EMPTY
    }

    static {
        sequenceNumberGen = 0;
    }

    public Heartbeat() {
        sequenceNumber = getNextSequenceNumber();
        timestamp = System.nanoTime();
    }

    public Heartbeat(Token token) {
        switch (token) {
            case EMPTY :
                sequenceNumber = 0;
                timestamp = 0;
                break;
            default:
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getElapsedTime() {
        return ((double) (System.nanoTime() - timestamp)) / 1.0E9d;
    }

    public short getSequenceNumber() {
        return sequenceNumber;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.HEARTBEAT;
    }

    public byte getRobotState() {
        return state.asByte();
    }

    public void setRobotState(RobotState state) {
        this.state = state;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.putShort(sequenceNumber);
            allocate.putLong(timestamp);
            allocate.put(state.asByte());
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
        sequenceNumber = wrap.getShort();
        timestamp = wrap.getLong();
        state = RobotState.fromByte(wrap.get());
    }

    public String toString() {
        return String.format("Heartbeat - seq: %4d, time: %d", sequenceNumber, timestamp);
    }

    private static synchronized short getNextSequenceNumber() {
        short next = sequenceNumberGen;
            sequenceNumberGen++;
            if (sequenceNumberGen > MAX_SEQUENCE_NUMBER) {
                sequenceNumberGen = 0;
            }
        return next;
    }
}
