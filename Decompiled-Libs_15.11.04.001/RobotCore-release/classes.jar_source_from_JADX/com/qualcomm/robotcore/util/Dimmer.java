package com.qualcomm.robotcore.util;

import android.app.Activity;
import android.os.Handler;
import android.view.WindowManager.LayoutParams;

public class Dimmer {
    public static final int DEFAULT_DIM_TIME = 30000;
    public static final int LONG_BRIGHT_TIME = 60000;
    public static final float MAXIMUM_BRIGHTNESS = 1.0f;
    public static final float MINIMUM_BRIGHTNESS = 0.05f;
    Handler f370a;
    Activity f371b;
    final LayoutParams f372c;
    long f373d;
    float f374e;

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.1 */
    class C00441 implements Runnable {
        final /* synthetic */ Dimmer f367a;

        C00441(Dimmer dimmer) {
            this.f367a = dimmer;
        }

        public void run() {
            this.f367a.m217a(this.f367a.m215a());
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.2 */
    class C00452 implements Runnable {
        final /* synthetic */ Dimmer f368a;

        C00452(Dimmer dimmer) {
            this.f368a = dimmer;
        }

        public void run() {
            this.f368a.f371b.getWindow().setAttributes(this.f368a.f372c);
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.3 */
    class C00463 implements Runnable {
        final /* synthetic */ Dimmer f369a;

        C00463(Dimmer dimmer) {
            this.f369a = dimmer;
        }

        public void run() {
            this.f369a.m217a(this.f369a.m215a());
        }
    }

    public Dimmer(Activity activity) {
        this(30000, activity);
    }

    public Dimmer(long waitTime, Activity activity) {
        this.f370a = new Handler();
        this.f374e = MAXIMUM_BRIGHTNESS;
        this.f373d = waitTime;
        this.f371b = activity;
        this.f372c = activity.getWindow().getAttributes();
        this.f374e = this.f372c.screenBrightness;
    }

    private float m215a() {
        float f = this.f374e * MINIMUM_BRIGHTNESS;
        if (f < MINIMUM_BRIGHTNESS) {
            return MINIMUM_BRIGHTNESS;
        }
        return f;
    }

    public void handleDimTimer() {
        m217a(this.f374e);
        this.f370a.removeCallbacks(null);
        this.f370a.postDelayed(new C00441(this), this.f373d);
    }

    private void m217a(float f) {
        this.f372c.screenBrightness = f;
        this.f371b.runOnUiThread(new C00452(this));
    }

    public void longBright() {
        m217a(this.f374e);
        Runnable c00463 = new C00463(this);
        this.f370a.removeCallbacksAndMessages(null);
        this.f370a.postDelayed(c00463, 60000);
    }
}
