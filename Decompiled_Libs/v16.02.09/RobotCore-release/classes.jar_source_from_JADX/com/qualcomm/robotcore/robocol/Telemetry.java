package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Telemetry extends RobocolParsableBase {
    public static final String DEFAULT_TAG = "TELEMETRY_DATA";
    private static final Charset f333a;
    public static final int cCountMax = 255;
    public static final int cbKeyMax = 65535;
    public static final int cbTagMax = 255;
    public static final int cbValueMax = 65535;
    private final Map<String, String> f334b;
    private final Map<String, Float> f335c;
    private String f336d;
    private long f337e;
    private boolean f338f;

    static {
        f333a = Charset.forName("UTF-8");
    }

    public Telemetry() {
        this.f334b = new LinkedHashMap();
        this.f335c = new LinkedHashMap();
        this.f336d = BuildConfig.VERSION_NAME;
        this.f337e = 0;
        this.f338f = true;
    }

    public Telemetry(byte[] byteArray) throws RobotCoreException {
        this.f334b = new LinkedHashMap();
        this.f335c = new LinkedHashMap();
        this.f336d = BuildConfig.VERSION_NAME;
        this.f337e = 0;
        this.f338f = true;
        fromByteArray(byteArray);
    }

    public synchronized long getTimestamp() {
        return this.f337e;
    }

    public boolean isSorted() {
        return this.f338f;
    }

    public void setSorted(boolean isSorted) {
        this.f338f = isSorted;
    }

    public synchronized void setTag(String tag) {
        this.f336d = tag;
    }

    public synchronized String getTag() {
        String str;
        if (this.f336d.length() == 0) {
            str = DEFAULT_TAG;
        } else {
            str = this.f336d;
        }
        return str;
    }

    public synchronized void addData(String key, String msg) {
        this.f334b.put(key, msg);
    }

    public synchronized void addData(String key, Object msg) {
        this.f334b.put(key, msg.toString());
    }

    public synchronized void addData(String key, float msg) {
        this.f335c.put(key, Float.valueOf(msg));
    }

    public synchronized void addData(String key, double msg) {
        this.f335c.put(key, Float.valueOf((float) msg));
    }

    public synchronized Map<String, String> getDataStrings() {
        return this.f334b;
    }

    public synchronized Map<String, Float> getDataNumbers() {
        return this.f335c;
    }

    public synchronized boolean hasData() {
        boolean z;
        z = (this.f334b.isEmpty() && this.f335c.isEmpty()) ? false : true;
        return z;
    }

    public synchronized void clearData() {
        this.f337e = 0;
        this.f334b.clear();
        this.f335c.clear();
    }

    public MsgType getRobocolMsgType() {
        return MsgType.TELEMETRY;
    }

    public synchronized byte[] toByteArray() throws RobotCoreException {
        byte[] bytes;
        int i = 1;
        synchronized (this) {
            this.f337e = System.currentTimeMillis();
            if (this.f334b.size() > cbTagMax) {
                throw new RobotCoreException("Cannot have more than %d string data points", Integer.valueOf(cbTagMax));
            } else if (this.f335c.size() > cbTagMax) {
                throw new RobotCoreException("Cannot have more than %d number data points", Integer.valueOf(cbTagMax));
            } else {
                int a = m221a();
                if (a + 5 > RobocolConfig.MAX_PACKET_SIZE) {
                    throw new RobotCoreException(String.format("Cannot send telemetry data of %d bytes; max is %d", new Object[]{Integer.valueOf(a + 5), Integer.valueOf(RobocolConfig.MAX_PACKET_SIZE)}));
                }
                byte[] bytes2;
                ByteBuffer writeBuffer = getWriteBuffer(a);
                writeBuffer.putLong(this.f337e);
                if (!this.f338f) {
                    i = 0;
                }
                writeBuffer.put((byte) i);
                if (this.f336d.length() == 0) {
                    m225b(writeBuffer, 0);
                } else {
                    bytes = this.f336d.getBytes(f333a);
                    if (bytes.length > cbTagMax) {
                        throw new RobotCoreException(String.format("Telemetry tag cannot exceed %d bytes [%s]", new Object[]{Integer.valueOf(cbTagMax), this.f336d}));
                    }
                    m225b(writeBuffer, bytes.length);
                    writeBuffer.put(bytes);
                }
                m223a(writeBuffer, this.f334b.size());
                for (Entry entry : this.f334b.entrySet()) {
                    bytes2 = ((String) entry.getKey()).getBytes(f333a);
                    byte[] bytes3 = ((String) entry.getValue()).getBytes(f333a);
                    if (bytes2.length > cbValueMax) {
                        throw new RobotCoreException("telemetry key '%s' too long: %d bytes; max %d bytes", entry.getKey(), Integer.valueOf(bytes2.length), Integer.valueOf(cbValueMax));
                    } else if (bytes3.length > cbValueMax) {
                        throw new RobotCoreException("telemetry value '%s' too long: %d bytes; max %d bytes", entry.getValue(), Integer.valueOf(bytes3.length), Integer.valueOf(cbValueMax));
                    } else {
                        m227c(writeBuffer, bytes2.length);
                        writeBuffer.put(bytes2);
                        m229d(writeBuffer, bytes3.length);
                        writeBuffer.put(bytes3);
                    }
                }
                m223a(writeBuffer, this.f335c.size());
                for (Entry entry2 : this.f335c.entrySet()) {
                    bytes2 = ((String) entry2.getKey()).getBytes(f333a);
                    float floatValue = ((Float) entry2.getValue()).floatValue();
                    if (bytes2.length > cbValueMax) {
                        throw new RobotCoreException("telemetry key '%s' too long: %d bytes; max %d bytes", entry2.getKey(), Integer.valueOf(bytes2.length), Integer.valueOf(cbValueMax));
                    }
                    m227c(writeBuffer, bytes2.length);
                    writeBuffer.put(bytes2);
                    writeBuffer.putFloat(floatValue);
                }
                bytes = writeBuffer.array();
            }
        }
        return bytes;
    }

    public synchronized void fromByteArray(byte[] byteArray) throws RobotCoreException {
        int i = 0;
        synchronized (this) {
            clearData();
            ByteBuffer readBuffer = getReadBuffer(byteArray);
            this.f337e = readBuffer.getLong();
            this.f338f = readBuffer.get() != null;
            int b = m224b(readBuffer);
            if (b == 0) {
                this.f336d = BuildConfig.VERSION_NAME;
            } else {
                byte[] bArr = new byte[b];
                readBuffer.get(bArr);
                this.f336d = new String(bArr, f333a);
            }
            int a = m222a(readBuffer);
            for (b = 0; b < a; b++) {
                byte[] bArr2 = new byte[m226c(readBuffer)];
                readBuffer.get(bArr2);
                byte[] bArr3 = new byte[m228d(readBuffer)];
                readBuffer.get(bArr3);
                this.f334b.put(new String(bArr2, f333a), new String(bArr3, f333a));
            }
            b = m222a(readBuffer);
            while (i < b) {
                byte[] bArr4 = new byte[m226c(readBuffer)];
                readBuffer.get(bArr4);
                this.f335c.put(new String(bArr4, f333a), Float.valueOf(readBuffer.getFloat()));
                i++;
            }
        }
    }

    static void m223a(ByteBuffer byteBuffer, int i) {
        byteBuffer.put((byte) i);
    }

    static int m222a(ByteBuffer byteBuffer) {
        return TypeConversion.unsignedByteToInt(byteBuffer.get());
    }

    static void m225b(ByteBuffer byteBuffer, int i) {
        byteBuffer.put((byte) i);
    }

    static int m224b(ByteBuffer byteBuffer) {
        return TypeConversion.unsignedByteToInt(byteBuffer.get());
    }

    static void m227c(ByteBuffer byteBuffer, int i) {
        byteBuffer.putShort((short) i);
    }

    static int m226c(ByteBuffer byteBuffer) {
        return TypeConversion.unsignedShortToInt(byteBuffer.getShort());
    }

    static void m229d(ByteBuffer byteBuffer, int i) {
        m227c(byteBuffer, i);
    }

    static int m228d(ByteBuffer byteBuffer) {
        return m226c(byteBuffer);
    }

    private int m221a() {
        int length = (9 + (this.f336d.getBytes(f333a).length + 1)) + 1;
        int i = length;
        for (Entry entry : this.f334b.entrySet()) {
            i = (((String) entry.getValue()).getBytes(f333a).length + 2) + ((((String) entry.getKey()).getBytes(f333a).length + 2) + i);
        }
        length = i + 1;
        int i2 = length;
        for (Entry entry2 : this.f335c.entrySet()) {
            i2 = ((((String) entry2.getKey()).getBytes(f333a).length + 2) + i2) + 4;
        }
        return i2;
    }
}
