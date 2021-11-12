package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;
import lark.core.lang.EnumValuable;
import lark.core.util.Enums;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class EnumValuableHelper {
    public static int from(EnumValuable value) {
        return value.value();
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumValuable> T to(int value, Class<T> clazz) {
        return Enums.valueOf(clazz, value);
    }

    public static int sizeList(final int fieldNumber, final List<EnumValuable> values) {
        int size = 0;
        for (EnumValuable v : values) {
            if (v != null) {
                size += CodedOutputStream.computeEnumSizeNoTag(v.value());
            }
        }
        size += values.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<EnumValuable> values) throws IOException {
        if (!values.isEmpty()) {
            for (EnumValuable v : values) {
                stream.writeEnum(fieldNumber, v.value());
            }
        }
    }
}
