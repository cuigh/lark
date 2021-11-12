package lark.pb;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;

/**
 * 编解码接口
 *
 * @author cuigh
 */
public interface Codec<T> {
    /**
     * Do byte write action
     *
     * @param t generic target object
     * @return encoded byte array
     * @throws IOException if target object is invalid
     */
    byte[] encode(T t) throws IOException;

    /**
     * Do read action from byte array
     *
     * @param bytes encoded byte array
     * @return parse byte array to target object
     * @throws IOException if byte array is invalid
     */
    T decode(byte[] bytes) throws IOException;

    T decode(byte[] bytes, int offset, int length) throws IOException;

    /**
     * Calculate size of target object
     *
     * @param t target object
     * @return size of
     */
    int size(T t);

    /**
     * encode target object to byte array
     *
     * @param t      target object
     * @param output target {@link CodedOutputStream}
     * @throws IOException if target object is invalid
     */
    void encodeTo(T t, CodedOutputStream output) throws IOException;

    /**
     * decode object from target byte array input stream
     *
     * @param input target input stream object
     * @return unserialize object
     * @throws IOException if byte array is invalid
     */
    T decodeFrom(CodedInputStream input) throws IOException;
}
