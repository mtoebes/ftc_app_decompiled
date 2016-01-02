package com.qualcomm.hardware;

import android.content.Context;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.configuration.ControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration.ConfigurationType;
import com.qualcomm.robotcore.hardware.configuration.DeviceInterfaceModuleConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MatrixControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.MotorControllerConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ReadXMLFileHandler;
import com.qualcomm.robotcore.hardware.configuration.ServoControllerConfiguration;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.InputStream;
import java.util.List;

public class HardwareFactory {
    private Context context;
    private InputStream inputStream;

    public HardwareFactory(Context context) {
        this.inputStream = null;
        this.context = context;
    }

    public HardwareMap createHardwareMap(EventLoopManager manager) throws RobotCoreException, InterruptedException {
        if (this.inputStream == null) {
            throw new RobotCoreException("XML input stream is null, HardwareFactory cannot create a hardware map");
        }
        HardwareMap hardwareMap = new HardwareMap();
        RobotLog.v("Starting Modern Robotics device manager");
        HardwareDeviceManager hardwareDeviceManager = new HardwareDeviceManager(this.context, manager);
        for (ControllerConfiguration controllerConfiguration : new ReadXMLFileHandler(this.context).parse(this.inputStream)) {
            ConfigurationType type = controllerConfiguration.getType();
            switch (type) {
                case MOTOR_CONTROLLER :
                    createDcMotorController(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case SERVO_CONTROLLER :
                    createServoController(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case LEGACY_MODULE_CONTROLLER :
                    createLegacyModuleController(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case DEVICE_INTERFACE_MODULE :
                    createDeviceInterfaceModule(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                default:
                    RobotLog.w("Unexpected controller type while parsing XML: " + type.toString());
                    break;
            }
        }
        hardwareMap.appContext = this.context;
        return hardwareMap;
    }

    public void setXmlInputStream(InputStream xmlInputStream) {
        this.inputStream = xmlInputStream;
    }

    public InputStream getXmlInputStream() {
        return this.inputStream;
    }

    public static void enableDeviceEmulation() {
        HardwareDeviceManager.enableDeviceEmulation();
    }

    public static void disableDeviceEmulation() {
        HardwareDeviceManager.disableDeviceEmulation();
    }

    private void createDcMotorController(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        ModernRoboticsUsbDcMotorController modernRoboticsUsbDcMotorController = (ModernRoboticsUsbDcMotorController) deviceManager.createUsbDcMotorController(controllerConfiguration.getSerialNumber());
        hardwareMap.dcMotorController.put(controllerConfiguration.getName(), modernRoboticsUsbDcMotorController);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                hardwareMap.dcMotor.put(deviceConfiguration.getName(), deviceManager.createDcMotor(modernRoboticsUsbDcMotorController, deviceConfiguration.getPort()));
            }
        }
        hardwareMap.voltageSensor.put(controllerConfiguration.getName(), modernRoboticsUsbDcMotorController);
    }

    private void createServoController(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        ServoController createUsbServoController = deviceManager.createUsbServoController(controllerConfiguration.getSerialNumber());
        hardwareMap.servoController.put(controllerConfiguration.getName(), createUsbServoController);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                hardwareMap.servo.put(deviceConfiguration.getName(), deviceManager.createServo(createUsbServoController, deviceConfiguration.getPort()));
            }
        }
    }

    private void createDeviceInterfaceModule(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        DeviceInterfaceModule createDeviceInterfaceModule = deviceManager.createDeviceInterfaceModule(controllerConfiguration.getSerialNumber());
        hardwareMap.deviceInterfaceModule.put(controllerConfiguration.getName(), createDeviceInterfaceModule);
        createDeviceInterfaceModule(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getPwmDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        createDeviceInterfaceModule(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getI2cDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        createDeviceInterfaceModule(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getAnalogInputDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        createDeviceInterfaceModule(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getDigitalDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        createDeviceInterfaceModule(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getAnalogOutputDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
    }

    private void createDeviceInterfaceModule(List<DeviceConfiguration> list, HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule) {
        for (DeviceConfiguration deviceConfiguration : list) {
            if (deviceConfiguration.isEnabled()) {
                ConfigurationType type = deviceConfiguration.getType();
                switch (type) {
                    case OPTICAL_DISTANCE_SENSOR :
                        createOpticalDistanceSensor(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ANALOG_INPUT :
                        createAnalogInput(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case TOUCH_SENSOR :
                        createTouchSensor(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case DIGITAL_DEVICE :
                        createDigitalDevice(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case PULSE_WIDTH_DEVICE :
                        createPulseWidthDevice(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case IR_SEEKER_V3 :
                        createIrSeekerV3(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case I2C_DEVICE :
                        createI2CDevice(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ANALOG_OUTPUT :
                        createAnalogOutput(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ADAFRUIT_COLOR_SENSOR :
                        createAdafruitColorSensor(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case LED :
                        createLED(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case COLOR_SENSOR:
                        createColorSensor(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case GYRO :
                        createGyro(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case NOTHING :
                        break;
                    default:
                        RobotLog.w("Unexpected device type connected to Device Interface Module while parsing XML: " + type.toString());
                        break;
                }
            }
        }
    }

    private void createLegacyModuleController(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        LegacyModule createUsbLegacyModule = deviceManager.createUsbLegacyModule(controllerConfiguration.getSerialNumber());
        hardwareMap.legacyModule.put(controllerConfiguration.getName(), createUsbLegacyModule);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                ConfigurationType type = deviceConfiguration.getType();
                switch (type) {
                    case MOTOR_CONTROLLER :
                        createNxtDcMotorController(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case SERVO_CONTROLLER :
                        createNxtServoController(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case TOUCH_SENSOR :
                        createTouchSensor(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case COLOR_SENSOR :
                        createColorSensor(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case GYRO :
                        createGyro(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case NOTHING:
                        break;
                    case COMPASS :
                        createCompass(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case IR_SEEKER:
                        createIrSeeker(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case LIGHT_SENSOR :
                        createLightSensor(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ACCELEROMETER :
                        createAccelerometer(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case TOUCH_SENSOR_MULTIPLEXER :
                        createTouchSensorMultiplexer(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ULTRASONIC_SENSOR :
                        createUltrasonicSensor(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case MATRIX_CONTROLLER :
                        createMatrixController(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    default:
                        RobotLog.w("Unexpected device type connected to Legacy Module while parsing XML: " + type.toString());
                        break;
                }
            }
        }
    }

    private void createIrSeekerV3(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.irSeekerSensor.put(deviceConfiguration.getName(), deviceManager.createI2cIrSeekerSensorV3(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createDigitalDevice(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.digitalChannel.put(deviceConfiguration.getName(), deviceManager.createDigitalChannelDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createTouchSensor(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensor.put(deviceConfiguration.getName(), deviceManager.createDigitalTouchSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createAnalogInput(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.analogInput.put(deviceConfiguration.getName(), deviceManager.createAnalogInputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createPulseWidthDevice(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.pwmOutput.put(deviceConfiguration.getName(), deviceManager.createPwmOutputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createI2CDevice(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.i2cDevice.put(deviceConfiguration.getName(), deviceManager.createI2cDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createAnalogOutput(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.analogOutput.put(deviceConfiguration.getName(), deviceManager.createAnalogOutputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createOpticalDistanceSensor(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.opticalDistanceSensor.put(deviceConfiguration.getName(), deviceManager.createAnalogOpticalDistanceSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createTouchSensor(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensor.put(deviceConfiguration.getName(), deviceManager.createNxtTouchSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createTouchSensorMultiplexer(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensorMultiplexer.put(deviceConfiguration.getName(), deviceManager.createNxtTouchSensorMultiplexer(legacyModule, deviceConfiguration.getPort()));
    }

    private void createUltrasonicSensor(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.ultrasonicSensor.put(deviceConfiguration.getName(), deviceManager.createNxtUltrasonicSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createColorSensor(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createNxtColorSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createGyro(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.gyroSensor.put(deviceConfiguration.getName(), deviceManager.createNxtGyroSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createCompass(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.compassSensor.put(deviceConfiguration.getName(), deviceManager.createNxtCompassSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createIrSeeker(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.irSeekerSensor.put(deviceConfiguration.getName(), deviceManager.createNxtIrSeekerSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createLightSensor(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.lightSensor.put(deviceConfiguration.getName(), deviceManager.createNxtLightSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createAccelerometer(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.accelerationSensor.put(deviceConfiguration.getName(), deviceManager.createNxtAccelerationSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void createNxtDcMotorController(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        DcMotorController createNxtDcMotorController = deviceManager.createNxtDcMotorController(legacyModule, deviceConfiguration.getPort());
        hardwareMap.dcMotorController.put(deviceConfiguration.getName(), createNxtDcMotorController);
        for (DeviceConfiguration deviceConfiguration2 : ((MotorControllerConfiguration) deviceConfiguration).getMotors()) {
            hardwareMap.dcMotor.put(deviceConfiguration2.getName(), deviceManager.createDcMotor(createNxtDcMotorController, deviceConfiguration2.getPort()));
        }
    }

    private void createNxtServoController(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        ServoController createNxtServoController = deviceManager.createNxtServoController(legacyModule, deviceConfiguration.getPort());
        hardwareMap.servoController.put(deviceConfiguration.getName(), createNxtServoController);
        for (DeviceConfiguration deviceConfiguration2 : ((ServoControllerConfiguration) deviceConfiguration).getServos()) {
            hardwareMap.servo.put(deviceConfiguration2.getName(), deviceManager.createServo(createNxtServoController, deviceConfiguration2.getPort()));
        }
    }

    private void createMatrixController(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        MatrixMasterController matrixMasterController = new MatrixMasterController((ModernRoboticsUsbLegacyModule) legacyModule, deviceConfiguration.getPort());
        MatrixDcMotorController matrixDcMotorController = new MatrixDcMotorController(matrixMasterController);
        hardwareMap.dcMotorController.put(deviceConfiguration.getName() + "Motor", matrixDcMotorController);
        hardwareMap.dcMotorController.put(deviceConfiguration.getName(), matrixDcMotorController);
        for (DeviceConfiguration deviceConfiguration2 : ((MatrixControllerConfiguration) deviceConfiguration).getMotors()) {
            hardwareMap.dcMotor.put(deviceConfiguration2.getName(), deviceManager.createDcMotor(matrixDcMotorController, deviceConfiguration2.getPort()));
        }
        MatrixServoController matrixServoController = new MatrixServoController(matrixMasterController);
        hardwareMap.servoController.put(deviceConfiguration.getName() + "Servo", matrixServoController);
        hardwareMap.servoController.put(deviceConfiguration.getName(), matrixServoController);
        for (DeviceConfiguration deviceConfiguration22 : ((MatrixControllerConfiguration) deviceConfiguration).getServos()) {
            hardwareMap.servo.put(deviceConfiguration22.getName(), deviceManager.createServo(matrixServoController, deviceConfiguration22.getPort()));
        }
    }

    private void createAdafruitColorSensor(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createAdafruitI2cColorSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createLED(HardwareMap hardwareMap, DeviceManager deviceManager, DigitalChannelController digitalChannelController, DeviceConfiguration deviceConfiguration) {
        hardwareMap.led.put(deviceConfiguration.getName(), deviceManager.createLED(digitalChannelController, deviceConfiguration.getPort()));
    }

    private void createColorSensor(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createModernRoboticsI2cColorSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void createGyro(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.gyroSensor.put(deviceConfiguration.getName(), deviceManager.createModernRoboticsI2cGyroSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }
}
