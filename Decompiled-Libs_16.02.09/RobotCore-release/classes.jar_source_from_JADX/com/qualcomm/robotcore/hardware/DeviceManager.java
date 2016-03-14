package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Map;

public interface DeviceManager {

    public enum DeviceType {
        FTDI_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER,
        MODERN_ROBOTICS_USB_SERVO_CONTROLLER,
        MODERN_ROBOTICS_USB_LEGACY_MODULE,
        MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE,
        MODERN_ROBOTICS_USB_SENSOR_MUX
    }

    ColorSensor createAdafruitI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int i);

    AnalogInput createAnalogInputDevice(AnalogInputController analogInputController, int i);

    OpticalDistanceSensor createAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int i);

    AnalogOutput createAnalogOutputDevice(AnalogOutputController analogOutputController, int i);

    DcMotor createDcMotor(DcMotorController dcMotorController, int i);

    DeviceInterfaceModule createDeviceInterfaceModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    DigitalChannel createDigitalChannelDevice(DigitalChannelController digitalChannelController, int i);

    TouchSensor createDigitalTouchSensor(DeviceInterfaceModule deviceInterfaceModule, int i);

    I2cDevice createI2cDevice(I2cController i2cController, int i);

    IrSeekerSensor createI2cIrSeekerSensorV3(DeviceInterfaceModule deviceInterfaceModule, int i);

    LED createLED(DigitalChannelController digitalChannelController, int i);

    ColorSensor createModernRoboticsI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int i);

    GyroSensor createModernRoboticsI2cGyroSensor(DeviceInterfaceModule deviceInterfaceModule, int i);

    AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int i);

    ColorSensor createNxtColorSensor(LegacyModule legacyModule, int i);

    CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int i);

    DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int i);

    GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int i);

    IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int i);

    LightSensor createNxtLightSensor(LegacyModule legacyModule, int i);

    ServoController createNxtServoController(LegacyModule legacyModule, int i);

    TouchSensor createNxtTouchSensor(LegacyModule legacyModule, int i);

    TouchSensorMultiplexer createNxtTouchSensorMultiplexer(LegacyModule legacyModule, int i);

    UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int i);

    PWMOutput createPwmOutputDevice(DeviceInterfaceModule deviceInterfaceModule, int i);

    Servo createServo(ServoController servoController, int i);

    DcMotorController createUsbDcMotorController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    LegacyModule createUsbLegacyModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    ServoController createUsbServoController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException;
}
