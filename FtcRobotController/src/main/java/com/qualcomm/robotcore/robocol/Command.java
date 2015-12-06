package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Comparator;

public class Command implements RobocolParsable, Comparable<Command>, Comparator<Command> {
    public static final int MAX_COMMAND_LENGTH = 256;
    private static final int PAYLOAD_SIZE = 11;

    private static final Charset charset;
    String name;
    String extra;
    byte[] nameBuffer;
    byte[] extraBuffer;
    long timestamp;
    boolean isAcknowledged = false;
    byte attemps = 0;

    static {
        charset = Charset.forName("UTF-8");
    }

    public Command(String name) {
        this(name, "");
    }

    public Command(String name, String extra) {
        this.name = name;
        this.extra = extra;
        this.nameBuffer = TypeConversion.stringToUtf8(name);
        this.extraBuffer = TypeConversion.stringToUtf8(extra);
        timestamp = generateTimestamp();
        if (nameBuffer.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command name length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
        } else if (extraBuffer.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command extra data length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
        }
    }

    public Command(byte[] byteArray) throws RobotCoreException {
        fromByteArray(byteArray);
    }

    public void acknowledge() {
        isAcknowledged = true;
    }

    public boolean isAcknowledged() {
        return isAcknowledged;
    }

    public String getName() {
        return name;
    }

    public String getExtra() {
        return extra;
    }

    public byte getAttempts() {
        return attemps;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.COMMAND;
    }

    public byte[] toByteArray() throws RobotCoreException {
        if (attemps < Byte.MAX_VALUE) {
            attemps++;
        }
        short messageLength = (short) (nameBuffer.length + extraBuffer.length + PAYLOAD_SIZE);
        ByteBuffer allocate = ByteBuffer.allocate(messageLength + HEADER_LENGTH);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(messageLength);
            allocate.putLong(timestamp);
            allocate.put((byte) (isAcknowledged ? 1 : 0));
            allocate.put((byte) nameBuffer.length);
            allocate.put(nameBuffer);
            allocate.put((byte) extraBuffer.length);
            allocate.put(extraBuffer);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, byteArray.length - HEADER_LENGTH);
        timestamp = wrap.getLong();
        isAcknowledged = (wrap.get() == 1);
        nameBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
        wrap.get(nameBuffer);
        name = TypeConversion.utf8ToString(nameBuffer);
        extraBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
        wrap.get(extraBuffer);
        extra = TypeConversion.utf8ToString(extraBuffer);
    }

    public String toString() {
        return String.format("command: %20d %5s %s", timestamp, isAcknowledged, name);
    }

    public boolean equals(Object o) {
        if (o instanceof Command) {
            Command command = (Command) o;
            if (name.equals(command.name) && timestamp == command.timestamp) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (int) (name.hashCode() & timestamp);
    }

    public int compareTo(Command another) {
        if(another == null) {
            return 1;
        }

        int compareTo = name.compareTo(another.name);
        if (compareTo != 0) {
            return compareTo;
        }
        if (timestamp < another.timestamp) {
            return -1;
        }
        if (timestamp > another.timestamp) {
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
