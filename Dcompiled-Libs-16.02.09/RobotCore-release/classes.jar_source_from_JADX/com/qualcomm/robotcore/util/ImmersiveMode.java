package com.qualcomm.robotcore.util;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class ImmersiveMode {
    View f379a;
    Handler f380b;

    /* renamed from: com.qualcomm.robotcore.util.ImmersiveMode.1 */
    class C00541 extends Handler {
        final /* synthetic */ ImmersiveMode f378a;

        C00541(ImmersiveMode immersiveMode) {
            this.f378a = immersiveMode;
        }

        public void handleMessage(Message msg) {
            this.f378a.hideSystemUI();
        }
    }

    public ImmersiveMode(View decorView) {
        this.f380b = new C00541(this);
        this.f379a = decorView;
    }

    public void cancelSystemUIHide() {
        this.f380b.removeMessages(0);
    }

    public void hideSystemUI() {
        this.f379a.setSystemUiVisibility(RobocolConfig.MAX_PACKET_SIZE);
    }

    public static boolean apiOver19() {
        return VERSION.SDK_INT >= 19;
    }
}
