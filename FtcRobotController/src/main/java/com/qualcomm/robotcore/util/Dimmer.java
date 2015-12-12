package com.qualcomm.robotcore.util;

import android.app.Activity;
import android.os.Handler;
import android.view.WindowManager.LayoutParams;

public class Dimmer {
    public static final int DEFAULT_DIM_TIME = 30000;
    public static final int LONG_BRIGHT_TIME = 60000;
    public static final float MAXIMUM_BRIGHTNESS = 1.0f;
    public static final float MINIMUM_BRIGHTNESS = 0.05f;
    Handler handler;
    Activity activity;
    final LayoutParams layoutParams;
    long waitTime;
    float brightness;


    class setScreenBrightness implements Runnable {
        final Dimmer dimmer;

        setScreenBrightness(Dimmer dimmer) {
            this.dimmer = dimmer;
        }

        public void run() {
            this.dimmer.activity.getWindow().setAttributes(this.dimmer.layoutParams);
        }
    }

    class sendToUIThreadRunnable implements Runnable {
        final Dimmer dimmer;

        sendToUIThreadRunnable(Dimmer dimmer) {
            this.dimmer = dimmer;
        }

        public void run() {
            this.dimmer.sendToUIThread(this.dimmer.getBrightnessPercent());
        }
    }

    public Dimmer(Activity activity) {
        this(DEFAULT_DIM_TIME, activity);
    }

    public Dimmer(long waitTime, Activity activity) {
        this.handler = new Handler();
        this.brightness = MAXIMUM_BRIGHTNESS;
        this.waitTime = waitTime;
        this.activity = activity;
        this.layoutParams = activity.getWindow().getAttributes();
        this.brightness = this.layoutParams.screenBrightness;
    }

    private float getBrightnessPercent() {
        float f = this.brightness * MINIMUM_BRIGHTNESS;
        if (f < MINIMUM_BRIGHTNESS) {
            return MINIMUM_BRIGHTNESS;
        }
        return f;
    }

    public void handleDimTimer() {
        sendToUIThread(this.brightness);
        this.handler.removeCallbacks(null);
        this.handler.postDelayed(new sendToUIThreadRunnable(this), this.waitTime);
    }

    private void sendToUIThread(float brightness) {
        this.layoutParams.screenBrightness = brightness;
        this.activity.runOnUiThread(new setScreenBrightness(this));
    }

    public void longBright() {
        sendToUIThread(this.brightness);
        this.handler.removeCallbacksAndMessages(null);
        this.handler.postDelayed(new sendToUIThreadRunnable(this), LONG_BRIGHT_TIME);
    }
}
