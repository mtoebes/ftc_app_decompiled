package com.qualcomm.ftccommon;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import com.qualcomm.ftccommon.UpdateUI.Callback;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager.EventLoopMonitor;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.factory.RobotFactory;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.Event;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.WifiDirectAssistantCallback;
import java.lang.Thread.State;

public class FtcRobotControllerService extends Service implements WifiDirectAssistantCallback {
    private final IBinder f23a;
    private WifiDirectAssistant f24b;
    private Robot f25c;
    private EventLoop f26d;
    private Event f27e;
    private String f28f;
    private Callback f29g;
    private final C0006a f30h;
    private final ElapsedTime f31i;
    private Thread f32j;

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.1 */
    static /* synthetic */ class C00051 {
        static final /* synthetic */ int[] f18a;
        static final /* synthetic */ int[] f19b;

        static {
            f19b = new int[Event.values().length];
            try {
                f19b[Event.CONNECTED_AS_GROUP_OWNER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f19b[Event.CONNECTED_AS_PEER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f19b[Event.CONNECTION_INFO_AVAILABLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f19b[Event.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            f18a = new int[RobotState.values().length];
            try {
                f18a[RobotState.INIT.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f18a[RobotState.NOT_STARTED.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f18a[RobotState.RUNNING.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f18a[RobotState.STOPPED.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f18a[RobotState.EMERGENCY_STOP.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f18a[RobotState.DROPPED_CONNECTION.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public class FtcRobotControllerBinder extends Binder {
        final /* synthetic */ FtcRobotControllerService f20a;

        public FtcRobotControllerBinder(FtcRobotControllerService ftcRobotControllerService) {
            this.f20a = ftcRobotControllerService;
        }

        public FtcRobotControllerService getService() {
            return this.f20a;
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.a */
    private class C0006a implements EventLoopMonitor {
        final /* synthetic */ FtcRobotControllerService f21a;

        private C0006a(FtcRobotControllerService ftcRobotControllerService) {
            this.f21a = ftcRobotControllerService;
        }

        public void onStateChange(RobotState state) {
            if (this.f21a.f29g != null) {
                switch (C00051.f18a[state.ordinal()]) {
                    case BuildConfig.VERSION_CODE /*1*/:
                        this.f21a.f29g.robotUpdate("Robot Status: init");
                    case 2:
                        this.f21a.f29g.robotUpdate("Robot Status: not started");
                    case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                        this.f21a.f29g.robotUpdate("Robot Status: running");
                    case 4:
                        this.f21a.f29g.robotUpdate("Robot Status: stopped");
                    case 5:
                        this.f21a.f29g.robotUpdate("Robot Status: EMERGENCY STOP");
                    case 6:
                        this.f21a.f29g.robotUpdate("Robot Status: dropped connection");
                    default:
                }
            }
        }
    }

    /* renamed from: com.qualcomm.ftccommon.FtcRobotControllerService.b */
    private class C0007b implements Runnable {
        final /* synthetic */ FtcRobotControllerService f22a;

        private C0007b(FtcRobotControllerService ftcRobotControllerService) {
            this.f22a = ftcRobotControllerService;
        }

        public void run() {
            try {
                if (this.f22a.f25c != null) {
                    this.f22a.f25c.shutdown();
                    this.f22a.f25c = null;
                }
                this.f22a.m17a("Robot Status: scanning for USB devices");
                try {
                    Thread.sleep(5000);
                    this.f22a.f25c = RobotFactory.createRobot();
                    this.f22a.m17a("Robot Status: waiting on network");
                    this.f22a.f31i.reset();
                    do {
                        if (this.f22a.f24b.isConnected()) {
                            this.f22a.m17a("Robot Status: starting robot");
                            try {
                                this.f22a.f25c.eventLoopManager.setMonitor(this.f22a.f30h);
                                this.f22a.f25c.start(this.f22a.f24b.getGroupOwnerAddress(), this.f22a.f26d);
                                return;
                            } catch (RobotCoreException e) {
                                this.f22a.m17a("Robot Status: failed to start robot");
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
                    } while (this.f22a.f31i.time() <= 120.0d);
                    this.f22a.m17a("Robot Status: network timed out");
                } catch (InterruptedException e3) {
                    this.f22a.m17a("Robot Status: abort due to interrupt");
                }
            } catch (RobotCoreException e4) {
                this.f22a.m17a("Robot Status: Unable to create robot!");
                RobotLog.setGlobalErrorMsg(e4.getMessage());
            }
        }
    }

    public FtcRobotControllerService() {
        this.f23a = new FtcRobotControllerBinder(this);
        this.f27e = Event.DISCONNECTED;
        this.f28f = "Robot Status: null";
        this.f29g = null;
        this.f30h = new C0006a();
        this.f31i = new ElapsedTime();
        this.f32j = null;
    }

    public WifiDirectAssistant getWifiDirectAssistant() {
        return this.f24b;
    }

    public Event getWifiDirectStatus() {
        return this.f27e;
    }

    public String getRobotStatus() {
        return this.f28f;
    }

    public IBinder onBind(Intent intent) {
        DbgLog.msg("Starting FTC Controller Service");
        DbgLog.msg("Android device is " + Build.MANUFACTURER + ", " + Build.MODEL);
        this.f24b = WifiDirectAssistant.getWifiDirectAssistant(this);
        this.f24b.setCallback(this);
        this.f24b.enable();
        if (Build.MODEL.equals(Device.MODEL_FOXDA_FL7007)) {
            this.f24b.discoverPeers();
        } else {
            this.f24b.createGroup();
        }
        return this.f23a;
    }

    public boolean onUnbind(Intent intent) {
        DbgLog.msg("Stopping FTC Controller Service");
        this.f24b.disable();
        shutdownRobot();
        return false;
    }

    public synchronized void setCallback(Callback callback) {
        this.f29g = callback;
    }

    public synchronized void setupRobot(EventLoop eventLoop) {
        if (this.f32j != null && this.f32j.isAlive()) {
            DbgLog.msg("FtcRobotControllerService.setupRobot() is currently running, stopping old setup");
            this.f32j.interrupt();
            while (this.f32j.isAlive()) {
                Thread.yield();
            }
            DbgLog.msg("Old setup stopped; restarting setup");
        }
        RobotLog.clearGlobalErrorMsg();
        DbgLog.msg("Processing robot setup");
        this.f26d = eventLoop;
        this.f32j = new Thread(new C0007b(), "Robot Setup");
        this.f32j.start();
        while (this.f32j.getState() == State.NEW) {
            Thread.yield();
        }
    }

    public synchronized void shutdownRobot() {
        if (this.f32j != null && this.f32j.isAlive()) {
            this.f32j.interrupt();
        }
        if (this.f25c != null) {
            this.f25c.shutdown();
        }
        this.f25c = null;
        m17a("Robot Status: null");
    }

    public void onWifiDirectEvent(Event event) {
        switch (C00051.f19b[event.ordinal()]) {
            case BuildConfig.VERSION_CODE /*1*/:
                DbgLog.msg("Wifi Direct - Group Owner");
                this.f24b.cancelDiscoverPeers();
                break;
            case 2:
                DbgLog.error("Wifi Direct - connected as peer, was expecting Group Owner");
                break;
            case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                DbgLog.msg("Wifi Direct Passphrase: " + this.f24b.getPassphrase());
                break;
            case 4:
                DbgLog.error("Wifi Direct Error: " + this.f24b.getFailureReason());
                break;
        }
        m16a(event);
    }

    private void m16a(Event event) {
        this.f27e = event;
        if (this.f29g != null) {
            this.f29g.wifiDirectUpdate(this.f27e);
        }
    }

    private void m17a(String str) {
        this.f28f = str;
        if (this.f29g != null) {
            this.f29g.robotUpdate(str);
        }
    }
}
