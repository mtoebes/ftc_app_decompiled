package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.util.RobotLog;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;

public class RobocolDatagramSocket {
    private final byte[] f330a;
    private DatagramSocket f331b;
    private final DatagramPacket f332c;
    private final RobocolDatagram f333d;
    private volatile State f334e;

    public enum State {
        LISTENING,
        CLOSED,
        ERROR
    }

    public RobocolDatagramSocket() {
        this.f330a = new byte[RobocolConfig.MAX_PACKET_SIZE];
        this.f332c = new DatagramPacket(this.f330a, this.f330a.length);
        this.f333d = new RobocolDatagram();
        this.f334e = State.CLOSED;
    }

    public void listen(InetAddress destAddress) throws SocketException {
        bind(new InetSocketAddress(RobocolConfig.determineBindAddress(destAddress), RobocolConfig.PORT_NUMBER));
    }

    public void bind(InetSocketAddress bindAddress) throws SocketException {
        if (this.f334e != State.CLOSED) {
            close();
        }
        this.f334e = State.LISTENING;
        RobotLog.d("RobocolDatagramSocket binding to " + bindAddress.toString());
        this.f331b = new DatagramSocket(bindAddress);
    }

    public void connect(InetAddress connectAddress) throws SocketException {
        SocketAddress inetSocketAddress = new InetSocketAddress(connectAddress, RobocolConfig.PORT_NUMBER);
        RobotLog.d("RobocolDatagramSocket connected to " + inetSocketAddress.toString());
        this.f331b.connect(inetSocketAddress);
    }

    public void close() {
        this.f334e = State.CLOSED;
        if (this.f331b != null) {
            this.f331b.close();
        }
        RobotLog.d("RobocolDatagramSocket is closed");
    }

    public void send(RobocolDatagram message) {
        try {
            this.f331b.send(message.getPacket());
        } catch (IllegalArgumentException e) {
            RobotLog.w("Unable to send RobocolDatagram: " + e.toString());
            RobotLog.w("               " + message.toString());
        } catch (IOException e2) {
            RobotLog.w("Unable to send RobocolDatagram: " + e2.toString());
            RobotLog.w("               " + message.toString());
        } catch (NullPointerException e3) {
            RobotLog.w("Unable to send RobocolDatagram: " + e3.toString());
            RobotLog.w("               " + message.toString());
        }
    }

    public RobocolDatagram recv() {
        try {
            this.f331b.receive(this.f332c);
        } catch (PortUnreachableException e) {
            RobotLog.d("RobocolDatagramSocket receive error: remote port unreachable");
            return null;
        } catch (IOException e2) {
            RobotLog.d("RobocolDatagramSocket receive error: " + e2.toString());
            return null;
        } catch (NullPointerException e3) {
            RobotLog.d("RobocolDatagramSocket receive error: " + e3.toString());
        }
        this.f333d.setPacket(this.f332c);
        return this.f333d;
    }

    public State getState() {
        return this.f334e;
    }

    public InetAddress getInetAddress() {
        if (this.f331b == null) {
            return null;
        }
        return this.f331b.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        if (this.f331b == null) {
            return null;
        }
        return this.f331b.getLocalAddress();
    }

    public boolean isRunning() {
        return this.f334e == State.LISTENING;
    }

    public boolean isClosed() {
        return this.f334e == State.CLOSED;
    }
}
