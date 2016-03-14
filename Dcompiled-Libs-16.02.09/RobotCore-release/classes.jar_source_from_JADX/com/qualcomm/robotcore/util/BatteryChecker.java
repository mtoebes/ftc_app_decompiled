package com.qualcomm.robotcore.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class BatteryChecker {
    Runnable f355a;
    private Context f356b;
    protected Handler batteryHandler;
    private long f357c;
    private long f358d;
    private BatteryWatcher f359e;

    /* renamed from: com.qualcomm.robotcore.util.BatteryChecker.1 */
    class C00481 implements Runnable {
        final /* synthetic */ BatteryChecker f354a;

        C00481(BatteryChecker batteryChecker) {
            this.f354a = batteryChecker;
        }

        public void run() {
            float batteryLevel = this.f354a.getBatteryLevel();
            this.f354a.f359e.updateBatteryLevel(batteryLevel);
            RobotLog.m252i("Battery Checker, Level Remaining: " + batteryLevel);
            this.f354a.batteryHandler.postDelayed(this.f354a.f355a, this.f354a.f357c);
        }
    }

    public interface BatteryWatcher {
        void updateBatteryLevel(float f);
    }

    public BatteryChecker(Context context, BatteryWatcher watcher, long delay) {
        this.f358d = 5000;
        this.f355a = new C00481(this);
        this.f356b = context;
        this.f359e = watcher;
        this.f357c = delay;
        this.batteryHandler = new Handler();
    }

    public float getBatteryLevel() {
        int i = -1;
        Intent registerReceiver = this.f356b.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int intExtra = registerReceiver.getIntExtra("level", -1);
        int intExtra2 = registerReceiver.getIntExtra("scale", -1);
        if (intExtra >= 0 && intExtra2 > 0) {
            i = (intExtra * 100) / intExtra2;
        }
        return (float) i;
    }

    public void startBatteryMonitoring() {
        this.batteryHandler.postDelayed(this.f355a, this.f358d);
    }

    public void endBatteryMonitoring() {
        this.batteryHandler.removeCallbacks(this.f355a);
    }
}
