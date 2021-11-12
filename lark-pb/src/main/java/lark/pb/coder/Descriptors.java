package lark.pb.coder;

import com.google.protobuf.WireFormat;
import org.objectweb.asm.Type;

/**
 * General type descriptors.
 *
 * @author cuigh
 */
final class Descriptors {
    static final String WIRE_FORMAT_FIELD_TYPE = Type.getDescriptor(WireFormat.FieldType.class);
}
