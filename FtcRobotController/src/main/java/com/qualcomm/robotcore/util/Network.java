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

package com.qualcomm.robotcore.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Network {
    public static InetAddress getLoopbackAddress() {
        try {
            return InetAddress.getByAddress(new byte[]{Byte.MAX_VALUE, (byte) 0, (byte) 0, (byte) 1});
        } catch (UnknownHostException ignored) {
            return null;
        }
    }

    public static ArrayList<InetAddress> getLocalIpAddresses() {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        try {

            ArrayList<NetworkInterface> NetworkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : NetworkInterfaces) {
                arrayList.addAll(Collections.list(networkInterface.getInetAddresses()));
            }
        } catch (SocketException ignored) {
        }
        return arrayList;
    }

    public static ArrayList<InetAddress> getLocalIpAddress(String networkInterface) {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        try {
            ArrayList<NetworkInterface> NetworkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface currentNetworkInterface : NetworkInterfaces) {
                if (currentNetworkInterface.getName().equals(networkInterface)) {
                    arrayList.addAll(Collections.list(currentNetworkInterface.getInetAddresses()));
                }
            }
        } catch (SocketException ignored) {
        }
        return arrayList;
    }

    public static ArrayList<InetAddress> removeIPv6Addresses(Collection<InetAddress> addresses) {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        for (InetAddress inetAddress : addresses) {
            if (inetAddress instanceof Inet4Address) {
                arrayList.add(inetAddress);
            }
        }
        return arrayList;
    }

    public static ArrayList<InetAddress> removeIPv4Addresses(Collection<InetAddress> addresses) {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        for (InetAddress inetAddress : addresses) {
            if (inetAddress instanceof Inet6Address) {
                arrayList.add(inetAddress);
            }
        }
        return arrayList;
    }

    public static ArrayList<InetAddress> removeLoopbackAddresses(Collection<InetAddress> addresses) {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        for (InetAddress inetAddress : addresses) {
            if (!inetAddress.isLoopbackAddress()) {
                arrayList.add(inetAddress);
            }
        }
        return arrayList;
    }

    public static ArrayList<String> getHostAddresses(Collection<InetAddress> addresses) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (InetAddress hostAddress : addresses) {
            String hostAddress2 = hostAddress.getHostAddress();
            if (hostAddress2.contains("%")) {
                hostAddress2 = hostAddress2.substring(0, hostAddress2.indexOf(37));
            }
            arrayList.add(hostAddress2);
        }
        return arrayList;
    }
}
