package lark.pb.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

/**
 * @author cuigh
 */
public final class BigDecimalHelper {
    public static String from(BigDecimal value) {
        return value.toPlainString();
    }

    public static BigDecimal to(String value) {
        return new BigDecimal(value);
    }

    public static int sizeList(final int fieldNumber, final List<BigDecimal> value) {
        int size = 0;
        for (BigDecimal t : value) {
            if (t != null) {
                size += CodedOutputStream.computeStringSizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<BigDecimal> values) throws IOException {
        if (!values.isEmpty()) {
            for (BigDecimal value : values) {
                stream.writeString(fieldNumber, from(value));
            }
        }
    }
}
