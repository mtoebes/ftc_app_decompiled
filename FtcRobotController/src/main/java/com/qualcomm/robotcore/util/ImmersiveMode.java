package com.qualcomm.robotcore.util;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.qualcomm.robotcore.robocol.RobocolConfig;

public class ImmersiveMode {
    View view;
    Handler handler;

    class ImmersiveModeHandler extends Handler {
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    }

    public ImmersiveMode(View decorView) {
        this.handler = new ImmersiveModeHandler();
        this.view = decorView;
    }

    public void cancelSystemUIHide() {
        handler.removeMessages(0);
    }

    public void hideSystemUI() {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static boolean apiOver19() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
