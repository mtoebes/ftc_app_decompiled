package com.qualcomm.robotcore.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class TypeConversion {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private TypeConversion() {
    }

    public static byte[] shortToByteArray(short shortInt) {
        return shortToByteArray(shortInt, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] shortToByteArray(short shortInt, ByteOrder byteOrder) {
        return ByteBuffer.allocate(2).order(byteOrder).putShort(shortInt).array();
    }

    public static byte[] intToByteArray(int integer) {
        return intToByteArray(integer, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] intToByteArray(int integer, ByteOrder byteOrder) {
        return ByteBuffer.allocate(4).order(byteOrder).putInt(integer).array();
    }

    public static byte[] longToByteArray(long longInt) {
        return longToByteArray(longInt, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] longToByteArray(long longInt, ByteOrder byteOrder) {
        return ByteBuffer.allocate(8).order(byteOrder).putLong(longInt).array();
    }

    public static short byteArrayToShort(byte[] byteArray) {
        return byteArrayToShort(byteArray, ByteOrder.BIG_ENDIAN);
    }

    public static short byteArrayToShort(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getShort();
    }

    public static int byteArrayToInt(byte[] byteArray) {
        return byteArrayToInt(byteArray, ByteOrder.BIG_ENDIAN);
    }

    public static int byteArrayToInt(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getInt();
    }

    public static long byteArrayToLong(byte[] byteArray) {
        return byteArrayToLong(byteArray, ByteOrder.BIG_ENDIAN);
    }

    public static long byteArrayToLong(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getLong();
    }

    public static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    public static double unsignedByteToDouble(byte b) {
        return (double) (b & 0xFF);
    }

    public static long unsignedIntToLong(int i) {
        return (long) i;
    }

    public static byte[] stringToUtf8(String javaString) {
        byte[] bytes = javaString.getBytes(CHARSET);
        if (javaString.equals(new String(bytes, CHARSET))) {
            return bytes;
        }
        throw new IllegalArgumentException(String.format("string cannot be cleanly encoded into %s - '%s' -> '%s'", new Object[]{CHARSET.name(), javaString, new String(bytes, CHARSET)}));
    }

    public static String utf8ToString(byte[] utf8String) {
        return new String(utf8String, CHARSET);
    }
}
