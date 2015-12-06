package com.qualcomm.robotcore.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class BatteryChecker {
    private static final long DELAY = 5000;

    Runnable batteryCheckerRunnable = new BatteryCheckerRunnable();;
    protected Handler batteryHandler = new Handler();

    private Context context;
    private long delay;
    private BatteryWatcher watcher;

    class BatteryCheckerRunnable implements Runnable {
        public void run() {
            float batteryLevel = getBatteryLevel();
            watcher.updateBatteryLevel(batteryLevel);
            RobotLog.i("Battery Checker, Level Remaining: " + batteryLevel);
            batteryHandler.postDelayed(batteryCheckerRunnable, delay);
        }
    }

    public interface BatteryWatcher {
        void updateBatteryLevel(float f);
    }

    public BatteryChecker(Context context, BatteryWatcher watcher, long delay) {
        batteryCheckerRunnable = new BatteryCheckerRunnable();
        this.context = context;
        this.watcher = watcher;
        this.delay = delay;
    }

    public float getBatteryLevel() {
            Intent registerReceiver = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED ));
            int level = registerReceiver.getIntExtra("level", -1);
            int scale = registerReceiver.getIntExtra("scale", -1);
            if (level >= 0 && scale > 0) {
                return (level * 100) / scale;
            } else {
                return -1;
            }
    }

    public void startBatteryMonitoring() {
        this.batteryHandler.postDelayed(this.batteryCheckerRunnable, DELAY);
    }

    public void endBatteryMonitoring() {
        this.batteryHandler.removeCallbacks(this.batteryCheckerRunnable);
    }
}
