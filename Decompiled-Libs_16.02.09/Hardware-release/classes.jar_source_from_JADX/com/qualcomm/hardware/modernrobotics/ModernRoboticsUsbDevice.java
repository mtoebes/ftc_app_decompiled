package com.qualcomm.hardware.modernrobotics;

import android.content.Context;
import com.qualcomm.hardware.BuildConfig;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.hardware.R;
import com.qualcomm.hardware.modernrobotics.ReadWriteRunnable.Callback;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.usb.RobotUsbDevice;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ArmingStateCallback;
import com.qualcomm.robotcore.util.GlobalWarningSource;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ModernRoboticsUsbDevice implements Callback, RobotUsbModule, GlobalWarningSource {
    public static final int DEVICE_ID_DC_MOTOR_CONTROLLER = 77;
    public static final int DEVICE_ID_DEVICE_INTERFACE_MODULE = 65;
    public static final int DEVICE_ID_LEGACY_MODULE = 73;
    public static final int DEVICE_ID_SERVO_CONTROLLER = 83;
    public static final int MFG_CODE_MODERN_ROBOTICS = 77;
    protected final Object armingLock;
    protected ARMINGSTATE armingState;
    protected final Context context;
    protected final CreateReadWriteRunnable createReadWriteRunnable;
    protected final EventLoopManager eventLoopManager;
    protected final OpenRobotUsbDevice openRobotUsbDevice;
    protected volatile ReadWriteRunnable readWriteRunnable;
    protected ExecutorService readWriteService;
    protected final Set<ArmingStateCallback> registeredCallbacks;
    protected RobotUsbDevice robotUsbDevice;
    protected final SerialNumber serialNumber;
    protected String warningMessage;
    protected final Object warningMessageLock;
    protected int warningMessageSuppressionCount;

    public interface OpenRobotUsbDevice {
        RobotUsbDevice open() throws RobotCoreException, InterruptedException;
    }

    public interface CreateReadWriteRunnable {
        ReadWriteRunnable create(RobotUsbDevice robotUsbDevice) throws RobotCoreException, InterruptedException;
    }

    /* renamed from: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.1 */
    static /* synthetic */ class C00201 {
        static final /* synthetic */ int[] f154a;

        static {
            f154a = new int[ARMINGSTATE.values().length];
            try {
                f154a[ARMINGSTATE.ARMED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f154a[ARMINGSTATE.DISARMED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f154a[ARMINGSTATE.PRETENDING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f154a[ARMINGSTATE.TO_ARMED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f154a[ARMINGSTATE.TO_PRETENDING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f154a[ARMINGSTATE.CLOSED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public abstract String getDeviceName();

    public ModernRoboticsUsbDevice(Context context, SerialNumber serialNumber, EventLoopManager manager, OpenRobotUsbDevice openRobotUsbDevice, CreateReadWriteRunnable createReadWriteRunnable) throws RobotCoreException, InterruptedException {
        this.armingLock = new Object();
        this.registeredCallbacks = new CopyOnWriteArraySet();
        this.warningMessageLock = new Object();
        this.context = context;
        this.serialNumber = serialNumber;
        this.eventLoopManager = manager;
        this.openRobotUsbDevice = openRobotUsbDevice;
        this.robotUsbDevice = null;
        this.createReadWriteRunnable = createReadWriteRunnable;
        this.readWriteService = null;
        this.armingState = ARMINGSTATE.DISARMED;
        this.warningMessageSuppressionCount = 0;
        this.warningMessage = BuildConfig.VERSION_NAME;
        RobotLog.registerGlobalWarningSource(this);
    }

    public void initializeHardware() {
    }

    public void registerCallback(ArmingStateCallback callback) {
        this.registeredCallbacks.add(callback);
    }

    public void unregisterCallback(ArmingStateCallback callback) {
        this.registeredCallbacks.remove(callback);
    }

    public String getGlobalWarning() {
        String str;
        synchronized (this.warningMessageLock) {
            str = this.warningMessageSuppressionCount > 0 ? BuildConfig.VERSION_NAME : this.warningMessage;
        }
        return str;
    }

    public void clearGlobalWarning() {
        synchronized (this.warningMessageLock) {
            this.warningMessage = BuildConfig.VERSION_NAME;
            this.warningMessageSuppressionCount = 0;
        }
    }

    public void suppressGlobalWarning(boolean suppress) {
        synchronized (this.warningMessageLock) {
            if (suppress) {
                this.warningMessageSuppressionCount++;
            } else {
                this.warningMessageSuppressionCount--;
            }
        }
    }

    public boolean setGlobalWarning(String warning) {
        boolean z;
        synchronized (this.warningMessageLock) {
            if (warning != null) {
                if (this.warningMessage.isEmpty()) {
                    this.warningMessage = warning;
                    z = true;
                }
            }
            z = false;
        }
        return z;
    }

    protected void armDevice() throws RobotCoreException, InterruptedException {
        synchronized (this.armingLock) {
            this.warningMessage = BuildConfig.VERSION_NAME;
            try {
                armDevice(this.openRobotUsbDevice.open());
            } catch (RobotCoreException e) {
                setGlobalWarning(String.format(this.context.getString(R.string.warningUnableToOpen), new Object[]{HardwareFactory.getSerialNumberDisplayName(this.serialNumber)}));
                throw e;
            } catch (NullPointerException e2) {
                setGlobalWarning(String.format(this.context.getString(R.string.warningUnableToOpen), new Object[]{HardwareFactory.getSerialNumberDisplayName(this.serialNumber)}));
                throw e2;
            }
        }
    }

    protected void pretendDevice() throws RobotCoreException, InterruptedException {
        synchronized (this.armingLock) {
            armDevice(getPretendDevice());
        }
    }

    protected RobotUsbDevice getPretendDevice() {
        return new PretendModernRoboticsUsbDevice();
    }

    protected void armDevice(RobotUsbDevice device) throws RobotCoreException, InterruptedException {
        synchronized (this.armingLock) {
            this.robotUsbDevice = device;
            this.readWriteRunnable = this.createReadWriteRunnable.create(device);
            if (this.readWriteRunnable != null) {
                String str = "Starting up %sdevice %s";
                Object[] objArr = new Object[2];
                objArr[0] = this.armingState == ARMINGSTATE.TO_PRETENDING ? "pretend " : BuildConfig.VERSION_NAME;
                objArr[1] = this.serialNumber.toString();
                RobotLog.v(str, objArr);
                this.readWriteRunnable.setOwner(this);
                this.readWriteRunnable.setCallback(this);
                this.readWriteService = Executors.newSingleThreadExecutor();
                this.readWriteRunnable.executeUsing(this.readWriteService);
                this.readWriteRunnable.blockUntilReady();
                this.eventLoopManager.registerSyncdDevice(this.readWriteRunnable);
                this.readWriteRunnable.setAcceptingWrites(true);
            }
        }
    }

    protected void disarmDevice() throws InterruptedException {
        synchronized (this.armingLock) {
            if (this.readWriteService != null) {
                this.readWriteService.shutdown();
            }
            if (this.readWriteRunnable != null) {
                this.readWriteRunnable.setAcceptingWrites(false);
                this.readWriteRunnable.drainPendingWrites();
                this.eventLoopManager.unregisterSyncdDevice(this.readWriteRunnable);
                this.readWriteRunnable.close();
                this.readWriteRunnable = null;
            }
            if (this.readWriteService != null) {
                this.readWriteService.awaitTermination(30, TimeUnit.DAYS);
                this.readWriteService = null;
            }
            if (this.robotUsbDevice != null) {
                this.robotUsbDevice.close();
                this.robotUsbDevice = null;
            }
        }
    }

    public ARMINGSTATE getArmingState() {
        return this.armingState;
    }

    protected void setArmingState(ARMINGSTATE state) {
        this.armingState = state;
        for (ArmingStateCallback onModuleStateChange : this.registeredCallbacks) {
            onModuleStateChange.onModuleStateChange(this, state);
        }
    }

    protected boolean isArmed() {
        return this.armingState == ARMINGSTATE.ARMED;
    }

    protected boolean isPretending() {
        return this.armingState == ARMINGSTATE.PRETENDING;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void arm() throws com.qualcomm.robotcore.exception.RobotCoreException, java.lang.InterruptedException {
        /*
        r6 = this;
        r1 = r6.armingLock;
        monitor-enter(r1);
        r0 = com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.C00201.f154a;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = r6.armingState;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = r2.ordinal();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0 = r0[r2];	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        switch(r0) {
            case 1: goto L_0x002c;
            case 2: goto L_0x002e;
            default: goto L_0x0010;
        };	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
    L_0x0010:
        r0 = new com.qualcomm.robotcore.exception.RobotCoreException;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = "illegal state: can't arm() from state %s";
        r3 = 1;
        r3 = new java.lang.Object[r3];	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r4 = 0;
        r5 = r6.armingState;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r5 = r5.toString();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r3[r4] = r5;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0.<init>(r2, r3);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        throw r0;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
    L_0x0024:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
    L_0x0029:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
        throw r0;
    L_0x002c:
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
    L_0x002d:
        return;
    L_0x002e:
        r0 = com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE.TO_ARMED;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.setArmingState(r0);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.doArm();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0 = com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE.ARMED;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.setArmingState(r0);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
        goto L_0x002d;
    L_0x003d:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
    L_0x0042:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.arm():void");
    }

    protected void doArm() throws RobotCoreException, InterruptedException {
        armDevice();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void pretend() throws com.qualcomm.robotcore.exception.RobotCoreException, java.lang.InterruptedException {
        /*
        r6 = this;
        r1 = r6.armingLock;
        monitor-enter(r1);
        r0 = com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.C00201.f154a;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = r6.armingState;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = r2.ordinal();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0 = r0[r2];	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        switch(r0) {
            case 2: goto L_0x002e;
            case 3: goto L_0x002c;
            default: goto L_0x0010;
        };	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
    L_0x0010:
        r0 = new com.qualcomm.robotcore.exception.RobotCoreException;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r2 = "illegal state: can't pretend() from state %s";
        r3 = 1;
        r3 = new java.lang.Object[r3];	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r4 = 0;
        r5 = r6.armingState;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r5 = r5.toString();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r3[r4] = r5;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0.<init>(r2, r3);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        throw r0;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
    L_0x0024:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
    L_0x0029:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
        throw r0;
    L_0x002c:
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
    L_0x002d:
        return;
    L_0x002e:
        r0 = com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE.TO_PRETENDING;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.setArmingState(r0);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.doPretend();	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r0 = com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE.PRETENDING;	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        r6.setArmingState(r0);	 Catch:{ RobotCoreException -> 0x0024, InterruptedException -> 0x003d, NullPointerException -> 0x0042 }
        monitor-exit(r1);	 Catch:{ all -> 0x0029 }
        goto L_0x002d;
    L_0x003d:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
    L_0x0042:
        r0 = move-exception;
        r6.disarm();	 Catch:{ all -> 0x0029 }
        throw r0;	 Catch:{ all -> 0x0029 }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.hardware.modernrobotics.ModernRoboticsUsbDevice.pretend():void");
    }

    public void armOrPretend() throws RobotCoreException, InterruptedException {
        synchronized (this.armingLock) {
            try {
                arm();
            } catch (RobotCoreException e) {
                pretend();
            } catch (InterruptedException e2) {
                pretend();
                Thread.currentThread().interrupt();
            } catch (NullPointerException e3) {
                pretend();
            }
        }
    }

    protected void doPretend() throws RobotCoreException, InterruptedException {
        pretendDevice();
    }

    public void disarm() throws RobotCoreException, InterruptedException {
        synchronized (this.armingLock) {
            switch (C00201.f154a[this.armingState.ordinal()]) {
                case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                case ModernRoboticsUsbLegacyModule.ADDRESS_BUFFER_STATUS /*3*/:
                case ModernRoboticsUsbLegacyModule.ADDRESS_ANALOG_PORT_S0 /*4*/:
                case ModernRoboticsUsbDeviceInterfaceModule.MAX_I2C_PORT_NUMBER /*5*/:
                    setArmingState(ARMINGSTATE.TO_DISARMED);
                    doDisarm();
                    setArmingState(ARMINGSTATE.DISARMED);
                    return;
                case ModernRoboticsUsbDeviceInterfaceModule.WORD_SIZE /*2*/:
                    return;
                default:
                    throw new RobotCoreException("illegal state: can't disarm() from state %s", new Object[]{this.armingState.toString()});
            }
        }
    }

    protected void doDisarm() throws RobotCoreException, InterruptedException {
        disarmDevice();
    }

    public void close() {
        synchronized (this.armingLock) {
            try {
                switch (C00201.f154a[this.armingState.ordinal()]) {
                    case ModernRoboticsUsbDeviceInterfaceModule.OFFSET_I2C_PORT_I2C_ADDRESS /*1*/:
                        doCloseFromArmed();
                        break;
                    case ModernRoboticsUsbServoController.MAX_SERVOS /*6*/:
                        setArmingState(ARMINGSTATE.CLOSED);
                        this.warningMessage = BuildConfig.VERSION_NAME;
                        return;
                    default:
                        doCloseFromOther();
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (RobotCoreException e2) {
            } catch (NullPointerException e3) {
            } finally {
                setArmingState(ARMINGSTATE.CLOSED);
                this.warningMessage = BuildConfig.VERSION_NAME;
            }
        }
    }

    protected void doCloseFromArmed() throws RobotCoreException, InterruptedException {
        disarm();
    }

    protected void doCloseFromOther() throws RobotCoreException, InterruptedException {
        disarm();
    }

    public ReadWriteRunnable getReadWriteRunnable() {
        return this.readWriteRunnable;
    }

    public OpenRobotUsbDevice getOpenRobotUsbDevice() {
        return this.openRobotUsbDevice;
    }

    public CreateReadWriteRunnable getCreateReadWriteRunnable() {
        return this.createReadWriteRunnable;
    }

    public SerialNumber getSerialNumber() {
        return this.serialNumber;
    }

    public int getVersion() {
        return read(0);
    }

    public void write(int address, byte data) {
        write(address, new byte[]{data});
    }

    public void write(int address, int data) {
        write(address, new byte[]{(byte) data});
    }

    public void write(int address, double data) {
        write(address, new byte[]{(byte) ((int) data)});
    }

    public void write(int address, byte[] data) {
        ReadWriteRunnable readWriteRunnable = this.readWriteRunnable;
        if (readWriteRunnable != null) {
            readWriteRunnable.write(address, data);
        }
    }

    public byte readFromWriteCache(int address) {
        return readFromWriteCache(address, 1)[0];
    }

    public byte[] readFromWriteCache(int address, int size) {
        ReadWriteRunnable readWriteRunnable = this.readWriteRunnable;
        if (readWriteRunnable != null) {
            return readWriteRunnable.readFromWriteCache(address, size);
        }
        return new byte[size];
    }

    public byte read(int address) {
        return read(address, 1)[0];
    }

    public byte[] read(int address, int size) {
        ReadWriteRunnable readWriteRunnable = this.readWriteRunnable;
        if (readWriteRunnable != null) {
            return readWriteRunnable.read(address, size);
        }
        return new byte[size];
    }

    public void startupComplete() throws InterruptedException {
    }

    public void readComplete() throws InterruptedException {
    }

    public void writeComplete() throws InterruptedException {
    }

    public void shutdownComplete() throws InterruptedException {
    }
}
