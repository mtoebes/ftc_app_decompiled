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

package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import com.qualcomm.robotcore.util.RobotLog;

public class WifiAssistant {
    private final IntentFilter filter = new IntentFilter();
    private final Context context;
    private final WifiBroadcastReceiver wifiBroadcastReceiver;

    public interface WifiAssistantCallback {
        void wifiEventCallback(WifiState wifiState);
    }

    public enum WifiState {
        CONNECTED,
        NOT_CONNECTED
    }

    private static class WifiBroadcastReceiver extends BroadcastReceiver {
        private WifiState wifiState;
        private final WifiAssistantCallback wifiAssistantCallback;

        public WifiBroadcastReceiver(WifiAssistantCallback wifiAssistantCallback) {
            this.wifiAssistantCallback = wifiAssistantCallback;
        }

        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                return;
            }
            if (((NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)).isConnected()) {
                setWifiState(WifiState.CONNECTED);
            } else {
                setWifiState(WifiState.NOT_CONNECTED);
            }
        }

        private void setWifiState(WifiState wifiState) {
            if (this.wifiState != wifiState) {
                this.wifiState = wifiState;
                if (this.wifiAssistantCallback != null) {
                    this.wifiAssistantCallback.wifiEventCallback(this.wifiState);
                }
            }
        }
    }

    public WifiAssistant(Context context, WifiAssistantCallback callback) {
        this.context = context;
        if (callback == null) {
            RobotLog.v("WifiAssistantCallback is null");
        }
        this.wifiBroadcastReceiver = new WifiBroadcastReceiver(callback);
        this.filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    public void enable() {
        this.context.registerReceiver(this.wifiBroadcastReceiver, this.filter);
    }

    public void disable() {
        this.context.unregisterReceiver(this.wifiBroadcastReceiver);
    }
}
