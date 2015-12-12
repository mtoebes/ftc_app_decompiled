package com.qualcomm.robotcore.hardware;

public interface DcMotorController extends HardwareDevice {

    public enum DeviceMode {
        SWITCHING_TO_READ_MODE,
        SWITCHING_TO_WRITE_MODE,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE
    }

    public enum RunMode {
        RUN_USING_ENCODERS,
        RUN_WITHOUT_ENCODERS,
        RUN_TO_POSITION,
        RESET_ENCODERS
    }

    RunMode getMotorChannelMode(int i);

    DeviceMode getMotorControllerDeviceMode();

    int getMotorCurrentPosition(int i);

    double getMotorPower(int i);

    boolean getMotorPowerFloat(int i);

    int getMotorTargetPosition(int i);

    boolean isBusy(int i);

    void setMotorChannelMode(int i, RunMode runMode);

    void setMotorControllerDeviceMode(DeviceMode deviceMode);

    void setMotorPower(int i, double d);

    void setMotorPowerFloat(int i);

    void setMotorTargetPosition(int i, int i2);
}
