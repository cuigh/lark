package lark.pb.coder;

import lark.pb.ProtoException;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldKind;
import lark.pb.field.FieldType;

import java.util.EnumMap;

/**
 * @author cuigh
 */
public final class CoderManager {
    private static final EnumMap<FieldType, CoderFactory> FACTORIES = new EnumMap<>(FieldType.class);

    static {
        FACTORIES.put(FieldType.DOUBLE, new DoubleCoderFactory());
        FACTORIES.put(FieldType.FLOAT, new FloatCoderFactory());
        FACTORIES.put(FieldType.INT32, new Int32CoderFactory());
        FACTORIES.put(FieldType.INT64, new Int64CoderFactory());
        FACTORIES.put(FieldType.UINT32, new UInt32CoderFactory());
        FACTORIES.put(FieldType.UINT64, new UInt64CoderFactory());
        FACTORIES.put(FieldType.FIXED32, new Fixed32CoderFactory());
        FACTORIES.put(FieldType.FIXED64, new Fixed64CoderFactory());
        FACTORIES.put(FieldType.SFIXED32, new SFixed32CoderFactory());
        FACTORIES.put(FieldType.SFIXED64, new SFixed64CoderFactory());
        FACTORIES.put(FieldType.SINT32, new SInt32CoderFactory());
        FACTORIES.put(FieldType.SINT64, new SInt64CoderFactory());
        FACTORIES.put(FieldType.BOOL, new BoolCoderFactory());
        FACTORIES.put(FieldType.STRING, new StringCoderFactory());
        FACTORIES.put(FieldType.BYTES, new BytesCoderFactory());
        FACTORIES.put(FieldType.MESSAGE, new MessageCoderFactory());
        FACTORIES.put(FieldType.ENUM, new EnumCoderFactory());
    }

    private CoderManager() {
        // 防止实例化
    }

    /**
     * Acquire Coder instance
     *
     * @param fi Field information
     * @return Coder instance
     */
    public static Coder get(FieldInfo fi) {
        Class<?> clazz;
        if (fi.getKind() == FieldKind.LIST) {
            clazz = fi.getList().getItemClass();
        } else {
            clazz = fi.getField().getType();
        }

        return get(fi.getType(), clazz);
    }

    /**
     * Acquire Coder instance
     *
     * @param type  Field type
     * @param clazz Field class
     * @return Coder instance
     */
    public static Coder get(FieldType type, Class<?> clazz) {
        CoderFactory factory = FACTORIES.get(type);
        if (factory == null) {
            throw new ProtoException("No coder registered for type: " + type);
        }

        Coder coder = factory.get(clazz);
        if (coder == null) {
            String msg = String.format("Can't map type %s to %s", clazz.getName(), type.toString());
            throw new ProtoException(msg);
        }
        return coder;
    }
}
