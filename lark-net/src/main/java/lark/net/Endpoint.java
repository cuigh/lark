package lark.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * @author cuigh
 */
public class Endpoint {
    private String host;
    private int port;

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Endpoint parse(String address) {
        Objects.requireNonNull(address);

        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException();
        }

        int port = 0;
        if (!parts[1].isEmpty()) {
            port = Integer.parseInt(parts[1]);
        }
        return new Endpoint(parts[0], port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SocketAddress toSocketAddress() {
        return host.isEmpty() ? new InetSocketAddress(port) : new InetSocketAddress(host, port);

    }
}
