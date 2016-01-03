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
    public static final boolean DEBUG_LOGGING = false;


    public static final int BUFFER_SIZE = 27;
    public static final int START_ADDRESS = 3;
    public static final int WORD_SIZE = 2;
    public static final int MONITOR_LENGTH = 21;

    public static final int ADDRESS_BUFFER_STATUS = 3;

    public static final int ADDRESS_DIGITAL_INPUT_STATE = 20;
    public static final int ADDRESS_DIGITAL_IO_CONTROL = 21;
    public static final int ADDRESS_DIGITAL_OUTPUT_STATE = 22;

    public static final int ADDRESS_LED_SET = 23;

    public static final byte I2C_MODE_READ = Byte.MIN_VALUE;
    public static final byte I2C_MODE_WRITE = (byte) 0;

    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_FREQ = 2;
    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_MODE = 4;
    public static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_VOLTAGE = 0;
    public static final int ANALOG_VOLTAGE_OUTPUT_BUFFER_SIZE = 5;

    public static final int OFFSET_PULSE_OUTPUT_TIME = 0;
    public static final int OFFSET_PULSE_OUTPUT_PERIOD = 2;
    public static final int PULSE_OUTPUT_BUFFER_SIZE = 4;

    public static final int OFFSET_I2C_PORT_MODE = 0;
    public static final int OFFSET_I2C_PORT_I2C_ADDRESS = 1;
    public static final int OFFSET_I2C_PORT_MEMORY_ADDRESS = 2;
    public static final int OFFSET_I2C_PORT_MEMORY_LENGTH = 3;
    public static final int OFFSET_I2C_PORT_MEMORY_BUFFER = 4;
    public static final int I2C_PORT_BUFFER_SIZE = 32;

    public static final int OFFSET_I2C_PORT_FLAG = 31;
    public static final byte I2C_ACTION_FLAG = (byte) -1;
    public static final byte I2C_NO_ACTION_FLAG = (byte) 0;
    
    public static final int ANALOG_INPUT_BUFFER_SIZE = 2;

    private static final int NUMBER_OF_ANALOG_OUTPUT_PORTS = 2;
    private static final int NUMBER_OF_PULSE_OUTPUT_PORTS = 2;
    private static final int NUMBER_OF_I2C_PORTS = 6;
    private static final int NUMBER_OF_ANALOG_INPUT_PORTS = 8;

    private final I2cPortReadyCallback[] callbacks = new I2cPortReadyCallback[NUMBER_OF_I2C_PORTS];
    private ReadWriteRunnableSegment[] analogOutputSegments = new ReadWriteRunnableSegment[NUMBER_OF_ANALOG_OUTPUT_PORTS];
    private ReadWriteRunnableSegment[] pulseOutputSegments = new ReadWriteRunnableSegment[NUMBER_OF_PULSE_OUTPUT_PORTS];
    private ReadWriteRunnableSegment[] i2cSegments = new ReadWriteRunnableSegment[NUMBER_OF_I2C_PORTS];
    private ReadWriteRunnableSegment[] flagSegments = new ReadWriteRunnableSegment[NUMBER_OF_I2C_PORTS];

    protected ModernRoboticsUsbDeviceInterfaceModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, START_ADDRESS, DEBUG_LOGGING));

        for (int analogPort = 0; analogPort < NUMBER_OF_ANALOG_OUTPUT_PORTS; analogPort++) {
            this.analogOutputSegments[analogPort] = this.readWriteRunnable.createSegment(getAnalogOutputKey(analogPort), getAnalogOutputPortAddress(analogPort), ANALOG_VOLTAGE_OUTPUT_BUFFER_SIZE);
        }
        for (int pulsePort = 0; pulsePort < NUMBER_OF_PULSE_OUTPUT_PORTS; pulsePort++) {
            this.pulseOutputSegments[pulsePort] = this.readWriteRunnable.createSegment(getPulseOutputKey(pulsePort), getPulseOutputPortAddress(pulsePort), PULSE_OUTPUT_BUFFER_SIZE);
        }
        for (int i2cPort = 0; i2cPort < NUMBER_OF_I2C_PORTS; i2cPort++) {
            this.i2cSegments[i2cPort] = this.readWriteRunnable.createSegment(getI2cKey(i2cPort), getI2cPortAddress(i2cPort), I2C_PORT_BUFFER_SIZE);
            this.flagSegments[i2cPort] = this.readWriteRunnable.createSegment(getFlagKey(i2cPort), getI2cPortAddress(i2cPort) + OFFSET_I2C_PORT_FLAG, OFFSET_I2C_PORT_I2C_ADDRESS);
        }
    }

    public String getDeviceName() {
        return "Modern Robotics USB Device Interface Module";
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public int getAnalogInputValue(int channel) {
        validateAnalogInputPort(channel);
        return TypeConversion.byteArrayToShort(read(getAnalogInputPortAddress(channel), WORD_SIZE), ByteOrder.LITTLE_ENDIAN);
    }

    public Mode getDigitalChannelMode(int channel) {
        return m60a(channel, getDigitalIOControlByte());
    }

    public void setDigitalChannelMode(int channel, Mode mode) {
        int a = m59a(channel, mode);
        byte readFromWriteCache = readFromWriteCache(ADDRESS_DIGITAL_IO_CONTROL);
        if (mode == Mode.OUTPUT) {
            a |= readFromWriteCache;
        } else {
            a &= readFromWriteCache;
        }
        write(ADDRESS_DIGITAL_IO_CONTROL, a);
    }

    public boolean getDigitalChannelState(int channel) {
        int digitalOutputStateByte;
        if (Mode.OUTPUT == getDigitalChannelMode(channel)) {
            digitalOutputStateByte = getDigitalOutputStateByte();
        } else {
            digitalOutputStateByte = getDigitalInputStateByte();
        }
        return (digitalOutputStateByte & getDigitalBitMask(channel)) > 0;
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
        return read(ADDRESS_DIGITAL_IO_CONTROL);
    }

    public void setDigitalIOControlByte(byte input) {
        write(ADDRESS_DIGITAL_IO_CONTROL, input);
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
        validateLedPort(channel);
        return (read(ADDRESS_LED_SET) & getLedBitMask(channel)) > 0;
    }

    public void setLED(int channel, boolean set) {
        int i;
        validateLedPort(channel);
        byte readFromWriteCache = readFromWriteCache(ADDRESS_LED_SET);
        if (set) {
            i = readFromWriteCache | getLedBitMask(channel);
        } else {
            i = readFromWriteCache & (~getLedBitMask(channel));
        }
        write(ADDRESS_LED_SET, i);
    }

    public void setAnalogOutputVoltage(int port, int voltage) {
        validateAnalogOutputPort(port);
        Lock writeLock = this.analogOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.analogOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) voltage, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, 0, writeBuffer, 0, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(getAnalogOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setAnalogOutputFrequency(int port, int freq) {
        validateAnalogOutputPort(port);
        Lock writeLock = this.analogOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.analogOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) freq, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, 0, writeBuffer, WORD_SIZE, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(getAnalogOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setAnalogOutputMode(int port, byte mode) {
        validateAnalogOutputPort(port);
        Lock writeLock = this.analogOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.analogOutputSegments[port].getWriteBuffer();
        try {
            writeLock.lock();
            writeBuffer[PULSE_OUTPUT_BUFFER_SIZE] = mode;
            this.readWriteRunnable.queueSegmentWrite(getAnalogOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthOutputTime(int port, int time) {
        validatePulseOutputPort(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) time, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, OFFSET_PULSE_OUTPUT_TIME, writeBuffer, 0, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(getPulseOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthPeriod(int port, int period) {
        validateI2cPort(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] shortToByteArray = TypeConversion.shortToByteArray((short) period, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(shortToByteArray, 0, writeBuffer, WORD_SIZE, shortToByteArray.length);
            this.readWriteRunnable.queueSegmentWrite(getPulseOutputKey(port));
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
        validateI2cPort(physicalPort);
        validateI2CBufferLength(length);
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
        validateI2cPort(physicalPort);
        validateI2CBufferLength(length);
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
        validateI2cPort(physicalPort);
        try {
            this.i2cSegments[physicalPort].getReadLock().lock();
            byte[] readBuffer = this.i2cSegments[physicalPort].getReadBuffer();
            byte[] bArr = new byte[readBuffer[START_ADDRESS]];
            System.arraycopy(readBuffer, PULSE_OUTPUT_BUFFER_SIZE, bArr, 0, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.i2cSegments[physicalPort].getReadLock();
            bArr.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        validateI2cPort(physicalPort);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            byte[] bArr = new byte[writeBuffer[START_ADDRESS]];
            System.arraycopy(writeBuffer, PULSE_OUTPUT_BUFFER_SIZE, bArr, 0, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.i2cSegments[physicalPort].getWriteLock();
            bArr.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        validateI2cPort(physicalPort);
        validateI2CBufferLength(buffer.length);
        try {
            this.i2cSegments[physicalPort].getWriteLock().lock();
            System.arraycopy(buffer, 0, this.i2cSegments[physicalPort].getWriteBuffer(), PULSE_OUTPUT_BUFFER_SIZE, buffer.length);
        } finally {
            this.i2cSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setI2cPortActionFlag(int port) {
        validateI2cPort(port);
        try {
            this.i2cSegments[port].getWriteLock().lock();
            this.i2cSegments[port].getWriteBuffer()[OFFSET_I2C_PORT_FLAG] = I2C_ACTION_FLAG;
        } finally {
            this.i2cSegments[port].getWriteLock().unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int port) {
        validateI2cPort(port);
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
        validateI2cPort(port);
        this.readWriteRunnable.queueSegmentRead(getI2cKey(port));
    }

    public void writeI2cCacheToController(int port) {
        validateI2cPort(port);
        this.readWriteRunnable.queueSegmentWrite(getI2cKey(port));
    }

    public void writeI2cPortFlagOnlyToController(int port) {
        validateI2cPort(port);
        ReadWriteRunnableSegment readWriteRunnableSegment = this.i2cSegments[port];
        ReadWriteRunnableSegment readWriteRunnableSegment2 = this.flagSegments[port];
        try {
            readWriteRunnableSegment.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteBuffer()[0] = readWriteRunnableSegment.getWriteBuffer()[OFFSET_I2C_PORT_FLAG];
            this.readWriteRunnable.queueSegmentWrite(getFlagKey(port));
        } finally {
            readWriteRunnableSegment.getWriteLock().unlock();
            readWriteRunnableSegment2.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int port) {
        boolean z = DEBUG_LOGGING;
        validateI2cPort(port);
        try {
            this.i2cSegments[port].getReadLock().lock();
            if (this.i2cSegments[port].getReadBuffer()[0] == -128) {
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
        validateI2cPort(port);
        try {
            this.i2cSegments[port].getReadLock().lock();
            if (this.i2cSegments[port].getReadBuffer()[0] == 0) { //TODO originally was comparing to null, why?
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
        return checkPortData(port, read(START_ADDRESS));
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

    private void validateLedPort(int port) {
        if (port != 0 && port != 1) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", port));
        }
    }

    private void validateAnalogOutputPort(int port) {
        if (port != 0 && port >= NUMBER_OF_ANALOG_OUTPUT_PORTS) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", port));
        }
    }

    private void validatePulseOutputPort(int port) {
        if (port < 0 || port >= NUMBER_OF_PULSE_OUTPUT_PORTS) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", port));
        }
    }

    private void validateAnalogInputPort(int port) {
        if (port < 0 || port >= NUMBER_OF_ANALOG_INPUT_PORTS) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", port, 0, NUMBER_OF_ANALOG_INPUT_PORTS-1));
        }
    }

    private void validateI2cPort(int port) {
        if (port < 0 || port >= NUMBER_OF_I2C_PORTS) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", port, 0, NUMBER_OF_I2C_PORTS-1));
        }
    }

    private void validateI2CBufferLength(int length) {
        if (length > BUFFER_SIZE) {
            throw new IllegalArgumentException(String.format("buffer is too large (%d byte), max size is %d bytes", length, BUFFER_SIZE));
        }
    }

    private boolean checkPortData(int port, byte data) {
        return ((1 << port) & data) == 0;
    }

    public void readComplete() throws InterruptedException {
        if (this.callbacks != null) {
            byte read = read(START_ADDRESS);
            int i = 0;
            while (i < NUMBER_OF_I2C_PORTS) {
                if (this.callbacks[i] != null && checkPortData(i, read)) {
                    this.callbacks[i].portIsReady(i);
                }
                i ++;
            }
        }
    }

    private int getAnalogInputPortAddress(int port) {
        return (2 * port) + 4;
    }

    private int getAnalogOutputPortAddress(int port) {
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

    private int getAnalogOutputKey(int port) {
        return port;
    }

    private int getPulseOutputKey(int port) {
        return port + 2;
    }

    private int getI2cKey(int port) {
        return port + 4;
    }

    private int getFlagKey(int port) {
        return port + 10;
    }
}
