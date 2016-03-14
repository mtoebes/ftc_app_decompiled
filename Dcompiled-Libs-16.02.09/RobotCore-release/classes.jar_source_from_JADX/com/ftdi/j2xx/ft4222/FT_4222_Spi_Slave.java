package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.CHIPTOP_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.interfaces.SpiSlave;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FT_4222_Spi_Slave implements SpiSlave {
    private FT_4222_Device f76a;
    private FT_Device f77b;
    private Lock f78c;

    public FT_4222_Spi_Slave(FT_4222_Device pDevice) {
        this.f76a = pDevice;
        this.f77b = pDevice.mFtDev;
        this.f78c = new ReentrantLock();
    }

    public int init() {
        int i = 0;
        C0010b c0010b = this.f76a.mChipStatus;
        C0009a c0009a = this.f76a.mSpiMasterCfg;
        c0009a.f79a = 1;
        c0009a.f80b = 2;
        c0009a.f81c = 0;
        c0009a.f82d = 0;
        c0009a.f83e = (byte) 1;
        this.f78c.lock();
        this.f76a.cleanRxData();
        if (this.f77b.VendorCmdSet(33, (c0009a.f79a << 8) | 66) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, (c0009a.f80b << 8) | 68) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, (c0009a.f81c << 8) | 69) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, (c0009a.f82d << 8) | 70) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, 67) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, (c0009a.f83e << 8) | 72) < 0) {
            i = 4;
        }
        if (this.f77b.VendorCmdSet(33, 1029) < 0) {
            i = 4;
        }
        this.f78c.unlock();
        c0010b.f90g = (byte) 4;
        return i;
    }

    public int getRxStatus(int[] pRxSize) {
        if (pRxSize == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        int a = m100a();
        if (a != 0) {
            return a;
        }
        this.f78c.lock();
        a = this.f77b.getQueueStatus();
        this.f78c.unlock();
        if (a >= 0) {
            pRxSize[0] = a;
            return 0;
        }
        pRxSize[0] = -1;
        return 4;
    }

    public int read(byte[] buffer, int bufferSize, int[] sizeOfRead) {
        this.f78c.lock();
        if (this.f77b == null || !this.f77b.isOpen()) {
            this.f78c.unlock();
            return 3;
        }
        int read = this.f77b.read(buffer, bufferSize);
        this.f78c.unlock();
        sizeOfRead[0] = read;
        if (read < 0) {
            return 4;
        }
        return 0;
    }

    public int write(byte[] buffer, int bufferSize, int[] sizeTransferred) {
        if (sizeTransferred == null || buffer == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        int a = m100a();
        if (a != 0) {
            return a;
        }
        if (bufferSize > 512) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        this.f78c.lock();
        sizeTransferred[0] = this.f77b.write(buffer, bufferSize);
        this.f78c.unlock();
        if (sizeTransferred[0] == bufferSize) {
            return a;
        }
        Log.e("FTDI_Device::", "Error write =" + bufferSize + " tx=" + sizeTransferred[0]);
        return 4;
    }

    private int m100a() {
        if (this.f76a.mChipStatus.f90g != 4) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_MODE;
        }
        return 0;
    }

    public int reset() {
        int i = 0;
        this.f78c.lock();
        if (this.f77b.VendorCmdSet(33, 74) < 0) {
            i = 4;
        }
        this.f78c.unlock();
        return i;
    }

    public int setDrivingStrength(int clkStrength, int ioStrength, int ssoStregth) {
        int i = 3;
        int i2 = 4;
        int i3 = 0;
        C0010b c0010b = this.f76a.mChipStatus;
        if (c0010b.f90g != (byte) 3 && c0010b.f90g != (byte) 4) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_MODE;
        }
        int i4 = ((clkStrength << 4) | (ioStrength << 2)) | ssoStregth;
        if (c0010b.f90g != (byte) 3) {
            i = 4;
        }
        this.f78c.lock();
        if (this.f77b.VendorCmdSet(33, (i4 << 8) | CHIPTOP_CMD.CHIPTOP_SET_DS_CTL0_REG) < 0) {
            i3 = 4;
        }
        if (this.f77b.VendorCmdSet(33, (i << 8) | 5) >= 0) {
            i2 = i3;
        }
        this.f78c.unlock();
        return i2;
    }
}
