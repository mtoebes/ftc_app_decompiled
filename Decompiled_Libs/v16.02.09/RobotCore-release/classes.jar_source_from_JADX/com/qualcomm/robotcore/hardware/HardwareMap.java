package com.qualcomm.robotcore.hardware;

import android.content.Context;
import com.android.internal.util.Predicate;
import com.qualcomm.robotcore.util.RobotLog;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HardwareMap {
    public DeviceMapping<AccelerationSensor> accelerationSensor;
    public DeviceMapping<AnalogInput> analogInput;
    public DeviceMapping<AnalogOutput> analogOutput;
    public final Context appContext;
    public DeviceMapping<ColorSensor> colorSensor;
    public DeviceMapping<CompassSensor> compassSensor;
    public DeviceMapping<DcMotor> dcMotor;
    public DeviceMapping<DcMotorController> dcMotorController;
    public DeviceMapping<DeviceInterfaceModule> deviceInterfaceModule;
    public DeviceMapping<DigitalChannel> digitalChannel;
    public DeviceMapping<GyroSensor> gyroSensor;
    public DeviceMapping<I2cDevice> i2cDevice;
    public DeviceMapping<IrSeekerSensor> irSeekerSensor;
    public DeviceMapping<LED> led;
    public DeviceMapping<LegacyModule> legacyModule;
    public DeviceMapping<LightSensor> lightSensor;
    public DeviceMapping<OpticalDistanceSensor> opticalDistanceSensor;
    public DeviceMapping<PWMOutput> pwmOutput;
    public DeviceMapping<Servo> servo;
    public DeviceMapping<ServoController> servoController;
    public DeviceMapping<TouchSensor> touchSensor;
    public DeviceMapping<TouchSensorMultiplexer> touchSensorMultiplexer;
    public DeviceMapping<UltrasonicSensor> ultrasonicSensor;
    public DeviceMapping<VoltageSensor> voltageSensor;

    public static class DeviceMapping<DEVICE_TYPE> implements Iterable<DEVICE_TYPE> {
        private Map<String, DEVICE_TYPE> f237a;

        public DeviceMapping() {
            this.f237a = new HashMap();
        }

        public DEVICE_TYPE get(String deviceName) {
            DEVICE_TYPE device_type = this.f237a.get(deviceName);
            if (device_type != null) {
                return device_type;
            }
            throw new IllegalArgumentException(String.format("Unable to find a hardware device with the name \"%s\"", new Object[]{deviceName}));
        }

        public void put(String deviceName, DEVICE_TYPE device) {
            this.f237a.put(deviceName, device);
        }

        public Iterator<DEVICE_TYPE> iterator() {
            return this.f237a.values().iterator();
        }

        public Set<Entry<String, DEVICE_TYPE>> entrySet() {
            return this.f237a.entrySet();
        }

        public int size() {
            return this.f237a.size();
        }

        public void logDevices() {
            if (!this.f237a.isEmpty()) {
                for (Entry entry : this.f237a.entrySet()) {
                    if (entry.getValue() instanceof HardwareDevice) {
                        String connectionInfo = ((HardwareDevice) entry.getValue()).getConnectionInfo();
                        String str = (String) entry.getKey();
                        RobotLog.m252i(String.format("%-45s %-30s %s", new Object[]{r1.getDeviceName(), str, connectionInfo}));
                    }
                }
            }
        }
    }

    public HardwareMap(Context appContext) {
        this.dcMotorController = new DeviceMapping();
        this.dcMotor = new DeviceMapping();
        this.servoController = new DeviceMapping();
        this.servo = new DeviceMapping();
        this.legacyModule = new DeviceMapping();
        this.touchSensorMultiplexer = new DeviceMapping();
        this.deviceInterfaceModule = new DeviceMapping();
        this.analogInput = new DeviceMapping();
        this.digitalChannel = new DeviceMapping();
        this.opticalDistanceSensor = new DeviceMapping();
        this.touchSensor = new DeviceMapping();
        this.pwmOutput = new DeviceMapping();
        this.i2cDevice = new DeviceMapping();
        this.analogOutput = new DeviceMapping();
        this.colorSensor = new DeviceMapping();
        this.led = new DeviceMapping();
        this.accelerationSensor = new DeviceMapping();
        this.compassSensor = new DeviceMapping();
        this.gyroSensor = new DeviceMapping();
        this.irSeekerSensor = new DeviceMapping();
        this.lightSensor = new DeviceMapping();
        this.ultrasonicSensor = new DeviceMapping();
        this.voltageSensor = new DeviceMapping();
        this.appContext = appContext;
    }

    public Set<Object> findDevices(Predicate<Object> predicate) {
        Set<Object> hashSet = new HashSet();
        m199a(hashSet, predicate, this.dcMotorController);
        m199a(hashSet, predicate, this.dcMotor);
        m199a(hashSet, predicate, this.servoController);
        m199a(hashSet, predicate, this.servo);
        m199a(hashSet, predicate, this.legacyModule);
        m199a(hashSet, predicate, this.touchSensorMultiplexer);
        m199a(hashSet, predicate, this.deviceInterfaceModule);
        m199a(hashSet, predicate, this.analogInput);
        m199a(hashSet, predicate, this.digitalChannel);
        m199a(hashSet, predicate, this.opticalDistanceSensor);
        m199a(hashSet, predicate, this.touchSensor);
        m199a(hashSet, predicate, this.pwmOutput);
        m199a(hashSet, predicate, this.i2cDevice);
        m199a(hashSet, predicate, this.analogOutput);
        m199a(hashSet, predicate, this.colorSensor);
        m199a(hashSet, predicate, this.led);
        m199a(hashSet, predicate, this.accelerationSensor);
        m199a(hashSet, predicate, this.compassSensor);
        m199a(hashSet, predicate, this.gyroSensor);
        m199a(hashSet, predicate, this.irSeekerSensor);
        m199a(hashSet, predicate, this.lightSensor);
        m199a(hashSet, predicate, this.ultrasonicSensor);
        m199a(hashSet, predicate, this.voltageSensor);
        return hashSet;
    }

    private <DEVICE_TYPE> void m199a(Set<Object> set, Predicate<Object> predicate, DeviceMapping<DEVICE_TYPE> deviceMapping) {
        Iterator it = deviceMapping.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (predicate.apply(next)) {
                set.add(next);
            }
        }
    }

    public void logDevices() {
        RobotLog.m252i("========= Device Information ===================================================");
        RobotLog.m252i(String.format("%-45s %-30s %s", new Object[]{"Type", "Name", "Connection"}));
        this.dcMotorController.logDevices();
        this.dcMotor.logDevices();
        this.servoController.logDevices();
        this.servo.logDevices();
        this.legacyModule.logDevices();
        this.touchSensorMultiplexer.logDevices();
        this.deviceInterfaceModule.logDevices();
        this.analogInput.logDevices();
        this.digitalChannel.logDevices();
        this.opticalDistanceSensor.logDevices();
        this.touchSensor.logDevices();
        this.pwmOutput.logDevices();
        this.i2cDevice.logDevices();
        this.analogOutput.logDevices();
        this.colorSensor.logDevices();
        this.led.logDevices();
        this.accelerationSensor.logDevices();
        this.compassSensor.logDevices();
        this.gyroSensor.logDevices();
        this.irSeekerSensor.logDevices();
        this.lightSensor.logDevices();
        this.ultrasonicSensor.logDevices();
        this.voltageSensor.logDevices();
    }
}
