package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author cuigh
 */
public final class LocalDateHelper {
    public static long from(LocalDate value) {
        return value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate to(long value) {
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int sizeList(final int fieldNumber, final List<LocalDate> value) {
        int size = 0;
        for (LocalDate t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<LocalDate> values) throws IOException {
        if (!values.isEmpty()) {
            for (LocalDate value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
