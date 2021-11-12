package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class Fixed32Helper {
    public static int sizeList(final int fieldNumber, final List<Integer> value) {
        int size = 0;
        for (Integer t : value) {
            if (t != null) {
                size += CodedOutputStream.computeFixed32SizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Integer> values) throws IOException {
        if (!values.isEmpty()) {
            for (Integer value : values) {
                stream.writeFixed32(fieldNumber, value);
            }
        }
    }
}
