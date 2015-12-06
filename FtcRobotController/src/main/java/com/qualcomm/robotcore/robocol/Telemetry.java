package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Telemetry implements RobocolParsable {
    public static final String DEFAULT_TAG = "TELEMETRY_DATA";
    private static final int BASE_PAYLOAD_SIZE = 8;
    private static final Charset charset = Charset.forName("UTF-8");
    private final Map<String, String> dataStrings = new HashMap<String, String>();
    private final Map<String, Float> dataNumbers = new HashMap<String, Float>();
    private String tag = "";
    private long timestamp = 0;

    public Telemetry() {
    }

    public Telemetry(byte[] byteArray) throws RobotCoreException {
        fromByteArray(byteArray);
    }

    public synchronized long getTimestamp() {
        return timestamp;
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
        return !(this.dataStrings.isEmpty() && this.dataNumbers.isEmpty());
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
            throw new RobotCoreException("Cannot have more than " + Command.MAX_COMMAND_LENGTH + " string data points");
        } else if (this.dataNumbers.size() > Command.MAX_COMMAND_LENGTH) {
            throw new RobotCoreException("Cannot have more than " + Command.MAX_COMMAND_LENGTH + " number data points");
        } else {
            int payloadSize = getMessageLength() + BASE_PAYLOAD_SIZE;
            int totalSize = payloadSize + HEADER_LENGTH;
            if (totalSize > RobocolConfig.MAX_PACKET_SIZE) {
                throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d", totalSize, RobocolConfig.MAX_PACKET_SIZE));
            }
            byte[] keyBuffer;
            allocate = ByteBuffer.allocate(totalSize);
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort((short) payloadSize);
            allocate.putLong(this.timestamp);
            if (this.tag.length() == 0) {
                allocate.put((byte) 0);
            } else {
                byte[] charsetBuffer = this.tag.getBytes(charset);
                if (charsetBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry tag cannot exceed %d bytes [%s]", Command.MAX_COMMAND_LENGTH, tag));
                }
                allocate.put((byte) charsetBuffer.length);
                allocate.put(charsetBuffer);
            }
            allocate.put((byte) this.dataStrings.size());
            for (Entry<String, String> entry : this.dataStrings.entrySet()) {
                keyBuffer = entry.getKey().getBytes(charset);
                byte[] valueBuffer = entry.getValue().getBytes(charset);
                if (keyBuffer.length > Command.MAX_COMMAND_LENGTH || valueBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed %d bytes [%s:%s]", Command.MAX_COMMAND_LENGTH, entry.getKey(), entry.getValue()));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.put((byte) valueBuffer.length);
                allocate.put(valueBuffer);
            }
            allocate.put((byte) this.dataNumbers.size());
            for (Entry<String, Float> entry : this.dataNumbers.entrySet()) {
                keyBuffer = entry.getKey().getBytes(charset);
                float floatValue = entry.getValue();
                if (keyBuffer.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed %d bytes [%s:%f]", Command.MAX_COMMAND_LENGTH, entry.getKey(), floatValue));
                }
                allocate.put((byte) keyBuffer.length);
                allocate.put(keyBuffer);
                allocate.putFloat(floatValue);
            }
        }
        return allocate.array();
    }

    public synchronized void fromByteArray(byte[] byteArray) throws RobotCoreException {
        byte b = (byte) 0;
        synchronized (this) {
            byte b2;
            clearData();
            ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, byteArray.length - 3);
            this.timestamp = wrap.getLong();
            int unsignedByteToInt = TypeConversion.unsignedByteToInt(wrap.get());
            if (unsignedByteToInt == 0) {
                this.tag = "";
            } else {
                byte[] bArr = new byte[unsignedByteToInt];
                wrap.get(bArr);
                this.tag = new String(bArr, charset);
            }
            byte b3 = wrap.get();
            for (b2 = (byte) 0; b2 < b3; b2++) {
                byte[] bArr2 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr2);
                byte[] bArr3 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr3);
                this.dataStrings.put(new String(bArr2, charset), new String(bArr3, charset));
            }
            b2 = wrap.get();
            while (b < b2) {
                byte[] bArr4 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr4);
                this.dataNumbers.put(new String(bArr4, charset), wrap.getFloat());
                b++;
            }
        }
    }

    private int getMessageLength() {
        int length = this.tag.getBytes(charset).length + 2;

        for (Entry<String, String> entry : this.dataStrings.entrySet()) {
            length += entry.getValue().getBytes(charset).length + 1;
            length += entry.getKey().getBytes(charset).length + 1;
        }

        for (Entry<String, Float> entry : this.dataNumbers.entrySet()) {
            length += entry.getKey().getBytes(charset).length + 1;
            length += 4;
        }
        return length;
    }
}
