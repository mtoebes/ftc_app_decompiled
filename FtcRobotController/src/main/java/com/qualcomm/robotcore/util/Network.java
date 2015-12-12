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
import java.util.Iterator;

public class Network {
    public static InetAddress getLoopbackAddress() {
        try {
            return InetAddress.getByAddress(new byte[]{Byte.MAX_VALUE, (byte) 0, (byte) 0, (byte) 1});
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static ArrayList<InetAddress> getLocalIpAddresses() {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                arrayList.addAll(Collections.list(((NetworkInterface) it.next()).getInetAddresses()));
            }
        } catch (SocketException e) {
        }
        return arrayList;
    }

    public static ArrayList<InetAddress> getLocalIpAddress(String networkInterface) {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface2 = (NetworkInterface) it.next();
                if (networkInterface2.getName() == networkInterface) {
                    arrayList.addAll(Collections.list(networkInterface2.getInetAddresses()));
                }
            }
        } catch (SocketException e) {
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
