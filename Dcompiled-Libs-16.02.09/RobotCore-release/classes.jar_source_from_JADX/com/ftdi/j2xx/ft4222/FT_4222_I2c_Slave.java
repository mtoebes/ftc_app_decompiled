package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.interfaces.I2cSlave;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.Telemetry;

public class FT_4222_I2c_Slave implements I2cSlave {
    FT_4222_Device f72a;
    FT_Device f73b;

    public FT_4222_I2c_Slave(FT_4222_Device ft4222Device) {
        this.f72a = ft4222Device;
        this.f73b = this.f72a.mFtDev;
    }

    int m93a(int i, int i2) {
        return this.f73b.VendorCmdSet(33, (i2 << 8) | i);
    }

    public int init() {
        int init = this.f72a.init();
        if (init != 0) {
            return init;
        }
        if (!m96a()) {
            return FT4222_STATUS.FT4222_I2C_NOT_SUPPORTED_IN_THIS_MODE;
        }
        init = m93a(5, 2);
        if (init < 0) {
            return init;
        }
        this.f72a.mChipStatus.f90g = (byte) 2;
        return 0;
    }

    public int reset() {
        int a = m94a(false);
        return a != 0 ? a : m93a(91, 1);
    }

    public int getAddress(int[] addr) {
        byte[] bArr = new byte[1];
        int a = m94a(false);
        if (a != 0) {
            return a;
        }
        if (this.f73b.VendorCmdGet(33, 92, bArr, 1) < 0) {
            return 18;
        }
        addr[0] = bArr[0];
        return 0;
    }

    public int setAddress(int addr) {
        byte[] bArr = new byte[]{(byte) (addr & Telemetry.cbTagMax)};
        int a = m94a(false);
        if (a != 0) {
            return a;
        }
        return m93a(92, bArr[0]) < 0 ? 18 : 0;
    }

    public int read(byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        int[] iArr = new int[1];
        long currentTimeMillis = System.currentTimeMillis();
        int readTimeout = this.f73b.getReadTimeout();
        if (sizeToTransfer < 1) {
            return 6;
        }
        int a = m94a(false);
        if (a != 0) {
            return a;
        }
        a = m95a(iArr);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer > iArr[0]) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        a = this.f73b.getQueueStatus();
        while (a < sizeToTransfer && System.currentTimeMillis() - currentTimeMillis < ((long) readTimeout)) {
            a = this.f73b.getQueueStatus();
        }
        if (a <= sizeToTransfer) {
            sizeToTransfer = a;
        }
        a = this.f73b.read(buffer, sizeToTransfer);
        if (a < 0) {
            return FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
        }
        sizeTransferred[0] = a;
        return 0;
    }

    public int write(byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        int[] iArr = new int[1];
        if (sizeToTransfer < 1) {
            return 6;
        }
        int a = m94a(false);
        if (a != 0) {
            return a;
        }
        a = m95a(iArr);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer > iArr[0]) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        a = this.f73b.write(buffer, sizeToTransfer);
        sizeTransferred[0] = a;
        if (sizeToTransfer == a) {
            return 0;
        }
        return 10;
    }

    boolean m96a() {
        if (this.f72a.mChipStatus.f84a == null || this.f72a.mChipStatus.f84a == 3) {
            return true;
        }
        return false;
    }

    int m94a(boolean z) {
        if (z) {
            if (this.f72a.mChipStatus.f90g != 1) {
                return FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
            }
        } else if (this.f72a.mChipStatus.f90g != 2) {
            return FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
        }
        return 0;
    }

    int m95a(int[] iArr) {
        iArr[0] = 0;
        int maxBuckSize = this.f72a.getMaxBuckSize();
        switch (this.f72a.mChipStatus.f90g) {
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                iArr[0] = maxBuckSize - 4;
                return 0;
            default:
                return 17;
        }
    }
}
