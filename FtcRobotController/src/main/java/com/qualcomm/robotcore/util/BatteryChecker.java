/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        if (registerReceiver != null) {
            int level = registerReceiver.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = registerReceiver.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if ((level >= 0) && (scale > 0)) {
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
