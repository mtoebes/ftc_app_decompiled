package com.qualcomm.robotcore.util;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class ImmersiveMode {
    View f383a;
    Handler f384b;

    /* renamed from: com.qualcomm.robotcore.util.ImmersiveMode.1 */
    class C00491 extends Handler {
        final /* synthetic */ ImmersiveMode f382a;

        C00491(ImmersiveMode immersiveMode) {
            this.f382a = immersiveMode;
        }

        public void handleMessage(Message msg) {
            this.f382a.hideSystemUI();
        }
    }

    public ImmersiveMode(View decorView) {
        this.f384b = new C00491(this);
        this.f383a = decorView;
    }

    public void cancelSystemUIHide() {
        this.f384b.removeMessages(0);
    }

    public void hideSystemUI() {
        this.f383a.setSystemUiVisibility(RobocolConfig.MAX_PACKET_SIZE);
    }

    public static boolean apiOver19() {
        return VERSION.SDK_INT >= 19;
    }
}
