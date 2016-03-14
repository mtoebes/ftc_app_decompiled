package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.R;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtCompassSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.CreateReadWriteRunnable;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.OpenRobotUsbDevice;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.util.DifferentialControlLoopCoefficients;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.util.TypeConversion;

public class ModernRoboticsUsbDcMotorController extends ModernRoboticsUsbDevice implements DcMotorController, VoltageSensor {
    public static final int ADDRESS_BATTERY_VOLTAGE = 84;
    public static final int[] ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP;
    public static final int ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE = 76;
    public static final int ADDRESS_MOTOR1_D_COEFFICIENT = 89;
    public static final int ADDRESS_MOTOR1_GEAR_RATIO = 86;
    public static final int ADDRESS_MOTOR1_I_COEFFICIENT = 88;
    public static final int ADDRESS_MOTOR1_MODE = 68;
    public static final int ADDRESS_MOTOR1_POWER = 69;
    public static final int ADDRESS_MOTOR1_P_COEFFICIENT = 87;
    public static final int ADDRESS_MOTOR1_TARGET_ENCODER_VALUE = 64;
    public static final int ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE = 80;
    public static final int ADDRESS_MOTOR2_D_COEFFICIENT = 93;
    public static final int ADDRESS_MOTOR2_GEAR_RATIO = 90;
    public static final int ADDRESS_MOTOR2_I_COEFFICIENT = 92;
    public static final int ADDRESS_MOTOR2_MODE = 71;
    public static final int ADDRESS_MOTOR2_POWER = 70;
    public static final int ADDRESS_MOTOR2_P_COEFFICIENT = 91;
    public static final int ADDRESS_MOTOR2_TARGET_ENCODER_VALUE = 72;
    public static final int[] ADDRESS_MOTOR_CURRENT_ENCODER_VALUE_MAP;
    public static final int[] ADDRESS_MOTOR_GEAR_RATIO_MAP;
    public static final int[] ADDRESS_MOTOR_MODE_MAP;
    public static final int[] ADDRESS_MOTOR_POWER_MAP;
    public static final int[] ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP;
    public static final int ADDRESS_UNUSED = 255;
    public static final double BATTERY_MAX_MEASURABLE_VOLTAGE = 20.4d;
    public static final int BATTERY_MAX_MEASURABLE_VOLTAGE_INT = 1023;
    public static final int BUSY_THRESHOLD = 5;
    public static final byte CHANNEL_MODE_FLAG_BUSY = Byte.MIN_VALUE;
    public static final byte CHANNEL_MODE_FLAG_ERROR = (byte) 64;
    public static final byte CHANNEL_MODE_FLAG_LOCK = (byte) 4;
    public static final byte CHANNEL_MODE_FLAG_NO_TIMEOUT = (byte) 16;
    public static final byte CHANNEL_MODE_FLAG_REVERSE = (byte) 8;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RESET = (byte) 3;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED = (byte) 1;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_POWER_CONTROL_ONLY = (byte) 0;
    public static final byte CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION = (byte) 2;
    public static final byte CHANNEL_MODE_FLAG_UNUSED = (byte) 32;
    public static final int CHANNEL_MODE_MASK_BUSY = 128;
    public static final int CHANNEL_MODE_MASK_EMPTY_D5 = 32;
    public static final int CHANNEL_MODE_MASK_ERROR = 64;
    public static final int CHANNEL_MODE_MASK_LOCK = 4;
    public static final int CHANNEL_MODE_MASK_NO_TIMEOUT = 16;
    public static final int CHANNEL_MODE_MASK_REVERSE = 8;
    public static final int CHANNEL_MODE_MASK_SELECTION = 3;
    public static final boolean DEBUG_LOGGING = false;
    public static final byte DEFAULT_D_COEFFICIENT = (byte) -72;
    public static final byte DEFAULT_I_COEFFICIENT = (byte) 64;
    public static final byte DEFAULT_P_COEFFICIENT = Byte.MIN_VALUE;
    public static final int DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX = 255;
    public static final int MAX_MOTOR = 2;
    public static final int MIN_MOTOR = 1;
    public static final int MONITOR_LENGTH = 30;
    public static final byte POWER_BREAK = (byte) 0;
    public static final byte POWER_FLOAT = Byte.MIN_VALUE;
    public static final byte POWER_MAX = (byte) 100;
    public static final byte POWER_MIN = (byte) -100;
    public static final byte RATIO_MAX = Byte.MAX_VALUE;
    public static final byte RATIO_MIN = Byte.MIN_VALUE;
    public static final byte START_ADDRESS = (byte) 64;

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController.1 */
    class C00181 implements CreateReadWriteRunnable {
        final /* synthetic */ Context f151a;
        final /* synthetic */ SerialNumber f152b;

        C00181(Context context, SerialNumber serialNumber) {
            this.f151a = context;
            this.f152b = serialNumber;
        }

        public ReadWriteRunnable create(RobotUsbDevice device) {
            return new ReadWriteRunnableBlocking(this.f151a, this.f152b, device, ModernRoboticsUsbDcMotorController.MONITOR_LENGTH, ModernRoboticsUsbDcMotorController.CHANNEL_MODE_MASK_ERROR, ModernRoboticsUsbDcMotorController.DEBUG_LOGGING);
        }
    }

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController.2 */
    static /* synthetic */ class C00192 {
        static final /* synthetic */ int[] f153a;

        static {
            f153a = new int[RunMode.values().length];
            try {
                f153a[RunMode.RUN_USING_ENCODERS.ordinal()] = ModernRoboticsUsbDcMotorController.MIN_MOTOR;
            } catch (NoSuchFieldError e) {
            }
            try {
                f153a[RunMode.RUN_WITHOUT_ENCODERS.ordinal()] = ModernRoboticsUsbDcMotorController.MAX_MOTOR;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f153a[RunMode.RUN_TO_POSITION.ordinal()] = ModernRoboticsUsbDcMotorController.CHANNEL_MODE_MASK_SELECTION;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f153a[RunMode.RESET_ENCODERS.ordinal()] = ModernRoboticsUsbDcMotorController.CHANNEL_MODE_MASK_LOCK;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    static {
        ADDRESS_MOTOR_POWER_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, ADDRESS_MOTOR1_POWER, ADDRESS_MOTOR2_POWER};
        ADDRESS_MOTOR_MODE_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, ADDRESS_MOTOR1_MODE, ADDRESS_MOTOR2_MODE};
        ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, CHANNEL_MODE_MASK_ERROR, ADDRESS_MOTOR2_TARGET_ENCODER_VALUE};
        ADDRESS_MOTOR_CURRENT_ENCODER_VALUE_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, ADDRESS_MOTOR1_CURRENT_ENCODER_VALUE, ADDRESS_MOTOR2_CURRENT_ENCODER_VALUE};
        ADDRESS_MOTOR_GEAR_RATIO_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, ADDRESS_MOTOR1_GEAR_RATIO, ADDRESS_MOTOR2_GEAR_RATIO};
        ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP = new int[]{DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAX, ADDRESS_MOTOR1_P_COEFFICIENT, ADDRESS_MOTOR2_P_COEFFICIENT};
    }

    public ModernRoboticsUsbDcMotorController(Context context, SerialNumber serialNumber, OpenRobotUsbDevice openRobotUsbDevice, EventLoopManager manager) throws RobotCoreException, InterruptedException {
        super(context, serialNumber, manager, openRobotUsbDevice, new C00181(context, serialNumber));
    }

    public void initializeHardware() {
        m57a();
        m59b();
    }

    public String getDeviceName() {
        return this.context.getString(R.string.moduleDisplayNameMotorController);
    }

    public String getConnectionInfo() {
        return "USB " + getSerialNumber();
    }

    public void close() {
        m57a();
        super.close();
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return DeviceMode.READ_WRITE;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        m58a(motor);
        write(ADDRESS_MOTOR_MODE_MAP[motor], runModeToFlag(mode));
    }

    public RunMode getMotorChannelMode(int motor) {
        m58a(motor);
        return flagToRunMode(read(ADDRESS_MOTOR_MODE_MAP[motor]));
    }

    public void setMotorPower(int motor, double power) {
        m58a(motor);
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        int i = ADDRESS_MOTOR_POWER_MAP[motor];
        byte[] bArr = new byte[MIN_MOTOR];
        bArr[0] = (byte) ((int) (100.0d * power));
        write(i, bArr);
    }

    public double getMotorPower(int motor) {
        m58a(motor);
        byte read = read(ADDRESS_MOTOR_POWER_MAP[motor]);
        if (read == -128) {
            return 0.0d;
        }
        return ((double) read) / 100.0d;
    }

    public boolean isBusy(int motor) {
        m58a(motor);
        return Math.abs(getMotorTargetPosition(motor) - getMotorCurrentPosition(motor)) > BUSY_THRESHOLD ? true : DEBUG_LOGGING;
    }

    public void setMotorPowerFloat(int motor) {
        m58a(motor);
        int i = ADDRESS_MOTOR_POWER_MAP[motor];
        byte[] bArr = new byte[MIN_MOTOR];
        bArr[0] = RATIO_MIN;
        write(i, bArr);
    }

    public boolean getMotorPowerFloat(int motor) {
        m58a(motor);
        return read(ADDRESS_MOTOR_POWER_MAP[motor]) == -128 ? true : DEBUG_LOGGING;
    }

    public void setMotorTargetPosition(int motor, int position) {
        m58a(motor);
        Range.throwIfRangeIsInvalid((double) position, -2.147483648E9d, 2.147483647E9d);
        write(ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP[motor], TypeConversion.intToByteArray(position));
    }

    public int getMotorTargetPosition(int motor) {
        m58a(motor);
        return TypeConversion.byteArrayToInt(read(ADDRESS_MOTOR_TARGET_ENCODER_VALUE_MAP[motor], CHANNEL_MODE_MASK_LOCK));
    }

    public int getMotorCurrentPosition(int motor) {
        m58a(motor);
        return TypeConversion.byteArrayToInt(read(ADDRESS_MOTOR_CURRENT_ENCODER_VALUE_MAP[motor], CHANNEL_MODE_MASK_LOCK));
    }

    public double getVoltage() {
        byte[] read = read(ADDRESS_BATTERY_VOLTAGE, MAX_MOTOR);
        return (((double) (((TypeConversion.unsignedByteToInt(read[MIN_MOTOR]) & CHANNEL_MODE_MASK_SELECTION) + (TypeConversion.unsignedByteToInt(read[0]) << MAX_MOTOR)) & BATTERY_MAX_MEASURABLE_VOLTAGE_INT)) / 1023.0d) * BATTERY_MAX_MEASURABLE_VOLTAGE;
    }

    public void setGearRatio(int motor, double ratio) {
        m58a(motor);
        Range.throwIfRangeIsInvalid(ratio, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        int i = ADDRESS_MOTOR_GEAR_RATIO_MAP[motor];
        byte[] bArr = new byte[MIN_MOTOR];
        bArr[0] = (byte) ((int) (127.0d * ratio));
        write(i, bArr);
    }

    public double getGearRatio(int motor) {
        m58a(motor);
        return ((double) read(ADDRESS_MOTOR_GEAR_RATIO_MAP[motor], MIN_MOTOR)[0]) / 127.0d;
    }

    public void setDifferentialControlLoopCoefficients(int motor, DifferentialControlLoopCoefficients pid) {
        m58a(motor);
        if (pid.p > 255.0d) {
            pid.p = 255.0d;
        }
        if (pid.i > 255.0d) {
            pid.i = 255.0d;
        }
        if (pid.d > 255.0d) {
            pid.d = 255.0d;
        }
        int i = ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP[motor];
        byte[] bArr = new byte[CHANNEL_MODE_MASK_SELECTION];
        bArr[0] = (byte) ((int) pid.p);
        bArr[MIN_MOTOR] = (byte) ((int) pid.i);
        bArr[MAX_MOTOR] = (byte) ((int) pid.d);
        write(i, bArr);
    }

    public DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor) {
        m58a(motor);
        DifferentialControlLoopCoefficients differentialControlLoopCoefficients = new DifferentialControlLoopCoefficients();
        byte[] read = read(ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP[motor], CHANNEL_MODE_MASK_SELECTION);
        differentialControlLoopCoefficients.p = (double) read[0];
        differentialControlLoopCoefficients.i = (double) read[MIN_MOTOR];
        differentialControlLoopCoefficients.d = (double) read[MAX_MOTOR];
        return differentialControlLoopCoefficients;
    }

    public static byte runModeToFlag(RunMode mode) {
        switch (C00192.f153a[mode.ordinal()]) {
            case MAX_MOTOR /*2*/:
                return POWER_BREAK;
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
                return CHANNEL_MODE_FLAG_SELECT_RUN_TO_POSITION;
            case CHANNEL_MODE_MASK_LOCK /*4*/:
                return CHANNEL_MODE_FLAG_SELECT_RESET;
            default:
                return CHANNEL_MODE_FLAG_SELECT_RUN_CONSTANT_SPEED;
        }
    }

    public static RunMode flagToRunMode(byte flag) {
        switch (flag & CHANNEL_MODE_MASK_SELECTION) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_PULSE_OUTPUT_TIME /*0*/:
                return RunMode.RUN_WITHOUT_ENCODERS;
            case MIN_MOTOR /*1*/:
                return RunMode.RUN_USING_ENCODERS;
            case MAX_MOTOR /*2*/:
                return RunMode.RUN_TO_POSITION;
            case CHANNEL_MODE_MASK_SELECTION /*3*/:
                return RunMode.RESET_ENCODERS;
            default:
                return RunMode.RUN_WITHOUT_ENCODERS;
        }
    }

    private void m57a() {
        setMotorPowerFloat(MIN_MOTOR);
        setMotorPowerFloat(MAX_MOTOR);
    }

    private void m59b() {
        for (int i = MIN_MOTOR; i <= MAX_MOTOR; i += MIN_MOTOR) {
            write(ADDRESS_MAX_DIFFERENTIAL_CONTROL_LOOP_COEFFICIENT_MAP[i], new byte[]{RATIO_MIN, START_ADDRESS, DEFAULT_D_COEFFICIENT});
        }
    }

    private void m58a(int i) {
        if (i < MIN_MOTOR || i > MAX_MOTOR) {
            Object[] objArr = new Object[MAX_MOTOR];
            objArr[0] = Integer.valueOf(i);
            objArr[MIN_MOTOR] = Integer.valueOf(MAX_MOTOR);
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", objArr));
        }
    }
}
