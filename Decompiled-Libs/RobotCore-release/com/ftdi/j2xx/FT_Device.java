package com.ftdi.j2xx;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.D2xxManager.DriverParameters;
import com.ftdi.j2xx.D2xxManager.FtDeviceInfoListNode;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.SPI_SLAVE_CMD;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.robocol.Command;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FT_Device {
    long f14a;
    Boolean f15b;
    UsbDevice f16c;
    UsbInterface f17d;
    UsbEndpoint f18e;
    UsbEndpoint f19f;
    FtDeviceInfoListNode f20g;
    C0025r f21h;
    C0024q f22i;
    Context f23j;
    private UsbRequest f24k;
    private UsbDeviceConnection f25l;
    private C0002a f26m;
    private Thread f27n;
    private Thread f28o;
    private C0021o f29p;
    private C0004k f30q;
    private byte f31r;
    private DriverParameters f32s;
    private int f33t;
    private int f34u;

    public FT_Device(Context parentContext, UsbManager usbManager, UsbDevice dev, UsbInterface itf) {
        this.f33t = 0;
        byte[] bArr = new byte[FT_4222_Defines.CHIPTOP_DEBUG_REQUEST];
        this.f23j = parentContext;
        this.f32s = new DriverParameters();
        try {
            this.f16c = dev;
            this.f17d = itf;
            this.f18e = null;
            this.f19f = null;
            this.f34u = 0;
            this.f21h = new C0025r();
            this.f22i = new C0024q();
            this.f20g = new FtDeviceInfoListNode();
            this.f24k = new UsbRequest();
            m23a(usbManager.openDevice(this.f16c));
            if (m28c() == null) {
                Log.e("FTDI_Device::", "Failed to open the device!");
                throw new D2xxException("Failed to open the device!");
            }
            m28c().claimInterface(this.f17d, false);
            byte[] rawDescriptors = m28c().getRawDescriptors();
            int deviceId = this.f16c.getDeviceId();
            this.f33t = this.f17d.getId() + 1;
            this.f20g.location = (deviceId << 4) | (this.f33t & 15);
            ByteBuffer allocate = ByteBuffer.allocate(2);
            allocate.order(ByteOrder.LITTLE_ENDIAN);
            allocate.put(rawDescriptors[12]);
            allocate.put(rawDescriptors[13]);
            this.f20g.bcdDevice = allocate.getShort(0);
            this.f20g.iSerialNumber = rawDescriptors[16];
            this.f20g.serialNumber = m28c().getSerial();
            this.f20g.id = (this.f16c.getVendorId() << 16) | this.f16c.getProductId();
            this.f20g.breakOnParam = 8;
            m28c().controlTransfer(-128, 6, rawDescriptors[15] | 768, 0, bArr, FT_4222_Defines.CHIPTOP_DEBUG_REQUEST, 0);
            this.f20g.description = m8a(bArr);
            switch (this.f20g.bcdDevice & 65280) {
                case 512:
                    if (this.f20g.iSerialNumber != null) {
                        this.f20g.type = 1;
                        this.f30q = new C0007e(this);
                        break;
                    }
                    this.f30q = new C0008f(this);
                    this.f20g.type = 0;
                    break;
                case 1024:
                    this.f30q = new C0008f(this);
                    this.f20g.type = 0;
                    break;
                case 1280:
                    this.f30q = new C0006d(this);
                    this.f20g.type = 4;
                    m19n();
                    break;
                case 1536:
                    this.f30q = new C0004k(this);
                    short a = (short) (this.f30q.m43a((short) 0) & 1);
                    this.f30q = null;
                    if (a != (short) 0) {
                        this.f20g.type = 5;
                        this.f30q = new C0016i(this);
                        break;
                    }
                    this.f20g.type = 5;
                    this.f30q = new C0015h(this);
                    break;
                case 1792:
                    this.f20g.type = 6;
                    m19n();
                    this.f30q = new C0005c(this);
                    break;
                case 2048:
                    this.f20g.type = 7;
                    m19n();
                    this.f30q = new C0017j(this);
                    break;
                case 2304:
                    this.f20g.type = 8;
                    this.f30q = new C0014g(this);
                    break;
                case 4096:
                    this.f20g.type = 9;
                    this.f30q = new C0018l(this);
                    break;
                case 5888:
                    this.f20g.type = 12;
                    this.f20g.flags = 2;
                    break;
                case 6144:
                    this.f20g.type = 10;
                    if (this.f33t != 1) {
                        this.f20g.flags = 0;
                        break;
                    } else {
                        this.f20g.flags = 2;
                        break;
                    }
                case 6400:
                    this.f20g.type = 11;
                    if (this.f33t != 4) {
                        this.f20g.flags = 2;
                        break;
                    }
                    deviceId = this.f16c.getInterface(this.f33t - 1).getEndpoint(0).getMaxPacketSize();
                    Log.e("dev", "mInterfaceID : " + this.f33t + "   iMaxPacketSize : " + deviceId);
                    if (deviceId != 8) {
                        this.f20g.flags = 2;
                        break;
                    } else {
                        this.f20g.flags = 0;
                        break;
                    }
                default:
                    this.f20g.type = 3;
                    this.f30q = new C0004k(this);
                    break;
            }
            switch (this.f20g.bcdDevice & 65280) {
                case 5888:
                case 6144:
                case 6400:
                    if (this.f20g.serialNumber == null) {
                        bArr = new byte[16];
                        m28c().controlTransfer(-64, 144, 0, 27, bArr, 16, 0);
                        String str = BuildConfig.VERSION_NAME;
                        for (deviceId = 0; deviceId < 8; deviceId++) {
                            str = new StringBuilder(String.valueOf(str)).append((char) bArr[deviceId * 2]).toString();
                        }
                        this.f20g.serialNumber = new String(str);
                        break;
                    }
                    break;
            }
            switch (this.f20g.bcdDevice & 65280) {
                case 6144:
                case 6400:
                    FtDeviceInfoListNode ftDeviceInfoListNode;
                    if (this.f33t != 1) {
                        if (this.f33t != 2) {
                            if (this.f33t != 3) {
                                if (this.f33t == 4) {
                                    ftDeviceInfoListNode = this.f20g;
                                    ftDeviceInfoListNode.description += " D";
                                    ftDeviceInfoListNode = this.f20g;
                                    ftDeviceInfoListNode.serialNumber += "D";
                                    break;
                                }
                            }
                            ftDeviceInfoListNode = this.f20g;
                            ftDeviceInfoListNode.description += " C";
                            ftDeviceInfoListNode = this.f20g;
                            ftDeviceInfoListNode.serialNumber += "C";
                            break;
                        }
                        ftDeviceInfoListNode = this.f20g;
                        ftDeviceInfoListNode.description += " B";
                        ftDeviceInfoListNode = this.f20g;
                        ftDeviceInfoListNode.serialNumber += "B";
                        break;
                    }
                    ftDeviceInfoListNode = this.f20g;
                    ftDeviceInfoListNode.description += " A";
                    ftDeviceInfoListNode = this.f20g;
                    ftDeviceInfoListNode.serialNumber += "A";
                    break;
                    break;
            }
            m28c().releaseInterface(this.f17d);
            m28c().close();
            m23a(null);
            m21p();
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e("FTDI_Device::", e.getMessage());
            }
        }
    }

    private final boolean m11f() {
        return m14i() || m15j() || m27b();
    }

    private final boolean m12g() {
        return m18m() || m17l() || m16k() || m15j() || m27b() || m14i() || m13h();
    }

    final boolean m24a() {
        return m17l() || m15j() || m27b();
    }

    private final boolean m13h() {
        return (this.f20g.bcdDevice & 65280) == 4096;
    }

    private final boolean m14i() {
        return (this.f20g.bcdDevice & 65280) == 2304;
    }

    final boolean m27b() {
        return (this.f20g.bcdDevice & 65280) == 2048;
    }

    private final boolean m15j() {
        return (this.f20g.bcdDevice & 65280) == 1792;
    }

    private final boolean m16k() {
        return (this.f20g.bcdDevice & 65280) == 1536;
    }

    private final boolean m17l() {
        return (this.f20g.bcdDevice & 65280) == 1280;
    }

    private final boolean m18m() {
        return (this.f20g.bcdDevice & 65280) == 1024 || ((this.f20g.bcdDevice & 65280) == 512 && this.f20g.iSerialNumber == null);
    }

    private final String m8a(byte[] bArr) throws UnsupportedEncodingException {
        return new String(bArr, 2, bArr[0] - 2, "UTF-16LE");
    }

    UsbDeviceConnection m28c() {
        return this.f25l;
    }

    void m23a(UsbDeviceConnection usbDeviceConnection) {
        this.f25l = usbDeviceConnection;
    }

    synchronized boolean m25a(Context context) {
        boolean z;
        z = false;
        if (context != null) {
            this.f23j = context;
            z = true;
        }
        return z;
    }

    protected void setDriverParameters(DriverParameters params) {
        this.f32s.setMaxBufferSize(params.getMaxBufferSize());
        this.f32s.setMaxTransferSize(params.getMaxTransferSize());
        this.f32s.setBufferNumber(params.getBufferNumber());
        this.f32s.setReadTimeout(params.getReadTimeout());
    }

    DriverParameters m29d() {
        return this.f32s;
    }

    public int getReadTimeout() {
        return this.f32s.getReadTimeout();
    }

    private void m19n() {
        FtDeviceInfoListNode ftDeviceInfoListNode;
        if (this.f33t == 1) {
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.serialNumber += "A";
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.description += " A";
        } else if (this.f33t == 2) {
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.serialNumber += "B";
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.description += " B";
        } else if (this.f33t == 3) {
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.serialNumber += "C";
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.description += " C";
        } else if (this.f33t == 4) {
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.serialNumber += "D";
            ftDeviceInfoListNode = this.f20g;
            ftDeviceInfoListNode.description += " D";
        }
    }

    synchronized boolean m26a(UsbManager usbManager) {
        boolean z;
        z = false;
        if (!isOpen()) {
            if (usbManager == null) {
                Log.e("FTDI_Device::", "UsbManager cannot be null.");
            } else if (m28c() != null) {
                Log.e("FTDI_Device::", "There should not have an UsbConnection.");
            } else {
                m23a(usbManager.openDevice(this.f16c));
                if (m28c() == null) {
                    Log.e("FTDI_Device::", "UsbConnection cannot be null.");
                } else if (m28c().claimInterface(this.f17d, true)) {
                    Log.d("FTDI_Device::", "open SUCCESS");
                    if (m22q()) {
                        this.f24k.initialize(this.f25l, this.f18e);
                        Log.d("D2XX::", "**********************Device Opened**********************");
                        this.f29p = new C0021o(this);
                        this.f26m = new C0002a(this, this.f29p, m28c(), this.f19f);
                        this.f28o = new Thread(this.f26m);
                        this.f28o.setName("bulkInThread");
                        this.f27n = new Thread(new C0022p(this.f29p));
                        this.f27n.setName("processRequestThread");
                        m10a(true, true);
                        this.f28o.start();
                        this.f27n.start();
                        m20o();
                        z = true;
                    } else {
                        Log.e("FTDI_Device::", "Failed to find endpoints.");
                    }
                } else {
                    Log.e("FTDI_Device::", "ClaimInteface returned false.");
                }
            }
        }
        return z;
    }

    public synchronized boolean isOpen() {
        return this.f15b.booleanValue();
    }

    private synchronized void m20o() {
        this.f15b = Boolean.valueOf(true);
        FtDeviceInfoListNode ftDeviceInfoListNode = this.f20g;
        ftDeviceInfoListNode.flags |= 1;
    }

    private synchronized void m21p() {
        this.f15b = Boolean.valueOf(false);
        FtDeviceInfoListNode ftDeviceInfoListNode = this.f20g;
        ftDeviceInfoListNode.flags &= 2;
    }

    public synchronized void close() {
        if (this.f27n != null) {
            this.f27n.interrupt();
        }
        if (this.f28o != null) {
            this.f28o.interrupt();
        }
        if (this.f25l != null) {
            this.f25l.releaseInterface(this.f17d);
            this.f25l.close();
            this.f25l = null;
        }
        if (this.f29p != null) {
            this.f29p.m151g();
        }
        this.f27n = null;
        this.f28o = null;
        this.f26m = null;
        this.f29p = null;
        m21p();
    }

    protected UsbDevice getUsbDevice() {
        return this.f16c;
    }

    public FtDeviceInfoListNode getDeviceInfo() {
        return this.f20g;
    }

    public int read(byte[] data, int length, long wait_ms) {
        if (!isOpen()) {
            return -1;
        }
        if (length <= 0) {
            return -2;
        }
        if (this.f29p == null) {
            return -3;
        }
        return this.f29p.m138a(data, length, wait_ms);
    }

    public int read(byte[] data, int length) {
        return read(data, length, (long) this.f32s.getReadTimeout());
    }

    public int read(byte[] data) {
        return read(data, data.length, (long) this.f32s.getReadTimeout());
    }

    public int write(byte[] data, int length) {
        return write(data, length, true);
    }

    public int write(byte[] data, int length, boolean wait) {
        int i = -1;
        if (!isOpen() || length < 0) {
            return -1;
        }
        UsbRequest usbRequest = this.f24k;
        if (wait) {
            usbRequest.setClientData(this);
        }
        if (length == 0) {
            if (usbRequest.queue(ByteBuffer.wrap(new byte[1]), length)) {
                i = length;
            }
        } else if (usbRequest.queue(ByteBuffer.wrap(data), length)) {
            i = length;
        }
        if (!wait) {
            return i;
        }
        do {
            usbRequest = this.f25l.requestWait();
            if (usbRequest == null) {
                Log.e("FTDI_Device::", "UsbConnection.requestWait() == null");
                return -99;
            }
        } while (usbRequest.getClientData() != this);
        return i;
    }

    public int write(byte[] data) {
        return write(data, data.length, true);
    }

    public short getModemStatus() {
        if (!isOpen()) {
            return (short) -1;
        }
        if (this.f29p == null) {
            return (short) -2;
        }
        this.f14a &= -3;
        return (short) (this.f20g.modemStatus & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST);
    }

    public short getLineStatus() {
        if (!isOpen()) {
            return (short) -1;
        }
        if (this.f29p == null) {
            return (short) -2;
        }
        return this.f20g.lineStatus;
    }

    public int getQueueStatus() {
        if (!isOpen()) {
            return -1;
        }
        if (this.f29p == null) {
            return -2;
        }
        return this.f29p.m144c();
    }

    public boolean readBufferFull() {
        return this.f29p.m141a();
    }

    public long getEventStatus() {
        if (!isOpen()) {
            return -1;
        }
        if (this.f29p == null) {
            return -2;
        }
        long j = this.f14a;
        this.f14a = 0;
        return j;
    }

    public boolean setBaudRate(int baudRate) {
        int[] iArr = new int[2];
        if (!isOpen()) {
            return false;
        }
        byte b;
        switch (baudRate) {
            case 300:
                iArr[0] = 10000;
                b = (byte) 1;
                break;
            case 600:
                iArr[0] = 5000;
                b = (byte) 1;
                break;
            case 1200:
                iArr[0] = 2500;
                b = (byte) 1;
                break;
            case 2400:
                iArr[0] = 1250;
                b = (byte) 1;
                break;
            case 4800:
                iArr[0] = 625;
                b = (byte) 1;
                break;
            case 9600:
                iArr[0] = 16696;
                b = (byte) 1;
                break;
            case 19200:
                iArr[0] = 32924;
                b = (byte) 1;
                break;
            case 38400:
                iArr[0] = 49230;
                b = (byte) 1;
                break;
            case 57600:
                iArr[0] = 52;
                b = (byte) 1;
                break;
            case 115200:
                iArr[0] = 26;
                b = (byte) 1;
                break;
            case 230400:
                iArr[0] = 13;
                b = (byte) 1;
                break;
            case 460800:
                iArr[0] = 16390;
                b = (byte) 1;
                break;
            case 921600:
                iArr[0] = 32771;
                b = (byte) 1;
                break;
            default:
                if (m11f() && baudRate >= 1200) {
                    b = C0003b.m34a(baudRate, iArr);
                    break;
                }
                b = C0003b.m35a(baudRate, iArr, m12g());
                break;
                break;
        }
        if (m24a() || m14i() || m13h()) {
            iArr[1] = iArr[1] << 8;
            iArr[1] = iArr[1] & 65280;
            iArr[1] = iArr[1] | this.f33t;
        }
        if (b == (byte) 1 && m28c().controlTransfer(64, 3, iArr[0], iArr[1], null, 0, 0) == 0) {
            return true;
        }
        return false;
    }

    public boolean setDataCharacteristics(byte dataBits, byte stopBits, byte parity) {
        if (!isOpen()) {
            return false;
        }
        short s = (short) (((short) ((parity << 8) | dataBits)) | (stopBits << 11));
        this.f20g.breakOnParam = s;
        if (m28c().controlTransfer(64, 4, s, this.f33t, null, 0, 0) == 0) {
            return true;
        }
        return false;
    }

    public boolean setBreakOn() {
        return m9a((int) D2xxManager.FTDI_BREAK_ON);
    }

    public boolean setBreakOff() {
        return m9a(0);
    }

    private boolean m9a(int i) {
        int i2 = this.f20g.breakOnParam | i;
        if (isOpen() && m28c().controlTransfer(64, 4, i2, this.f33t, null, 0, 0) == 0) {
            return true;
        }
        return false;
    }

    public boolean setFlowControl(short flowControl, byte xon, byte xoff) {
        if (!isOpen()) {
            return false;
        }
        int i;
        if (flowControl == D2xxManager.FT_FLOW_XON_XOFF) {
            i = (short) (((short) (xoff << 8)) | (xon & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST));
        } else {
            i = 0;
        }
        if (m28c().controlTransfer(64, 2, i, this.f33t | flowControl, null, 0, 0) != 0) {
            return false;
        }
        if (flowControl == D2xxManager.FT_FLOW_RTS_CTS) {
            return setRts();
        }
        if (flowControl == D2xxManager.FT_FLOW_DTR_DSR) {
            return setDtr();
        }
        return true;
    }

    public boolean setRts() {
        boolean z = true;
        if (!isOpen()) {
            return false;
        }
        if (m28c().controlTransfer(64, 1, 514, this.f33t, null, 0, 0) != 0) {
            z = false;
        }
        return z;
    }

    public boolean clrRts() {
        boolean z = true;
        if (!isOpen()) {
            return false;
        }
        if (m28c().controlTransfer(64, 1, 512, this.f33t, null, 0, 0) != 0) {
            z = false;
        }
        return z;
    }

    public boolean setDtr() {
        boolean z = true;
        if (!isOpen()) {
            return false;
        }
        if (m28c().controlTransfer(64, 1, 257, this.f33t, null, 0, 0) != 0) {
            z = false;
        }
        return z;
    }

    public boolean clrDtr() {
        boolean z = true;
        if (!isOpen()) {
            return false;
        }
        if (m28c().controlTransfer(64, 1, Command.MAX_COMMAND_LENGTH, this.f33t, null, 0, 0) != 0) {
            z = false;
        }
        return z;
    }

    public boolean setChars(byte eventChar, byte eventCharEnable, byte errorChar, byte errorCharEnable) {
        C0025r c0025r = new C0025r();
        c0025r.f172a = eventChar;
        c0025r.f173b = eventCharEnable;
        c0025r.f174c = errorChar;
        c0025r.f175d = errorCharEnable;
        if (!isOpen()) {
            return false;
        }
        int i = eventChar & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (eventCharEnable != null) {
            i |= Command.MAX_COMMAND_LENGTH;
        }
        if (m28c().controlTransfer(64, 6, i, this.f33t, null, 0, 0) != 0) {
            return false;
        }
        i = errorChar & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (errorCharEnable > null) {
            i |= Command.MAX_COMMAND_LENGTH;
        }
        if (m28c().controlTransfer(64, 7, i, this.f33t, null, 0, 0) != 0) {
            return false;
        }
        this.f21h = c0025r;
        return true;
    }

    public boolean setBitMode(byte mask, byte bitMode) {
        boolean z = true;
        int i = this.f20g.type;
        if (!isOpen() || i == 1) {
            return false;
        }
        if (i != 0 || bitMode == null) {
            if (i != 4 || bitMode == null) {
                if (i != 5 || bitMode == null) {
                    if (i != 6 || bitMode == null) {
                        if (i != 7 || bitMode == null) {
                            if (i == 8 && bitMode != null && bitMode > D2xxManager.FT_RI) {
                                return false;
                            }
                        } else if ((bitMode & 7) == 0) {
                            return false;
                        } else {
                            if (((this.f17d.getId() != 1 ? 1 : 0) & ((this.f17d.getId() != 0 ? 1 : 0) & (bitMode == (byte) 2 ? 1 : 0))) != 0) {
                                return false;
                            }
                        }
                    } else if ((bitMode & 95) == 0) {
                        return false;
                    } else {
                        int i2;
                        if ((bitMode & 72) > 0) {
                            i = 1;
                        } else {
                            i = 0;
                        }
                        if (this.f17d.getId() != 0) {
                            i2 = 1;
                        } else {
                            i2 = 0;
                        }
                        if ((i & i2) != 0) {
                            return false;
                        }
                    }
                } else if ((bitMode & 37) == 0) {
                    return false;
                }
            } else if ((bitMode & 31) == 0) {
                return false;
            } else {
                if (((bitMode == (byte) 2 ? 1 : 0) & (this.f17d.getId() != 0 ? 1 : 0)) != 0) {
                    return false;
                }
            }
        } else if ((bitMode & 1) == 0) {
            return false;
        }
        if (m28c().controlTransfer(64, 11, (bitMode << 8) | (mask & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST), this.f33t, null, 0, 0) != 0) {
            z = false;
        }
        return z;
    }

    public byte getBitMode() {
        byte[] bArr = new byte[1];
        if (!isOpen()) {
            return (byte) -1;
        }
        if (!m12g()) {
            return (byte) -2;
        }
        if (m28c().controlTransfer(-64, 12, 0, this.f33t, bArr, bArr.length, 0) == bArr.length) {
            return bArr[0];
        }
        return (byte) -3;
    }

    public boolean resetDevice() {
        if (isOpen() && m28c().controlTransfer(64, 0, 0, 0, null, 0, 0) == 0) {
            return true;
        }
        return false;
    }

    public int VendorCmdSet(int request, int wValue) {
        if (!isOpen()) {
            return -1;
        }
        return m28c().controlTransfer(64, request, wValue, this.f33t, null, 0, 0);
    }

    public int VendorCmdSet(int request, int wValue, byte[] buf, int datalen) {
        if (!isOpen()) {
            Log.e("FTDI_Device::", "VendorCmdSet: Device not open");
            return -1;
        } else if (datalen < 0) {
            Log.e("FTDI_Device::", "VendorCmdSet: Invalid data length");
            return -1;
        } else {
            if (buf == null) {
                if (datalen > 0) {
                    Log.e("FTDI_Device::", "VendorCmdSet: buf is null!");
                    return -1;
                }
            } else if (buf.length < datalen) {
                Log.e("FTDI_Device::", "VendorCmdSet: length of buffer is smaller than data length to set");
                return -1;
            }
            return m28c().controlTransfer(64, request, wValue, this.f33t, buf, datalen, 0);
        }
    }

    public int VendorCmdGet(int request, int wValue, byte[] buf, int datalen) {
        if (!isOpen()) {
            Log.e("FTDI_Device::", "VendorCmdGet: Device not open");
            return -1;
        } else if (datalen < 0) {
            Log.e("FTDI_Device::", "VendorCmdGet: Invalid data length");
            return -1;
        } else if (buf == null) {
            Log.e("FTDI_Device::", "VendorCmdGet: buf is null");
            return -1;
        } else if (buf.length < datalen) {
            Log.e("FTDI_Device::", "VendorCmdGet: length of buffer is smaller than data length to get");
            return -1;
        } else {
            return m28c().controlTransfer(-64, request, wValue, this.f33t, buf, datalen, 0);
        }
    }

    public void stopInTask() {
        try {
            if (!this.f26m.m33c()) {
                this.f26m.m31a();
            }
        } catch (InterruptedException e) {
            Log.d("FTDI_Device::", "stopInTask called!");
            e.printStackTrace();
        }
    }

    public void restartInTask() {
        this.f26m.m32b();
    }

    public boolean stoppedInTask() {
        return this.f26m.m33c();
    }

    public boolean purge(byte flags) {
        boolean z;
        boolean z2 = true;
        if ((flags & 1) == 1) {
            z = true;
        } else {
            z = false;
        }
        if ((flags & 2) != 2) {
            z2 = false;
        }
        return m10a(z, z2);
    }

    private boolean m10a(boolean z, boolean z2) {
        if (!isOpen()) {
            return false;
        }
        boolean z3;
        if (z) {
            int i = 0;
            int i2 = 0;
            while (i < 6) {
                i++;
                i2 = m28c().controlTransfer(64, 0, 1, this.f33t, null, 0, 0);
            }
            if (i2 > 0) {
                return false;
            }
            this.f29p.m148e();
        }
        if (z2 && m28c().controlTransfer(64, 0, 2, this.f33t, null, 0, 0) == 0) {
            z3 = true;
        } else {
            z3 = false;
        }
        return z3;
    }

    public boolean setLatencyTimer(byte latency) {
        int i = latency & FT_4222_Defines.CHIPTOP_DEBUG_REQUEST;
        if (!isOpen() || m28c().controlTransfer(64, 9, i, this.f33t, null, 0, 0) != 0) {
            return false;
        }
        this.f31r = latency;
        return true;
    }

    public byte getLatencyTimer() {
        byte[] bArr = new byte[1];
        if (!isOpen()) {
            return (byte) -1;
        }
        if (m28c().controlTransfer(-64, 10, 0, this.f33t, bArr, bArr.length, 0) == bArr.length) {
            return bArr[0];
        }
        return (byte) 0;
    }

    public boolean setEventNotification(long Mask) {
        if (!isOpen() || Mask == 0) {
            return false;
        }
        this.f14a = 0;
        this.f22i.f171a = Mask;
        return true;
    }

    private boolean m22q() {
        for (int i = 0; i < this.f17d.getEndpointCount(); i++) {
            Log.i("FTDI_Device::", "EP: " + String.format("0x%02X", new Object[]{Integer.valueOf(this.f17d.getEndpoint(i).getAddress())}));
            if (this.f17d.getEndpoint(i).getType() != 2) {
                Log.i("FTDI_Device::", "Not Bulk Endpoint");
            } else if (this.f17d.getEndpoint(i).getDirection() == SPI_SLAVE_CMD.SPI_MASTER_TRANSFER) {
                this.f19f = this.f17d.getEndpoint(i);
                this.f34u = this.f19f.getMaxPacketSize();
            } else {
                this.f18e = this.f17d.getEndpoint(i);
            }
        }
        if (this.f18e == null || this.f19f == null) {
            return false;
        }
        return true;
    }

    public FT_EEPROM eepromRead() {
        if (isOpen()) {
            return this.f30q.m45a();
        }
        return null;
    }

    public short eepromWrite(FT_EEPROM eeData) {
        if (isOpen()) {
            return this.f30q.m47a(eeData);
        }
        return (short) -1;
    }

    public boolean eepromErase() {
        if (isOpen() && this.f30q.m55c() == 0) {
            return true;
        }
        return false;
    }

    public int eepromWriteUserArea(byte[] data) {
        if (isOpen()) {
            return this.f30q.m44a(data);
        }
        return 0;
    }

    public byte[] eepromReadUserArea(int length) {
        if (isOpen()) {
            return this.f30q.m52a(length);
        }
        return null;
    }

    public int eepromGetUserAreaSize() {
        if (isOpen()) {
            return this.f30q.m53b();
        }
        return -1;
    }

    public int eepromReadWord(short offset) {
        if (isOpen()) {
            return this.f30q.m43a(offset);
        }
        return -1;
    }

    public boolean eepromWriteWord(short address, short data) {
        if (isOpen()) {
            return this.f30q.m50a(address, data);
        }
        return false;
    }

    int m30e() {
        return this.f34u;
    }
}
