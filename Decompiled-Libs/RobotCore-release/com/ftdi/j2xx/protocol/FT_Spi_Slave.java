package com.ftdi.j2xx.protocol;

import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.ftdi.j2xx.interfaces.SpiSlave;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.robocol.Command;
import junit.framework.Assert;

public class FT_Spi_Slave extends SpiSlaveThread {
    private static /* synthetic */ int[] f152m;
    private C0023a f153a;
    private int f154b;
    private int f155c;
    private int f156d;
    private int f157e;
    private int f158f;
    private byte[] f159g;
    private int f160h;
    private int f161i;
    private SpiSlave f162j;
    private SpiSlaveListener f163k;
    private boolean f164l;

    /* renamed from: com.ftdi.j2xx.protocol.FT_Spi_Slave.a */
    private enum C0023a {
        STATE_SYNC,
        STATE_CMD,
        STATE_SN,
        STATE_SIZE_HIGH,
        STATE_SIZE_LOW,
        STATE_COLLECT_DATA,
        STATE_CHECKSUM_HIGH,
        STATE_CHECKSUM_LOW
    }

    static /* synthetic */ int[] m155a() {
        int[] iArr = f152m;
        if (iArr == null) {
            iArr = new int[C0023a.values().length];
            try {
                iArr[C0023a.STATE_CHECKSUM_HIGH.ordinal()] = 7;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[C0023a.STATE_CHECKSUM_LOW.ordinal()] = 8;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[C0023a.STATE_CMD.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[C0023a.STATE_COLLECT_DATA.ordinal()] = 6;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[C0023a.STATE_SIZE_HIGH.ordinal()] = 4;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[C0023a.STATE_SIZE_LOW.ordinal()] = 5;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[C0023a.STATE_SN.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[C0023a.STATE_SYNC.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            f152m = iArr;
        }
        return iArr;
    }

    public FT_Spi_Slave(SpiSlave pSlaveInterface) {
        this.f162j = pSlaveInterface;
        this.f153a = C0023a.STATE_SYNC;
    }

    public void registerSpiSlaveListener(SpiSlaveListener pListener) {
        this.f163k = pListener;
    }

    public int open() {
        if (this.f164l) {
            return 1;
        }
        this.f164l = true;
        this.f162j.init();
        start();
        return 0;
    }

    public int close() {
        if (!this.f164l) {
            return 3;
        }
        sendMessage(new SpiSlaveRequestEvent(-1, true, null, null, null));
        this.f164l = false;
        return 0;
    }

    public int write(byte[] wrBuf) {
        if (!this.f164l) {
            return 3;
        }
        if (wrBuf.length > 65536) {
            return FT4222_STATUS.FT4222_EXCEEDED_MAX_TRANSFER_SIZE;
        }
        int[] iArr = new int[1];
        int length = wrBuf.length;
        int a = m152a(wrBuf, 90, SPI_SLAVE_CMD.SPI_SLAVE_TRANSFER, this.f161i, length);
        byte[] bArr = new byte[(wrBuf.length + 8)];
        bArr[0] = (byte) 0;
        bArr[1] = (byte) 90;
        bArr[2] = (byte) -127;
        bArr[3] = (byte) this.f161i;
        bArr[4] = (byte) ((65280 & length) >> 8);
        bArr[5] = (byte) (length & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        int i = 6;
        int i2 = 0;
        while (i2 < wrBuf.length) {
            int i3 = i + 1;
            bArr[i] = wrBuf[i2];
            i2++;
            i = i3;
        }
        i2 = i + 1;
        bArr[i] = (byte) ((65280 & a) >> 8);
        i = i2 + 1;
        bArr[i2] = (byte) (a & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        this.f162j.write(bArr, bArr.length, iArr);
        if (iArr[0] != bArr.length) {
            return 4;
        }
        this.f161i++;
        if (this.f161i < Command.MAX_COMMAND_LENGTH) {
            return 0;
        }
        this.f161i = 0;
        return 0;
    }

    private boolean m154a(int i) {
        if (i == SPI_SLAVE_CMD.SPI_MASTER_TRANSFER || i == SPI_SLAVE_CMD.SPI_SHORT_MASTER_TRANSFER || i == 136) {
            return true;
        }
        return false;
    }

    private int m152a(byte[] bArr, int i, int i2, int i3, int i4) {
        int i5;
        int i6 = 0;
        if (bArr != null) {
            i5 = 0;
            while (i6 < bArr.length) {
                i5 += bArr[i6] & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
                i6++;
            }
        } else {
            i5 = 0;
        }
        return ((((i5 + i) + i2) + i3) + ((65280 & i4) >> 8)) + (i4 & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
    }

    private void m156b() {
        r6 = new byte[8];
        int a = m152a(null, 90, SPI_SLAVE_CMD.SPI_ACK, this.f156d, 0);
        r6[6] = (byte) ((65280 & a) >> 8);
        r6[7] = (byte) (a & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
        this.f162j.write(r6, r6.length, new int[1]);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void m153a(byte[] r14) {
        /*
        r13 = this;
        r12 = 3;
        r11 = 0;
        r9 = 1;
        r7 = 0;
        r6 = r7;
        r8 = r7;
        r0 = r7;
    L_0x0007:
        r1 = r14.length;
        if (r6 < r1) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r1 = r14[r6];
        r1 = r1 & 255;
        r2 = m155a();
        r3 = r13.f153a;
        r3 = r3.ordinal();
        r2 = r2[r3];
        switch(r2) {
            case 1: goto L_0x0050;
            case 2: goto L_0x005e;
            case 3: goto L_0x006f;
            case 4: goto L_0x0077;
            case 5: goto L_0x0081;
            case 6: goto L_0x0094;
            case 7: goto L_0x00af;
            case 8: goto L_0x00ba;
            default: goto L_0x001e;
        };
    L_0x001e:
        r10 = r0;
    L_0x001f:
        if (r8 == 0) goto L_0x0034;
    L_0x0021:
        r0 = r13.f163k;
        if (r0 == 0) goto L_0x0034;
    L_0x0025:
        r0 = new com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
        r1 = r12;
        r2 = r9;
        r3 = r11;
        r4 = r11;
        r5 = r11;
        r0.<init>(r1, r2, r3, r4, r5);
        r1 = r13.f163k;
        r1.OnDataReceived(r0);
    L_0x0034:
        if (r10 == 0) goto L_0x00f4;
    L_0x0036:
        r0 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_SYNC;
        r13.f153a = r0;
        r13.f154b = r7;
        r13.f155c = r7;
        r13.f156d = r7;
        r13.f157e = r7;
        r13.f158f = r7;
        r13.f160h = r7;
        r13.f159g = r11;
        r1 = r7;
        r2 = r7;
    L_0x004a:
        r0 = r6 + 1;
        r6 = r0;
        r8 = r1;
        r0 = r2;
        goto L_0x0007;
    L_0x0050:
        r2 = 90;
        if (r1 == r2) goto L_0x0056;
    L_0x0054:
        r10 = r9;
        goto L_0x001f;
    L_0x0056:
        r2 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_CMD;
        r13.f153a = r2;
        r13.f154b = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x005e:
        r2 = r13.m154a(r1);
        if (r2 != 0) goto L_0x006c;
    L_0x0064:
        r8 = r9;
        r0 = r9;
    L_0x0066:
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_SN;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x006c:
        r13.f155c = r1;
        goto L_0x0066;
    L_0x006f:
        r13.f156d = r1;
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_SIZE_HIGH;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x0077:
        r1 = r1 * 256;
        r13.f157e = r1;
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_SIZE_LOW;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x0081:
        r2 = r13.f157e;
        r1 = r1 + r2;
        r13.f157e = r1;
        r13.f158f = r7;
        r1 = r13.f157e;
        r1 = new byte[r1];
        r13.f159g = r1;
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_COLLECT_DATA;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x0094:
        r1 = r13.f159g;
        r2 = r13.f158f;
        r3 = r14[r6];
        r1[r2] = r3;
        r1 = r13.f158f;
        r1 = r1 + 1;
        r13.f158f = r1;
        r1 = r13.f158f;
        r2 = r13.f157e;
        if (r1 != r2) goto L_0x001e;
    L_0x00a8:
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_CHECKSUM_HIGH;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x00af:
        r1 = r1 * 256;
        r13.f160h = r1;
        r1 = com.ftdi.j2xx.protocol.FT_Spi_Slave.C0023a.STATE_CHECKSUM_LOW;
        r13.f153a = r1;
        r10 = r0;
        goto L_0x001f;
    L_0x00ba:
        r0 = r13.f160h;
        r0 = r0 + r1;
        r13.f160h = r0;
        r1 = r13.f159g;
        r2 = r13.f154b;
        r3 = r13.f155c;
        r4 = r13.f156d;
        r5 = r13.f157e;
        r0 = r13;
        r0 = r0.m152a(r1, r2, r3, r4, r5);
        r1 = r13.f160h;
        if (r1 != r0) goto L_0x00f2;
    L_0x00d2:
        r0 = r13.f155c;
        r1 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        if (r0 != r1) goto L_0x00ef;
    L_0x00d8:
        r13.m156b();
        r0 = r13.f163k;
        if (r0 == 0) goto L_0x00ef;
    L_0x00df:
        r0 = new com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
        r3 = r13.f159g;
        r1 = r12;
        r2 = r7;
        r4 = r11;
        r5 = r11;
        r0.<init>(r1, r2, r3, r4, r5);
        r1 = r13.f163k;
        r1.OnDataReceived(r0);
    L_0x00ef:
        r10 = r9;
        goto L_0x001f;
    L_0x00f2:
        r8 = r9;
        goto L_0x00ef;
    L_0x00f4:
        r1 = r8;
        r2 = r10;
        goto L_0x004a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ftdi.j2xx.protocol.FT_Spi_Slave.a(byte[]):void");
    }

    protected boolean pollData() {
        int[] iArr = new int[1];
        int rxStatus = this.f162j.getRxStatus(iArr);
        if (iArr[0] > 0 && rxStatus == 0) {
            byte[] bArr = new byte[iArr[0]];
            rxStatus = this.f162j.read(bArr, bArr.length, iArr);
            if (rxStatus == 0) {
                m153a(bArr);
            }
        }
        if (rxStatus == 4 && this.f163k != null) {
            this.f163k.OnDataReceived(new SpiSlaveResponseEvent(3, 2, this.f159g, null, null));
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        return true;
    }

    protected void requestEvent(SpiSlaveEvent pEvent) {
        if (pEvent instanceof SpiSlaveRequestEvent) {
            switch (pEvent.getEventType()) {
            }
        } else {
            Assert.assertTrue("processEvent wrong type" + pEvent.getEventType(), false);
        }
    }

    protected boolean isTerminateEvent(SpiSlaveEvent pEvent) {
        if (!Thread.interrupted()) {
            return true;
        }
        if (pEvent instanceof SpiSlaveRequestEvent) {
            switch (pEvent.getEventType()) {
                case Gamepad.ID_UNASSOCIATED /*-1*/:
                    return true;
            }
        }
        Assert.assertTrue("processEvent wrong type" + pEvent.getEventType(), false);
        return false;
    }
}
