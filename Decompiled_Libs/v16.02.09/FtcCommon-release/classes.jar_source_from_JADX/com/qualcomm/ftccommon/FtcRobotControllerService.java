package com.qualcomm.ftccommon;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import com.qualcomm.ftccommon.ConfigWifiDirectActivity.Flag;
import com.qualcomm.ftccommon.UpdateUI.Callback;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager.EventLoopMonitor;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.factory.RobotFactory;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Util;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.Event;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.WifiDirectAssistantCallback;
import java.lang.Thread.State;

public class FtcRobotControllerService extends Service implements WifiDirectAssistantCallback {
    private final IBinder f36a;
    private WifiDirectAssistant f37b;
    private Robot f38c;
    private EventLoop f39d;
    private Event f40e;
    private String f41f;
    private Callback f42g;
    private final C0010a f43h;
    private final ElapsedTime f44i;
    private Thread f45j;

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.1 */
    static /* synthetic */ class C00091 {
        static final /* synthetic */ int[] f30a;
        static final /* synthetic */ int[] f31b;

        static {
            f31b = new int[Event.values().length];
            try {
                f31b[Event.CONNECTED_AS_GROUP_OWNER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f31b[Event.CONNECTED_AS_PEER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f31b[Event.CONNECTION_INFO_AVAILABLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f31b[Event.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            f30a = new int[RobotState.values().length];
            try {
                f30a[RobotState.INIT.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f30a[RobotState.NOT_STARTED.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f30a[RobotState.RUNNING.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f30a[RobotState.STOPPED.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f30a[RobotState.EMERGENCY_STOP.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f30a[RobotState.DROPPED_CONNECTION.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public class FtcRobotControllerBinder extends Binder {
        final /* synthetic */ FtcRobotControllerService f32a;

        public FtcRobotControllerBinder(FtcRobotControllerService ftcRobotControllerService) {
            this.f32a = ftcRobotControllerService;
        }

        public FtcRobotControllerService getService() {
            return this.f32a;
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.a */
    private class C0010a implements EventLoopMonitor {
        final /* synthetic */ FtcRobotControllerService f33a;

        private C0010a(FtcRobotControllerService ftcRobotControllerService) {
            this.f33a = ftcRobotControllerService;
        }

        public void onStateChange(RobotState state) {
            if (this.f33a.f42g != null) {
                switch (C00091.f30a[state.ordinal()]) {
                    case BuildConfig.VERSION_CODE /*1*/:
                        this.f33a.f42g.robotUpdate("Robot Status: init");
                    case 2:
                        this.f33a.f42g.robotUpdate("Robot Status: not started");
                    case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                        this.f33a.f42g.robotUpdate("Robot Status: running");
                    case 4:
                        this.f33a.f42g.robotUpdate("Robot Status: stopped");
                    case 5:
                        this.f33a.f42g.robotUpdate("Robot Status: EMERGENCY STOP");
                    case 6:
                        this.f33a.f42g.robotUpdate("Robot Status: dropped connection");
                    default:
                }
            }
        }

        public void onErrorOrWarning() {
            if (this.f33a.f42g != null) {
                this.f33a.f42g.refreshErrorTextOnUiThread();
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.b */
    private class C0012b implements Runnable {
        final /* synthetic */ FtcRobotControllerService f35a;

        /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.b.1 */
        class C00111 implements Runnable {
            final /* synthetic */ C0012b f34a;

            C00111(C0012b c0012b) {
                this.f34a = c0012b;
            }

            public void run() {
                try {
                    if (this.f34a.f35a.f38c != null) {
                        this.f34a.f35a.f38c.shutdown();
                        this.f34a.f35a.f38c = null;
                    }
                    this.f34a.f35a.m25a("Robot Status: scanning for USB devices");
                    try {
                        Thread.sleep(5000);
                        this.f34a.f35a.f38c = RobotFactory.createRobot();
                        this.f34a.f35a.m25a("Robot Status: waiting on network");
                        this.f34a.f35a.f44i.reset();
                        do {
                            if (this.f34a.f35a.f37b.isConnected()) {
                                this.f34a.f35a.m25a("Robot Status: starting robot");
                                try {
                                    this.f34a.f35a.f38c.eventLoopManager.setMonitor(this.f34a.f35a.f43h);
                                    this.f34a.f35a.f38c.start(this.f34a.f35a.f37b.getGroupOwnerAddress(), this.f34a.f35a.f39d);
                                    return;
                                } catch (RobotCoreException e) {
                                    this.f34a.f35a.m25a("Robot Status: failed to start robot");
                                    RobotLog.setGlobalErrorMsg(e.getMessage());
                                    return;
                                }
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e2) {
                                DbgLog.msg("interrupt waiting for network; aborting setup");
                                return;
                            }
                        } while (this.f34a.f35a.f44i.time() <= 120.0d);
                        this.f34a.f35a.m25a("Robot Status: network timed out");
                    } catch (InterruptedException e3) {
                        this.f34a.f35a.m25a("Robot Status: abort due to interrupt");
                    }
                } catch (RobotCoreException e4) {
                    this.f34a.f35a.m25a("Robot Status: Unable to create robot!");
                    RobotLog.setGlobalErrorMsg(e4.getMessage());
                }
            }
        }

        private C0012b(FtcRobotControllerService ftcRobotControllerService) {
            this.f35a = ftcRobotControllerService;
        }

        public void run() {
            Util.logThreadLifeCycle("RobotSetupRunnable.run()", new C00111(this));
        }
    }

    public FtcRobotControllerService() {
        this.f36a = new FtcRobotControllerBinder(this);
        this.f40e = Event.DISCONNECTED;
        this.f41f = "Robot Status: null";
        this.f42g = null;
        this.f43h = new C0010a();
        this.f44i = new ElapsedTime();
        this.f45j = null;
    }

    public WifiDirectAssistant getWifiDirectAssistant() {
        return this.f37b;
    }

    public Event getWifiDirectStatus() {
        return this.f40e;
    }

    public String getRobotStatus() {
        return this.f41f;
    }

    public IBinder onBind(Intent intent) {
        DbgLog.msg("Starting FTC Controller Service");
        DbgLog.msg("Android device is " + Build.MANUFACTURER + ", " + Build.MODEL);
        this.f37b = WifiDirectAssistant.getWifiDirectAssistant(this);
        this.f37b.setCallback(this);
        this.f37b.enable();
        if (Build.MODEL.equals(Device.MODEL_FOXDA_FL7007)) {
            this.f37b.discoverPeers();
        } else {
            this.f37b.createGroup();
        }
        return this.f36a;
    }

    public boolean onUnbind(Intent intent) {
        DbgLog.msg("Stopping FTC Controller Service");
        this.f37b.disable();
        shutdownRobot();
        return false;
    }

    public synchronized void setCallback(Callback callback) {
        this.f42g = callback;
    }

    public synchronized void setupRobot(EventLoop eventLoop) {
        m31a();
        RobotLog.clearGlobalErrorMsg();
        RobotLog.clearGlobalWarningMsg();
        DbgLog.msg("Processing robot setup");
        this.f39d = eventLoop;
        this.f45j = new Thread(new C0012b());
        this.f45j.start();
        while (this.f45j.getState() == State.NEW) {
            Thread.yield();
        }
    }

    void m31a() {
        if (this.f45j != null && this.f45j.isAlive()) {
            while (true) {
                try {
                    this.f45j.interrupt();
                    this.f45j.join(5000);
                    if (this.f45j.isAlive()) {
                        RobotLog.v("waiting for robotSetupThread shutdown");
                    } else {
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public synchronized void shutdownRobot() {
        m31a();
        if (this.f38c != null) {
            this.f38c.shutdown();
        }
        this.f38c = null;
        m25a("Robot Status: null");
    }

    public void onWifiDirectEvent(Event event) {
        switch (C00091.f31b[event.ordinal()]) {
            case BuildConfig.VERSION_CODE /*1*/:
                DbgLog.msg("Wifi Direct - Group Owner");
                this.f37b.cancelDiscoverPeers();
                if (!this.f37b.isDeviceNameValid()) {
                    RobotLog.e("Wifi-Direct device name contains non-printable characters");
                    ConfigWifiDirectActivity.launch(getBaseContext(), Flag.WIFI_DIRECT_DEVICE_NAME_INVALID);
                    break;
                }
                break;
            case 2:
                DbgLog.error("Wifi Direct - connected as peer, was expecting Group Owner");
                ConfigWifiDirectActivity.launch(getBaseContext(), Flag.WIFI_DIRECT_FIX_CONFIG);
                break;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                DbgLog.msg("Wifi Direct Passphrase: " + this.f37b.getPassphrase());
                break;
            case 4:
                DbgLog.error("Wifi Direct Error: " + this.f37b.getFailureReason());
                break;
        }
        m24a(event);
    }

    private void m24a(Event event) {
        this.f40e = event;
        if (this.f42g != null) {
            this.f42g.wifiDirectUpdate(this.f40e);
        }
    }

    private void m25a(String str) {
        this.f41f = str;
        if (this.f42g != null) {
            this.f42g.robotUpdate(str);
        }
    }
}
