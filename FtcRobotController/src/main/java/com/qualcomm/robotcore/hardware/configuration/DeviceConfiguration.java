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

package com.qualcomm.robotcore.hardware.configuration;

import java.io.Serializable;

public class DeviceConfiguration implements Serializable {
    public static final String DISABLED_DEVICE_NAME = "NO DEVICE ATTACHED";
    private ConfigurationType type = ConfigurationType.NOTHING;
    private int port;
    private boolean enabled;
    protected String name = DISABLED_DEVICE_NAME;

    public enum ConfigurationType {
        MOTOR,
        SERVO,
        GYRO,
        COMPASS,
        IR_SEEKER,
        LIGHT_SENSOR,
        ACCELEROMETER,
        MOTOR_CONTROLLER,
        SERVO_CONTROLLER,
        LEGACY_MODULE_CONTROLLER,
        DEVICE_INTERFACE_MODULE,
        I2C_DEVICE,
        ANALOG_INPUT,
        TOUCH_SENSOR,
        OPTICAL_DISTANCE_SENSOR,
        ANALOG_OUTPUT,
        DIGITAL_DEVICE,
        PULSE_WIDTH_DEVICE,
        IR_SEEKER_V3,
        TOUCH_SENSOR_MULTIPLEXER,
        MATRIX_CONTROLLER,
        ULTRASONIC_SENSOR,
        ADAFRUIT_COLOR_SENSOR,
        COLOR_SENSOR,
        LED,
        OTHER,
        NOTHING
    }

    public DeviceConfiguration(int port, ConfigurationType type, String name, boolean enabled) {
        this.port = port;
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

    public DeviceConfiguration(int port) {
        this(port, ConfigurationType.NOTHING, DISABLED_DEVICE_NAME, false);
    }

    public DeviceConfiguration(ConfigurationType type) {
        this(0, type, "", false);
    }

    public DeviceConfiguration(int port, ConfigurationType type) {
        this(port, type, DISABLED_DEVICE_NAME, false);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public ConfigurationType getType() {
        return this.type;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ConfigurationType typeFromString(String type) {
        for (ConfigurationType configurationType : ConfigurationType.values()) {
            if (type.equalsIgnoreCase(configurationType.toString())) {
                return configurationType;
            }
        }
        return ConfigurationType.NOTHING;
    }
}
