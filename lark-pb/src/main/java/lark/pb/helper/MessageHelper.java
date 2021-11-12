package lark.pb.helper;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import lark.pb.CodecUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author cuigh
 */
public final class MessageHelper {
    public static <T> int size(final int fieldNumber, final T value, final Class<T> clazz) {
        Codec<T> codec = CodecFactory.get(clazz);
        int size = codec.size(value);
        size = size + CodedOutputStream.computeUInt32SizeNoTag(size);
        return size + CodedOutputStream.computeTagSize(fieldNumber);
    }

    public static <T> int sizeList(final int fieldNumber, final List<T> value, final Class<T> clazz) {
        if (value.isEmpty()) {
            return 0;
        }

        Codec<T> codec = CodecFactory.get(clazz);
        int totalSize = 0;
        for (T t : value) {
            if (t != null) {
                int size = codec.size(t);
                totalSize += CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeUInt32SizeNoTag(size) + size;
            }
        }
        return totalSize;
    }

    public static <T> void write(CodedOutputStream stream, int fieldNumber, T value, Class<T> clazz) throws IOException {
        Codec<T> codec = CodecFactory.get(clazz);
        write(stream, fieldNumber, value, codec);
    }

    public static <T> void writeList(CodedOutputStream stream, int fieldNumber, List<T> values, Class<T> clazz) throws IOException {
        if (!values.isEmpty()) {
            Codec<T> codec = CodecFactory.get(clazz);
            for (T value : values) {
                write(stream, fieldNumber, value, codec);
            }
        }
    }

    private static <T> void write(CodedOutputStream stream, int fieldNumber, T value, Codec<T> codec) throws IOException {
        stream.writeUInt32NoTag(CodecUtils.makeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED));
        stream.writeUInt32NoTag(codec.size(value));
        codec.encodeTo(value, stream);
    }

    public static <T> T read(CodedInputStream stream, Class<T> clazz) throws IOException {
        Codec<T> codec = CodecFactory.get(clazz);
        int length = stream.readRawVarint32();
        final int oldLimit = stream.pushLimit(length);
        T value = codec.decodeFrom(stream);
        stream.checkLastTagWas(0);
        stream.popLimit(oldLimit);
        return value;
    }

//    public static <T> T read(CodedInputStream stream, Codec<T> codec) throws IOException {
//        int length = stream.readRawVarint32();
//        final int oldLimit = stream.pushLimit(length);
//        T value = codec.decodeFrom(stream);
//        stream.checkLastTagWas(0);
//        stream.popLimit(oldLimit);
//        return value;
//    }
}
