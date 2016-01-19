package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.MatrixI2cProperties;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;

public class MatrixServoController implements ServoController {
    private static final int VERSION = 1;

    private static final int SERVO_POSITION_MAX = 240;
    private final MatrixMasterController masterController;
    private PwmStatus pwmStatus = PwmStatus.DISABLED;
    private final double[] servoCache = new double[4];

    public MatrixServoController(MatrixMasterController master) {
        this.masterController = master;
        master.registerServoController(this);
    }

    public void pwmEnable() {
        this.masterController.queueTransaction(new MatrixI2cTransaction((byte) 0, MatrixI2cProperties.PROPERTY_SERVO_ENABLE, 15));
        this.pwmStatus = PwmStatus.ENABLED;
    }

    public void pwmDisable() {
        this.masterController.queueTransaction(new MatrixI2cTransaction((byte) 0, MatrixI2cProperties.PROPERTY_SERVO_ENABLE, 0));
        this.pwmStatus = PwmStatus.DISABLED;
    }

    public PwmStatus getPwmStatus() {
        return this.pwmStatus;
    }

    public void setServoPosition(int channel, double position) {
        validateChannel(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        this.masterController.queueTransaction(new MatrixI2cTransaction((byte) channel, (byte) ((int) (SERVO_POSITION_MAX * position)), (byte) 0));
    }

    public void setServoPosition(int channel, double position, byte speed) {
        validateChannel(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        this.masterController.queueTransaction(new MatrixI2cTransaction((byte) channel, (byte) ((int) (SERVO_POSITION_MAX * position)), speed));
    }

    public double getServoPosition(int channel) {
        if (this.masterController.queueTransaction(new MatrixI2cTransaction((byte) channel, MatrixI2cProperties.PROPERTY_SERVO))) {
            this.masterController.waitOnRead();
        }
        return this.servoCache[channel] / SERVO_POSITION_MAX;
    }

    public String getDeviceName() {
        return "Matrix Servo Controller";
    }

    public String getConnectionInfo() {
        return this.masterController.getConnectionInfo();
    }

    public int getVersion() {
        return VERSION;
    }

    public void close() {
        pwmDisable();
    }

    private void validateChannel(int channel) {
        if (channel < 1 || channel > 4) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", channel, (byte) 4));
        }
    }

    public void handleReadServo(MatrixI2cTransaction transaction, byte[] buffer) {
        this.servoCache[transaction.servo] = (double) TypeConversion.unsignedByteToInt(buffer[4]);
    }
}
