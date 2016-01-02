package com.qualcomm.hardware;

import com.qualcomm.hardware.MatrixI2cTransaction.C0008a;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.Direction;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorController.DeviceMode;
import com.qualcomm.robotcore.hardware.DcMotorController.RunMode;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;
import java.util.Arrays;
import java.util.Set;

public class MatrixDcMotorController implements DcMotorController {
    public static final byte POWER_MAX = (byte) 100;
    public static final byte POWER_MIN = (byte) -100;
    private C0006a[] f90a;
    private int f91b;
    protected DeviceMode deviceMode;
    protected MatrixMasterController master;

    /* renamed from: com.qualcomm.hardware.MatrixDcMotorController.1 */
    static /* synthetic */ class C00051 {
        static final /* synthetic */ int[] f82a;

        static {
            f82a = new int[RunMode.values().length];
            try {
                f82a[RunMode.RUN_USING_ENCODERS.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f82a[RunMode.RUN_WITHOUT_ENCODERS.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f82a[RunMode.RUN_TO_POSITION.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }
            try {
                f82a[RunMode.RESET_ENCODERS.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.MatrixDcMotorController.a */
    private class C0006a {
        public int f83a;
        public int f84b;
        public byte f85c;
        public boolean f86d;
        public double f87e;
        public RunMode f88f;
        final /* synthetic */ MatrixDcMotorController f89g;

        public C0006a(MatrixDcMotorController matrixDcMotorController) {
            this.f89g = matrixDcMotorController;
            this.f83a = 0;
            this.f84b = 0;
            this.f85c = (byte) 0;
            this.f87e = 0.0d;
            this.f86d = true;
            this.f88f = RunMode.RESET_ENCODERS;
        }
    }

    public MatrixDcMotorController(MatrixMasterController master) {
        this.f90a = new C0006a[]{new C0006a(this), new C0006a(this), new C0006a(this), new C0006a(this), new C0006a(this)};
        this.master = master;
        this.f91b = 0;
        master.registerMotorController(this);
        for (int i = 0; i < 4; i = (byte) (i + 1)) {
            master.queueTransaction(new MatrixI2cTransaction((byte) i, (byte) 0, 0, (byte) 0));
            this.f90a[i].f88f = RunMode.RUN_WITHOUT_ENCODERS;
            this.f90a[i].f86d = true;
        }
        this.deviceMode = DeviceMode.READ_ONLY;
    }

    protected byte runModeToFlagMatrix(RunMode mode) {
        switch (C00051.f82a[mode.ordinal()]) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                return (byte) 2;
            case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                return (byte) 1;
            case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                return (byte) 3;
            default:
                return (byte) 4;
        }
    }

    protected RunMode flagMatrixToRunMode(byte flag) {
        switch (flag) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                return RunMode.RUN_WITHOUT_ENCODERS;
            case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                return RunMode.RUN_USING_ENCODERS;
            case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                return RunMode.RUN_TO_POSITION;
            case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
                return RunMode.RESET_ENCODERS;
            default:
                RobotLog.e("Invalid run mode flag " + flag);
                return RunMode.RUN_WITHOUT_ENCODERS;
        }
    }

    public boolean isBusy(int motor) {
        MatrixI2cTransaction matrixI2cTransaction = new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_MODE);
        this.master.queueTransaction(matrixI2cTransaction);
        this.master.waitOnRead();
        return (this.f90a[matrixI2cTransaction.motor].f85c & ModernRoboticsUsbDeviceInterfaceModule.D7_MASK) != 0;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        this.deviceMode = mode;
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.deviceMode;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        m49a(motor);
        if (this.f90a[motor].f86d || this.f90a[motor].f88f != mode) {
            this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_MODE, runModeToFlagMatrix(mode)));
            this.f90a[motor].f88f = mode;
            this.f90a[motor].f86d = mode == RunMode.RESET_ENCODERS;
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        m49a(motor);
        return this.f90a[motor].f88f;
    }

    public void setMotorPowerFloat(int motor) {
        m49a(motor);
        if (!this.f90a[motor].f86d) {
            this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_MODE, 4));
        }
        this.f90a[motor].f86d = true;
    }

    public boolean getMotorPowerFloat(int motor) {
        m49a(motor);
        return this.f90a[motor].f86d;
    }

    public void setMotorPower(Set<DcMotor> motors, double power) {
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        for (DcMotor dcMotor : motors) {
            byte b = (byte) ((int) (100.0d * power));
            if (dcMotor.getDirection() == Direction.REVERSE) {
                b = (byte) (b * -1);
            }
            int portNumber = dcMotor.getPortNumber();
            this.master.queueTransaction(new MatrixI2cTransaction((byte) portNumber, b, this.f90a[portNumber].f83a, (byte) (runModeToFlagMatrix(this.f90a[portNumber].f88f) | 8)));
        }
        this.master.queueTransaction(new MatrixI2cTransaction((byte) 0, C0008a.PROPERTY_START, 1));
    }

    public void setMotorPower(int motor, double power) {
        m49a(motor);
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, (byte) ((int) (100.0d * power)), this.f90a[motor].f83a, runModeToFlagMatrix(this.f90a[motor].f88f)));
        this.f90a[motor].f87e = power;
    }

    public double getMotorPower(int motor) {
        m49a(motor);
        return this.f90a[motor].f87e;
    }

    public void setMotorTargetPosition(int motor, int position) {
        m49a(motor);
        this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_TARGET, position));
        this.f90a[motor].f83a = position;
    }

    public int getMotorTargetPosition(int motor) {
        m49a(motor);
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_TARGET))) {
            this.master.waitOnRead();
        }
        return this.f90a[motor].f83a;
    }

    public int getMotorCurrentPosition(int motor) {
        m49a(motor);
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0008a.PROPERTY_POSITION))) {
            this.master.waitOnRead();
        }
        return this.f90a[motor].f84b;
    }

    public int getBattery() {
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) 0, C0008a.PROPERTY_BATTERY))) {
            this.master.waitOnRead();
        }
        return this.f91b;
    }

    public String getDeviceName() {
        return "Matrix Motor Controller";
    }

    public String getConnectionInfo() {
        return this.master.getConnectionInfo();
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
        setMotorPowerFloat(1);
        setMotorPowerFloat(2);
        setMotorPowerFloat(3);
        setMotorPowerFloat(4);
    }

    public void handleReadBattery(byte[] buffer) {
        this.f91b = TypeConversion.unsignedByteToInt(buffer[4]) * 40;
        RobotLog.v("Battery voltage: " + this.f91b + "mV");
    }

    public void handleReadPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f90a[transaction.motor].f84b = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, 4, 8));
        RobotLog.v("Position motor: " + transaction.motor + " " + this.f90a[transaction.motor].f84b);
    }

    public void handleReadTargetPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f90a[transaction.motor].f83a = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, 4, 8));
        RobotLog.v("Target motor: " + transaction.motor + " " + this.f90a[transaction.motor].f83a);
    }

    public void handleReadMode(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f90a[transaction.motor].f85c = buffer[4];
        RobotLog.v("Mode: " + this.f90a[transaction.motor].f85c);
    }

    private void m49a(int i) {
        if (i < 1 || i > 4) {
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", new Object[]{i, 4}));
        }
    }
}
