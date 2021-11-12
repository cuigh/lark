package lark.pb;

import lark.pb.field.FieldType;
import lombok.Getter;
import lombok.Setter;
import lark.pb.annotation.ProtoField;

/**
 * @author cuigh
 */
@Getter
@Setter
public class ProtoLombok {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.BOOL, required = true)
    private boolean is;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.BOOL, required = true)
    private boolean issue;

    @ProtoField(order = 3, type = lark.pb.field.FieldType.BOOL, required = true)
    private boolean isFine1;

    @ProtoField(order = 4, type = lark.pb.field.FieldType.BOOL, required = true)
    private Boolean isFine2;

    @ProtoField(order = 5, type = lark.pb.field.FieldType.BOOL, required = true)
    private boolean boolean1;

    @ProtoField(order = 6, type = FieldType.BOOL, required = true)
    private Boolean boolean2;

    public static ProtoLombok getSample() {
        ProtoLombok v = new ProtoLombok();
        v.is = true;
        v.issue = true;
        v.isFine1 = true;
        v.isFine2 = true;
        v.boolean1 = true;
        v.boolean2 = true;
        return v;
    }
}
