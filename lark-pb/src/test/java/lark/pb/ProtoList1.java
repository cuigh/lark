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
public class ProtoList1 {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.DOUBLE, required = false)
    public List<Double> DoubleList = new ArrayList<>();

    @ProtoField(order = 2, type = lark.pb.field.FieldType.FLOAT, required = false)
    public List<Float> FloatList = new ArrayList<>();

    @ProtoField(order = 3, type = lark.pb.field.FieldType.INT64, required = false)
    public List<Long> LongList = new ArrayList<>();

    @ProtoField(order = 4, type = lark.pb.field.FieldType.INT32, required = false)
    public List<Integer> IntegerList = new ArrayList<>();

    @ProtoField(order = 5, type = lark.pb.field.FieldType.BOOL, required = false)
    public List<Boolean> BooleanList = new ArrayList<>();

    @ProtoField(order = 6, type = lark.pb.field.FieldType.STRING, required = false)
    public List<String> StringList = new ArrayList<>();

    @ProtoField(order = 7, type = lark.pb.field.FieldType.BYTES, required = true)
    public List<byte[]> BytesList = new ArrayList<>();

    @ProtoField(order = 8, type = FieldType.ENUM, required = false)
    public List<ObjectStatus> EnumList = new ArrayList<>();

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
