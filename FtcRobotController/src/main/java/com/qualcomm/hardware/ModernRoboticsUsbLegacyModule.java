package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsUsbLegacyModule extends ModernRoboticsUsbDevice implements LegacyModule {
    public static final int[] ADDRESS_ANALOG_PORT_MAP;
    public static final int ADDRESS_ANALOG_PORT_S0 = 4;
    public static final int ADDRESS_ANALOG_PORT_S1 = 6;
    public static final int ADDRESS_ANALOG_PORT_S2 = 8;
    public static final int ADDRESS_ANALOG_PORT_S3 = 10;
    public static final int ADDRESS_ANALOG_PORT_S4 = 12;
    public static final int ADDRESS_ANALOG_PORT_S5 = 14;


    public static final int[] ADDRESS_I2C_PORT_MAP;
    public static final int ADDRESS_I2C_PORT_S1 = 48;
    public static final int ADDRESS_I2C_PORT_S2 = 80;
    public static final int ADDRESS_I2C_PORT_S3 = 112;
    public static final int ADDRESS_I2C_PORT_S4 = 144;
    public static final int ADDRESS_I2C_PORT_S5 = 176;
    public static final int ADDRESS_I2C_PORT_SO = 16;

    public static final int[] BUFFER_FLAG_MAP;
    public static final byte BUFFER_FLAG_S0 = (byte) 1;
    public static final byte BUFFER_FLAG_S1 = (byte) 2;
    public static final byte BUFFER_FLAG_S2 = (byte) 4;
    public static final byte BUFFER_FLAG_S3 = (byte) 8;
    public static final byte BUFFER_FLAG_S4 = (byte) 16;
    public static final byte BUFFER_FLAG_S5 = (byte) 32;

    public static final int[] DIGITAL_LINE;
    public static final byte NXT_MODE_DIGITAL_0 = (byte) 4;
    public static final byte NXT_MODE_DIGITAL_1 = (byte) 8;

    public static final int[] PORT_9V_CAPABLE;

    public static final byte I2C_ACTION_FLAG = (byte) -1;
    public static final byte I2C_NO_ACTION_FLAG = (byte) 0;

    public static final byte MIN_PORT_NUMBER = (byte) 0;
    public static final byte MAX_PORT_NUMBER = (byte) 5;
    public static final byte NUMBER_OF_PORTS = (byte) 6;

    public static final byte NXT_MODE_9V_ENABLED = (byte) 2;
    public static final byte NXT_MODE_ANALOG = (byte) 0;
    public static final byte NXT_MODE_I2C = (byte) 1;
    public static final byte NXT_MODE_READ = -127;
    public static final byte NXT_MODE_WRITE = (byte) 0;

    public static final byte OFFSET_I2C_PORT_FLAG = (byte) 31;

    public static final byte OFFSET_I2C_PORT_MODE = (byte) 0;
    public static final byte OFFSET_I2C_PORT_I2C_ADDRESS = (byte) 1;
    public static final byte OFFSET_I2C_PORT_MEMORY_ADDRESS = (byte) 2;
    public static final byte OFFSET_I2C_PORT_MEMORY_LENGTH = (byte) 3;
    public static final byte OFFSET_I2C_PORT_MEMORY_BUFFER = (byte) 4;

    public static final byte SIZE_ANALOG_BUFFER = (byte) 2;
    public static final byte SIZE_I2C_BUFFER = (byte) 27;
    public static final byte SIZE_OF_PORT_BUFFER = (byte) 32;

    public static final int ADDRESS_BUFFER_STATUS = 3;
    public static final boolean DEBUG_LOGGING = false;
    public static final int MONITOR_LENGTH = 13;
    public static final byte START_ADDRESS = (byte) 3;

    private final ReadWriteRunnableSegment[] readWriteRunnableSegments;
    private final I2cPortReadyCallback[] i2cPortReadyCallbacks;

    static {
        ADDRESS_ANALOG_PORT_MAP = new int[]{ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S1, ADDRESS_ANALOG_PORT_S2, ADDRESS_ANALOG_PORT_S3, ADDRESS_ANALOG_PORT_S4, ADDRESS_ANALOG_PORT_S5};
        ADDRESS_I2C_PORT_MAP = new int[]{ADDRESS_I2C_PORT_SO, ADDRESS_I2C_PORT_S1, ADDRESS_I2C_PORT_S2, ADDRESS_I2C_PORT_S3, ADDRESS_I2C_PORT_S4, ADDRESS_I2C_PORT_S5};
        BUFFER_FLAG_MAP = new int[]{BUFFER_FLAG_S0, BUFFER_FLAG_S1, BUFFER_FLAG_S2, BUFFER_FLAG_S3, BUFFER_FLAG_S4, BUFFER_FLAG_S5};
        DIGITAL_LINE = new int[]{NXT_MODE_DIGITAL_0, NXT_MODE_DIGITAL_1};
        PORT_9V_CAPABLE = new int[]{4, 5};
    }

    protected ModernRoboticsUsbLegacyModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, ADDRESS_BUFFER_STATUS, DEBUG_LOGGING));
        this.readWriteRunnableSegments = new ReadWriteRunnableSegment[ADDRESS_ANALOG_PORT_S4];
        this.i2cPortReadyCallbacks = new I2cPortReadyCallback[ADDRESS_ANALOG_PORT_S1];
        this.readWriteRunnable.setCallback(this);
        for (int i =0; i < NUMBER_OF_PORTS; i++) {
            this.readWriteRunnableSegments[i] = this.readWriteRunnable.createSegment(i, ADDRESS_I2C_PORT_MAP[i], SIZE_OF_PORT_BUFFER);
            this.readWriteRunnableSegments[i + NUMBER_OF_PORTS] =
                    this.readWriteRunnable.createSegment(i + NUMBER_OF_PORTS, ADDRESS_I2C_PORT_MAP[i] + OFFSET_I2C_PORT_FLAG, 1);
            enableAnalogReadMode(i);
            this.readWriteRunnable.queueSegmentWrite(i);
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
        this.i2cPortReadyCallbacks[port] = callback;
    }

    public void deregisterForPortReadyCallback(int port) {
        this.i2cPortReadyCallbacks[port] = null;
    }

    public void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        validatePort(physicalPort);
        validateLength(length);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = NXT_MODE_READ;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        validatePort(physicalPort);
        validateLength(length);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = NXT_MODE_I2C;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableAnalogReadMode(int physicalPort) {
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[0] = NXT_MODE_ANALOG;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void enable9v(int physicalPort, boolean enable) {
        if (Arrays.binarySearch(PORT_9V_CAPABLE, physicalPort) < 0) {
            throw new IllegalArgumentException("9v is only available on the following ports: " + Arrays.toString(PORT_9V_CAPABLE));
        }
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte b = this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[0];
            if (enable) {
                b = (byte) (b | NXT_MODE_9V_ENABLED);
            } else {
                b = (byte) (b & ~NXT_MODE_9V_ENABLED);
            }
            this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[0] = b;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = NXT_MODE_READ;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setWriteMode(int physicalPort, int i2cAddress, int memAddress) {
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            writeBuffer[OFFSET_I2C_PORT_MODE] = 1;
            writeBuffer[OFFSET_I2C_PORT_I2C_ADDRESS] = (byte) i2cAddress;
            writeBuffer[OFFSET_I2C_PORT_MEMORY_ADDRESS] = (byte) memAddress;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setData(int physicalPort, byte[] data, int length) {
        validatePort(physicalPort);
        validateLength(length);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            System.arraycopy(data, 0, writeBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, length);
            writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH] = (byte) length;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setDigitalLine(int physicalPort, int line, boolean set) {
        validatePort(physicalPort);
        validateLine(line);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            byte data = this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[0];
            if (set) {
                data = (byte) (data | DIGITAL_LINE[line]);
            } else {
                data = (byte) (data & (~DIGITAL_LINE[line]));
            }
            this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[0] = data;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public byte[] readAnalog(int physicalPort) {
        validatePort(physicalPort);
        return read(ADDRESS_ANALOG_PORT_MAP[physicalPort], SIZE_ANALOG_BUFFER);
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.readWriteRunnableSegments[physicalPort].getReadLock();
        try {
            lock.lock();
            byte[] readBuffer = this.readWriteRunnableSegments[physicalPort].getReadBuffer();
            byte[] copyBuffer = new byte[readBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(readBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            lock.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        validatePort(physicalPort);
        Lock lock = this.readWriteRunnableSegments[physicalPort].getWriteLock();

        try {
            lock.lock();
            byte[] writeBuffer = this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
            byte[] copyBuffer = new byte[writeBuffer[OFFSET_I2C_PORT_MEMORY_LENGTH]];
            System.arraycopy(writeBuffer, OFFSET_I2C_PORT_MEMORY_BUFFER, copyBuffer, 0, copyBuffer.length);
            return copyBuffer;
        } finally {
            lock.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        validatePort(physicalPort);
        validateLength(buffer.length);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            System.arraycopy(buffer, 0, this.readWriteRunnableSegments[physicalPort].getWriteBuffer(), OFFSET_I2C_PORT_MEMORY_BUFFER, buffer.length);
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public void setI2cPortActionFlag(int physicalPort) {
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().lock();
            this.readWriteRunnableSegments[physicalPort].getWriteBuffer()[OFFSET_I2C_PORT_FLAG] = I2C_ACTION_FLAG;
        } finally {
            this.readWriteRunnableSegments[physicalPort].getWriteLock().unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int physicalPort) {
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getReadLock().lock();
            boolean isSet = this.readWriteRunnableSegments[physicalPort].getReadBuffer()[OFFSET_I2C_PORT_FLAG] == I2C_ACTION_FLAG;
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return isSet;
        } catch (Throwable th) {
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return false;
        }
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
        ReadWriteRunnableSegment readWriteSegment = this.readWriteRunnableSegments[physicalPort];
        ReadWriteRunnableSegment portFlagSegment = this.readWriteRunnableSegments[physicalPort + NUMBER_OF_PORTS];
        try {
            readWriteSegment.getWriteLock().lock();
            portFlagSegment.getWriteLock().lock();
            portFlagSegment.getWriteBuffer()[0] = readWriteSegment.getWriteBuffer()[OFFSET_I2C_PORT_FLAG];
            this.readWriteRunnable.queueSegmentWrite(physicalPort + NUMBER_OF_PORTS);
        } finally {
            readWriteSegment.getWriteLock().unlock();
            portFlagSegment.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int physicalPort) {
        boolean isReadMode = false;
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getReadLock().lock();
            if (this.readWriteRunnableSegments[physicalPort].getReadBuffer()[OFFSET_I2C_PORT_MODE] == NXT_MODE_READ) {
                isReadMode = true;
            }
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return isReadMode;
        } catch (Throwable th) {
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return false;
        }
    }

    public boolean isI2cPortInWriteMode(int physicalPort) {
        boolean isWriteMode = true;
        validatePort(physicalPort);
        try {
            this.readWriteRunnableSegments[physicalPort].getReadLock().lock();
            if (this.readWriteRunnableSegments[physicalPort].getReadBuffer()[0] != NXT_MODE_I2C) {
                isWriteMode = false;
            }
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return isWriteMode;
        } catch (Throwable th) {
            this.readWriteRunnableSegments[physicalPort].getReadLock().unlock();
            return false;
        }
    }

    public boolean isI2cPortReady(int physicalPort) {
        return isIndexValue(physicalPort, read(ADDRESS_BUFFER_STATUS));
    }

    private void validatePort(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are [%d, %d]", port, MIN_PORT_NUMBER, MAX_PORT_NUMBER));
        }
    }

    private void validateLength(int length) {
        if (length < 0 || length > SIZE_I2C_BUFFER) {
            throw new IllegalArgumentException(String.format("buffer length of %d is invalid; max value is %d", length, SIZE_I2C_BUFFER));
        }
    }

    private void validateLine(int line) {
        if (line != 0 && line != 1) {
            throw new IllegalArgumentException("line is invalid, valid lines are 0 and 1");
        }
    }

    public void readComplete() throws InterruptedException {
        if (this.i2cPortReadyCallbacks != null) {
            byte read = read(ADDRESS_BUFFER_STATUS);
            for (int i = 0; i< NUMBER_OF_PORTS; i++) {
                if (this.i2cPortReadyCallbacks[i] != null && isIndexValue(i, read)) {
                    this.i2cPortReadyCallbacks[i].portIsReady(i);
                }
            }
        }
    }

    private boolean isIndexValue(int index, byte value) {
        return (BUFFER_FLAG_MAP[index] & value) == 0;
    }

    public Lock getI2cReadCacheLock(int physicalPort) {
        validatePort(physicalPort);
        return this.readWriteRunnableSegments[physicalPort].getReadLock();
    }

    public Lock getI2cWriteCacheLock(int physicalPort) {
        validatePort(physicalPort);
        return this.readWriteRunnableSegments[physicalPort].getWriteLock();
    }

    public byte[] getI2cReadCache(int physicalPort) {
        validatePort(physicalPort);
        return this.readWriteRunnableSegments[physicalPort].getReadBuffer();
    }

    public byte[] getI2cWriteCache(int physicalPort) {
        validatePort(physicalPort);
        return this.readWriteRunnableSegments[physicalPort].getWriteBuffer();
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
