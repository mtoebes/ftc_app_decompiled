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
import com.qualcomm.robotcore.util.RobotLog;

import java.nio.ByteBuffer;

public class PeerDiscovery implements RobocolParsable {
    public static final short PAYLOAD_SIZE = (short) 10;
    public static final byte ROBOCOL_VERSION = (byte) 1;
    public static final short BUFFER_SIZE = PAYLOAD_SIZE + HEADER_LENGTH;

    private PeerType peerType;

    public enum PeerType {
        NOT_SET(0),
        PEER(1),
        GROUP_OWNER(2);

        private static final PeerType[] peerTypes = values();
        private final int type;

        public static PeerType fromByte(byte b) {
            try {
                return peerTypes[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to Peer: %s", b, e.toString()));
                return NOT_SET;
            }
        }

        PeerType(int type) {
            this.type = type;
        }

        public byte asByte() {
            return (byte) this.type;
        }
    }

    public PeerDiscovery(PeerType peerType) {
        this.peerType = peerType;
    }

    public PeerType getPeerType() {
        return this.peerType;
    }

    public MsgType getRobocolMsgType() {
        return MsgType.PEER_DISCOVERY;
    }

    public byte[] toByteArray() throws RobotCoreException {
        ByteBuffer allocate = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            allocate.put(getRobocolMsgType().asByte());
            allocate.putShort(PAYLOAD_SIZE);
            allocate.put(ROBOCOL_VERSION);
            allocate.put(this.peerType.asByte());
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
        }
        return allocate.array();
    }

    public void fromByteArray(byte[] byteArray) throws RobotCoreException {
        if (byteArray.length < BUFFER_SIZE) {
            throw new RobotCoreException("Expected buffer of at least 13 bytes, received " + byteArray.length);
        }
        ByteBuffer wrap = ByteBuffer.wrap(byteArray, HEADER_LENGTH, PAYLOAD_SIZE);

        if ((PeerType.fromByte(wrap.get())) == PeerType.PEER) {
            this.peerType = PeerType.fromByte(wrap.get());
        }
    }

    public String toString() {
        return String.format("Peer Discovery - peer type: %s", this.peerType.name());
    }
}
