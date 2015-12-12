package com.qualcomm.robotcore.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class BatteryChecker {
    Runnable f359a;
    private Context f360b;
    protected Handler batteryHandler;
    private long f361c;
    private long f362d;
    private BatteryWatcher f363e;

    /* renamed from: com.qualcomm.robotcore.util.BatteryChecker.1 */
    class C00431 implements Runnable {
        final /* synthetic */ BatteryChecker f358a;

        C00431(BatteryChecker batteryChecker) {
            this.f358a = batteryChecker;
        }

        public void run() {
            float batteryLevel = this.f358a.getBatteryLevel();
            this.f358a.f363e.updateBatteryLevel(batteryLevel);
            RobotLog.i("Battery Checker, Level Remaining: " + batteryLevel);
            this.f358a.batteryHandler.postDelayed(this.f358a.f359a, this.f358a.f361c);
        }
    }

    public interface BatteryWatcher {
        void updateBatteryLevel(float f);
    }

    public BatteryChecker(Context context, BatteryWatcher watcher, long delay) {
        this.f362d = 5000;
        this.f359a = new C00431(this);
        this.f360b = context;
        this.f363e = watcher;
        this.f361c = delay;
        this.batteryHandler = new Handler();
    }

    public float getBatteryLevel() {
        int i = -1;
        Intent registerReceiver = this.f360b.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int intExtra = registerReceiver.getIntExtra("level", -1);
        int intExtra2 = registerReceiver.getIntExtra("scale", -1);
        if (intExtra >= 0 && intExtra2 > 0) {
            i = (intExtra * 100) / intExtra2;
        }
        return (float) i;
    }

    public void startBatteryMonitoring() {
        this.batteryHandler.postDelayed(this.f359a, this.f362d);
    }

    public void endBatteryMonitoring() {
        this.batteryHandler.removeCallbacks(this.f359a);
    }
}
