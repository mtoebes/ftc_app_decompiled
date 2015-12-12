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
    private static final Charset f338a;
    private final Map<String, String> f339b;
    private final Map<String, Float> f340c;
    private String f341d;
    private long f342e;

    static {
        f338a = Charset.forName("UTF-8");
    }

    public Telemetry() {
        this.f339b = new HashMap();
        this.f340c = new HashMap();
        this.f341d = "";
        this.f342e = 0;
    }

    public Telemetry(byte[] byteArray) throws RobotCoreException {
        this.f339b = new HashMap();
        this.f340c = new HashMap();
        this.f341d = "";
        this.f342e = 0;
        fromByteArray(byteArray);
    }

    public synchronized long getTimestamp() {
        return this.f342e;
    }

    public synchronized void setTag(String tag) {
        this.f341d = tag;
    }

    public synchronized String getTag() {
        String str;
        if (this.f341d.length() == 0) {
            str = DEFAULT_TAG;
        } else {
            str = this.f341d;
        }
        return str;
    }

    public synchronized void addData(String key, String msg) {
        this.f339b.put(key, msg);
    }

    public synchronized void addData(String key, Object msg) {
        this.f339b.put(key, msg.toString());
    }

    public synchronized void addData(String key, float msg) {
        this.f340c.put(key, Float.valueOf(msg));
    }

    public synchronized void addData(String key, double msg) {
        this.f340c.put(key, Float.valueOf((float) msg));
    }

    public synchronized Map<String, String> getDataStrings() {
        return this.f339b;
    }

    public synchronized Map<String, Float> getDataNumbers() {
        return this.f340c;
    }

    public synchronized boolean hasData() {
        boolean z;
        z = (this.f339b.isEmpty() && this.f340c.isEmpty()) ? false : true;
        return z;
    }

    public synchronized void clearData() {
        this.f342e = 0;
        this.f339b.clear();
        this.f340c.clear();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.TELEMETRY;
    }

    public synchronized byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate;
        this.f342e = System.currentTimeMillis();
        if (this.f339b.size() > Command.MAX_COMMAND_LENGTH) {
            throw new RobotCoreException("Cannot have more than 256 string data points");
        } else if (this.f340c.size() > Command.MAX_COMMAND_LENGTH) {
            throw new RobotCoreException("Cannot have more than 256 number data points");
        } else {
            int a = m211a() + 8;
            int i = a + 3;
            if (i > RobocolConfig.MAX_PACKET_SIZE) {
                throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d", new Object[]{Integer.valueOf(i), Integer.valueOf(RobocolConfig.MAX_PACKET_SIZE)}));
            }
            byte[] bytes;
            allocate = ByteBuffer.allocate(i);
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort((short) a);
            allocate.putLong(this.f342e);
            if (this.f341d.length() == 0) {
                allocate.put((byte) 0);
            } else {
                byte[] bytes2 = this.f341d.getBytes(f338a);
                if (bytes2.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry tag cannot exceed 256 bytes [%s]", new Object[]{this.f341d}));
                }
                allocate.put((byte) bytes2.length);
                allocate.put(bytes2);
            }
            allocate.put((byte) this.f339b.size());
            for (Entry entry : this.f339b.entrySet()) {
                bytes = ((String) entry.getKey()).getBytes(f338a);
                byte[] bytes3 = ((String) entry.getValue()).getBytes(f338a);
                if (bytes.length > Command.MAX_COMMAND_LENGTH || bytes3.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%s]", new Object[]{entry.getKey(), entry.getValue()}));
                }
                allocate.put((byte) bytes.length);
                allocate.put(bytes);
                allocate.put((byte) bytes3.length);
                allocate.put(bytes3);
            }
            allocate.put((byte) this.f340c.size());
            for (Entry entry2 : this.f340c.entrySet()) {
                bytes = ((String) entry2.getKey()).getBytes(f338a);
                float floatValue = ((Float) entry2.getValue()).floatValue();
                if (bytes.length > Command.MAX_COMMAND_LENGTH) {
                    throw new RobotCoreException(String.format("Telemetry elements cannot exceed 256 bytes [%s:%f]", new Object[]{entry2.getKey(), Float.valueOf(floatValue)}));
                }
                allocate.put((byte) bytes.length);
                allocate.put(bytes);
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
            this.f342e = wrap.getLong();
            int unsignedByteToInt = TypeConversion.unsignedByteToInt(wrap.get());
            if (unsignedByteToInt == 0) {
                this.f341d = "";
            } else {
                byte[] bArr = new byte[unsignedByteToInt];
                wrap.get(bArr);
                this.f341d = new String(bArr, f338a);
            }
            byte b3 = wrap.get();
            for (b2 = (byte) 0; b2 < b3; b2++) {
                byte[] bArr2 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr2);
                byte[] bArr3 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr3);
                this.f339b.put(new String(bArr2, f338a), new String(bArr3, f338a));
            }
            b2 = wrap.get();
            while (b < b2) {
                byte[] bArr4 = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
                wrap.get(bArr4);
                this.f340c.put(new String(bArr4, f338a), Float.valueOf(wrap.getFloat()));
                b++;
            }
        }
    }

    private int m211a() {
        int length = (0 + (this.f341d.getBytes(f338a).length + 1)) + 1;
        int i = length;
        for (Entry entry : this.f339b.entrySet()) {
            i = (((String) entry.getValue()).getBytes(f338a).length + 1) + ((((String) entry.getKey()).getBytes(f338a).length + 1) + i);
        }
        length = i + 1;
        int i2 = length;
        for (Entry entry2 : this.f340c.entrySet()) {
            i2 = ((((String) entry2.getKey()).getBytes(f338a).length + 1) + i2) + 4;
        }
        return i2;
    }
}
