package lark.pb;

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
public class ProtoList2 {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.DOUBLE, required = false)
    private List<Double> doubleList;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.FLOAT, required = false)
    private List<Float> floatList = new ArrayList<>();

    @ProtoField(order = 3, type = lark.pb.field.FieldType.INT64, required = false)
    private List<Long> longList = new ArrayList<>();

    @ProtoField(order = 4, type = lark.pb.field.FieldType.INT32, required = false)
    private List<Integer> integerList = new ArrayList<>();

    @ProtoField(order = 5, type = lark.pb.field.FieldType.BOOL, required = false)
    private List<Boolean> booleanList = new ArrayList<>();

    @ProtoField(order = 6, type = lark.pb.field.FieldType.STRING, required = false)
    private List<String> stringList = new ArrayList<>();

    @ProtoField(order = 9, type = FieldType.BYTES, required = true)
    private List<byte[]> bytesList = new ArrayList<>();
}
