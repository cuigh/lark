package lark.pb.coder;

/**
 * 不用 Type.getInternalName(Class) 获取是为了提高性能
 *
 * @author cuigh
 */
public final class Types {
    public static final String LIST = "java/util/List";
    public static final String ARRAY_LIST = "java/util/ArrayList";
    public static final String HASH_MAP = "java/util/HashMap";
    public static final String INTEGER = "java/lang/Integer";
    public static final String LONG = "java/lang/Long";
    public static final String FLOAT = "java/lang/Float";
    public static final String DOUBLE = "java/lang/Double";
    public static final String BOOLEAN = "java/lang/Boolean";
    public static final String DATE = "java/util/Date";
    public static final String LOCAL_DATE = "java/time/LocalDate";
    public static final String LOCAL_DATE_TIME = "java/time/LocalDateTime";
    public static final String DAY_OF_WEEK = "java/time/DayOfWeek";
    public static final String CODEC = "lark/pb/Codec";
    public static final String IOEXCEPTION = "java/io/IOException";
    public static final String BASE_CODEC = "lark/pb/BaseCodec";
    public static final String CODED_OUTPUT_STREAM = "com/google/protobuf/CodedOutputStream";
    public static final String CODED_INPUT_STREAM = "com/google/protobuf/CodedInputStream";
    public static final String FIELD_TYPE = "com/google/protobuf/WireFormat$FieldType";
}
