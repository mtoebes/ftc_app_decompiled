package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.robocol.RobocolConfig;
import com.qualcomm.robotcore.robocol.Telemetry;

/* renamed from: com.ftdi.j2xx.l */
class C0018l extends C0004k {
    private static FT_Device f107d;

    C0018l(FT_Device fT_Device) {
        super(fT_Device);
        f107d = fT_Device;
        this.b = SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
        this.a = (short) 1;
    }

    short m126a(FT_EEPROM ft_eeprom) {
        int[] iArr = new int[this.b];
        if (ft_eeprom.getClass() != FT_EEPROM_X_Series.class) {
            return (short) 1;
        }
        FT_EEPROM_X_Series fT_EEPROM_X_Series = (FT_EEPROM_X_Series) ft_eeprom;
        short s = (short) 0;
        do {
            iArr[s] = m43a(s);
            s = (short) (s + 1);
        } while (s < this.b);
        try {
            int i;
            byte b;
            iArr[0] = 0;
            if (fT_EEPROM_X_Series.BCDEnable) {
                iArr[0] = iArr[0] | 1;
            }
            if (fT_EEPROM_X_Series.BCDForceCBusPWREN) {
                iArr[0] = iArr[0] | 2;
            }
            if (fT_EEPROM_X_Series.BCDDisableSleep) {
                iArr[0] = iArr[0] | 4;
            }
            if (fT_EEPROM_X_Series.RS485EchoSuppress) {
                iArr[0] = iArr[0] | 8;
            }
            if (fT_EEPROM_X_Series.A_LoadVCP) {
                iArr[0] = iArr[0] | SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
            }
            if (fT_EEPROM_X_Series.PowerSaveEnable) {
                if (fT_EEPROM_X_Series.CBus0 == 17) {
                    s = (short) 1;
                } else {
                    s = (short) 0;
                }
                if (fT_EEPROM_X_Series.CBus1 == 17) {
                    s = (short) 1;
                }
                if (fT_EEPROM_X_Series.CBus2 == 17) {
                    s = (short) 1;
                }
                if (fT_EEPROM_X_Series.CBus3 == 17) {
                    s = (short) 1;
                }
                if (fT_EEPROM_X_Series.CBus4 == 17) {
                    s = (short) 1;
                }
                if (fT_EEPROM_X_Series.CBus5 == 17) {
                    s = (short) 1;
                }
                if (fT_EEPROM_X_Series.CBus6 == 17) {
                    s = (short) 1;
                }
                if (s == (short) 0) {
                    return (short) 1;
                }
                iArr[0] = iArr[0] | 64;
            }
            iArr[1] = fT_EEPROM_X_Series.VendorId;
            iArr[2] = fT_EEPROM_X_Series.ProductId;
            iArr[3] = 4096;
            iArr[4] = m41a((Object) ft_eeprom);
            iArr[5] = m54b(ft_eeprom);
            if (fT_EEPROM_X_Series.FT1248ClockPolarity) {
                iArr[5] = iArr[5] | 16;
            }
            if (fT_EEPROM_X_Series.FT1248LSB) {
                iArr[5] = iArr[5] | 32;
            }
            if (fT_EEPROM_X_Series.FT1248FlowControl) {
                iArr[5] = iArr[5] | 64;
            }
            if (fT_EEPROM_X_Series.I2CDisableSchmitt) {
                iArr[5] = iArr[5] | SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
            }
            if (fT_EEPROM_X_Series.InvertTXD) {
                iArr[5] = iArr[5] | 256;
            }
            if (fT_EEPROM_X_Series.InvertRXD) {
                iArr[5] = iArr[5] | 512;
            }
            if (fT_EEPROM_X_Series.InvertRTS) {
                iArr[5] = iArr[5] | 1024;
            }
            if (fT_EEPROM_X_Series.InvertCTS) {
                iArr[5] = iArr[5] | 2048;
            }
            if (fT_EEPROM_X_Series.InvertDTR) {
                iArr[5] = iArr[5] | 4096;
            }
            if (fT_EEPROM_X_Series.InvertDSR) {
                iArr[5] = iArr[5] | 8192;
            }
            if (fT_EEPROM_X_Series.InvertDCD) {
                iArr[5] = iArr[5] | D2xxManager.FTDI_BREAK_ON;
            }
            if (fT_EEPROM_X_Series.InvertRI) {
                iArr[5] = iArr[5] | 32768;
            }
            iArr[6] = 0;
            int i2 = fT_EEPROM_X_Series.AD_DriveCurrent;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[6] = i2 | iArr[6];
            if (fT_EEPROM_X_Series.AD_SlowSlew) {
                iArr[6] = iArr[6] | 4;
            }
            if (fT_EEPROM_X_Series.AD_SchmittInput) {
                iArr[6] = iArr[6] | 8;
            }
            i2 = fT_EEPROM_X_Series.AC_DriveCurrent;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[6] = ((short) (i2 << 4)) | iArr[6];
            if (fT_EEPROM_X_Series.AC_SlowSlew) {
                iArr[6] = iArr[6] | 64;
            }
            if (fT_EEPROM_X_Series.AC_SchmittInput) {
                iArr[6] = iArr[6] | SPI_SLAVE_CMD.SPI_MASTER_TRANSFER;
            }
            int a = m42a(fT_EEPROM_X_Series.Product, iArr, m42a(fT_EEPROM_X_Series.Manufacturer, iArr, 80, 7, false), 8, false);
            if (fT_EEPROM_X_Series.SerNumEnable) {
                m42a(fT_EEPROM_X_Series.SerialNumber, iArr, a, 9, false);
            }
            iArr[10] = fT_EEPROM_X_Series.I2CSlaveAddress;
            iArr[11] = fT_EEPROM_X_Series.I2CDeviceID & Telemetry.cbValueMax;
            iArr[12] = fT_EEPROM_X_Series.I2CDeviceID >> 16;
            byte b2 = fT_EEPROM_X_Series.CBus0;
            if (b2 == (byte) -1) {
                i = 0;
            } else {
                b = b2;
            }
            i2 = fT_EEPROM_X_Series.CBus1;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[13] = (short) ((i2 << 8) | i);
            b2 = fT_EEPROM_X_Series.CBus2;
            if (b2 == (byte) -1) {
                i = 0;
            } else {
                b = b2;
            }
            i2 = fT_EEPROM_X_Series.CBus3;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[14] = (short) ((i2 << 8) | i);
            b2 = fT_EEPROM_X_Series.CBus4;
            if (b2 == (byte) -1) {
                i = 0;
            } else {
                b = b2;
            }
            i2 = fT_EEPROM_X_Series.CBus5;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[15] = (short) ((i2 << 8) | i);
            i2 = fT_EEPROM_X_Series.CBus6;
            if (i2 == -1) {
                i2 = 0;
            }
            iArr[16] = (short) i2;
            if (iArr[1] == 0 || iArr[2] == 0) {
                return (short) 2;
            }
            if (m129b(iArr, this.b - 1)) {
                return (short) 0;
            }
            return (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    boolean m129b(int[] iArr, int i) {
        int i2 = 43690;
        int i3 = 0;
        do {
            int i4 = iArr[i3] & Telemetry.cbValueMax;
            m50a((short) i3, (short) i4);
            i2 = (i2 ^ i4) & Telemetry.cbValueMax;
            i4 = (i2 << 1) & Telemetry.cbValueMax;
            if ((i2 & 32768) > 0) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            i2 = (i2 | i4) & Telemetry.cbValueMax;
            i3++;
            if (i3 == 18) {
                i3 = 64;
                continue;
            }
        } while (i3 != i);
        m50a((short) i, (short) i2);
        return true;
    }

    FT_EEPROM m125a() {
        short s = (short) 0;
        FT_EEPROM fT_EEPROM_X_Series = new FT_EEPROM_X_Series();
        int[] iArr = new int[this.b];
        while (s < this.b) {
            try {
                iArr[s] = m43a(s);
                s = (short) (s + 1);
            } catch (Exception e) {
                return null;
            }
        }
        if ((iArr[0] & 1) > 0) {
            fT_EEPROM_X_Series.BCDEnable = true;
        } else {
            fT_EEPROM_X_Series.BCDEnable = false;
        }
        if ((iArr[0] & 2) > 0) {
            fT_EEPROM_X_Series.BCDForceCBusPWREN = true;
        } else {
            fT_EEPROM_X_Series.BCDForceCBusPWREN = false;
        }
        if ((iArr[0] & 4) > 0) {
            fT_EEPROM_X_Series.BCDDisableSleep = true;
        } else {
            fT_EEPROM_X_Series.BCDDisableSleep = false;
        }
        if ((iArr[0] & 8) > 0) {
            fT_EEPROM_X_Series.RS485EchoSuppress = true;
        } else {
            fT_EEPROM_X_Series.RS485EchoSuppress = false;
        }
        if ((iArr[0] & 64) > 0) {
            fT_EEPROM_X_Series.PowerSaveEnable = true;
        } else {
            fT_EEPROM_X_Series.PowerSaveEnable = false;
        }
        if ((iArr[0] & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) > 0) {
            fT_EEPROM_X_Series.A_LoadVCP = true;
            fT_EEPROM_X_Series.A_LoadD2XX = false;
        } else {
            fT_EEPROM_X_Series.A_LoadVCP = false;
            fT_EEPROM_X_Series.A_LoadD2XX = true;
        }
        fT_EEPROM_X_Series.VendorId = (short) iArr[1];
        fT_EEPROM_X_Series.ProductId = (short) iArr[2];
        m48a(fT_EEPROM_X_Series, iArr[4]);
        m49a((Object) fT_EEPROM_X_Series, iArr[5]);
        if ((iArr[5] & 16) > 0) {
            fT_EEPROM_X_Series.FT1248ClockPolarity = true;
        } else {
            fT_EEPROM_X_Series.FT1248ClockPolarity = false;
        }
        if ((iArr[5] & 32) > 0) {
            fT_EEPROM_X_Series.FT1248LSB = true;
        } else {
            fT_EEPROM_X_Series.FT1248LSB = false;
        }
        if ((iArr[5] & 64) > 0) {
            fT_EEPROM_X_Series.FT1248FlowControl = true;
        } else {
            fT_EEPROM_X_Series.FT1248FlowControl = false;
        }
        if ((iArr[5] & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) > 0) {
            fT_EEPROM_X_Series.I2CDisableSchmitt = true;
        } else {
            fT_EEPROM_X_Series.I2CDisableSchmitt = false;
        }
        if ((iArr[5] & 256) == 256) {
            fT_EEPROM_X_Series.InvertTXD = true;
        } else {
            fT_EEPROM_X_Series.InvertTXD = false;
        }
        if ((iArr[5] & 512) == 512) {
            fT_EEPROM_X_Series.InvertRXD = true;
        } else {
            fT_EEPROM_X_Series.InvertRXD = false;
        }
        if ((iArr[5] & 1024) == 1024) {
            fT_EEPROM_X_Series.InvertRTS = true;
        } else {
            fT_EEPROM_X_Series.InvertRTS = false;
        }
        if ((iArr[5] & 2048) == 2048) {
            fT_EEPROM_X_Series.InvertCTS = true;
        } else {
            fT_EEPROM_X_Series.InvertCTS = false;
        }
        if ((iArr[5] & 4096) == 4096) {
            fT_EEPROM_X_Series.InvertDTR = true;
        } else {
            fT_EEPROM_X_Series.InvertDTR = false;
        }
        if ((iArr[5] & 8192) == 8192) {
            fT_EEPROM_X_Series.InvertDSR = true;
        } else {
            fT_EEPROM_X_Series.InvertDSR = false;
        }
        if ((iArr[5] & D2xxManager.FTDI_BREAK_ON) == D2xxManager.FTDI_BREAK_ON) {
            fT_EEPROM_X_Series.InvertDCD = true;
        } else {
            fT_EEPROM_X_Series.InvertDCD = false;
        }
        if ((iArr[5] & 32768) == 32768) {
            fT_EEPROM_X_Series.InvertRI = true;
        } else {
            fT_EEPROM_X_Series.InvertRI = false;
        }
        switch ((short) (iArr[6] & 3)) {
            case SpiSlaveResponseEvent.OK /*0*/:
                fT_EEPROM_X_Series.AD_DriveCurrent = (byte) 0;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                fT_EEPROM_X_Series.AD_DriveCurrent = (byte) 1;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                fT_EEPROM_X_Series.AD_DriveCurrent = (byte) 2;
                break;
            case RobocolConfig.TTL /*3*/:
                fT_EEPROM_X_Series.AD_DriveCurrent = (byte) 3;
                break;
        }
        if (((short) (iArr[6] & 4)) == (short) 4) {
            fT_EEPROM_X_Series.AD_SlowSlew = true;
        } else {
            fT_EEPROM_X_Series.AD_SlowSlew = false;
        }
        if (((short) (iArr[6] & 8)) == (short) 8) {
            fT_EEPROM_X_Series.AD_SchmittInput = true;
        } else {
            fT_EEPROM_X_Series.AD_SchmittInput = false;
        }
        switch ((short) ((iArr[6] & 48) >> 4)) {
            case SpiSlaveResponseEvent.OK /*0*/:
                fT_EEPROM_X_Series.AC_DriveCurrent = (byte) 0;
                break;
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                fT_EEPROM_X_Series.AC_DriveCurrent = (byte) 1;
                break;
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                fT_EEPROM_X_Series.AC_DriveCurrent = (byte) 2;
                break;
            case RobocolConfig.TTL /*3*/:
                fT_EEPROM_X_Series.AC_DriveCurrent = (byte) 3;
                break;
        }
        if (((short) (iArr[6] & 64)) == (short) 64) {
            fT_EEPROM_X_Series.AC_SlowSlew = true;
        } else {
            fT_EEPROM_X_Series.AC_SlowSlew = false;
        }
        if (((short) (iArr[6] & SPI_SLAVE_CMD.SPI_MASTER_TRANSFER)) == (short) 128) {
            fT_EEPROM_X_Series.AC_SchmittInput = true;
        } else {
            fT_EEPROM_X_Series.AC_SchmittInput = false;
        }
        fT_EEPROM_X_Series.I2CSlaveAddress = iArr[10];
        fT_EEPROM_X_Series.I2CDeviceID = iArr[11];
        fT_EEPROM_X_Series.I2CDeviceID |= (iArr[12] & Telemetry.cbTagMax) << 16;
        fT_EEPROM_X_Series.CBus0 = (byte) (iArr[13] & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus1 = (byte) ((iArr[13] >> 8) & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus2 = (byte) (iArr[14] & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus3 = (byte) ((iArr[14] >> 8) & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus4 = (byte) (iArr[15] & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus5 = (byte) ((iArr[15] >> 8) & Telemetry.cbTagMax);
        fT_EEPROM_X_Series.CBus6 = (byte) (iArr[16] & Telemetry.cbTagMax);
        this.a = (short) (iArr[73] >> 8);
        fT_EEPROM_X_Series.Manufacturer = m46a((iArr[7] & Telemetry.cbTagMax) / 2, iArr);
        fT_EEPROM_X_Series.Product = m46a((iArr[8] & Telemetry.cbTagMax) / 2, iArr);
        fT_EEPROM_X_Series.SerialNumber = m46a((iArr[9] & Telemetry.cbTagMax) / 2, iArr);
        return fT_EEPROM_X_Series;
    }

    int m128b() {
        int a = m43a((short) 9);
        return (((this.b - 1) - 1) - (((((a & 65280) >> 8) / 2) + ((a & Telemetry.cbTagMax) / 2)) + 1)) * 2;
    }

    int m124a(byte[] bArr) {
        if (bArr.length > m128b()) {
            return 0;
        }
        int[] iArr = new int[this.b];
        for (short s = (short) 0; s < this.b; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        int b = (short) (((this.b - (m128b() / 2)) - 1) - 1);
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
        if (iArr[1] == 0 || iArr[2] == 0 || !m129b(iArr, this.b - 1)) {
            return 0;
        }
        return bArr.length;
    }

    byte[] m127a(int i) {
        byte[] bArr = new byte[i];
        if (i == 0 || i > m128b()) {
            return null;
        }
        short b = (short) (((this.b - (m128b() / 2)) - 1) - 1);
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
