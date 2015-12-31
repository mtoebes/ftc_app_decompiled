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

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class RobocolDatagram {
    private DatagramPacket packet;

    public RobocolDatagram(RobocolParsable message) throws RobotCoreException {
        setData(message.toByteArray());
    }

    public RobocolDatagram(byte[] message) {
        setData(message);
    }

    protected RobocolDatagram(DatagramPacket packet) {
        this.packet = packet;
    }

    protected RobocolDatagram() {
    }

    public MsgType getMsgType() {
        return MsgType.fromByte(this.packet.getData()[0]);
    }

    public int getLength() {
        return this.packet.getLength();
    }

    public int getPayloadLength() {
        return this.packet.getLength() - RobocolParsable.HEADER_LENGTH;
    }

    public byte[] getData() {
        return this.packet.getData();
    }

    public void setData(byte[] data) {
        this.packet = new DatagramPacket(data, data.length);
    }

    public InetAddress getAddress() {
        return this.packet.getAddress();
    }

    public void setAddress(InetAddress address) {
        this.packet.setAddress(address);
    }

    public String toString() {
        int length;
        String messageType = "NONE";
        String hostAddress = null;
        if ((this.packet == null) || (this.packet.getAddress() == null) || (this.packet.getLength() <= 0)) {
            length = 0;
        } else {
            messageType = MsgType.fromByte(this.packet.getData()[0]).name();
            length = this.packet.getLength();
            hostAddress = this.packet.getAddress().getHostAddress();
        }
        return String.format("RobocolDatagram - type:%s, addr:%s, size:%d", messageType, hostAddress, length);
    }

    protected DatagramPacket getPacket() {
        return this.packet;
    }

    protected void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }
}
