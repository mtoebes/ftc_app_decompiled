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
    private static final boolean DEBUG_LOGGING = false;

    private static final int BUFFER_SIZE = 27;
    private static final int START_ADDRESS = 3;
    private static final int MONITOR_LENGTH = 21;

    private static final int ADDRESS_BUFFER_STATUS = 3;

    private static final int ADDRESS_DIGITAL_INPUT_STATE = 20;
    private static final int ADDRESS_DIGITAL_IO_CONTROL = 21;
    private static final int ADDRESS_DIGITAL_OUTPUT_STATE = 22;
    private static final int ADDRESS_LED_SET = 23;

    private static final byte I2C_MODE_READ = Byte.MIN_VALUE;
    private static final byte I2C_MODE_WRITE = (byte) 0;

    private static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_VOLTAGE = 0;
    private static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_FREQ = 2;
    private static final int OFFSET_ANALOG_VOLTAGE_OUTPUT_MODE = 4;
    private static final int ANALOG_VOLTAGE_OUTPUT_BUFFER_SIZE = 5;
    private static final int ANALOG_VOLTAGE_OUTPUT_PORT_ADDRESS = 24;

    private static final int OFFSET_PULSE_OUTPUT_TIME = 0;
    private static final int OFFSET_PULSE_OUTPUT_PERIOD = 2;
    private static final int PULSE_OUTPUT_BUFFER_SIZE = 4;
    private static final int PULSE_OUTPUT_PORT_ADDRESS = 36;

    private static final int OFFSET_I2C_PORT_MODE = 0;
    private static final int OFFSET_I2C_PORT_I2C_ADDRESS = 1;
    private static final int OFFSET_I2C_PORT_MEMORY_ADDRESS = 2;
    private static final int OFFSET_I2C_PORT_MEMORY_LENGTH = 3;
    private static final int OFFSET_I2C_PORT_MEMORY_BUFFER = 4;
    private static final int I2C_PORT_BUFFER_SIZE = 32;
    private static final int I2C_PORT_PORT_ADDRESS = 48;

    private static final int OFFSET_I2C_PORT_FLAG = 31;
    private static final byte I2C_ACTION_FLAG = (byte) -1;

    private static final int ANALOG_INPUT_BUFFER_SIZE = 2;
    private static final int ANALOG_INPUT_PORT_ADDRESS = 4;

    private static final int NUMBER_OF_ANALOG_OUTPUT_PORTS = 2;
    private static final int NUMBER_OF_PULSE_OUTPUT_PORTS = 2;
    private static final int NUMBER_OF_I2C_PORTS = 6;
    private static final int NUMBER_OF_ANALOG_INPUT_PORTS = 8;

    private final I2cPortReadyCallback[] callbacks = new I2cPortReadyCallback[NUMBER_OF_I2C_PORTS];
    private final ReadWriteRunnableSegment[] analogOutputSegments = new ReadWriteRunnableSegment[NUMBER_OF_ANALOG_OUTPUT_PORTS];
    private final ReadWriteRunnableSegment[] pulseOutputSegments = new ReadWriteRunnableSegment[NUMBER_OF_PULSE_OUTPUT_PORTS];
    private final ReadWriteRunnableSegment[] i2cSegments = new ReadWriteRunnableSegment[NUMBER_OF_I2C_PORTS];
    private final ReadWriteRunnableSegment[] flagSegments = new ReadWriteRunnableSegment[NUMBER_OF_I2C_PORTS];

    ModernRoboticsUsbDeviceInterfaceModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
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
        return TypeConversion.byteArrayToShort(read(getAnalogInputPortAddress(channel), ANALOG_INPUT_BUFFER_SIZE), ByteOrder.LITTLE_ENDIAN);
    }

    public Mode getDigitalChannelMode(int channel) {
        return gitDigitalMode(channel, getDigitalIOControlByte());
    }

    public void setDigitalChannelMode(int channel, Mode mode) {
        int mask = gitDigitalBitMask(channel, mode);
        byte data = readFromWriteCache(ADDRESS_DIGITAL_IO_CONTROL);
        if (mode == Mode.OUTPUT) {
            data |= mask;
        } else {
            data &= mask;
        }
        write(ADDRESS_DIGITAL_IO_CONTROL, data);
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
            byte data = readFromWriteCache(ADDRESS_DIGITAL_OUTPUT_STATE);
            int mask = getDigitalBitMask(channel);
            if (state) {
                data |= mask;
            } else {
                data &= ~mask;
            }
            setDigitalOutputByte(data);
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

    private int gitDigitalBitMask(int port, Mode mode) {
        if (mode == Mode.OUTPUT) {
            return getDigitalBitMask(port);
        } else {
            return ~getDigitalBitMask(port);
        }
    }

    private Mode gitDigitalMode(int port, int mask) {
        if ((getDigitalBitMask(port) & mask) > 0) {
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
        validateLedPort(channel);
        byte data = readFromWriteCache(ADDRESS_LED_SET);
        int mask = getLedBitMask(channel);
        if (set) {
            data |= mask;
        } else {
            data &= ~mask;
        }
        write(ADDRESS_LED_SET, data);
    }

    public void setAnalogOutputVoltage(int port, int voltage) {
        validateAnalogOutputPort(port);
        Lock writeLock = this.analogOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.analogOutputSegments[port].getWriteBuffer();
        byte[] voltageBuffer = TypeConversion.shortToByteArray((short) voltage, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(voltageBuffer, OFFSET_ANALOG_VOLTAGE_OUTPUT_VOLTAGE, writeBuffer, 0, voltageBuffer.length);
            this.readWriteRunnable.queueSegmentWrite(getAnalogOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setAnalogOutputFrequency(int port, int freq) {
        validateAnalogOutputPort(port);
        Lock writeLock = this.analogOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.analogOutputSegments[port].getWriteBuffer();
        byte[] freqBuffer = TypeConversion.shortToByteArray((short) freq, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(freqBuffer, 0, writeBuffer, OFFSET_ANALOG_VOLTAGE_OUTPUT_FREQ, freqBuffer.length);
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
            writeBuffer[OFFSET_ANALOG_VOLTAGE_OUTPUT_MODE] = mode;
            this.readWriteRunnable.queueSegmentWrite(getAnalogOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthOutputTime(int port, int time) {
        validatePulseOutputPort(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] timeBuffer = TypeConversion.shortToByteArray((short) time, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(timeBuffer, 0, writeBuffer, OFFSET_PULSE_OUTPUT_TIME, timeBuffer.length);
            this.readWriteRunnable.queueSegmentWrite(getPulseOutputKey(port));
        } finally {
            writeLock.unlock();
        }
    }

    public void setPulseWidthPeriod(int port, int period) {
        validateI2cPort(port);
        Lock writeLock = this.pulseOutputSegments[port].getWriteLock();
        byte[] writeBuffer = this.pulseOutputSegments[port].getWriteBuffer();
        byte[] periodBuffer = TypeConversion.shortToByteArray((short) period, ByteOrder.LITTLE_ENDIAN);
        try {
            writeLock.lock();
            System.arraycopy(periodBuffer, 0, writeBuffer, OFFSET_PULSE_OUTPUT_PERIOD, periodBuffer.length);
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
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = I2C_MODE_READ;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        validateI2cPort(physicalPort);
        validateI2CBufferLength(length);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = I2C_MODE_WRITE;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        validateI2cPort(physicalPort);
        try {
            getI2cReadCacheLock(physicalPort).lock();
            byte[] readBuffer = this.i2cSegments[physicalPort].getReadBuffer();
            byte[] copyBuffer = new byte[readBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(readBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            getI2cReadCacheLock(physicalPort).unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        validateI2cPort(physicalPort);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = this.i2cSegments[physicalPort].getWriteBuffer();
            byte[] copyBuffer = new byte[writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(writeBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        validateI2cPort(physicalPort);
        validateI2CBufferLength(buffer.length);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            System.arraycopy(buffer, 0, this.i2cSegments[physicalPort].getWriteBuffer(), OFFSET_I2C_PORT_MEMORY_BUFFER, buffer.length);
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void setI2cPortActionFlag(int port) {
        validateI2cPort(port);
        try {
            getI2cWriteCacheLock(port).lock();
            this.i2cSegments[port].getWriteBuffer()[OFFSET_I2C_PORT_FLAG] = I2C_ACTION_FLAG;
        } finally {
            getI2cWriteCacheLock(port).unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int port) {
        validateI2cPort(port);
        boolean isFlagSet = false;
        try {
            getI2cReadCacheLock(port).lock();
            isFlagSet = this.i2cSegments[port].getReadBuffer()[OFFSET_I2C_PORT_FLAG] == I2C_ACTION_FLAG;
        } catch (Throwable ignored) {
        } finally {
            getI2cReadCacheLock(port).unlock();
        }
        return isFlagSet;
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
        ReadWriteRunnableSegment i2cSegment = this.i2cSegments[port];
        ReadWriteRunnableSegment flagSegment = this.flagSegments[port];
        try {
            i2cSegment.getWriteLock().lock();
            flagSegment.getWriteLock().lock();
            flagSegment.getWriteBuffer()[0] = i2cSegment.getWriteBuffer()[OFFSET_I2C_PORT_FLAG];
            this.readWriteRunnable.queueSegmentWrite(getFlagKey(port));
        } finally {
            i2cSegment.getWriteLock().unlock();
            flagSegment.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int port) {
        boolean isReadMode = DEBUG_LOGGING;
        validateI2cPort(port);
        try {
            getI2cReadCacheLock(port).lock();
            isReadMode = (this.i2cSegments[port].getReadBuffer()[OFFSET_I2C_PORT_MODE] == I2C_MODE_READ);
        } catch (Throwable ignored) {
        } finally {
            getI2cReadCacheLock(port).unlock();
        }
        return isReadMode;
    }

    public boolean isI2cPortInWriteMode(int port) {
        boolean isWriteMode = false;
        validateI2cPort(port);
        try {
            getI2cReadCacheLock(port).lock();
            isWriteMode = (this.i2cSegments[port].getReadBuffer()[OFFSET_I2C_PORT_MODE] == I2C_MODE_WRITE);
        } catch (Throwable ignored) {
        } finally {
            getI2cReadCacheLock(port).unlock();
        }
        return isWriteMode;
    }

    public boolean isI2cPortReady(int port) {
        return checkPortData(port, read(ADDRESS_BUFFER_STATUS));
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
        if (port < 0 && port > 1) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are 0 and 1.", port));
        }
    }

    private void validateAnalogOutputPort(int port) {
        if (port < 0 || port >= NUMBER_OF_ANALOG_OUTPUT_PORTS) {
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
            byte data = read(ADDRESS_BUFFER_STATUS);
            for(int port = 0; port < NUMBER_OF_I2C_PORTS; port++) {
                if (this.callbacks[port] != null && checkPortData(port, data)) {
                    this.callbacks[port].portIsReady(port);
                }
            }
        }
    }

    private int getAnalogInputPortAddress(int port) {
        return (ANALOG_INPUT_BUFFER_SIZE * port) + ANALOG_INPUT_PORT_ADDRESS;
    }

    private int getAnalogOutputPortAddress(int port) {
        //TODO this is 6 vs buffer of 5, why?
        return ((ANALOG_VOLTAGE_OUTPUT_BUFFER_SIZE+1) * port) + ANALOG_VOLTAGE_OUTPUT_PORT_ADDRESS;
    }

    private int getPulseOutputPortAddress(int port) {
        return (PULSE_OUTPUT_BUFFER_SIZE * port) + PULSE_OUTPUT_PORT_ADDRESS;
    }

    private int getI2cPortAddress(int port) {
        return (I2C_PORT_BUFFER_SIZE * port) + I2C_PORT_PORT_ADDRESS;
    }

    private int getAnalogOutputKey(int port) {
        return port;
    }

    private int getPulseOutputKey(int port) {
        return getAnalogOutputKey(port) + 2;
    }

    private int getI2cKey(int port) {
        return getPulseOutputKey(port) + 4;
    }

    private int getFlagKey(int port) {
        return getI2cKey(port) + 6;
    }

    private int getDigitalBitMask(int port) {
        return 1 << port;
    }

    private int getLedBitMask(int port) {
        return port + 1;
    }
}
