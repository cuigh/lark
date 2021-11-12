package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.util.List;

/**
 * @author cuigh
 */
@ProtoMessage(description = "修改任务信息参数")
public class ModifyRequest {
    @ProtoField(order = 1, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 2, description = "是否启用任务")
    private boolean enabled;

    @ProtoField(order = 3, description = "触发器列表")
    private List<Trigger> triggers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }
}
