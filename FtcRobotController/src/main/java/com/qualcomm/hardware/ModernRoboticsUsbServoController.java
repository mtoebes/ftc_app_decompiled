package com.qualcomm.hardware;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoController.PwmStatus;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;

public class ModernRoboticsUsbServoController extends ModernRoboticsUsbDevice implements ServoController {
    public static final int ADDRESS_CHANNEL1 = 66;
    public static final int ADDRESS_CHANNEL2 = 67;
    public static final int ADDRESS_CHANNEL3 = 68;
    public static final int ADDRESS_CHANNEL4 = 69;
    public static final int ADDRESS_CHANNEL5 = 70;
    public static final int ADDRESS_CHANNEL6 = 71;
    public static final byte[] ADDRESS_CHANNEL_MAP;
    public static final int ADDRESS_PWM = 72;
    public static final int ADDRESS_UNUSED = -1;
    public static final boolean DEBUG_LOGGING = false;
    public static final int MAX_SERVOS = 6;
    public static final int MONITOR_LENGTH = 9;
    public static final byte PWM_DISABLE = (byte) -1;
    public static final byte PWM_ENABLE = (byte) 0;
    public static final byte PWM_ENABLE_WITHOUT_TIMEOUT = (byte) -86;
    public static final int SERVO_POSITION_MAX = 255;
    public static final byte START_ADDRESS = (byte) 64;

    static {
        ADDRESS_CHANNEL_MAP = new byte[]{PWM_DISABLE, HiTechnicNxtCompassSensor.HEADING_IN_TWO_DEGREE_INCREMENTS, HiTechnicNxtCompassSensor.ONE_DEGREE_HEADING_ADDER, (byte) 68, (byte) 69, HiTechnicNxtCompassSensor.CALIBRATION_FAILURE, (byte) 71};
    }

    protected ModernRoboticsUsbServoController(SerialNumber serialNumber, RobotUsbDevice device, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(serialNumber, manager, new ReadWriteRunnableBlocking(serialNumber, device, MONITOR_LENGTH, 64, DEBUG_LOGGING));
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
        m72a(channel);
        Range.throwIfRangeIsInvalid(position, 0.0d, 1.0d);
        write((int) ADDRESS_CHANNEL_MAP[channel], 255.0d * position);
        pwmEnable();
    }

    public double getServoPosition(int channel) {
        m72a(channel);
        return TypeConversion.unsignedByteToDouble(read(ADDRESS_CHANNEL_MAP[channel], 1)[0]) / 255.0d;
    }

    private void m72a(int i) {
        if (i < 1 || i > ADDRESS_CHANNEL_MAP.length) {
            throw new IllegalArgumentException(String.format("Channel %d is invalid; valid channels are 1..%d", new Object[]{i, MAX_SERVOS}));
        }
    }
}
