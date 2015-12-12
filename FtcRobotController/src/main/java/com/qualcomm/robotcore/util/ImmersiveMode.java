package com.qualcomm.robotcore.util;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class ImmersiveMode {
    View view;
    Handler handler;

    static class ImmersiveModeHandler extends Handler {
        final View view;

        ImmersiveModeHandler(View view) {
            this.view = view;
        }

        public void handleMessage(Message msg) {
            hideSystemUI(view);
        }
    }

    public ImmersiveMode(View decorView) {
        this.handler = new ImmersiveModeHandler(view);
        this.view = decorView;
    }

    public void cancelSystemUIHide() {
        this.handler.removeMessages(0);
    }

    private static void hideSystemUI(View view) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public void hideSystemUI() {
        hideSystemUI(this.view);
    }

    public static boolean apiOver19() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
