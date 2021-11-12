package lark.net.rpc.protocol.simple.data;

import lark.core.lang.BusinessException;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.protocol.simple.SimpleEncoder;
import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author cuigh
 */
@Getter
@Setter
public class SimpleResponseMessage {
    /**
     * 是否成功
     */
    @ProtoField(order = 1, required = true)
    private boolean success;

    /**
     * 调用结果
     */
    @ProtoField(order = 2)
    private SimpleValue result;

    /**
     * 错误信息
     */
    @ProtoField(order = 3)
    private String errorInfo;

    /**
     * 服务器时间
     */
    @ProtoField(order = 4, required = true)
    private long serverTime;

    /**
     * 错误代码
     */
    @ProtoField(order = 6)
    private int errorCode;

    /**
     * 错误详情，如异常堆栈，一般只应该在 DEBUG 模式传播给客户端，便于快速调试
     */
    @ProtoField(order = 7)
    private String errorDetail;

    /**
     * 消息 ID, 每个请求唯一
     */
    @ProtoField(order = 8)
    private String id;

    @ProtoField(order = 9)
    private List<SimpleLabel> labels;

    public SimpleResponseMessage() {
        // for decode
    }

    public void fillResult(Object result) {
        this.success = true;
        this.result = SimpleEncoder.encode(result);
    }

    public void fillError(Throwable e) {
        this.success = false;
        if (e instanceof BusinessException) {
            this.errorCode = ((BusinessException) e).getCode();
        } else {
            this.errorCode = RpcError.SERVER_UNKNOWN_ERROR.value();
        }
        this.errorInfo = e.getMessage();
        if (StringUtils.isEmpty(this.errorInfo)) {
            this.errorInfo = e.toString();
        }
//        if (AppConfig.getDefault().isDebugEnabled()) {
//            this.errorDetail = Exceptions.getStackTrace(e);
//        }
    }

    public void fillError(RpcError re) {
        this.success = false;
        this.errorCode = re.value();
        this.errorInfo = re.message();
    }
}