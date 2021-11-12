package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class DoubleHelper {
    public static int sizeList(final int fieldNumber, final List<Double> value) {
        int size = 0;
        for (Double t : value) {
            if (t != null) {
                size += CodedOutputStream.computeDoubleSizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Double> values) throws IOException {
        if (!values.isEmpty()) {
            for (Double value : values) {
                stream.writeDouble(fieldNumber, value);
            }
        }
    }
}
