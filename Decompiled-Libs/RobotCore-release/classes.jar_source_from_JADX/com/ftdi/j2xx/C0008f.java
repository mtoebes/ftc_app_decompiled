package com.ftdi.j2xx;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;

/* renamed from: com.ftdi.j2xx.f */
class C0008f extends C0004k {
    private static FT_Device f48d;

    C0008f(FT_Device fT_Device) {
        super(fT_Device);
        f48d = fT_Device;
    }

    short m73a(FT_EEPROM ft_eeprom) {
        int[] iArr = new int[64];
        if (ft_eeprom.getClass() != FT_EEPROM.class) {
            return (short) 1;
        }
        for (short s = (short) 0; s < (short) 64; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        try {
            iArr[1] = ft_eeprom.VendorId;
            iArr[2] = ft_eeprom.ProductId;
            iArr[3] = f48d.f20g.bcdDevice;
            iArr[4] = m41a((Object) ft_eeprom);
            int a = m42a(ft_eeprom.Product, iArr, m42a(ft_eeprom.Manufacturer, iArr, 10, 7, true), 8, true);
            if (ft_eeprom.SerNumEnable) {
                m42a(ft_eeprom.SerialNumber, iArr, a, 9, true);
            }
            if (iArr[1] == 0 || iArr[2] == 0) {
                return (short) 2;
            }
            if (m51a(iArr, 63)) {
                return (short) 0;
            }
            return (short) 1;
        } catch (Exception e) {
            e.printStackTrace();
            return (short) 0;
        }
    }

    FT_EEPROM m72a() {
        int i;
        FT_EEPROM ft_eeprom = new FT_EEPROM();
        int[] iArr = new int[64];
        for (i = 0; i < 64; i++) {
            iArr[i] = m43a((short) i);
        }
        try {
            ft_eeprom.VendorId = (short) iArr[1];
            ft_eeprom.ProductId = (short) iArr[2];
            m48a(ft_eeprom, iArr[4]);
            ft_eeprom.Manufacturer = m46a(10, iArr);
            i = 10 + (ft_eeprom.Manufacturer.length() + 1);
            ft_eeprom.Product = m46a(i, iArr);
            ft_eeprom.SerialNumber = m46a(i + (ft_eeprom.Product.length() + 1), iArr);
            return ft_eeprom;
        } catch (Exception e) {
            return null;
        }
    }

    int m75b() {
        return (((63 - ((((((m43a((short) 7) & 65280) >> 8) / 2) + 10) + (((m43a((short) 8) & 65280) >> 8) / 2)) + 1)) - 1) - (((m43a((short) 9) & 65280) >> 8) / 2)) * 2;
    }

    int m71a(byte[] bArr) {
        if (bArr.length > m75b()) {
            return 0;
        }
        int[] iArr = new int[64];
        for (short s = (short) 0; s < (short) 64; s = (short) (s + 1)) {
            iArr[s] = m43a(s);
        }
        int b = (short) (((short) ((63 - (m75b() / 2)) - 1)) & 65535);
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
        if (iArr[1] == 0 || iArr[2] == 0 || !m51a(iArr, 63)) {
            return 0;
        }
        return bArr.length;
    }

    byte[] m74a(int i) {
        byte[] bArr = new byte[i];
        if (i == 0 || i > m75b()) {
            return null;
        }
        short b = (short) (((short) ((63 - (m75b() / 2)) - 1)) & 65535);
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
