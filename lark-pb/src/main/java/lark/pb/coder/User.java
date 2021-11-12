package lark.pb.coder;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import lark.core.lang.EnumValuable;
import lark.pb.BaseCodec;
import lark.pb.CodecUtils;
import lark.pb.MapEntry;
import lark.pb.annotation.ProtoField;
import lark.pb.field.FieldType;
import lark.pb.helper.DateHelper;
import lark.pb.helper.EnumHelper;
import lark.pb.helper.EnumValuableHelper;
import lark.pb.helper.MessageHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.*;

/**
 * @author cuigh
 */
@Getter
@Setter
public class User {
    @ProtoField(order = 1, type = FieldType.INT32, required = true)
    private int id1;
    @ProtoField(order = 2, type = FieldType.INT32)
    private Integer id2;
    @ProtoField(order = 3, type = FieldType.INT64, required = true)
    private long id3;
    @ProtoField(order = 4, type = FieldType.INT64, required = true)
    private Long id4;
    @ProtoField(order = 5, type = FieldType.INT64, required = true)
    private Date f5;
    @ProtoField(order = 6, type = FieldType.ENUM)
    private UserStatus f6;
    @ProtoField(order = 7, type = FieldType.MESSAGE)
    private User f7;
    @ProtoField(order = 8, type = FieldType.INT32)
    private List<Integer> f8;
    @ProtoField(order = 9, type = FieldType.MESSAGE)
    private List<User> f9;
    @ProtoField(order = 10, type = FieldType.INT64)
    private List<Long> f10;
    @ProtoField(order = 11, type = FieldType.ENUM)
    private DayOfWeek f11;
    @ProtoField(order = 12)
    private Map<Integer, String> f12;

    public enum UserStatus implements EnumValuable {
        VALID(1), INVALID(2);

        private int value;

        UserStatus(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }

    public static class UserCodec extends BaseCodec<User> {

        @Override
        public int size(User value) {
            int totalSize = 0;

            totalSize += CodedOutputStream.computeInt32Size(1, value.getId1());
            if (value.getId2() != null) {
                totalSize += CodedOutputStream.computeInt32Size(2, value.getId2());
            }

            if (value.getF8() != null) {
                for (Integer t : value.getF8()) {
                    if (t != null) {
                        totalSize += CodedOutputStream.computeInt32SizeNoTag(t);
                    }
                }
                totalSize += value.getF8().size() * CodedOutputStream.computeTagSize(8);
            }

            if (value.getF12() != null) {
                for (Map.Entry<Integer, String> entry : value.getF12().entrySet()) {
                    int size = CodedOutputStream.computeInt32Size(1, entry.getKey()) +
                            CodedOutputStream.computeStringSize(2, entry.getValue());
                    totalSize += CodedOutputStream.computeUInt32SizeNoTag(size) + size;
                }
                totalSize += value.getF12().size() * CodedOutputStream.computeTagSize(12);
            }

            return totalSize;
        }

        @Override
        public void encodeTo(User user, CodedOutputStream output) throws IOException {
            output.writeInt32(1, user.getId1());
            if (user.getId2() != null) {
                output.writeInt32(2, user.getId2());
            }
            output.writeInt64(3, user.getId3());
            output.writeInt64(4, user.getId4());
            if (user.getF7() != null) {
                MessageHelper.write(output, 7, user.getF7(), User.class);
            }
            if (user.getF8() != null) {
                for (Integer item : user.getF8()) {
                    output.writeInt32(8, item);
                }
            }
            if (user.getF9() != null) {
                MessageHelper.writeList(output, 9, user.getF9(), User.class);
            }
            if (user.getF10() != null) {
                for (Long item : user.getF10()) {
                    output.writeInt64(10, item);
                }
            }
            if (user.getF12() != null) {
                for (Map.Entry<Integer, String> entry : user.getF12().entrySet()) {
                    output.writeTag(12, WireFormat.WIRETYPE_LENGTH_DELIMITED);
                    int size = CodedOutputStream.computeInt32Size(1, entry.getKey()) +
                            CodedOutputStream.computeStringSize(2, entry.getValue());
                    output.writeUInt32NoTag(size);
                    output.writeInt32(1, entry.getKey());
                    output.writeString(2, entry.getValue());
                }
            }
        }

        @Override
        public User decodeFrom(CodedInputStream input) throws IOException {
            User result = new User();
            while (!input.isAtEnd()) {
                int tag = input.readTag();
                switch (tag) {
                    case 3:
                        result.setId3(input.readInt64());
                        break;
                    case 4:
                        result.setId4(input.readInt64());
                        break;
                    case 5:
                        result.setF5(DateHelper.to(input.readInt64()));
                        break;
                    case 6:
                        result.setF6(EnumValuableHelper.to(input.readEnum(), UserStatus.class));
                        break;
                    case 7:
                        result.setF7(MessageHelper.read(input, User.class));
                        break;
                    case 8:
                        if ((result.getF8()) == null) {
                            List list = new ArrayList();
                            result.setF8(list);
                        }
                        result.getF8().add(input.readInt32());
                        break;
                    case 9:
                        if ((result.getF9()) == null) {
                            List list = new ArrayList();
                            result.setF9(list);
                        }
                        result.getF9().add(MessageHelper.read(input, User.class));
                        break;
                    case 11:
                        result.setF11(EnumHelper.to(input.readEnum(), DayOfWeek.values()));
                        break;
                    case 12:
//                        if ((result.getF12()) == null) {
//                            Map map = new HashMap();
//                            result.setF12(map);
//                        }
//                        MapEntry entry = new MapEntry(keyType, valueType, keyClass, valueClass);
//                        input.readMessage(entry.getParserForType(), null);
//                        result.getF12().put(entry.getKey(), entry.getValue());
                        break;
                }
            }
            return result;
        }

//        @Override
//        public Descriptors.Descriptor getDescriptor() throws IOException {
//            return null;
//        }

        // todo: 动态生成此方法
//        private static void validate(User value) {
//            if (value.getId4() == null) {
//                throwNullError("id4");
//            }
//        }
    }
}
