package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.util.Date;

/**
 * @author cuigh
 */
@ProtoMessage(description = "通知任务执行结果参数")
public class NotifyRequest {
    @ProtoField(order = 1, required = true, description = "任务唯一标识")
    private String id;

    @ProtoField(order = 2, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 3, required = true, description = "执行结果")
    private Result result;

    @ProtoField(order = 4, required = true, description = "执行开始时间")
    private Date startTime;

    @ProtoField(order = 5, required = true, description = "执行结束时间")
    private Date endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
