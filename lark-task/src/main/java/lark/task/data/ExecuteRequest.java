package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cuigh
 */
@ProtoMessage(description = "执行任务参数")
public class ExecuteRequest {
    @ProtoField(order = 3, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 5, description = "参数列表")
    private List<Arg> args;

    @ProtoField(order = 6, description = "执行时间, 如不指定则立即执行")
    private LocalDateTime time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}

