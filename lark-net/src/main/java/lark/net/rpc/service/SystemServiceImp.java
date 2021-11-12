package lark.net.rpc.service;

import io.netty.channel.group.DefaultChannelGroup;
import lark.net.rpc.server.ChannelState;
import lark.net.rpc.server.Server;

import java.util.ArrayList;

/**
 * @author cuigh
 */
public class SystemServiceImp implements SystemService {
    private Server server;
    private long startTime;

    public SystemServiceImp(Server server, long startTime) {
        this.server = server;
        this.startTime = startTime;
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public GetInfoResponse getInfo() {
        GetInfoResponse response = new GetInfoResponse();
//        response.name = AppConfig.getDefault().getAppName();
        response.startTime = this.startTime;
//        response.buildInfo = BuildUtil.getBuildInfo();
        response.version = server.getOptions().getVersion() == null ? "" : server.getOptions().getVersion();
        response.clients = server.getChannels().size();
        response.maxClients = server.getOptions().getMaxClients();
//        response.profiles = ConfigProperties.activeProfiles();
        return response;
    }

    @Override
    public GetClientListResponse getClientList() {
        GetClientListResponse response = new GetClientListResponse();
        DefaultChannelGroup channels = server.getChannels();
        response.clients = new ArrayList<>(channels.size());
        channels.forEach(channel -> {
            GetClientListResponse.ClientInfo client = new GetClientListResponse.ClientInfo();
            ChannelState state = channel.attr(ChannelState.KEY).get();
            if (state == null) {
                client.id = "";
                client.createTime = System.currentTimeMillis();
            } else {
                client.id = state.getClient();
                client.createTime = state.getCreateTime().getTime();
                client.activeTime = state.getActiveTime().getTime();
                client.service = state.getService();
                client.method = state.getMethod();
            }
            client.address = channel.remoteAddress().toString().substring(1);

            response.clients.add(client);
        });
        return response;
    }
}
