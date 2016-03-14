package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class RobocolParsableBase implements RobocolParsable {
    protected static final long nanotimeTransmitInterval = 200000000;
    protected static AtomicInteger nextSequenceNumber;
    protected long nanotimeTransmit;
    protected int sequenceNumber;

    static {
        nextSequenceNumber = new AtomicInteger();
    }

    public static void initializeSequenceNumber(int sequenceNumber) {
        nextSequenceNumber = new AtomicInteger(sequenceNumber);
    }

    public RobocolParsableBase() {
        setSequenceNumber();
        this.nanotimeTransmit = 0;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(short sequenceNumber) {
        this.sequenceNumber = TypeConversion.unsignedShortToInt(sequenceNumber);
    }

    public void setSequenceNumber() {
        setSequenceNumber((short) nextSequenceNumber.getAndIncrement());
    }

    public byte[] toByteArrayForTransmission() throws RobotCoreException {
        byte[] toByteArray = toByteArray();
        this.nanotimeTransmit = System.nanoTime();
        return toByteArray;
    }

    public boolean shouldTransmit(long nanotimeNow) {
        return this.nanotimeTransmit == 0 || nanotimeNow - this.nanotimeTransmit > nanotimeTransmitInterval;
    }

    protected ByteBuffer allocateWholeWriteBuffer(int overallSize) {
        return ByteBuffer.allocate(overallSize);
    }

    protected ByteBuffer getWholeReadBuffer(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray);
    }

    protected ByteBuffer getWriteBuffer(int payloadSize) {
        ByteBuffer allocateWholeWriteBuffer = allocateWholeWriteBuffer(payloadSize + 5);
        allocateWholeWriteBuffer.put(getRobocolMsgType().asByte());
        allocateWholeWriteBuffer.putShort((short) payloadSize);
        allocateWholeWriteBuffer.putShort((short) this.sequenceNumber);
        return allocateWholeWriteBuffer;
    }

    protected ByteBuffer getReadBuffer(byte[] byteArray) {
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, byteArray.length - 3);
        setSequenceNumber(wrap.getShort());
        return wrap;
    }
}
