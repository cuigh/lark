package lark.pb;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.UninitializedMessageException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @param <T> object type
 * @author cuigh
 */
public abstract class BaseCodec<T> implements Codec<T> {
    protected Descriptors.Descriptor descriptor;

    /**********
     * checkNull
     **********/

    protected static void checkNull(String field, Object value) {
        if (value == null) {
            ArrayList<String> missingFields = new ArrayList<>();
            missingFields.add(field);
            throw new UninitializedMessageException(missingFields);
        }
    }

    @Override
    public byte[] encode(T t) throws IOException {
        int size = size(t);
        final byte[] result = new byte[size];
        final CodedOutputStream output = CodedOutputStream.newInstance(result);
        encodeTo(t, output);
        return result;
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        CodedInputStream input = CodedInputStream.newInstance(bytes);
        return decodeFrom(input);
    }

    @Override
    public T decode(byte[] bytes, int offset, int length) throws IOException {
        CodedInputStream input = CodedInputStream.newInstance(bytes, offset, length);
        return decodeFrom(input);
    }


//    protected static void throwNullError(String field) {
//        ArrayList<String> missingFields = new ArrayList<>();
//        missingFields.add(field);
//        throw new UninitializedMessageException(missingFields);
//    }
}
