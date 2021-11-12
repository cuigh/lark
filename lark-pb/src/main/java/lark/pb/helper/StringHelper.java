package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class StringHelper {
    public static int sizeList(final int fieldNumber, final List<String> value) {
        int size = 0;
        for (String t : value) {
            if (t != null) {
                size += CodedOutputStream.computeStringSizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<String> values) throws IOException {
        if (!values.isEmpty()) {
            for (String value : values) {
                stream.writeString(fieldNumber, value);
            }
        }
    }
}
