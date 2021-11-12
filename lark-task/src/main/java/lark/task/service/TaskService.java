package lark.task.service;

import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcParameter;
import lark.net.rpc.annotation.RpcService;
import lark.task.data.ExecuteParam;
import lark.task.data.Result;

/**
 * @author cuigh
 */
@RpcService(description = "计划任务执行服务")
public interface TaskService {
    /**
     * 执行任务
     *
     * @param param 参数
     * @return
     */
    @RpcMethod(name = "Execute", description = "执行任务")
    @RpcParameter(description = "任务执行结果")
    Result execute(@RpcParameter(description = "任务参数") ExecuteParam param);
}
