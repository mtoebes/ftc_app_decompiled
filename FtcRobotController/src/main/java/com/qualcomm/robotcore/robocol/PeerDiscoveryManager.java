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
    private InetAddress address;
    private final RobocolDatagramSocket socket;
    private ScheduledExecutorService service;
    private ScheduledFuture<?> future;
    private final PeerDiscovery message = new PeerDiscovery(PeerType.PEER);

    private class PeerDiscoveryRunnable implements Runnable {
        final PeerDiscoveryManager peerDiscoveryManager;

        private PeerDiscoveryRunnable(PeerDiscoveryManager peerDiscoveryManager) {
            this.peerDiscoveryManager = peerDiscoveryManager;
        }

        public void run() {
            try {
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram robocolDatagram = new RobocolDatagram(new PeerDiscovery(PeerType.PEER));
                if (this.peerDiscoveryManager.socket.getInetAddress() == null) {
                    robocolDatagram.setAddress(this.peerDiscoveryManager.address);
                }
                this.peerDiscoveryManager.socket.send(robocolDatagram);
            } catch (RobotCoreException e) {
                RobotLog.d("Unable to send peer discovery packet: " + e.toString());
            }
        }
    }

    public PeerDiscoveryManager(RobocolDatagramSocket socket) {
        this.socket = socket;
    }

    public InetAddress getPeerDiscoveryDevice() {
        return this.address;
    }

    public void start(InetAddress peerDiscoveryDevice) {
        RobotLog.v("Starting peer discovery");
        if (peerDiscoveryDevice == this.socket.getLocalAddress()) {
            RobotLog.v("No need for peer discovery, we are the peer discovery device");
            return;
        }
        if (this.future != null) {
            this.future.cancel(true);
        }
        this.address = peerDiscoveryDevice;
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.future = this.service.scheduleAtFixedRate(new PeerDiscoveryRunnable(this), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        RobotLog.v("Stopping peer discovery");
        if (this.future != null) {
            this.future.cancel(true);
        }
    }
}
