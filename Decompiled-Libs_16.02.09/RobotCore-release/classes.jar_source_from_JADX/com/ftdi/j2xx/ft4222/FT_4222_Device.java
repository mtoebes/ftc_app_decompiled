package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.interfaces.Gpio;
import com.ftdi.j2xx.interfaces.I2cMaster;
import com.ftdi.j2xx.interfaces.I2cSlave;
import com.ftdi.j2xx.interfaces.SpiMaster;
import com.ftdi.j2xx.interfaces.SpiSlave;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;

public class FT_4222_Device {
    protected String TAG;
    protected C0010b mChipStatus;
    protected FT_Device mFtDev;
    protected C0013e mGpio;
    protected C0009a mSpiMasterCfg;

    public FT_4222_Device(FT_Device ftDev) {
        this.TAG = "FT4222";
        this.mFtDev = ftDev;
        this.mChipStatus = new C0010b();
        this.mSpiMasterCfg = new C0009a();
        this.mGpio = new C0013e();
    }

    public int init() {
        byte[] bArr = new byte[13];
        if (this.mFtDev.VendorCmdGet(32, 1, bArr, 13) != 13) {
            return 18;
        }
        this.mChipStatus.m101a(bArr);
        return 0;
    }

    public int setClock(byte clk) {
        if (clk == this.mChipStatus.f89f) {
            return 0;
        }
        int VendorCmdSet = this.mFtDev.VendorCmdSet(33, (clk << 8) | 4);
        if (VendorCmdSet != 0) {
            return VendorCmdSet;
        }
        this.mChipStatus.f89f = clk;
        return VendorCmdSet;
    }

    public int getClock(byte[] clk) {
        if (this.mFtDev.VendorCmdGet(32, 4, clk, 1) < 0) {
            return 18;
        }
        this.mChipStatus.f89f = clk[0];
        return 0;
    }

    public boolean cleanRxData() {
        int queueStatus = this.mFtDev.getQueueStatus();
        if (queueStatus > 0) {
            byte[] bArr = new byte[queueStatus];
            if (this.mFtDev.read(bArr, queueStatus) != bArr.length) {
                return false;
            }
        }
        return true;
    }

    protected int getMaxBuckSize() {
        if (this.mChipStatus.f86c != null) {
            return 64;
        }
        switch (this.mChipStatus.f84a) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                return 256;
            default:
                return 512;
        }
    }

    public boolean isFT4222Device() {
        if (this.mFtDev != null) {
            switch (this.mFtDev.getDeviceInfo().bcdDevice & 65280) {
                case 5888:
                    this.mFtDev.getDeviceInfo().type = 12;
                    return true;
                case 6144:
                    this.mFtDev.getDeviceInfo().type = 10;
                    return true;
                case 6400:
                    this.mFtDev.getDeviceInfo().type = 11;
                    return true;
            }
        }
        return false;
    }

    public I2cMaster getI2cMasterDevice() {
        if (isFT4222Device()) {
            return new FT_4222_I2c_Master(this);
        }
        return null;
    }

    public I2cSlave getI2cSlaveDevice() {
        if (isFT4222Device()) {
            return new FT_4222_I2c_Slave(this);
        }
        return null;
    }

    public SpiMaster getSpiMasterDevice() {
        if (isFT4222Device()) {
            return new FT_4222_Spi_Master(this);
        }
        return null;
    }

    public SpiSlave getSpiSlaveDevice() {
        if (isFT4222Device()) {
            return new FT_4222_Spi_Slave(this);
        }
        return null;
    }

    public Gpio getGpioDevice() {
        if (isFT4222Device()) {
            return new FT_4222_Gpio(this);
        }
        return null;
    }
}
