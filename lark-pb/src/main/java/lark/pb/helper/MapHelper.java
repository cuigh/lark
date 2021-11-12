package lark.pb.helper;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import lark.pb.CodecUtils;
import lark.pb.MapEntry;

import java.io.IOException;
import java.util.Map;

/**
 * @author cuigh
 */
public final class MapHelper {
    public static <K, V> int size(final int fieldNumber, final Map<K, V> value,
                                  final WireFormat.FieldType keyType, final WireFormat.FieldType valueType) {
        if (value.isEmpty()) {
            return 0;
        }

        int size = 0;
        for (Map.Entry<K, V> entry : value.entrySet()) {
            int elemSize = CodecUtils.computeElementSize(keyType, MapEntry.KEY_FIELD_NUMBER, entry.getKey()) +
                    CodecUtils.computeElementSize(valueType, MapEntry.VALUE_FIELD_NUMBER, entry.getValue());
            size += CodedOutputStream.computeTagSize(fieldNumber);
            size += CodedOutputStream.computeUInt32SizeNoTag(elemSize) + elemSize;
        }
        return size;
    }

    public static <K, V> void write(CodedOutputStream stream, int fieldNumber, Map<K, V> map,
                                    WireFormat.FieldType keyType, WireFormat.FieldType valueType) throws IOException {
        if (map.isEmpty()) {
            return;
        }

        for (Map.Entry<K, V> entry : map.entrySet()) {
            stream.writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int elemSize = CodecUtils.computeElementSize(keyType, MapEntry.KEY_FIELD_NUMBER, entry.getKey()) +
                    CodecUtils.computeElementSize(valueType, MapEntry.VALUE_FIELD_NUMBER, entry.getValue());
            stream.writeUInt32NoTag(elemSize);
            CodecUtils.writeElement(stream, keyType, 1, entry.getKey());
            CodecUtils.writeElement(stream, valueType, 2, entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public static void read(CodedInputStream stream, Map map,
                            WireFormat.FieldType keyType, WireFormat.FieldType valueType,
                            Class<?> keyClass, Class<?> valueClass) throws IOException {
        MapEntry entry = new MapEntry(keyType, valueType, keyClass, valueClass);
        stream.readMessage(entry.getParserForType(), null);
        map.put(entry.getKey(), entry.getValue());
    }
}
