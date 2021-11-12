package lark.task.service;

import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcService;
import lark.task.data.*;

/**
 * @author cuigh
 */
@RpcService(description = "计划任务调度服务")
public interface ScheduleService {
    @RpcMethod(description = "创建业务任务")
    Result create(CreateRequest param);

    @RpcMethod(description = "修改任务调度信息")
    Result modify(ModifyRequest param);

    @RpcMethod(description = "执行任务")
    Result execute(ExecuteRequest param);

    @RpcMethod(description = "通知任务执行结果")
    Result notify(NotifyRequest param);
}