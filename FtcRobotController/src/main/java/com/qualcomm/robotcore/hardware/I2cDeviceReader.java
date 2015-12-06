package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

public class I2cDeviceReader {
    private final I2cDevice device;

    class callback implements I2cPortReadyCallback {
        public void portIsReady(int port) {
            device.setI2cPortActionFlag();
            device.readI2cCacheFromModule();
            device.writeI2cPortFlagOnlyToModule();
        }
    }

    public I2cDeviceReader(I2cDevice i2cDevice, int i2cAddress, int memAddress, int length) {
        this.device = i2cDevice;
        i2cDevice.enableI2cReadMode(i2cAddress, memAddress, length);
        i2cDevice.setI2cPortActionFlag();
        i2cDevice.writeI2cCacheToModule();
        i2cDevice.registerForI2cPortReadyCallback(new callback());
    }

    public byte[] getReadBuffer() {
        return this.device.getCopyOfReadBuffer();
    }
}
