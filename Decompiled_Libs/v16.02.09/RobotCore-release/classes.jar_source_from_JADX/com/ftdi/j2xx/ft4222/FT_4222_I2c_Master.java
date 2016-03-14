package com.ftdi.j2xx.ft4222;

import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.CHIPTOP_CMD;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.ftdi.j2xx.interfaces.I2cMaster;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.RobocolConfig;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;

public class FT_4222_I2c_Master implements I2cMaster {
    FT_4222_Device f69a;
    FT_Device f70b;
    int f71c;

    public FT_4222_I2c_Master(FT_4222_Device ft4222Device) {
        this.f69a = ft4222Device;
        this.f70b = this.f69a.mFtDev;
    }

    int m87a(int i, int i2) {
        return this.f70b.VendorCmdSet(33, (i2 << 8) | i);
    }

    public int init(int kbps) {
        byte[] bArr = new byte[1];
        int init = this.f69a.init();
        if (init != 0) {
            return init;
        }
        if (!m91a()) {
            return FT4222_STATUS.FT4222_I2C_NOT_SUPPORTED_IN_THIS_MODE;
        }
        m87a(81, 0);
        init = this.f69a.getClock(bArr);
        if (init != 0) {
            return init;
        }
        int b = m85b(bArr[0], kbps);
        init = m87a(5, 1);
        if (init < 0) {
            return init;
        }
        this.f69a.mChipStatus.f90g = (byte) 1;
        init = m87a(82, b);
        if (init < 0) {
            return init;
        }
        this.f71c = kbps;
        return 0;
    }

    public int reset() {
        int a = m88a(true);
        return a != 0 ? a : m87a(81, 1);
    }

    public int read(int deviceAddress, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        return readEx(deviceAddress, 6, buffer, sizeToTransfer, sizeTransferred);
    }

    public int readEx(int deviceAddress, int flag, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        short s = (short) (Telemetry.cbValueMax & deviceAddress);
        short s2 = (short) sizeToTransfer;
        int[] iArr = new int[1];
        byte[] bArr = new byte[4];
        long currentTimeMillis = System.currentTimeMillis();
        int readTimeout = this.f70b.getReadTimeout();
        int a = m86a(flag);
        if (a != 0) {
            return a;
        }
        a = m92b(deviceAddress);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer < 1) {
            return 6;
        }
        a = m88a(true);
        if (a != 0) {
            return a;
        }
        a = m90a(iArr);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer > iArr[0]) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        bArr[0] = (byte) ((short) ((s << 1) + 1));
        bArr[1] = (byte) flag;
        bArr[2] = (byte) ((s2 >> 8) & Telemetry.cbTagMax);
        bArr[3] = (byte) (s2 & Telemetry.cbTagMax);
        if (4 != this.f70b.write(bArr, 4)) {
            return FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
        }
        a = this.f70b.getQueueStatus();
        while (a < sizeToTransfer && System.currentTimeMillis() - currentTimeMillis < ((long) readTimeout)) {
            a = this.f70b.getQueueStatus();
        }
        if (a <= sizeToTransfer) {
            sizeToTransfer = a;
        }
        a = this.f70b.read(buffer, sizeToTransfer);
        sizeTransferred[0] = a;
        if (a >= 0) {
            return 0;
        }
        return FT4222_STATUS.FT4222_FAILED_TO_READ_DEVICE;
    }

    public int write(int deviceAddress, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        return writeEx(deviceAddress, 6, buffer, sizeToTransfer, sizeTransferred);
    }

    public int writeEx(int deviceAddress, int flag, byte[] buffer, int sizeToTransfer, int[] sizeTransferred) {
        short s = (short) deviceAddress;
        short s2 = (short) sizeToTransfer;
        byte[] bArr = new byte[(sizeToTransfer + 4)];
        int[] iArr = new int[1];
        int a = m86a(flag);
        if (a != 0) {
            return a;
        }
        a = m92b(deviceAddress);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer < 1) {
            return 6;
        }
        a = m88a(true);
        if (a != 0) {
            return a;
        }
        a = m90a(iArr);
        if (a != 0) {
            return a;
        }
        if (sizeToTransfer > iArr[0]) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        sizeTransferred[0] = 0;
        bArr[0] = (byte) ((short) (s << 1));
        bArr[1] = (byte) flag;
        bArr[2] = (byte) ((s2 >> 8) & Telemetry.cbTagMax);
        bArr[3] = (byte) (s2 & Telemetry.cbTagMax);
        for (a = 0; a < sizeToTransfer; a++) {
            bArr[a + 4] = buffer[a];
        }
        sizeTransferred[0] = this.f70b.write(bArr, sizeToTransfer + 4) - 4;
        if (sizeToTransfer == sizeTransferred[0]) {
            return 0;
        }
        return 10;
    }

    public int getStatus(int deviceAddress, byte[] controllerStatus) {
        int a = m88a(true);
        if (a != 0) {
            return a;
        }
        if (this.f70b.VendorCmdGet(34, 62900, controllerStatus, 1) < 0) {
            return 18;
        }
        return 0;
    }

    boolean m91a() {
        if (this.f69a.mChipStatus.f84a == null || this.f69a.mChipStatus.f84a == 3) {
            return true;
        }
        return false;
    }

    int m88a(boolean z) {
        if (z) {
            if (this.f69a.mChipStatus.f90g != 1) {
                return FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
            }
        } else if (this.f69a.mChipStatus.f90g != 2) {
            return FT4222_STATUS.FT4222_IS_NOT_I2C_MODE;
        }
        return 0;
    }

    int m86a(int i) {
        if (this.f70b == null || !this.f70b.isOpen()) {
            return 3;
        }
        if (i == 6) {
            return 0;
        }
        char[] cArr = new char[1];
        m89a(cArr);
        if (cArr[0] < 'B') {
            return FT4222_STATUS.FT4222_FUN_NOT_SUPPORT;
        }
        return 0;
    }

    int m92b(int i) {
        if ((64512 & i) > 0) {
            return FT4222_STATUS.FT4222_WRONG_I2C_ADDR;
        }
        return 0;
    }

    private int m85b(int i, int i2) {
        double d;
        switch (i) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                d = 41.666666666666664d;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                d = 20.833333333333332d;
                break;
            case RobocolConfig.TTL /*3*/:
                d = 12.5d;
                break;
            default:
                d = 16.666666666666668d;
                break;
        }
        if (60 <= i2 && i2 <= 100) {
            int i3 = (int) ((((ElapsedTime.dMILLIS_IN_NANO / ((double) i2)) / (d * 8.0d)) - Servo.MAX_POSITION) + 0.5d);
            if (i3 > 127) {
                return 127;
            }
            return i3;
        } else if (100 < i2 && i2 <= 400) {
            return ((int) ((((ElapsedTime.dMILLIS_IN_NANO / ((double) i2)) / (d * 6.0d)) - Servo.MAX_POSITION) + 0.5d)) | CHIPTOP_CMD.CHIPTOP_WRITE_OTP_TEST_BYTE;
        } else {
            if (400 < i2 && i2 <= RobocolConfig.TIMEOUT) {
                return ((int) ((((ElapsedTime.dMILLIS_IN_NANO / ((double) i2)) / (d * 6.0d)) - Servo.MAX_POSITION) + 0.5d)) | CHIPTOP_CMD.CHIPTOP_WRITE_OTP_TEST_BYTE;
            }
            if (RobocolConfig.TIMEOUT >= i2 || i2 > 3400) {
                return 74;
            }
            return (((int) ((((ElapsedTime.dMILLIS_IN_NANO / ((double) i2)) / (d * 6.0d)) - Servo.MAX_POSITION) + 0.5d)) | SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) & -65;
        }
    }

    int m90a(int[] iArr) {
        iArr[0] = 0;
        int maxBuckSize = this.f69a.getMaxBuckSize();
        switch (this.f69a.mChipStatus.f90g) {
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                iArr[0] = maxBuckSize - 4;
                return 0;
            default:
                return 17;
        }
    }

    int m89a(char[] cArr) {
        byte[] bArr = new byte[12];
        if (this.f70b.VendorCmdGet(32, 0, bArr, 12) < 0) {
            return 18;
        }
        if (bArr[2] == 1) {
            cArr[0] = 'A';
            return 0;
        } else if (bArr[2] != (byte) 2) {
            return 0;
        } else {
            cArr[0] = 'B';
            return 0;
        }
    }
}
