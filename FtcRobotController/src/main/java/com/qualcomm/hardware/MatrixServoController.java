package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.C0008a;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoController.PwmStatus;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;

public class MatrixServoController implements ServoController {
    public static final int SERVO_POSITION_MAX = 240;
    private MatrixMasterController f118a;
    protected PwmStatus pwmStatus;
    protected double[] servoCache;

    public MatrixServoController(MatrixMasterController master) {
        this.servoCache = new double[4];
        this.f118a = master;
        this.pwmStatus = PwmStatus.DISABLED;
        Arrays.fill(this.servoCache, 0.0d);
        master.registerServoController(this);
    }

    public void pwmEnable() {
        this.f118a.queueTransaction(new MatrixI2cTransaction((byte) 0, C0008a.PROPERTY_SERVO_ENABLE, 15));
        this.pwmStatus = PwmStatus.ENABLED;
    }

    public void pwmDisable() {
        this.f118a.queueTransaction(new MatrixI2cTransaction((byte) 0, C0008a.PROPERTY_SERVO_ENABLE, 0));
        this.pwmStatus = PwmStatus.DISABLED;
    }

    public PwmStatus getPwmStatus() {
        return this.pwmStatus;
    }

    public void setServoPosition(int channel, double position) {
        m50a(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        this.f118a.queueTransaction(new MatrixI2cTransaction((byte) channel, (byte) ((int) (240.0d * position)), (byte) 0));
    }

    public void setServoPosition(int channel, double position, byte speed) {
        m50a(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        this.f118a.queueTransaction(new MatrixI2cTransaction((byte) channel, (byte) ((int) (240.0d * position)), speed));
    }

    public double getServoPosition(int channel) {
        if (this.f118a.queueTransaction(new MatrixI2cTransaction((byte) channel, C0008a.PROPERTY_SERVO))) {
            this.f118a.waitOnRead();
        }
        return this.servoCache[channel] / 240.0d;
    }

    public String getDeviceName() {
        return "Matrix Servo Controller";
    }

    public String getConnectionInfo() {
        return this.f118a.getConnectionInfo();
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        pwmDisable();
    }

    private void m50a(int i) {
        if (i < 1 || i > 4) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", new Object[]{i, (byte) 4}));
        }
    }

    public void handleReadServo(MatrixI2cTransaction transaction, byte[] buffer) {
        this.servoCache[transaction.servo] = (double) TypeConversion.unsignedByteToInt(buffer[4]);
    }
}
