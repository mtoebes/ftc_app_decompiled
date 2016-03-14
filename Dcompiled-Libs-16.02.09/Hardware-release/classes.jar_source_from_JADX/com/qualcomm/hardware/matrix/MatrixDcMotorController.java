package com.qualcomm.hardware.matrix;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtCompassSensor;
import com.qualcomm.hardware.matrix.MatrixI2cTransaction.C0012a;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
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
    private C0010a[] f75a;
    private int f76b;
    protected DeviceMode deviceMode;
    protected MatrixMasterController master;

    /* renamed from: com.qualcomm.hardware.matrix.MatrixDcMotorController.1 */
    static /* synthetic */ class C00091 {
        static final /* synthetic */ int[] f67a;

        static {
            f67a = new int[RunMode.values().length];
            try {
                f67a[RunMode.RUN_USING_ENCODERS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f67a[RunMode.RUN_WITHOUT_ENCODERS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f67a[RunMode.RUN_TO_POSITION.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f67a[RunMode.RESET_ENCODERS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.matrix.MatrixDcMotorController.a */
    private class C0010a {
        public int f68a;
        public int f69b;
        public byte f70c;
        public boolean f71d;
        public double f72e;
        public RunMode f73f;
        final /* synthetic */ MatrixDcMotorController f74g;

        public C0010a(MatrixDcMotorController matrixDcMotorController) {
            this.f74g = matrixDcMotorController;
            this.f68a = 0;
            this.f69b = 0;
            this.f70c = (byte) 0;
            this.f72e = 0.0d;
            this.f71d = true;
            this.f73f = RunMode.RESET_ENCODERS;
        }
    }

    public MatrixDcMotorController(MatrixMasterController master) {
        this.f75a = new C0010a[]{new C0010a(this), new C0010a(this), new C0010a(this), new C0010a(this), new C0010a(this)};
        this.master = master;
        this.f76b = 0;
        master.registerMotorController(this);
        for (int i = 0; i < 4; i = (byte) (i + 1)) {
            master.queueTransaction(new MatrixI2cTransaction(i, (byte) 0, 0, (byte) 0));
            this.f75a[i].f73f = RunMode.RUN_WITHOUT_ENCODERS;
            this.f75a[i].f71d = true;
        }
        this.deviceMode = DeviceMode.READ_ONLY;
    }

    protected byte runModeToFlagMatrix(RunMode mode) {
        switch (C00091.f67a[mode.ordinal()]) {
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
        MatrixI2cTransaction matrixI2cTransaction = new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_MODE);
        this.master.queueTransaction(matrixI2cTransaction);
        this.master.waitOnRead();
        if ((this.f75a[matrixI2cTransaction.motor].f70c & ModernRoboticsUsbDeviceInterfaceModule.D7_MASK) != 0) {
            return true;
        }
        return false;
    }

    public void setMotorControllerDeviceMode(DeviceMode mode) {
        this.deviceMode = mode;
    }

    public DeviceMode getMotorControllerDeviceMode() {
        return this.deviceMode;
    }

    public void setMotorChannelMode(int motor, RunMode mode) {
        m52a(motor);
        if (this.f75a[motor].f71d || this.f75a[motor].f73f != mode) {
            this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_MODE, runModeToFlagMatrix(mode)));
            this.f75a[motor].f73f = mode;
            if (mode == RunMode.RESET_ENCODERS) {
                this.f75a[motor].f71d = true;
            } else {
                this.f75a[motor].f71d = false;
            }
        }
    }

    public RunMode getMotorChannelMode(int motor) {
        m52a(motor);
        return this.f75a[motor].f73f;
    }

    public void setMotorPowerFloat(int motor) {
        m52a(motor);
        if (!this.f75a[motor].f71d) {
            this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_MODE, 4));
        }
        this.f75a[motor].f71d = true;
    }

    public boolean getMotorPowerFloat(int motor) {
        m52a(motor);
        return this.f75a[motor].f71d;
    }

    public void setMotorPower(Set<DcMotor> motors, double power) {
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        for (DcMotor dcMotor : motors) {
            byte b = (byte) ((int) (100.0d * power));
            if (dcMotor.getDirection() == Direction.REVERSE) {
                b = (byte) (b * -1);
            }
            int portNumber = dcMotor.getPortNumber();
            this.master.queueTransaction(new MatrixI2cTransaction((byte) portNumber, b, this.f75a[portNumber].f68a, (byte) (runModeToFlagMatrix(this.f75a[portNumber].f73f) | 8)));
        }
        this.master.queueTransaction(new MatrixI2cTransaction((byte) 0, C0012a.PROPERTY_START, 1));
    }

    public void setMotorPower(int motor, double power) {
        m52a(motor);
        Range.throwIfRangeIsInvalid(power, HiTechnicNxtCompassSensor.INVALID_DIRECTION, 1.0d);
        this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, (byte) ((int) (100.0d * power)), this.f75a[motor].f68a, runModeToFlagMatrix(this.f75a[motor].f73f)));
        this.f75a[motor].f72e = power;
    }

    public double getMotorPower(int motor) {
        m52a(motor);
        return this.f75a[motor].f72e;
    }

    public void setMotorTargetPosition(int motor, int position) {
        m52a(motor);
        this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_TARGET, position));
        this.f75a[motor].f68a = position;
    }

    public int getMotorTargetPosition(int motor) {
        m52a(motor);
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_TARGET))) {
            this.master.waitOnRead();
        }
        return this.f75a[motor].f68a;
    }

    public int getMotorCurrentPosition(int motor) {
        m52a(motor);
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) motor, C0012a.PROPERTY_POSITION))) {
            this.master.waitOnRead();
        }
        return this.f75a[motor].f69b;
    }

    public int getBattery() {
        if (this.master.queueTransaction(new MatrixI2cTransaction((byte) 0, C0012a.PROPERTY_BATTERY))) {
            this.master.waitOnRead();
        }
        return this.f76b;
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
        this.f76b = TypeConversion.unsignedByteToInt(buffer[4]) * 40;
        RobotLog.v("Battery voltage: " + this.f76b + "mV");
    }

    public void handleReadPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f75a[transaction.motor].f69b = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, 4, 8));
        RobotLog.v("Position motor: " + transaction.motor + " " + this.f75a[transaction.motor].f69b);
    }

    public void handleReadTargetPosition(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f75a[transaction.motor].f68a = TypeConversion.byteArrayToInt(Arrays.copyOfRange(buffer, 4, 8));
        RobotLog.v("Target motor: " + transaction.motor + " " + this.f75a[transaction.motor].f68a);
    }

    public void handleReadMode(MatrixI2cTransaction transaction, byte[] buffer) {
        this.f75a[transaction.motor].f70c = buffer[4];
        RobotLog.v("Mode: " + this.f75a[transaction.motor].f70c);
    }

    private void m52a(int i) {
        if (i < 1 || i > 4) {
            throw new IllegalArgumentException(String.format("Motor %d is invalid; valid motors are 1..%d", new Object[]{Integer.valueOf(i), Integer.valueOf(4)}));
        }
    }
}
