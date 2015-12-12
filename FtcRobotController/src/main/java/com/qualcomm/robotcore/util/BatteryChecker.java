package com.qualcomm.robotcore.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;

public class BatteryChecker {
    private static final long DEFAULT_DELAY = 5000;
    Runnable batteryRunnable;
    private Context context;
    protected Handler batteryHandler;
    private long delay;
    private BatteryWatcher batteryWatcher;

    class batteryRunnable implements Runnable {
        final BatteryChecker batteryChecker;

        batteryRunnable(BatteryChecker batteryChecker) {
            this.batteryChecker = batteryChecker;
        }

        public void run() {
            float batteryLevel = this.batteryChecker.getBatteryLevel();
            this.batteryChecker.batteryWatcher.updateBatteryLevel(batteryLevel);
            RobotLog.i("Battery Checker, Level Remaining: " + batteryLevel);
            this.batteryChecker.batteryHandler.postDelayed(this.batteryChecker.batteryRunnable, this.batteryChecker.delay);
        }
    }

    public interface BatteryWatcher {
        void updateBatteryLevel(float f);
    }

    public BatteryChecker(Context context, BatteryWatcher watcher, long delay) {
        this.batteryRunnable = new batteryRunnable(this);
        this.context = context;
        this.batteryWatcher = watcher;
        this.delay = delay;
        this.batteryHandler = new Handler();
    }

    public float getBatteryLevel() {
        float batteryLevel = -1;
        Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(registerReceiver != null) {
            int level = registerReceiver.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = registerReceiver.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level >= 0 && scale > 0) {
                batteryLevel = (level * 100) / scale;
            }
        }
        return batteryLevel;
    }

    public void startBatteryMonitoring() {
        this.batteryHandler.postDelayed(this.batteryRunnable, DEFAULT_DELAY);
    }

    public void endBatteryMonitoring() {
        this.batteryHandler.removeCallbacks(this.batteryRunnable);
    }
}
