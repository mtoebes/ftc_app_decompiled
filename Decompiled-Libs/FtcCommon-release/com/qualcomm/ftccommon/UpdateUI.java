package com.qualcomm.ftccommon;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.Event;

public class UpdateUI {
    Restarter f66a;
    FtcRobotControllerService f67b;
    Activity f68c;
    Dimmer f69d;
    protected TextView textDeviceName;
    protected TextView textErrorMessage;
    protected TextView[] textGamepad;
    protected TextView textOpMode;
    protected TextView textRobotStatus;
    protected TextView textWifiDirectStatus;

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.1 */
    class C00151 implements Runnable {
        final /* synthetic */ String f52a;
        final /* synthetic */ UpdateUI f53b;

        C00151(UpdateUI updateUI, String str) {
            this.f53b = updateUI;
            this.f52a = str;
        }

        public void run() {
            this.f53b.textWifiDirectStatus.setText(this.f52a);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.2 */
    class C00162 implements Runnable {
        final /* synthetic */ String f54a;
        final /* synthetic */ UpdateUI f55b;

        C00162(UpdateUI updateUI, String str) {
            this.f55b = updateUI;
            this.f54a = str;
        }

        public void run() {
            this.f55b.textDeviceName.setText(this.f54a);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.3 */
    static /* synthetic */ class C00173 {
        static final /* synthetic */ int[] f56a;

        static {
            f56a = new int[Event.values().length];
            try {
                f56a[Event.DISCONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f56a[Event.CONNECTED_AS_GROUP_OWNER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f56a[Event.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f56a[Event.CONNECTION_INFO_AVAILABLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public class Callback {
        final /* synthetic */ UpdateUI f65a;

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.1 */
        class C00181 implements Runnable {
            final /* synthetic */ Callback f57a;

            C00181(Callback callback) {
                this.f57a = callback;
            }

            public void run() {
                Toast.makeText(this.f57a.f65a.f68c, "Restarting Robot", 0).show();
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.2 */
        class C00202 extends Thread {
            final /* synthetic */ Callback f59a;

            /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.2.1 */
            class C00191 implements Runnable {
                final /* synthetic */ C00202 f58a;

                C00191(C00202 c00202) {
                    this.f58a = c00202;
                }

                public void run() {
                    this.f58a.f59a.f65a.m27a();
                }
            }

            C00202(Callback callback) {
                this.f59a = callback;
            }

            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                }
                this.f59a.f65a.f68c.runOnUiThread(new C00191(this));
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.3 */
        class C00213 implements Runnable {
            final /* synthetic */ Gamepad[] f60a;
            final /* synthetic */ String f61b;
            final /* synthetic */ Callback f62c;

            C00213(Callback callback, Gamepad[] gamepadArr, String str) {
                this.f62c = callback;
                this.f60a = gamepadArr;
                this.f61b = str;
            }

            public void run() {
                int i = 0;
                while (i < this.f62c.f65a.textGamepad.length && i < this.f60a.length) {
                    if (this.f60a[i].id == -1) {
                        this.f62c.f65a.textGamepad[i].setText(" ");
                    } else {
                        this.f62c.f65a.textGamepad[i].setText(this.f60a[i].toString());
                    }
                    i++;
                }
                this.f62c.f65a.textOpMode.setText("Op Mode: " + this.f61b);
                this.f62c.f65a.textErrorMessage.setText(RobotLog.getGlobalErrorMsg());
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.4 */
        class C00224 implements Runnable {
            final /* synthetic */ String f63a;
            final /* synthetic */ Callback f64b;

            C00224(Callback callback, String str) {
                this.f64b = callback;
                this.f63a = str;
            }

            public void run() {
                this.f64b.f65a.textRobotStatus.setText(this.f63a);
                this.f64b.f65a.textErrorMessage.setText(RobotLog.getGlobalErrorMsg());
                if (RobotLog.hasGlobalErrorMsg()) {
                    this.f64b.f65a.f69d.longBright();
                }
            }
        }

        public Callback(UpdateUI updateUI) {
            this.f65a = updateUI;
        }

        public void restartRobot() {
            this.f65a.f68c.runOnUiThread(new C00181(this));
            new C00202(this).start();
        }

        public void updateUi(String opModeName, Gamepad[] gamepads) {
            this.f65a.f68c.runOnUiThread(new C00213(this, gamepads, opModeName));
        }

        public void wifiDirectUpdate(Event event) {
            String str = "Wifi Direct - ";
            switch (C00173.f56a[event.ordinal()]) {
                case BuildConfig.VERSION_CODE /*1*/:
                    this.f65a.m30a("Wifi Direct - disconnected");
                case 2:
                    this.f65a.m30a("Wifi Direct - enabled");
                case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                    this.f65a.m30a("Wifi Direct - ERROR");
                case 4:
                    this.f65a.m32b(this.f65a.f67b.getWifiDirectAssistant().getDeviceName());
                default:
            }
        }

        public void robotUpdate(String status) {
            DbgLog.msg(status);
            this.f65a.f68c.runOnUiThread(new C00224(this, status));
        }
    }

    public UpdateUI(Activity activity, Dimmer dimmer) {
        this.textGamepad = new TextView[2];
        this.f68c = activity;
        this.f69d = dimmer;
    }

    public void setTextViews(TextView textWifiDirectStatus, TextView textRobotStatus, TextView[] textGamepad, TextView textOpMode, TextView textErrorMessage, TextView textDeviceName) {
        this.textWifiDirectStatus = textWifiDirectStatus;
        this.textRobotStatus = textRobotStatus;
        this.textGamepad = textGamepad;
        this.textOpMode = textOpMode;
        this.textErrorMessage = textErrorMessage;
        this.textDeviceName = textDeviceName;
    }

    public void setControllerService(FtcRobotControllerService controllerService) {
        this.f67b = controllerService;
    }

    public void setRestarter(Restarter restarter) {
        this.f66a = restarter;
    }

    private void m30a(String str) {
        DbgLog.msg(str);
        this.f68c.runOnUiThread(new C00151(this, str));
    }

    private void m32b(String str) {
        this.f68c.runOnUiThread(new C00162(this, str));
    }

    private void m27a() {
        this.f66a.requestRestart();
    }
}
