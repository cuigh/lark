package lark.pb;

import lark.core.lang.EnumValuable;
import lark.pb.field.FieldType;
import lombok.Getter;
import lombok.Setter;
import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
@ProtoMessage
@Getter
@Setter
public class ProtoObject2 {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.ENUM, required = true)
    private ObjectStatus status;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.MESSAGE, required = false)
    private List<ObjectItem> items = new ArrayList<>();

    @ProtoMessage
    @Getter
    @Setter
    public static class ObjectItem {
        @ProtoField(order = 1, required = true)
        private String name;

        @ProtoField(order = 2, type = FieldType.MESSAGE, required = false)
        private List<ObjectSubItem> subItems = new ArrayList<>();

        @ProtoMessage
        @Getter
        @Setter
        public static class ObjectSubItem {
            @ProtoField(order = 1, required = true)
            private String name;
        }
    }

    public enum ObjectStatus implements EnumValuable {
        VALID(1), INVALID(2);

        private int value;

        ObjectStatus(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }
}
