package com.qualcomm.robotcore.util;

import android.app.Activity;
import android.os.Handler;
import android.view.WindowManager.LayoutParams;

public class Dimmer {
    public static final int DEFAULT_DIM_TIME = 30000;
    public static final int LONG_BRIGHT_TIME = 60000;
    public static final float MAXIMUM_BRIGHTNESS = 1.0f;
    public static final float MINIMUM_BRIGHTNESS = 0.05f;

    Handler handler = new Handler();
    Activity activity;
    final LayoutParams params;
    long waitTime;
    float screenBrightness = MAXIMUM_BRIGHTNESS;
    
    public Dimmer(Activity activity) {
        this(DEFAULT_DIM_TIME, activity);
    }

    public Dimmer(long waitTime, Activity activity) {
        this.waitTime = waitTime;
        this.activity = activity;
        this.params = activity.getWindow().getAttributes();
        this.screenBrightness = this.params.screenBrightness;
    }

    private float percentageDim() {
        float f = this.screenBrightness * MINIMUM_BRIGHTNESS;
        if (f < MINIMUM_BRIGHTNESS) {
            return MINIMUM_BRIGHTNESS;
        }
        return f;
    }

    public void handleDimTimer() {
        sendToUIThread(this.screenBrightness);
        this.handler.removeCallbacks(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendToUIThread(percentageDim());
            }
        }, waitTime);    }

    private void sendToUIThread(float brightness) {
        this.params.screenBrightness = brightness;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWindow().setAttributes(params);
            }
        });
    }

    public void longBright() {
        sendToUIThread(this.screenBrightness);
        this.handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendToUIThread(percentageDim());
            }
        }, LONG_BRIGHT_TIME);
    }
}
