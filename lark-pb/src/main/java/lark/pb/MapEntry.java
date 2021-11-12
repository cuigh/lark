package lark.pb;

import com.google.protobuf.*;
import lark.core.lang.EnumValuable;
import lark.core.util.Enums;
import lark.pb.helper.EnumHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author cuigh
 */
public final class MapEntry extends AbstractMessage {
    public static final int KEY_FIELD_NUMBER = 1;
    public static final int VALUE_FIELD_NUMBER = 2;
    private WireFormat.FieldType keyType;
    private WireFormat.FieldType valueType;
    private Class<?> keyClass;
    private Class<?> valueClass;
    private Object key;
    private Object value;

    public MapEntry(WireFormat.FieldType keyType, WireFormat.FieldType valueType, Class<?> keyClass, Class<?> valueClass) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Parser<? extends Message> getParserForType() {
        return new MapEntryParser(this);
    }

    @Override
    public Message.Builder newBuilderForType() {
        return null;
    }

    @Override
    public Message.Builder toBuilder() {
        return null;
    }

    @Override
    public Message getDefaultInstanceForType() {
        return null;
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        return null;
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return null;
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor field) {
        return false;
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor field) {
        return null;
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
        return 0;
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
        return null;
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
        return null;
    }

    static class MapEntryParser extends AbstractParser<MapEntry> {
        private MapEntry entry;

        MapEntryParser(MapEntry entry) {
            this.entry = entry;
        }

        @SuppressWarnings("unchecked")
        private static Object parseField(CodedInputStream input, WireFormat.FieldType type, Class<?> clazz) throws IOException {
            switch (type) {
                case MESSAGE:
                    int length = input.readRawVarint32();
                    final int oldLimit = input.pushLimit(length);
                    Object ret = CodecFactory.get(clazz).decode(input.readRawBytes(length));
                    input.popLimit(oldLimit);
                    return ret;
                case ENUM:
                    int value = input.readEnum();
                    if (EnumValuable.class.isAssignableFrom(clazz)) {
                        return Enums.valueOf((Class<? extends EnumValuable>) clazz, value);
                    }
                    return EnumHelper.to(value, ((Class<? extends java.lang.Enum>) clazz).getEnumConstants());
                default:
                    return CodecUtils.readPrimitiveField(input, type, true);
            }
        }

        @Override
        public MapEntry parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            try {
                while (true) {
                    int tag = input.readTag();
                    if (tag == 0) {
                        break;
                    }
                    if (tag == CodecUtils.makeTag(KEY_FIELD_NUMBER, entry.keyType.getWireType())) {
                        entry.key = parseField(input, entry.keyType, entry.keyClass);
                    } else if (tag == CodecUtils.makeTag(VALUE_FIELD_NUMBER, entry.valueType.getWireType())) {
                        entry.value = parseField(input, entry.valueType, entry.valueClass);
                    } else {
                        if (!input.skipField(tag)) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new ProtoException(e);
            }

            if (entry.value == null) {
                throw new UninitializedMessageException(Collections.singletonList("value"));
            }
            return entry;
        }
    }
}
