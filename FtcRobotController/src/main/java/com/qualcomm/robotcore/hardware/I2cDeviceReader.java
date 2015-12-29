package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

public class I2cDeviceReader {
    private final I2cDevice device;

    class I2cDeviceReaderCallback implements I2cPortReadyCallback {
        final I2cDeviceReader deviceReader;

        I2cDeviceReaderCallback(I2cDeviceReader i2cDeviceReader) {
            this.deviceReader = i2cDeviceReader;
        }

        public void portIsReady(int port) {
            deviceReader.device.setI2cPortActionFlag();
            deviceReader.device.readI2cCacheFromModule();
            deviceReader.device.writeI2cPortFlagOnlyToModule();
        }
    }

    public I2cDeviceReader(I2cDevice i2cDevice, int i2cAddress, int memAddress, int length) {
        this.device = i2cDevice;
        i2cDevice.enableI2cReadMode(i2cAddress, memAddress, length);
        i2cDevice.setI2cPortActionFlag();
        i2cDevice.writeI2cCacheToModule();
        i2cDevice.registerForI2cPortReadyCallback(new I2cDeviceReaderCallback(this));
    }

    public byte[] getReadBuffer() {
        return this.device.getCopyOfReadBuffer();
    }
}
