package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;

public class ModernRoboticsUsbServoController extends ModernRoboticsUsbDevice implements ServoController {

    public static final int ADDRESS_PWM = 72;
    public static final int ADDRESS_UNUSED = -1;
    public static final boolean DEBUG_LOGGING = false;
    public static final int NUMBER_OF_SERVOS = 6;
    public static final int MONITOR_LENGTH = 9;

    public static final byte PWM_DISABLE = (byte) -1;
    public static final byte PWM_ENABLE = (byte) 0;

    public static final int SERVO_POSITION_MAX = 255;
    public static final byte START_ADDRESS = (byte) 64;

    protected ModernRoboticsUsbServoController(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableBlocking(serialNumber, device, MONITOR_LENGTH, START_ADDRESS, DEBUG_LOGGING));
        pwmDisable();
    }

    public String getDeviceName() {
        return "Modern Robotics USB Servo Controller";
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public void close() {
        pwmDisable();
        super.close();
    }

    public void pwmEnable() {
        write(ADDRESS_PWM, PWM_ENABLE);
    }

    public void pwmDisable() {
        write(ADDRESS_PWM, PWM_DISABLE);
    }

    public PwmStatus getPwmStatus() {
        if (read(ADDRESS_PWM, 1)[0] == ADDRESS_UNUSED) {
            return PwmStatus.DISABLED;
        }
        return PwmStatus.ENABLED;
    }

    public void setServoPosition(int channel, double position) {
        validateChannel(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        write(getChannelAddress(channel), SERVO_POSITION_MAX * position);
        pwmEnable();
    }

    public double getServoPosition(int channel) {
        validateChannel(channel);
        return TypeConversion.unsignedByteToDouble(read(getChannelAddress(channel), 1)[0]) / SERVO_POSITION_MAX;
    }

    private void validateChannel(int port) {
        if (port < 1 || port >= NUMBER_OF_SERVOS) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", port, NUMBER_OF_SERVOS-1));
        }
    }

    private int getChannelAddress(int channel) {
        return channel + 65;
    }
}
