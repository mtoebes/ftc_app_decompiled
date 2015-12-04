package com.ftdi.j2xx.ft4222;

import android.util.Log;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.CHIPTOP_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.ftdi.j2xx.interfaces.SpiMaster;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.RobocolConfig;
import com.qualcomm.robotcore.util.Dimmer;
import junit.framework.Assert;

public class FT_4222_Spi_Master implements SpiMaster {
    private FT_4222_Device f74a;
    private FT_Device f75b;

    public FT_4222_Spi_Master(FT_4222_Device pDevice) {
        this.f74a = pDevice;
        this.f75b = pDevice.mFtDev;
    }

    public int init(int ioLine, int clock, int cpol, int cpha, byte ssoMap) {
        int i = 1;
        C0010b c0010b = this.f74a.mChipStatus;
        C0009a c0009a = this.f74a.mSpiMasterCfg;
        c0009a.f79a = ioLine;
        c0009a.f80b = clock;
        c0009a.f81c = cpol;
        c0009a.f82d = cpha;
        c0009a.f83e = ssoMap;
        if (c0009a.f79a != 1 && c0009a.f79a != 2 && c0009a.f79a != 4) {
            return 6;
        }
        this.f74a.cleanRxData();
        switch (c0010b.f84a) {
            case SpiSlaveResponseEvent.OK /*0*/:
            case RobocolConfig.TTL /*3*/:
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                i = 7;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                i = 15;
                break;
            default:
                i = 0;
                break;
        }
        if ((c0009a.f83e & i) == 0) {
            return 6;
        }
        c0009a.f83e = (byte) (i & c0009a.f83e);
        if (this.f75b.VendorCmdSet(33, (c0009a.f79a << 8) | 66) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, (c0009a.f80b << 8) | 68) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, (c0009a.f81c << 8) | 69) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, (c0009a.f82d << 8) | 70) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, 67) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, (c0009a.f83e << 8) | 72) < 0) {
            return 4;
        }
        if (this.f75b.VendorCmdSet(33, 773) < 0) {
            return 4;
        }
        c0010b.f90g = (byte) 3;
        return 0;
    }

    public int setLines(int spiMode) {
        if (this.f74a.mChipStatus.f90g != 3) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_MODE;
        }
        if (spiMode == 0) {
            return 17;
        }
        if (this.f75b.VendorCmdSet(33, (spiMode << 8) | 66) < 0 || this.f75b.VendorCmdSet(33, 330) < 0) {
            return 4;
        }
        this.f74a.mSpiMasterCfg.f79a = spiMode;
        return 0;
    }

    public int singleWrite(byte[] writeBuffer, int sizeToTransfer, int[] sizeTransferred, boolean isEndTransaction) {
        return singleReadWrite(new byte[writeBuffer.length], writeBuffer, sizeToTransfer, sizeTransferred, isEndTransaction);
    }

    public int singleRead(byte[] readBuffer, int sizeToTransfer, int[] sizeOfRead, boolean isEndTransaction) {
        return singleReadWrite(readBuffer, new byte[readBuffer.length], sizeToTransfer, sizeOfRead, isEndTransaction);
    }

    public int singleReadWrite(byte[] readBuffer, byte[] writeBuffer, int sizeToTransfer, int[] sizeTransferred, boolean isEndTransaction) {
        C0010b c0010b = this.f74a.mChipStatus;
        C0009a c0009a = this.f74a.mSpiMasterCfg;
        if (writeBuffer == null || readBuffer == null || sizeTransferred == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        sizeTransferred[0] = 0;
        if (c0010b.f90g != 3 || c0009a.f79a != 1) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_SINGLE_MODE;
        }
        if (sizeToTransfer == 0) {
            return 6;
        }
        if (sizeToTransfer > writeBuffer.length || sizeToTransfer > readBuffer.length) {
            Assert.assertTrue("sizeToTransfer > writeBuffer.length || sizeToTransfer > readBuffer.length", false);
        }
        if (writeBuffer.length != readBuffer.length || writeBuffer.length == 0) {
            Assert.assertTrue("writeBuffer.length != readBuffer.length || writeBuffer.length == 0", false);
        }
        sizeTransferred[0] = m91a(this.f75b, writeBuffer, readBuffer, sizeToTransfer);
        if (!isEndTransaction) {
            return 0;
        }
        this.f75b.write(null, 0);
        return 0;
    }

    public int multiReadWrite(byte[] readBuffer, byte[] writeBuffer, int singleWriteBytes, int multiWriteBytes, int multiReadBytes, int[] sizeOfRead) {
        C0010b c0010b = this.f74a.mChipStatus;
        C0009a c0009a = this.f74a.mSpiMasterCfg;
        if (multiReadBytes > 0 && readBuffer == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (singleWriteBytes + multiWriteBytes > 0 && writeBuffer == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (multiReadBytes > 0 && sizeOfRead == null) {
            return FT4222_STATUS.FT4222_INVALID_POINTER;
        }
        if (c0010b.f90g != (byte) 3 || c0009a.f79a == 1) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_MULTI_MODE;
        }
        if (singleWriteBytes > 15) {
            Log.e("FTDI_Device::", "The maxium single write bytes are 15 bytes");
            return 6;
        }
        byte[] bArr = new byte[((singleWriteBytes + 5) + multiWriteBytes)];
        bArr[0] = (byte) ((singleWriteBytes & 15) | SPI_SLAVE_CMD.SPI_MASTER_TRANSFER);
        bArr[1] = (byte) ((multiWriteBytes & 65280) >> 8);
        bArr[2] = (byte) (multiWriteBytes & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        bArr[3] = (byte) ((multiReadBytes & 65280) >> 8);
        bArr[4] = (byte) (multiReadBytes & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        for (int i = 0; i < singleWriteBytes + multiWriteBytes; i++) {
            bArr[i + 5] = writeBuffer[i];
        }
        sizeOfRead[0] = m90a(this.f75b, bArr, readBuffer);
        return 0;
    }

    public int reset() {
        if (this.f75b.VendorCmdSet(33, 74) < 0) {
            return 4;
        }
        return 0;
    }

    public int setDrivingStrength(int clkStrength, int ioStrength, int ssoStregth) {
        int i = 3;
        C0010b c0010b = this.f74a.mChipStatus;
        if (c0010b.f90g != (byte) 3 && c0010b.f90g != (byte) 4) {
            return FT4222_STATUS.FT4222_IS_NOT_SPI_MODE;
        }
        int i2 = ((clkStrength << 4) | (ioStrength << 2)) | ssoStregth;
        if (c0010b.f90g != (byte) 3) {
            i = 4;
        }
        if (this.f75b.VendorCmdSet(33, (i2 << 8) | CHIPTOP_CMD.CHIPTOP_SET_DS_CTL0_REG) < 0 || this.f75b.VendorCmdSet(33, (r0 << 8) | 5) < 0) {
            return 4;
        }
        return 0;
    }

    private int m90a(FT_Device fT_Device, byte[] bArr, byte[] bArr2) {
        if (fT_Device == null || !fT_Device.isOpen()) {
            return -1;
        }
        fT_Device.write(bArr, bArr.length);
        int i = 0;
        int i2 = 0;
        while (i < bArr2.length && r2 < Dimmer.DEFAULT_DIM_TIME) {
            int queueStatus = fT_Device.getQueueStatus();
            if (queueStatus > 0) {
                byte[] bArr3 = new byte[queueStatus];
                queueStatus = fT_Device.read(bArr3, queueStatus);
                Assert.assertEquals(bArr3.length == queueStatus, true);
                for (i2 = 0; i2 < bArr3.length; i2++) {
                    if (i + i2 < bArr2.length) {
                        bArr2[i + i2] = bArr3[i2];
                    }
                }
                i += queueStatus;
                i2 = 0;
            }
            try {
                Thread.sleep((long) 10);
                i2 += 10;
            } catch (InterruptedException e) {
                i2 = Dimmer.DEFAULT_DIM_TIME;
            }
        }
        if (bArr2.length == i && r2 <= Dimmer.DEFAULT_DIM_TIME) {
            return i;
        }
        Log.e("FTDI_Device::", "MultiReadWritePackage timeout!!!!");
        return -1;
    }

    private int m91a(FT_Device fT_Device, byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        byte[] bArr3 = new byte[D2xxManager.FTDI_BREAK_ON];
        byte[] bArr4 = new byte[bArr3.length];
        int length = i / bArr3.length;
        int length2 = i % bArr3.length;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (i3 < length) {
            int i6 = i4;
            for (i4 = 0; i4 < bArr3.length; i4++) {
                bArr3[i4] = bArr[i6];
                i6++;
            }
            if (m92b(fT_Device, bArr3, bArr4) <= 0) {
                return -1;
            }
            for (byte b : bArr4) {
                bArr2[i5] = b;
                i5++;
            }
            i3++;
            i4 = i6;
        }
        if (length2 <= 0) {
            return i5;
        }
        byte[] bArr5 = new byte[length2];
        bArr3 = new byte[bArr5.length];
        i6 = i4;
        for (i4 = 0; i4 < bArr5.length; i4++) {
            bArr5[i4] = bArr[i6];
            i6++;
        }
        if (m92b(fT_Device, bArr5, bArr3) <= 0) {
            return -1;
        }
        while (i2 < bArr3.length) {
            bArr2[i5] = bArr3[i2];
            i5++;
            i2++;
        }
        return i5;
    }

    private int m92b(FT_Device fT_Device, byte[] bArr, byte[] bArr2) {
        if (fT_Device == null || !fT_Device.isOpen()) {
            return -1;
        }
        Assert.assertEquals(bArr.length == bArr2.length, true);
        if (bArr.length != fT_Device.write(bArr, bArr.length)) {
            Log.e("FTDI_Device::", "setReadWritePackage Incomplete Write Error!!!");
            return -1;
        }
        int i = 0;
        int i2 = 0;
        while (i < bArr2.length && r3 < Dimmer.DEFAULT_DIM_TIME) {
            int queueStatus = fT_Device.getQueueStatus();
            if (queueStatus > 0) {
                byte[] bArr3 = new byte[queueStatus];
                queueStatus = fT_Device.read(bArr3, queueStatus);
                Assert.assertEquals(bArr3.length == queueStatus, true);
                for (i2 = 0; i2 < bArr3.length; i2++) {
                    if (i + i2 < bArr2.length) {
                        bArr2[i + i2] = bArr3[i2];
                    }
                }
                i += queueStatus;
                i2 = 0;
            }
            try {
                Thread.sleep((long) 10);
                i2 += 10;
            } catch (InterruptedException e) {
                i2 = Dimmer.DEFAULT_DIM_TIME;
            }
        }
        if (bArr2.length == i && r3 <= Dimmer.DEFAULT_DIM_TIME) {
            return i;
        }
        Log.e("FTDI_Device::", "SingleReadWritePackage timeout!!!!");
        return -1;
    }
}
