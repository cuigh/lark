package lark.pb;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lark.pb.field.FieldType;

/**
 * @author cuigh
 */
@ProtoMessage
public class ProtoBasic1 {
//        DOUBLE("Double", "double", "WIRETYPE_FIXED64", ".doubleValue()", WireFormat.FieldType.DOUBLE, "0d"),
//        FLOAT("Float", "float", "WIRETYPE_FIXED32", ".floatValue()", WireFormat.FieldType.FLOAT, "0f"),
//        INT64("Long", "int64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.INT64, "0L"),
//        UINT64("Long", "uInt64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.UINT64, "0L"),
//        INT32("Integer", "int32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.INT32, "0"),
//        FIXED64("Long", "fixed64", "WIRETYPE_FIXED64", ".longValue()", WireFormat.FieldType.FIXED64, "0L"),
//        FIXED32("Integer", "fixed32", "WIRETYPE_FIXED32", ".intValue()", WireFormat.FieldType.FIXED32, "0"),
//        BOOL("Boolean", "bool", "WIRETYPE_VARINT", ".booleanValue()", WireFormat.FieldType.BOOL, "false"),
//        STRING("String", "string", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.STRING, "\"\""),
//        BYTES("byte[]", "bytes", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.BYTES, "new byte[0]"),
//        UINT32("Integer", "uInt32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.UINT32, "0"),

//        SFIXED32("Integer", "sFixed32", "WIRETYPE_FIXED32", ".intValue()", WireFormat.FieldType.SFIXED32, "0"),
//        SFIXED64("Long", "sFixed64", "WIRETYPE_FIXED64", ".longValue()", WireFormat.FieldType.SFIXED64, "0L"),
//        SINT32("Integer", "sInt32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.SINT32, "0"),
//        SINT64("Long", "sInt64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.SINT64, "0L"),

    //        MESSAGE("Object", "object", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.MESSAGE, null),
//        ENUM("Enum", "enum", "WIRETYPE_VARINT", ".ordinal()", WireFormat.FieldType.ENUM, null),
//        TIME("Date", "int64", "WIRETYPE_VARINT", ".getTime()", WireFormat.FieldType.INT64, "0L"),
//        LTIME("LocalDateTime", "int64", "WIRETYPE_VARINT", ".atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()", WireFormat.FieldType.INT64, "0L"),
//        DEFAULT("", "", "", "", WireFormat.FieldType.MESSAGE, null);
    @ProtoField(order = 1, type = lark.pb.field.FieldType.DOUBLE, required = true)
    public double Double1 = 1.0;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.DOUBLE, required = true)
    public Double Double2 = 1.0;

    @ProtoField(order = 3, type = lark.pb.field.FieldType.FLOAT, required = true)
    public float Float1 = 1.0F;

    @ProtoField(order = 4, type = lark.pb.field.FieldType.FLOAT, required = true)
    public Float Float2 = 1.0F;

    @ProtoField(order = 5, type = lark.pb.field.FieldType.INT64, required = true)
    public long Long1 = 1;

    @ProtoField(order = 6, type = lark.pb.field.FieldType.INT64, required = true)
    public Long Long2 = 1L;

    @ProtoField(order = 7, type = lark.pb.field.FieldType.UINT64, required = true)
    public long ULong1 = 1;

    @ProtoField(order = 8, type = lark.pb.field.FieldType.UINT64, required = true)
    public Long ULong2 = 1L;

    @ProtoField(order = 9, type = lark.pb.field.FieldType.INT32, required = true)
    public int Integer1 = 1;

    @ProtoField(order = 10, type = lark.pb.field.FieldType.INT32, required = true)
    public Integer Integer2 = 1;

    @ProtoField(order = 11, type = lark.pb.field.FieldType.FIXED64, required = true)
    public long FLong1 = 1;

    @ProtoField(order = 12, type = lark.pb.field.FieldType.FIXED64, required = true)
    public Long FLong2 = 1L;

    @ProtoField(order = 13, type = lark.pb.field.FieldType.FIXED32, required = true)
    public int FInteger1 = 1;

    @ProtoField(order = 14, type = lark.pb.field.FieldType.FIXED32, required = true)
    public Integer FInteger2 = 1;

    @ProtoField(order = 15, type = lark.pb.field.FieldType.BOOL, required = true)
    public boolean Boolean1 = true;

    @ProtoField(order = 16, type = lark.pb.field.FieldType.BOOL, required = true)
    public Boolean Boolean2 = true;

    @ProtoField(order = 17, type = lark.pb.field.FieldType.STRING, required = true)
    public String String = "1";

    @ProtoField(order = 19, type = lark.pb.field.FieldType.BYTES, required = true)
    public byte[] Bytes = new byte[]{1};

    @ProtoField(order = 20, type = lark.pb.field.FieldType.UINT32, required = true)
    public int UInteger1 = 1;

    @ProtoField(order = 21, type = lark.pb.field.FieldType.UINT32, required = true)
    public Integer UInteger2 = 1;

    @ProtoField(order = 22, type = lark.pb.field.FieldType.SFIXED32, required = true)
    public int SFInteger1 = 1;

    @ProtoField(order = 23, type = lark.pb.field.FieldType.SFIXED32, required = true)
    public Integer SFInteger2 = 1;

    @ProtoField(order = 24, type = lark.pb.field.FieldType.SFIXED64, required = true)
    public long SFLong1 = 1;

    @ProtoField(order = 25, type = lark.pb.field.FieldType.SFIXED64, required = true)
    public Long SFLong2 = 1L;

    @ProtoField(order = 26, type = lark.pb.field.FieldType.SINT32, required = true)
    public int SInteger1 = 1;

    @ProtoField(order = 27, type = lark.pb.field.FieldType.SINT32, required = true)
    public Integer SInteger2 = 1;

    @ProtoField(order = 28, type = lark.pb.field.FieldType.SINT64, required = true)
    public long SLong1 = 1;

    @ProtoField(order = 29, type = FieldType.SINT64, required = true)
    public Long SLong2 = 1L;
}
