package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

/**
 * @author cuigh
 */
@ProtoMessage(description = "任务参数")
public class Arg {
    @ProtoField(order = 1, required = true, description = "参数名称")
    private String name;

    @ProtoField(order = 2, required = true, description = "参数值")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
