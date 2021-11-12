package lark.pb;

import com.google.protobuf.*;
import lark.core.lang.EnumValuable;
import lark.pb.field.FieldType;

import java.io.IOException;

/**
 * @author cuigh
 */
@SuppressWarnings("unchecked")
public final class CodecUtils {
    public static final String CODEC_CLASS_SUFFIX = "$$CODEC";
    private static final int TAG_TYPE_BITS = 3;

    public static int makeTag(final int fieldNumber, final int wireType) {
        return (fieldNumber << TAG_TYPE_BITS) | wireType;
    }

    public static String getCodecTypeName(final Class<?> cls) {
        return cls.getName() + CODEC_CLASS_SUFFIX;
    }

    /**
     * Write a single tag-value pair to the stream.
     *
     * @param output The output stream.
     * @param type   The field's type.
     * @param number The field's number.
     * @param value  Object representing the field's value. Must be of the exact type which would be returned by
     *               {@link Message#getField(Descriptors.FieldDescriptor)} for this field.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeElement(final CodedOutputStream output, final WireFormat.FieldType type, final int number,
                                    final Object value) throws IOException {
        output.writeTag(number, getWireFormatForFieldType(type, false));
        writeElementNoTag(output, type, value);
    }


    /**
     * Write a field of arbitrary type, without its tag, to the stream.
     *
     * @param output The output stream.
     * @param type   The field's type.
     * @param value  Object representing the field's value. Must be of the exact type which would be returned by
     *               {@link Message#getField(Descriptors.FieldDescriptor)} for this field.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeElementNoTag(final CodedOutputStream output, final WireFormat.FieldType type,
                                         final Object value) throws IOException {
        switch (type) {
            case DOUBLE:
                output.writeDoubleNoTag((Double) value);
                break;
            case FLOAT:
                output.writeFloatNoTag((Float) value);
                break;
            case INT64:
                output.writeInt64NoTag((Long) value);
                break;
            case UINT64:
                output.writeUInt64NoTag((Long) value);
                break;
            case INT32:
                output.writeInt32NoTag((Integer) value);
                break;
            case FIXED64:
                output.writeFixed64NoTag((Long) value);
                break;
            case FIXED32:
                output.writeFixed32NoTag((Integer) value);
                break;
            case BOOL:
                output.writeBoolNoTag((Boolean) value);
                break;
            case STRING:
                output.writeStringNoTag((String) value);
                break;
            case MESSAGE:
                writeObject(output, 0, FieldType.MESSAGE, value, false, false);
                break;
            case BYTES:
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString) value);
                } else {
                    output.writeByteArrayNoTag((byte[]) value);
                }
                break;
            case UINT32:
                output.writeUInt32NoTag((Integer) value);
                break;
            case SFIXED32:
                output.writeSFixed32NoTag((Integer) value);
                break;
            case SFIXED64:
                output.writeSFixed64NoTag((Long) value);
                break;
            case SINT32:
                output.writeSInt32NoTag((Integer) value);
                break;
            case SINT64:
                output.writeSInt64NoTag((Long) value);
                break;
            case ENUM:
                if (value instanceof Internal.EnumLite) {
                    output.writeEnumNoTag(((Internal.EnumLite) value).getNumber());
                } else {
                    if (value instanceof EnumValuable) {
                        output.writeEnumNoTag(((EnumValuable) value).value());
                    } else if (value instanceof java.lang.Enum) {
                        output.writeEnumNoTag(((java.lang.Enum) value).ordinal());
                    } else {
                        output.writeEnumNoTag((Integer) value);
                    }
                }
                break;
        }
    }

    /**
     * Compute the number of bytes that would be needed to encode a single tag/value pair of arbitrary type.
     *
     * @param type   The field's type.
     * @param number The field's number.
     * @param value  Object representing the field's value. Must be of the exact type which would be returned by
     *               {@link Message#getField(Descriptors.FieldDescriptor)} for this field.
     * @return the int
     */
    public static int computeElementSize(final WireFormat.FieldType type, final int number, final Object value) {
        int tagSize = CodedOutputStream.computeTagSize(number);
        return tagSize + computeElementSizeNoTag(type, value);
    }

    /**
     * Compute the number of bytes that would be needed to encode a particular value of arbitrary type, excluding tag.
     *
     * @param type  The field's type.
     * @param value Object representing the field's value. Must be of the exact type which would be returned by
     *              {@link Message#getField(Descriptors.FieldDescriptor)} for this field.
     * @return the int
     */
    public static int computeElementSizeNoTag(final WireFormat.FieldType type, final Object value) {
        switch (type) {
            // Note: Minor violation of 80-char limit rule here because this would
            // actually be harder to read if we wrapped the lines.
            case DOUBLE:
                return CodedOutputStream.computeDoubleSizeNoTag((Double) value);
            case FLOAT:
                return CodedOutputStream.computeFloatSizeNoTag((Float) value);
            case INT64:
                return CodedOutputStream.computeInt64SizeNoTag((Long) value);
            case UINT64:
                return CodedOutputStream.computeUInt64SizeNoTag((Long) value);
            case INT32:
                return CodedOutputStream.computeInt32SizeNoTag((Integer) value);
            case FIXED64:
                return CodedOutputStream.computeFixed64SizeNoTag((Long) value);
            case FIXED32:
                return CodedOutputStream.computeFixed32SizeNoTag((Integer) value);
            case BOOL:
                return CodedOutputStream.computeBoolSizeNoTag((Boolean) value);
            case STRING:
                return CodedOutputStream.computeStringSizeNoTag((String) value);
            case BYTES:
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString) value);
                } else {
                    return CodedOutputStream.computeByteArraySizeNoTag((byte[]) value);
                }
            case UINT32:
                return CodedOutputStream.computeUInt32SizeNoTag((Integer) value);
            case SFIXED32:
                return CodedOutputStream.computeSFixed32SizeNoTag((Integer) value);
            case SFIXED64:
                return CodedOutputStream.computeSFixed64SizeNoTag((Long) value);
            case SINT32:
                return CodedOutputStream.computeSInt32SizeNoTag((Integer) value);
            case SINT64:
                return CodedOutputStream.computeSInt64SizeNoTag((Long) value);
            case MESSAGE:
                if (value instanceof LazyField) {
                    return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField) value);
                } else {
                    return computeObjectSizeNoTag(value);
                }
            case ENUM:
                if (value instanceof Internal.EnumLite) {
                    return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite) value).getNumber());
                } else {
                    if (value instanceof EnumValuable) {
                        return CodedOutputStream.computeEnumSizeNoTag(((EnumValuable) value).value());
                    } else if (value instanceof java.lang.Enum) {
                        return CodedOutputStream.computeEnumSizeNoTag(((java.lang.Enum) value).ordinal());
                    }
                    return CodedOutputStream.computeEnumSizeNoTag((Integer) value);
                }
        }

        throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
    }

    /**
     * Compute object size no tag.
     *
     * @param value the value
     * @return the int
     */
    public static int computeObjectSizeNoTag(Object value) {
        int size = 0;
        if (value == null) {
            return size;
        }

        Class cls = value.getClass();
        Codec codec = CodecFactory.get(cls);
        size = codec.size(value);
        size = size + CodedOutputStream.computeUInt32SizeNoTag(size);
        return size;
    }

    /**
     * Read a field of any primitive type for immutable messages from a CodedInputStream. Enums and embedded
     * messages are not handled by this method.
     *
     * @param input     The stream from which to read.
     * @param type      Declared type of the field.
     * @param checkUtf8 When true, check that the input is valid utf8.
     * @return An object representing the field's value, of the exact type which would be returned by
     * {@link Message#getField(Descriptors.FieldDescriptor)} for this field.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Object readPrimitiveField(CodedInputStream input, final WireFormat.FieldType type, boolean checkUtf8)
            throws IOException {
        switch (type) {
            case DOUBLE:
                return input.readDouble();
            case FLOAT:
                return input.readFloat();
            case INT64:
                return input.readInt64();
            case UINT64:
                return input.readUInt64();
            case INT32:
                return input.readInt32();
            case FIXED64:
                return input.readFixed64();
            case FIXED32:
                return input.readFixed32();
            case BOOL:
                return input.readBool();
            case STRING:
                if (checkUtf8) {
                    return input.readStringRequireUtf8();
                } else {
                    return input.readString();
                }
            case BYTES:
                return input.readBytes();
            case UINT32:
                return input.readUInt32();
            case SFIXED32:
                return input.readSFixed32();
            case SFIXED64:
                return input.readSFixed64();
            case SINT32:
                return input.readSInt32();
            case SINT64:
                return input.readSInt64();
            case MESSAGE:
                throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
            case ENUM:
                // We don't handle enums because we don't know what to do if the
                // value is not recognized.
                throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
        }

        throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
    }

    /**
     * Given a field type, return the wire type.
     *
     * @param type     the type
     * @param isPacked the is packed
     * @return the wire format for field type
     */
    static int getWireFormatForFieldType(final WireFormat.FieldType type, boolean isPacked) {
        if (isPacked) {
            return WireFormat.WIRETYPE_LENGTH_DELIMITED;
        } else {
            return type.getWireType();
        }
    }

    /**
     * Write object to byte array by {@link FieldType}.
     *
     * @param out     the out
     * @param order   the order
     * @param type    the type
     * @param o       the o
     * @param list    the list
     * @param withTag the with tag
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeObject(CodedOutputStream out, int order, FieldType type, Object o, boolean list,
                                   boolean withTag) throws IOException {
        if (o == null) {
            return;
        }

        if (type == FieldType.MESSAGE) {
            Class cls = o.getClass();
            Codec codec = CodecFactory.get(cls);
            if (withTag) {
                out.writeUInt32NoTag(makeTag(order, WireFormat.WIRETYPE_LENGTH_DELIMITED));
            }
            out.writeUInt32NoTag(codec.size(o));
            codec.encodeTo(o, out);
            return;
        }

        if (type == FieldType.BOOL) {
            if (withTag) {
                out.writeBool(order, (Boolean) o);
            } else {
                out.writeBoolNoTag((Boolean) o);
            }
        } else if (type == FieldType.BYTES) {
            byte[] bb = (byte[]) o;
            if (withTag) {
                out.writeBytes(order, ByteString.copyFrom(bb));
            } else {
                out.writeBytesNoTag(ByteString.copyFrom(bb));
            }
        } else if (type == FieldType.DOUBLE) {
            if (withTag) {
                out.writeDouble(order, (Double) o);
            } else {
                out.writeDoubleNoTag((Double) o);
            }
        } else if (type == FieldType.FIXED32) {
            if (withTag) {
                out.writeFixed32(order, (Integer) o);
            } else {
                out.writeFixed32NoTag((Integer) o);
            }
        } else if (type == FieldType.FIXED64) {
            if (withTag) {
                out.writeFixed64(order, (Long) o);
            } else {
                out.writeFixed64NoTag((Long) o);
            }
        } else if (type == FieldType.FLOAT) {
            if (withTag) {
                out.writeFloat(order, (Float) o);
            } else {
                out.writeFloatNoTag((Float) o);
            }
        } else if (type == FieldType.INT32) {
            if (withTag) {
                out.writeInt32(order, (Integer) o);
            } else {
                out.writeInt32NoTag((Integer) o);
            }
        } else if (type == FieldType.INT64) {
            if (withTag) {
                out.writeInt64(order, (Long) o);
            } else {
                out.writeInt64NoTag((Long) o);
            }
        } else if (type == FieldType.SFIXED32) {
            if (withTag) {
                out.writeSFixed32(order, (Integer) o);
            } else {
                out.writeSFixed32NoTag((Integer) o);
            }
        } else if (type == FieldType.SFIXED64) {
            if (withTag) {
                out.writeSFixed64(order, (Long) o);
            } else {
                out.writeSFixed64NoTag((Long) o);
            }
        } else if (type == FieldType.SINT32) {
            if (withTag) {
                out.writeSInt32(order, (Integer) o);
            } else {
                out.writeSInt32NoTag((Integer) o);
            }
        } else if (type == FieldType.SINT64) {
            if (withTag) {
                out.writeSInt64(order, (Long) o);
            } else {
                out.writeSInt64NoTag((Long) o);
            }
        } else if (type == FieldType.STRING) {
            if (withTag) {
                out.writeBytes(order, ByteString.copyFromUtf8(String.valueOf(o)));
            } else {
                out.writeBytesNoTag(ByteString.copyFromUtf8(String.valueOf(o)));
            }
        } else if (type == FieldType.UINT32) {
            if (withTag) {
                out.writeUInt32(order, (Integer) o);
            } else {
                out.writeUInt32NoTag((Integer) o);
            }
        } else if (type == FieldType.UINT64) {
            if (withTag) {
                out.writeUInt64(order, (Long) o);
            } else {
                out.writeUInt64NoTag((Long) o);
            }
        } else if (type == FieldType.ENUM) {
            int value = 0;
            if (o instanceof EnumValuable) {
                value = ((EnumValuable) o).value();
            } else if (o instanceof java.lang.Enum) {
                value = ((java.lang.Enum) o).ordinal();
            }
            if (withTag) {
                out.writeEnum(order, value);
            } else {
                out.writeEnumNoTag(value);
            }
        }
    }
}
