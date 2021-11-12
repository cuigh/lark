package lark.net.rpc.protocol;

import io.netty.channel.Channel;

/**
 * @author cuigh
 */
public interface ClientProtocol {
    void initChannel(Channel ch);
}
