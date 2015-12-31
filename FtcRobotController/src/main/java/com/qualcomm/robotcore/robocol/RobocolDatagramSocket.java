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
    private DatagramSocket socket;
    private final DatagramPacket packet;
    private final RobocolDatagram datagram = new RobocolDatagram();
    private volatile State state = State.CLOSED;

    public enum State {
        LISTENING,
        CLOSED,
        ERROR
    }

    public RobocolDatagramSocket() {
        byte[] buffer = new byte[RobocolConfig.MAX_PACKET_SIZE];
        this.packet = new DatagramPacket(buffer, buffer.length);
    }

    public void listen(InetAddress destAddress) throws SocketException {
        bind(new InetSocketAddress(RobocolConfig.determineBindAddress(destAddress), RobocolConfig.PORT_NUMBER));
    }

    public void bind(InetSocketAddress bindAddress) throws SocketException {
        if (this.state != State.CLOSED) {
            close();
        }
        this.state = State.LISTENING;
        RobotLog.d("RobocolDatagramSocket binding to " + bindAddress.toString());
        this.socket = new DatagramSocket(bindAddress);
    }

    public void connect(InetAddress connectAddress) throws SocketException {
        SocketAddress inetSocketAddress = new InetSocketAddress(connectAddress, RobocolConfig.PORT_NUMBER);
        RobotLog.d("RobocolDatagramSocket connected to " + inetSocketAddress.toString());
        this.socket.connect(inetSocketAddress);
    }

    public void close() {
        this.state = State.CLOSED;
        if (this.socket != null) {
            this.socket.close();
        }
        RobotLog.d("RobocolDatagramSocket is closed");
    }

    public void send(RobocolDatagram message) {
        try {
            this.socket.send(message.getPacket());
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
            this.socket.receive(this.packet);
        } catch (PortUnreachableException e) {
            RobotLog.d("RobocolDatagramSocket receive error: remote port unreachable");
            return null;
        } catch (IOException e2) {
            RobotLog.d("RobocolDatagramSocket receive error: " + e2.toString());
            return null;
        } catch (NullPointerException e3) {
            RobotLog.d("RobocolDatagramSocket receive error: " + e3.toString());
        }
        this.datagram.setPacket(this.packet);
        return this.datagram;
    }

    public State getState() {
        return this.state;
    }

    public InetAddress getInetAddress() {
        if (this.socket == null) {
            return null;
        }
        return this.socket.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        if (this.socket == null) {
            return null;
        }
        return this.socket.getLocalAddress();
    }

    public boolean isRunning() {
        return this.state == State.LISTENING;
    }

    public boolean isClosed() {
        return this.state == State.CLOSED;
    }
}
