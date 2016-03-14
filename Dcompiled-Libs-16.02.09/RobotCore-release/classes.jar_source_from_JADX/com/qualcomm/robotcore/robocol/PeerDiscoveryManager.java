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
    private InetAddress f316a;
    private final RobocolDatagramSocket f317b;
    private ScheduledExecutorService f318c;
    private ScheduledFuture<?> f319d;
    private final PeerDiscovery f320e;

    /* renamed from: com.qualcomm.robotcore.robocol.PeerDiscoveryManager.a */
    private class C0045a implements Runnable {
        final /* synthetic */ PeerDiscoveryManager f315a;

        private C0045a(PeerDiscoveryManager peerDiscoveryManager) {
            this.f315a = peerDiscoveryManager;
        }

        public void run() {
            try {
                RobotLog.m255v("sending peer discovery packet(%d)", Integer.valueOf(this.f315a.f320e.getSequenceNumber()));
                RobocolDatagram robocolDatagram = new RobocolDatagram(this.f315a.f320e);
                if (this.f315a.f317b.getInetAddress() == null) {
                    robocolDatagram.setAddress(this.f315a.f316a);
                }
                this.f315a.f317b.send(robocolDatagram);
            } catch (RobotCoreException e) {
                RobotLog.m248d("Unable to send peer discovery packet: " + e.toString());
            }
        }
    }

    public PeerDiscoveryManager(RobocolDatagramSocket socket) {
        this.f320e = new PeerDiscovery(PeerType.PEER);
        this.f317b = socket;
    }

    public InetAddress getPeerDiscoveryDevice() {
        return this.f316a;
    }

    public void start(InetAddress peerDiscoveryDevice) {
        RobotLog.m254v("Starting peer discovery");
        if (peerDiscoveryDevice == this.f317b.getLocalAddress()) {
            RobotLog.m254v("No need for peer discovery, we are the peer discovery device");
            return;
        }
        if (this.f319d != null) {
            this.f319d.cancel(true);
        }
        this.f316a = peerDiscoveryDevice;
        this.f318c = Executors.newSingleThreadScheduledExecutor();
        this.f319d = this.f318c.scheduleAtFixedRate(new C0045a(), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        RobotLog.m254v("Stopping peer discovery");
        if (this.f319d != null) {
            this.f319d.cancel(true);
        }
    }
}
