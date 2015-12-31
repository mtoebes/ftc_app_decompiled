/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.hardware;

import android.content.Context;

import com.qualcomm.robotcore.util.RobotLog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Hardware Mappings By default this creates a bunch of empty mappings between a string and an instance of a hardware driver.
 */
public class HardwareMap {
    public DeviceMapping<AccelerationSensor> accelerationSensor;
    public DeviceMapping<AnalogInput> analogInput;
    public DeviceMapping<AnalogOutput> analogOutput;
    public Context appContext;
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
        private Map<String, DEVICE_TYPE> deviceTypeMap;

        public DeviceMapping() {
            this.deviceTypeMap = new HashMap<String, DEVICE_TYPE>();
        }

        public DEVICE_TYPE get(String deviceName) {
            DEVICE_TYPE device_type = this.deviceTypeMap.get(deviceName);
            if (device_type == null) {
                throw new IllegalArgumentException(String.format("Unable to find a hardware device with the name \"%s\"", deviceName));
            } else {
                return device_type;
            }
        }

        public void put(String deviceName, DEVICE_TYPE device) {
            this.deviceTypeMap.put(deviceName, device);
        }

        public Iterator<DEVICE_TYPE> iterator() {
            return this.deviceTypeMap.values().iterator();
        }

        public Set<Entry<String, DEVICE_TYPE>> entrySet() {
            return this.deviceTypeMap.entrySet();
        }

        public int size() {
            return this.deviceTypeMap.size();
        }

        public void logDevices() {
            if (!this.deviceTypeMap.isEmpty()) {
                for (Entry<String, DEVICE_TYPE> entry : this.deviceTypeMap.entrySet()) {
                    if (entry.getValue() instanceof HardwareDevice) {
                        HardwareDevice device = (HardwareDevice) entry.getValue();
                        RobotLog.i(String.format("%-45s %-30s %s", device.getDeviceName(), entry.getKey(), device.getConnectionInfo()));
                    }
                }
            }
        }
    }

    public HardwareMap() {
        this.dcMotorController = new DeviceMapping<DcMotorController>();
        this.dcMotor = new DeviceMapping<DcMotor>();
        this.servoController = new DeviceMapping<ServoController>();
        this.servo = new DeviceMapping<Servo>();
        this.legacyModule = new DeviceMapping<LegacyModule>();
        this.touchSensorMultiplexer = new DeviceMapping<TouchSensorMultiplexer>();
        this.deviceInterfaceModule = new DeviceMapping<DeviceInterfaceModule>();
        this.analogInput = new DeviceMapping<AnalogInput>();
        this.digitalChannel = new DeviceMapping<DigitalChannel>();
        this.opticalDistanceSensor = new DeviceMapping<OpticalDistanceSensor>();
        this.touchSensor = new DeviceMapping<TouchSensor>();
        this.pwmOutput = new DeviceMapping<PWMOutput>();
        this.i2cDevice = new DeviceMapping<I2cDevice>();
        this.analogOutput = new DeviceMapping<AnalogOutput>();
        this.colorSensor = new DeviceMapping<ColorSensor>();
        this.led = new DeviceMapping<LED>();
        this.accelerationSensor = new DeviceMapping<AccelerationSensor>();
        this.compassSensor = new DeviceMapping<CompassSensor>();
        this.gyroSensor = new DeviceMapping<GyroSensor>();
        this.irSeekerSensor = new DeviceMapping<IrSeekerSensor>();
        this.lightSensor = new DeviceMapping<LightSensor>();
        this.ultrasonicSensor = new DeviceMapping<UltrasonicSensor>();
        this.voltageSensor = new DeviceMapping<VoltageSensor>();
        this.appContext = null;
    }

    public void logDevices() {
        RobotLog.i("========= Device Information ===================================================");
        RobotLog.i(String.format("%-45s %-30s %s", "Type", "Name", "Connection"));
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
