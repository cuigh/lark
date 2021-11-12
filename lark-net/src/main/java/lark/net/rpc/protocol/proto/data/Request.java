package lark.net.rpc.protocol.proto.data;

import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author cuigh
 */
@Getter
@Setter
public class Request {
    /**
     * 消息 ID, 每个请求唯一
     */
    @ProtoField(order = 1)
    private long id;

    @ProtoField(order = 2, required = true)
    private String service;

    @ProtoField(order = 3, required = true)
    private String method;

    @ProtoField(order = 4)
    private List<byte[]> args;

    @ProtoField(order = 5)
    private List<Label> labels;

    @ProtoField(order = 6)
    private String client;

//    @ProtoField(order = 7)
//    private String server;
}
