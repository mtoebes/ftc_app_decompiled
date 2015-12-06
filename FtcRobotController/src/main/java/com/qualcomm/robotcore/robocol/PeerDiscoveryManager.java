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
    private ScheduledFuture<?> future;
    private final PeerDiscovery message;

    private class PeerDiscoveryRunnable implements Runnable {

        public void run() {
            try {
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram robocolDatagram = new RobocolDatagram(message);
                if (socket.getInetAddress() == null) {
                    robocolDatagram.setAddress(address);
                }
                socket.send(robocolDatagram);
            } catch (RobotCoreException e) {
                RobotLog.d("Unable to send peer discovery packet: " + e.toString());
            }
        }
    }

    public PeerDiscoveryManager(RobocolDatagramSocket socket) {
        message = new PeerDiscovery(PeerType.PEER);
        this.socket = socket;
    }

    public InetAddress getPeerDiscoveryDevice() {
        return address;
    }

    public void start(InetAddress peerDiscoveryDevice) {
        RobotLog.v("Starting peer discovery");
        if (peerDiscoveryDevice == socket.getLocalAddress()) {
            RobotLog.v("No need for peer discovery, we are the peer discovery device");
            return;
        }
        if (future != null) {
            future.cancel(true);
        }
        address = peerDiscoveryDevice;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        future = executorService.scheduleAtFixedRate(new PeerDiscoveryRunnable(), 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        RobotLog.v("Stopping peer discovery");
        if (future != null) {
            future.cancel(true);
        }
    }
}
