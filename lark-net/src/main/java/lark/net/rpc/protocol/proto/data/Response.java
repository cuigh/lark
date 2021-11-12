package lark.net.rpc.protocol.proto.data;

import lark.core.lang.BusinessException;
import lark.core.util.Exceptions;
import lark.net.rpc.ResponseMessage;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author cuigh
 */
@Getter
@Setter
public class Response implements ResponseMessage {
    /**
     * 消息 ID, 每个请求唯一
     */
    @ProtoField(order = 1)
    private long id;

    /**
     * 调用结果
     */
    @ProtoField(order = 2)
    private byte[] result;

    /**
     * 错误代码
     */
    @ProtoField(order = 3)
    private Error error;

    @ProtoField(order = 4)
    private List<Label> labels;

    public Response() {
        // for decode
    }

    @Override
    public Object decode(Class<?> type) {
        if (this.error != null) {
            throw new BusinessException(this.error.getCode(), this.error.getMessage(), this.error.getDetail());
        }

        Codec codec = CodecFactory.get(type);
        try {
            return codec.decode(this.result);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }
}


