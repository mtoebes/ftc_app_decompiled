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

    private final Map<String, String> dataStrings = new HashMap();
    private final Map<String, Float> dataNumbers = new HashMap();
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
        this.dataNumbers.put(key, Float.valueOf(msg));
    }

    public synchronized void addData(String key, double msg) {
        this.dataNumbers.put(key, Float.valueOf((float) msg));
    }

    public synchronized Map<String, String> getDataStrings() {
        return this.dataStrings;
    }

    public synchronized Map<String, Float> getDataNumbers() {
        return this.dataNumbers;
    }

    public synchronized boolean hasData() {
        boolean hasData;
        hasData = (this.dataStrings.isEmpty() && this.dataNumbers.isEmpty()) ? false : true;
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
                throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d", new Object[]{Integer.valueOf(bufferSize), Integer.valueOf(RobocolConfig.MAX_PACKET_SIZE)}));
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
                    throw new RobotCoreException(String.format("Telemetry tag cannot exceed 256 bytes [%s]", new Object[]{this.tag}));
                }
                allocate.put((byte) tagBuffer.length);
                allocate.put(tagBuffer);
            }
            allocate.put((byte) this.dataStrings.size());
            for (Entry entry : this.dataStrings.entrySet()) {
                byte[] keyBuffer = ((String) entry.getKey()).getBytes(CHARSET);
                byte[] StringValueBuffer = ((String) entry.getValue()).getBytes(CHARSET);
                if (keyBuffer.length > Command.MAX_COMMAND_LENGTH || StringValueBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%s]", new Object[]{entry.getKey(), entry.getValue()}));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.put((byte) StringValueBuffer.length);
                allocate.put(StringValueBuffer);
            }
            allocate.put((byte) this.dataNumbers.size());
            for (Entry entry2 : this.dataNumbers.entrySet()) {
                byte[] keyBuffer = ((String) entry2.getKey()).getBytes(CHARSET);
                float floatValue = ((Float) entry2.getValue()).floatValue();
                if (keyBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%f]", new Object[]{entry2.getKey(), Float.valueOf(floatValue)}));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.putFloat(floatValue);
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
                this.dataNumbers.put(new String(keyBuffer, CHARSET), Float.valueOf(wrap.getFloat()));
            }
        }
    }

    private int getMessageLength() {
        int length = (0 + (this.tag.getBytes(CHARSET).length + 1)) + 1;
        for (Entry entry : this.dataStrings.entrySet()) {
            length = (((String) entry.getValue()).getBytes(CHARSET).length + 1) + ((((String) entry.getKey()).getBytes(CHARSET).length + 1) + length);
        }
        length = length + 1;
        for (Entry entry2 : this.dataNumbers.entrySet()) {
            length = ((((String) entry2.getKey()).getBytes(CHARSET).length + 1) + length) + 4;
        }
        return length;
    }
}
