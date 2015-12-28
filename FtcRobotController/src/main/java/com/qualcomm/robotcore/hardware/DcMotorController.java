package com.qualcomm.robotcore.hardware;

public interface DcMotorController extends HardwareDevice {

    enum DeviceMode {
        SWITCHING_TO_READ_MODE,
        SWITCHING_TO_WRITE_MODE,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE
    }

    enum RunMode {
        RUN_USING_ENCODERS,
        RUN_WITHOUT_ENCODERS,
        RUN_TO_POSITION,
        RESET_ENCODERS
    }

    RunMode getMotorChannelMode(int motor);

    DeviceMode getMotorControllerDeviceMode();

    int getMotorCurrentPosition(int motor);

    double getMotorPower(int motor);

    boolean getMotorPowerFloat(int motor);

    int getMotorTargetPosition(int motor);

    boolean isBusy(int motor);

    void setMotorChannelMode(int motor, RunMode runMode);

    void setMotorControllerDeviceMode(DeviceMode deviceMode);

    void setMotorPower(int motor, double power);

    void setMotorPowerFloat(int motor);

    void setMotorTargetPosition(int motor, int position);
}
