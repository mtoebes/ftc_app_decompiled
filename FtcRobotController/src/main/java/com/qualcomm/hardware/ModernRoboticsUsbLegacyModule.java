package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsUsbLegacyModule extends ModernRoboticsUsbDevice implements LegacyModule {

    private static final byte NXT_MODE_READ = -127;
    private static final byte NXT_MODE_ANALOG = (byte) 0;
    private static final byte NXT_MODE_I2C = (byte) 1;
    private static final byte NXT_MODE_9V_ENABLED = (byte) 2;

    private static final byte OFFSET_I2C_PORT_MODE = (byte) 0;
    private static final byte OFFSET_I2C_PORT_I2C_ADDRESS = (byte) 1;
    private static final byte OFFSET_I2C_PORT_MEMORY_ADDRESS = (byte) 2;
    private static final byte OFFSET_I2C_PORT_MEMORY_LENGTH = (byte) 3;
    private static final byte OFFSET_I2C_PORT_MEMORY_BUFFER = (byte) 4;
    private static final byte OFFSET_I2C_PORT_FLAG = (byte) 31;

    private static final byte SIZE_I2C_BUFFER = (byte) 27;
    private static final byte SIZE_OF_PORT_BUFFER = (byte) 32;

    private static final int START_ADDRESS = 3;
    private static final int ADDRESS_BUFFER_STATUS = 3;
    private static final int MONITOR_LENGTH = 13;
    private static final boolean DEBUG_LOGGING = false;

    private static final int NUMBER_OF_PORTS = 6;

    private static final byte I2C_ACTION_FLAG = (byte) -1;

    private static final int[] PORT_9V_CAPABLE = new int[]{4, 5};

    private final ReadWriteRunnableSegment[] segments;
    private final I2cPortReadyCallback[] callbacks;

    ModernRoboticsUsbLegacyModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, START_ADDRESS, DEBUG_LOGGING));
        this.segments = new ReadWriteRunnableSegment[2*NUMBER_OF_PORTS];
        this.callbacks = new I2cPortReadyCallback[NUMBER_OF_PORTS];
        this.readWriteRunnable.setCallback(this);
        for (int port = 0; port < NUMBER_OF_PORTS; port++) {
            this.segments[port] = this.readWriteRunnable.createSegment(port, getI2cPortAddress(port), SIZE_OF_PORT_BUFFER);
            this.segments[port + NUMBER_OF_PORTS] = this.readWriteRunnable.createSegment(port + NUMBER_OF_PORTS, getI2cPortAddress(port) + OFFSET_I2C_PORT_FLAG, 1);
            enableAnalogReadMode(port);
            this.readWriteRunnable.queueSegmentWrite(port);
        }
    }

    public String getDeviceName() {
        return "Modern Robotics USB Legacy Module";
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public void close() {
        super.close();
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback, int port) {
        this.callbacks[port] = callback;
    }

    public void deregisterForPortReadyCallback(int port) {
        this.callbacks[port] = null;
    }

    public void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        setPortMode(physicalPort, i2cAddress, memAddress, length, NXT_MODE_READ, true);
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        setPortMode(physicalPort, i2cAddress, memAddress, length, NXT_MODE_I2C, true);
    }

    public void enableAnalogReadMode(int physicalPort) {
        validatePort(physicalPort);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_MODE] = NXT_MODE_ANALOG;
            writeI2cCacheToController(physicalPort);
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void enable9v(int physicalPort, boolean enable) {
        if (Arrays.binarySearch(PORT_9V_CAPABLE, physicalPort) < 0) {
            throw new IllegalArgumentException("9v is only available on the following ports: " + Arrays.toString(PORT_9V_CAPABLE));
        }
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte port9vCapableData = getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_MODE];
            if (enable) {
                port9vCapableData = (byte) (port9vCapableData | NXT_MODE_9V_ENABLED);
            } else {
                port9vCapableData = (byte) (port9vCapableData & ~NXT_MODE_9V_ENABLED);
            }
            getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_MODE] = port9vCapableData;
            writeI2cCacheToController(physicalPort);
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void setReadMode(int physicalPort, int i2cAddr, int memAddr, int memLen) {
        setPortMode(physicalPort, i2cAddr, memAddr, memLen, NXT_MODE_READ, false);
    }

    public void setWriteMode(int physicalPort, int i2cAddress, int memAddress) {
        setPortMode(physicalPort, i2cAddress, memAddress, -1, NXT_MODE_I2C, false);
    }

    private void setPortMode(int physicalPort, int i2cAddress, int memAddress, int length, byte portMode, boolean enable) {
        validatePort(physicalPort);
        if(enable) {
            validateLength(length);
        }
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = getI2cWriteCache(physicalPort);
            writeBuffer[OFFSET_I2C_PORT_MODE] = portMode;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            if(portMode == NXT_MODE_READ || enable) {
                writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
            }
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void setData(int physicalPort, byte[] data, int length) {
        validatePort(physicalPort);
        validateLength(length);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = getI2cWriteCache(physicalPort);
            System.arraycopy(data, 0, writeBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, length);
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void setDigitalLine(int physicalPort, int line, boolean set) {
        validatePort(physicalPort);
        validateLine(line);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte digitalLineData = getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_MODE];
            if (set) {
                digitalLineData = (byte) (digitalLineData | getDigitalLine(line));
            } else {
                digitalLineData = (byte) (digitalLineData & (~getDigitalLine(line)));
            }
            getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_MODE] = digitalLineData;
            writeI2cCacheToController(physicalPort);
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public byte[] readAnalog(int physicalPort) {
        validatePort(physicalPort);
        return read(getAnalogPortAddress(physicalPort), 2);
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        validatePort(physicalPort);
        try {
            getI2cReadCacheLock(physicalPort).lock();
            byte[] readBuffer = getI2cReadCache(physicalPort);
            byte[] copyBuffer = new byte[readBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(readBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            getI2cReadCacheLock(physicalPort).unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        validatePort(physicalPort);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            byte[] writeBuffer = getI2cWriteCache(physicalPort);
            byte[] copyBuffer = new byte[writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(writeBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        setData(physicalPort, buffer, buffer.length);
    }

    public void setI2cPortActionFlag(int physicalPort) {
        validatePort(physicalPort);
        try {
            getI2cWriteCacheLock(physicalPort).lock();
            getI2cWriteCache(physicalPort)[OFFSET_I2C_PORT_FLAG] = I2C_ACTION_FLAG;
        } finally {
            getI2cWriteCacheLock(physicalPort).unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int physicalPort) {
        boolean isFlagSet = false;
        validatePort(physicalPort);
        try {
            getI2cReadCacheLock(physicalPort).lock();
            isFlagSet = (getI2cReadCache(physicalPort)[OFFSET_I2C_PORT_FLAG] == I2C_ACTION_FLAG);
        } catch (Throwable ignored) {
        } finally {
            getI2cReadCacheLock(physicalPort).unlock();
        }
        return isFlagSet;
    }

    public void readI2cCacheFromController(int physicalPort) {
        validatePort(physicalPort);
        this.readWriteRunnable.queueSegmentRead(physicalPort);
    }

    public void writeI2cCacheToController(int physicalPort) {
        validatePort(physicalPort);
        this.readWriteRunnable.queueSegmentWrite(physicalPort);
    }

    public void writeI2cPortFlagOnlyToController(int physicalPort) {
        validatePort(physicalPort);
        ReadWriteRunnableSegment readWriteRunnableSegment = this.segments[physicalPort];
        ReadWriteRunnableSegment flagSegment = this.segments[physicalPort + NUMBER_OF_PORTS];
        try {
            readWriteRunnableSegment.getWriteLock().lock();
            flagSegment.getWriteLock().lock();
            flagSegment.getWriteBuffer()[0] = readWriteRunnableSegment.getWriteBuffer()[OFFSET_I2C_PORT_FLAG];
            this.readWriteRunnable.queueSegmentWrite(physicalPort + NUMBER_OF_PORTS);
        } finally {
            readWriteRunnableSegment.getWriteLock().unlock();
            flagSegment.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int physicalPort) {
        return getI2cPortMode(physicalPort) == NXT_MODE_READ;
    }

    public boolean isI2cPortInWriteMode(int physicalPort) {
        return getI2cPortMode(physicalPort) == NXT_MODE_I2C;
    }

    private byte getI2cPortMode(int physicalPort) {
        byte mode = -1;
        validatePort(physicalPort);
        try {
            getI2cReadCacheLock(physicalPort).lock();
            mode = getI2cReadCache(physicalPort)[OFFSET_I2C_PORT_MODE];
        } catch (Throwable ignored) {
        } finally {
            getI2cReadCacheLock(physicalPort).unlock();
        }
        return mode;
    }

    public boolean isI2cPortReady(int physicalPort) {
        return checkPortData(physicalPort, read(ADDRESS_BUFFER_STATUS));
    }

    private void validatePort(int port) {
        if (port < 0 || port >= NUMBER_OF_PORTS) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", port, 0, NUMBER_OF_PORTS-1));
        }
    }

    private void validateLength(int length) {
        if (length < 0 || length > SIZE_I2C_BUFFER) {
            throw new IllegalArgumentException(String.format("buffer length of %d is invalid; max value is %d", new Object[]{length, SIZE_I2C_BUFFER}));
        }
    }

    private void validateLine(int line) {
        if (line < 0 || line > 1) {
            throw new IllegalArgumentException("line is invalid, valid lines are 0 and 1");
        }
    }

    public void readComplete() throws InterruptedException {
        if (this.callbacks != null) {
            byte data = read(ADDRESS_BUFFER_STATUS);
            for (int port = 0; port < NUMBER_OF_PORTS; port++) {
                if (this.callbacks[port] != null && checkPortData(port, data)) {
                    this.callbacks[port].portIsReady(port);
                }
            }
        }
    }

    private boolean checkPortData(int port, byte data) {
        return ((1 << port) & data) == 0;
    }

    public Lock getI2cReadCacheLock(int physicalPort) {
        validatePort(physicalPort);
        return this.segments[physicalPort].getReadLock();
    }

    public Lock getI2cWriteCacheLock(int physicalPort) {
        validatePort(physicalPort);
        return this.segments[physicalPort].getWriteLock();
    }

    public byte[] getI2cReadCache(int physicalPort) {
        validatePort(physicalPort);
        return this.segments[physicalPort].getReadBuffer();
    }

    public byte[] getI2cWriteCache(int physicalPort) {
        validatePort(physicalPort);
        return this.segments[physicalPort].getWriteBuffer();
    }

    private int getAnalogPortAddress(int port) {
        return (2 * port) + OFFSET_I2C_PORT_MEMORY_BUFFER;
    }

    private int getI2cPortAddress(int port) {
        return (SIZE_OF_PORT_BUFFER * port) + 16;
    }

    private int getDigitalLine(int lineNum) {
        return (4 * lineNum) + OFFSET_I2C_PORT_MEMORY_BUFFER;
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
}
