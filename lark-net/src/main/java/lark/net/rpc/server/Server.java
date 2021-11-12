package lark.net.rpc.server;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import lark.core.util.Exceptions;
import lark.net.Endpoint;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.client.BaseService;
import lark.net.rpc.protocol.ServerProtocol;
import lark.net.rpc.protocol.proto.ProtoServerProtocol;
import lark.net.rpc.protocol.simple.SimpleServerProtocol;
import lark.net.rpc.server.register.Provider;
import lark.net.rpc.server.register.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cuigh
 */
public class Server extends ServerBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final ServerOptions options;
    private final SocketAddress address;
    private final ServiceContainer container = new ServiceContainer();
    private final DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<ServerProtocol> protocols;
    private InvokeDispatcher dispatcher;
    private Register register;
    private MeterRegistry meterRegistry;

    public Server(ServerOptions options) {
        this.options = options;
        this.address = createAddress(options.getAddress());
        this.protocols = createProtocols(options.getType());
        this.dispatcher = new InvokeDispatcher(options.getMinProcessThreads(), options.getMaxProcessThreads(), options.getBacklog());

        EventLoopGroup bossGroup = new NioEventLoopGroup(options.getAcceptThreads());
        EventLoopGroup workerGroup = new NioEventLoopGroup(options.getWorkThreads());
        this.group(bossGroup, workerGroup);
        this.channel(NioServerSocketChannel.class);
        this.childHandler(new ServerChannelInitializer());

        // worker option
        this.childOption(ChannelOption.SO_KEEPALIVE, options.isKeepAlive());
        this.childOption(ChannelOption.SO_REUSEADDR, true);
        this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.childOption(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
        this.childOption(ChannelOption.SO_LINGER, options.getLinger());
        this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) options.getDialTimeout().toMillis());
        this.childOption(ChannelOption.SO_RCVBUF, options.getReceiveBufferSize());
        this.childOption(ChannelOption.SO_SNDBUF, options.getSendBufferSize());
    }

    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private List<ServerProtocol> createProtocols(String type) {
        if (StringUtils.isEmpty(type)) {
            return Collections.singletonList(new SimpleServerProtocol(this));
        }

        List<ServerProtocol> list = new ArrayList<>();
        String[] types = type.split(",");
        for (String t : types) {
            switch (t) {
                case "simple":
                    list.add(new SimpleServerProtocol(this));
                    break;
                case "proto":
                    list.add(new ProtoServerProtocol(this));
                    break;
                default:
                    throw new RpcException(RpcError.SERVER_UNKNOWN_PROTOCOL, options.getType());
            }
        }
        return list;
    }

    public InvokeDispatcher getDispatcher() {
        return dispatcher;
    }

    public DefaultChannelGroup getChannels() {
        return channels;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public void registerService(Object instance) {
        if (instance instanceof BaseService) {
            throw new IllegalArgumentException("can't register a proxy object as service, this will cause dead circulation");
        }
        this.container.registerService(instance);
    }

    public void registerService(Class<?> clazz, Object instance) {
        if (instance instanceof BaseService) {
            throw new IllegalArgumentException(String.format("can't register a proxy object as service [%s], this will cause dead circulation", clazz.getName()));
        }
        this.container.registerService(clazz, instance);
    }

    public void use(ServerFilter... filters) {
        this.dispatcher.use(filters);
    }

    public void registerProtocol(ServerProtocol... protocols) {
        Collections.addAll(this.protocols, protocols);
    }

    public void start() {
        ChannelFuture future = this.bind(this.address).awaitUninterruptibly();
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            LOGGER.error("RPC server start failed: " + cause.getMessage());
            throw Exceptions.asRuntime(cause);
        }

        LOGGER.info("RPC server started at: " + options.getAddress());
        this.registerProvider();
        this.registerMetrics();
    }

    public ServiceContainer getContainer() {
        return container;
    }

    public ServerOptions getOptions() {
        return options;
    }

    private SocketAddress createAddress(String address) {
        return Endpoint.parse(address).toSocketAddress();
    }

    private void registerProvider() {
        if (register == null || !options.isRegister()) {
            return;
        }

        Provider provider = new Provider();
        provider.setName(options.getName());
        provider.setType(options.getType());
        provider.setVersion(options.getVersion());
        provider.setNote(options.getDescription());
        provider.setAddress(options.getAddress());

        register.register(() -> {
            provider.setClients(channels.size());
            return provider;
        });
    }

    private void registerMetrics() {
        if (meterRegistry != null) {
            meterRegistry.gauge("rpc.server.clients.current", channels, DefaultChannelGroup::size);
            meterRegistry.gauge("rpc.server.clients.max", options, ServerOptions::getMaxClients);
            meterRegistry.gauge("rpc.server.threads.current", dispatcher, InvokeDispatcher::getActiveCount);
            meterRegistry.gauge("rpc.server.threads.max", options, ServerOptions::getMaxProcessThreads);
        }
    }

    class ServerChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) {
            if (channels.size() >= options.getMaxClients()) {
                LOGGER.warn("Reach max clients {}, close connection", options.getMaxClients());
                ch.close();
                return;
            }
            ch.attr(ChannelState.KEY).set(new ChannelState());
            channels.add(ch);

            ChannelPipeline channelPipe = ch.pipeline();
            channelPipe.addLast("handshaker", new Handshaker());
        }
    }

    class Handshaker extends ByteToMessageDecoder {
        private static final int MAX_BYTES = 32;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            for (ServerProtocol codec : protocols) {
                if (codec.match(ctx, in)) {
                    codec.initChannnel(ctx);
                    ctx.pipeline().remove(this);
                    return;
                }
            }

            if (in.readableBytes() >= MAX_BYTES) {
                // Unknown protocol; discard everything and close the connection.
                in.clear();
                ctx.close();
                LOGGER.warn("Unknown protocol");
            }
        }
    }
}


