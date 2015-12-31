package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
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
    public static final int ADDRESS_BUFFER_STATUS = 3;
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
    public static final boolean DEBUG_LOGGING = false;
    public static final int[] DIGITAL_LINE;
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
    public static final int[] PORT_9V_CAPABLE;
    public static final byte SIZE_ANALOG_BUFFER = (byte) 2;
    public static final byte SIZE_I2C_BUFFER = (byte) 27;
    public static final byte SIZE_OF_PORT_BUFFER = (byte) 32;
    public static final byte START_ADDRESS = (byte) 3;
    private final ReadWriteRunnableSegment[] f187a;
    private final I2cPortReadyCallback[] f188b;

    static {
        ADDRESS_ANALOG_PORT_MAP = new int[]{ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S1, ADDRESS_ANALOG_PORT_S2, ADDRESS_ANALOG_PORT_S3, ADDRESS_ANALOG_PORT_S4, ADDRESS_ANALOG_PORT_S5};
        ADDRESS_I2C_PORT_MAP = new int[]{ADDRESS_I2C_PORT_SO, ADDRESS_I2C_PORT_S1, ADDRESS_I2C_PORT_S2, ADDRESS_I2C_PORT_S3, ADDRESS_I2C_PORT_S4, ADDRESS_I2C_PORT_S5};
        BUFFER_FLAG_MAP = new int[]{1, 2, ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S2, ADDRESS_I2C_PORT_SO, 32};
        DIGITAL_LINE = new int[]{ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S2};
        PORT_9V_CAPABLE = new int[]{ADDRESS_ANALOG_PORT_S0, 5};
    }

    protected ModernRoboticsUsbLegacyModule(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableStandard(serialNumber, device, MONITOR_LENGTH, ADDRESS_BUFFER_STATUS, DEBUG_LOGGING));
        int i = 0;
        this.f187a = new ReadWriteRunnableSegment[ADDRESS_ANALOG_PORT_S4];
        this.f188b = new I2cPortReadyCallback[ADDRESS_ANALOG_PORT_S1];
        this.readWriteRunnable.setCallback(this);
        while (i < ADDRESS_ANALOG_PORT_S1) {
            this.f187a[i] = this.readWriteRunnable.createSegment(i, ADDRESS_I2C_PORT_MAP[i], 32);
            this.f187a[i + ADDRESS_ANALOG_PORT_S1] = this.readWriteRunnable.createSegment(i + ADDRESS_ANALOG_PORT_S1, ADDRESS_I2C_PORT_MAP[i] + 31, 1);
            enableAnalogReadMode(i);
            this.readWriteRunnable.queueSegmentWrite(i);
            i++;
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
        this.f188b[port] = callback;
    }

    public void deregisterForPortReadyCallback(int port) {
        this.f188b[port] = null;
    }

    public void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        m68a(physicalPort);
        m70b(length);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            writeBuffer[0] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        m68a(physicalPort);
        m70b(length);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableAnalogReadMode(int physicalPort) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            this.f187a[physicalPort].getWriteBuffer()[0] = OFFSET_I2C_PORT_MODE;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enable9v(int physicalPort, boolean enable) {
        if (Arrays.binarySearch(PORT_9V_CAPABLE, physicalPort) < 0) {
            throw new IllegalArgumentException("9v is only available on the following ports: " + Arrays.toString(PORT_9V_CAPABLE));
        }
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte b = this.f187a[physicalPort].getWriteBuffer()[0];
            if (enable) {
                b = (byte) (b | 2);
            } else {
                b = (byte) (b & -3);
            }
            this.f187a[physicalPort].getWriteBuffer()[0] = b;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setReadMode(int physicalPort, int i2cAddr, int memAddr, int memLen) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            writeBuffer[0] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddr;
            writeBuffer[2] = (byte) memAddr;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) memLen;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setWriteMode(int physicalPort, int i2cAddress, int memAddress) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setData(int physicalPort, byte[] data, int length) {
        m68a(physicalPort);
        m70b(length);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            System.arraycopy(data, 0, writeBuffer, ADDRESS_ANALOG_PORT_S0, length);
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setDigitalLine(int physicalPort, int line, boolean set) {
        m68a(physicalPort);
        m71c(line);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte b = this.f187a[physicalPort].getWriteBuffer()[0];
            if (set) {
                b = (byte) (b | DIGITAL_LINE[line]);
            } else {
                b = (byte) (b & (~DIGITAL_LINE[line]));
            }
            this.f187a[physicalPort].getWriteBuffer()[0] = b;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public byte[] readAnalog(int physicalPort) {
        m68a(physicalPort);
        return read(ADDRESS_ANALOG_PORT_MAP[physicalPort], 2);
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getReadLock().lock();
            byte[] readBuffer = this.f187a[physicalPort].getReadBuffer();
            byte[] bArr = new byte[readBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(readBuffer, ADDRESS_ANALOG_PORT_S0, bArr, 0, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.f187a[physicalPort].getReadLock();
            bArr.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f187a[physicalPort].getWriteBuffer();
            byte[] bArr = new byte[writeBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(writeBuffer, ADDRESS_ANALOG_PORT_S0, bArr, 0, bArr.length);
            return bArr;
        } finally {
            Lock bArr = this.f187a[physicalPort].getWriteLock();
            bArr.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        m68a(physicalPort);
        m70b(buffer.length);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            System.arraycopy(buffer, 0, this.f187a[physicalPort].getWriteBuffer(), ADDRESS_ANALOG_PORT_S0, buffer.length);
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setI2cPortActionFlag(int physicalPort) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getWriteLock().lock();
            this.f187a[physicalPort].getWriteBuffer()[31] = I2C_ACTION_FLAG;
        } finally {
            this.f187a[physicalPort].getWriteLock().unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int physicalPort) {
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getReadLock().lock();
            boolean z = (this.f187a[physicalPort].getReadBuffer()[31] == -1) || DEBUG_LOGGING;
            this.f187a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f187a[physicalPort].getReadLock().unlock();
        }
        return false; //TODO return statement was missing, need to investigate proper return value
    }

    public void readI2cCacheFromController(int physicalPort) {
        m68a(physicalPort);
        this.readWriteRunnable.queueSegmentRead(physicalPort);
    }

    public void writeI2cCacheToController(int physicalPort) {
        m68a(physicalPort);
        this.readWriteRunnable.queueSegmentWrite(physicalPort);
    }

    public void writeI2cPortFlagOnlyToController(int physicalPort) {
        m68a(physicalPort);
        ReadWriteRunnableSegment readWriteRunnableSegment = this.f187a[physicalPort];
        ReadWriteRunnableSegment readWriteRunnableSegment2 = this.f187a[physicalPort + ADDRESS_ANALOG_PORT_S1];
        try {
            readWriteRunnableSegment.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteLock().lock();
            readWriteRunnableSegment2.getWriteBuffer()[0] = readWriteRunnableSegment.getWriteBuffer()[31];
            this.readWriteRunnable.queueSegmentWrite(physicalPort + ADDRESS_ANALOG_PORT_S1);
        } finally {
            readWriteRunnableSegment.getWriteLock().unlock();
            readWriteRunnableSegment2.getWriteLock().unlock();
        }
    }

    public boolean isI2cPortInReadMode(int physicalPort) {
        boolean z = DEBUG_LOGGING;
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getReadLock().lock();
            if (this.f187a[physicalPort].getReadBuffer()[0] == -127) {
                z = true;
            }
            this.f187a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f187a[physicalPort].getReadLock().unlock();
        }
        return false; //TODO return statement was missing, need to investigate proper return value
    }

    public boolean isI2cPortInWriteMode(int physicalPort) {
        boolean z = true;
        m68a(physicalPort);
        try {
            this.f187a[physicalPort].getReadLock().lock();
            if (this.f187a[physicalPort].getReadBuffer()[0] != OFFSET_I2C_PORT_I2C_ADDRESS) {
                z = DEBUG_LOGGING;
            }
            this.f187a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f187a[physicalPort].getReadLock().unlock();
        }
        return false; //TODO return statement was missing, need to investigate proper return value
    }

    public boolean isI2cPortReady(int physicalPort) {
        return m69a(physicalPort, read(ADDRESS_BUFFER_STATUS));
    }

    private void m68a(int i) {
        if ((i < 0) || (i > 5)) {
            Object[] objArr = new Object[ADDRESS_BUFFER_STATUS];
            objArr[0] = i;
            objArr[1] = OFFSET_I2C_PORT_MODE;
            objArr[2] = MAX_PORT_NUMBER;
            throw new IllegalArgumentException(String.format("port %d is invalid; valid ports are %d..%d", objArr));
        }
    }

    private void m70b(int i) {
        if ((i < 0) || (i > 27)) {
            throw new IllegalArgumentException(String.format("buffer length of %d is invalid; max value is %d", new Object[]{i, SIZE_I2C_BUFFER}));
        }
    }

    private void m71c(int i) {
        if ((i != 0) && (i != 1)) {
            throw new IllegalArgumentException("line is invalid, valid lines are 0 and 1");
        }
    }

    public void readComplete() throws InterruptedException {
        if (this.f188b != null) {
            byte read = read(ADDRESS_BUFFER_STATUS);
            int i = 0;
            while (i < ADDRESS_ANALOG_PORT_S1) {
                if ((this.f188b[i] != null) && m69a(i, read)) {
                    this.f188b[i].portIsReady(i);
                }
                i++;
            }
        }
    }

    private boolean m69a(int i, byte b) {
        return ((BUFFER_FLAG_MAP[i] & b) == 0) || DEBUG_LOGGING;
    }

    public Lock getI2cReadCacheLock(int physicalPort) {
        m68a(physicalPort);
        return this.f187a[physicalPort].getReadLock();
    }

    public Lock getI2cWriteCacheLock(int physicalPort) {
        m68a(physicalPort);
        return this.f187a[physicalPort].getWriteLock();
    }

    public byte[] getI2cReadCache(int physicalPort) {
        m68a(physicalPort);
        return this.f187a[physicalPort].getReadBuffer();
    }

    public byte[] getI2cWriteCache(int physicalPort) {
        m68a(physicalPort);
        return this.f187a[physicalPort].getWriteBuffer();
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
