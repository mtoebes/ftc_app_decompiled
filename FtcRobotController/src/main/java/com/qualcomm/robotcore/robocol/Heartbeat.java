/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.ByteBuffer;

public class Heartbeat implements RobocolParsable {
    private static final double TIME_IN_NANO = 1.0E9d;

    public static final short MAX_SEQUENCE_NUMBER = (short) 10000;
    public static final short PAYLOAD_SIZE = (short) 11;
    public static final short BUFFER_SIZE = PAYLOAD_SIZE + HEADER_LENGTH;
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
            case EMPTY:
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
        return String.format("Heartbeat - seq: %4d, time: %d", this.sequenceNumber, this.timestamp);
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
