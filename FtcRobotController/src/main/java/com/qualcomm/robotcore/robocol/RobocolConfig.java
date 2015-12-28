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
