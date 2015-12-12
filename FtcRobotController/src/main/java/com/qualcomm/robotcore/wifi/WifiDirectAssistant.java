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
    private static WifiDirectAssistant f441a;
    private final List<WifiP2pDevice> f442b;
    private Context f443c;
    private boolean f444d;
    private final IntentFilter f445e;
    private final Channel f446f;
    private final WifiP2pManager f447g;
    private C0062d f448h;
    private final C0059a f449i;
    private final C0061c f450j;
    private final C0060b f451k;
    private int f452l;
    private ConnectStatus f453m;
    private Event f454n;
    private String f455o;
    private String f456p;
    private InetAddress f457q;
    private String f458r;
    private String f459s;
    private String f460t;
    private boolean f461u;
    private int f462v;
    private WifiDirectAssistantCallback f463w;

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.1 */
    class C00561 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f432a;

        C00561(WifiDirectAssistant wifiDirectAssistant) {
            this.f432a = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.f432a.m245a(Event.DISCOVERING_PEERS);
            RobotLog.d("Wifi Direct discovering peers");
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f432a.f452l = reason;
            RobotLog.w("Wifi Direct failure while trying to discover peers - reason: " + failureReasonToString);
            this.f432a.m245a(Event.ERROR);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.2 */
    class C00572 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f433a;

        C00572(WifiDirectAssistant wifiDirectAssistant) {
            this.f433a = wifiDirectAssistant;
        }

        public void onSuccess() {
            this.f433a.f453m = ConnectStatus.GROUP_OWNER;
            this.f433a.m245a(Event.GROUP_CREATED);
            RobotLog.d("Wifi Direct created group");
        }

        public void onFailure(int reason) {
            if (reason == 2) {
                RobotLog.d("Wifi Direct cannot create group, does group already exist?");
                return;
            }
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f433a.f452l = reason;
            RobotLog.w("Wifi Direct failure while trying to create group - reason: " + failureReasonToString);
            this.f433a.f453m = ConnectStatus.ERROR;
            this.f433a.m245a(Event.ERROR);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.3 */
    class C00583 implements ActionListener {
        final /* synthetic */ WifiDirectAssistant f434a;

        C00583(WifiDirectAssistant wifiDirectAssistant) {
            this.f434a = wifiDirectAssistant;
        }

        public void onSuccess() {
            RobotLog.d("WifiDirect connect started");
            this.f434a.m245a(Event.CONNECTING);
        }

        public void onFailure(int reason) {
            String failureReasonToString = WifiDirectAssistant.failureReasonToString(reason);
            this.f434a.f452l = reason;
            RobotLog.d("WifiDirect connect cannot start - reason: " + failureReasonToString);
            this.f434a.m245a(Event.ERROR);
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
    private class C0059a implements ConnectionInfoListener {
        final /* synthetic */ WifiDirectAssistant f437a;

        private C0059a(WifiDirectAssistant wifiDirectAssistant) {
            this.f437a = wifiDirectAssistant;
        }

        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            this.f437a.f447g.requestGroupInfo(this.f437a.f446f, this.f437a.f451k);
            this.f437a.f457q = info.groupOwnerAddress;
            RobotLog.d("Group owners address: " + this.f437a.f457q.toString());
            if (info.groupFormed && info.isGroupOwner) {
                RobotLog.d("Wifi Direct group formed, this device is the group owner (GO)");
                this.f437a.f453m = ConnectStatus.GROUP_OWNER;
                this.f437a.m245a(Event.CONNECTED_AS_GROUP_OWNER);
            } else if (info.groupFormed) {
                RobotLog.d("Wifi Direct group formed, this device is a client");
                this.f437a.f453m = ConnectStatus.CONNECTED;
                this.f437a.m245a(Event.CONNECTED_AS_PEER);
            } else {
                RobotLog.d("Wifi Direct group NOT formed, ERROR: " + info.toString());
                this.f437a.f452l = 0;
                this.f437a.f453m = ConnectStatus.ERROR;
                this.f437a.m245a(Event.ERROR);
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.b */
    private class C0060b implements GroupInfoListener {
        final /* synthetic */ WifiDirectAssistant f438a;

        private C0060b(WifiDirectAssistant wifiDirectAssistant) {
            this.f438a = wifiDirectAssistant;
        }

        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                if (group.isGroupOwner()) {
                    this.f438a.f458r = this.f438a.f455o;
                    this.f438a.f459s = this.f438a.f456p;
                } else {
                    WifiP2pDevice owner = group.getOwner();
                    this.f438a.f458r = owner.deviceAddress;
                    this.f438a.f459s = owner.deviceName;
                }
                this.f438a.f460t = group.getPassphrase();
                this.f438a.f460t = this.f438a.f460t != null ? this.f438a.f460t : BuildConfig.VERSION_NAME;
                RobotLog.v("Wifi Direct connection information available");
                this.f438a.m245a(Event.CONNECTION_INFO_AVAILABLE);
            }
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.c */
    private class C0061c implements PeerListListener {
        final /* synthetic */ WifiDirectAssistant f439a;

        private C0061c(WifiDirectAssistant wifiDirectAssistant) {
            this.f439a = wifiDirectAssistant;
        }

        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            this.f439a.f442b.clear();
            this.f439a.f442b.addAll(peerList.getDeviceList());
            RobotLog.v("Wifi Direct peers found: " + this.f439a.f442b.size());
            for (WifiP2pDevice wifiP2pDevice : this.f439a.f442b) {
                RobotLog.v("    peer: " + wifiP2pDevice.deviceAddress + " " + wifiP2pDevice.deviceName);
            }
            this.f439a.m245a(Event.PEERS_AVAILABLE);
        }
    }

    /* renamed from: com.qualcomm.robotcore.wifi.WifiDirectAssistant.d */
    private class C0062d extends BroadcastReceiver {
        final /* synthetic */ WifiDirectAssistant f440a;

        private C0062d(WifiDirectAssistant wifiDirectAssistant) {
            this.f440a = wifiDirectAssistant;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
                this.f440a.f444d = intent.getIntExtra("wifi_p2p_state", -1) == 2;
                RobotLog.d("Wifi Direct state - enabled: " + this.f440a.f444d);
            } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
                RobotLog.d("Wifi Direct peers changed");
                this.f440a.f447g.requestPeers(this.f440a.f446f, this.f440a.f450j);
            } else if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
                RobotLog.d("Wifi Direct connection changed - connected: " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    this.f440a.f447g.requestConnectionInfo(this.f440a.f446f, this.f440a.f449i);
                    this.f440a.f447g.stopPeerDiscovery(this.f440a.f446f, null);
                    return;
                }
                this.f440a.f453m = ConnectStatus.NOT_CONNECTED;
                if (!this.f440a.f461u) {
                    this.f440a.discoverPeers();
                }
                if (this.f440a.isConnected()) {
                    this.f440a.m245a(Event.DISCONNECTED);
                }
                this.f440a.f461u = wifiP2pInfo.groupFormed;
            } else if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
                RobotLog.d("Wifi Direct this device changed");
                this.f440a.m244a((WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice"));
            }
        }
    }

    static {
        f441a = null;
    }

    public static synchronized WifiDirectAssistant getWifiDirectAssistant(Context context) {
        WifiDirectAssistant wifiDirectAssistant;
        synchronized (WifiDirectAssistant.class) {
            if (f441a == null) {
                f441a = new WifiDirectAssistant(context);
            }
            wifiDirectAssistant = f441a;
        }
        return wifiDirectAssistant;
    }

    private WifiDirectAssistant(Context context) {
        this.f442b = new ArrayList();
        this.f443c = null;
        this.f444d = false;
        this.f452l = 0;
        this.f453m = ConnectStatus.NOT_CONNECTED;
        this.f454n = null;
        this.f455o = BuildConfig.VERSION_NAME;
        this.f456p = BuildConfig.VERSION_NAME;
        this.f457q = null;
        this.f458r = BuildConfig.VERSION_NAME;
        this.f459s = BuildConfig.VERSION_NAME;
        this.f460t = BuildConfig.VERSION_NAME;
        this.f461u = false;
        this.f462v = 0;
        this.f463w = null;
        this.f443c = context;
        this.f445e = new IntentFilter();
        this.f445e.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.f445e.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.f445e.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.f445e.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.f447g = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.f446f = this.f447g.initialize(context, Looper.getMainLooper(), null);
        this.f448h = new C0062d(this);
        this.f449i = new C0059a(this);
        this.f450j = new C0061c(this);
        this.f451k = new C0060b(this);
    }

    public synchronized void enable() {
        this.f462v++;
        RobotLog.v("There are " + this.f462v + " Wifi Direct Assistant Clients (+)");
        if (this.f462v == 1) {
            RobotLog.v("Enabling Wifi Direct Assistant");
            if (this.f448h == null) {
                this.f448h = new C0062d(this);
            }
            this.f443c.registerReceiver(this.f448h, this.f445e);
        }
    }

    public synchronized void disable() {
        this.f462v--;
        RobotLog.v("There are " + this.f462v + " Wifi Direct Assistant Clients (-)");
        if (this.f462v == 0) {
            RobotLog.v("Disabling Wifi Direct Assistant");
            this.f447g.stopPeerDiscovery(this.f446f, null);
            this.f447g.cancelConnect(this.f446f, null);
            try {
                this.f443c.unregisterReceiver(this.f448h);
            } catch (IllegalArgumentException e) {
            }
            this.f454n = null;
        }
    }

    public synchronized boolean isEnabled() {
        return this.f462v > 0;
    }

    public ConnectStatus getConnectStatus() {
        return this.f453m;
    }

    public List<WifiP2pDevice> getPeers() {
        return new ArrayList(this.f442b);
    }

    public WifiDirectAssistantCallback getCallback() {
        return this.f463w;
    }

    public void setCallback(WifiDirectAssistantCallback callback) {
        this.f463w = callback;
    }

    public String getDeviceMacAddress() {
        return this.f455o;
    }

    public String getDeviceName() {
        return this.f456p;
    }

    public InetAddress getGroupOwnerAddress() {
        return this.f457q;
    }

    public String getGroupOwnerMacAddress() {
        return this.f458r;
    }

    public String getGroupOwnerName() {
        return this.f459s;
    }

    public String getPassphrase() {
        return this.f460t;
    }

    public boolean isWifiP2pEnabled() {
        return this.f444d;
    }

    public boolean isConnected() {
        return this.f453m == ConnectStatus.CONNECTED || this.f453m == ConnectStatus.GROUP_OWNER;
    }

    public boolean isGroupOwner() {
        return this.f453m == ConnectStatus.GROUP_OWNER;
    }

    public void discoverPeers() {
        this.f447g.discoverPeers(this.f446f, new C00561(this));
    }

    public void cancelDiscoverPeers() {
        RobotLog.d("Wifi Direct stop discovering peers");
        this.f447g.stopPeerDiscovery(this.f446f, null);
    }

    public void createGroup() {
        this.f447g.createGroup(this.f446f, new C00572(this));
    }

    public void removeGroup() {
        this.f447g.removeGroup(this.f446f, null);
    }

    public void connect(WifiP2pDevice peer) {
        if (this.f453m == ConnectStatus.CONNECTING || this.f453m == ConnectStatus.CONNECTED) {
            RobotLog.d("WifiDirect connection request to " + peer.deviceAddress + " ignored, already connected");
            return;
        }
        RobotLog.d("WifiDirect connecting to " + peer.deviceAddress);
        this.f453m = ConnectStatus.CONNECTING;
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = peer.deviceAddress;
        wifiP2pConfig.wps.setup = 0;
        wifiP2pConfig.groupOwnerIntent = 1;
        this.f447g.connect(this.f446f, wifiP2pConfig, new C00583(this));
    }

    private void m244a(WifiP2pDevice wifiP2pDevice) {
        this.f456p = wifiP2pDevice.deviceName;
        this.f455o = wifiP2pDevice.deviceAddress;
        RobotLog.v("Wifi Direct device information: " + this.f456p + " " + this.f455o);
    }

    public String getFailureReason() {
        return failureReasonToString(this.f452l);
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

    private void m245a(Event event) {
        if (this.f454n != event || this.f454n == Event.PEERS_AVAILABLE) {
            this.f454n = event;
            if (this.f463w != null) {
                this.f463w.onWifiDirectEvent(event);
            }
        }
    }
}
