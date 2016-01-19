package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cProperties;
import com.qualcomm.hardware.MatrixI2cTransaction.C0009b;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatrixMasterController implements I2cPortReadyCallback {
    private static final byte[] f111a = new byte[]{(byte) 0, (byte) 70, (byte) 72, (byte) 74, (byte) 76};
    private static final byte[] f112b = new byte[]{(byte) 0, (byte) 78, (byte) 88, (byte) 98, (byte) 108};
    private static final byte[] f113c = new byte[]{(byte) 0, (byte) 82, (byte) 92, (byte) 102, (byte) 112};
    private static final byte[] f114d = new byte[]{(byte) 0, (byte) 86, (byte) 96, (byte) 106, (byte) 116};
    private static final byte[] f115e = new byte[]{(byte) 0, (byte) 87, (byte) 97, (byte) 107, (byte) 117};
    private volatile boolean isReading = false;
    private final ElapsedTime elapsedTime =  new ElapsedTime(0);
    protected ModernRoboticsUsbLegacyModule legacyModule;
    protected MatrixDcMotorController motorController;
    protected int physicalPort;
    protected MatrixServoController servoController;
    protected ConcurrentLinkedQueue<MatrixI2cTransaction> transactionQueue = new ConcurrentLinkedQueue<MatrixI2cTransaction>();

    public MatrixMasterController(ModernRoboticsUsbLegacyModule legacyModule, int physicalPort) {
        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;
        legacyModule.registerForI2cPortReadyCallback(this, physicalPort);
    }

    public void registerMotorController(MatrixDcMotorController motorController) {
        this.motorController = motorController;
    }

    public void registerServoController(MatrixServoController servoController) {
        this.servoController = servoController;
    }

    public int getPort() {
        return this.physicalPort;
    }

    public String getConnectionInfo() {
        return String.format("%s; port %d", this.legacyModule.getConnectionInfo(), this.physicalPort);
    }

    public boolean queueTransaction(MatrixI2cTransaction transaction, boolean force) {
        if (!force) {
            for (MatrixI2cTransaction aTransactionQueue : this.transactionQueue) {
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

    public boolean queueTransaction(MatrixI2cTransaction transaction) {
        return queueTransaction(transaction, false);
    }

    public void waitOnRead() {
        synchronized (this) {
            this.isReading = true;
            while (this.isReading) {
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
        switch (transaction.property) {
            case PROPERTY_BATTERY:
                this.motorController.handleReadBattery(i2cReadCache);
                break;
            case PROPERTY_POSITION:
                this.motorController.handleReadPosition(transaction, i2cReadCache);
                break;
            case PROPERTY_TARGET:
                this.motorController.handleReadPosition(transaction, i2cReadCache);
                break;
            case PROPERTY_MODE:
                this.motorController.handleReadMode(transaction, i2cReadCache);
                break;
            case PROPERTY_SERVO:
                this.servoController.handleReadServo(transaction, i2cReadCache);
                break;
            default:
                RobotLog.e("Transaction not a read " + transaction.property);
                break;
        }
        synchronized (this) {
            if (this.isReading) {
                this.isReading = false;
                notify();
            }
        }
    }

    protected void sendHeartbeat() {
        queueTransaction(new MatrixI2cTransaction((byte) 0, MatrixI2cProperties.PROPERTY_TIMEOUT, 3));
    }

    public void portIsReady(int port) {
        int i = 4;
        if (!this.transactionQueue.isEmpty()) {
            MatrixI2cTransaction matrixI2cTransaction = this.transactionQueue.peek();
            if (matrixI2cTransaction.state == C0009b.PENDING_I2C_READ) {
                this.legacyModule.readI2cCacheFromController(this.physicalPort);
                matrixI2cTransaction.state = C0009b.PENDING_READ_DONE;
                return;
            }
            byte[] bArr;
            int i2 = 0;
            if (matrixI2cTransaction.state == C0009b.PENDING_I2C_WRITE) {
                matrixI2cTransaction = this.transactionQueue.poll();
                if (!this.transactionQueue.isEmpty()) {
                    matrixI2cTransaction = this.transactionQueue.peek();
                } else {
                    return;
                }
            } else if (matrixI2cTransaction.state == C0009b.PENDING_READ_DONE) {
                handleReadDone(matrixI2cTransaction);
                matrixI2cTransaction = this.transactionQueue.poll();
                if (!this.transactionQueue.isEmpty()) {
                    matrixI2cTransaction = this.transactionQueue.peek();
                } else {
                    return;
                }
            }
            byte b;
            switch (matrixI2cTransaction.property) {
                case  PROPERTY_BATTERY :
                    bArr = new byte[]{(byte) 0};
                    i2 = 67;
                    i = 1;
                    break;
                case PROPERTY_POSITION :
                    byte b2 = f112b[matrixI2cTransaction.motor];
                    bArr = new byte[]{(byte) 0};
                    b = b2;
                    break;
                case PROPERTY_TARGET :
                    i2 = f113c[matrixI2cTransaction.motor];
                    bArr = TypeConversion.intToByteArray(matrixI2cTransaction.value);
                    break;
                case PROPERTY_MODE :
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    b = f115e[matrixI2cTransaction.motor];
                    i = 1;
                    break;
                case PROPERTY_SERVO :
                    bArr = new byte[]{matrixI2cTransaction.speed, (byte) matrixI2cTransaction.target};
                    b = f111a[matrixI2cTransaction.servo];
                    i = 2;
                    break;
                case PROPERTY_TIMEOUT :
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i2 = 66;
                    i = 1;
                    break;
                case PROPERTY_START :
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    i2 = 68;
                    i = 1;
                    break;
                case PROPERTY_SPEED :
                    bArr = new byte[]{(byte) matrixI2cTransaction.value};
                    b = f114d[matrixI2cTransaction.motor];
                    i = 1;
                    break;
                case PROPERTY_MOTOR_BATCH :
                    byte b3 = f112b[matrixI2cTransaction.motor];
                    ByteBuffer allocate = ByteBuffer.allocate(10);
                    allocate.put(TypeConversion.intToByteArray(0));
                    allocate.put(TypeConversion.intToByteArray(matrixI2cTransaction.target));
                    allocate.put(matrixI2cTransaction.speed);
                    allocate.put(matrixI2cTransaction.mode);
                    bArr = allocate.array();
                    b = b3;
                    i = 10;
                    break;
                case PROPERTY_SERVO_ENABLE :
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
                    matrixI2cTransaction.state = C0009b.PENDING_I2C_WRITE;
                } else {
                    this.legacyModule.setReadMode(this.physicalPort, 16, i2, i);
                    matrixI2cTransaction.state = C0009b.PENDING_I2C_READ;
                }
                this.legacyModule.setI2cPortActionFlag(this.physicalPort);
                this.legacyModule.writeI2cCacheToController(this.physicalPort);
            } catch (IllegalArgumentException e) {
                RobotLog.e(e.getMessage());
            }
            buginf(matrixI2cTransaction.toString());
        } else if (this.elapsedTime.time() > 2.0d) {
            sendHeartbeat();
            this.elapsedTime.reset();
        }
    }

    protected void buginf(String s) {
    }
}
