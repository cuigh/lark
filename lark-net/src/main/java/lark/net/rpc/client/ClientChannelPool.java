package lark.net.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import lark.net.Address;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.protocol.ClientProtocol;
import lark.net.rpc.protocol.proto.ProtoClientProtocol;
import lark.net.rpc.protocol.simple.SimpleClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;

/**
 * @author cuigh
 */
public class ClientChannelPool implements ChannelPool, ChannelFactory<ClientChannel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannelPool.class);
    private static final ConcurrentMap<Address, ClientChannelPool> POOLS = PlatformDependent.newConcurrentHashMap();
    private final FixedChannelPool pool;
    private final SocketAddress address;
    private final ClientProtocol protocol;

    private ClientChannelPool(Address address, ClientOptions options, ClientProtocol protocol) {
        this.protocol = protocol;

        URI uri = URI.create(address.getUrl());
        this.address = new InetSocketAddress(uri.getHost(), uri.getPort());

        Bootstrap client = createClient(options);
        pool = new FixedChannelPool(client,
                new ClientChannelPoolHandler(),
                ChannelHealthChecker.ACTIVE,
                FixedChannelPool.AcquireTimeoutAction.FAIL,
                10 * 1000,
                options.getChannels(),
                100);
    }

    public static ClientChannelPool get(Address address, ClientOptions options) {
        return POOLS.computeIfAbsent(address, a -> {
            ClientProtocol protocol;
            String type = StringUtils.isEmpty(options.getType()) ? "proto" : options.getType();
            switch (type) {
                case "simple":
                    protocol = new SimpleClientProtocol(options);
                    break;
                case "proto":
                    protocol = new ProtoClientProtocol(options);
                    break;
                default:
                    throw new RpcException(RpcError.CLIENT_UNKNOWN_PROTOCOL, options.getType());
            }
            return new ClientChannelPool(address, options, protocol);
        });
    }

    public static boolean close(Address address) {
        ClientChannelPool pool = POOLS.remove(address);
        if (pool != null) {
            pool.close();
            return true;
        }
        return false;
    }

    private Bootstrap createClient(ClientOptions options) {
        Bootstrap bs = new Bootstrap();

        bs.group(new NioEventLoopGroup());
        bs.channelFactory(this);
        bs.remoteAddress(address);

        // 设置传输设置
        bs.option(ChannelOption.SO_REUSEADDR, options.isReuseAddress());
        bs.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) options.getDialTimeout().toMillis());
        bs.option(ChannelOption.SO_SNDBUF, options.getSendBufferSize());
        bs.option(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
        bs.option(ChannelOption.SO_KEEPALIVE, true);
        bs.option(ChannelOption.TCP_NODELAY, true);
        bs.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, new DefaultMessageSizeEstimator(options.getReceiveBufferSize()));

        return bs;
    }

    @Override
    public Future<Channel> acquire() {
        return pool.acquire();
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        return pool.acquire(promise);
    }

    @Override
    public Future<Void> release(Channel channel) {
        return pool.release(channel);
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return pool.release(channel, promise);
    }

    @Override
    public void close() {
        pool.close();
    }

    @Override
    public ClientChannel newChannel() {
        return new ClientChannel();
    }

    class ClientChannelPoolHandler implements ChannelPoolHandler {
        @Override
        public void channelReleased(Channel channel) throws Exception {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Channel released");
            }
        }

        @Override
        public void channelAcquired(Channel channel) throws Exception {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Channel acquired");
            }
        }

        @Override
        public void channelCreated(Channel channel) throws Exception {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Channel created");
            }
            protocol.initChannel(channel);
        }
    }
}
