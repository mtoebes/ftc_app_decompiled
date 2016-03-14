package com.qualcomm.ftccommon;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import com.android.internal.util.Predicate;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.qualcomm.ftccommon.UpdateUI.Callback;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.modernrobotics.ModernRoboticsUsbUtil;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule.ARMINGSTATE;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.util.Util;
import java.util.HashSet;
import java.util.Set;

public class FtcEventLoop implements EventLoop {
    protected Set<String> attachedUsbDevices;
    protected final Object attachedUsbDevicesLock;
    protected FtcEventLoopHandler ftcEventLoopHandler;
    protected OpModeManager opModeManager;
    protected OpModeRegister register;
    protected Context robotControllerContext;
    protected UsbModuleAttachmentHandler usbModuleAttachmentHandler;

    /* renamed from: com.qualcomm.ftccommon.FtcEventLoop.1 */
    class C00081 implements Predicate<Object> {
        final /* synthetic */ FtcEventLoop f16a;

        C00081(FtcEventLoop ftcEventLoop) {
            this.f16a = ftcEventLoop;
        }

        public boolean apply(Object o) {
            return o instanceof RobotUsbModule;
        }
    }

    public class DefaultUsbModuleAttachmentHandler implements UsbModuleAttachmentHandler {
        final /* synthetic */ FtcEventLoop f17a;

        public DefaultUsbModuleAttachmentHandler(FtcEventLoop ftcEventLoop) {
            this.f17a = ftcEventLoop;
        }

        public void handleUsbModuleAttach(RobotUsbModule module) throws RobotCoreException, InterruptedException {
            String a = m15a(module);
            DbgLog.msg("======= MODULE ATTACH: disarm %s=======", a);
            module.disarm();
            DbgLog.msg("======= MODULE ATTACH: arm or pretend %s=======", a);
            module.armOrPretend();
            if (module.getArmingState() == ARMINGSTATE.ARMED) {
                DbgLog.msg("======= MODULE ATTACH: complete %s=======", a);
            } else {
                DbgLog.msg("======= MODULE ATTACH: complete %s=======", a);
            }
        }

        public void handleUsbModuleDetach(RobotUsbModule module) throws RobotCoreException, InterruptedException {
            String a = m15a(module);
            DbgLog.msg("======= MODULE DETACH RECOVERY: disarm %s=======", a);
            module.disarm();
            DbgLog.msg("======= MODULE DETACH RECOVERY: pretend %s=======", a);
            module.pretend();
            DbgLog.msg("======= MODULE DETACH RECOVERY: complete %s=======", a);
        }

        String m15a(RobotUsbModule robotUsbModule) {
            return HardwareFactory.getSerialNumberDisplayName(robotUsbModule.getSerialNumber());
        }
    }

    public FtcEventLoop(HardwareFactory hardwareFactory, OpModeRegister register, Callback callback, Context robotControllerContext) {
        this.robotControllerContext = robotControllerContext;
        this.opModeManager = createOpModeManager();
        this.ftcEventLoopHandler = new FtcEventLoopHandler(hardwareFactory, callback, robotControllerContext);
        this.register = register;
        this.usbModuleAttachmentHandler = new DefaultUsbModuleAttachmentHandler(this);
        this.attachedUsbDevicesLock = new Object();
        this.attachedUsbDevices = new HashSet();
    }

    protected OpModeManager createOpModeManager() {
        return new OpModeManager(new HardwareMap(this.robotControllerContext));
    }

    public OpModeManager getOpModeManager() {
        return this.opModeManager;
    }

    public UsbModuleAttachmentHandler getUsbModuleAttachmentHandler() {
        return this.usbModuleAttachmentHandler;
    }

    public void setUsbModuleAttachmentHandler(UsbModuleAttachmentHandler handler) {
        this.usbModuleAttachmentHandler = handler;
    }

    public synchronized void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException {
        DbgLog.msg("======= INIT START =======");
        this.opModeManager.init(eventLoopManager);
        this.opModeManager.registerOpModes(this.register);
        this.ftcEventLoopHandler.init(eventLoopManager);
        HardwareMap hardwareMap = this.ftcEventLoopHandler.getHardwareMap();
        ModernRoboticsUsbUtil.init(hardwareMap.appContext, hardwareMap);
        this.opModeManager.setHardwareMap(hardwareMap);
        hardwareMap.logDevices();
        DbgLog.msg("======= INIT FINISH =======");
    }

    public synchronized void loop() throws RobotCoreException {
        this.ftcEventLoopHandler.displayGamePadInfo(this.opModeManager.getActiveOpModeName());
        this.opModeManager.runActiveOpMode(this.ftcEventLoopHandler.getGamepads());
        this.ftcEventLoopHandler.sendTelemetryData(this.opModeManager.getActiveOpMode().telemetry);
    }

    public synchronized void teardown() throws RobotCoreException {
        DbgLog.msg("======= TEARDOWN =======");
        this.opModeManager.stopActiveOpMode();
        this.ftcEventLoopHandler.shutdownMotorControllers();
        this.ftcEventLoopHandler.shutdownServoControllers();
        this.ftcEventLoopHandler.shutdownLegacyModules();
        this.ftcEventLoopHandler.shutdownCoreInterfaceDeviceModules();
        DbgLog.msg("======= TEARDOWN COMPLETE =======");
    }

    public synchronized void processCommand(Command command) {
        DbgLog.msg("Processing Command: %s(%d) %s", command.getName(), Integer.valueOf(command.getSequenceNumber()), command.getExtra());
        this.ftcEventLoopHandler.sendBatteryInfo();
        String name = command.getName();
        String extra = command.getExtra();
        if (name.equals(CommandList.CMD_RESTART_ROBOT)) {
            m16a();
        } else if (name.equals(CommandList.CMD_REQUEST_OP_MODE_LIST)) {
            m18b();
        } else if (name.equals(CommandList.CMD_INIT_OP_MODE)) {
            m17a(extra);
        } else if (name.equals(CommandList.CMD_RUN_OP_MODE)) {
            m19c();
        } else {
            DbgLog.msg("Unknown command: " + name);
        }
    }

    private void m16a() {
        this.ftcEventLoopHandler.restartRobot();
    }

    private void m18b() {
        String str = BuildConfig.VERSION_NAME;
        for (String str2 : this.opModeManager.getOpModes()) {
            if (!str2.equals("$Stop$Robot$")) {
                if (!str.isEmpty()) {
                    str = str + Util.ASCII_RECORD_SEPARATOR;
                }
                str = str + str2;
            }
        }
        this.ftcEventLoopHandler.sendCommand(new Command(CommandList.CMD_REQUEST_OP_MODE_LIST_RESP, str));
        EventLoopManager eventLoopManager = this.ftcEventLoopHandler.getEventLoopManager();
        if (eventLoopManager != null) {
            eventLoopManager.refreshSystemTelemetryNow();
        }
    }

    private void m17a(String str) {
        String opMode = this.ftcEventLoopHandler.getOpMode(str);
        this.opModeManager.initActiveOpMode(opMode);
        this.ftcEventLoopHandler.sendCommand(new Command(CommandList.CMD_INIT_OP_MODE_RESP, opMode));
    }

    private void m19c() {
        this.opModeManager.startActiveOpMode();
        this.ftcEventLoopHandler.sendCommand(new Command(CommandList.CMD_RUN_OP_MODE_RESP, this.opModeManager.getActiveOpModeName()));
    }

    public void onUsbDeviceAttached(UsbDevice usbDevice) {
        Object serialNumberOfUsbDevice = getSerialNumberOfUsbDevice(usbDevice);
        if (serialNumberOfUsbDevice == null) {
            serialNumberOfUsbDevice = getSerialNumberOfUsbDevice(usbDevice);
        }
        if (serialNumberOfUsbDevice != null) {
            synchronized (this.attachedUsbDevicesLock) {
                this.attachedUsbDevices.add(serialNumberOfUsbDevice);
            }
            return;
        }
        DbgLog.msg("ignoring: unable get serial number of attached UsbDevice vendor=0x%04x, product=0x%04x device=0x%04x name=%s", Integer.valueOf(usbDevice.getVendorId()), Integer.valueOf(usbDevice.getProductId()), Integer.valueOf(usbDevice.getDeviceId()), usbDevice.getDeviceName());
    }

    protected String getSerialNumberOfUsbDevice(UsbDevice usbDevice) {
        Throwable th;
        String str = null;
        FT_Device openByUsbDevice;
        try {
            openByUsbDevice = D2xxManager.getInstance(this.robotControllerContext).openByUsbDevice(this.robotControllerContext, usbDevice);
            if (openByUsbDevice != null) {
                try {
                    str = openByUsbDevice.getDeviceInfo().serialNumber;
                } catch (NullPointerException e) {
                    if (openByUsbDevice != null) {
                        openByUsbDevice.close();
                    }
                    return str;
                } catch (D2xxException e2) {
                    if (openByUsbDevice != null) {
                        openByUsbDevice.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (openByUsbDevice != null) {
                        openByUsbDevice.close();
                    }
                    throw th;
                }
            }
            if (openByUsbDevice != null) {
                openByUsbDevice.close();
            }
        } catch (NullPointerException e3) {
            openByUsbDevice = str;
            if (openByUsbDevice != null) {
                openByUsbDevice.close();
            }
            return str;
        } catch (D2xxException e4) {
            openByUsbDevice = str;
            if (openByUsbDevice != null) {
                openByUsbDevice.close();
            }
            return str;
        } catch (Throwable th3) {
            Throwable th4 = th3;
            openByUsbDevice = str;
            th = th4;
            if (openByUsbDevice != null) {
                openByUsbDevice.close();
            }
            throw th;
        }
        return str;
    }

    public void processedRecentlyAttachedUsbDevices() throws RobotCoreException, InterruptedException {
        synchronized (this.attachedUsbDevicesLock) {
            Set<String> set = this.attachedUsbDevices;
            this.attachedUsbDevices = new HashSet();
        }
        UsbModuleAttachmentHandler usbModuleAttachmentHandler = this.usbModuleAttachmentHandler;
        if (usbModuleAttachmentHandler != null && !set.isEmpty()) {
            Set<RobotUsbModule> findDevices = this.ftcEventLoopHandler.getHardwareMap().findDevices(new C00081(this));
            for (String str : set) {
                for (RobotUsbModule robotUsbModule : findDevices) {
                    if (robotUsbModule.getSerialNumber().toString().equals(str)) {
                        usbModuleAttachmentHandler.handleUsbModuleAttach(robotUsbModule);
                        break;
                    }
                }
            }
        }
    }

    public synchronized void handleUsbModuleDetach(RobotUsbModule module) throws RobotCoreException, InterruptedException {
        UsbModuleAttachmentHandler usbModuleAttachmentHandler = this.usbModuleAttachmentHandler;
        if (usbModuleAttachmentHandler != null) {
            usbModuleAttachmentHandler.handleUsbModuleDetach(module);
        }
    }
}
