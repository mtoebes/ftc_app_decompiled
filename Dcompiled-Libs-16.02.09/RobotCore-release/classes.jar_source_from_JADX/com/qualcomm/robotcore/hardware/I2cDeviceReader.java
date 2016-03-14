package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

public class I2cDeviceReader {
    private final I2cDevice f240a;

    /* renamed from: com.qualcomm.robotcore.hardware.I2cDeviceReader.1 */
    class C00391 implements I2cPortReadyCallback {
        final /* synthetic */ I2cDeviceReader f239a;

        C00391(I2cDeviceReader i2cDeviceReader) {
            this.f239a = i2cDeviceReader;
        }

        public void portIsReady(int port) {
            this.f239a.m200a();
        }
    }

    public I2cDeviceReader(I2cDevice i2cDevice, int i2cAddress, int memAddress, int length) {
        this.f240a = i2cDevice;
        i2cDevice.enableI2cReadMode(i2cAddress, memAddress, length);
        i2cDevice.setI2cPortActionFlag();
        i2cDevice.writeI2cCacheToModule();
        i2cDevice.registerForI2cPortReadyCallback(new C00391(this));
    }

    public byte[] getReadBuffer() {
        return this.f240a.getCopyOfReadBuffer();
    }

    private void m200a() {
        this.f240a.setI2cPortActionFlag();
        this.f240a.readI2cCacheFromModule();
        this.f240a.writeI2cPortFlagOnlyToModule();
    }
}
