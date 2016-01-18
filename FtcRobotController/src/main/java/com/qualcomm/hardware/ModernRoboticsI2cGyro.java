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
    protected static final byte COMMAND_CALIBRATING = (byte) 78;
    protected static final byte COMMAND_RESET_Z_AXIS = (byte) 82;

    protected static final int OFFSET_COMMAND = 3;
    protected static final int OFFSET_HEADING_DATA = 8;
    protected static final int OFFSET_INTEGRATED_Z_VAL = 10;
    protected static final int OFFSET_RAW_X_VAL = 12;
    protected static final int OFFSET_RAW_Y_VAL = 14;
    protected static final int OFFSET_RAW_Z_VAL = 16;

    private final DeviceInterfaceModule deviceInterfaceModule;
    private final byte[] readCache;
    private final Lock readCacheLock;
    private final int physicalPort;
    private HeadingMode headingMode = HeadingMode.HEADING_CARDINAL;
    private MeasurementMode measurementMode = MeasurementMode.GYRO_NORMAL;
    private GyroData gyroData = new GyroData();
    protected ConcurrentLinkedQueue<GyroI2cTransaction> transactionQueue = new ConcurrentLinkedQueue<GyroI2cTransaction>();

    public class GyroI2cTransaction {
        byte memAddress = 0;
        boolean writeMode = false;
        byte[] data = new byte[1];
        byte dataLength = BUFFER_LENGTH;

        I2cTransactionState state;
        final ModernRoboticsI2cGyro modernRoboticsI2cGyro;

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.modernRoboticsI2cGyro = modernRoboticsI2cGyro;
        }

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro, byte data) {
            this(modernRoboticsI2cGyro);
            this.memAddress = (byte) OFFSET_COMMAND;
            this.data[0] = data;
            this.dataLength = (byte) this.data.length;
            this.writeMode = true;
        }

        public boolean isEqual(GyroI2cTransaction transaction) {
            if (this.memAddress != transaction.memAddress) {
                return false;
            } else {
                switch (this.memAddress) {
                    case OFFSET_COMMAND:
                    case 16: // TODO what is this value from?
                        return Arrays.equals(this.data, transaction.data);
                    default:
                        return false;
                }
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
        byte command;
        short heading;
        short integratedZValue;
        short rawX;
        short rawY;
        short rawZ;

        private GyroData() {
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
        queueTransaction(new GyroI2cTransaction(this, COMMAND_CALIBRATING));
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
                this.physicalPort);
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
            this.gyroData.command = this.readCache[OFFSET_COMMAND];
            this.gyroData.heading = wrap.getShort(OFFSET_HEADING_DATA);
            this.gyroData.integratedZValue = wrap.getShort(OFFSET_INTEGRATED_Z_VAL);
            this.gyroData.rawX = wrap.getShort(OFFSET_RAW_X_VAL);
            this.gyroData.rawY = wrap.getShort(OFFSET_RAW_Y_VAL);
            this.gyroData.rawZ = wrap.getShort(OFFSET_RAW_Z_VAL);
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

        GyroI2cTransaction transaction = this.transactionQueue.peek();
        if (transaction.state == I2cTransactionState.PENDING_I2C_READ) {
            this.deviceInterfaceModule.readI2cCacheFromController(this.physicalPort);
            transaction.state = I2cTransactionState.PENDING_READ_DONE;
        } else {
            if (transaction.state == I2cTransactionState.PENDING_I2C_WRITE ||
                    transaction.state == I2cTransactionState.PENDING_READ_DONE) {
                if (transaction.state == I2cTransactionState.PENDING_READ_DONE) {
                    readCache();
                }

                this.transactionQueue.poll();
                transaction = this.transactionQueue.peek();
            }

            if(transaction == null) {
                return;
            }

            try {
                if (transaction.writeMode) {
                    if (transaction.memAddress == OFFSET_COMMAND) {
                        this.gyroData.command = transaction.data[0];
                        this.measurementMode = MeasurementMode.GYRO_CALIBRATING;
                    }
                    this.deviceInterfaceModule.enableI2cWriteMode(port, ADDRESS_I2C, transaction.memAddress, transaction.dataLength);
                    this.deviceInterfaceModule.copyBufferIntoWriteBuffer(port, transaction.data);
                    transaction.state = I2cTransactionState.PENDING_I2C_WRITE;
                } else { // readMode
                    this.deviceInterfaceModule.enableI2cReadMode(port, ADDRESS_I2C, transaction.memAddress, transaction.dataLength);
                    transaction.state = I2cTransactionState.PENDING_I2C_READ;
                }
                this.deviceInterfaceModule.writeI2cCacheToController(port);
            } catch (IllegalArgumentException e) {
                RobotLog.e(e.getMessage());
            }

            if (this.gyroData.command == COMMAND_NORMAL) {
                this.measurementMode = MeasurementMode.GYRO_NORMAL;
            }
        }
    }

    protected void buginf(String s) {
        //TODO implement this?
    }
}
