package com.qualcomm.ftccommon;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Util;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant.Event;

public class UpdateUI {
    Restarter f81a;
    FtcRobotControllerService f82b;
    Activity f83c;
    Dimmer f84d;
    protected TextView textDeviceName;
    protected TextView textErrorMessage;
    protected TextView[] textGamepad;
    protected TextView textOpMode;
    protected TextView textRobotStatus;
    protected TextView textWifiDirectStatus;

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.1 */
    class C00201 implements Runnable {
        final /* synthetic */ String f65a;
        final /* synthetic */ UpdateUI f66b;

        C00201(UpdateUI updateUI, String str) {
            this.f66b = updateUI;
            this.f65a = str;
        }

        public void run() {
            this.f66b.textWifiDirectStatus.setText(this.f65a);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.2 */
    class C00212 implements Runnable {
        final /* synthetic */ String f67a;
        final /* synthetic */ UpdateUI f68b;

        C00212(UpdateUI updateUI, String str) {
            this.f68b = updateUI;
            this.f67a = str;
        }

        public void run() {
            this.f68b.textDeviceName.setText(this.f67a);
        }
    }

    /* renamed from: com.qualcomm.ftccommon.UpdateUI.3 */
    static /* synthetic */ class C00223 {
        static final /* synthetic */ int[] f69a;

        static {
            f69a = new int[Event.values().length];
            try {
                f69a[Event.DISCONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f69a[Event.CONNECTED_AS_GROUP_OWNER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f69a[Event.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f69a[Event.CONNECTION_INFO_AVAILABLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public class Callback {
        final /* synthetic */ UpdateUI f80a;

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.1 */
        class C00231 implements Runnable {
            final /* synthetic */ Callback f70a;

            C00231(Callback callback) {
                this.f70a = callback;
            }

            public void run() {
                Toast.makeText(this.f70a.f80a.f83c, "Restarting Robot", 0).show();
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.2 */
        class C00262 implements Runnable {
            final /* synthetic */ Callback f73a;

            /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.2.1 */
            class C00251 implements Runnable {
                final /* synthetic */ C00262 f72a;

                /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.2.1.1 */
                class C00241 implements Runnable {
                    final /* synthetic */ C00251 f71a;

                    C00241(C00251 c00251) {
                        this.f71a = c00251;
                    }

                    public void run() {
                        this.f71a.f72a.f73a.f80a.m38a();
                    }
                }

                C00251(C00262 c00262) {
                    this.f72a = c00262;
                }

                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                    this.f72a.f73a.f80a.f83c.runOnUiThread(new C00241(this));
                }
            }

            C00262(Callback callback) {
                this.f73a = callback;
            }

            public void run() {
                Util.logThreadLifeCycle("restart robot UI thunker", new C00251(this));
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.3 */
        class C00273 implements Runnable {
            final /* synthetic */ Gamepad[] f74a;
            final /* synthetic */ String f75b;
            final /* synthetic */ Callback f76c;

            C00273(Callback callback, Gamepad[] gamepadArr, String str) {
                this.f76c = callback;
                this.f74a = gamepadArr;
                this.f75b = str;
            }

            public void run() {
                String string;
                int i = 0;
                while (i < this.f76c.f80a.textGamepad.length && i < this.f74a.length) {
                    if (this.f74a[i].id == -1) {
                        this.f76c.f80a.textGamepad[i].setText(" ");
                    } else {
                        this.f76c.f80a.textGamepad[i].setText(this.f74a[i].toString());
                    }
                    i++;
                }
                if (this.f75b.equals("$Stop$Robot$")) {
                    string = this.f76c.f80a.f83c.getString(R.string.defaultOpModeName);
                } else {
                    string = this.f75b;
                }
                this.f76c.f80a.textOpMode.setText("Op Mode: " + string);
                this.f76c.m37a();
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.4 */
        class C00284 implements Runnable {
            final /* synthetic */ String f77a;
            final /* synthetic */ Callback f78b;

            C00284(Callback callback, String str) {
                this.f78b = callback;
                this.f77a = str;
            }

            public void run() {
                this.f78b.f80a.textRobotStatus.setText(this.f77a);
                this.f78b.m37a();
            }
        }

        /* renamed from: com.qualcomm.ftccommon.UpdateUI.Callback.5 */
        class C00295 implements Runnable {
            final /* synthetic */ Callback f79a;

            C00295(Callback callback) {
                this.f79a = callback;
            }

            public void run() {
                this.f79a.m37a();
            }
        }

        public Callback(UpdateUI updateUI) {
            this.f80a = updateUI;
        }

        public void restartRobot() {
            this.f80a.f83c.runOnUiThread(new C00231(this));
            new Thread(new C00262(this)).start();
        }

        public void updateUi(String opModeName, Gamepad[] gamepads) {
            this.f80a.f83c.runOnUiThread(new C00273(this, gamepads, opModeName));
        }

        public void wifiDirectUpdate(Event event) {
            String str = "Wifi Direct - ";
            switch (C00223.f69a[event.ordinal()]) {
                case BuildConfig.VERSION_CODE /*1*/:
                    this.f80a.m41a("Wifi Direct - disconnected");
                case 2:
                    this.f80a.m41a("Wifi Direct - enabled");
                case LaunchActivityConstantsList.FTC_ROBOT_CONTROLLER_ACTIVITY_CONFIGURE_ROBOT /*3*/:
                    this.f80a.m41a("Wifi Direct - ERROR");
                case 4:
                    this.f80a.m43b(this.f80a.f82b.getWifiDirectAssistant().getDeviceName());
                default:
            }
        }

        public void robotUpdate(String status) {
            DbgLog.msg(status);
            this.f80a.f83c.runOnUiThread(new C00284(this, status));
        }

        public void refreshErrorTextOnUiThread() {
            this.f80a.f83c.runOnUiThread(new C00295(this));
        }

        void m37a() {
            String globalErrorMsg = RobotLog.getGlobalErrorMsg();
            String globalWarningMessage = RobotLog.getGlobalWarningMessage();
            if (globalErrorMsg.isEmpty() && globalWarningMessage.isEmpty()) {
                this.f80a.textErrorMessage.setText(BuildConfig.VERSION_NAME);
                return;
            }
            if (globalErrorMsg.isEmpty()) {
                this.f80a.textErrorMessage.setText(this.f80a.f83c.getString(R.string.error_text_warning, new Object[]{m36a(globalWarningMessage)}));
            } else {
                this.f80a.textErrorMessage.setText(this.f80a.f83c.getString(R.string.error_text_error, new Object[]{m36a(globalErrorMsg)}));
            }
            this.f80a.f84d.longBright();
        }

        String m36a(String str) {
            if (str.length() > 160) {
                return str.substring(0, 160) + "...";
            }
            return str;
        }
    }

    public UpdateUI(Activity activity, Dimmer dimmer) {
        this.textGamepad = new TextView[2];
        this.f83c = activity;
        this.f84d = dimmer;
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
        this.f82b = controllerService;
    }

    public void setRestarter(Restarter restarter) {
        this.f81a = restarter;
    }

    private void m41a(String str) {
        DbgLog.msg(str);
        this.f83c.runOnUiThread(new C00201(this, str));
    }

    private void m43b(String str) {
        this.f83c.runOnUiThread(new C00212(this, str));
    }

    private void m38a() {
        this.f81a.requestRestart();
    }
}
