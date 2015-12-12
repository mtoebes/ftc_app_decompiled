package com.qualcomm.robotcore.util;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class ImmersiveMode {
    View view;
    Handler handler;

    class ImmersiveModeHandler extends Handler {
        final ImmersiveMode immersiveMode;

        ImmersiveModeHandler(ImmersiveMode immersiveMode) {
            this.immersiveMode = immersiveMode;
        }

        public void handleMessage(Message msg) {
            this.immersiveMode.hideSystemUI();
        }
    }

    public ImmersiveMode(View decorView) {
        this.handler = new ImmersiveModeHandler(this);
        this.view = decorView;
    }

    public void cancelSystemUIHide() {
        this.handler.removeMessages(0);
    }

    public void hideSystemUI() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            this.view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public static boolean apiOver19() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
