package com.qualcomm.robotcore.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Looper;

import com.qualcomm.robotcore.util.RobotLog;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectAssistant {
    private static WifiDirectAssistant wifiDirectAssistant;
    private final List<WifiP2pDevice> wifiP2pDevices = new ArrayList<WifiP2pDevice>();
    private Context context;
    private boolean isWifiP2pEnabled;
    private final IntentFilter intentFilter = new IntentFilter();
    private final Channel p2pChannel;
    private final WifiP2pManager wifiP2pManager;
    private AssistantBroadcastReceiver assistantBroadcastReceiver = new AssistantBroadcastReceiver(this);
    private final AssistantConnectionInfoListener connectionInfoListener = new AssistantConnectionInfoListener(this);
    private final AssistantPeerListListener peerListListener = new AssistantPeerListListener(this);
    private final AssistantGroupInfoListener groupInfoListener = new AssistantGroupInfoListener(this);
    private int failureReason;
    private ConnectStatus connectStatus = ConnectStatus.NOT_CONNECTED;
    private Event event;
    private String deviceMacAddress = "";
    private String deviceName = "";
    private InetAddress GroupOwnerInetAddress;
    private String groupOwnerMacAddress = "";
    private String groupOwnerName = "";
    private String passphrase = "";
    private boolean groupFormed;
    private int wifiDirectAssistantClientsNum;
    private WifiDirectAssistantCallback wifiDirectAssistantCallback;

    class DiscoverActionListener implements ActionListener {
        final WifiDirectAssistant wifiDirectAssistant1;

        DiscoverActionListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant1 = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.wifiDirectAssistant1.setEvent(Event.DISCOVERING_PEERS);
            RobotLog.d("Wifi Direct discovering peers");
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.wifiDirectAssistant1.failureReason = reason;
            RobotLog.w("Wifi Direct failure while trying to discover peers - reason: " + failureReasonToString);
            this.wifiDirectAssistant1.setEvent(Event.ERROR);
        }
    }

    class GroupActionListener implements ActionListener {
        final WifiDirectAssistant wifiDirectAssistant1;

        GroupActionListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant1 = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.wifiDirectAssistant1.connectStatus = ConnectStatus.GROUP_OWNER;
            this.wifiDirectAssistant1.setEvent(Event.GROUP_CREATED);
            RobotLog.d("Wifi Direct created group");
        }

        public void onFailure(int reason) {
            if (reason == 2) {
                RobotLog.d("Wifi Direct cannot create group, does group already exist?");
                return;
            }
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.wifiDirectAssistant1.failureReason = reason;
            RobotLog.w("Wifi Direct failure while trying to create group - reason: " + failureReasonToString);
            this.wifiDirectAssistant1.connectStatus = ConnectStatus.ERROR;
            this.wifiDirectAssistant1.setEvent(Event.ERROR);
        }
    }

    class ConnectActionListener implements ActionListener {
        final WifiDirectAssistant wifiDirectAssistant1;

        ConnectActionListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant1 = wifiDirectAssistant;
        }

        public void onSuccess() {
            RobotLog.d("WifiDirect connect started");
            this.wifiDirectAssistant1.setEvent(Event.CONNECTING);
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.wifiDirectAssistant1.failureReason = reason;
            RobotLog.d("WifiDirect connect cannot start - reason: " + failureReasonToString);
            this.wifiDirectAssistant1.setEvent(Event.ERROR);
        }
    }

    public enum ConnectStatus {
        NOT_CONNECTED,
        CONNECTING,
        CONNECTED,
        GROUP_OWNER,
        ERROR
    }

    public enum Event {
        DISCOVERING_PEERS,
        PEERS_AVAILABLE,
        GROUP_CREATED,
        CONNECTING,
        CONNECTED_AS_PEER,
        CONNECTED_AS_GROUP_OWNER,
        DISCONNECTED,
        CONNECTION_INFO_AVAILABLE,
        ERROR
    }

    public interface WifiDirectAssistantCallback {
        void onWifiDirectEvent(Event event);
    }

    private class AssistantConnectionInfoListener implements ConnectionInfoListener {
        final WifiDirectAssistant wifiDirectAssistant;

        private AssistantConnectionInfoListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant = wifiDirectAssistant;
        }

        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            this.wifiDirectAssistant.wifiP2pManager.requestGroupInfo(this.wifiDirectAssistant.p2pChannel, this.wifiDirectAssistant.groupInfoListener);
            this.wifiDirectAssistant.GroupOwnerInetAddress = info.groupOwnerAddress;
            RobotLog.d("Group owners address: " + this.wifiDirectAssistant.GroupOwnerInetAddress);
            if (info.groupFormed && info.isGroupOwner) {
                RobotLog.d("Wifi Direct group formed, this device is the group owner (GO)");
                this.wifiDirectAssistant.connectStatus = ConnectStatus.GROUP_OWNER;
                this.wifiDirectAssistant.setEvent(Event.CONNECTED_AS_GROUP_OWNER);
            } else if (info.groupFormed) {
                RobotLog.d("Wifi Direct group formed, this device is a client");
                this.wifiDirectAssistant.connectStatus = ConnectStatus.CONNECTED;
                this.wifiDirectAssistant.setEvent(Event.CONNECTED_AS_PEER);
            } else {
                RobotLog.d("Wifi Direct group NOT formed, ERROR: " + info);
                this.wifiDirectAssistant.failureReason = 0;
                this.wifiDirectAssistant.connectStatus = ConnectStatus.ERROR;
                this.wifiDirectAssistant.setEvent(Event.ERROR);
            }
        }
    }

    private class AssistantGroupInfoListener implements GroupInfoListener {
        final WifiDirectAssistant wifiDirectAssistant;

        private AssistantGroupInfoListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant = wifiDirectAssistant;
        }

        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                if (group.isGroupOwner()) {
                    this.wifiDirectAssistant.groupOwnerMacAddress = this.wifiDirectAssistant.deviceMacAddress;
                    this.wifiDirectAssistant.groupOwnerName = this.wifiDirectAssistant.deviceName;
                } else {
                    WifiP2pDevice owner = group.getOwner();
                    this.wifiDirectAssistant.groupOwnerMacAddress = owner.deviceAddress;
                    this.wifiDirectAssistant.groupOwnerName = owner.deviceName;
                }

                String passphrase = group.getPassphrase();
                if (passphrase == null) {
                    passphrase = "";
                }
                this.wifiDirectAssistant.passphrase = passphrase;

                RobotLog.v("Wifi Direct connection information available");
                this.wifiDirectAssistant.setEvent(Event.CONNECTION_INFO_AVAILABLE);
            }
        }
    }

    private class AssistantPeerListListener implements PeerListListener {
        final WifiDirectAssistant wifiDirectAssistant;

        private AssistantPeerListListener(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant = wifiDirectAssistant;
        }

        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            this.wifiDirectAssistant.wifiP2pDevices.clear();
            this.wifiDirectAssistant.wifiP2pDevices.addAll(peerList.getDeviceList());
            RobotLog.v("Wifi Direct peers found: " + this.wifiDirectAssistant.wifiP2pDevices.size());
            for (WifiP2pDevice wifiP2pDevice : this.wifiDirectAssistant.wifiP2pDevices) {
                RobotLog.v("    peer: " + wifiP2pDevice.deviceAddress + " " + wifiP2pDevice.deviceName);
            }
            this.wifiDirectAssistant.setEvent(Event.PEERS_AVAILABLE);
        }
    }

    private class AssistantBroadcastReceiver extends BroadcastReceiver {
        final WifiDirectAssistant wifiDirectAssistant;

        private AssistantBroadcastReceiver(WifiDirectAssistant wifiDirectAssistant) {
            this.wifiDirectAssistant = wifiDirectAssistant;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                this.wifiDirectAssistant.isWifiP2pEnabled = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1) == 2;
                RobotLog.d("Wifi Direct state - enabled: " + this.wifiDirectAssistant.isWifiP2pEnabled);
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                RobotLog.d("Wifi Direct peers changed");
                this.wifiDirectAssistant.wifiP2pManager.requestPeers(this.wifiDirectAssistant.p2pChannel, this.wifiDirectAssistant.peerListListener);
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                RobotLog.d("Wifi Direct connection changed - connected: " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    this.wifiDirectAssistant.wifiP2pManager.requestConnectionInfo(this.wifiDirectAssistant.p2pChannel, this.wifiDirectAssistant.connectionInfoListener);
                    this.wifiDirectAssistant.wifiP2pManager.stopPeerDiscovery(this.wifiDirectAssistant.p2pChannel, null);
                    return;
                }
                this.wifiDirectAssistant.connectStatus = ConnectStatus.NOT_CONNECTED;
                if (!this.wifiDirectAssistant.groupFormed) {
                    this.wifiDirectAssistant.discoverPeers();
                }
                if (this.wifiDirectAssistant.isConnected()) {
                    this.wifiDirectAssistant.setEvent(Event.DISCONNECTED);
                }
                this.wifiDirectAssistant.groupFormed = wifiP2pInfo.groupFormed;
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                RobotLog.d("Wifi Direct this device changed");
                this.wifiDirectAssistant.m244a((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
            }
        }
    }

    public static synchronized WifiDirectAssistant getWifiDirectAssistant(Context context) {
        WifiDirectAssistant wifiDirectAssistant;
        synchronized (WifiDirectAssistant.class) {
            if (WifiDirectAssistant.wifiDirectAssistant == null) {
                WifiDirectAssistant.wifiDirectAssistant = new WifiDirectAssistant(context);
            }
            wifiDirectAssistant = WifiDirectAssistant.wifiDirectAssistant;
        }
        return wifiDirectAssistant;
    }

    private WifiDirectAssistant(Context context) {
        this.context = context;
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        this.wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.p2pChannel = this.wifiP2pManager.initialize(context, Looper.getMainLooper(), null);
    }

    public synchronized void enable() {
        this.wifiDirectAssistantClientsNum++;
        RobotLog.v("There are " + this.wifiDirectAssistantClientsNum + " Wifi Direct Assistant Clients (+)");
        if (this.wifiDirectAssistantClientsNum == 1) {
            RobotLog.v("Enabling Wifi Direct Assistant");
            if (this.assistantBroadcastReceiver == null) {
                this.assistantBroadcastReceiver = new AssistantBroadcastReceiver(this);
            }
            this.context.registerReceiver(this.assistantBroadcastReceiver, this.intentFilter);
        }
    }

    public synchronized void disable() {
        this.wifiDirectAssistantClientsNum--;
        RobotLog.v("There are " + this.wifiDirectAssistantClientsNum + " Wifi Direct Assistant Clients (-)");
        if (this.wifiDirectAssistantClientsNum == 0) {
            RobotLog.v("Disabling Wifi Direct Assistant");
            this.wifiP2pManager.stopPeerDiscovery(this.p2pChannel, null);
            this.wifiP2pManager.cancelConnect(this.p2pChannel, null);
            try {
                this.context.unregisterReceiver(this.assistantBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
            this.event = null;
        }
    }

    public synchronized boolean isEnabled() {
        return this.wifiDirectAssistantClientsNum > 0;
    }

    public ConnectStatus getConnectStatus() {
        return this.connectStatus;
    }

    public List<WifiP2pDevice> getPeers() {
        return new ArrayList<WifiP2pDevice>(this.wifiP2pDevices);
    }

    public WifiDirectAssistantCallback getCallback() {
        return this.wifiDirectAssistantCallback;
    }

    public void setCallback(WifiDirectAssistantCallback callback) {
        this.wifiDirectAssistantCallback = callback;
    }

    public String getDeviceMacAddress() {
        return this.deviceMacAddress;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public InetAddress getGroupOwnerAddress() {
        return this.GroupOwnerInetAddress;
    }

    public String getGroupOwnerMacAddress() {
        return this.groupOwnerMacAddress;
    }

    public String getGroupOwnerName() {
        return this.groupOwnerName;
    }

    public String getPassphrase() {
        return this.passphrase;
    }

    public boolean isWifiP2pEnabled() {
        return this.isWifiP2pEnabled;
    }

    public boolean isConnected() {
        return (this.connectStatus == ConnectStatus.CONNECTED) ||
                (this.connectStatus == ConnectStatus.GROUP_OWNER);
    }

    public boolean isGroupOwner() {
        return this.connectStatus == ConnectStatus.GROUP_OWNER;
    }

    public void discoverPeers() {
        this.wifiP2pManager.discoverPeers(this.p2pChannel, new DiscoverActionListener(this));
    }

    public void cancelDiscoverPeers() {
        RobotLog.d("Wifi Direct stop discovering peers");
        this.wifiP2pManager.stopPeerDiscovery(this.p2pChannel, null);
    }

    public void createGroup() {
        this.wifiP2pManager.createGroup(this.p2pChannel, new GroupActionListener(this));
    }

    public void removeGroup() {
        this.wifiP2pManager.removeGroup(this.p2pChannel, null);
    }

    public void connect(WifiP2pDevice peer) {
        if ((this.connectStatus == ConnectStatus.CONNECTING) ||
                (this.connectStatus == ConnectStatus.CONNECTED)) {
            RobotLog.d("WifiDirect connection request to " + peer.deviceAddress + " ignored, already connected");
            return;
        }
        RobotLog.d("WifiDirect connecting to " + peer.deviceAddress);
        this.connectStatus = ConnectStatus.CONNECTING;
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = peer.deviceAddress;
        wifiP2pConfig.wps.setup = 0;
        wifiP2pConfig.groupOwnerIntent = 1;
        this.wifiP2pManager.connect(this.p2pChannel, wifiP2pConfig, new ConnectActionListener(this));
    }

    private void m244a(WifiP2pDevice wifiP2pDevice) {
        this.deviceName = wifiP2pDevice.deviceName;
        this.deviceMacAddress = wifiP2pDevice.deviceAddress;
        RobotLog.v("Wifi Direct device information: " + this.deviceName + " " + this.deviceMacAddress);
    }

    public String getFailureReason() {
        return failureReasonToString(this.failureReason);
    }

    public static String failureReasonToString(int reason) {
        switch (reason) {
            case WifiP2pManager.ERROR:
                return "ERROR";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "P2P_UNSUPPORTED";
            case WifiP2pManager.BUSY:
                return "BUSY";
            default:
                return "UNKNOWN (reason " + reason + ")";
        }
    }

    private void setEvent(Event event) {
        if ((this.event != event) || (this.event == Event.PEERS_AVAILABLE)) {
            this.event = event;
            if (this.wifiDirectAssistantCallback != null) {
                this.wifiDirectAssistantCallback.onWifiDirectEvent(event);
            }
        }
    }
}
