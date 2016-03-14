package com.qualcomm.hardware.matrix;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtCompassSensor;
import com.qualcomm.hardware.matrix.MatrixI2cTransaction.C0012a;
import com.qualcomm.hardware.matrix.MatrixI2cTransaction.C0013b;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatrixMasterController implements I2cPortReadyCallback {
    private static final byte[] f96a;
    private static final byte[] f97b;
    private static final byte[] f98c;
    private static final byte[] f99d;
    private static final byte[] f100e;
    private volatile boolean f101f;
    private final ElapsedTime f102g;
    protected ModernRoboticsUsbLegacyModule legacyModule;
    protected MatrixDcMotorController motorController;
    protected int physicalPort;
    protected MatrixServoController servoController;
    protected ConcurrentLinkedQueue<MatrixI2cTransaction> transactionQueue;

    /* renamed from: com.qualcomm.hardware.matrix.MatrixMasterController.1 */
    static /* synthetic */ class C00141 {
        static final /* synthetic */ int[] f95a;

        static {
            f95a = new int[C0012a.values().length];
            try {
                f95a[C0012a.PROPERTY_BATTERY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f95a[C0012a.PROPERTY_POSITION.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f95a[C0012a.PROPERTY_TARGET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f95a[C0012a.PROPERTY_MODE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f95a[C0012a.PROPERTY_SERVO.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f95a[C0012a.PROPERTY_TIMEOUT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f95a[C0012a.PROPERTY_START.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f95a[C0012a.PROPERTY_SPEED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f95a[C0012a.PROPERTY_MOTOR_BATCH.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f95a[C0012a.PROPERTY_SERVO_ENABLE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    static {
        f96a = new byte[]{(byte) 0, HiTechnicNxtCompassSensor.CALIBRATION_FAILURE, (byte) 72, (byte) 74, (byte) 76};
        f97b = new byte[]{(byte) 0, (byte) 78, (byte) 88, (byte) 98, (byte) 108};
        f98c = new byte[]{(byte) 0, (byte) 82, (byte) 92, (byte) 102, (byte) 112};
        f99d = new byte[]{(byte) 0, (byte) 86, (byte) 96, (byte) 106, (byte) 116};
        f100e = new byte[]{(byte) 0, (byte) 87, (byte) 97, (byte) 107, (byte) 117};
    }

    public MatrixMasterController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.f101f = false;
        this.f102g = new ElapsedTime(0);
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
        this.transactionQueue = new ConcurrentLinkedQueue();
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public void registerMotorController(MatrixDcMotorController mc) {
        this.motorController = mc;
    }

    public void registerServoController(MatrixServoController sc) {
        this.servoController = sc;
    }

    public int getPort() {
        return this.physicalPort;
    }

    public String getConnectionInfo() {
        return this.legacyModule.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public boolean queueTransaction(MatrixI2cTransaction transaction, boolean force) {
        if (!force) {
            Iterator it = this.transactionQueue.iterator();
            while (it.hasNext()) {
                if (((MatrixI2cTransaction) it.next()).isEqual(transaction)) {
                    buginf("NO Queue transaction " + transaction.toString());
                    return false;
                }
            }
        }
        buginf("YES Queue transaction " + transaction.toString());
        this.transactionQueue.add(transaction);
        return true;
    }

    public boolean queueTransaction(MatrixI2cTransaction transaction) {
        return queueTransaction(transaction, false);
    }

    public void waitOnRead() {
        synchronized (this) {
            this.f101f = true;
            while (this.f101f) {
                try {
                    wait(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void handleReadDone(MatrixI2cTransaction transaction) {
        byte[] i2cReadCache = this.legacyModule.getI2cReadCache(this.physicalPort);
        switch (C00141.f95a[transaction.property.ordinal()]) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                this.motorController.handleReadBattery(i2cReadCache);
                break;
            case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                this.motorController.handleReadPosition(transaction, i2cReadCache);
                break;
            case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                this.motorController.handleReadPosition(transaction, i2cReadCache);
                break;
            case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
                this.motorController.handleReadMode(transaction, i2cReadCache);
                break;
            case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
                this.servoController.handleReadServo(transaction, i2cReadCache);
                break;
            default:
                RobotLog.e("Transaction not a read " + transaction.property);
                break;
        }
        synchronized (this) {
            if (this.f101f) {
                this.f101f = false;
                notify();
            }
        }
    }

    protected void sendHeartbeat() {
        queueTransaction(new MatrixI2cTransaction((byte) 0, C0012a.PROPERTY_TIMEOUT, 3));
    }

    public void portIsReady(int port) {
        int i = 4;
        if (!this.transactionQueue.isEmpty()) {
            MatrixI2cTransaction matrixI2cTransaction = (MatrixI2cTransaction) this.transactionQueue.peek();
            if (matrixI2cTransaction.state == C0013b.PENDING_I2C_READ) {
                this.legacyModule.readI2cCacheFromModule(this.physicalPort);
                matrixI2cTransaction.state = C0013b.PENDING_READ_DONE;
                return;
            }
            byte[] bArr;
            int i2;
            if (matrixI2cTransaction.state == C0013b.PENDING_I2C_WRITE) {
                matrixI2cTransaction = (MatrixI2cTransaction) this.transactionQueue.poll();
                if (!this.transactionQueue.isEmpty()) {
                    matrixI2cTransaction = (MatrixI2cTransaction) this.transactionQueue.peek();
                } else {
                    return;
                }
            } else if (matrixI2cTransaction.state == C0013b.PENDING_READ_DONE) {
                handleReadDone(matrixI2cTransaction);
                matrixI2cTransaction = (MatrixI2cTransaction) this.transactionQueue.poll();
                if (!this.transactionQueue.isEmpty()) {
                    matrixI2cTransaction = (MatrixI2cTransaction) this.transactionQueue.peek();
                } else {
                    return;
                }
            }
            byte b;
            switch (C00141.f95a[matrixI2cTransaction.property.ordinal()]) {
                case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                    bArr = new byte[]{(byte) 0};
                    i2 = 67;
                    i = 1;
                    break;
                case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                    byte b2 = f97b[matrixI2cTransaction.motor];
                    bArr = new byte[]{(byte) 0};
                    b = b2;
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                    i2 = f98c[matrixI2cTransaction.motor];
                    bArr = TypeConversion.intToByteArray(matrixI2cTransaction.value);
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    b = f100e[matrixI2cTransaction.motor];
                    i = 1;
                    break;
                case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
                    bArr = new byte[]{matrixI2cTransaction.speed, (byte) matrixI2cTransaction.target};
                    b = f96a[matrixI2cTransaction.servo];
                    i = 2;
                    break;
                case ModernRoboticsUsbServoController.MAX_SERVOS /*6*/:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i2 = 66;
                    i = 1;
                    break;
                case ModernRoboticsUsbDeviceInterfaceModule.MAX_ANALOG_PORT_NUMBER /*7*/:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i2 = 68;
                    i = 1;
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S2 /*8*/:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    b = f99d[matrixI2cTransaction.motor];
                    i = 1;
                    break;
                case ModernRoboticsUsbServoController.MONITOR_LENGTH /*9*/:
                    byte b3 = f97b[matrixI2cTransaction.motor];
                    ByteBuffer allocate = ByteBuffer.allocate(10);
                    allocate.put(TypeConversion.intToByteArray(0));
                    allocate.put(TypeConversion.intToByteArray(matrixI2cTransaction.target));
                    allocate.put(matrixI2cTransaction.speed);
                    allocate.put(matrixI2cTransaction.mode);
                    bArr = allocate.array();
                    b = b3;
                    i = 10;
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S3 /*10*/:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i2 = 69;
                    i = 1;
                    break;
                default:
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i = 1;
                    i2 = 0;
                    break;
            }
            try {
                if (matrixI2cTransaction.write) {
                    this.legacyModule.setWriteMode(this.physicalPort, 16, i2);
                    this.legacyModule.setData(this.physicalPort, bArr, i);
                    matrixI2cTransaction.state = C0013b.PENDING_I2C_WRITE;
                } else {
                    this.legacyModule.setReadMode(this.physicalPort, 16, i2, i);
                    matrixI2cTransaction.state = C0013b.PENDING_I2C_READ;
                }
                this.legacyModule.setI2cPortActionFlag(this.physicalPort);
                this.legacyModule.writeI2cCacheToModule(this.physicalPort);
            } catch (IllegalArgumentException e) {
                RobotLog.e(e.getMessage());
            }
            buginf(matrixI2cTransaction.toString());
        } else if (this.f102g.time() > 2.0d) {
            sendHeartbeat();
            this.f102g.reset();
        }
    }

    protected void buginf(String s) {
    }
}
