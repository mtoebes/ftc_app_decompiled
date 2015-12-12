package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Comparator;

public class Command implements RobocolParsable, Comparable<Command>, Comparator<Command> {
    public static final int MAX_COMMAND_LENGTH = 256;
    private static final Charset f304h;
    String f305a;
    String f306b;
    byte[] f307c;
    byte[] f308d;
    long f309e;
    boolean f310f;
    byte f311g;

    static {
        f304h = Charset.forName("UTF-8");
    }

    public Command(String name) {
        this(name, "");
    }

    public Command(String name, String extra) {
        this.f310f = false;
        this.f311g = (byte) 0;
        this.f305a = name;
        this.f306b = extra;
        this.f307c = TypeConversion.stringToUtf8(this.f305a);
        this.f308d = TypeConversion.stringToUtf8(this.f306b);
        this.f309e = generateTimestamp();
        if (this.f307c.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command name length is too long (MAX: %d)", new Object[]{Integer.valueOf(MAX_COMMAND_LENGTH)}));
        } else if (this.f308d.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command extra data length is too long (MAX: %d)", new Object[]{Integer.valueOf(MAX_COMMAND_LENGTH)}));
        }
    }

    public Command(byte[] byteArray) throws RobotCoreException {
        this.f310f = false;
        this.f311g = (byte) 0;
        fromByteArray(byteArray);
    }

    public void acknowledge() {
        this.f310f = true;
    }

    public boolean isAcknowledged() {
        return this.f310f;
    }

    public String getName() {
        return this.f305a;
    }

    public String getExtra() {
        return this.f306b;
    }

    public byte getAttempts() {
        return this.f311g;
    }

    public long getTimestamp() {
        return this.f309e;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.COMMAND;
    }

    public byte[] toByteArray() throws RobotCoreException {
        if (this.f311g != 127) {
            this.f311g = (byte) (this.f311g + 1);
        }
        short length = (short) ((this.f307c.length + 11) + this.f308d.length);
        ByteBuffer allocate = ByteBuffer.allocate(length + 3);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(length);
            allocate.putLong(this.f309e);
            allocate.put((byte) (this.f310f ? 1 : 0));
            allocate.put((byte) this.f307c.length);
            allocate.put(this.f307c);
            allocate.put((byte) this.f308d.length);
            allocate.put(this.f308d);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        boolean z = true;
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, 3, byteArray.length - 3);
        this.f309e = wrap.getLong();
        if (wrap.get() != (byte) 1) {
            z = false;
        }
        this.f310f = z;
        this.f307c = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
        wrap.get(this.f307c);
        this.f305a = TypeConversion.utf8ToString(this.f307c);
        this.f308d = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
        wrap.get(this.f308d);
        this.f306b = TypeConversion.utf8ToString(this.f308d);
    }

    public String toString() {
        return String.format("command: %20d %5s %s", new Object[]{Long.valueOf(this.f309e), Boolean.valueOf(this.f310f), this.f305a});
    }

    public boolean equals(Object o) {
        if (o instanceof Command) {
            Command command = (Command) o;
            if (this.f305a.equals(command.f305a) && this.f309e == command.f309e) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (int) (((long) this.f305a.hashCode()) & this.f309e);
    }

    public int compareTo(Command another) {
        int compareTo = this.f305a.compareTo(another.f305a);
        if (compareTo != 0) {
            return compareTo;
        }
        if (this.f309e < another.f309e) {
            return -1;
        }
        if (this.f309e > another.f309e) {
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
