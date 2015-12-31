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

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

/**
 * Monitor an I2C Device and read in the most current values
 */
public class I2cDeviceReader {
    private final I2cDevice device;

    class I2cDeviceReaderCallback implements I2cPortReadyCallback {
        final I2cDeviceReader deviceReader;

        I2cDeviceReaderCallback(I2cDeviceReader i2cDeviceReader) {
            this.deviceReader = i2cDeviceReader;
        }

        public void portIsReady(int port) {
            deviceReader.device.setI2cPortActionFlag();
            deviceReader.device.readI2cCacheFromController();
            deviceReader.device.writeI2cCacheToController();
        }
    }

    /**
     * Constructor
     *
     * @param i2cDevice  device to monitor
     * @param i2cAddress I2C address to read from
     * @param memAddress memory address to read from
     * @param length     length (in bytes) to read
     */
    public I2cDeviceReader(I2cDevice i2cDevice, int i2cAddress, int memAddress, int length) {
        this.device = i2cDevice;
        i2cDevice.enableI2cReadMode(i2cAddress, memAddress, length);
        i2cDevice.setI2cPortActionFlag();
        i2cDevice.readI2cCacheFromController();
        i2cDevice.registerForI2cPortReadyCallback(new I2cDeviceReaderCallback(this));
    }

    /**
     * Get a copy of the most recent data read in from the I2C device
     *
     * @return byte array
     */
    public byte[] getReadBuffer() {
        return this.device.getCopyOfReadBuffer();
    }
}
