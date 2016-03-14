package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.R;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.CreateReadWriteRunnable;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.OpenRobotUsbDevice;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsUsbLegacyModule extends ModernRoboticsUsbI2cController implements LegacyModule {
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
    private final ReadWriteRunnableSegment[] f169a;
    private final I2cPortReadyCallback[] f170b;
    protected final byte[] lastI2cPortModes;

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule.1 */
    class C00221 implements CreateReadWriteRunnable {
        final /* synthetic */ Context f167a;
        final /* synthetic */ SerialNumber f168b;

        C00221(Context context, SerialNumber serialNumber) {
            this.f167a = context;
            this.f168b = serialNumber;
        }

        public ReadWriteRunnable create(RobotUsbDevice device) {
            return new ReadWriteRunnableStandard(this.f167a, this.f168b, device, ModernRoboticsUsbLegacyModule.MONITOR_LENGTH, ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS, ModernRoboticsUsbLegacyModule.DEBUG_LOGGING);
        }
    }

    static {
        ADDRESS_ANALOG_PORT_MAP = new int[]{ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S1, ADDRESS_ANALOG_PORT_S2, ADDRESS_ANALOG_PORT_S3, ADDRESS_ANALOG_PORT_S4, ADDRESS_ANALOG_PORT_S5};
        ADDRESS_I2C_PORT_MAP = new int[]{ADDRESS_I2C_PORT_SO, ADDRESS_I2C_PORT_S1, ADDRESS_I2C_PORT_S2, ADDRESS_I2C_PORT_S3, ADDRESS_I2C_PORT_S4, ADDRESS_I2C_PORT_S5};
        BUFFER_FLAG_MAP = new int[]{1, 2, ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S2, ADDRESS_I2C_PORT_SO, 32};
        DIGITAL_LINE = new int[]{ADDRESS_ANALOG_PORT_S0, ADDRESS_ANALOG_PORT_S2};
        PORT_9V_CAPABLE = new int[]{ADDRESS_ANALOG_PORT_S0, 5};
    }

    public ModernRoboticsUsbLegacyModule(Context context, SerialNumber serialNumber, OpenRobotUsbDevice openRobotUsbDevice, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(ADDRESS_ANALOG_PORT_S1, context, serialNumber, manager, openRobotUsbDevice, new C00221(context, serialNumber));
        this.f169a = new ReadWriteRunnableSegment[ADDRESS_ANALOG_PORT_S4];
        this.f170b = new I2cPortReadyCallback[ADDRESS_ANALOG_PORT_S1];
        this.lastI2cPortModes = new byte[ADDRESS_ANALOG_PORT_S1];
    }

    protected void doArm() throws RobotCoreException, InterruptedException {
        super.doArm();
        createSegments();
    }

    protected void doPretend() throws RobotCoreException, InterruptedException {
        super.doPretend();
        createSegments();
    }

    protected void createSegments() {
        for (int i = 0; i < ADDRESS_ANALOG_PORT_S1; i++) {
            this.f169a[i] = this.readWriteRunnable.createSegment(i, ADDRESS_I2C_PORT_MAP[i], 32);
            this.f169a[i + ADDRESS_ANALOG_PORT_S1] = this.readWriteRunnable.createSegment(i + ADDRESS_ANALOG_PORT_S1, ADDRESS_I2C_PORT_MAP[i] + 31, 1);
        }
    }

    public void initializeHardware() {
        for (int i = 0; i < ADDRESS_ANALOG_PORT_S1; i++) {
            enableAnalogReadMode(i);
            this.readWriteRunnable.queueSegmentWrite(i);
        }
    }

    public String getDeviceName() {
        return this.context.getString(R.string.moduleDisplayNameLegacyModule);
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public void close() {
        super.close();
    }

    public void registerForI2cPortReadyCallback(I2cPortReadyCallback callback, int port) {
        throwIfI2cPortIsInvalid(port);
        this.f170b[port] = callback;
    }

    public I2cPortReadyCallback getI2cPortReadyCallback(int port) {
        throwIfI2cPortIsInvalid(port);
        return this.f170b[port];
    }

    public void deregisterForPortReadyCallback(int port) {
        throwIfI2cPortIsInvalid(port);
        this.f170b[port] = null;
    }

    public void enableI2cReadMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        throwIfI2cPortIsInvalid(physicalPort);
        m68a(length);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            byte[] bArr = this.lastI2cPortModes;
            writeBuffer[0] = (byte) -127;
            bArr[physicalPort] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableI2cWriteMode(int physicalPort, int i2cAddress, int memAddress, int length) {
        throwIfI2cPortIsInvalid(physicalPort);
        m68a(length);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            byte[] bArr = this.lastI2cPortModes;
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            bArr[physicalPort] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enableAnalogReadMode(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            byte[] bArr = this.lastI2cPortModes;
            writeBuffer[0] = OFFSET_I2C_PORT_MODE;
            bArr[physicalPort] = OFFSET_I2C_PORT_MODE;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void enable9v(int physicalPort, boolean enable) {
        if (Arrays.binarySearch(PORT_9V_CAPABLE, physicalPort) < 0) {
            throw new IllegalArgumentException("9v is only available on the following ports: " + Arrays.toString(PORT_9V_CAPABLE));
        }
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte b = this.f169a[physicalPort].getWriteBuffer()[0];
            if (enable) {
                b = (byte) (b | 2);
            } else {
                b = (byte) (b & -3);
            }
            this.f169a[physicalPort].getWriteBuffer()[0] = b;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setReadMode(int physicalPort, int i2cAddr, int memAddr, int memLen) {
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            byte[] bArr = this.lastI2cPortModes;
            writeBuffer[0] = (byte) -127;
            bArr[physicalPort] = (byte) -127;
            writeBuffer[1] = (byte) i2cAddr;
            writeBuffer[2] = (byte) memAddr;
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) memLen;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setWriteMode(int physicalPort, int i2cAddress, int memAddress) {
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte[] writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            byte[] bArr = this.lastI2cPortModes;
            writeBuffer[0] = OFFSET_I2C_PORT_I2C_ADDRESS;
            bArr[physicalPort] = OFFSET_I2C_PORT_I2C_ADDRESS;
            writeBuffer[1] = (byte) i2cAddress;
            writeBuffer[2] = (byte) memAddress;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setData(int physicalPort, byte[] data, int length) {
        throwIfI2cPortIsInvalid(physicalPort);
        m68a(length);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            Object writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            System.arraycopy(data, 0, writeBuffer, ADDRESS_ANALOG_PORT_S0, length);
            writeBuffer[ADDRESS_BUFFER_STATUS] = (byte) length;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setDigitalLine(int physicalPort, int line, boolean set) {
        throwIfI2cPortIsInvalid(physicalPort);
        m70b(line);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            byte b = this.f169a[physicalPort].getWriteBuffer()[0];
            if (set) {
                b = (byte) (b | DIGITAL_LINE[line]);
            } else {
                b = (byte) (b & (DIGITAL_LINE[line] ^ -1));
            }
            this.f169a[physicalPort].getWriteBuffer()[0] = b;
            writeI2cCacheToController(physicalPort);
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public byte[] readAnalog(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        return read(ADDRESS_ANALOG_PORT_MAP[physicalPort], 2);
    }

    public byte[] getCopyOfReadBuffer(int physicalPort) {
        byte[] bArr;
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getReadLock().lock();
            Object readBuffer = this.f169a[physicalPort].getReadBuffer();
            bArr = new byte[readBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(readBuffer, ADDRESS_ANALOG_PORT_S0, bArr, 0, bArr.length);
            return bArr;
        } finally {
            bArr = this.f169a[physicalPort].getReadLock();
            bArr.unlock();
        }
    }

    public byte[] getCopyOfWriteBuffer(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        byte[] bArr;
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            Object writeBuffer = this.f169a[physicalPort].getWriteBuffer();
            bArr = new byte[writeBuffer[ADDRESS_BUFFER_STATUS]];
            System.arraycopy(writeBuffer, ADDRESS_ANALOG_PORT_S0, bArr, 0, bArr.length);
            return bArr;
        } finally {
            bArr = this.f169a[physicalPort].getWriteLock();
            bArr.unlock();
        }
    }

    public void copyBufferIntoWriteBuffer(int physicalPort, byte[] buffer) {
        throwIfI2cPortIsInvalid(physicalPort);
        m68a(buffer.length);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            System.arraycopy(buffer, 0, this.f169a[physicalPort].getWriteBuffer(), ADDRESS_ANALOG_PORT_S0, buffer.length);
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public void setI2cPortActionFlag(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getWriteLock().lock();
            this.f169a[physicalPort].getWriteBuffer()[31] = I2C_ACTION_FLAG;
        } finally {
            this.f169a[physicalPort].getWriteLock().unlock();
        }
    }

    public boolean isI2cPortActionFlagSet(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getReadLock().lock();
            boolean z = this.f169a[physicalPort].getReadBuffer()[31] == -1 ? true : DEBUG_LOGGING;
            this.f169a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f169a[physicalPort].getReadLock().unlock();
        }
    }

    public void readI2cCacheFromController(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        this.readWriteRunnable.queueSegmentRead(physicalPort);
    }

    public void writeI2cCacheToController(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        this.readWriteRunnable.queueSegmentWrite(physicalPort);
    }

    public void writeI2cPortFlagOnlyToController(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        ReadWriteRunnableSegment readWriteRunnableSegment = this.f169a[physicalPort];
        ReadWriteRunnableSegment readWriteRunnableSegment2 = this.f169a[physicalPort + ADDRESS_ANALOG_PORT_S1];
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
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getReadLock().lock();
            if ((isArmed() ? this.f169a[physicalPort].getReadBuffer()[0] : this.lastI2cPortModes[physicalPort]) == -127) {
                z = true;
            }
            this.f169a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f169a[physicalPort].getReadLock().unlock();
        }
    }

    public boolean isI2cPortInWriteMode(int physicalPort) {
        boolean z = true;
        throwIfI2cPortIsInvalid(physicalPort);
        try {
            this.f169a[physicalPort].getReadLock().lock();
            if ((isArmed() ? this.f169a[physicalPort].getReadBuffer()[0] : this.lastI2cPortModes[physicalPort]) != OFFSET_I2C_PORT_I2C_ADDRESS) {
                z = DEBUG_LOGGING;
            }
            this.f169a[physicalPort].getReadLock().unlock();
            return z;
        } catch (Throwable th) {
            this.f169a[physicalPort].getReadLock().unlock();
        }
    }

    public boolean isI2cPortReady(int physicalPort) {
        return m69a(physicalPort, read(ADDRESS_BUFFER_STATUS));
    }

    private void m68a(int i) {
        if (i < 0 || i > 27) {
            throw new IllegalArgumentException(String.format("buffer length of %d is invalid; max value is %d", new Object[]{Integer.valueOf(i), Byte.valueOf(SIZE_I2C_BUFFER)}));
        }
    }

    private void m70b(int i) {
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("line is invalid, valid lines are 0 and 1");
        }
    }

    public void readComplete() throws InterruptedException {
        if (this.f170b != null) {
            byte read = read(ADDRESS_BUFFER_STATUS);
            int i = 0;
            while (i < ADDRESS_ANALOG_PORT_S1) {
                if (this.f170b[i] != null && m69a(i, read)) {
                    this.f170b[i].portIsReady(i);
                }
                i++;
            }
        }
    }

    private boolean m69a(int i, byte b) {
        if (!isArmed() || (BUFFER_FLAG_MAP[i] & b) == 0) {
            return true;
        }
        return DEBUG_LOGGING;
    }

    public Lock getI2cReadCacheLock(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        return this.f169a[physicalPort].getReadLock();
    }

    public Lock getI2cWriteCacheLock(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        return this.f169a[physicalPort].getWriteLock();
    }

    public byte[] getI2cReadCache(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        return this.f169a[physicalPort].getReadBuffer();
    }

    public byte[] getI2cWriteCache(int physicalPort) {
        throwIfI2cPortIsInvalid(physicalPort);
        return this.f169a[physicalPort].getWriteBuffer();
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
