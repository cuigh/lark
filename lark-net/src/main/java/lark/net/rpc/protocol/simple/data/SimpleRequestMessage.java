package lark.net.rpc.protocol.simple.data;

import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author cuigh
 */
@Getter
@Setter
public class SimpleRequestMessage {
    @ProtoField(order = 1, required = true)
    private String client;

    @ProtoField(order = 3, required = true)
    private String service;

    @ProtoField(order = 4, required = true)
    private String method;

    @ProtoField(order = 5)
    private List<SimpleValue> args;

    @ProtoField(order = 6)
    private List<SimpleLabel> labels;

    /**
     * 追踪 ID, 在整个请求链中保持不变
     */
    @ProtoField(order = 7)
    private String traceId;

    /**
     * 消息 ID, 每个请求唯一
     */
    @ProtoField(order = 8)
    private String id;

    @ProtoField(order = 9)
    private String server;
}
