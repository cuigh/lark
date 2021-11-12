package lark.pb.field;

import com.google.protobuf.WireFormat;

/**
 * @author cuigh
 */
public enum FieldType {
    AUTO("", WireFormat.FieldType.MESSAGE, 0),
    DOUBLE("double", WireFormat.FieldType.DOUBLE, 1),
    FLOAT("float", WireFormat.FieldType.FLOAT, 2),
    INT64("int64", WireFormat.FieldType.INT64, 3),
    UINT64("uInt64", WireFormat.FieldType.UINT64, 4),
    INT32("int32", WireFormat.FieldType.INT32, 5),
    FIXED64("fixed64", WireFormat.FieldType.FIXED64, 6),
    FIXED32("fixed32", WireFormat.FieldType.FIXED32, 7),
    BOOL("bool", WireFormat.FieldType.BOOL, 8),
    STRING("string", WireFormat.FieldType.STRING, 9),
    MESSAGE("object", WireFormat.FieldType.MESSAGE, 11),
    BYTES("bytes", WireFormat.FieldType.BYTES, 12),
    UINT32("uInt32", WireFormat.FieldType.UINT32, 13),
    ENUM("enum", WireFormat.FieldType.ENUM, 14),
    SFIXED32("sFixed32", WireFormat.FieldType.SFIXED32, 15),
    SFIXED64("sFixed64", WireFormat.FieldType.SFIXED64, 16),
    SINT32("sInt32", WireFormat.FieldType.SINT32, 17),
    SINT64("sInt64", WireFormat.FieldType.SINT64, 18);

    /**
     * protobuf type
     */
    private final String type;

    /**
     * internal field type
     */
    private WireFormat.FieldType protoFieldType;

    /**
     * kind
     */
    private int kind;

    FieldType(String type, WireFormat.FieldType protoFieldType, int kind) {
        this.type = type;
        this.protoFieldType = protoFieldType;
        this.kind = kind;
    }

    /**
     * get the protoFieldType
     *
     * @return the protoFieldType
     */
    public WireFormat.FieldType getProtoFieldType() {
        return protoFieldType;
    }

    /**
     * get protobuf type
     *
     * @return protobuf type
     */
    public String getType() {
        return type;
    }

    public int getKind() {
        return this.kind;
    }
}
