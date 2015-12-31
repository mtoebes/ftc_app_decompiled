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

/**
 * Light Sensor
 */
public abstract class ColorSensor implements HardwareDevice {
    public abstract int alpha();

    /**
     * Get the "hue"
     *
     * @return hue
     */
    public abstract int argb();

    /**
     * Get the Blue values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int blue();

    /**
     * Enable the LED light
     *
     * @param enable true to enable; false to disable
     */
    public abstract void enableLed(boolean enable);

    /**
     * Get the current I2C Address of this object. Not necessarily the same as the I2C address of the actual device. Return the current I2C address.
     *
     * @return current I2C address
     */
    public abstract int getI2cAddress();

    /**
     * Get the Green values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int green();

    /**
     * Get the Red values detected by the sensor as an int.
     *
     * @return reading, unscaled.
     */
    public abstract int red();

    /**
     * Set the I2C address to a new value.
     *
     * @param newAddress value of new I2C address
     */
    public abstract void setI2cAddress(int newAddress);

    public String toString() {
        return String.format("argb: %d", argb());
    }
}
