package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.PeerDiscovery.PeerType;
import com.qualcomm.robotcore.util.RobotLog;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PeerDiscoveryManager {
    private InetAddress f323a;
    private final RobocolDatagramSocket f324b;
    private ScheduledExecutorService f325c;
    private ScheduledFuture<?> f326d;
    private final PeerDiscovery f327e;

    /* renamed from: com.qualcomm.robotcore.robocol.PeerDiscoveryManager.a */
    private class C0040a implements Runnable {
        final /* synthetic */ PeerDiscoveryManager f322a;

        private C0040a(PeerDiscoveryManager peerDiscoveryManager) {
            this.f322a = peerDiscoveryManager;
        }

        public void run() {
            try {
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram robocolDatagram = new RobocolDatagram(this.f322a.f327e);
                if (this.f322a.f324b.getInetAddress() == null) {
                    robocolDatagram.setAddress(this.f322a.f323a);
                }
                this.f322a.f324b.send(robocolDatagram);
            } catch (RobotCoreException e) {
                RobotLog.d("Unable to send peer discovery packet: " + e.toString());
            }
        }
    }

    public PeerDiscoveryManager(RobocolDatagramSocket socket) {
        this.f327e = new PeerDiscovery(PeerType.PEER);
        this.f324b = socket;
    }

    public InetAddress getPeerDiscoveryDevice() {
        return this.f323a;
    }

    public void start(InetAddress peerDiscoveryDevice) {
        RobotLog.v("Starting peer discovery");
        if (peerDiscoveryDevice == this.f324b.getLocalAddress()) {
            RobotLog.v("No need for peer discovery, we are the peer discovery device");
            return;
        }
        if (this.f326d != null) {
            this.f326d.cancel(true);
        }
        this.f323a = peerDiscoveryDevice;
        this.f325c = Executors.newSingleThreadScheduledExecutor();
        this.f326d = this.f325c.scheduleAtFixedRate(new C0040a(this), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        RobotLog.v("Stopping peer discovery");
        if (this.f326d != null) {
            this.f326d.cancel(true);
        }
    }
}
