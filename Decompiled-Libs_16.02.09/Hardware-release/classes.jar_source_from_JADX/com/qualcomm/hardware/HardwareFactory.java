package com.qualcomm.hardware;

import android.content.Context;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.hardware.matrix.MatrixDcMotorController;
import com.qualcomm.hardware.matrix.MatrixMasterController;
import com.qualcomm.hardware.matrix.MatrixServoController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
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
import com.qualcomm.robotcore.util.SerialNumber;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class HardwareFactory {
    protected static final HashMap<String, String> serialNumberDisplayNames;
    private Context f17a;
    private InputStream f18b;

    /* renamed from: com.qualcomm.hardware.HardwareFactory.1 */
    static /* synthetic */ class C00061 {
        static final /* synthetic */ int[] f16a;

        static {
            f16a = new int[ConfigurationType.values().length];
            try {
                f16a[ConfigurationType.MOTOR_CONTROLLER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f16a[ConfigurationType.SERVO_CONTROLLER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f16a[ConfigurationType.LEGACY_MODULE_CONTROLLER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f16a[ConfigurationType.DEVICE_INTERFACE_MODULE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f16a[ConfigurationType.OPTICAL_DISTANCE_SENSOR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f16a[ConfigurationType.ANALOG_INPUT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f16a[ConfigurationType.TOUCH_SENSOR.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f16a[ConfigurationType.DIGITAL_DEVICE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f16a[ConfigurationType.PULSE_WIDTH_DEVICE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f16a[ConfigurationType.IR_SEEKER_V3.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f16a[ConfigurationType.I2C_DEVICE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                f16a[ConfigurationType.ANALOG_OUTPUT.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f16a[ConfigurationType.ADAFRUIT_COLOR_SENSOR.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f16a[ConfigurationType.LED.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                f16a[ConfigurationType.COLOR_SENSOR.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                f16a[ConfigurationType.GYRO.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                f16a[ConfigurationType.NOTHING.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                f16a[ConfigurationType.COMPASS.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                f16a[ConfigurationType.IR_SEEKER.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                f16a[ConfigurationType.LIGHT_SENSOR.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                f16a[ConfigurationType.ACCELEROMETER.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                f16a[ConfigurationType.TOUCH_SENSOR_MULTIPLEXER.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                f16a[ConfigurationType.ULTRASONIC_SENSOR.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                f16a[ConfigurationType.MATRIX_CONTROLLER.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
        }
    }

    static {
        serialNumberDisplayNames = new HashMap();
    }

    public HardwareFactory(Context context) {
        this.f18b = null;
        this.f17a = context;
    }

    public HardwareMap createHardwareMap(EventLoopManager manager) throws RobotCoreException, InterruptedException {
        if (this.f18b == null) {
            throw new RobotCoreException("XML input stream is null, HardwareFactory cannot create a hardware map");
        }
        HardwareMap hardwareMap = new HardwareMap(this.f17a);
        RobotLog.v("Starting Modern Robotics device manager");
        HardwareDeviceManager hardwareDeviceManager = new HardwareDeviceManager(this.f17a, manager);
        for (ControllerConfiguration controllerConfiguration : new ReadXMLFileHandler(this.f17a).parse(this.f18b)) {
            ConfigurationType type = controllerConfiguration.getType();
            switch (C00061.f16a[type.ordinal()]) {
                case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                    m9a(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                    m13b(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                    m19d(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
                    m16c(hardwareMap, hardwareDeviceManager, controllerConfiguration);
                    break;
                default:
                    RobotLog.w("Unexpected controller type while parsing XML: " + type.toString());
                    break;
            }
        }
        return hardwareMap;
    }

    public void setXmlInputStream(InputStream xmlInputStream) {
        this.f18b = xmlInputStream;
    }

    public InputStream getXmlInputStream() {
        return this.f18b;
    }

    public static void enableDeviceEmulation() {
        HardwareDeviceManager.enableDeviceEmulation();
    }

    public static void disableDeviceEmulation() {
        HardwareDeviceManager.disableDeviceEmulation();
    }

    private void m9a(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        ModernRoboticsUsbDcMotorController modernRoboticsUsbDcMotorController = (ModernRoboticsUsbDcMotorController) deviceManager.createUsbDcMotorController(controllerConfiguration.getSerialNumber());
        hardwareMap.dcMotorController.put(controllerConfiguration.getName(), modernRoboticsUsbDcMotorController);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                hardwareMap.dcMotor.put(deviceConfiguration.getName(), deviceManager.createDcMotor(modernRoboticsUsbDcMotorController, deviceConfiguration.getPort()));
            }
        }
        hardwareMap.voltageSensor.put(controllerConfiguration.getName(), modernRoboticsUsbDcMotorController);
    }

    private void m13b(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        ServoController createUsbServoController = deviceManager.createUsbServoController(controllerConfiguration.getSerialNumber());
        hardwareMap.servoController.put(controllerConfiguration.getName(), createUsbServoController);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                hardwareMap.servo.put(deviceConfiguration.getName(), deviceManager.createServo(createUsbServoController, deviceConfiguration.getPort()));
            }
        }
    }

    private void m16c(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        DeviceInterfaceModule createDeviceInterfaceModule = deviceManager.createDeviceInterfaceModule(controllerConfiguration.getSerialNumber());
        hardwareMap.deviceInterfaceModule.put(controllerConfiguration.getName(), createDeviceInterfaceModule);
        m10a(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getPwmDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        m10a(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getI2cDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        m10a(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getAnalogInputDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        m10a(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getDigitalDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
        m10a(((DeviceInterfaceModuleConfiguration) controllerConfiguration).getAnalogOutputDevices(), hardwareMap, deviceManager, createDeviceInterfaceModule);
    }

    private void m10a(List<DeviceConfiguration> list, HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule) {
        for (DeviceConfiguration deviceConfiguration : list) {
            if (deviceConfiguration.isEnabled()) {
                ConfigurationType type = deviceConfiguration.getType();
                switch (C00061.f16a[type.ordinal()]) {
                    case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
                        m26h(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbServoController.MAX_SERVOS /*6*/:
                        m17d(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.MAX_ANALOG_PORT_NUMBER /*7*/:
                        m14c(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S2 /*8*/:
                        m11b(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbServoController.MONITOR_LENGTH /*9*/:
                        m20e(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S3 /*10*/:
                        m6a(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case HiTechnicNxtDcMotorController.OFFSET_MOTOR2_MODE /*11*/:
                        m22f(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S4 /*12*/:
                        m24g(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.MONITOR_LENGTH /*13*/:
                        m28i(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S5 /*14*/:
                        m7a(hardwareMap, deviceManager, (DigitalChannelController) deviceInterfaceModule, deviceConfiguration);
                        break;
                    case 15:
                        m30j(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_I2C_PORT_SO /*16*/:
                        m32k(hardwareMap, deviceManager, deviceInterfaceModule, deviceConfiguration);
                        break;
                    case 17:
                        break;
                    default:
                        RobotLog.w("Unexpected device type connected to Device Interface Module while parsing XML: " + type.toString());
                        break;
                }
            }
        }
    }

    private void m19d(HardwareMap hardwareMap, DeviceManager deviceManager, ControllerConfiguration controllerConfiguration) throws RobotCoreException, InterruptedException {
        LegacyModule createUsbLegacyModule = deviceManager.createUsbLegacyModule(controllerConfiguration.getSerialNumber());
        hardwareMap.legacyModule.put(controllerConfiguration.getName(), createUsbLegacyModule);
        for (DeviceConfiguration deviceConfiguration : controllerConfiguration.getDevices()) {
            if (deviceConfiguration.isEnabled()) {
                ConfigurationType type = deviceConfiguration.getType();
                switch (C00061.f16a[type.ordinal()]) {
                    case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                        m31j(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                        m33k(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.MAX_ANALOG_PORT_NUMBER /*7*/:
                        m8a(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case 15:
                        m18d(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbLegacyModule.ADDRESS_I2C_PORT_SO /*16*/:
                        m21e(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case 17:
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.ADDRESS_ANALOG_PORT_A7 /*18*/:
                        m23f(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case 19:
                        m25g(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.ADDRESS_DIGITAL_INPUT_STATE /*20*/:
                        m27h(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.MONITOR_LENGTH /*21*/:
                        m29i(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.ADDRESS_DIGITAL_OUTPUT_STATE /*22*/:
                        m12b(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.ADDRESS_LED_SET /*23*/:
                        m15c(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    case ModernRoboticsUsbDeviceInterfaceModule.ADDRESS_VOLTAGE_OUTPUT_PORT_0 /*24*/:
                        m34l(hardwareMap, deviceManager, createUsbLegacyModule, deviceConfiguration);
                        break;
                    default:
                        RobotLog.w("Unexpected device type connected to Legacy Module while parsing XML: " + type.toString());
                        break;
                }
            }
        }
    }

    private void m6a(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.irSeekerSensor.put(deviceConfiguration.getName(), deviceManager.createI2cIrSeekerSensorV3(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m11b(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.digitalChannel.put(deviceConfiguration.getName(), deviceManager.createDigitalChannelDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m14c(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensor.put(deviceConfiguration.getName(), deviceManager.createDigitalTouchSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m17d(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.analogInput.put(deviceConfiguration.getName(), deviceManager.createAnalogInputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m20e(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.pwmOutput.put(deviceConfiguration.getName(), deviceManager.createPwmOutputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m22f(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.i2cDevice.put(deviceConfiguration.getName(), deviceManager.createI2cDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m24g(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.analogOutput.put(deviceConfiguration.getName(), deviceManager.createAnalogOutputDevice(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m26h(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.opticalDistanceSensor.put(deviceConfiguration.getName(), deviceManager.createAnalogOpticalDistanceSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m8a(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensor.put(deviceConfiguration.getName(), deviceManager.createNxtTouchSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m12b(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.touchSensorMultiplexer.put(deviceConfiguration.getName(), deviceManager.createNxtTouchSensorMultiplexer(legacyModule, deviceConfiguration.getPort()));
    }

    private void m15c(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.ultrasonicSensor.put(deviceConfiguration.getName(), deviceManager.createNxtUltrasonicSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m18d(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createNxtColorSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m21e(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.gyroSensor.put(deviceConfiguration.getName(), deviceManager.createNxtGyroSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m23f(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.compassSensor.put(deviceConfiguration.getName(), deviceManager.createNxtCompassSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m25g(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.irSeekerSensor.put(deviceConfiguration.getName(), deviceManager.createNxtIrSeekerSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m27h(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.lightSensor.put(deviceConfiguration.getName(), deviceManager.createNxtLightSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m29i(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.accelerationSensor.put(deviceConfiguration.getName(), deviceManager.createNxtAccelerationSensor(legacyModule, deviceConfiguration.getPort()));
    }

    private void m31j(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        DcMotorController createNxtDcMotorController = deviceManager.createNxtDcMotorController(legacyModule, deviceConfiguration.getPort());
        hardwareMap.dcMotorController.put(deviceConfiguration.getName(), createNxtDcMotorController);
        for (DeviceConfiguration deviceConfiguration2 : ((MotorControllerConfiguration) deviceConfiguration).getMotors()) {
            hardwareMap.dcMotor.put(deviceConfiguration2.getName(), deviceManager.createDcMotor(createNxtDcMotorController, deviceConfiguration2.getPort()));
        }
    }

    private void m33k(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
        ServoController createNxtServoController = deviceManager.createNxtServoController(legacyModule, deviceConfiguration.getPort());
        hardwareMap.servoController.put(deviceConfiguration.getName(), createNxtServoController);
        for (DeviceConfiguration deviceConfiguration2 : ((ServoControllerConfiguration) deviceConfiguration).getServos()) {
            hardwareMap.servo.put(deviceConfiguration2.getName(), deviceManager.createServo(createNxtServoController, deviceConfiguration2.getPort()));
        }
    }

    private void m34l(HardwareMap hardwareMap, DeviceManager deviceManager, LegacyModule legacyModule, DeviceConfiguration deviceConfiguration) {
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

    private void m28i(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createAdafruitI2cColorSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m7a(HardwareMap hardwareMap, DeviceManager deviceManager, DigitalChannelController digitalChannelController, DeviceConfiguration deviceConfiguration) {
        hardwareMap.led.put(deviceConfiguration.getName(), deviceManager.createLED(digitalChannelController, deviceConfiguration.getPort()));
    }

    private void m30j(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.colorSensor.put(deviceConfiguration.getName(), deviceManager.createModernRoboticsI2cColorSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    private void m32k(HardwareMap hardwareMap, DeviceManager deviceManager, DeviceInterfaceModule deviceInterfaceModule, DeviceConfiguration deviceConfiguration) {
        hardwareMap.gyroSensor.put(deviceConfiguration.getName(), deviceManager.createModernRoboticsI2cGyroSensor(deviceInterfaceModule, deviceConfiguration.getPort()));
    }

    public static void noteSerialNumberType(SerialNumber serialNumber, String typeName) {
        noteSerialNumberType(serialNumber.toString(), typeName);
    }

    public static void noteSerialNumberType(String serialNumber, String typeName) {
        synchronized (serialNumberDisplayNames) {
            serialNumberDisplayNames.put(serialNumber, String.format("%s [%s]", new Object[]{typeName, serialNumber}));
        }
    }

    public static String getSerialNumberDisplayName(SerialNumber serialNumber) {
        return getSerialNumberDisplayName(serialNumber.toString());
    }

    public static String getSerialNumberDisplayName(String serialNumber) {
        String str;
        synchronized (serialNumberDisplayNames) {
            str = (String) serialNumberDisplayNames.get(serialNumber);
            if (str == null) {
                str = String.format("unknown USB device [%s]", new Object[]{serialNumber});
            }
        }
        return str;
    }
}
