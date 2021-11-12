package lark.pb;

import lark.core.lang.EnumValuable;
import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lark.pb.field.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
@ProtoMessage
public class ProtoObject1 {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.ENUM, required = true)
    public ObjectStatus Status;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.MESSAGE, required = false)
    public List<ObjectItem> Items = new ArrayList<>();

    @ProtoMessage
    public static class ObjectItem {
        @ProtoField(order = 1, required = true)
        public String Name;

        @ProtoField(order = 2, type = FieldType.MESSAGE, required = false)
        public List<ObjectSubItem> SubItems = new ArrayList<>();

        @ProtoMessage
        public static class ObjectSubItem {
            @ProtoField(order = 1, required = true)
            public String Name;
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
