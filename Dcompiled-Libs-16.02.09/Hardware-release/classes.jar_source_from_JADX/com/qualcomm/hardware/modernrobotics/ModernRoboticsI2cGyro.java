package com.qualcomm.hardware.modernrobotics;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.hardware.I2cControllerPortDeviceImpl;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cGyro extends I2cControllerPortDeviceImpl implements GyroSensor, I2cPortReadyCallback {
    public static final int ADDRESS_I2C = 32;
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
    private int f139a;
    private byte[] f140b;
    private Lock f141c;
    private byte[] f142d;
    private Lock f143e;
    private HeadingMode f144f;
    private MeasurementMode f145g;
    private C0017a f146h;
    protected ConcurrentLinkedQueue<GyroI2cTransaction> transactionQueue;

    public class GyroI2cTransaction {
        I2cTransactionState f118a;
        byte[] f119b;
        byte f120c;
        byte f121d;
        boolean f122e;
        final /* synthetic */ ModernRoboticsI2cGyro f123f;

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.f123f = modernRoboticsI2cGyro;
            this.f120c = ModernRoboticsI2cGyro.COMMAND_NORMAL;
            this.f121d = (byte) 18;
            this.f122e = false;
        }

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro, byte data) {
            this.f123f = modernRoboticsI2cGyro;
            this.f120c = (byte) 3;
            this.f119b = new byte[ModernRoboticsI2cGyro.OFFSET_MANUFACTURE_CODE];
            this.f119b[ModernRoboticsI2cGyro.OFFSET_FIRMWARE_REV] = data;
            this.f121d = (byte) this.f119b.length;
            this.f122e = true;
        }

        public boolean isEqual(GyroI2cTransaction transaction) {
            if (this.f120c != transaction.f120c) {
                return false;
            }
            switch (this.f120c) {
                case ModernRoboticsI2cGyro.OFFSET_COMMAND /*3*/:
                case ModernRoboticsI2cGyro.OFFSET_Z_AXIS_SCALE_COEF /*16*/:
                    if (Arrays.equals(this.f119b, transaction.f119b)) {
                        return true;
                    }
                    return false;
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

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro.a */
    private class C0017a {
        byte f127a;
        byte f128b;
        byte f129c;
        byte f130d;
        short f131e;
        short f132f;
        short f133g;
        short f134h;
        short f135i;
        short f136j;
        short f137k;
        final /* synthetic */ ModernRoboticsI2cGyro f138l;

        private C0017a(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.f138l = modernRoboticsI2cGyro;
        }
    }

    public ModernRoboticsI2cGyro(I2cController module, int physicalPort) {
        super(module, physicalPort);
        this.f139a = ADDRESS_I2C;
        this.f144f = HeadingMode.HEADING_CARDINAL;
        this.transactionQueue = new ConcurrentLinkedQueue();
        this.f146h = new C0017a();
        this.f145g = MeasurementMode.GYRO_NORMAL;
        finishConstruction();
    }

    protected void controllerNowArmedOrPretending() {
        this.f140b = this.controller.getI2cReadCache(this.physicalPort);
        this.f141c = this.controller.getI2cReadCacheLock(this.physicalPort);
        this.f142d = this.controller.getI2cWriteCache(this.physicalPort);
        this.f143e = this.controller.getI2cWriteCacheLock(this.physicalPort);
        this.controller.enableI2cReadMode(this.physicalPort, ADDRESS_I2C, OFFSET_FIRMWARE_REV, BUFFER_LENGTH);
        this.controller.setI2cPortActionFlag(this.physicalPort);
        this.controller.writeI2cCacheToController(this.physicalPort);
        this.controller.registerForI2cPortReadyCallback(this, this.physicalPort);
    }

    public String toString() {
        Object[] objArr = new Object[OFFSET_MANUFACTURE_CODE];
        objArr[OFFSET_FIRMWARE_REV] = Double.valueOf(getRotation());
        return String.format("Gyro: %3.1f", objArr);
    }

    public boolean queueTransaction(GyroI2cTransaction transaction, boolean force) {
        if (!force) {
            Iterator it = this.transactionQueue.iterator();
            while (it.hasNext()) {
                if (((GyroI2cTransaction) it.next()).isEqual(transaction)) {
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
        if (this.f145g == MeasurementMode.GYRO_NORMAL) {
            return false;
        }
        return true;
    }

    public HeadingMode getHeadingMode() {
        return this.f144f;
    }

    public void setHeadingMode(HeadingMode headingMode) {
        this.f144f = headingMode;
    }

    public MeasurementMode getMeasurementMode() {
        return this.f145g;
    }

    public int getHeading() {
        if (this.f144f != HeadingMode.HEADING_CARDINAL) {
            return this.f146h.f131e;
        }
        if (this.f146h.f131e == (short) 0) {
            return this.f146h.f131e;
        }
        return Math.abs(this.f146h.f131e - 360);
    }

    public double getRotation() {
        notSupported();
        return 0.0d;
    }

    public int getIntegratedZValue() {
        return this.f146h.f132f;
    }

    public int rawX() {
        return this.f146h.f133g;
    }

    public int rawY() {
        return this.f146h.f134h;
    }

    public int rawZ() {
        return this.f146h.f135i;
    }

    public void resetZAxisIntegrator() {
        queueTransaction(new GyroI2cTransaction(this, COMMAND_RESET_Z_AXIS));
    }

    public String getDeviceName() {
        return "Modern Robotics Gyro";
    }

    public String getConnectionInfo() {
        return this.controller.getConnectionInfo() + "; I2C port: " + this.physicalPort;
    }

    public String status() {
        Object[] objArr = new Object[OFFSET_SENSOR_ID];
        objArr[OFFSET_FIRMWARE_REV] = this.controller.getSerialNumber().toString();
        objArr[OFFSET_MANUFACTURE_CODE] = Integer.valueOf(this.physicalPort);
        return String.format("Modern Robotics Gyro, connected via device %s, port %d", objArr);
    }

    public int getVersion() {
        return OFFSET_MANUFACTURE_CODE;
    }

    public void close() {
    }

    private void m55a() {
        try {
            this.f141c.lock();
            ByteBuffer wrap = ByteBuffer.wrap(this.f140b);
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            this.f146h.f127a = this.f140b[OFFSET_HEADING_DATA];
            this.f146h.f128b = this.f140b[5];
            this.f146h.f129c = this.f140b[OFFSET_INTEGRATED_Z_VAL];
            this.f146h.f130d = this.f140b[7];
            this.f146h.f131e = wrap.getShort(OFFSET_RAW_X_VAL);
            this.f146h.f132f = wrap.getShort(OFFSET_RAW_Y_VAL);
            this.f146h.f133g = wrap.getShort(OFFSET_RAW_Z_VAL);
            this.f146h.f134h = wrap.getShort(OFFSET_Z_AXIS_OFFSET);
            this.f146h.f135i = wrap.getShort(OFFSET_Z_AXIS_SCALE_COEF);
            this.f146h.f136j = wrap.getShort(BUFFER_LENGTH);
            this.f146h.f137k = wrap.getShort(20);
        } finally {
            this.f141c.unlock();
        }
    }

    private void m56b() {
        queueTransaction(new GyroI2cTransaction(this));
    }

    public void portIsReady(int port) {
        if (this.transactionQueue.isEmpty()) {
            m56b();
            return;
        }
        GyroI2cTransaction gyroI2cTransaction = (GyroI2cTransaction) this.transactionQueue.peek();
        if (gyroI2cTransaction.f118a == I2cTransactionState.PENDING_I2C_READ) {
            this.controller.readI2cCacheFromModule(this.physicalPort);
            gyroI2cTransaction.f118a = I2cTransactionState.PENDING_READ_DONE;
            return;
        }
        if (gyroI2cTransaction.f118a == I2cTransactionState.PENDING_I2C_WRITE) {
            gyroI2cTransaction = (GyroI2cTransaction) this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = (GyroI2cTransaction) this.transactionQueue.peek();
            } else {
                return;
            }
        } else if (gyroI2cTransaction.f118a == I2cTransactionState.PENDING_READ_DONE) {
            m55a();
            gyroI2cTransaction = (GyroI2cTransaction) this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = (GyroI2cTransaction) this.transactionQueue.peek();
            } else {
                return;
            }
        }
        try {
            if (gyroI2cTransaction.f122e) {
                if (gyroI2cTransaction.f120c == OFFSET_COMMAND) {
                    this.f146h.f130d = gyroI2cTransaction.f119b[OFFSET_FIRMWARE_REV];
                    this.f145g = MeasurementMode.GYRO_CALIBRATING;
                }
                this.controller.enableI2cWriteMode(port, this.f139a, gyroI2cTransaction.f120c, gyroI2cTransaction.f121d);
                this.controller.copyBufferIntoWriteBuffer(port, gyroI2cTransaction.f119b);
                gyroI2cTransaction.f118a = I2cTransactionState.PENDING_I2C_WRITE;
            } else {
                this.controller.enableI2cReadMode(port, this.f139a, gyroI2cTransaction.f120c, gyroI2cTransaction.f121d);
                gyroI2cTransaction.f118a = I2cTransactionState.PENDING_I2C_READ;
            }
            this.controller.writeI2cCacheToController(port);
        } catch (IllegalArgumentException e) {
            RobotLog.e(e.getMessage());
        }
        if (this.f146h.f130d == null) {
            this.f145g = MeasurementMode.GYRO_NORMAL;
        }
    }

    protected void buginf(String s) {
    }

    protected void notSupported() {
        throw new UnsupportedOperationException("This method is not supported for " + getDeviceName());
    }
}
