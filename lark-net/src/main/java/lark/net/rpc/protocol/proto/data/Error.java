package lark.net.rpc.protocol.proto.data;

import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cuigh
 */
@Getter
@Setter
public class Error {
    /**
     * 错误代码
     */
    @ProtoField(order = 1)
    private int code;

    /**
     * 错误信息
     */
    @ProtoField(order = 2)
    private String message;

    /**
     * 错误详情，如异常堆栈，一般只应该在 DEBUG 模式传播给客户端，便于快速调试
     */
    @ProtoField(order = 3)
    private String detail;
}
