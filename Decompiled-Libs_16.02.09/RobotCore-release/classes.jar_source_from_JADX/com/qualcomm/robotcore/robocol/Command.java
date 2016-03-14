package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Comparator;

public class Command extends RobocolParsableBase implements Comparable<Command>, Comparator<Command> {
    private static final Charset f299h;
    String f300a;
    String f301b;
    byte[] f302c;
    byte[] f303d;
    long f304e;
    boolean f305f;
    byte f306g;

    static {
        f299h = Charset.forName("UTF-8");
    }

    public Command(String name) {
        this(name, BuildConfig.VERSION_NAME);
    }

    public Command(String name, String extra) {
        this.f305f = false;
        this.f306g = (byte) 0;
        this.f300a = name;
        this.f301b = extra;
        this.f302c = TypeConversion.stringToUtf8(this.f300a);
        this.f303d = TypeConversion.stringToUtf8(this.f301b);
        this.f304e = generateTimestamp();
        if (m217a() > 32767) {
            throw new IllegalArgumentException(String.format("command payload is too large: %d", new Object[]{Integer.valueOf(m217a())}));
        }
    }

    public Command(byte[] byteArray) throws RobotCoreException {
        this.f305f = false;
        this.f306g = (byte) 0;
        fromByteArray(byteArray);
    }

    public void acknowledge() {
        this.f305f = true;
    }

    public boolean isAcknowledged() {
        return this.f305f;
    }

    public String getName() {
        return this.f300a;
    }

    public String getExtra() {
        return this.f301b;
    }

    public byte getAttempts() {
        return this.f306g;
    }

    public long getTimestamp() {
        return this.f304e;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.COMMAND;
    }

    public byte[] toByteArray() throws RobotCoreException {
        if (this.f306g != 127) {
            this.f306g = (byte) (this.f306g + 1);
        }
        ByteBuffer writeBuffer = getWriteBuffer((short) m217a());
        try {
            writeBuffer.putLong(this.f304e);
            writeBuffer.put((byte) (this.f305f ? 1 : 0));
            writeBuffer.putShort((short) this.f302c.length);
            writeBuffer.put(this.f302c);
            writeBuffer.putShort((short) this.f303d.length);
            writeBuffer.put(this.f303d);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return writeBuffer.array();
    }

    int m217a() {
        return (this.f302c.length + 13) + this.f303d.length;
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        ByteBuffer readBuffer = getReadBuffer(byteArray);
        this.f304e = readBuffer.getLong();
        this.f305f = readBuffer.get() != null;
        this.f302c = new byte[TypeConversion.unsignedShortToInt(readBuffer.getShort())];
        readBuffer.get(this.f302c);
        this.f300a = TypeConversion.utf8ToString(this.f302c);
        this.f303d = new byte[TypeConversion.unsignedShortToInt(readBuffer.getShort())];
        readBuffer.get(this.f303d);
        this.f301b = TypeConversion.utf8ToString(this.f303d);
    }

    public String toString() {
        return String.format("command: %20d %5s %s", new Object[]{Long.valueOf(this.f304e), Boolean.valueOf(this.f305f), this.f300a});
    }

    public boolean equals(Object o) {
        if (o instanceof Command) {
            Command command = (Command) o;
            if (this.f300a.equals(command.f300a) && this.f304e == command.f304e) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.f300a.hashCode() ^ ((int) this.f304e);
    }

    public int compareTo(Command another) {
        int compareTo = this.f300a.compareTo(another.f300a);
        if (compareTo != 0) {
            return compareTo;
        }
        if (this.f304e < another.f304e) {
            return -1;
        }
        if (this.f304e > another.f304e) {
            return 1;
        }
        return 0;
    }

    public int compare(Command c1, Command c2) {
        return c1.compareTo(c2);
    }

    public static long generateTimestamp() {
        return System.nanoTime();
    }
}
