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

import com.qualcomm.robotcore.util.Network;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class RobocolConfig {
    public static final int MAX_PACKET_SIZE = 4098;
    public static final int PORT_NUMBER = 20884;
    public static final int TIMEOUT = 1000;
    public static final int TTL = 3;
    public static final int WIFI_P2P_SUBNET_MASK = -256;

    public static InetAddress determineBindAddress(InetAddress destAddress) {
        ArrayList<InetAddress> removeIPv6Addresses = Network.removeIPv6Addresses(Network.removeLoopbackAddresses(Network.getLocalIpAddresses()));
        for (InetAddress removeIPv6Address : removeIPv6Addresses) {
            InetAddress inetAddress = null;
            try {
                Enumeration inetAddresses = NetworkInterface.getByInetAddress(removeIPv6Address).getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = (InetAddress) inetAddresses.nextElement();
                    if (inetAddress.equals(destAddress)) {
                        return inetAddress;
                    }
                }
            } catch (SocketException e) {
                if (inetAddress != null) {
                    RobotLog.v(String.format("socket exception while trying to get network interface of %s", inetAddress.getHostAddress()));
                } else {
                    RobotLog.v("exception while trying to get remote address");
                }
            }
        }
        return determineBindAddressBasedOnWifiP2pSubnet(removeIPv6Addresses, destAddress);
    }

    public static InetAddress determineBindAddressBasedOnWifiP2pSubnet(ArrayList<InetAddress> localIpAddresses, InetAddress destAddress) {
        int byteArrayToInt = TypeConversion.byteArrayToInt(destAddress.getAddress());
        for (InetAddress inetAddress : localIpAddresses) {
            if ((TypeConversion.byteArrayToInt(inetAddress.getAddress()) & WIFI_P2P_SUBNET_MASK) == (byteArrayToInt & WIFI_P2P_SUBNET_MASK)) {
                return inetAddress;
            }
        }
        return Network.getLoopbackAddress();
    }

    public static InetAddress determineBindAddressBasedOnIsReachable(ArrayList<InetAddress> localIpAddresses, InetAddress destAddress) {
        for (InetAddress inetAddress : localIpAddresses) {
            try {
                if (inetAddress.isReachable(NetworkInterface.getByInetAddress(inetAddress), TTL, TIMEOUT)) {
                    return inetAddress;
                }
            } catch (SocketException e) {
                RobotLog.v(String.format("socket exception while trying to get network interface of %s", inetAddress.getHostAddress()));
            } catch (IOException e2) {
                RobotLog.v(String.format("IO exception while trying to determine if %s is reachable via %s", destAddress.getHostAddress(), inetAddress.getHostAddress()));
            }
        }
        return Network.getLoopbackAddress();
    }
}
