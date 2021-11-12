package lark.net.rpc.protocol.simple;

import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lark.net.rpc.protocol.simple.data.SimpleValue;
import lark.pb.Codec;
import lark.pb.CodecFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * simple 序列化
 *
 * @author cuigh
 */
@SuppressWarnings("unchecked")
public class SimpleEncoder {
    public static final int DT_NULL = 0;
    public static final int DT_BYTE_ARRAY = 1;
    public static final int DT_STRING = 3;
    public static final int DT_INT32 = 4;
    public static final int DT_INT64 = 5;
    public static final int DT_BOOL = 10;
    public static final int DT_FLOAT = 18;
    public static final int DT_DOUBLE = 19;
    public static final int DT_PROTOBUF = 21;
    public static final int DT_BOOL_ARRAY = 100;
    public static final int DT_STRING_ARRAY = 103;
    public static final int DT_INT32_ARRAY = 104;
    public static final int DT_INT_64_ARRAY = 105;
    public static final int DT_FLOAT_ARRAY = 118;
    public static final int DT_DOUBLE_ARRAY = 119;
    public static final int DT_COMPRESSED_BOOL_ARRAY = 200;
    public static final int DT_COMPRESSED_STRING_ARRAY = 203;
    public static final int DT_COMPRESSED_INT32_ARRAY = 204;
    public static final int DT_COMPRESSED_INT64_ARRAY = 205;
    public static final int DT_COMPRESSED_FLOAT_ARRAY = 218;
    public static final int DT_COMPRESSED_DOUBLE_ARRAY = 219;
    public static final int DT_COMPRESSED_PROTOBUF = 251;
    public static final int DT_COMPRESSED_STRING = 254;
    public static final int DT_COMPRESSED_BYTE_ARRAY = 255;
    /**
     * 启用压缩的阈值，100K
     */
    private static final int COMPRESSION_THRESHOLD = 100 * 1024;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private SimpleEncoder() {
        // 防止实例化
    }

    public static int getDataType(Class<?> clazz) {
        if (clazz == null) {
            return DT_NULL;
        } else if (clazz.isArray()) {
            Class<?> ct = clazz.getComponentType();
            if (ct == byte.class || ct == Byte.class) {
                return DT_BYTE_ARRAY;
            } else if (ct == boolean.class || ct == Boolean.class) {
                return DT_BOOL_ARRAY;
            } else if (ct == String.class) {
                return DT_STRING_ARRAY;
            } else if (ct == int.class || ct == Integer.class) {
                return DT_INT32_ARRAY;
            } else if (ct == long.class || ct == Long.class) {
                return DT_INT_64_ARRAY;
            } else if (ct == float.class || ct == Float.class) {
                return DT_FLOAT_ARRAY;
            } else if (ct == double.class || ct == Double.class) {
                return DT_DOUBLE_ARRAY;
            }
        } else if (clazz == String.class) {
            return DT_STRING;
        } else if (clazz == int.class || clazz == Integer.class) {
            return DT_INT32;
        } else if (clazz == long.class || clazz == Long.class) {
            return DT_INT64;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return DT_BOOL;
        } else if (clazz == float.class || clazz == Float.class) {
            return DT_FLOAT;
        } else if (clazz == double.class || clazz == Double.class) {
            return DT_DOUBLE;
        }
        return DT_PROTOBUF;
    }

    public static SimpleValue encode(Object obj) {
        if (obj == null) {
            return new SimpleValue(DT_NULL, new byte[]{0});
        } else if (obj instanceof byte[]) {
            return encode((byte[]) obj);
        } else if (obj instanceof String) {
            return encode((String) obj);
        } else if (obj instanceof Integer) {
            return encode((Integer) obj);
        } else if (obj instanceof Long) {
            return encode((Long) obj);
        } else if (obj instanceof Boolean) {
            return encode((Boolean) obj);
        } else if (obj instanceof Float) {
            return encode((Float) obj);
        } else if (obj instanceof Double) {
            return encode((Double) obj);
        } else if (obj instanceof GeneratedMessage) {
            GeneratedMessage message = (GeneratedMessage) obj;
            byte[] bytes = message.toByteArray();
            if (bytes.length > COMPRESSION_THRESHOLD) {
                return new SimpleValue(DT_COMPRESSED_PROTOBUF, compress(bytes, bytes.length));
            } else {
                return new SimpleValue(DT_PROTOBUF, bytes);
            }
        } else if (obj instanceof boolean[]) {
            return encode((boolean[]) obj);
        } else if (obj instanceof Boolean[]) {
            return encode((Boolean[]) obj);
        } else if (obj instanceof String[]) {
            return encode((String[]) obj);
        } else if (obj instanceof int[]) {
            return encode((int[]) obj);
        } else if (obj instanceof Integer[]) {
            return encode((Integer[]) obj);
        } else if (obj instanceof long[]) {
            return encode((long[]) obj);
        } else if (obj instanceof Long[]) {
            return encode((Long[]) obj);
        } else if (obj instanceof float[]) {
            return encode((float[]) obj);
        } else if (obj instanceof Float[]) {
            return encode((Float[]) obj);
        } else if (obj instanceof double[]) {
            return encode((double[]) obj);
        } else if (obj instanceof Double[]) {
            return encode((Double[]) obj);
        } else {
            try {
                Codec codec = CodecFactory.get(obj.getClass());
                byte[] bytes = codec.encode(obj);
                return new SimpleValue(DT_PROTOBUF, bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object decode(SimpleValue value, Class<?> clazz) {
        return decode(value.getDataType(), value.getData(), clazz);
    }

    public static Object decode(int type, byte[] data, Class<?> clazz) {
        switch (type) {
            case DT_NULL:
                return null;
            case DT_BYTE_ARRAY:
                return data;
            case DT_STRING:
                return new String(data, CHARSET);
            case DT_INT32:
                return toIntLE(data);
            case DT_INT64:
                return toLongLE(data);
            case DT_BOOL:
                return data[0] == 1 ? Boolean.TRUE : Boolean.FALSE;
            case DT_FLOAT:
                return Float.intBitsToFloat(toIntLE(data));
            case DT_DOUBLE:
                return Double.longBitsToDouble(toLongLE(data));
            case DT_PROTOBUF:
                return decodeProto(data, clazz);
            case DT_BOOL_ARRAY:
            case DT_STRING_ARRAY:
            case DT_INT32_ARRAY:
            case DT_INT_64_ARRAY:
            case DT_FLOAT_ARRAY:
            case DT_DOUBLE_ARRAY:
                return decodeArray(data, false, clazz);
            case DT_COMPRESSED_BOOL_ARRAY:
            case DT_COMPRESSED_STRING_ARRAY:
            case DT_COMPRESSED_INT32_ARRAY:
            case DT_COMPRESSED_INT64_ARRAY:
            case DT_COMPRESSED_FLOAT_ARRAY:
            case DT_COMPRESSED_DOUBLE_ARRAY:
                return decodeArray(data, true, clazz);
            case DT_COMPRESSED_PROTOBUF:
                return decodeProto(decompress(data), clazz);
            case DT_COMPRESSED_STRING:
                return new String(decompress(data), CHARSET);
            case DT_COMPRESSED_BYTE_ARRAY:
                return decompress(data);
            default:
                throw new RuntimeException("unknown object type: " + type);
        }
    }

    private static Object decodeProto(byte[] data, Class<?> clazz) {
        try {
            if (GeneratedMessage.class.isAssignableFrom(clazz)) {
                Method method = clazz.getMethod("parseFrom", byte[].class);
                return method.invoke(null, data);
            } else {
                Codec codec = CodecFactory.get(clazz);
                return codec.decode(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T decodeArray(byte[] data, boolean compressed, Class<T> clazz) {
        byte[] bytes = compressed ? decompress(data) : data;
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        int length = buf.readIntLE();
        T array = (T) Array.newInstance(clazz.getComponentType(), length);
        for (int i = 0; i < length; i++) {
            int itemType = buf.readIntLE();
            int itemLength = buf.readIntLE();
            byte[] itemData = new byte[itemLength];
            buf.readBytes(itemData);
            Object item = decode(itemType, itemData, null);
            Array.set(array, i, item);
        }
        return array;
    }

    private static SimpleValue encode(byte[] value) {
        if (value.length > COMPRESSION_THRESHOLD) {
            return new SimpleValue(DT_COMPRESSED_BYTE_ARRAY, compress(value, value.length));
        } else {
            return new SimpleValue(DT_BYTE_ARRAY, value);
        }
    }

    private static SimpleValue encode(boolean value) {
        return new SimpleValue(DT_BOOL, value ? new byte[]{1} : new byte[]{0});
    }

    private static SimpleValue encode(Boolean value) {
        return new SimpleValue(DT_BOOL, value ? new byte[]{1} : new byte[]{0});
    }

    private static SimpleValue encode(boolean[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (boolean item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_BOOL_ARRAY, DT_COMPRESSED_BOOL_ARRAY);
    }

    private static SimpleValue encode(Boolean[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Boolean item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_BOOL_ARRAY, DT_COMPRESSED_BOOL_ARRAY);
    }

    private static SimpleValue encode(int value) {
        return new SimpleValue(DT_INT32, toBytesLE(value));
    }

    private static SimpleValue encode(Integer value) {
        return new SimpleValue(DT_INT32, toBytesLE(value));
    }

    private static SimpleValue encode(int[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (int item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_INT32_ARRAY, DT_COMPRESSED_INT32_ARRAY);
    }

    private static SimpleValue encode(Integer[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Integer item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_INT32_ARRAY, DT_COMPRESSED_INT32_ARRAY);
    }

    private static SimpleValue encode(long value) {
        return new SimpleValue(DT_INT64, toBytesLE(value));
    }

    private static SimpleValue encode(Long value) {
        return new SimpleValue(DT_INT64, toBytesLE(value));
    }

    private static SimpleValue encode(long[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (long item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_INT_64_ARRAY, DT_COMPRESSED_INT64_ARRAY);
    }

    private static SimpleValue encode(Long[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Long item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_INT_64_ARRAY, DT_COMPRESSED_INT64_ARRAY);
    }

    private static SimpleValue encode(float value) {
        return new SimpleValue(DT_FLOAT, toBytesLE(Float.floatToIntBits(value)));
    }

    private static SimpleValue encode(Float value) {
        return new SimpleValue(DT_FLOAT, toBytesLE(Float.floatToIntBits(value)));
    }

    private static SimpleValue encode(float[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (float item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_FLOAT_ARRAY, DT_COMPRESSED_FLOAT_ARRAY);
    }

    private static SimpleValue encode(Float[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Float item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_FLOAT_ARRAY, DT_COMPRESSED_FLOAT_ARRAY);
    }

    private static SimpleValue encode(double value) {
        return new SimpleValue(DT_DOUBLE, toBytesLE(Double.doubleToLongBits(value)));
    }

    private static SimpleValue encode(Double value) {
        return new SimpleValue(DT_DOUBLE, toBytesLE(Double.doubleToLongBits(value)));
    }

    private static SimpleValue encode(double[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (double item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_DOUBLE_ARRAY, DT_COMPRESSED_DOUBLE_ARRAY);
    }

    private static SimpleValue encode(Double[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Double item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_DOUBLE_ARRAY, DT_COMPRESSED_DOUBLE_ARRAY);
    }

    private static SimpleValue encode(String value) {
        byte[] bytes = value.getBytes(CHARSET);
        if (bytes.length > COMPRESSION_THRESHOLD) {
            return new SimpleValue(DT_COMPRESSED_STRING, compress(bytes, bytes.length));
        } else {
            return new SimpleValue(DT_STRING, bytes);
        }
    }

    private static SimpleValue encode(String[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (String item : array) {
            SimpleValue value = encode(item);
            writeValue(buf, value);
        }

        return buildResult(buf, DT_STRING_ARRAY, DT_COMPRESSED_STRING_ARRAY);
    }

    private static SimpleValue buildResult(ByteBuf buf, int type, int compressedType) {
        int length = buf.readableBytes();
        if (length > COMPRESSION_THRESHOLD) {
            return new SimpleValue(compressedType, compress(buf.array(), length));
        } else {
            byte[] data = new byte[length];
            buf.readBytes(data);
            return new SimpleValue(type, data);
        }
    }

    private static void writeValue(ByteBuf buf, SimpleValue value) {
        buf.writeIntLE(value.getDataType());
        buf.writeIntLE(value.getData().length);
        buf.writeBytes(value.getData());
    }

    private static byte[] compress(byte[] data, int count) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             GZIPOutputStream gos = new GZIPOutputStream(os)) {
            gos.write(data, 0, count);
            gos.close();

            byte[] bytes = os.toByteArray();
            byte[] result = new byte[bytes.length + 4];
            System.arraycopy(toBytesLE(data.length), 0, result, 0, 4);
            System.arraycopy(bytes, 0, result, 4, bytes.length);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] decompress(byte[] data) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(data, 4, data.length - 4);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(is)) {
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = gis.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }

            os.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toBytesLE(int v) {
        return new byte[]{(byte) v, (byte) (v >> 8), (byte) (v >> 16), (byte) (v >> 24)};
    }

    private static byte[] toBytesLE(long v) {
        return new byte[]{(byte) v, (byte) (v >> 8), (byte) (v >> 16), (byte) (v >> 24),
                (byte) (v >> 32), (byte) (v >> 40), (byte) (v >> 48), (byte) (v >> 56)};
    }

    private static int toIntLE(byte[] bytes) {
        return (bytes[3]) << 24
                | (bytes[2] & 0xff) << 16
                | (bytes[1] & 0xff) << 8
                | (bytes[0] & 0xff);
    }

    private static long toLongLE(byte[] bytes) {
        return (long) (bytes[7]) << 56
                | (long) (bytes[6] & 0xff) << 48
                | (long) (bytes[5] & 0xff) << 40
                | (long) (bytes[4] & 0xff) << 32
                | (long) (bytes[3] & 0xff) << 24
                | (long) (bytes[2] & 0xff) << 16
                | (long) (bytes[1] & 0xff) << 8
                | (long) (bytes[0] & 0xff);
    }
}
