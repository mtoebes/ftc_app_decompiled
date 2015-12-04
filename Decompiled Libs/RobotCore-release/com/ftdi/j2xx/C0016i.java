package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.qualcomm.robotcore.robocol.Command;

/* renamed from: com.ftdi.j2xx.i */
class C0016i extends C0004k {
    private static FT_Device f106d;

    C0016i(FT_Device fT_Device) {
        super(fT_Device);
        f106d = fT_Device;
    }

    boolean m109a(short s, short s2) {
        boolean z = false;
        int i = s2 & 65535;
        int i2 = s & 65535;
        if (s < D2xxManager.FT_FLOW_XON_XOFF) {
            byte latencyTimer = f106d.getLatencyTimer();
            f106d.setLatencyTimer((byte) 119);
            if (f106d.m28c().controlTransfer(64, 145, i, i2, null, 0, 0) == 0) {
                z = true;
            }
            f106d.setLatencyTimer(latencyTimer);
        }
        return z;
    }

    short m108a(FT_EEPROM ft_eeprom) {
        int[] iArr = new int[80];
        if (ft_eeprom.getClass() != FT_EEPROM_245R.class) {
            return (short) 1;
        }
        FT_EEPROM_245R ft_eeprom_245r = (FT_EEPROM_245R) ft_eeprom;
        for (short s = (short) 0; s < (short) 80; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        try {
            int i = (iArr[0] & 65280) | 0;
            if (ft_eeprom_245r.HighIO) {
                i |= 4;
            }
            if (ft_eeprom_245r.LoadVCP) {
                i |= 8;
            }
            if (ft_eeprom_245r.ExternalOscillator) {
                i |= 2;
            } else {
                i &= 65533;
            }
            iArr[0] = i;
            iArr[1] = ft_eeprom_245r.VendorId;
            iArr[2] = ft_eeprom_245r.ProductId;
            iArr[3] = 1536;
            iArr[4] = m41a((Object) ft_eeprom);
            i = m54b(ft_eeprom);
            if (ft_eeprom_245r.InvertTXD) {
                i |= Command.MAX_COMMAND_LENGTH;
            }
            if (ft_eeprom_245r.InvertRXD) {
                i |= 512;
            }
            if (ft_eeprom_245r.InvertRTS) {
                i |= 1024;
            }
            if (ft_eeprom_245r.InvertCTS) {
                i |= 2048;
            }
            if (ft_eeprom_245r.InvertDTR) {
                i |= 4096;
            }
            if (ft_eeprom_245r.InvertDSR) {
                i |= 8192;
            }
            if (ft_eeprom_245r.InvertDCD) {
                i |= D2xxManager.FTDI_BREAK_ON;
            }
            if (ft_eeprom_245r.InvertRI) {
                i |= 32768;
            }
            iArr[5] = i;
            int i2 = ft_eeprom_245r.CBus2 << 8;
            iArr[10] = ((ft_eeprom_245r.CBus0 | (ft_eeprom_245r.CBus1 << 4)) | i2) | (ft_eeprom_245r.CBus3 << 12);
            iArr[11] = ft_eeprom_245r.CBus4;
            i2 = m42a(ft_eeprom_245r.Product, iArr, m42a(ft_eeprom_245r.Manufacturer, iArr, 12, 7, true), 8, true);
            if (ft_eeprom_245r.SerNumEnable) {
                m42a(ft_eeprom_245r.SerialNumber, iArr, i2, 9, true);
            }
            if (iArr[1] == 0 || iArr[2] == 0) {
                return (short) 2;
            }
            byte latencyTimer = f106d.getLatencyTimer();
            f106d.setLatencyTimer((byte) 119);
            boolean a = m51a(iArr, 80);
            f106d.setLatencyTimer(latencyTimer);
            if (a) {
                return (short) 0;
            }
            return (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    FT_EEPROM m107a() {
        int i;
        FT_EEPROM ft_eeprom_245r = new FT_EEPROM_245R();
        int[] iArr = new int[80];
        for (i = 0; i < 80; i++) {
            iArr[i] = m43a((short) i);
        }
        try {
            if ((iArr[0] & 4) == 4) {
                ft_eeprom_245r.HighIO = true;
            } else {
                ft_eeprom_245r.HighIO = false;
            }
            if ((iArr[0] & 8) == 8) {
                ft_eeprom_245r.LoadVCP = true;
            } else {
                ft_eeprom_245r.LoadVCP = false;
            }
            if ((iArr[0] & 2) == 2) {
                ft_eeprom_245r.ExternalOscillator = true;
            } else {
                ft_eeprom_245r.ExternalOscillator = false;
            }
            ft_eeprom_245r.VendorId = (short) iArr[1];
            ft_eeprom_245r.ProductId = (short) iArr[2];
            m48a(ft_eeprom_245r, iArr[4]);
            m49a((Object) ft_eeprom_245r, iArr[5]);
            if ((iArr[5] & Command.MAX_COMMAND_LENGTH) == Command.MAX_COMMAND_LENGTH) {
                ft_eeprom_245r.InvertTXD = true;
            } else {
                ft_eeprom_245r.InvertTXD = false;
            }
            if ((iArr[5] & 512) == 512) {
                ft_eeprom_245r.InvertRXD = true;
            } else {
                ft_eeprom_245r.InvertRXD = false;
            }
            if ((iArr[5] & 1024) == 1024) {
                ft_eeprom_245r.InvertRTS = true;
            } else {
                ft_eeprom_245r.InvertRTS = false;
            }
            if ((iArr[5] & 2048) == 2048) {
                ft_eeprom_245r.InvertCTS = true;
            } else {
                ft_eeprom_245r.InvertCTS = false;
            }
            if ((iArr[5] & 4096) == 4096) {
                ft_eeprom_245r.InvertDTR = true;
            } else {
                ft_eeprom_245r.InvertDTR = false;
            }
            if ((iArr[5] & 8192) == 8192) {
                ft_eeprom_245r.InvertDSR = true;
            } else {
                ft_eeprom_245r.InvertDSR = false;
            }
            if ((iArr[5] & D2xxManager.FTDI_BREAK_ON) == D2xxManager.FTDI_BREAK_ON) {
                ft_eeprom_245r.InvertDCD = true;
            } else {
                ft_eeprom_245r.InvertDCD = false;
            }
            if ((iArr[5] & 32768) == 32768) {
                ft_eeprom_245r.InvertRI = true;
            } else {
                ft_eeprom_245r.InvertRI = false;
            }
            i = iArr[10];
            ft_eeprom_245r.CBus0 = (byte) (i & 15);
            ft_eeprom_245r.CBus1 = (byte) ((i & 240) >> 4);
            ft_eeprom_245r.CBus2 = (byte) ((i & 3840) >> 8);
            ft_eeprom_245r.CBus3 = (byte) ((i & 61440) >> 12);
            ft_eeprom_245r.CBus4 = (byte) (iArr[11] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
            ft_eeprom_245r.Manufacturer = m46a(((iArr[7] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, iArr);
            ft_eeprom_245r.Product = m46a(((iArr[8] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, iArr);
            ft_eeprom_245r.SerialNumber = m46a(((iArr[9] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) - 128) / 2, iArr);
            return ft_eeprom_245r;
        } catch (Exception e) {
            return null;
        }
    }

    int m111b() {
        return (((63 - ((((((m43a((short) 7) & 65280) >> 8) / 2) + 12) + (((m43a((short) 8) & 65280) >> 8) / 2)) + 1)) - (((m43a((short) 9) & 65280) >> 8) / 2)) - 1) * 2;
    }

    int m106a(byte[] bArr) {
        if (bArr.length > m111b()) {
            return 0;
        }
        int[] iArr = new int[80];
        for (short s = (short) 0; s < (short) 80; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        int b = (short) (((short) ((63 - (m111b() / 2)) - 1)) & 65535);
        int i = 0;
        while (i < bArr.length) {
            int i2;
            if (i + 1 < bArr.length) {
                i2 = bArr[i + 1] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
            } else {
                i2 = 0;
            }
            int i3 = (bArr[i] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST) | (i2 << 8);
            short s2 = (short) (b + 1);
            iArr[b] = i3;
            i += 2;
            short s3 = s2;
        }
        if (iArr[1] == 0 || iArr[2] == 0) {
            return 0;
        }
        byte latencyTimer = f106d.getLatencyTimer();
        f106d.setLatencyTimer((byte) 119);
        boolean a = m51a(iArr, 63);
        f106d.setLatencyTimer(latencyTimer);
        if (a) {
            return bArr.length;
        }
        return 0;
    }

    byte[] m110a(int i) {
        byte[] bArr = new byte[i];
        if (i == 0 || i > m111b()) {
            return null;
        }
        short b = (short) ((63 - (m111b() / 2)) - 1);
        int i2 = 0;
        while (i2 < i) {
            short s = (short) (b + 1);
            int a = m43a(b);
            if (i2 + 1 < bArr.length) {
                bArr[i2 + 1] = (byte) (a & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
            }
            bArr[i2] = (byte) ((a & 65280) >> 8);
            i2 += 2;
            b = s;
        }
        return bArr;
    }
}
