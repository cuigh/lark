package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

/**
 * @author cuigh
 */
@ProtoMessage(description = "调用结果")
public class Result {
    @ProtoField(order = 1, required = true, description = "是否成功")
    private boolean success;

    @ProtoField(order = 2, description = "错误信息")
    private String errorInfo;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}

