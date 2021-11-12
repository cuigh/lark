package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static java.time.temporal.ChronoField.NANO_OF_DAY;

/**
 * @author cuigh
 */
public final class LocalTimeHelper {
    public static long from(LocalTime value) {
        return value.getLong(NANO_OF_DAY);
    }

    public static LocalTime to(long value) {
        return LocalTime.ofNanoOfDay(value);
    }

    public static int sizeList(final int fieldNumber, final List<LocalTime> value) {
        int size = 0;
        for (LocalTime t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<LocalTime> values) throws IOException {
        if (!values.isEmpty()) {
            for (LocalTime value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
