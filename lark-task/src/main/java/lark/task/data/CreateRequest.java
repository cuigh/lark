package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.util.List;

/**
 * @author cuigh
 */
@ProtoMessage(description = "创建业务任务信息参数")
public class CreateRequest {
    @ProtoField(order = 1, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 2, description = "别名")
    private String alias;

    @ProtoField(order = 3, description = "任务描述")
    private String note;

    @ProtoField(order = 4, required = true, description = "执行器服务名称")
    private String executor;

    @ProtoField(order = 5, description = "触发器列表")
    private List<Trigger> triggers;

    @ProtoField(order = 6, description = "参数列表")
    private List<Arg> args;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }
}
