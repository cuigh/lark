package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class BoolHelper {
    public static int sizeList(final int fieldNumber, final List<Boolean> value) {
        int size = 0;
        for (Boolean t : value) {
            if (t != null) {
                size += CodedOutputStream.computeBoolSizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Boolean> values) throws IOException {
        if (!values.isEmpty()) {
            for (Boolean value : values) {
                stream.writeBool(fieldNumber, value);
            }
        }
    }
}
