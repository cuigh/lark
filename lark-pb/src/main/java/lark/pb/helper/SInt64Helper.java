package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class SInt64Helper {
    public static int sizeList(final int fieldNumber, final List<Long> value) {
        int size = 0;
        for (Long t : value) {
            if (t != null) {
                size += CodedOutputStream.computeSInt64SizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Long> values) throws IOException {
        if (!values.isEmpty()) {
            for (Long value : values) {
                stream.writeSInt64(fieldNumber, value);
            }
        }
    }
}
