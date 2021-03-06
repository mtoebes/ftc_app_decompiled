package com.qualcomm.hardware;

import com.qualcomm.robotcore.util.RobotLog;

public class MatrixI2cTransaction {
    public byte mode;
    public byte motor;
    public final MatrixI2cProperties property;
    public byte servo;
    public byte speed;
    public MatrixI2cTransactionState state;
    public int target;
    public int value;
    public final boolean write;

    enum MatrixI2cProperties {
        PROPERTY_MODE,
        PROPERTY_TARGET,
        PROPERTY_SPEED,
        PROPERTY_BATTERY,
        PROPERTY_POSITION,
        PROPERTY_MOTOR_BATCH,
        PROPERTY_SERVO,
        PROPERTY_SERVO_ENABLE,
        PROPERTY_START,
        PROPERTY_TIMEOUT
    }

    enum MatrixI2cTransactionState {
        QUEUED,
        PENDING_I2C_READ,
        PENDING_I2C_WRITE,
        PENDING_READ_DONE,
        DONE
    }

    MatrixI2cTransaction(byte motor, MatrixI2cProperties property) {
        this.motor = motor;
        this.property = property;
        this.state = MatrixI2cTransactionState.QUEUED;
        this.write = false;
    }

    MatrixI2cTransaction(byte motor, MatrixI2cProperties property, int value) {
        this.motor = motor;
        this.value = value;
        this.property = property;
        this.state = MatrixI2cTransactionState.QUEUED;
        this.write = true;
    }

    MatrixI2cTransaction(byte motor, byte speed, int target, byte mode) {
        this.motor = motor;
        this.speed = speed;
        this.target = target;
        this.mode = mode;
        this.property = MatrixI2cProperties.PROPERTY_MOTOR_BATCH;
        this.state = MatrixI2cTransactionState.QUEUED;
        this.write = true;
    }

    MatrixI2cTransaction(byte servo, byte target, byte speed) {
        this.servo = servo;
        this.speed = speed;
        this.target = target;
        this.property = MatrixI2cProperties.PROPERTY_SERVO;
        this.state = MatrixI2cTransactionState.QUEUED;
        this.write = true;
    }

    public boolean isEqual(MatrixI2cTransaction transaction) {
        if (this.property != transaction.property) {
            return false;
        }
        switch (this.property) {
            case PROPERTY_MODE :
            case PROPERTY_START :
            case PROPERTY_TIMEOUT :
            case PROPERTY_TARGET :
            case PROPERTY_SPEED :
            case PROPERTY_BATTERY :
            case PROPERTY_POSITION :
                return this.write == transaction.write && this.motor == transaction.motor && this.value == transaction.value;
            case PROPERTY_MOTOR_BATCH :
                return this.write == transaction.write && this.motor == transaction.motor && this.speed == transaction.speed && this.target == transaction.target && this.mode == transaction.mode;
            case PROPERTY_SERVO :
                return this.write == transaction.write && this.servo == transaction.servo && this.speed == transaction.speed && this.target == transaction.target;
            case PROPERTY_SERVO_ENABLE :
                return this.write == transaction.write && this.value == transaction.value;
            default:
                RobotLog.e("Can not compare against unknown transaction property " + transaction.toString());
                return false;
        }
    }

    public String toString() {
        if (this.property == MatrixI2cProperties.PROPERTY_MOTOR_BATCH) {
            return String.format("Matrix motor transaction: %s motor %d write %b change rate %d target %d", this.property.toString(), this.motor, this.write, this.speed, this.target);
        } else if (this.property == MatrixI2cProperties.PROPERTY_SERVO) {
            return String.format("Matrix servo transaction: %s servo %d write %b change rate %d target %d", this.property.toString(), this.servo, this.write, this.speed, this.target);
        } else if (this.property == MatrixI2cProperties.PROPERTY_SERVO_ENABLE) {
            return String.format("Matrix servo transaction: %s servo %d write %b value %d", this.property.toString(), this.servo, this.write, this.value);
        } else {
            return String.format("Matrix servo transaction: %s motor %d write %b value %d", this.property.toString(), this.motor, this.write, this.value);
        }
    }
}
