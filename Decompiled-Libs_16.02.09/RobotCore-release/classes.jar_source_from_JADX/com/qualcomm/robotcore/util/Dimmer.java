package com.qualcomm.robotcore.util;

import android.app.Activity;
import android.os.Handler;
import android.view.WindowManager.LayoutParams;

public class Dimmer {
    public static final int DEFAULT_DIM_TIME = 30000;
    public static final int LONG_BRIGHT_TIME = 60000;
    public static final float MAXIMUM_BRIGHTNESS = 1.0f;
    public static final float MINIMUM_BRIGHTNESS = 0.05f;
    Handler f366a;
    Activity f367b;
    final LayoutParams f368c;
    long f369d;
    float f370e;

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.1 */
    class C00491 implements Runnable {
        final /* synthetic */ Dimmer f363a;

        C00491(Dimmer dimmer) {
            this.f363a = dimmer;
        }

        public void run() {
            this.f363a.m235a(this.f363a.m233a());
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.2 */
    class C00502 implements Runnable {
        final /* synthetic */ Dimmer f364a;

        C00502(Dimmer dimmer) {
            this.f364a = dimmer;
        }

        public void run() {
            this.f364a.f367b.getWindow().setAttributes(this.f364a.f368c);
        }
    }

    /* renamed from: com.qualcomm.robotcore.util.Dimmer.3 */
    class C00513 implements Runnable {
        final /* synthetic */ Dimmer f365a;

        C00513(Dimmer dimmer) {
            this.f365a = dimmer;
        }

        public void run() {
            this.f365a.m235a(this.f365a.m233a());
        }
    }

    public Dimmer(Activity activity) {
        this(30000, activity);
    }

    public Dimmer(long waitTime, Activity activity) {
        this.f366a = new Handler();
        this.f370e = MAXIMUM_BRIGHTNESS;
        this.f369d = waitTime;
        this.f367b = activity;
        this.f368c = activity.getWindow().getAttributes();
        this.f370e = this.f368c.screenBrightness;
    }

    private float m233a() {
        float f = this.f370e * MINIMUM_BRIGHTNESS;
        if (f < MINIMUM_BRIGHTNESS) {
            return MINIMUM_BRIGHTNESS;
        }
        return f;
    }

    public void handleDimTimer() {
        m235a(this.f370e);
        this.f366a.removeCallbacks(null);
        this.f366a.postDelayed(new C00491(this), this.f369d);
    }

    private void m235a(float f) {
        this.f368c.screenBrightness = f;
        this.f367b.runOnUiThread(new C00502(this));
    }

    public void longBright() {
        m235a(this.f370e);
        Runnable c00513 = new C00513(this);
        this.f366a.removeCallbacksAndMessages(null);
        this.f366a.postDelayed(c00513, 60000);
    }
}
