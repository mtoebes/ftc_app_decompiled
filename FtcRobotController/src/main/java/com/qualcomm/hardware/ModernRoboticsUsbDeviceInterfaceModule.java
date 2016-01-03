package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteOrder;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsUsbDeviceInterfaceModule extends ModernRoboticsUsbDevice implements DeviceInterfaceModule {
    public static final int ADDRESS_ANALOG_PORT_A0 = 4;
    public static final int ADDRESS_ANALOG_PORT_A1 = 6;
    public static final int ADDRESS_ANALOG_PORT_A2 = 8;
    public static final int ADDRESS_ANALOG_PORT_A3 = 10;
    public static final int ADDRESS_ANALOG_PORT_A4 = 12;
    public static final int ADDRESS_ANALOG_PORT_A5 = 14;
    public static final int ADDRESS_ANALOG_PORT_A6 = 16;
    public static final int ADDRESS_ANALOG_PORT_A7 = 18;
    public static final int ADDRESS_BUFFER_STATUS = 3;
    public static final int ADDRESS_DIGITAL_INPUT_STATE = 20;
    public static final int ADDRESS_DIGITAL_IO_CONTROL = 21;
    public static final int ADDRESS_DIGITAL_OUTPUT_STATE = 22;
    public static final int ADDRESS_I2C0 = 48;
    public static final int ADDRESS_I2C1 = 80;
    public static final int ADDRESS_I2C2 = 112;
    public static final int ADDRESS_I2C3 = 144;
    public static final int ADDRESS_I2C4 = 176;
    public static final int ADDRESS_I2C5 = 208;
    public static final int ADDRESS_LED_SET = 23;
    public static final int ADDRESS_PULSE_OUTPUT_PORT_0 = 36;
    public static final int ADDRESS_PULSE_OUTPUT_PORT_1 = 40;
    public static final int ADDRESS_VOLTAGE_OUTPUT_PORT_0 = 24;
    public static final int ADDRESS_VOLTAGE_OUTPUT_PORT_1 = 30;
    public static final int ANALOG_VOLTAGE_OUTPUT_BUFFER_SIZE = 5;
    public static final byte BUFFER_FLAG_I2C0 = (byte) 1;
    public static final byte BUFFER_FLAG_I2C1 = (byte) 2;
    public static final byte BUFFER_FLAG_I2C2 = (byte) 4;
    public static final byte BUFFER_FLAG_I2C3 = (byte) 8;
    public static final byte BUFFER_FLAG_I2C4 = (byte) 16;
    public static final byte BUFFER_FLAG_I2C5 = (byte) 32;
    public static final int D0_MASK = 1;
    public static final int D1_MASK = 2;
    public static final int D2_MASK = 4;
    public static final int D3_MASK = 8;
    public static final int D4_MASK = 16;
    public static final int D5_MASK = 32;
    public static final int D6_MASK = 64;
    public static final int D7_MASK = 128;
    public static final boolean DEBUG_LOGGING = false;
    public static final byte I2C_ACTION_FLAG = (byte) -1;
    public static final byte I2C_MODE_READ = Byte.MIN_VALUE;
    public static final byte I2C_MODE_WRITE = (byte) 0;
    public static final byte I2C_NO_ACTION_FLAG = (byte) 0;
    public static final int I2C_PORT_BUFFER_SIZE = 32;
    public static final int LED_0_BIT_MASK = 1;
    public static final int LED_1_BIT_MASK = 2;
    public static final int MAX_ANALOG_PORT_NUMBER = 7;
    public static final int MAX_I2C_PORT_NUMBER = 5;
    public static final int MIN_ANALOG_PORT_NUMBER = 0;
    public static final int MIN_I2C_PORT_NUMBER = 0;
    public static final int MONITOR_LENGTH = 21;
    public static final int NUMBER_OF_PORTS = 6;
    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_FREQ = 2;
    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_MODE = 4;
    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_VOLTAGE = 0;
    public static final int OFFSET_I2C_PORT_FLAG = 31;
    public static final int OFFSET_I2C_PORT_I2C_ADDRESS = 1;
    public static final int OFFSET_I2C_PORT_MEMORY_ADDRESS = 2;
    public static final int OFFSET_I2C_PORT_MEMORY_BUFFER = 4;
    public static final int OFFSET_I2C_PORT_MEMORY_LENGTH = 3;
    public static final int OFFSET_I2C_PORT_MODE = 0;
    public static final int OFFSET_PULSE_OUTPUT_PERIOD = 2;
    public static final int OFFSET_PULSE_OUTPUT_TIME = 0;
    public static final int PULSE_OUTPUT_BUFFER_SIZE = 4;
    public static final int SIZE_ANALOG_BUFFER = 2;
    public static final int SIZE_I2C_BUFFER = 27;
    public static final int START_ADDRESS = 3;
    public static final int WORD_SIZE = 2;
    private static final int[] VOLTAGE_OUTPUT_PORTS;
    private static final int[] PULSE_OUTPUT_PORTS;
    private static final int[] I2C_PORTS;
    private static final int[] FLAG_PORTS;
    private final I2cPortReadyCallback[] callbacks;
    private ReadWriteRunnableSegment[] voltageOutputSegments;
    private ReadWriteRunnableSegment[] pulseOutputSegments;
    private ReadWriteRunnableSegment[] i2cSegments;
    private ReadWriteRunnableSegment[] flagSegments;

    static {
        VOLTAGE_OUTPUT_PORTS = new int[]{0, 1};
        PULSE_OUTPUT_PORTS = new int[]{2, 3};
        I2C_PORTS = new int[]{4, 5, 6, 7, 8, 9};
        FLAG_PORTS = new int[]{10, 11, 12, 13, 14, 15};
    }

    protected ModernRoboticsUsbDeviceInterfaceModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, START_ADDRESS, DEBUG_LOGGING));
        int i;
        int i2 = OFFSET_PULSE_OUTPUT_TIME;
        this.callbacks = new I2cPortReadyCallback[NUMBER_OF_PORTS];
        this.voltageOutputSegments = new ReadWriteRunnableSegment[VOLTAGE_OUTPUT_PORTS.length];
        this.pulseOutputSegments = new ReadWriteRunnableSegment[PULSE_OUTPUT_PORTS.length];
        this.i2cSegments = new ReadWriteRunnableSegment[I2C_PORTS.length];
        this.flagSegments = new ReadWriteRunnableSegment[FLAG_PORTS.length];
        for (i = OFFSET_PULSE_OUTPUT_TIME; i < VOLTAGE_OUTPUT_PORTS.length; i += OFFSET_I2C_PORT_I2C_ADDRESS) {
            this.voltageOutputSegments[i] = this.readWriteRunnable.createSegment(VOLTAGE_OUTPUT_PORTS[i], getVoltageOutputPortAddress(i), MAX_I2C_PORT_NUMBER);
        }
        for (i = OFFSET_PULSE_OUTPUT_TIME; i < PULSE_OUTPUT_PORTS.length; i += OFFSET_I2C_PORT_I2C_ADDRESS) {
            this.pulseOutputSegments[i] = this.readWriteRunnable.createSegment(PULSE_OUTPUT_PORTS[i], getPulseOutputPortAddress(i), PULSE_OUTPUT_BUFFER_SIZE);
        }
        while (i2 < I2C_PORTS.length) {
            this.i2cSegments[i2] = this.readWriteRunnable.createSegment(I2C_PORTS[i2], getI2cPortAddress(i2), I2C_PORT_BUFFER_SIZE);
            this.flagSegments[i2] = this.readWriteRunnable.createSegment(FLAG_PORTS[i2], getI2cPortAddress(i2) + OFFSET_I2C_PORT_FLAG, OFFSET_I2C_PORT_I2C_ADDRESS);
            i2 += OFFSET_I2C_PORT_I2C_ADDRESS;
        }
    }

    public String getDeviceName() {
        return "Modern Robotics USB Device Interface Module";
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public int getAnalogInputValue(int channel) {
        m65d(channel);
        return TypeConversion.byteArrayToShort(read(getAnalogPortAddress(channel), WORD_SIZE), ByteOrder.LITTLE_ENDIAN);
    }

    public Mode getDigitalChannelMode(int channel) {
        return m60a(channel, getDigitalIOControlByte());
    }

    public void setDigitalChannelMode(int channel, Mode mode) {
        int a = m59a(channel, mode);
        byte readFromWriteCache = readFromWriteCache(MONITOR_LENGTH);
        if (mode == Mode.OUTPUT) {
            a |= readFromWriteCache;
        } else {
            a &= readFromWriteCache;
        }
        write(MONITOR_LENGTH, a);
    }

    public boolean getDigitalChannelState(int channel) {
        int digitalOutputStateByte;
        if (Mode.OUTPUT == getDigitalChannelMode(channel)) {
            digitalOutputStateByte = getDigitalOutputStateByte();
        } else {
            digitalOutputStateByte = getDigitalInputStateByte();
        }
        return (digitalOutputStateByte & getDigitalBitMask(channel)) > 0 || DEBUG_LOGGING;
    }

    public void setDigitalChannelState(int channel, boolean state) {
        if (Mode.OUTPUT == getDigitalChannelMode(channel)) {
            int i;
            byte readFromWriteCache = readFromWriteCache(ADDRESS_DIGITAL_OUTPUT_STATE);
            if (state) {
                i = readFromWriteCache | getDigitalBitMask(channel);
            } else {
                i = readFromWriteCache & (~getDigitalBitMask(channel));
            }
            setDigitalOutputByte((byte) i);
        }
    }

    public int getDigitalInputStateByte() {
        return TypeConversion.unsignedByteToInt(read(ADDRESS_DIGITAL_INPUT_STATE));
    }

    public byte getDigitalIOControlByte() {
        return read(MONITOR_LENGTH);
    }

    public void setDigitalIOControlByte(byte input) {
        write(MONITOR_LENGTH, input);
    }

    public byte getDigitalOutputStateByte() {
        return read(ADDRESS_DIGITAL_OUTPUT_STATE);
    }

    public void setDigitalOutputByte(byte input) {
        write(ADDRESS_DIGITAL_OUTPUT_STATE, input);
    }

    private int m59a(int i, Mode mode) {
        if (mode == Mode.OUTPUT) {
            return getDigitalBitMask(i);
        } else {
            return ~getDigitalBitMask(i);
        }
    }

    private Mode m60a(int i, int i2) {
        if ((getDigitalBitMask(i) & i2) > 0) {
            return Mode.OUTPUT;
        } else {
            return Mode.INPUT;
        }
    }

    public boolean getLEDState(int channel) {
        m61a(channel);
        return (read(ADDRESS_LED_SET) & getLedBitMask(channel)) > 0 || DEBUG_LOGGING;
    }

    public void setLED(int channel, boolean set) {
        int i;
        m61a(channel);
        byte readFromWriteCache = readFromWriteCache(ADDRESS_LED_SET);
        if (set) {
            i = readFromWriteCache | getLedBitMask(channel);
        } else {
            i = readFromWriteCache & (~getLedBitMask(channel));
        }
        write(ADDRESS_LED_SET, i);
    }

    public void setAnalogOutputVoltage(int port, int voltage) {
        m63b(port);
        Lock writeLock = this.voltageOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.voltageOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) voltage, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, OFFSET_PULSE_OUTPUT_TIME, writeBuffer, OFFSET_PULSE_OUTPUT_TIME, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(VOLTAGE_OUTPUT_PORTS[port]);
        } finally {
            writeLock.unlock();
        }
    }

    public void setAnalogOutputFrequency(int port, int freq) {
        m63b(port);
        Lock writeLock = this.voltageOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.voltageOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) freq, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, OFFSET_PULSE_OUTPUT_TIME, writeBuffer, WORD_SIZE, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(VOLTAGE_OUTPUT_PORTS[port]);
        } finally {
            writeLock.unlock();
        }
    }

    public void setAnalogOutputMode(int port, byte mode) {
        m63b(port);
        Lock writeLock = this.voltageOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.voltageOutputSegments[port].getWriteBuffer();
        try {
            writeLock.lock();
            writeBuffer[PULSE_OUTPUT_BUFFER_SIZE] = mode;
            this.readWriteRunnable.queueSegmentWrite(VOLTAGE_OUTPUT_PORTS[port]);
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthOutputTime(int port, int time) {
        m64c(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) time, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, OFFSET_PULSE_OUTPUT_TIME, writeBuffer, OFFSET_PULSE_OUTPUT_TIME, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(PULSE_OUTPUT_PORTS[port]);
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthPeriod(int port, int period) {
        m66e(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) period, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, OFFSET_PULSE_OUTPUT_TIME, writeBuffer, WORD_SIZE, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(PULSE_OUTPUT_PORTS[port]);
        } finally {
            writeLock.unlock();
        }
    }

    public int getPulseWidthOutputTime(int port) {
        throw new UnsupportedOperationException("getPulseWidthOutputTime is not implemented.");
    }

    public int getPulseWidthPeriod(int port) {
        throw new UnsupportedOperationException("getPulseWidthOutputTime is not implemented.");
    }

    public void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        m66e(physicalPort);
        m67f(length);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_PULSE_OUTPUT_TIME] = I2C_MODE_READ;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[WORD_SIZE] = (byte) memAddress;
            writeBuffer[START_ADDRESS] = (byte) length;
        } finally {
            this.i2cSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        m66e(physicalPort);
        m67f(length);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_PULSE_OUTPUT_TIME] = I2C_NO_ACTION_FLAG;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[WORD_SIZE] = (byte) memAddress;
            writeBuffer[START_ADDRESS] = (byte) length;
        } finally {
            this.i2cSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        m66e(physicalPort);
        try {
            this.i2cSegments[physicalPort].getReadLock().lock();
            byte[] readBuffer = this.i2cSegments[physicalPort].getReadBuffer();
            byte[] bArr = new byte[readBuffer[START_ADDRESS]];
            System.arraycopy(readBuffer, PULSE_OUTPUT_BUFFER_SIZE, bArr, OFFSET_PULSE_OUTPUT_TIME, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.i2cSegments[physicalPort].getReadLock();
            bArr.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        m66e(physicalPort);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            byte[] bArr = new byte[writeBuffer[START_ADDRESS]];
            System.arraycopy(writeBuffer, PULSE_OUTPUT_BUFFER_SIZE, bArr, OFFSET_PULSE_OUTPUT_TIME, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.i2cSegments[physicalPort].getWriteLock();
            bArr.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        m66e(physicalPort);
        m67f(buffer.length);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            System.arraycopy(buffer, OFFSET_PULSE_OUTPUT_TIME, this.i2cSegments[physicalPort].getWriteBuffer(), PULSE_OUTPUT_BUFFER_SIZE, buffer.length);
        } finally {
            this.i2cSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setI2cPortActionFlag(int port) {
        m66e(port);
        try {
            this.i2cSegments[port].getWriteLock().lock();
            this.i2cSegments[port].getWriteBuffer()[OFFSET_I2C_PORT_FLAG] = I2C_ACTION_FLAG;
        } finally {
            this.i2cSegments[port].getWriteLock().unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int port) {
        m66e(port);
        try {
            this.i2cSegments[port].getReadLock().lock();
            boolean z = this.i2cSegments[port].getReadBuffer()[OFFSET_I2C_PORT_FLAG] == -1 || DEBUG_LOGGING;
            this.i2cSegments[port].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.i2cSegments[port].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
    }

    public void readI2cCacheFromController(int port) {
        m66e(port);
        this.readWriteRunnable.queueSegmentRead(I2C_PORTS[port]);
    }

    public void writeI2cCacheToController(int port) {
        m66e(port);
        this.readWriteRunnable.queueSegmentWrite(I2C_PORTS[port]);
    }

    public void writeI2cPortFlagOnlyToController(int port) {
        m66e(port);
        ReadWriteRunnableSegment readWriteRunnableSegment = this.i2cSegments[port];
        ReadWriteRunnableSegment readWriteRunnableSegment2 = this.flagSegments[port];
        try {
            readWriteRunnableSegment.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteBuffer()[OFFSET_PULSE_OUTPUT_TIME] = readWriteRunnableSegment.getWriteBuffer()[OFFSET_I2C_PORT_FLAG];
            this.readWriteRunnable.queueSegmentWrite(FLAG_PORTS[port]);
        } finally {
            readWriteRunnableSegment.getWriteLock().unlock();
            readWriteRunnableSegment2.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int port) {
        boolean z = DEBUG_LOGGING;
        m66e(port);
        try {
            this.i2cSegments[port].getReadLock().lock();
            if (this.i2cSegments[port].getReadBuffer()[OFFSET_PULSE_OUTPUT_TIME] == -128) {
                z = true;
            }
            this.i2cSegments[port].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.i2cSegments[port].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
    }

    public boolean isI2cPortInWriteMode(int port) {
        boolean z = DEBUG_LOGGING;
        m66e(port);
        try {
            this.i2cSegments[port].getReadLock().lock();
            if (this.i2cSegments[port].getReadBuffer()[OFFSET_PULSE_OUTPUT_TIME] == 0) { //TODO originally was comparing to null, why?
                z = true;
            }
            this.i2cSegments[port].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.i2cSegments[port].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
    }

    public boolean isI2cPortReady(int port) {
        return m62a(port, read(START_ADDRESS));
    }

    public Lock getI2cReadCacheLock(int port) {
        return this.i2cSegments[port].getReadLock();
    }

    public Lock getI2cWriteCacheLock(int port) {
        return this.i2cSegments[port].getWriteLock();
    }

    public byte[] getI2cReadCache(int port) {
        return this.i2cSegments[port].getReadBuffer();
    }

    public byte[] getI2cWriteCache(int port) {
        return this.i2cSegments[port].getWriteBuffer();
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback, int port) {
        this.callbacks[port] = callback;
    }

    public void deregisterForPortReadyCallback(int port) {
        this.callbacks[port] = null;
    }

    @Deprecated
    public void readI2cCacheFromModule(int port) {
        readI2cCacheFromController(port);
    }

    @Deprecated
    public void writeI2cCacheToModule(int port) {
        writeI2cCacheToController(port);
    }

    @Deprecated
    public void writeI2cPortFlagOnlyToModule(int port) {
        writeI2cPortFlagOnlyToController(port);
    }

    private void m61a(int i) {
        if (i != 0 && i != OFFSET_I2C_PORT_I2C_ADDRESS) {
            Object[] objArr = new Object[OFFSET_I2C_PORT_I2C_ADDRESS];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", objArr));
        }
    }

    private void m63b(int i) {
        if (i != 0 && i != OFFSET_I2C_PORT_I2C_ADDRESS) {
            Object[] objArr = new Object[OFFSET_I2C_PORT_I2C_ADDRESS];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", objArr));
        }
    }

    private void m64c(int i) {
        if (i != 0 && i != OFFSET_I2C_PORT_I2C_ADDRESS) {
            Object[] objArr = new Object[OFFSET_I2C_PORT_I2C_ADDRESS];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", objArr));
        }
    }

    private void m65d(int i) {
        if (i < 0 || i > MAX_ANALOG_PORT_NUMBER) {
            Object[] objArr = new Object[START_ADDRESS];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            objArr[OFFSET_I2C_PORT_I2C_ADDRESS] = OFFSET_PULSE_OUTPUT_TIME;
            objArr[WORD_SIZE] = MAX_ANALOG_PORT_NUMBER;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", objArr));
        }
    }

    private void m66e(int i) {
        if (i < 0 || i > MAX_I2C_PORT_NUMBER) {
            Object[] objArr = new Object[START_ADDRESS];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            objArr[OFFSET_I2C_PORT_I2C_ADDRESS] = OFFSET_PULSE_OUTPUT_TIME;
            objArr[WORD_SIZE] = MAX_I2C_PORT_NUMBER;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", objArr));
        }
    }

    private void m67f(int i) {
        if (i > SIZE_I2C_BUFFER) {
            Object[] objArr = new Object[WORD_SIZE];
            objArr[OFFSET_PULSE_OUTPUT_TIME] = i;
            objArr[OFFSET_I2C_PORT_I2C_ADDRESS] = SIZE_I2C_BUFFER;
            throw new IllegalArgumentException(String.format("buffer is too large (%d byte), max size is %d bytes", objArr));
        }
    }

    private boolean m62a(int i, byte b) {
        return ((1 << i) & b) == 0 || DEBUG_LOGGING;
    }

    public void readComplete() throws InterruptedException {
        if (this.callbacks != null) {
            byte read = read(START_ADDRESS);
            int i = OFFSET_PULSE_OUTPUT_TIME;
            while (i < NUMBER_OF_PORTS) {
                if (this.callbacks[i] != null && m62a(i, read)) {
                    this.callbacks[i].portIsReady(i);
                }
                i += OFFSET_I2C_PORT_I2C_ADDRESS;
            }
        }
    }

    private int getAnalogPortAddress(int port) {
        return (2 * port) + 4;
    }

    private int getVoltageOutputPortAddress(int port) {
        return (6 * port) + 24;
    }

    private int getDigitalBitMask(int port) {
        return 1 << port;
    }

    private int getLedBitMask(int port) {
        return port+1;
    }

    private int getPulseOutputPortAddress(int port) {
        return 36 + (4 * port);
    }

    private int getI2cPortAddress(int port) {
        return (I2C_PORT_BUFFER_SIZE * port) + 48;
    }
}
