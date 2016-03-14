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
import java.util.Iterator;

public class RobocolConfig {
    public static final int MAX_PACKET_SIZE = 4098;
    public static final int PORT_NUMBER = 20884;
    public static final int TIMEOUT = 1000;
    public static final int TTL = 3;
    public static final int WIFI_P2P_SUBNET_MASK = -256;

    public static InetAddress determineBindAddress(InetAddress destAddress) {
        ArrayList removeIPv6Addresses = Network.removeIPv6Addresses(Network.removeLoopbackAddresses(Network.getLocalIpAddresses()));
        Iterator it = removeIPv6Addresses.iterator();
        while (it.hasNext()) {
            try {
                Enumeration inetAddresses = NetworkInterface.getByInetAddress((InetAddress) it.next()).getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
                    if (inetAddress.equals(destAddress)) {
                        return inetAddress;
                    }
                }
                continue;
            } catch (SocketException e) {
                RobotLog.m233v(String.format("socket exception while trying to get network interface of %s", new Object[]{r0.getHostAddress()}));
            }
        }
        return determineBindAddressBasedOnWifiP2pSubnet(removeIPv6Addresses, destAddress);
    }

    public static InetAddress determineBindAddressBasedOnWifiP2pSubnet(ArrayList<InetAddress> localIpAddresses, InetAddress destAddress) {
        int byteArrayToInt = TypeConversion.byteArrayToInt(destAddress.getAddress());
        Iterator it = localIpAddresses.iterator();
        while (it.hasNext()) {
            InetAddress inetAddress = (InetAddress) it.next();
            if ((TypeConversion.byteArrayToInt(inetAddress.getAddress()) & WIFI_P2P_SUBNET_MASK) == (byteArrayToInt & WIFI_P2P_SUBNET_MASK)) {
                return inetAddress;
            }
        }
        return Network.getLoopbackAddress();
    }

    public static InetAddress determineBindAddressBasedOnIsReachable(ArrayList<InetAddress> localIpAddresses, InetAddress destAddress) {
        Iterator it = localIpAddresses.iterator();
        while (it.hasNext()) {
            InetAddress inetAddress = (InetAddress) it.next();
            try {
                if (inetAddress.isReachable(NetworkInterface.getByInetAddress(inetAddress), TTL, TIMEOUT)) {
                    return inetAddress;
                }
            } catch (SocketException e) {
                RobotLog.m233v(String.format("socket exception while trying to get network interface of %s", new Object[]{inetAddress.getHostAddress()}));
            } catch (IOException e2) {
                RobotLog.m233v(String.format("IO exception while trying to determine if %s is reachable via %s", new Object[]{destAddress.getHostAddress(), inetAddress.getHostAddress()}));
            }
        }
        return Network.getLoopbackAddress();
    }
}
