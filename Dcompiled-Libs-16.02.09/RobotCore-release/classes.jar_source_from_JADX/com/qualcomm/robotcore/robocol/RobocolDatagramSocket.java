package com.qualcomm.robotcore.robocol;

import com.qualcomm.robotcore.util.RobotLog;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;

public class RobocolDatagramSocket {
    private DatagramSocket f325a;
    private volatile State f326b;
    private final Object f327c;
    private final Object f328d;
    private final Object f329e;

    public enum State {
        LISTENING,
        CLOSED,
        ERROR
    }

    public RobocolDatagramSocket() {
        this.f327c = new Object();
        this.f328d = new Object();
        this.f329e = new Object();
        this.f326b = State.CLOSED;
    }

    public void listen(InetAddress destAddress) throws SocketException {
        bind(new InetSocketAddress(RobocolConfig.determineBindAddress(destAddress), RobocolConfig.PORT_NUMBER));
    }

    public void bind(InetSocketAddress bindAddress) throws SocketException {
        synchronized (this.f329e) {
            if (this.f326b != State.CLOSED) {
                close();
            }
            this.f326b = State.LISTENING;
            RobotLog.m248d("RobocolDatagramSocket binding to " + bindAddress.toString());
            this.f325a = new DatagramSocket(bindAddress);
        }
    }

    public void connect(InetAddress connectAddress) throws SocketException {
        SocketAddress inetSocketAddress = new InetSocketAddress(connectAddress, RobocolConfig.PORT_NUMBER);
        RobotLog.m248d("RobocolDatagramSocket connected to " + inetSocketAddress.toString());
        this.f325a.connect(inetSocketAddress);
    }

    public void close() {
        synchronized (this.f329e) {
            this.f326b = State.CLOSED;
            if (this.f325a != null) {
                this.f325a.close();
            }
            RobotLog.m248d("RobocolDatagramSocket is closed");
        }
    }

    public void send(RobocolDatagram message) {
        synchronized (this.f328d) {
            try {
                this.f325a.send(message.getPacket());
            } catch (IllegalArgumentException e) {
                RobotLog.m256w("Unable to send RobocolDatagram: " + e.toString());
                RobotLog.m256w("               " + message.toString());
            } catch (IOException e2) {
                RobotLog.m256w("Unable to send RobocolDatagram: " + e2.toString());
                RobotLog.m256w("               " + message.toString());
            } catch (NullPointerException e3) {
                RobotLog.m256w("Unable to send RobocolDatagram: " + e3.toString());
                RobotLog.m256w("               " + message.toString());
            }
        }
    }

    public RobocolDatagram recv() {
        RobocolDatagram forReceive;
        synchronized (this.f327c) {
            forReceive = RobocolDatagram.forReceive();
            try {
                this.f325a.receive(forReceive.getPacket());
            } catch (PortUnreachableException e) {
                RobotLog.m248d("RobocolDatagramSocket receive error: remote port unreachable");
                return null;
            } catch (IOException e2) {
                RobotLog.m248d("RobocolDatagramSocket receive error: " + e2.toString());
                return null;
            } catch (NullPointerException e3) {
                RobotLog.m248d("RobocolDatagramSocket receive error: " + e3.toString());
            }
        }
        return forReceive;
    }

    public State getState() {
        return this.f326b;
    }

    public InetAddress getInetAddress() {
        if (this.f325a == null) {
            return null;
        }
        return this.f325a.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        if (this.f325a == null) {
            return null;
        }
        return this.f325a.getLocalAddress();
    }

    public boolean isRunning() {
        return this.f326b == State.LISTENING;
    }

    public boolean isClosed() {
        return this.f326b == State.CLOSED;
    }
}
