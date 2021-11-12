package lark.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author cuigh
 */
public final class Networks {
    private static final String LOOPBACK_ADDRESS = "127.0.0.1";

    public static String getLocalIP4() {
        return findLocalIP4(null);
    }

    public static String findLocalIP4(String prefix) {
        Enumeration<NetworkInterface> nis;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw Exceptions.asRuntime(e);
        }

        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            String address = findValidAddress(ni);
            if (address == null) {
                continue;
            }
            if (Strings.isEmpty(prefix) || address.startsWith(prefix)) {
                return address;
            }
        }
        return LOOPBACK_ADDRESS;
    }

    private static String findValidAddress(NetworkInterface ni) {
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (isValid(address)) {
                return address.getHostAddress();
            }
        }
        return null;
    }

    private static boolean isValid(InetAddress address) {
        return (address instanceof Inet4Address) && !address.isAnyLocalAddress() && !address.isLoopbackAddress();
    }
}
