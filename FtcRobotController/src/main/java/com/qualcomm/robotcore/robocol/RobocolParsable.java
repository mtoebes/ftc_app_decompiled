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

import java.nio.charset.Charset;

public interface RobocolParsable {
    int HEADER_LENGTH = 3;
    byte[] EMPTY_HEADER_BUFFER = new byte[HEADER_LENGTH];

    Charset CHARSET = Charset.forName("UTF-8");

    enum MsgType {
        EMPTY(0),
        HEARTBEAT(1),
        GAMEPAD(2),
        PEER_DISCOVERY(RobocolParsable.HEADER_LENGTH),
        COMMAND(4),
        TELEMETRY(5);

        private static final MsgType[] MESSAGE_TYPES;
        private final int f337b;

        static {
            MESSAGE_TYPES = values();
        }

        public static MsgType fromByte(byte b) {
            MsgType msgType = EMPTY;
            try {
                return MESSAGE_TYPES[b];
            } catch (ArrayIndexOutOfBoundsException e) {
                RobotLog.w(String.format("Cannot convert %d to MsgType: %s", b, e.toString()));
                return msgType;
            }
        }

        MsgType(int type) {
            this.f337b = type;
        }

        public byte asByte() {
            return (byte) this.f337b;
        }
    }

    void fromByteArray(byte[] bArr) throws RobotCoreException;

    MsgType getRobocolMsgType();

    byte[] toByteArray() throws RobotCoreException;
}
