package lark.task.data;

import lark.core.lang.EnumValuable;
import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author cuigh
 */
@Getter
@Setter
@ProtoMessage(description = "执行参数")
public class ExecuteParam {
    @ProtoField(order = 1, required = true, description = "执行类型, 0-自动, 1-手动")
    private ExecuteType type;

    @ProtoField(order = 2, description = "任务唯一标识")
    private String id;

    @ProtoField(order = 3, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 4, description = "任务别名")
    private String alias;

    @ProtoField(order = 5, description = "参数")
    private List<Arg> args;

    /**
     * 执行方式
     */
    public enum ExecuteType implements EnumValuable {
        /**
         * 自动
         */
        AUTO(0),

        /**
         * 手动
         */
        MANUAL(1);

        private int value;

        ExecuteType(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }
}

