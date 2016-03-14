package com.ftdi.j2xx;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class D2xxManager {
    protected static final String ACTION_USB_PERMISSION = "com.ftdi.j2xx";
    public static final int FTDI_BREAK_OFF = 0;
    public static final int FTDI_BREAK_ON = 16384;
    public static final byte FT_BI = (byte) 16;
    public static final byte FT_BITMODE_ASYNC_BITBANG = (byte) 1;
    public static final byte FT_BITMODE_CBUS_BITBANG = (byte) 32;
    public static final byte FT_BITMODE_FAST_SERIAL = (byte) 16;
    public static final byte FT_BITMODE_MCU_HOST = (byte) 8;
    public static final byte FT_BITMODE_MPSSE = (byte) 2;
    public static final byte FT_BITMODE_RESET = (byte) 0;
    public static final byte FT_BITMODE_SYNC_BITBANG = (byte) 4;
    public static final byte FT_BITMODE_SYNC_FIFO = (byte) 64;
    public static final byte FT_CTS = (byte) 16;
    public static final byte FT_DATA_BITS_7 = (byte) 7;
    public static final byte FT_DATA_BITS_8 = (byte) 8;
    public static final byte FT_DCD = Byte.MIN_VALUE;
    public static final int FT_DEVICE_2232 = 4;
    public static final int FT_DEVICE_2232H = 6;
    public static final int FT_DEVICE_232B = 0;
    public static final int FT_DEVICE_232H = 8;
    public static final int FT_DEVICE_232R = 5;
    public static final int FT_DEVICE_245R = 5;
    public static final int FT_DEVICE_4222_0 = 10;
    public static final int FT_DEVICE_4222_1_2 = 11;
    public static final int FT_DEVICE_4222_3 = 12;
    public static final int FT_DEVICE_4232H = 7;
    public static final int FT_DEVICE_8U232AM = 1;
    public static final int FT_DEVICE_UNKNOWN = 3;
    public static final int FT_DEVICE_X_SERIES = 9;
    public static final byte FT_DSR = (byte) 32;
    public static final byte FT_EVENT_LINE_STATUS = (byte) 4;
    public static final byte FT_EVENT_MODEM_STATUS = (byte) 2;
    public static final byte FT_EVENT_REMOVED = (byte) 8;
    public static final byte FT_EVENT_RXCHAR = (byte) 1;
    public static final byte FT_FE = (byte) 8;
    public static final byte FT_FLAGS_HI_SPEED = (byte) 2;
    public static final byte FT_FLAGS_OPENED = (byte) 1;
    public static final short FT_FLOW_DTR_DSR = (short) 512;
    public static final short FT_FLOW_NONE = (short) 0;
    public static final short FT_FLOW_RTS_CTS = (short) 256;
    public static final short FT_FLOW_XON_XOFF = (short) 1024;
    public static final byte FT_OE = (byte) 2;
    public static final byte FT_PARITY_EVEN = (byte) 2;
    public static final byte FT_PARITY_MARK = (byte) 3;
    public static final byte FT_PARITY_NONE = (byte) 0;
    public static final byte FT_PARITY_ODD = (byte) 1;
    public static final byte FT_PARITY_SPACE = (byte) 4;
    public static final byte FT_PE = (byte) 4;
    public static final byte FT_PURGE_RX = (byte) 1;
    public static final byte FT_PURGE_TX = (byte) 2;
    public static final byte FT_RI = (byte) 64;
    public static final byte FT_STOP_BITS_1 = (byte) 0;
    public static final byte FT_STOP_BITS_2 = (byte) 2;
    private static D2xxManager f5a;
    private static Context f6b;
    private static PendingIntent f7c;
    private static IntentFilter f8d;
    private static List<C0019m> f9e;
    private static UsbManager f10g;
    private static BroadcastReceiver f11i;
    private ArrayList<FT_Device> f12f;
    private BroadcastReceiver f13h;

    /* renamed from: com.ftdi.j2xx.D2xxManager.1 */
    class C00001 extends BroadcastReceiver {
        final /* synthetic */ D2xxManager f0a;

        C00001(D2xxManager d2xxManager) {
            this.f0a = d2xxManager;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra("device");
                FT_Device a = this.f0a.m0a(usbDevice);
                while (a != null) {
                    a.close();
                    synchronized (this.f0a.f12f) {
                        this.f0a.f12f.remove(a);
                    }
                    a = this.f0a.m0a(usbDevice);
                }
            } else if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                this.f0a.addUsbDevice((UsbDevice) intent.getParcelableExtra("device"));
            }
        }
    }

    /* renamed from: com.ftdi.j2xx.D2xxManager.2 */
    class C00012 extends BroadcastReceiver {
        C00012() {
        }

        public void onReceive(Context context, Intent intent) {
            if (D2xxManager.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false)) {
                        Log.d("D2xx::", "permission denied for device " + usbDevice);
                    }
                }
            }
        }
    }

    public static class D2xxException extends IOException {
        public D2xxException(String ftStatusMsg) {
            super(ftStatusMsg);
        }
    }

    public static class DriverParameters {
        private int f1a;
        private int f2b;
        private int f3c;
        private int f4d;

        public DriverParameters() {
            this.f1a = D2xxManager.FTDI_BREAK_ON;
            this.f2b = D2xxManager.FTDI_BREAK_ON;
            this.f3c = 16;
            this.f4d = 5000;
        }

        public boolean setMaxBufferSize(int size) {
            if (size < 64 || size > 262144) {
                Log.e("D2xx::", "***bufferSize Out of correct range***");
                return false;
            }
            this.f1a = size;
            return true;
        }

        public int getMaxBufferSize() {
            return this.f1a;
        }

        public boolean setMaxTransferSize(int size) {
            if (size < 64 || size > 262144) {
                Log.e("D2xx::", "***maxTransferSize Out of correct range***");
                return false;
            }
            this.f2b = size;
            return true;
        }

        public int getMaxTransferSize() {
            return this.f2b;
        }

        public boolean setBufferNumber(int number) {
            if (number < 2 || number > 16) {
                Log.e("D2xx::", "***nrBuffers Out of correct range***");
                return false;
            }
            this.f3c = number;
            return true;
        }

        public int getBufferNumber() {
            return this.f3c;
        }

        public boolean setReadTimeout(int timeout) {
            this.f4d = timeout;
            return true;
        }

        public int getReadTimeout() {
            return this.f4d;
        }
    }

    public static class FtDeviceInfoListNode {
        public short bcdDevice;
        public int breakOnParam;
        public String description;
        public int flags;
        public int handle;
        public byte iSerialNumber;
        public int id;
        public short lineStatus;
        public int location;
        public short modemStatus;
        public String serialNumber;
        public int type;
    }

    static {
        f5a = null;
        f6b = null;
        f7c = null;
        f8d = null;
        f9e = new ArrayList(Arrays.asList(new C0019m[]{new C0019m(1027, 24597), new C0019m(1027, 24596), new C0019m(1027, 24593), new C0019m(1027, 24592), new C0019m(1027, 24577), new C0019m(1027, 24582), new C0019m(1027, 24604), new C0019m(1027, 64193), new C0019m(1027, 64194), new C0019m(1027, 64195), new C0019m(1027, 64196), new C0019m(1027, 64197), new C0019m(1027, 64198), new C0019m(1027, 24594), new C0019m(2220, 4133), new C0019m(5590, FT_DEVICE_8U232AM), new C0019m(1027, 24599)}));
        f11i = new C00012();
    }

    private FT_Device m0a(UsbDevice usbDevice) {
        FT_Device fT_Device;
        synchronized (this.f12f) {
            int size = this.f12f.size();
            for (int i = FT_DEVICE_232B; i < size; i += FT_DEVICE_8U232AM) {
                fT_Device = (FT_Device) this.f12f.get(i);
                if (fT_Device.getUsbDevice().equals(usbDevice)) {
                    break;
                }
            }
            fT_Device = null;
        }
        return fT_Device;
    }

    public boolean isFtDevice(UsbDevice dev) {
        boolean z = false;
        if (f6b != null) {
            C0019m c0019m = new C0019m(dev.getVendorId(), dev.getProductId());
            if (f9e.contains(c0019m)) {
                z = true;
            }
            Log.v("D2xx::", c0019m.toString());
        }
        return z;
    }

    private static synchronized boolean m4a(Context context) {
        boolean z = false;
        synchronized (D2xxManager.class) {
            if (context != null) {
                if (f6b != context) {
                    f6b = context;
                    f7c = PendingIntent.getBroadcast(f6b.getApplicationContext(), FT_DEVICE_232B, new Intent(ACTION_USB_PERMISSION), 134217728);
                    f8d = new IntentFilter(ACTION_USB_PERMISSION);
                    f6b.getApplicationContext().registerReceiver(f11i, f8d);
                }
                z = true;
            }
        }
        return z;
    }

    private boolean m7b(UsbDevice usbDevice) {
        if (!f10g.hasPermission(usbDevice)) {
            f10g.requestPermission(usbDevice, f7c);
        }
        if (f10g.hasPermission(usbDevice)) {
            return true;
        }
        return false;
    }

    private D2xxManager(Context parentContext) throws D2xxException {
        this.f13h = new C00001(this);
        Log.v("D2xx::", "Start constructor");
        if (parentContext == null) {
            throw new D2xxException("D2xx init failed: Can not find parentContext!");
        }
        m4a(parentContext);
        if (m3a()) {
            this.f12f = new ArrayList();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            parentContext.getApplicationContext().registerReceiver(this.f13h, intentFilter);
            Log.v("D2xx::", "End constructor");
            return;
        }
        throw new D2xxException("D2xx init failed: Can not find UsbManager!");
    }

    public static synchronized D2xxManager getInstance(Context parentContext) throws D2xxException {
        D2xxManager d2xxManager;
        synchronized (D2xxManager.class) {
            if (f5a == null) {
                f5a = new D2xxManager(parentContext);
            }
            if (parentContext != null) {
                m4a(parentContext);
            }
            d2xxManager = f5a;
        }
        return d2xxManager;
    }

    private static boolean m3a() {
        if (f10g == null && f6b != null) {
            f10g = (UsbManager) f6b.getApplicationContext().getSystemService("usb");
        }
        return f10g != null;
    }

    public boolean setVIDPID(int vendorId, int productId) {
        boolean z = false;
        if (vendorId == 0 || productId == 0) {
            Log.d("D2xx::", "Invalid parameter to setVIDPID");
        } else {
            C0019m c0019m = new C0019m(vendorId, productId);
            if (f9e.contains(c0019m)) {
                Log.i("D2xx::", "Existing vid:" + vendorId + "  pid:" + productId);
                return true;
            } else if (f9e.add(c0019m)) {
                z = true;
            } else {
                Log.d("D2xx::", "Failed to add VID/PID combination to list.");
            }
        }
        return z;
    }

    public int[][] getVIDPID() {
        int size = f9e.size();
        int[][] iArr = (int[][]) Array.newInstance(Integer.TYPE, new int[]{2, size});
        for (int i = FT_DEVICE_232B; i < size; i += FT_DEVICE_8U232AM) {
            C0019m c0019m = (C0019m) f9e.get(i);
            iArr[FT_DEVICE_232B][i] = c0019m.m123a();
            iArr[FT_DEVICE_8U232AM][i] = c0019m.m124b();
        }
        return iArr;
    }

    private void m6b() {
        synchronized (this.f12f) {
            int size = this.f12f.size();
            for (int i = FT_DEVICE_232B; i < size; i += FT_DEVICE_8U232AM) {
                this.f12f.remove(i);
            }
        }
    }

    public int createDeviceInfoList(Context parentContext) {
        ArrayList arrayList = new ArrayList();
        if (parentContext == null) {
            return FT_DEVICE_232B;
        }
        int size;
        m4a(parentContext);
        for (UsbDevice usbDevice : f10g.getDeviceList().values()) {
            if (isFtDevice(usbDevice)) {
                int interfaceCount = usbDevice.getInterfaceCount();
                for (int i = FT_DEVICE_232B; i < interfaceCount; i += FT_DEVICE_8U232AM) {
                    if (m7b(usbDevice)) {
                        synchronized (this.f12f) {
                            Object a = m0a(usbDevice);
                            if (a == null) {
                                a = new FT_Device(parentContext, f10g, usbDevice, usbDevice.getInterface(i));
                            } else {
                                this.f12f.remove(a);
                                a.m25a(parentContext);
                            }
                            arrayList.add(a);
                        }
                    }
                }
                continue;
            }
        }
        synchronized (this.f12f) {
            m6b();
            this.f12f = arrayList;
            size = this.f12f.size();
        }
        return size;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getDeviceInfoList(int r3, com.ftdi.j2xx.D2xxManager.FtDeviceInfoListNode[] r4) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = 0;
        r1 = r0;
    L_0x0003:
        if (r1 < r3) goto L_0x000d;
    L_0x0005:
        r0 = r2.f12f;	 Catch:{ all -> 0x001d }
        r0 = r0.size();	 Catch:{ all -> 0x001d }
        monitor-exit(r2);
        return r0;
    L_0x000d:
        r0 = r2.f12f;	 Catch:{ all -> 0x001d }
        r0 = r0.get(r1);	 Catch:{ all -> 0x001d }
        r0 = (com.ftdi.j2xx.FT_Device) r0;	 Catch:{ all -> 0x001d }
        r0 = r0.f20g;	 Catch:{ all -> 0x001d }
        r4[r1] = r0;	 Catch:{ all -> 0x001d }
        r0 = r1 + 1;
        r1 = r0;
        goto L_0x0003;
    L_0x001d:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ftdi.j2xx.D2xxManager.getDeviceInfoList(int, com.ftdi.j2xx.D2xxManager$FtDeviceInfoListNode[]):int");
    }

    public synchronized FtDeviceInfoListNode getDeviceInfoListDetail(int index) {
        FtDeviceInfoListNode ftDeviceInfoListNode;
        if (index > this.f12f.size() || index < 0) {
            ftDeviceInfoListNode = null;
        } else {
            ftDeviceInfoListNode = ((FT_Device) this.f12f.get(index)).f20g;
        }
        return ftDeviceInfoListNode;
    }

    public static int getLibraryVersion() {
        return 537919488;
    }

    private boolean m5a(Context context, FT_Device fT_Device, DriverParameters driverParameters) {
        if (fT_Device == null || context == null) {
            return false;
        }
        fT_Device.m25a(context);
        if (driverParameters != null) {
            fT_Device.setDriverParameters(driverParameters);
        }
        if (fT_Device.m26a(f10g) && fT_Device.isOpen()) {
            return true;
        }
        return false;
    }

    public synchronized FT_Device openByUsbDevice(Context parentContext, UsbDevice dev, DriverParameters params) {
        FT_Device fT_Device = null;
        synchronized (this) {
            if (isFtDevice(dev)) {
                FT_Device a = m0a(dev);
                if (m5a(parentContext, a, params)) {
                    fT_Device = a;
                }
            }
        }
        return fT_Device;
    }

    public synchronized FT_Device openByUsbDevice(Context parentContext, UsbDevice dev) {
        return openByUsbDevice(parentContext, dev, null);
    }

    public synchronized FT_Device openByIndex(Context parentContext, int index, DriverParameters params) {
        FT_Device fT_Device = null;
        synchronized (this) {
            if (index >= 0 && parentContext != null) {
                m4a(parentContext);
                FT_Device fT_Device2 = (FT_Device) this.f12f.get(index);
                if (!m5a(parentContext, fT_Device2, params)) {
                    fT_Device2 = null;
                }
                fT_Device = fT_Device2;
            }
        }
        return fT_Device;
    }

    public synchronized FT_Device openByIndex(Context parentContext, int index) {
        return openByIndex(parentContext, index, null);
    }

    public synchronized FT_Device openBySerialNumber(Context parentContext, String serialNumber, DriverParameters params) {
        FT_Device fT_Device = null;
        synchronized (this) {
            if (parentContext != null) {
                FT_Device fT_Device2;
                m4a(parentContext);
                for (int i = FT_DEVICE_232B; i < this.f12f.size(); i += FT_DEVICE_8U232AM) {
                    fT_Device2 = (FT_Device) this.f12f.get(i);
                    if (fT_Device2 != null) {
                        FtDeviceInfoListNode ftDeviceInfoListNode = fT_Device2.f20g;
                        if (ftDeviceInfoListNode == null) {
                            Log.d("D2xx::", "***devInfo cannot be null***");
                        } else if (ftDeviceInfoListNode.serialNumber.equals(serialNumber)) {
                            break;
                        }
                    }
                }
                fT_Device2 = null;
                if (!m5a(parentContext, fT_Device2, params)) {
                    fT_Device2 = null;
                }
                fT_Device = fT_Device2;
            }
        }
        return fT_Device;
    }

    public synchronized FT_Device openBySerialNumber(Context parentContext, String serialNumber) {
        return openBySerialNumber(parentContext, serialNumber, null);
    }

    public synchronized FT_Device openByDescription(Context parentContext, String description, DriverParameters params) {
        FT_Device fT_Device = null;
        synchronized (this) {
            if (parentContext != null) {
                FT_Device fT_Device2;
                m4a(parentContext);
                for (int i = FT_DEVICE_232B; i < this.f12f.size(); i += FT_DEVICE_8U232AM) {
                    fT_Device2 = (FT_Device) this.f12f.get(i);
                    if (fT_Device2 != null) {
                        FtDeviceInfoListNode ftDeviceInfoListNode = fT_Device2.f20g;
                        if (ftDeviceInfoListNode == null) {
                            Log.d("D2xx::", "***devInfo cannot be null***");
                        } else if (ftDeviceInfoListNode.description.equals(description)) {
                            break;
                        }
                    }
                }
                fT_Device2 = null;
                if (!m5a(parentContext, fT_Device2, params)) {
                    fT_Device2 = null;
                }
                fT_Device = fT_Device2;
            }
        }
        return fT_Device;
    }

    public synchronized FT_Device openByDescription(Context parentContext, String description) {
        return openByDescription(parentContext, description, null);
    }

    public synchronized FT_Device openByLocation(Context parentContext, int location, DriverParameters params) {
        FT_Device fT_Device = null;
        synchronized (this) {
            if (parentContext != null) {
                FT_Device fT_Device2;
                m4a(parentContext);
                for (int i = FT_DEVICE_232B; i < this.f12f.size(); i += FT_DEVICE_8U232AM) {
                    fT_Device2 = (FT_Device) this.f12f.get(i);
                    if (fT_Device2 != null) {
                        FtDeviceInfoListNode ftDeviceInfoListNode = fT_Device2.f20g;
                        if (ftDeviceInfoListNode == null) {
                            Log.d("D2xx::", "***devInfo cannot be null***");
                        } else if (ftDeviceInfoListNode.location == location) {
                            break;
                        }
                    }
                }
                fT_Device2 = null;
                if (!m5a(parentContext, fT_Device2, params)) {
                    fT_Device2 = null;
                }
                fT_Device = fT_Device2;
            }
        }
        return fT_Device;
    }

    public synchronized FT_Device openByLocation(Context parentContext, int location) {
        return openByLocation(parentContext, location, null);
    }

    public int addUsbDevice(UsbDevice dev) {
        int i = FT_DEVICE_232B;
        if (isFtDevice(dev)) {
            int interfaceCount = dev.getInterfaceCount();
            for (int i2 = FT_DEVICE_232B; i2 < interfaceCount; i2 += FT_DEVICE_8U232AM) {
                if (m7b(dev)) {
                    synchronized (this.f12f) {
                        Object a = m0a(dev);
                        if (a == null) {
                            a = new FT_Device(f6b, f10g, dev, dev.getInterface(i2));
                        } else {
                            a.m25a(f6b);
                        }
                        this.f12f.add(a);
                        i += FT_DEVICE_8U232AM;
                    }
                }
            }
        }
        return i;
    }
}
