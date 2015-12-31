package com.qualcomm.hardware;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.RobotLog;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;

public class ModernRoboticsI2cGyro extends GyroSensor implements HardwareDevice, I2cPortReadyCallback {
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
    private int f156a;
    private final DeviceInterfaceModule f157b;
    private final byte[] f158c;
    private final Lock f159d;
    private final int f162g;
    private HeadingMode f163h;
    private MeasurementMode f164i;
    private C0013a f165j;
    protected ConcurrentLinkedQueue<GyroI2cTransaction> transactionQueue;

    public class GyroI2cTransaction {
        I2cTransactionState f135a;
        byte[] f136b;
        byte f137c;
        byte f138d;
        boolean f139e;
        final /* synthetic */ ModernRoboticsI2cGyro f140f;

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.f140f = modernRoboticsI2cGyro;
            this.f137c = ModernRoboticsI2cGyro.COMMAND_NORMAL;
            this.f138d = (byte) 18;
            this.f139e = false;
        }

        public GyroI2cTransaction(ModernRoboticsI2cGyro modernRoboticsI2cGyro, byte data) {
            this.f140f = modernRoboticsI2cGyro;
            this.f137c = (byte) 3;
            this.f136b = new byte[ModernRoboticsI2cGyro.OFFSET_MANUFACTURE_CODE];
            this.f136b[ModernRoboticsI2cGyro.OFFSET_FIRMWARE_REV] = data;
            this.f138d = (byte) this.f136b.length;
            this.f139e = true;
        }

        public boolean isEqual(GyroI2cTransaction transaction) {
            if (this.f137c != transaction.f137c) {
                return false;
            }
            switch (this.f137c) {
                case ModernRoboticsI2cGyro.OFFSET_COMMAND /*3*/:
                case ModernRoboticsI2cGyro.OFFSET_Z_AXIS_SCALE_COEF /*16*/:
                    return Arrays.equals(this.f136b, transaction.f136b);
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

    /* renamed from: com.qualcomm.hardware.ModernRoboticsI2cGyro.a */
    private class C0013a {
        byte f144a;
        byte f145b;
        byte f146c;
        byte f147d;
        short f148e;
        short f149f;
        short f150g;
        short f151h;
        short f152i;
        short f153j;
        short f154k;
        final /* synthetic */ ModernRoboticsI2cGyro f155l;

        private C0013a(ModernRoboticsI2cGyro modernRoboticsI2cGyro) {
            this.f155l = modernRoboticsI2cGyro;
        }
    }

    public ModernRoboticsI2cGyro(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        this.f156a = ADDRESS_I2C;
        this.f157b = deviceInterfaceModule;
        this.f162g = physicalPort;
        this.f158c = deviceInterfaceModule.getI2cReadCache(physicalPort);
        this.f159d = deviceInterfaceModule.getI2cReadCacheLock(physicalPort);
        byte[] f160e = deviceInterfaceModule.getI2cWriteCache(physicalPort);
        Lock f161f = deviceInterfaceModule.getI2cWriteCacheLock(physicalPort);
        this.f163h = HeadingMode.HEADING_CARDINAL;
        deviceInterfaceModule.enableI2cReadMode(physicalPort, ADDRESS_I2C, OFFSET_FIRMWARE_REV, BUFFER_LENGTH);
        deviceInterfaceModule.setI2cPortActionFlag(physicalPort);
        deviceInterfaceModule.writeI2cCacheToController(physicalPort);
        deviceInterfaceModule.registerForI2cPortReadyCallback(this, physicalPort);
        this.transactionQueue = new ConcurrentLinkedQueue<GyroI2cTransaction>();
        this.f165j = new C0013a(this);
        this.f164i = MeasurementMode.GYRO_NORMAL;
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
        return this.f164i != MeasurementMode.GYRO_NORMAL;
    }

    public HeadingMode getHeadingMode() {
        return this.f163h;
    }

    public void setHeadingMode(HeadingMode headingMode) {
        this.f163h = headingMode;
    }

    public MeasurementMode getMeasurementMode() {
        return this.f164i;
    }

    public int getHeading() {
        if (this.f163h != HeadingMode.HEADING_CARDINAL) {
            return this.f165j.f148e;
        }
        if (this.f165j.f148e == (short) 0) {
            return this.f165j.f148e;
        }
        return Math.abs(this.f165j.f148e - 360);
    }

    public double getRotation() {
        notSupported();
        return 0.0d;
    }

    public int getIntegratedZValue() {
        return this.f165j.f149f;
    }

    public int rawX() {
        return this.f165j.f150g;
    }

    public int rawY() {
        return this.f165j.f151h;
    }

    public int rawZ() {
        return this.f165j.f152i;
    }

    public void resetZAxisIntegrator() {
        queueTransaction(new GyroI2cTransaction(this, COMMAND_RESET_Z_AXIS));
    }

    public String getDeviceName() {
        return "Modern Robotics Gyro";
    }

    public String getConnectionInfo() {
        return this.f157b.getConnectionInfo() + "; I2C port: " + this.f162g;
    }

    public String status() {
        Object[] objArr = new Object[OFFSET_SENSOR_ID];
        objArr[OFFSET_FIRMWARE_REV] = this.f157b.getSerialNumber().toString();
        objArr[OFFSET_MANUFACTURE_CODE] = this.f162g;
        return String.format("Modern Robotics Gyro, connected via device %s, port %d", objArr);
    }

    public int getVersion() {
        return OFFSET_MANUFACTURE_CODE;
    }

    public void close() {
    }

    private void m52a() {
        try {
            this.f159d.lock();
            ByteBuffer wrap = ByteBuffer.wrap(this.f158c);
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            this.f165j.f144a = this.f158c[OFFSET_HEADING_DATA];
            this.f165j.f145b = this.f158c[5];
            this.f165j.f146c = this.f158c[OFFSET_INTEGRATED_Z_VAL];
            this.f165j.f147d = this.f158c[7];
            this.f165j.f148e = wrap.getShort(OFFSET_RAW_X_VAL);
            this.f165j.f149f = wrap.getShort(OFFSET_RAW_Y_VAL);
            this.f165j.f150g = wrap.getShort(OFFSET_RAW_Z_VAL);
            this.f165j.f151h = wrap.getShort(OFFSET_Z_AXIS_OFFSET);
            this.f165j.f152i = wrap.getShort(OFFSET_Z_AXIS_SCALE_COEF);
            this.f165j.f153j = wrap.getShort(BUFFER_LENGTH);
            this.f165j.f154k = wrap.getShort(20);
        } finally {
            this.f159d.unlock();
        }
    }

    private void m53b() {
        queueTransaction(new GyroI2cTransaction(this));
    }

    public void portIsReady(int port) {
        if (this.transactionQueue.isEmpty()) {
            m53b();
            return;
        }
        GyroI2cTransaction gyroI2cTransaction = this.transactionQueue.peek();
        if (gyroI2cTransaction.f135a == I2cTransactionState.PENDING_I2C_READ) {
            this.f157b.readI2cCacheFromController(this.f162g);
            gyroI2cTransaction.f135a = I2cTransactionState.PENDING_READ_DONE;
            return;
        }
        if (gyroI2cTransaction.f135a == I2cTransactionState.PENDING_I2C_WRITE) {
            gyroI2cTransaction = this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = this.transactionQueue.peek();
            } else {
                return;
            }
        } else if (gyroI2cTransaction.f135a == I2cTransactionState.PENDING_READ_DONE) {
            m52a();
            gyroI2cTransaction = this.transactionQueue.poll();
            if (!this.transactionQueue.isEmpty()) {
                gyroI2cTransaction = this.transactionQueue.peek();
            } else {
                return;
            }
        }
        try {
            if (gyroI2cTransaction.f139e) {
                if (gyroI2cTransaction.f137c == OFFSET_COMMAND) {
                    this.f165j.f147d = gyroI2cTransaction.f136b[OFFSET_FIRMWARE_REV];
                    this.f164i = MeasurementMode.GYRO_CALIBRATING;
                }
                this.f157b.enableI2cWriteMode(port, this.f156a, gyroI2cTransaction.f137c, gyroI2cTransaction.f138d);
                this.f157b.copyBufferIntoWriteBuffer(port, gyroI2cTransaction.f136b);
                gyroI2cTransaction.f135a = I2cTransactionState.PENDING_I2C_WRITE;
            } else {
                this.f157b.enableI2cReadMode(port, this.f156a, gyroI2cTransaction.f137c, gyroI2cTransaction.f138d);
                gyroI2cTransaction.f135a = I2cTransactionState.PENDING_I2C_READ;
            }
            this.f157b.writeI2cCacheToController(port);
        } catch (IllegalArgumentException e) {
            RobotLog.e(e.getMessage());
        }
        if (this.f165j.f147d == 0) { //TODO was comparing to null, investigate what byte value to compare to
            this.f164i = MeasurementMode.GYRO_NORMAL;
        }
    }

    protected void buginf(String s) {
    }
}
