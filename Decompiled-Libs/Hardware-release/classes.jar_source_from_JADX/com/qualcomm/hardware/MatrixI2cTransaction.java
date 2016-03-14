package com.qualcomm.hardware;

import com.qualcomm.robotcore.util.RobotLog;

public class MatrixI2cTransaction {
    public byte mode;
    public byte motor;
    public C0008a property;
    public byte servo;
    public byte speed;
    public C0009b state;
    public int target;
    public int value;
    public boolean write;

    /* renamed from: com.qualcomm.hardware.MatrixI2cTransaction.1 */
    static /* synthetic */ class C00071 {
        static final /* synthetic */ int[] f92a;

        static {
            f92a = new int[C0008a.values().length];
            try {
                f92a[C0008a.PROPERTY_MODE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f92a[C0008a.PROPERTY_START.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f92a[C0008a.PROPERTY_TIMEOUT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f92a[C0008a.PROPERTY_TARGET.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f92a[C0008a.PROPERTY_SPEED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f92a[C0008a.PROPERTY_BATTERY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f92a[C0008a.PROPERTY_POSITION.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f92a[C0008a.PROPERTY_MOTOR_BATCH.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f92a[C0008a.PROPERTY_SERVO.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f92a[C0008a.PROPERTY_SERVO_ENABLE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.MatrixI2cTransaction.a */
    enum C0008a {
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

    /* renamed from: com.qualcomm.hardware.MatrixI2cTransaction.b */
    enum C0009b {
        QUEUED,
        PENDING_I2C_READ,
        PENDING_I2C_WRITE,
        PENDING_READ_DONE,
        DONE
    }

    MatrixI2cTransaction(byte motor, C0008a property) {
        this.motor = motor;
        this.property = property;
        this.state = C0009b.QUEUED;
        this.write = false;
    }

    MatrixI2cTransaction(byte motor, C0008a property, int value) {
        this.motor = motor;
        this.value = value;
        this.property = property;
        this.state = C0009b.QUEUED;
        this.write = true;
    }

    MatrixI2cTransaction(byte motor, byte speed, int target, byte mode) {
        this.motor = motor;
        this.speed = speed;
        this.target = target;
        this.mode = mode;
        this.property = C0008a.PROPERTY_MOTOR_BATCH;
        this.state = C0009b.QUEUED;
        this.write = true;
    }

    MatrixI2cTransaction(byte servo, byte target, byte speed) {
        this.servo = servo;
        this.speed = speed;
        this.target = target;
        this.property = C0008a.PROPERTY_SERVO;
        this.state = C0009b.QUEUED;
        this.write = true;
    }

    public boolean isEqual(MatrixI2cTransaction transaction) {
        if (this.property != transaction.property) {
            return false;
        }
        switch (C00071.f92a[this.property.ordinal()]) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
            case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
            case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
            case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
            case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
            case ModernRoboticsUsbServoController.MAX_SERVOS /*6*/:
            case ModernRoboticsUsbDeviceInterfaceModule.MAX_ANALOG_PORT_NUMBER /*7*/:
                if (this.write == transaction.write && this.motor == transaction.motor && this.value == transaction.value) {
                    return true;
                }
                return false;
            case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S2 /*8*/:
                if (this.write == transaction.write && this.motor == transaction.motor && this.speed == transaction.speed && this.target == transaction.target && this.mode == transaction.mode) {
                    return true;
                }
                return false;
            case ModernRoboticsUsbServoController.MONITOR_LENGTH /*9*/:
                if (this.write == transaction.write && this.servo == transaction.servo && this.speed == transaction.speed && this.target == transaction.target) {
                    return true;
                }
                return false;
            case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S3 /*10*/:
                if (this.write == transaction.write && this.value == transaction.value) {
                    return true;
                }
                return false;
            default:
                RobotLog.e("Can not compare against unknown transaction property " + transaction.toString());
                return false;
        }
    }

    public String toString() {
        if (this.property == C0008a.PROPERTY_MOTOR_BATCH) {
            return "Matrix motor transaction: " + this.property + " motor " + this.motor + " write " + this.write + " speed " + this.speed + " target " + this.target + " mode " + this.mode;
        }
        if (this.property == C0008a.PROPERTY_SERVO) {
            return "Matrix servo transaction: " + this.property + " servo " + this.servo + " write " + this.write + " change rate " + this.speed + " target " + this.target;
        }
        if (this.property == C0008a.PROPERTY_SERVO_ENABLE) {
            return "Matrix servo transaction: " + this.property + " servo " + this.servo + " write " + this.write + " value " + this.value;
        }
        return "Matrix motor transaction: " + this.property + " motor " + this.motor + " write " + this.write + " value " + this.value;
    }
}
