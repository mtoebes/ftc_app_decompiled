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
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Telemetry implements RobocolParsable {
    public static final String DEFAULT_TAG = "TELEMETRY_DATA";
    public static final short BASE_PAYLOAD_SIZE = (short) 8;

    private final Map<String, String> dataStrings = new HashMap<String, String>();
    private final Map<String, Float> dataNumbers = new HashMap<String, Float>();
    private String tag = "";
    private long timestamp;

    public Telemetry() {
    }

    public Telemetry(byte[] byteArray) throws RobotCoreException {
        fromByteArray(byteArray);
    }

    public synchronized long getTimestamp() {
        return this.timestamp;
    }

    public synchronized void setTag(String tag) {
        this.tag = tag;
    }

    public synchronized String getTag() {
        String str;
        if (this.tag.length() == 0) {
            str = DEFAULT_TAG;
        } else {
            str = this.tag;
        }
        return str;
    }

    public synchronized void addData(String key, String msg) {
        this.dataStrings.put(key, msg);
    }

    public synchronized void addData(String key, Object msg) {
        this.dataStrings.put(key, msg.toString());
    }

    public synchronized void addData(String key, float msg) {
        this.dataNumbers.put(key, msg);
    }

    public synchronized void addData(String key, double msg) {
        this.dataNumbers.put(key, (float) msg);
    }

    public synchronized Map<String, String> getDataStrings() {
        return this.dataStrings;
    }

    public synchronized Map<String, Float> getDataNumbers() {
        return this.dataNumbers;
    }

    public synchronized boolean hasData() {
        boolean hasData;
        hasData = (!(this.dataStrings.isEmpty() && this.dataNumbers.isEmpty()));
        return hasData;
    }

    public synchronized void clearData() {
        this.timestamp = 0;
        this.dataStrings.clear();
        this.dataNumbers.clear();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.TELEMETRY;
    }

    public synchronized byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate;
        this.timestamp = System.currentTimeMillis();
        if (this.dataStrings.size() > Command.MAX_COMMAND_LENGTH) {
            throw new RobotCoreException("Cannot have more than 256 string data points");
        } else if (this.dataNumbers.size() > Command.MAX_COMMAND_LENGTH) {
            throw new RobotCoreException("Cannot have more than 256 number data points");
        } else {
            int payloadLength = getMessageLength() + BASE_PAYLOAD_SIZE;
            int bufferSize = payloadLength + HEADER_LENGTH;
            if (bufferSize > RobocolConfig.MAX_PACKET_SIZE) {
                throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d", bufferSize, RobocolConfig.MAX_PACKET_SIZE));
            }
            allocate = ByteBuffer.allocate(bufferSize);
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort((short) payloadLength);
            allocate.putLong(this.timestamp);
            if (this.tag.length() == 0) {
                allocate.put((byte) 0);
            } else {
                byte[] tagBuffer = this.tag.getBytes(CHARSET);
                if (tagBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry tag cannot exceed 256 bytes [%s]", this.tag));
                }
                allocate.put((byte) tagBuffer.length);
                allocate.put(tagBuffer);
            }
            allocate.put((byte) this.dataStrings.size());
            for (Entry<String, String> entry : this.dataStrings.entrySet()) {
                byte[] keyBuffer = (entry.getKey()).getBytes(CHARSET);
                byte[] valueBuffer = (entry.getValue()).getBytes(CHARSET);
                if ((keyBuffer.length > Command.MAX_COMMAND_LENGTH) || (valueBuffer.length > Command.MAX_COMMAND_LENGTH)) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%s]", entry.getKey(), entry.getValue()));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.put((byte) valueBuffer.length);
                allocate.put(valueBuffer);
            }
            allocate.put((byte) this.dataNumbers.size());
            for (Entry<String, Float> entry : this.dataNumbers.entrySet()) {
                byte[] keyBuffer = entry.getKey().getBytes(CHARSET);
                float valueBuffer = entry.getValue();
                if (keyBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%f]", entry.getKey(), valueBuffer));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.putFloat(valueBuffer);
            }
        }
        return allocate.array();
    }

    public synchronized void fromByteArray(byte[] byteArray) throws RobotCoreException {
        synchronized (this) {
            clearData();
            ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, byteArray.length - HEADER_LENGTH);
            this.timestamp = wrap.getLong();
            int tagLength = TypeConversion.unsignedByteToInt(wrap.get());
            if (tagLength == 0) {
                this.tag = "";
            } else {
                byte[] tagBuffer = new byte[tagLength];
                wrap.get(tagBuffer);
                this.tag = new String(tagBuffer, CHARSET);
            }
            int stringDataPoints = wrap.get();
            for (int i = 0; i < stringDataPoints; i++) {
                byte[] keyBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(keyBuffer);
                byte[] valueBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(valueBuffer);
                this.dataStrings.put(new String(keyBuffer, CHARSET), new String(valueBuffer, CHARSET));
            }

            int integerDataPoints = wrap.get();
            for (int i = 0; i < integerDataPoints; i++) {
                byte[] keyBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(keyBuffer);
                this.dataNumbers.put(new String(keyBuffer, CHARSET), wrap.getFloat());
            }
        }
    }

    private int getMessageLength() {
        int length = this.tag.getBytes(CHARSET).length + 2;
        for (Entry<String, String> entry : this.dataStrings.entrySet()) {
            length += entry.getValue().getBytes(CHARSET).length + entry.getKey().getBytes(CHARSET).length + 2;
        }
        length += 1;
        for (Entry<String, Float> entry : this.dataNumbers.entrySet()) {
            length += entry.getKey().getBytes(CHARSET).length + 5;
        }
        return length;
    }
}
