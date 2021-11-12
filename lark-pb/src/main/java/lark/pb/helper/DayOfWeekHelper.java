package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

/**
 * @author cuigh
 */
public final class DayOfWeekHelper {
    public static int from(DayOfWeek value) {
        return value.getValue();
    }

    public static DayOfWeek to(int value) {
        return DayOfWeek.of(value);
    }

    public static int sizeList(final int fieldNumber, final List<DayOfWeek> value) {
        int size = 0;
        for (DayOfWeek t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<DayOfWeek> values) throws IOException {
        if (!values.isEmpty()) {
            for (DayOfWeek value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
