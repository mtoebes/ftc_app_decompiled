package com.qualcomm.robotcore.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class TypeConversion {
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private TypeConversion() {
    }

    /**
     * convert a short into a byte array; big endian is assumed
     *
     * @param shortInt short to convert
     * @return a byte array
     */
    public static byte[] shortToByteArray(short shortInt) {
        return shortToByteArray(shortInt, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert a short into a byte array
     *
     * @param shortInt  short to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a byte array
     */
    public static byte[] shortToByteArray(short shortInt, ByteOrder byteOrder) {
        return ByteBuffer.allocate(2).order(byteOrder).putShort(shortInt).array();
    }

    /**
     * convert an int into a byte array; big endian is assumed
     *
     * @param integer integer to convert
     * @return a byte array
     */
    public static byte[] intToByteArray(int integer) {
        return intToByteArray(integer, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert an int into a byte array
     *
     * @param integer   integer to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a byte array
     */
    public static byte[] intToByteArray(int integer, ByteOrder byteOrder) {
        return ByteBuffer.allocate(4).order(byteOrder).putInt(integer).array();
    }

    /**
     * convert a long into a byte array; big endian is assumed
     *
     * @param longInt long to convert
     * @return a byte array
     */
    public static byte[] longToByteArray(long longInt) {
        return longToByteArray(longInt, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert a long into a byte array
     *
     * @param longInt   long to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a byte array
     */
    public static byte[] longToByteArray(long longInt, ByteOrder byteOrder) {
        return ByteBuffer.allocate(8).order(byteOrder).putLong(longInt).array();
    }

    /**
     * convert a byte array into a short
     *
     * @param byteArray byte array to convert
     * @return a short
     */
    public static short byteArrayToShort(byte[] byteArray) {
        return byteArrayToShort(byteArray, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert a byte array into a short
     *
     * @param byteArray byte array to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a short
     */
    public static short byteArrayToShort(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getShort();
    }

    /**
     * convert a byte array into an int; big endian is assumed
     *
     * @param byteArray byte array to convert
     * @return a integer
     */
    public static int byteArrayToInt(byte[] byteArray) {
        return byteArrayToInt(byteArray, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert a byte array into an int
     *
     * @param byteArray byte array to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a integer
     */
    public static int byteArrayToInt(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getInt();
    }

    /**
     * convert a byte array into a long; big endian is assumed
     *
     * @param byteArray byte array to convert
     * @return a long
     */
    public static long byteArrayToLong(byte[] byteArray) {
        return byteArrayToLong(byteArray, ByteOrder.BIG_ENDIAN);
    }

    /**
     * convert a byte array into a long
     *
     * @param byteArray byte array to convert
     * @param byteOrder order of bytes, big or little endian
     * @return a long
     */
    public static long byteArrayToLong(byte[] byteArray, ByteOrder byteOrder) {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getLong();
    }

    /**
     * Accept a byte, treat that byte as an unsigned byte, then covert it to the return type
     *
     * @param b byte to convert
     * @return a positive between 0 and 255
     */
    public static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Accept a byte, treat that byte as an unsigned byte, then covert it to the return type
     *
     * @param b byte to treat as unsigned byte
     * @return a positive between 0 and 255
     */
    public static double unsignedByteToDouble(byte b) {
        return (double) (b & 0xFF);
    }

    /**
     * Accept an int, treat that int as an unsigned int, then covert it to the return type
     *
     * @param i int to treat as unsigned int
     * @return a positive between 0 and 2^32
     */
    public static long unsignedIntToLong(int i) {
        return (long) i;
    }

    /**
     * Convert a Java String into a UTF-8 byte array
     *
     * @param javaString Java String to convert
     * @return UTF-8 byte array
     */
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
