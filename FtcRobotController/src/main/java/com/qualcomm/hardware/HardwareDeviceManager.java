package com.qualcomm.hardware;

import android.content.Context;

import com.qualcomm.modernrobotics.ModernRoboticsUsbUtil;
import com.qualcomm.modernrobotics.RobotUsbManagerEmulator;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.AnalogOutputController;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.hardware.usb.ftdi.RobotUsbManagerFtdi;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.HashMap;
import java.util.Map;

public class HardwareDeviceManager extends DeviceManager {
    private static DeviceEmulation deviceEmulation = DeviceEmulation.DEFAULT;
    private RobotUsbManager robotUsbManager;
    private final EventLoopManager eventLoopManager;

    static class C00001 {
        static final int[] f8a;

        static {
            f8a = new int[DeviceEmulation.values().length];
            try {
                f8a[DeviceEmulation.ENABLE_DEVICE_EMULATION.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }

    private enum DeviceEmulation {
        DEFAULT,
        ENABLE_DEVICE_EMULATION
    }

    public HardwareDeviceManager(Context context, EventLoopManager manager) throws RobotCoreException {
        this.eventLoopManager = manager;
        switch (deviceEmulation) {
            case ENABLE_DEVICE_EMULATION:
                this.robotUsbManager = new RobotUsbManagerEmulator();
            case DEFAULT:
                this.robotUsbManager = new RobotUsbManagerFtdi(context);
        }
    }

    public Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException {
        Map<SerialNumber, DeviceType> hashMap = new HashMap<SerialNumber, DeviceType>();
        try {
            int devicesNum = this.robotUsbManager.scanForDevices();
            for (int deviceIndex = 0; deviceIndex < devicesNum; deviceIndex++) {
                SerialNumber deviceSerialNumber = this.robotUsbManager.getDeviceSerialNumberByIndex(deviceIndex);
                RobotUsbDevice usbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.robotUsbManager, deviceSerialNumber);
                DeviceType usbDeviceType = getUbsDeviceType(usbDevice);
                hashMap.put(deviceSerialNumber, usbDeviceType);
                usbDevice.close();
            }
        } catch (RobotCoreException e) {
            RobotLog.setGlobalErrorMsgAndThrow("Error while scanning for USB devices", e);
        }
        return hashMap;
    }

    public DcMotorController createUsbDcMotorController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        RobotLog.v("Creating Modern Robotics USB DC Motor Controller - " + serialNumber.toString());
        RobotUsbDevice openUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.robotUsbManager, serialNumber);
        if (getUbsDeviceType(openUsbDevice) != DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER) {
            return new ModernRoboticsUsbDcMotorController(serialNumber, openUsbDevice, this.eventLoopManager);
        } else { // device with given SerialNumber is not a UsbDcMotorController
            RobotCoreException e = buildRobotCoreException(openUsbDevice, "Modern Robotics USB DC Motor Controller", serialNumber);
            RobotLog.setGlobalErrorMsgAndThrow("Unable to open Modern Robotics USB DC Motor Controller", e);
            return null;
        }
    }

    public ServoController createUsbServoController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        RobotLog.v("Creating Modern Robotics USB Servo Controller - " + serialNumber.toString());
        RobotUsbDevice openUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.robotUsbManager, serialNumber);
        if (getUbsDeviceType(openUsbDevice) == DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER) {
            return new ModernRoboticsUsbServoController(serialNumber, openUsbDevice, this.eventLoopManager);
        } else { // device with given SerialNumber is not a UsbServoController
            RobotCoreException e = buildRobotCoreException(openUsbDevice, "Modern Robotics USB Servo Controller", serialNumber);
            RobotLog.setGlobalErrorMsgAndThrow("Unable to open Modern Robotics USB Servo Controller", e);
            return null;
        }
    }

    public DeviceInterfaceModule createDeviceInterfaceModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        RobotLog.v("Creating Modern Robotics USB Core Device Interface Module - " + serialNumber.toString());
        RobotUsbDevice openUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.robotUsbManager, serialNumber);
        if (getUbsDeviceType(openUsbDevice) == DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE) {
            return new ModernRoboticsUsbDeviceInterfaceModule(serialNumber, openUsbDevice, this.eventLoopManager);
        } else { // device with given SerialNumber is not a DeviceInterfaceModule
            RobotCoreException e = buildRobotCoreException(openUsbDevice, "Modern Robotics USB Core Device Interface Module", serialNumber);
            RobotLog.setGlobalErrorMsgAndThrow("Unable to open Modern Robotics USB Core Device Interface Module", e);
            return null;
        }
    }

    public LegacyModule createUsbLegacyModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        RobotLog.v("Creating Modern Robotics USB Legacy Module - " + serialNumber.toString());
        RobotUsbDevice openUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.robotUsbManager, serialNumber);

        if (getUbsDeviceType(openUsbDevice) == DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE) {
            return new ModernRoboticsUsbLegacyModule(serialNumber, openUsbDevice, this.eventLoopManager);
        } else { // device with given SerialNumber is not a UsbLegacyModule
            RobotCoreException e = buildRobotCoreException(openUsbDevice, "Modern Robotics USB Legacy Module", serialNumber);
            RobotLog.setGlobalErrorMsgAndThrow("Unable to open Modern Robotics USB Legacy Module", e);
            return null;
        }
    }

    public DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT DC Motor Controller - Port: " + physicalPort);
        return new HiTechnicNxtDcMotorController(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Servo Controller - Port: " + physicalPort);
        return new HiTechnicNxtServoController(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Compass Sensor - Port: " + physicalPort);
        return new HiTechnicNxtCompassSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public TouchSensor createDigitalTouchSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics Digital Touch Sensor - Port: " + physicalPort);
        return new ModernRoboticsDigitalTouchSensor(castModernRoboticsUsbDeviceInterfaceModule(deviceInterfaceModule), physicalPort);
    }

    public AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Acceleration Sensor - Port: " + physicalPort);
        return new HiTechnicNxtAccelerationSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Light Sensor - Port: " + physicalPort);
        return new HiTechnicNxtLightSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Gyro Sensor - Port: " + physicalPort);
        return new HiTechnicNxtGyroSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT IR Seeker Sensor - Port: " + physicalPort);
        return new HiTechnicNxtIrSeekerSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public IrSeekerSensor createI2cIrSeekerSensorV3(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics I2C IR Seeker Sensor V3 - Port: " + physicalPort);
        return new ModernRoboticsI2cIrSeekerSensorV3(castModernRoboticsUsbDeviceInterfaceModule(deviceInterfaceModule), physicalPort);
    }

    public UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Ultrasonic Sensor - Port: " + physicalPort);
        return new HiTechnicNxtUltrasonicSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public OpticalDistanceSensor createAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics Analog Optical Distance Sensor - Port: " + physicalPort);
        return new ModernRoboticsAnalogOpticalDistanceSensor(castModernRoboticsUsbDeviceInterfaceModule(deviceInterfaceModule), physicalPort);
    }

    public TouchSensor createNxtTouchSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Touch Sensor - Port: " + physicalPort);
        return new HiTechnicNxtTouchSensor(castModernRoboticsUsbLegacyModule(legacyModule), physicalPort);
    }

    public TouchSensorMultiplexer createNxtTouchSensorMultiplexer(LegacyModule legacyModule, int port) {
        RobotLog.v("Creating HiTechnic NXT Touch Sensor Multiplexer - Port: " + port);
        return new HiTechnicNxtTouchSensorMultiplexer(castModernRoboticsUsbLegacyModule(legacyModule), port);
    }

    public AnalogInput createAnalogInputDevice(AnalogInputController controller, int channel) {
        RobotLog.v("Creating Analog Input Device - Port: " + channel);
        return new AnalogInput(controller, channel);
    }

    public AnalogOutput createAnalogOutputDevice(AnalogOutputController controller, int channel) {
        RobotLog.v("Creating Analog Output Device - Port: " + channel);
        return new AnalogOutput(controller, channel);
    }

    public DigitalChannel createDigitalChannelDevice(DigitalChannelController controller, int channel) {
        RobotLog.v("Creating Digital Channel Device - Port: " + channel);
        return new DigitalChannel(controller, channel);
    }

    public PWMOutput createPwmOutputDevice(DeviceInterfaceModule controller, int channel) {
        RobotLog.v("Creating PWM Output Device - Port: " + channel);
        return new PWMOutput(controller, channel);
    }

    public I2cDevice createI2cDevice(I2cController controller, int channel) {
        RobotLog.v("Creating I2C Device - Port: " + channel);
        return new I2cDevice(controller, channel);
    }

    public ColorSensor createAdafruitI2cColorSensor(DeviceInterfaceModule controller, int channel) {
        RobotLog.v("Creating Adafruit I2C Color Sensor - Port: " + channel);
        return new AdafruitI2cColorSensor(controller, channel);
    }

    public ColorSensor createNxtColorSensor(LegacyModule controller, int channel) {
        RobotLog.v("Creating HiTechnic NXT Color Sensor - Port: " + channel);
        return new HiTechnicNxtColorSensor(controller, channel);
    }

    public ColorSensor createModernRoboticsI2cColorSensor(DeviceInterfaceModule controller, int channel) {
        RobotLog.v("Creating Modern Robotics I2C Color Sensor - Port: " + channel);
        return new ModernRoboticsI2cColorSensor(controller, channel);
    }

    public GyroSensor createModernRoboticsI2cGyroSensor(DeviceInterfaceModule controller, int channel) {
        RobotLog.v("Creating Modern Robotics I2C Gyro Sensor - Port: " + channel);
        return new ModernRoboticsI2cGyro(controller, channel);
    }

    public LED createLED(DigitalChannelController controller, int channel) {
        RobotLog.v("Creating LED - Port: " + channel);
        return new LED(controller, channel);
    }

    public static void enableDeviceEmulation() {
        deviceEmulation = DeviceEmulation.ENABLE_DEVICE_EMULATION;
    }

    public static void disableDeviceEmulation() {
        deviceEmulation = DeviceEmulation.DEFAULT;
    }

    private ModernRoboticsUsbLegacyModule castModernRoboticsUsbLegacyModule(LegacyModule legacyModule) {
        if (legacyModule instanceof ModernRoboticsUsbLegacyModule) {
            return (ModernRoboticsUsbLegacyModule) legacyModule;
        } else {
            throw new IllegalArgumentException("Modern Robotics Device Manager needs a Modern Robotics LegacyModule");
        }
    }

    private ModernRoboticsUsbDeviceInterfaceModule castModernRoboticsUsbDeviceInterfaceModule(DeviceInterfaceModule deviceInterfaceModule) {
        if (deviceInterfaceModule instanceof ModernRoboticsUsbDeviceInterfaceModule) {
            return (ModernRoboticsUsbDeviceInterfaceModule) deviceInterfaceModule;
        } else {
            throw new IllegalArgumentException("Modern Robotics Device Manager needs a Modern Robotics Device Interface Module");
        }
    }

    private RobotCoreException buildRobotCoreException(RobotUsbDevice robotUsbDevice, String errorMessage, SerialNumber serialNumber) {
        String exceptionMessage = errorMessage + " [" + serialNumber + "] is returning garbage data via the USB bus";
        robotUsbDevice.close();
        System.err.println(exceptionMessage);
        return new RobotCoreException(exceptionMessage);
    }

    private DeviceType getUbsDeviceType(RobotUsbDevice usbDevice) throws RobotCoreException {
        byte[] usbDeviceHeader = ModernRoboticsUsbUtil.getUsbDeviceHeader(usbDevice);
        return ModernRoboticsUsbUtil.getDeviceType(usbDeviceHeader);
    }
}
