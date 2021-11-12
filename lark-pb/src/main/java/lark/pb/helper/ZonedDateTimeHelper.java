package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.util.List;

/**
 * @author cuigh
 */
public final class ZonedDateTimeHelper {
    public static long from(ZonedDateTime value) {
        return value.toInstant().toEpochMilli();
    }

    public static ZonedDateTime to(long value) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
    }

    public static int sizeList(final int fieldNumber, final List<ZonedDateTime> value) {
        int size = 0;
        for (ZonedDateTime t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<ZonedDateTime> values) throws IOException {
        if (!values.isEmpty()) {
            for (ZonedDateTime value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
