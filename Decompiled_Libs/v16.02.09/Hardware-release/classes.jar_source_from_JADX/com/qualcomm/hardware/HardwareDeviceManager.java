package com.qualcomm.hardware;

import android.content.Context;
import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtAccelerationSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtColorSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtCompassSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtDcMotorController;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtGyroSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtIrSeekerSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtLightSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtServoController;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtTouchSensor;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtTouchSensorMultiplexer;
import com.qualcomm.hardware.hitechnic.HiTechnicNxtUltrasonicSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsDigitalTouchSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cIrSeekerSensorV3;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDcMotorController;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.OpenRobotUsbDevice;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDeviceInterfaceModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbLegacyModule;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbServoController;
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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.Direction;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.DeviceManager.DeviceType;
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
import com.qualcomm.robotcore.hardware.Servo;
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

public class HardwareDeviceManager implements DeviceManager {
    private static C0005a f12a;
    private RobotUsbManager f13b;
    private final EventLoopManager f14c;
    private final Context f15d;

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.1 */
    class C00001 implements OpenRobotUsbDevice {
        final /* synthetic */ SerialNumber f0a;
        final /* synthetic */ HardwareDeviceManager f1b;

        C00001(HardwareDeviceManager hardwareDeviceManager, SerialNumber serialNumber) {
            this.f1b = hardwareDeviceManager;
            this.f0a = serialNumber;
        }

        public RobotUsbDevice open() throws RobotCoreException, InterruptedException {
            RobotUsbDevice robotUsbDevice = null;
            try {
                robotUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.f1b.f13b, this.f0a);
                if (ModernRoboticsUsbUtil.getDeviceType(ModernRoboticsUsbUtil.getUsbDeviceHeader(robotUsbDevice)) != DeviceType.MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER) {
                    this.f1b.m4a(robotUsbDevice, this.f0a);
                }
                return robotUsbDevice;
            } catch (RobotCoreException e) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e;
            } catch (RuntimeException e2) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e2;
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.2 */
    class C00012 implements OpenRobotUsbDevice {
        final /* synthetic */ SerialNumber f2a;
        final /* synthetic */ HardwareDeviceManager f3b;

        C00012(HardwareDeviceManager hardwareDeviceManager, SerialNumber serialNumber) {
            this.f3b = hardwareDeviceManager;
            this.f2a = serialNumber;
        }

        public RobotUsbDevice open() throws RobotCoreException, InterruptedException {
            RobotUsbDevice robotUsbDevice = null;
            try {
                robotUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.f3b.f13b, this.f2a);
                if (ModernRoboticsUsbUtil.getDeviceType(ModernRoboticsUsbUtil.getUsbDeviceHeader(robotUsbDevice)) != DeviceType.MODERN_ROBOTICS_USB_SERVO_CONTROLLER) {
                    this.f3b.m4a(robotUsbDevice, this.f2a);
                }
                return robotUsbDevice;
            } catch (RobotCoreException e) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e;
            } catch (RuntimeException e2) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e2;
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.3 */
    class C00023 implements OpenRobotUsbDevice {
        final /* synthetic */ SerialNumber f4a;
        final /* synthetic */ HardwareDeviceManager f5b;

        C00023(HardwareDeviceManager hardwareDeviceManager, SerialNumber serialNumber) {
            this.f5b = hardwareDeviceManager;
            this.f4a = serialNumber;
        }

        public RobotUsbDevice open() throws RobotCoreException, InterruptedException {
            RobotUsbDevice robotUsbDevice = null;
            try {
                robotUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.f5b.f13b, this.f4a);
                if (ModernRoboticsUsbUtil.getDeviceType(ModernRoboticsUsbUtil.getUsbDeviceHeader(robotUsbDevice)) != DeviceType.MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE) {
                    this.f5b.m4a(robotUsbDevice, this.f4a);
                }
                return robotUsbDevice;
            } catch (RobotCoreException e) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e;
            } catch (RuntimeException e2) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e2;
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.4 */
    class C00034 implements OpenRobotUsbDevice {
        final /* synthetic */ SerialNumber f6a;
        final /* synthetic */ HardwareDeviceManager f7b;

        C00034(HardwareDeviceManager hardwareDeviceManager, SerialNumber serialNumber) {
            this.f7b = hardwareDeviceManager;
            this.f6a = serialNumber;
        }

        public RobotUsbDevice open() throws RobotCoreException, InterruptedException {
            RobotUsbDevice robotUsbDevice = null;
            try {
                robotUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.f7b.f13b, this.f6a);
                if (ModernRoboticsUsbUtil.getDeviceType(ModernRoboticsUsbUtil.getUsbDeviceHeader(robotUsbDevice)) != DeviceType.MODERN_ROBOTICS_USB_LEGACY_MODULE) {
                    this.f7b.m4a(robotUsbDevice, this.f6a);
                }
                return robotUsbDevice;
            } catch (RobotCoreException e) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e;
            } catch (RuntimeException e2) {
                if (robotUsbDevice != null) {
                    robotUsbDevice.close();
                }
                throw e2;
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.5 */
    static /* synthetic */ class C00045 {
        static final /* synthetic */ int[] f8a;

        static {
            f8a = new int[C0005a.values().length];
            try {
                f8a[C0005a.ENABLE_DEVICE_EMULATION.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* renamed from: com.qualcomm.hardware.HardwareDeviceManager.a */
    private enum C0005a {
        DEFAULT,
        ENABLE_DEVICE_EMULATION
    }

    static {
        f12a = C0005a.DEFAULT;
    }

    public HardwareDeviceManager(Context context, EventLoopManager manager) throws RobotCoreException {
        this.f15d = context;
        this.f14c = manager;
        switch (C00045.f8a[f12a.ordinal()]) {
            case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                this.f13b = new RobotUsbManagerEmulator();
            default:
                this.f13b = new RobotUsbManagerFtdi(context);
        }
    }

    public Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException {
        Map hashMap = new HashMap();
        try {
            int scanForDevices = this.f13b.scanForDevices();
            for (int i = 0; i < scanForDevices; i++) {
                SerialNumber deviceSerialNumberByIndex = this.f13b.getDeviceSerialNumberByIndex(i);
                RobotUsbDevice openUsbDevice = ModernRoboticsUsbUtil.openUsbDevice(this.f13b, deviceSerialNumberByIndex);
                hashMap.put(deviceSerialNumberByIndex, ModernRoboticsUsbUtil.getDeviceType(ModernRoboticsUsbUtil.getUsbDeviceHeader(openUsbDevice)));
                openUsbDevice.close();
            }
        } catch (RobotCoreException e) {
            RobotLog.setGlobalErrorMsgAndThrow(e, "Error while scanning for USB devices");
        }
        return hashMap;
    }

    public DcMotorController createUsbDcMotorController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        HardwareFactory.noteSerialNumberType(serialNumber, this.f15d.getString(R.string.moduleDisplayNameMotorController));
        RobotLog.v("Creating %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(serialNumber)});
        ModernRoboticsUsbDcMotorController modernRoboticsUsbDcMotorController = new ModernRoboticsUsbDcMotorController(this.f15d, serialNumber, new C00001(this, serialNumber), this.f14c);
        modernRoboticsUsbDcMotorController.armOrPretend();
        modernRoboticsUsbDcMotorController.initializeHardware();
        return modernRoboticsUsbDcMotorController;
    }

    public DcMotor createDcMotor(DcMotorController controller, int portNumber) {
        return new DcMotor(controller, portNumber, Direction.FORWARD);
    }

    public ServoController createUsbServoController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        HardwareFactory.noteSerialNumberType(serialNumber, this.f15d.getString(R.string.moduleDisplayNameServoController));
        RobotLog.v("Creating %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(serialNumber)});
        ModernRoboticsUsbServoController modernRoboticsUsbServoController = new ModernRoboticsUsbServoController(this.f15d, serialNumber, new C00012(this, serialNumber), this.f14c);
        modernRoboticsUsbServoController.armOrPretend();
        modernRoboticsUsbServoController.initializeHardware();
        return modernRoboticsUsbServoController;
    }

    public Servo createServo(ServoController controller, int portNumber) {
        return new Servo(controller, portNumber, Servo.Direction.FORWARD);
    }

    public DeviceInterfaceModule createDeviceInterfaceModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        HardwareFactory.noteSerialNumberType(serialNumber, this.f15d.getString(R.string.moduleDisplayNameCDIM));
        RobotLog.v("Creating %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(serialNumber)});
        ModernRoboticsUsbDeviceInterfaceModule modernRoboticsUsbDeviceInterfaceModule = new ModernRoboticsUsbDeviceInterfaceModule(this.f15d, serialNumber, new C00023(this, serialNumber), this.f14c);
        modernRoboticsUsbDeviceInterfaceModule.armOrPretend();
        modernRoboticsUsbDeviceInterfaceModule.initializeHardware();
        return modernRoboticsUsbDeviceInterfaceModule;
    }

    public LegacyModule createUsbLegacyModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException {
        HardwareFactory.noteSerialNumberType(serialNumber, this.f15d.getString(R.string.moduleDisplayNameLegacyModule));
        RobotLog.v("Creating %s", new Object[]{HardwareFactory.getSerialNumberDisplayName(serialNumber)});
        ModernRoboticsUsbLegacyModule modernRoboticsUsbLegacyModule = new ModernRoboticsUsbLegacyModule(this.f15d, serialNumber, new C00034(this, serialNumber), this.f14c);
        modernRoboticsUsbLegacyModule.armOrPretend();
        modernRoboticsUsbLegacyModule.initializeHardware();
        return modernRoboticsUsbLegacyModule;
    }

    public DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT DC Motor Controller - Port: " + physicalPort);
        return new HiTechnicNxtDcMotorController(m1a(legacyModule), physicalPort);
    }

    public ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Servo Controller - Port: " + physicalPort);
        return new HiTechnicNxtServoController(m1a(legacyModule), physicalPort);
    }

    public CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Compass Sensor - Port: " + physicalPort);
        return new HiTechnicNxtCompassSensor(m1a(legacyModule), physicalPort);
    }

    public TouchSensor createDigitalTouchSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics Digital Touch Sensor - Port: " + physicalPort);
        return new ModernRoboticsDigitalTouchSensor(m0a(deviceInterfaceModule), physicalPort);
    }

    public AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Acceleration Sensor - Port: " + physicalPort);
        return new HiTechnicNxtAccelerationSensor(m1a(legacyModule), physicalPort);
    }

    public LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Light Sensor - Port: " + physicalPort);
        return new HiTechnicNxtLightSensor(m1a(legacyModule), physicalPort);
    }

    public GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Gyro Sensor - Port: " + physicalPort);
        return new HiTechnicNxtGyroSensor(m1a(legacyModule), physicalPort);
    }

    public IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT IR Seeker Sensor - Port: " + physicalPort);
        return new HiTechnicNxtIrSeekerSensor(m1a(legacyModule), physicalPort);
    }

    public IrSeekerSensor createI2cIrSeekerSensorV3(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics I2C IR Seeker Sensor V3 - Port: " + physicalPort);
        return new ModernRoboticsI2cIrSeekerSensorV3(m0a(deviceInterfaceModule), physicalPort);
    }

    public UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Ultrasonic Sensor - Port: " + physicalPort);
        return new HiTechnicNxtUltrasonicSensor(m1a(legacyModule), physicalPort);
    }

    public OpticalDistanceSensor createAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort) {
        RobotLog.v("Creating Modern Robotics Analog Optical Distance Sensor - Port: " + physicalPort);
        return new ModernRoboticsAnalogOpticalDistanceSensor(m0a(deviceInterfaceModule), physicalPort);
    }

    public TouchSensor createNxtTouchSensor(LegacyModule legacyModule, int physicalPort) {
        RobotLog.v("Creating HiTechnic NXT Touch Sensor - Port: " + physicalPort);
        return new HiTechnicNxtTouchSensor(m1a(legacyModule), physicalPort);
    }

    public TouchSensorMultiplexer createNxtTouchSensorMultiplexer(LegacyModule legacyModule, int port) {
        RobotLog.v("Creating HiTechnic NXT Touch Sensor Multiplexer - Port: " + port);
        return new HiTechnicNxtTouchSensorMultiplexer(m1a(legacyModule), port);
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
        f12a = C0005a.ENABLE_DEVICE_EMULATION;
    }

    public static void disableDeviceEmulation() {
        f12a = C0005a.DEFAULT;
    }

    private ModernRoboticsUsbLegacyModule m1a(LegacyModule legacyModule) {
        if (legacyModule instanceof ModernRoboticsUsbLegacyModule) {
            return (ModernRoboticsUsbLegacyModule) legacyModule;
        }
        throw new IllegalArgumentException("Modern Robotics Device Manager needs a Modern Robotics LegacyModule");
    }

    private ModernRoboticsUsbDeviceInterfaceModule m0a(DeviceInterfaceModule deviceInterfaceModule) {
        if (deviceInterfaceModule instanceof ModernRoboticsUsbDeviceInterfaceModule) {
            return (ModernRoboticsUsbDeviceInterfaceModule) deviceInterfaceModule;
        }
        throw new IllegalArgumentException("Modern Robotics Device Manager needs a Modern Robotics Device Interface Module");
    }

    private void m4a(RobotUsbDevice robotUsbDevice, SerialNumber serialNumber) throws RobotCoreException {
        String format = String.format("%s is returning garbage data on the USB bus", new Object[]{HardwareFactory.getSerialNumberDisplayName(serialNumber)});
        robotUsbDevice.close();
        m5a(format);
    }

    private void m5a(String str) throws RobotCoreException {
        System.err.println(str);
        throw new RobotCoreException(str);
    }
}
