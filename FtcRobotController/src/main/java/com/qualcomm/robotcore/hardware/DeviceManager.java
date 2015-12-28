package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor.Direction;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Map;

public abstract class DeviceManager {

    public enum DeviceType {
        FTDI_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER,
        MODERN_ROBOTICS_USB_SERVO_CONTROLLER,
        MODERN_ROBOTICS_USB_LEGACY_MODULE,
        MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE,
        MODERN_ROBOTICS_USB_SENSOR_MUX
    }

    public abstract ColorSensor createAdafruitI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int channel);

    public abstract AnalogInput createAnalogInputDevice(AnalogInputController analogInputController, int channel);

    public abstract OpticalDistanceSensor createAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int channel);

    public abstract AnalogOutput createAnalogOutputDevice(AnalogOutputController analogOutputController, int channel);

    public abstract DeviceInterfaceModule createDeviceInterfaceModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    public abstract DigitalChannel createDigitalChannelDevice(DigitalChannelController digitalChannelController, int channel);

    public abstract TouchSensor createDigitalTouchSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    public abstract I2cDevice createI2cDevice(I2cController i2cController, int channel);

    public abstract IrSeekerSensor createI2cIrSeekerSensorV3(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    public abstract LED createLED(DigitalChannelController digitalChannelController, int channel);

    public abstract ColorSensor createModernRoboticsI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int channel);

    public abstract GyroSensor createModernRoboticsI2cGyroSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    public abstract AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort);

    public abstract ColorSensor createNxtColorSensor(LegacyModule legacyModule, int channel);

    public abstract CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort);

    public abstract DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort);

    public abstract GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort);

    public abstract IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort);

    public abstract LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort);

    public abstract ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort);

    public abstract TouchSensor createNxtTouchSensor(LegacyModule legacyModule, int physicalPort);

    public abstract TouchSensorMultiplexer createNxtTouchSensorMultiplexer(LegacyModule legacyModule, int port);

    public abstract UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort);

    public abstract PWMOutput createPwmOutputDevice(DeviceInterfaceModule deviceInterfaceModule, int channel);

    public abstract DcMotorController createUsbDcMotorController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    public abstract LegacyModule createUsbLegacyModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    public abstract ServoController createUsbServoController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    public abstract Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException;

    public DcMotor createDcMotor(DcMotorController controller, int portNumber) {
        return new DcMotor(controller, portNumber, Direction.FORWARD);
    }

    public Servo createServo(ServoController controller, int portNumber) {
        return new Servo(controller, portNumber, Servo.Direction.FORWARD);
    }
}
