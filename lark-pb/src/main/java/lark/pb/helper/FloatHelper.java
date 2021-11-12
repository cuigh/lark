package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class FloatHelper {
    public static int sizeList(final int fieldNumber, final List<Float> value) {
        int size = 0;
        for (Float t : value) {
            if (t != null) {
                size += CodedOutputStream.computeFloatSizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Float> values) throws IOException {
        if (!values.isEmpty()) {
            for (Float value : values) {
                stream.writeFloat(fieldNumber, value);
            }
        }
    }
}
