package com.ftdi.j2xx;

import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.GPIO_Tigger;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.RobocolConfig;
import com.qualcomm.robotcore.robocol.Telemetry;

/* renamed from: com.ftdi.j2xx.g */
class C0014g extends C0004k {
    C0014g(FT_Device fT_Device) throws D2xxException {
        super(fT_Device);
        m40a((byte) 15);
    }

    short m104a(FT_EEPROM ft_eeprom) {
        int[] iArr = new int[this.b];
        if (ft_eeprom.getClass() != FT_EEPROM_232H.class) {
            return (short) 1;
        }
        FT_EEPROM_232H ft_eeprom_232h = (FT_EEPROM_232H) ft_eeprom;
        try {
            if (ft_eeprom_232h.FIFO) {
                iArr[0] = iArr[0] | 1;
            } else if (ft_eeprom_232h.FIFOTarget) {
                iArr[0] = iArr[0] | 2;
            } else if (ft_eeprom_232h.FastSerial) {
                iArr[0] = iArr[0] | 4;
            }
            if (ft_eeprom_232h.FT1248) {
                iArr[0] = iArr[0] | 8;
            }
            if (ft_eeprom_232h.LoadVCP) {
                iArr[0] = iArr[0] | 16;
            }
            if (ft_eeprom_232h.FT1248ClockPolarity) {
                iArr[0] = iArr[0] | 256;
            }
            if (ft_eeprom_232h.FT1248LSB) {
                iArr[0] = iArr[0] | 512;
            }
            if (ft_eeprom_232h.FT1248FlowControl) {
                iArr[0] = iArr[0] | 1024;
            }
            if (ft_eeprom_232h.PowerSaveEnable) {
                iArr[0] = iArr[0] | 32768;
            }
            iArr[1] = ft_eeprom_232h.VendorId;
            iArr[2] = ft_eeprom_232h.ProductId;
            iArr[3] = 2304;
            iArr[4] = m41a((Object) ft_eeprom);
            iArr[5] = m54b(ft_eeprom);
            int i = ft_eeprom_232h.AL_DriveCurrent;
            if (i == -1) {
                i = 0;
            }
            iArr[6] = i | iArr[6];
            if (ft_eeprom_232h.AL_SlowSlew) {
                iArr[6] = iArr[6] | 4;
            }
            if (ft_eeprom_232h.AL_SchmittInput) {
                iArr[6] = iArr[6] | 8;
            }
            i = ft_eeprom_232h.BL_DriveCurrent;
            if (i == -1) {
                i = 0;
            }
            iArr[6] = ((short) (i << 8)) | iArr[6];
            if (ft_eeprom_232h.BL_SlowSlew) {
                iArr[6] = iArr[6] | 1024;
            }
            if (ft_eeprom_232h.BL_SchmittInput) {
                iArr[6] = iArr[6] | 2048;
            }
            int a = m42a(ft_eeprom_232h.Product, iArr, m42a(ft_eeprom_232h.Manufacturer, iArr, 80, 7, false), 8, false);
            if (ft_eeprom_232h.SerNumEnable) {
                m42a(ft_eeprom_232h.SerialNumber, iArr, a, 9, false);
            }
            iArr[10] = 0;
            iArr[11] = 0;
            iArr[12] = 0;
            a = ft_eeprom_232h.CBus2 << 8;
            iArr[12] = ((ft_eeprom_232h.CBus0 | (ft_eeprom_232h.CBus1 << 4)) | a) | (ft_eeprom_232h.CBus3 << 12);
            iArr[13] = 0;
            a = ft_eeprom_232h.CBus6 << 8;
            iArr[13] = ((ft_eeprom_232h.CBus4 | (ft_eeprom_232h.CBus5 << 4)) | a) | (ft_eeprom_232h.CBus7 << 12);
            iArr[14] = 0;
            iArr[14] = ft_eeprom_232h.CBus8 | (ft_eeprom_232h.CBus9 << 4);
            iArr[15] = this.a;
            iArr[69] = 72;
            if (this.a == (short) 70) {
                return (short) 1;
            }
            if (iArr[1] == 0 || iArr[2] == 0) {
                return (short) 2;
            }
            if (m51a(iArr, this.b - 1)) {
                return (short) 0;
            }
            return (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    FT_EEPROM m103a() {
        short s = (short) 0;
        FT_EEPROM ft_eeprom_232h = new FT_EEPROM_232H();
        int[] iArr = new int[this.b];
        if (this.c) {
            return ft_eeprom_232h;
        }
        while (s < this.b) {
            try {
                iArr[s] = m43a(s);
                s = (short) (s + 1);
            } catch (Exception e) {
                return null;
            }
        }
        ft_eeprom_232h.UART = false;
        switch (iArr[0] & 15) {
            case SpiSlaveResponseEvent.OK /*0*/:
                ft_eeprom_232h.UART = true;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                ft_eeprom_232h.FIFO = true;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                ft_eeprom_232h.FIFOTarget = true;
                break;
            case FT_4222_Defines.DEBUG_REQ_READ_SFR /*4*/:
                ft_eeprom_232h.FastSerial = true;
                break;
            case GPIO_Tigger.GPIO_TRIGGER_LEVEL_LOW /*8*/:
                ft_eeprom_232h.FT1248 = true;
                break;
            default:
                ft_eeprom_232h.UART = true;
                break;
        }
        if ((iArr[0] & 16) > 0) {
            ft_eeprom_232h.LoadVCP = true;
            ft_eeprom_232h.LoadD2XX = false;
        } else {
            ft_eeprom_232h.LoadVCP = false;
            ft_eeprom_232h.LoadD2XX = true;
        }
        if ((iArr[0] & 256) > 0) {
            ft_eeprom_232h.FT1248ClockPolarity = true;
        } else {
            ft_eeprom_232h.FT1248ClockPolarity = false;
        }
        if ((iArr[0] & 512) > 0) {
            ft_eeprom_232h.FT1248LSB = true;
        } else {
            ft_eeprom_232h.FT1248LSB = false;
        }
        if ((iArr[0] & 1024) > 0) {
            ft_eeprom_232h.FT1248FlowControl = true;
        } else {
            ft_eeprom_232h.FT1248FlowControl = false;
        }
        if ((iArr[0] & 32768) > 0) {
            ft_eeprom_232h.PowerSaveEnable = true;
        }
        ft_eeprom_232h.VendorId = (short) iArr[1];
        ft_eeprom_232h.ProductId = (short) iArr[2];
        m48a(ft_eeprom_232h, iArr[4]);
        m49a((Object) ft_eeprom_232h, iArr[5]);
        switch (iArr[6] & 3) {
            case SpiSlaveResponseEvent.OK /*0*/:
                ft_eeprom_232h.AL_DriveCurrent = (byte) 0;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                ft_eeprom_232h.AL_DriveCurrent = (byte) 1;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                ft_eeprom_232h.AL_DriveCurrent = (byte) 2;
                break;
            case RobocolConfig.TTL /*3*/:
                ft_eeprom_232h.AL_DriveCurrent = (byte) 3;
                break;
        }
        if ((iArr[6] & 4) > 0) {
            ft_eeprom_232h.AL_SlowSlew = true;
        } else {
            ft_eeprom_232h.AL_SlowSlew = false;
        }
        if ((iArr[6] & 8) > 0) {
            ft_eeprom_232h.AL_SchmittInput = true;
        } else {
            ft_eeprom_232h.AL_SchmittInput = false;
        }
        switch ((short) ((iArr[6] & 768) >> 8)) {
            case SpiSlaveResponseEvent.OK /*0*/:
                ft_eeprom_232h.BL_DriveCurrent = (byte) 0;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                ft_eeprom_232h.BL_DriveCurrent = (byte) 1;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                ft_eeprom_232h.BL_DriveCurrent = (byte) 2;
                break;
            case RobocolConfig.TTL /*3*/:
                ft_eeprom_232h.BL_DriveCurrent = (byte) 3;
                break;
        }
        if ((iArr[6] & 1024) > 0) {
            ft_eeprom_232h.BL_SlowSlew = true;
        } else {
            ft_eeprom_232h.BL_SlowSlew = false;
        }
        if ((iArr[6] & 2048) > 0) {
            ft_eeprom_232h.BL_SchmittInput = true;
        } else {
            ft_eeprom_232h.BL_SchmittInput = false;
        }
        ft_eeprom_232h.CBus0 = (byte) ((short) ((iArr[12] >> 0) & 15));
        ft_eeprom_232h.CBus1 = (byte) ((short) ((iArr[12] >> 4) & 15));
        ft_eeprom_232h.CBus2 = (byte) ((short) ((iArr[12] >> 8) & 15));
        ft_eeprom_232h.CBus3 = (byte) ((short) ((iArr[12] >> 12) & 15));
        ft_eeprom_232h.CBus4 = (byte) ((short) ((iArr[13] >> 0) & 15));
        ft_eeprom_232h.CBus5 = (byte) ((short) ((iArr[13] >> 4) & 15));
        ft_eeprom_232h.CBus6 = (byte) ((short) ((iArr[13] >> 8) & 15));
        ft_eeprom_232h.CBus7 = (byte) ((short) ((iArr[13] >> 12) & 15));
        ft_eeprom_232h.CBus8 = (byte) ((short) ((iArr[14] >> 0) & 15));
        ft_eeprom_232h.CBus9 = (byte) ((short) ((iArr[14] >> 4) & 15));
        ft_eeprom_232h.Manufacturer = m46a((iArr[7] & Telemetry.cbTagMax) / 2, iArr);
        ft_eeprom_232h.Product = m46a((iArr[8] & Telemetry.cbTagMax) / 2, iArr);
        ft_eeprom_232h.SerialNumber = m46a((iArr[9] & Telemetry.cbTagMax) / 2, iArr);
        return ft_eeprom_232h;
    }

    int m106b() {
        int a = m43a((short) 9);
        int i = ((a & Telemetry.cbTagMax) / 2) + 1;
        return (((this.b - i) - 1) - ((((a & 65280) >> 8) / 2) + 1)) * 2;
    }

    int m102a(byte[] bArr) {
        if (bArr.length > m106b()) {
            return 0;
        }
        int[] iArr = new int[this.b];
        for (short s = (short) 0; s < this.b; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        int b = (short) (((this.b - (m106b() / 2)) - 1) - 1);
        int i = 0;
        while (i < bArr.length) {
            int i2;
            if (i + 1 < bArr.length) {
                i2 = bArr[i + 1] & Telemetry.cbTagMax;
            } else {
                i2 = 0;
            }
            int i3 = (bArr[i] & Telemetry.cbTagMax) | (i2 << 8);
            short s2 = (short) (b + 1);
            iArr[b] = i3;
            i += 2;
            short s3 = s2;
        }
        if (iArr[1] == 0 || iArr[2] == 0 || !m51a(iArr, this.b - 1)) {
            return 0;
        }
        return bArr.length;
    }

    byte[] m105a(int i) {
        byte[] bArr = new byte[i];
        if (i == 0 || i > m106b()) {
            return null;
        }
        short b = (short) (((this.b - (m106b() / 2)) - 1) - 1);
        int i2 = 0;
        while (i2 < i) {
            short s = (short) (b + 1);
            int a = m43a(b);
            if (i2 + 1 < bArr.length) {
                bArr[i2 + 1] = (byte) (a & Telemetry.cbTagMax);
            }
            bArr[i2] = (byte) ((a & 65280) >> 8);
            i2 += 2;
            b = s;
        }
        return bArr;
    }
}
