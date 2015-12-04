package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.hardware.I2cController.I2cPortReadyCallback;

public class I2cDeviceReader {
    private final I2cDevice f247a;

    /* renamed from: com.qualcomm.robotcore.hardware.I2cDeviceReader.1 */
    class C00351 implements I2cPortReadyCallback {
        final /* synthetic */ I2cDeviceReader f246a;

        C00351(I2cDeviceReader i2cDeviceReader) {
            this.f246a = i2cDeviceReader;
        }

        public void portIsReady(int port) {
            this.f246a.m189a();
        }
    }

    public I2cDeviceReader(I2cDevice i2cDevice, int i2cAddress, int memAddress, int length) {
        this.f247a = i2cDevice;
        i2cDevice.enableI2cReadMode(i2cAddress, memAddress, length);
        i2cDevice.setI2cPortActionFlag();
        i2cDevice.writeI2cCacheToModule();
        i2cDevice.registerForI2cPortReadyCallback(new C00351(this));
    }

    public byte[] getReadBuffer() {
        return this.f247a.getCopyOfReadBuffer();
    }

    private void m189a() {
        this.f247a.setI2cPortActionFlag();
        this.f247a.readI2cCacheFromModule();
        this.f247a.writeI2cPortFlagOnlyToModule();
    }
}
