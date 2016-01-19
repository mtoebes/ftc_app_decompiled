package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cProperties;
import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cTransactionState;
import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

class MatrixMasterController implements I2cPortReadyCallback {

    private static final int ADDRESS_TIMEOUT = 66;
    private static final int ADDRESS_BATTERY = 67;
    private static final int ADDRESS_START = 68;
    private static final int ADDRESS_SERVO_ENABLE = 69;

    private static final int ADDRESS_SERVO_START = 70;
    private static final int ADDRESS_SERVO_LENGTH = 2;

    private static final int ADDRESS_MOTOR_START = 78;
    private static final int ADDRESS_MOTOR_LENGTH = 10;

    private static final int OFFSET_MOTOR_BATCH = 0;
    private static final int OFFSET_MOTOR_TARGET = 4;
    private static final int OFFSET_MOTOR_SPEED = 8;
    private static final int OFFSET_MOTOR_MODE = 9;

    private static final double ELAPSED_TIME_MAX = 2.0;

    private volatile boolean isReading = false;
    private final ElapsedTime elapsedTime =  new ElapsedTime(0);
    private final ModernRoboticsUsbLegacyModule legacyModule;
    private MatrixDcMotorController motorController;
    private final int physicalPort;
    private MatrixServoController servoController;
    private final ConcurrentLinkedQueue<MatrixI2cTransaction> transactionQueue = new ConcurrentLinkedQueue<MatrixI2cTransaction>();

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

    private boolean queueTransaction(MatrixI2cTransaction transaction, boolean force) {
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

    private void handleReadDone(MatrixI2cTransaction transaction) {
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

    private void sendHeartbeat() {
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

                try {
                    byte[] data = getData(transaction);
                    int memAddress = getMemAddress(transaction);

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

    private byte[] getData(MatrixI2cTransaction transaction) {
        switch (transaction.property) {
            case PROPERTY_BATTERY:
            case PROPERTY_POSITION:
                return new byte[]{(byte) 0};
            case PROPERTY_TARGET:
                return  TypeConversion.intToByteArray(transaction.value);
            case PROPERTY_SERVO:
                return new byte[]{transaction.speed, (byte) transaction.target};
            case PROPERTY_MOTOR_BATCH:
                ByteBuffer allocate = ByteBuffer.allocate(10);
                allocate.put(TypeConversion.intToByteArray(0));
                allocate.put(TypeConversion.intToByteArray(transaction.target));
                allocate.put(transaction.speed);
                allocate.put(transaction.mode);
                return allocate.array();
            case PROPERTY_MODE:
            case PROPERTY_TIMEOUT:
            case PROPERTY_START:
            case PROPERTY_SPEED:
            case PROPERTY_SERVO_ENABLE:
            default:
                return new byte[]{(byte) transaction.value};
        }
    }

    private int getMemAddress(MatrixI2cTransaction transaction) {
        switch (transaction.property) {
            case PROPERTY_POSITION:
                return getMotorBatchAddress(transaction.motor);
            case PROPERTY_TARGET:
                return getMotorTargetAddress(transaction.motor);
            case PROPERTY_MODE:
                return getMotorModeAddress(transaction.motor);
            case PROPERTY_SERVO:
                return getServoAddress(transaction.servo);
            case PROPERTY_SPEED:
                return getMotorSpeedAddress(transaction.motor);
            case PROPERTY_MOTOR_BATCH:
                return getMotorBatchAddress(transaction.motor);
            case PROPERTY_TIMEOUT:
                return ADDRESS_TIMEOUT;
            case PROPERTY_BATTERY:
                return ADDRESS_BATTERY;
            case PROPERTY_START:
                return ADDRESS_START;
            case PROPERTY_SERVO_ENABLE:
                return ADDRESS_SERVO_ENABLE;
            default:
                return 0;
        }
    }

    private static int getServoAddress(int servo) {
        return ADDRESS_SERVO_START + (servo * ADDRESS_SERVO_LENGTH);
    }

    private static int getMotorAddress(int motor) {
        return ADDRESS_MOTOR_START + (motor * ADDRESS_MOTOR_LENGTH);
    }

    private static int getMotorBatchAddress(int motor) {
        return getMotorAddress(motor) + OFFSET_MOTOR_BATCH;
    }

    private static int getMotorTargetAddress(int motor) {
        return getMotorAddress(motor) + OFFSET_MOTOR_TARGET;
    }

    private static int getMotorSpeedAddress(int motor) {
        return getMotorAddress(motor) + OFFSET_MOTOR_SPEED;
    }
    private static int getMotorModeAddress(int motor) {
        return getMotorAddress(motor) + OFFSET_MOTOR_MODE;
    }

    private void buginf(String s) {
    }
}
