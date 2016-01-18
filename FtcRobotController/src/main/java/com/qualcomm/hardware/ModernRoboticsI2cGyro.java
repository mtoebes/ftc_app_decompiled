package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cGyro extends GyroSensor implements HardwareDevice, I2cPortReadyCallback {
    public static final int ADDRESS_I2C = 32;
    protected static final int VERSION = 1;
    protected static final int BUFFER_LENGTH = 18;

    protected static final byte COMMAND_NORMAL = (byte) 0;
    protected static final byte COMMAND_NULL = (byte) 78;
    protected static final byte COMMAND_RESET_Z_AXIS = (byte) 82;
    protected static final byte COMMAND_WRITE_EEPROM = (byte) 87;
    protected static final int OFFSET_COMMAND = 3;

    protected static final int OFFSET_FIRMWARE_REV = 0;
    protected static final int OFFSET_HEADING_DATA = 4;
    protected static final int OFFSET_INTEGRATED_Z_VAL = 6;

    protected static final int OFFSET_MANUFACTURE_CODE = 1;

    protected static final int OFFSET_NEW_I2C_ADDRESS = 112;

    protected static final int OFFSET_RAW_X_VAL = 8;
    protected static final int OFFSET_RAW_Y_VAL = 10;
    protected static final int OFFSET_RAW_Z_VAL = 12;

    protected static final int OFFSET_SENSOR_ID = 2;
    protected static final int OFFSET_TRIGGER_1 = 113;
    protected static final int OFFSET_TRIGGER_2 = 114;

    protected static final int OFFSET_Z_AXIS_OFFSET = 14;
    protected static final int OFFSET_Z_AXIS_SCALE_COEF = 16;

    protected static final int TRIGGER_1_VAL = 85;
    protected static final int TRIGGER_2_VAL = 170;

    private final DeviceInterfaceModule deviceInterfaceModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final int physicalPort;
    private HeadingMode headingMode = HeadingMode.HEADING_CARDINAL;
    private MeasurementMode measurementMode = MeasurementMode.GYRO_NORMAL;
    private GyroData gyroData;
    protected ConcurrentLinkedQueue<GyroI2cTransaction> transactionQueue = new ConcurrentLinkedQueue<GyroI2cTransaction>();

    public class GyroI2cTransaction {
        I2cTransactionState i2cTransactionState;
        byte[] data;
        byte memAddress;
        byte dataLength;
        boolean hasData;
        final ModernRoboticsI2cGyro modernRoboticsI2cGyro;

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.modernRoboticsI2cGyro = modernRoboticsI2cGyro;
            this.memAddress = 0;
            this.dataLength = (byte) 18;
            this.hasData = false;
        }

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro, byte data) {
            this.modernRoboticsI2cGyro = modernRoboticsI2cGyro;
            this.memAddress = (byte) OFFSET_COMMAND;
            this.data = new byte[1];
            this.data[0] = data;
            this.dataLength = (byte) this.data.length;
            this.hasData = true;
        }

        public boolean isEqual(GyroI2cTransaction transaction) {
            if (this.memAddress != transaction.memAddress) {
                return false;
            }
            switch (this.memAddress) {
                case OFFSET_COMMAND :
                case OFFSET_Z_AXIS_SCALE_COEF :
                    return Arrays.equals(this.data, transaction.data);
                default:
                    return false;
            }
        }
    }

    public enum HeadingMode {
        HEADING_CARTESIAN,
        HEADING_CARDINAL
    }

    protected enum I2cTransactionState {
        QUEUED,
        PENDING_I2C_READ,
        PENDING_I2C_WRITE,
        PENDING_READ_DONE,
        DONE
    }

    public enum MeasurementMode {
        GYRO_CALIBRATING,
        GYRO_NORMAL
    }

    private class GyroData {
        byte measurement;
        short heading;
        short integratedZValue;
        short rawX;
        short rawY;
        short rawZ;
        final ModernRoboticsI2cGyro modernRoboticsI2cGyro;

        private GyroData(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.modernRoboticsI2cGyro = modernRoboticsI2cGyro;
        }
    }

    public ModernRoboticsI2cGyro(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.deviceInterfaceModule = deviceInterfaceModule;
        this.physicalPort = physicalPort;
        this.readCache = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.readCacheLock = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        deviceInterfaceModule.enableI2cReadMode(physicalPort, ADDRESS_I2C, 0, BUFFER_LENGTH);
        deviceInterfaceModule.setI2cPortActionFlag(physicalPort);
        deviceInterfaceModule.writeI2cCacheToController(physicalPort);
        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
        this.gyroData = new GyroData(this);
    }

    public boolean queueTransaction(GyroI2cTransaction transaction, boolean force) {
        if (!force) {
            for (GyroI2cTransaction aTransactionQueue : this.transactionQueue) {
                if ((aTransactionQueue).isEqual(transaction)) {
                    buginf("NO Queue transaction " + transaction.toString());
                    return false;
                }
            }
        }
        buginf("YES Queue transaction " + transaction.toString());
        this.transactionQueue.add(transaction);
        return true;
    }

    public boolean queueTransaction(GyroI2cTransaction transaction) {
        return queueTransaction(transaction, false);
    }

    public void calibrate() {
        queueTransaction(new GyroI2cTransaction(this, COMMAND_NULL));
    }

    public boolean isCalibrating() {
        return this.measurementMode != MeasurementMode.GYRO_NORMAL;
    }

    public HeadingMode getHeadingMode() {
        return this.headingMode;
    }

    public void setHeadingMode(HeadingMode headingMode) {
        this.headingMode = headingMode;
    }

    public MeasurementMode getMeasurementMode() {
        return this.measurementMode;
    }

    public int getHeading() {
        if (this.headingMode != HeadingMode.HEADING_CARDINAL) {
            return this.gyroData.heading;
        }
        if (this.gyroData.heading == (short) 0) {
            return this.gyroData.heading;
        }
        return Math.abs(this.gyroData.heading - 360);
    }

    public double getRotation() {
        notSupported();
        return 0.0d;
    }

    public int getIntegratedZValue() {
        return this.gyroData.integratedZValue;
    }

    public int rawX() {
        return this.gyroData.rawX;
    }

    public int rawY() {
        return this.gyroData.rawY;
    }

    public int rawZ() {
        return this.gyroData.rawZ;
    }

    public void resetZAxisIntegrator() {
        queueTransaction(new GyroI2cTransaction(this, COMMAND_RESET_Z_AXIS));
    }

    public String getDeviceName() {
        return "Modern Robotics Gyro";
    }

    public String getConnectionInfo() {
        return this.deviceInterfaceModule.getConnectionInfo() + "; I2C port: " + this.physicalPort;
    }

    public String status() {
        return String.format("Modern Robotics Gyro, connected via device %s, port %d",
                this.deviceInterfaceModule.getSerialNumber().toString(),
                this.physicalPort) ;
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
    }

    private void readCache() {
        try {
            this.readCacheLock.lock();
            ByteBuffer wrap = ByteBuffer.wrap(this.readCache);
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            this.gyroData.measurement = this.readCache[7];
            this.gyroData.heading = wrap.getShort(8);
            this.gyroData.integratedZValue = wrap.getShort(10);
            this.gyroData.rawX = wrap.getShort(12);
            this.gyroData.rawY = wrap.getShort(14);
            this.gyroData.rawZ = wrap.getShort(16);
        } finally {
            this.readCacheLock.unlock();
        }
    }

    private void createTransactionQueue() {
        queueTransaction(new GyroI2cTransaction(this));
    }

    public void portIsReady(int port) {
        if (this.transactionQueue.isEmpty()) {
            createTransactionQueue();
            return;
        }
        GyroI2cTransaction gyroI2cTransaction = this.transactionQueue.peek();
        if (gyroI2cTransaction.i2cTransactionState == I2cTransactionState.PENDING_I2C_READ) {
            this.deviceInterfaceModule.readI2cCacheFromController(this.physicalPort);
            gyroI2cTransaction.i2cTransactionState = I2cTransactionState.PENDING_READ_DONE;
            return;
        }
        if (gyroI2cTransaction.i2cTransactionState == I2cTransactionState.PENDING_I2C_WRITE) {
            this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = this.transactionQueue.peek();
            } else {
                return;
            }
        } else if (gyroI2cTransaction.i2cTransactionState == I2cTransactionState.PENDING_READ_DONE) {
            readCache();
            this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = this.transactionQueue.peek();
            } else {
                return;
            }
        }
        try {
            if (gyroI2cTransaction.hasData) {
                if (gyroI2cTransaction.memAddress == OFFSET_COMMAND) {
                    this.gyroData.measurement = gyroI2cTransaction.data[0];
                    this.measurementMode = MeasurementMode.GYRO_CALIBRATING;
                }
                this.deviceInterfaceModule.enableI2cWriteMode(port, ADDRESS_I2C, gyroI2cTransaction.memAddress, gyroI2cTransaction.dataLength);
                this.deviceInterfaceModule.copyBufferIntoWriteBuffer(port, gyroI2cTransaction.data);
                gyroI2cTransaction.i2cTransactionState = I2cTransactionState.PENDING_I2C_WRITE;
            } else {
                this.deviceInterfaceModule.enableI2cReadMode(port, ADDRESS_I2C, gyroI2cTransaction.memAddress, gyroI2cTransaction.dataLength);
                gyroI2cTransaction.i2cTransactionState = I2cTransactionState.PENDING_I2C_READ;
            }
            this.deviceInterfaceModule.writeI2cCacheToController(port);
        } catch (IllegalArgumentException e) {
            RobotLog.e(e.getMessage());
        }
        if (this.gyroData.measurement == 0) {
            this.measurementMode = MeasurementMode.GYRO_NORMAL;
        }
    }

    protected void buginf(String s) {
    }
}
