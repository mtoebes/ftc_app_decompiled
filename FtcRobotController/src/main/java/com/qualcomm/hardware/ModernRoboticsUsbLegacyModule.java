package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsUsbLegacyModule extends ModernRoboticsUsbDevice implements LegacyModule {

    public static final int ADDRESS_BUFFER_STATUS = 3;

    public static final boolean DEBUG_LOGGING = false;
    public static final byte I2C_ACTION_FLAG = (byte) -1;
    public static final byte I2C_NO_ACTION_FLAG = (byte) 0;
    public static final byte MAX_PORT_NUMBER = (byte) 5;
    public static final byte MIN_PORT_NUMBER = (byte) 0;
    public static final int MONITOR_LENGTH = 13;
    public static final byte NUMBER_OF_PORTS = (byte) 6;
    public static final byte NXT_MODE_9V_ENABLED = (byte) 2;
    public static final byte NXT_MODE_ANALOG = (byte) 0;
    public static final byte NXT_MODE_DIGITAL_0 = (byte) 4;
    public static final byte NXT_MODE_DIGITAL_1 = (byte) 8;
    public static final byte NXT_MODE_I2C = (byte) 1;
    public static final byte NXT_MODE_READ = Byte.MIN_VALUE;
    public static final byte NXT_MODE_WRITE = (byte) 0;
    public static final byte OFFSET_I2C_PORT_FLAG = (byte) 31;
    public static final byte OFFSET_I2C_PORT_I2C_ADDRESS = (byte) 1;
    public static final byte OFFSET_I2C_PORT_MEMORY_ADDRESS = (byte) 2;
    public static final byte OFFSET_I2C_PORT_MEMORY_BUFFER = (byte) 4;
    public static final byte OFFSET_I2C_PORT_MEMORY_LENGTH = (byte) 3;
    public static final byte OFFSET_I2C_PORT_MODE = (byte) 0;
    public static final int[] PORT_9V_CAPABLE = new int[]{4, 5};
    public static final byte SIZE_ANALOG_BUFFER = (byte) 2;
    public static final byte SIZE_I2C_BUFFER = (byte) 27;
    public static final byte SIZE_OF_PORT_BUFFER = (byte) 32;
    public static final byte START_ADDRESS = (byte) 3;
    private final ReadWriteRunnableSegment[] segments;
    private final I2cPortReadyCallback[] callbacks;

    protected ModernRoboticsUsbLegacyModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, ADDRESS_BUFFER_STATUS, DEBUG_LOGGING));
        this.segments = new ReadWriteRunnableSegment[2*6];
        this.callbacks = new I2cPortReadyCallback[6];
        this.readWriteRunnable.setCallback(this);
        for (int port = 0; port < 6; port++) {
            this.segments[port] = this.readWriteRunnable.createSegment(port, getI2cPortAddress(port), 32);
            this.segments[port + 6] = this.readWriteRunnable.createSegment(port + 6, getI2cPortAddress(port) + 31, 1);
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
        validatePort(physicalPort);
        validateLength(length);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            writeBuffer[0] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            lock.unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        validatePort(physicalPort);
        validateLength(length);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            lock.unlock();
        }
    }

    public void enableAnalogReadMode(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            this.segments[physicalPort].getWriteBuffer()[0] = OFFSET_I2C_PORT_MODE;
            writeI2cCacheToController(physicalPort);
        } finally {
            lock.unlock();
        }
    }

    public void enable9v(int physicalPort, boolean enable) {
        if (Arrays.binarySearch(PORT_9V_CAPABLE, physicalPort) < 0) {
            throw new IllegalArgumentException("9v is only available on the following ports: " + Arrays.toString(PORT_9V_CAPABLE));
        }
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte port9vCapableData = this.segments[physicalPort].getWriteBuffer()[0];
            if (enable) {
                port9vCapableData = (byte) (port9vCapableData | 2);
            } else {
                port9vCapableData = (byte) (port9vCapableData & -3);
            }
            this.segments[physicalPort].getWriteBuffer()[0] = port9vCapableData;
            writeI2cCacheToController(physicalPort);
        } finally {
            lock.unlock();
        }
    }

    public void setReadMode(int physicalPort, int i2cAddr, int memAddr, int memLen) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            writeBuffer[0] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddr;
            writeBuffer[2] = (byte) memAddr;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) memLen;
        } finally {
            lock.unlock();
        }
    }

    public void setWriteMode(int physicalPort, int i2cAddress, int memAddress) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
        } finally {
            lock.unlock();
        }
    }

    public void setData(int physicalPort, byte[] data, int length) {
        validatePort(physicalPort);
        validateLength(length);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            System.arraycopy(data, 0, writeBuffer, 4, length);
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            lock.unlock();
        }
    }

    public void setDigitalLine(int physicalPort, int line, boolean set) {
        validatePort(physicalPort);
        validateLine(line);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte digitalLineData = this.segments[physicalPort].getWriteBuffer()[0];
            if (set) {
                digitalLineData = (byte) (digitalLineData | getDigitalLine(line));
            } else {
                digitalLineData = (byte) (digitalLineData & (~getDigitalLine(line)));
            }
            this.segments[physicalPort].getWriteBuffer()[0] = digitalLineData;
            writeI2cCacheToController(physicalPort);
        } finally {
            lock.unlock();
        }
    }

    public byte[] readAnalog(int physicalPort) {
        validatePort(physicalPort);
        return read(getAnalogPortAddress(physicalPort), 2);
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getReadLock();
        try {
            lock.lock();
            byte[] readBuffer = this.segments[physicalPort].getReadBuffer();
            byte[] copyBuffer = new byte[readBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(readBuffer, 4, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            lock.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            byte[] writeBuffer = this.segments[physicalPort].getWriteBuffer();
            byte[] copyBuffer = new byte[writeBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(writeBuffer, 4, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            lock.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        validatePort(physicalPort);
        validateLength(buffer.length);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            System.arraycopy(buffer, 0, this.segments[physicalPort].getWriteBuffer(), 4, buffer.length);
        } finally {
            lock.unlock();
        }
    }

    public void setI2cPortActionFlag(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.segments[physicalPort].getWriteLock();
        try {
            lock.lock();
            this.segments[physicalPort].getWriteBuffer()[31] = I2C_ACTION_FLAG;
        } finally {
            lock.unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int physicalPort) {
        validatePort(physicalPort);
        try {
            this.segments[physicalPort].getReadLock().lock();
            boolean isFlagSet = this.segments[physicalPort].getReadBuffer()[31] == -1 || DEBUG_LOGGING;
            this.segments[physicalPort].getReadLock().unlock();
            return isFlagSet;
        } catch (Throwable th) {
            this.segments[physicalPort].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
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
        ReadWriteRunnableSegment readWriteRunnableSegment2 = this.segments[physicalPort + 6];
        try {
            readWriteRunnableSegment.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteBuffer()[0] = readWriteRunnableSegment.getWriteBuffer()[31];
            this.readWriteRunnable.queueSegmentWrite(physicalPort + 6);
        } finally {
            readWriteRunnableSegment.getWriteLock().unlock();
            readWriteRunnableSegment2.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int physicalPort) {
        boolean isReadMode = false;
        validatePort(physicalPort);
        try {
            this.segments[physicalPort].getReadLock().lock();
            if (this.segments[physicalPort].getReadBuffer()[0] == -127) {
                isReadMode = true;
            }
            this.segments[physicalPort].getReadLock().unlock();
            return isReadMode;
        } catch (Throwable th) {
            this.segments[physicalPort].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
    }

    public boolean isI2cPortInWriteMode(int physicalPort) {
        boolean isWriteMode = true;
        validatePort(physicalPort);
        try {
            this.segments[physicalPort].getReadLock().lock();
            if (this.segments[physicalPort].getReadBuffer()[0] != OFFSET_I2C_PORT_I2C_ADDRESS) {
                isWriteMode = false;
            }
            this.segments[physicalPort].getReadLock().unlock();
            return isWriteMode;
        } catch (Throwable th) {
            this.segments[physicalPort].getReadLock().unlock();
        }
        return false; //TODO originally no return statement. why?
    }

    public boolean isI2cPortReady(int physicalPort) {
        return checkPortData(physicalPort, read(ADDRESS_BUFFER_STATUS));
    }

    private void validatePort(int port) {
        if (port < 0 || port > 5) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", port, MIN_PORT_NUMBER, MAX_PORT_NUMBER));
        }
    }

    private void validateLength(int length) {
        if (length < 0 || length > 27) {
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
            for (int port = 0; port < 6; port++) {
                if (this.callbacks[port] != null && checkPortData(port, data)) {
                    this.callbacks[port].portIsReady(port);
                }
            }
        }
    }

    private boolean checkPortData(int port, byte data) {
        return ((1 << port) & data) == 0 || DEBUG_LOGGING;
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
        return (2 * port) + 4;
    }

    private int getI2cPortAddress(int port) {
        return (32 * port) + 16;
    }

    private int getDigitalLine(int lineNum) {
        return (4 * lineNum) + 4;
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
