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

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor.Direction;
import com.qualcomm.robotcore.util.SerialNumber;

import java.util.Map;

public abstract class DeviceManager {
    /**
     * Get a listing of all Modern Robotics devices connected.
     * <p/>
     * This method will attempt to open all USB devices that are using an FTDI USB chipset. It will then probe the device to determine if it is a Modern Robotics device. Finally, it will close the device.
     * <p/>
     * Because of the opening and closing of devices, it is recommended that this method is not called while any FTDI devices are in use.
     *
     * @return a map of serial numbers to Modern Robotics device types
     * @throws RobotCoreException if unable to open a device
     */
    public abstract Map<SerialNumber, DeviceType> scanForUsbDevices() throws RobotCoreException;

    /**
     * Create an instance of a AnalogInput
     *
     * @param controller Analog Output Controller this device is connected to
     * @param channel    the port number this device is connected to on the Controller
     * @return a AnalogInput instance
     */
    public abstract AnalogInput createAnalogInputDevice(AnalogInputController controller, int channel);

    /**
     * Create an instance of a AnalogOutput
     *
     * @param controller Analog Output Controller this device is connected to
     * @param channel    the port number this device is connected to on the Controller
     * @return a AnalogOutput instance
     */
    public abstract AnalogOutput createAnalogOutputDevice(AnalogOutputController controller, int channel);

    /**
     * Create an instance of a DeviceInterfaceModule
     *
     * @param serialNumber serial number of controller
     * @return a DeviceInterfaceModule instance
     * @throws RobotCoreException   if unable to create instance
     * @throws InterruptedException
     */
    public abstract DeviceInterfaceModule createDeviceInterfaceModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    /**
     * Create an instance of a DcMotorController
     *
     * @param serialNumber serial number of controller
     * @return a DcMotorController instance
     * @throws RobotCoreException   if unable to create instance
     * @throws InterruptedException
     */
    public abstract DcMotorController createUsbDcMotorController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    /**
     * Create an instance of a LegacyModule
     *
     * @param serialNumber serial number of legacy module
     * @return a LegacyModule instance
     * @throws RobotCoreException   if unable to create instance
     * @throws InterruptedException
     */
    public abstract LegacyModule createUsbLegacyModule(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    /**
     * Create an instance of a ServoController
     *
     * @param serialNumber serial number of controller
     * @return a ServoController instance
     * @throws RobotCoreException   if unable to create instance
     * @throws InterruptedException
     */
    public abstract ServoController createUsbServoController(SerialNumber serialNumber) throws RobotCoreException, InterruptedException;

    /**
     * Create an instance of a DigitalChannel
     *
     * @param controller Digital Channel Controller this device is connected to
     * @param channel    the port number this device is connected to on the Controller
     * @return a DigitalChannel instance
     */
    public abstract I2cDevice createI2cDevice(I2cController controller, int channel);

    /**
     * Create an instance of a DigitalChannel
     *
     * @param controller Digital Channel Controller this device is connected to
     * @param channel    the port number this device is connected to on the Controller
     * @return a DigitalChannel instance
     */
    public abstract DigitalChannel createDigitalChannelDevice(DigitalChannelController controller, int channel);

    /**
     * Create an instance of a LED
     *
     * @param controller Digital Channel Controller this device is connected to
     * @param channel    the port number this device is connected to on the Controller
     * @return a LED instance
     */
    public abstract LED createLED(DigitalChannelController controller, int channel);

    /**
     * Create an instance of a ColorSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param channel               the I2C port number this sensor is connected to on the Controller
     * @return a ColorSensor instance
     */
    public abstract ColorSensor createAdafruitI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int channel);

    /**
     * Create an instance of an OpticalDistanceSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param physicalPort          the port number this sensor is connected to on the Controller
     * @return an OpticalDistanceSensor instance
     */
    public abstract OpticalDistanceSensor createAnalogOpticalDistanceSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    /**
     * Create an instance of a TouchSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param physicalPort          the port number this sensor is connected to on the Controller
     * @return a TouchSensor instance
     */
    public abstract TouchSensor createDigitalTouchSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    /**
     * Create an instance of an IrSeekerSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param physicalPort          the port number this sensor is connected to on the Controller
     * @return an IrSeekerSensor instance
     */
    public abstract IrSeekerSensor createI2cIrSeekerSensorV3(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    /**
     * Create an instance of a ColorSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param channel               the I2C port number this sensor is connected to on the Controller
     * @return a ColorSensor instance
     */
    public abstract ColorSensor createModernRoboticsI2cColorSensor(DeviceInterfaceModule deviceInterfaceModule, int channel);

    /**
     * Create an instance of a GyroSensor
     *
     * @param deviceInterfaceModule Device Interface Module this sensor is connected to
     * @param physicalPort          the port number this sensor is connected to on the Controller
     * @return a GyroSensor instance
     */
    public abstract GyroSensor createModernRoboticsI2cGyroSensor(DeviceInterfaceModule deviceInterfaceModule, int physicalPort);

    /**
     * Create an instance of a PWMOutput
     *
     * @param deviceInterfaceModule Device Interface Module this device is connected to
     * @param channel               the I2C port number this device is connected to on the Controller
     * @return a PWMOutput instance
     */
    public abstract PWMOutput createPwmOutputDevice(DeviceInterfaceModule deviceInterfaceModule, int channel);

    /**
     * Create an instance of a AccelerationSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a AccelerationSensor instance
     */
    public abstract AccelerationSensor createNxtAccelerationSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a ColorSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param channel      the I2C port number this sensor is connected to on the Controller
     * @return a ColorSensor instance
     */
    public abstract ColorSensor createNxtColorSensor(LegacyModule legacyModule, int channel);

    /**
     * Create an instance of a CompassSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a CompassSensor instance
     */
    public abstract CompassSensor createNxtCompassSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a DcMotorController
     *
     * @param legacyModule Legacy Module this device is connected to
     * @param physicalPort the port number this device is connected to on the Controller
     * @return a DcMotorController instance
     */
    public abstract DcMotorController createNxtDcMotorController(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a GyroSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a GyroSensor instance
     */
    public abstract GyroSensor createNxtGyroSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a IrSeekerSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a IrSeekerSensor instance
     */
    public abstract IrSeekerSensor createNxtIrSeekerSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a LightSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a LightSensor instance
     */
    public abstract LightSensor createNxtLightSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a ServoController
     *
     * @param legacyModule Legacy Module this device is connected to
     * @param physicalPort the port number this device is connected to on the Controller
     * @return a ServoController instance
     */
    public abstract ServoController createNxtServoController(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a TouchSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a TouchSensor instance
     */
    public abstract TouchSensor createNxtTouchSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a TouchSensorMultiplexer
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param port         the port number this sensor is connected to on the Controller
     * @return a TouchSensorMultiplexer instance
     */
    public abstract TouchSensorMultiplexer createNxtTouchSensorMultiplexer(LegacyModule legacyModule, int port);

    /**
     * Create an instance of a UltrasonicSensor
     *
     * @param legacyModule Legacy Module this sensor is connected to
     * @param physicalPort the port number this sensor is connected to on the Controller
     * @return a UltrasonicSensor instance
     */
    public abstract UltrasonicSensor createNxtUltrasonicSensor(LegacyModule legacyModule, int physicalPort);

    /**
     * Create an instance of a DcMotor
     *
     * @param controller DC Motor controller this motor is connected to
     * @param portNumber the port number this motor is connected to on the Controller
     * @return a DcMotor instance
     */
    public DcMotor createDcMotor(DcMotorController controller, int portNumber) {
        return new DcMotor(controller, portNumber, Direction.FORWARD);
    }

    /**
     * Create an instance of a Servo
     *
     * @param controller Servo controller this servo is connected to
     * @param portNumber the port number this motor is connected to on the Controller
     * @return a Servo instance
     */
    public Servo createServo(ServoController controller, int portNumber) {
        return new Servo(controller, portNumber, Servo.Direction.FORWARD);
    }

    /**
     * Enum of Device Type
     */
    public enum DeviceType {
        FTDI_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_UNKNOWN_DEVICE,
        MODERN_ROBOTICS_USB_DC_MOTOR_CONTROLLER,
        MODERN_ROBOTICS_USB_SERVO_CONTROLLER,
        MODERN_ROBOTICS_USB_LEGACY_MODULE,
        MODERN_ROBOTICS_USB_DEVICE_INTERFACE_MODULE,
        MODERN_ROBOTICS_USB_SENSOR_MUX
    }
}
