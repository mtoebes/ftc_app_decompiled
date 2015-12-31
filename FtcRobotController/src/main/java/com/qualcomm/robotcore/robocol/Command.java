package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteBuffer;
import java.util.Comparator;

public class Command implements RobocolParsable, Comparable<Command>, Comparator<Command> {
    public static final int MAX_COMMAND_LENGTH = 0x7F;
    public static final short BASE_PAYLOAD_SIZE = (short) 11;

    String name;
    String extra;
    byte[] nameBuffer;
    byte[] extraBuffer;
    long timestamp;
    boolean isAcknowledged;
    byte attempts;

    public Command(String name) {
        this(name, "");
    }

    public Command(String name, String extra) {
        this.name = name;
        this.extra = extra;
        this.nameBuffer = TypeConversion.stringToUtf8(this.name);
        this.extraBuffer = TypeConversion.stringToUtf8(this.extra);
        this.timestamp = generateTimestamp();
        if (this.nameBuffer.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command name length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
        } else if (this.extraBuffer.length > MAX_COMMAND_LENGTH) {
            throw new IllegalArgumentException(String.format("command extra data length is too long (MAX: %d)", MAX_COMMAND_LENGTH));
        }
    }

    public Command(byte[] byteArray) throws RobotCoreException {
        this.isAcknowledged = false;
        this.attempts = (byte) 0;
        fromByteArray(byteArray);
    }

    public void acknowledge() {
        this.isAcknowledged = true;
    }

    public boolean isAcknowledged() {
        return this.isAcknowledged;
    }

    public String getName() {
        return this.name;
    }

    public String getExtra() {
        return this.extra;
    }

    public byte getAttempts() {
        return this.attempts;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.COMMAND;
    }

    public byte[] toByteArray() throws RobotCoreException {
        if (this.attempts < MAX_COMMAND_LENGTH) {
            this.attempts++;
        }
        short length = (short) (this.nameBuffer.length + this.extraBuffer.length + BASE_PAYLOAD_SIZE);
        ByteBuffer allocate = ByteBuffer.allocate(length + HEADER_LENGTH);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(length);
            allocate.putLong(this.timestamp);
            allocate.put((byte) (this.isAcknowledged ? 1 : 0));
            allocate.put((byte) this.nameBuffer.length);
            allocate.put(this.nameBuffer);
            allocate.put((byte) this.extraBuffer.length);
            allocate.put(this.extraBuffer);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, byteArray.length - HEADER_LENGTH);

        this.timestamp = wrap.getLong();

        this.isAcknowledged = (wrap.get() == 1);

        this.nameBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];
        wrap.get(this.nameBuffer);

        this.name = TypeConversion.utf8ToString(this.nameBuffer);
        this.extraBuffer = new byte[TypeConversion.unsignedByteToInt(wrap.get())];

        wrap.get(this.extraBuffer);
        this.extra = TypeConversion.utf8ToString(this.extraBuffer);
    }

    public String toString() {
        return String.format("command: %20d %5s %s", this.timestamp, this.isAcknowledged, this.name);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Command) {
            Command command = (Command) obj;
            if (this.name.equals(command.name) && (this.timestamp == command.timestamp)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (int) (((long) this.name.hashCode()) & this.timestamp);
    }

    public int compareTo(Command another) {
        int compareTo = this.name.compareTo(another.name);
        if (compareTo != 0) {
            return compareTo;
        }
        if (this.timestamp < another.timestamp) {
            return -1;
        }
        if (this.timestamp > another.timestamp) {
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
