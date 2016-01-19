package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cProperties;
import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cTransactionState;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatrixMasterController implements I2cPortReadyCallback {
    private static final byte[] ADDRESS_SERVO_MAP = new byte[]{(byte) 0, (byte) 70, (byte) 72, (byte) 74, (byte) 76};
    private static final byte[] ADDRESS_MOTOR_BATCH_MAP = new byte[]{(byte) 0, (byte) 78, (byte) 88, (byte) 98, (byte) 108};
    private static final byte[] ADDRESS_MOTOR_TARGET_MAP = new byte[]{(byte) 0, (byte) 82, (byte) 92, (byte) 102, (byte) 112};
    private static final byte[] ADDRESs_MOTOR_SPEED_MAP = new byte[]{(byte) 0, (byte) 86, (byte) 96, (byte) 106, (byte) 116};
    private static final byte[] ADDRESS_MOTOR_MODE_MAP = new byte[]{(byte) 0, (byte) 87, (byte) 97, (byte) 107, (byte) 117};
    private static final double ELAPSED_TIME_MAX = 2.0;

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
        if (!this.transactionQueue.isEmpty()) {
            MatrixI2cTransaction transaction = this.transactionQueue.peek();

            if (transaction.state == MatrixI2cTransactionState.PENDING_I2C_READ) {
                this.legacyModule.readI2cCacheFromController(this.physicalPort);
                transaction.state = MatrixI2cTransactionState.PENDING_READ_DONE;
            } else {
                if (transaction.state == MatrixI2cTransactionState.PENDING_I2C_WRITE ||
                        transaction.state == MatrixI2cTransactionState.PENDING_READ_DONE) {
                    if (transaction.state == MatrixI2cTransactionState.PENDING_READ_DONE) {
                        handleReadDone(transaction);
                    }

                    this.transactionQueue.poll();
                    transaction = this.transactionQueue.peek();
                }

                if(transaction == null) {
                    return;
                }
                byte[] data;
                int memAddress;

                switch (transaction.property) {
                    case PROPERTY_BATTERY:
                        data = new byte[]{(byte) 0};
                        memAddress = 67;
                        break;
                    case PROPERTY_POSITION:
                        memAddress = ADDRESS_MOTOR_BATCH_MAP[transaction.motor];
                        data = new byte[]{(byte) 0};
                        break;
                    case PROPERTY_TARGET:
                        memAddress = ADDRESS_MOTOR_TARGET_MAP[transaction.motor];
                        data = TypeConversion.intToByteArray(transaction.value);
                        break;
                    case PROPERTY_MODE:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = ADDRESS_MOTOR_MODE_MAP[transaction.motor];
                        break;
                    case PROPERTY_SERVO:
                        data = new byte[]{transaction.speed, (byte) transaction.target};
                        memAddress = ADDRESS_SERVO_MAP[transaction.servo];
                        break;
                    case PROPERTY_TIMEOUT:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = 66;
                        break;
                    case PROPERTY_START:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = 68;
                        break;
                    case PROPERTY_SPEED:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = ADDRESs_MOTOR_SPEED_MAP[transaction.motor];
                        break;
                    case PROPERTY_MOTOR_BATCH:
                        memAddress = ADDRESS_MOTOR_BATCH_MAP[transaction.motor];
                        ByteBuffer allocate = ByteBuffer.allocate(10);
                        allocate.put(TypeConversion.intToByteArray(0));
                        allocate.put(TypeConversion.intToByteArray(transaction.target));
                        allocate.put(transaction.speed);
                        allocate.put(transaction.mode);
                        data = allocate.array();
                        break;
                    case PROPERTY_SERVO_ENABLE:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = 69;
                        break;
                    default:
                        data = new byte[]{(byte) transaction.value};
                        memAddress = 0;
                        break;
                }

                try {
                    if (transaction.write) {
                        this.legacyModule.setWriteMode(this.physicalPort, 16, memAddress);
                        this.legacyModule.setData(this.physicalPort, data, data.length);
                        transaction.state = MatrixI2cTransactionState.PENDING_I2C_WRITE;
                    } else { // read
                        this.legacyModule.setReadMode(this.physicalPort, 16, memAddress, data.length);
                        transaction.state = MatrixI2cTransactionState.PENDING_I2C_READ;
                    }
                    this.legacyModule.setI2cPortActionFlag(this.physicalPort);
                    this.legacyModule.writeI2cCacheToController(this.physicalPort);
                } catch (IllegalArgumentException e) {
                    RobotLog.e(e.getMessage());
                }
                buginf(transaction.toString());
            }
        } else if (this.elapsedTime.time() > ELAPSED_TIME_MAX) {
            sendHeartbeat();
            this.elapsedTime.reset();
        }
    }

    protected void buginf(String s) {
    }
}
