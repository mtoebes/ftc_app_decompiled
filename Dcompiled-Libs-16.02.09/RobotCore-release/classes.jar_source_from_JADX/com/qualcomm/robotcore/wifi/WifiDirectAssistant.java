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
import com.ftdi.j2xx.protocol.SpiSlaveResponseEvent;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.util.RobotLog;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectAssistant {
    private static WifiDirectAssistant f446a;
    private final List<WifiP2pDevice> f447b;
    private Context f448c;
    private boolean f449d;
    private final IntentFilter f450e;
    private final Channel f451f;
    private final WifiP2pManager f452g;
    private C0067d f453h;
    private final C0064a f454i;
    private final C0066c f455j;
    private final C0065b f456k;
    private int f457l;
    private ConnectStatus f458m;
    private Event f459n;
    private String f460o;
    private String f461p;
    private InetAddress f462q;
    private String f463r;
    private String f464s;
    private String f465t;
    private boolean f466u;
    private int f467v;
    private WifiDirectAssistantCallback f468w;

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.1 */
    class C00611 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f437a;

        C00611(WifiDirectAssistant wifiDirectAssistant) {
            this.f437a = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.f437a.m268a(Event.DISCOVERING_PEERS);
            RobotLog.m248d("Wifi Direct discovering peers");
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f437a.f457l = reason;
            RobotLog.m256w("Wifi Direct failure while trying to discover peers - reason: " + failureReasonToString);
            this.f437a.m268a(Event.ERROR);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.2 */
    class C00622 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f438a;

        C00622(WifiDirectAssistant wifiDirectAssistant) {
            this.f438a = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.f438a.f458m = ConnectStatus.GROUP_OWNER;
            this.f438a.m268a(Event.GROUP_CREATED);
            RobotLog.m248d("Wifi Direct created group");
        }

        public void onFailure(int reason) {
            if (reason == 2) {
                RobotLog.m248d("Wifi Direct cannot create group, does group already exist?");
                return;
            }
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f438a.f457l = reason;
            RobotLog.m256w("Wifi Direct failure while trying to create group - reason: " + failureReasonToString);
            this.f438a.f458m = ConnectStatus.ERROR;
            this.f438a.m268a(Event.ERROR);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.3 */
    class C00633 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f439a;

        C00633(WifiDirectAssistant wifiDirectAssistant) {
            this.f439a = wifiDirectAssistant;
        }

        public void onSuccess() {
            RobotLog.m248d("WifiDirect connect started");
            this.f439a.m268a(Event.CONNECTING);
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f439a.f457l = reason;
            RobotLog.m248d("WifiDirect connect cannot start - reason: " + failureReasonToString);
            this.f439a.m268a(Event.ERROR);
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

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.a */
    private class C0064a implements ConnectionInfoListener {
        final /* synthetic */ WifiDirectAssistant f442a;

        private C0064a(WifiDirectAssistant wifiDirectAssistant) {
            this.f442a = wifiDirectAssistant;
        }

        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            this.f442a.f452g.requestGroupInfo(this.f442a.f451f, this.f442a.f456k);
            this.f442a.f462q = info.groupOwnerAddress;
            RobotLog.m248d("Group owners address: " + this.f442a.f462q.toString());
            if (info.groupFormed && info.isGroupOwner) {
                RobotLog.m248d("Wifi Direct group formed, this device is the group owner (GO)");
                this.f442a.f458m = ConnectStatus.GROUP_OWNER;
                this.f442a.m268a(Event.CONNECTED_AS_GROUP_OWNER);
            } else if (info.groupFormed) {
                RobotLog.m248d("Wifi Direct group formed, this device is a client");
                this.f442a.f458m = ConnectStatus.CONNECTED;
                this.f442a.m268a(Event.CONNECTED_AS_PEER);
            } else {
                RobotLog.m248d("Wifi Direct group NOT formed, ERROR: " + info.toString());
                this.f442a.f457l = 0;
                this.f442a.f458m = ConnectStatus.ERROR;
                this.f442a.m268a(Event.ERROR);
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.b */
    private class C0065b implements GroupInfoListener {
        final /* synthetic */ WifiDirectAssistant f443a;

        private C0065b(WifiDirectAssistant wifiDirectAssistant) {
            this.f443a = wifiDirectAssistant;
        }

        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                if (group.isGroupOwner()) {
                    this.f443a.f463r = this.f443a.f460o;
                    this.f443a.f464s = this.f443a.f461p;
                } else {
                    WifiP2pDevice owner = group.getOwner();
                    this.f443a.f463r = owner.deviceAddress;
                    this.f443a.f464s = owner.deviceName;
                }
                this.f443a.f465t = group.getPassphrase();
                this.f443a.f465t = this.f443a.f465t != null ? this.f443a.f465t : BuildConfig.VERSION_NAME;
                RobotLog.m254v("Wifi Direct connection information available");
                this.f443a.m268a(Event.CONNECTION_INFO_AVAILABLE);
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.c */
    private class C0066c implements PeerListListener {
        final /* synthetic */ WifiDirectAssistant f444a;

        private C0066c(WifiDirectAssistant wifiDirectAssistant) {
            this.f444a = wifiDirectAssistant;
        }

        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            this.f444a.f447b.clear();
            this.f444a.f447b.addAll(peerList.getDeviceList());
            RobotLog.m254v("Wifi Direct peers found: " + this.f444a.f447b.size());
            for (WifiP2pDevice wifiP2pDevice : this.f444a.f447b) {
                RobotLog.m254v("    peer: " + wifiP2pDevice.deviceAddress + " " + wifiP2pDevice.deviceName);
            }
            this.f444a.m268a(Event.PEERS_AVAILABLE);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.d */
    private class C0067d extends BroadcastReceiver {
        final /* synthetic */ WifiDirectAssistant f445a;

        private C0067d(WifiDirectAssistant wifiDirectAssistant) {
            this.f445a = wifiDirectAssistant;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
                this.f445a.f449d = intent.getIntExtra("wifi_p2p_state", -1) == 2;
                RobotLog.m248d("Wifi Direct state - enabled: " + this.f445a.f449d);
            } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
                RobotLog.m248d("Wifi Direct peers changed");
                this.f445a.f452g.requestPeers(this.f445a.f451f, this.f445a.f455j);
            } else if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
                RobotLog.m248d("Wifi Direct connection changed - connected: " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    this.f445a.f452g.requestConnectionInfo(this.f445a.f451f, this.f445a.f454i);
                    this.f445a.f452g.stopPeerDiscovery(this.f445a.f451f, null);
                    return;
                }
                this.f445a.f458m = ConnectStatus.NOT_CONNECTED;
                if (!this.f445a.f466u) {
                    this.f445a.discoverPeers();
                }
                if (this.f445a.isConnected()) {
                    this.f445a.m268a(Event.DISCONNECTED);
                }
                this.f445a.f466u = wifiP2pInfo.groupFormed;
            } else if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
                RobotLog.m248d("Wifi Direct this device changed");
                this.f445a.m267a((WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice"));
            }
        }
    }

    static {
        f446a = null;
    }

    public static synchronized WifiDirectAssistant getWifiDirectAssistant(Context context) {
        WifiDirectAssistant wifiDirectAssistant;
        synchronized (WifiDirectAssistant.class) {
            if (f446a == null) {
                f446a = new WifiDirectAssistant(context);
            }
            wifiDirectAssistant = f446a;
        }
        return wifiDirectAssistant;
    }

    private WifiDirectAssistant(Context context) {
        this.f447b = new ArrayList();
        this.f448c = null;
        this.f449d = false;
        this.f457l = 0;
        this.f458m = ConnectStatus.NOT_CONNECTED;
        this.f459n = null;
        this.f460o = BuildConfig.VERSION_NAME;
        this.f461p = BuildConfig.VERSION_NAME;
        this.f462q = null;
        this.f463r = BuildConfig.VERSION_NAME;
        this.f464s = BuildConfig.VERSION_NAME;
        this.f465t = BuildConfig.VERSION_NAME;
        this.f466u = false;
        this.f467v = 0;
        this.f468w = null;
        this.f448c = context;
        this.f450e = new IntentFilter();
        this.f450e.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.f450e.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.f450e.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.f450e.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.f452g = (WifiP2pManager) context.getSystemService("wifip2p");
        this.f451f = this.f452g.initialize(context, Looper.getMainLooper(), null);
        this.f453h = new C0067d();
        this.f454i = new C0064a();
        this.f455j = new C0066c();
        this.f456k = new C0065b();
    }

    public synchronized void enable() {
        this.f467v++;
        RobotLog.m254v("There are " + this.f467v + " Wifi Direct Assistant Clients (+)");
        if (this.f467v == 1) {
            RobotLog.m254v("Enabling Wifi Direct Assistant");
            if (this.f453h == null) {
                this.f453h = new C0067d();
            }
            this.f448c.registerReceiver(this.f453h, this.f450e);
        }
    }

    public synchronized void disable() {
        this.f467v--;
        RobotLog.m254v("There are " + this.f467v + " Wifi Direct Assistant Clients (-)");
        if (this.f467v == 0) {
            RobotLog.m254v("Disabling Wifi Direct Assistant");
            this.f452g.stopPeerDiscovery(this.f451f, null);
            this.f452g.cancelConnect(this.f451f, null);
            try {
                this.f448c.unregisterReceiver(this.f453h);
            } catch (IllegalArgumentException e) {
            }
            this.f459n = null;
        }
    }

    public synchronized boolean isEnabled() {
        return this.f467v > 0;
    }

    public ConnectStatus getConnectStatus() {
        return this.f458m;
    }

    public List<WifiP2pDevice> getPeers() {
        return new ArrayList(this.f447b);
    }

    public WifiDirectAssistantCallback getCallback() {
        return this.f468w;
    }

    public void setCallback(WifiDirectAssistantCallback callback) {
        this.f468w = callback;
    }

    public String getDeviceMacAddress() {
        return this.f460o;
    }

    public String getDeviceName() {
        return this.f461p;
    }

    public InetAddress getGroupOwnerAddress() {
        return this.f462q;
    }

    public String getGroupOwnerMacAddress() {
        return this.f463r;
    }

    public String getGroupOwnerName() {
        return this.f464s;
    }

    public String getPassphrase() {
        return this.f465t;
    }

    public boolean isWifiP2pEnabled() {
        return this.f449d;
    }

    public boolean isConnected() {
        return this.f458m == ConnectStatus.CONNECTED || this.f458m == ConnectStatus.GROUP_OWNER;
    }

    public boolean isGroupOwner() {
        return this.f458m == ConnectStatus.GROUP_OWNER;
    }

    public boolean isDeviceNameValid() {
        return this.f461p.matches("^\\p{Graph}+$");
    }

    public void discoverPeers() {
        this.f452g.discoverPeers(this.f451f, new C00611(this));
    }

    public void cancelDiscoverPeers() {
        RobotLog.m248d("Wifi Direct stop discovering peers");
        this.f452g.stopPeerDiscovery(this.f451f, null);
    }

    public void createGroup() {
        this.f452g.createGroup(this.f451f, new C00622(this));
    }

    public void removeGroup() {
        this.f452g.removeGroup(this.f451f, null);
    }

    public void connect(WifiP2pDevice peer) {
        if (this.f458m == ConnectStatus.CONNECTING || this.f458m == ConnectStatus.CONNECTED) {
            RobotLog.m248d("WifiDirect connection request to " + peer.deviceAddress + " ignored, already connected");
            return;
        }
        RobotLog.m248d("WifiDirect connecting to " + peer.deviceAddress);
        this.f458m = ConnectStatus.CONNECTING;
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = peer.deviceAddress;
        wifiP2pConfig.wps.setup = 0;
        wifiP2pConfig.groupOwnerIntent = 1;
        this.f452g.connect(this.f451f, wifiP2pConfig, new C00633(this));
    }

    private void m267a(WifiP2pDevice wifiP2pDevice) {
        this.f461p = wifiP2pDevice.deviceName;
        this.f460o = wifiP2pDevice.deviceAddress;
        RobotLog.m254v("Wifi Direct device information: " + this.f461p + " " + this.f460o);
    }

    public String getFailureReason() {
        return failureReasonToString(this.f457l);
    }

    public static String failureReasonToString(int reason) {
        switch (reason) {
            case SpiSlaveResponseEvent.OK /*0*/:
                return "ERROR";
            case SpiSlaveResponseEvent.DATA_CORRUPTED /*1*/:
                return "P2P_UNSUPPORTED";
            case SpiSlaveResponseEvent.IO_ERROR /*2*/:
                return "BUSY";
            default:
                return "UNKNOWN (reason " + reason + ")";
        }
    }

    private void m268a(Event event) {
        if (this.f459n != event || this.f459n == Event.PEERS_AVAILABLE) {
            this.f459n = event;
            if (this.f468w != null) {
                this.f468w.onWifiDirectEvent(event);
            }
        }
    }
}
