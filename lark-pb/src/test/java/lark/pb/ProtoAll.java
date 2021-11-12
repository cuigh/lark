package lark.pb;

import lark.core.lang.EnumValuable;
import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lark.pb.field.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/**
 * 测试对象, 包含支持的所有数据类型
 *
 * @author cuigh
 */
@ProtoMessage
@Getter
@Setter
public class ProtoAll {
    @ProtoField(order = 1, type = FieldType.INT32, required = true)
    private int f1;

    @ProtoField(order = 2, type = FieldType.INT32, required = true)
    private Integer f2;

    @ProtoField(order = 3, type = FieldType.INT64, required = true)
    private long f3;

    @ProtoField(order = 4, type = FieldType.INT64, required = true)
    private Long f4;

    @ProtoField(order = 5, type = FieldType.BOOL)
    private boolean f5;

    @ProtoField(order = 6, type = FieldType.BOOL)
    private Boolean f6;

    @ProtoField(order = 7, type = FieldType.INT64)
    private Date f7;

    @ProtoField(order = 8, type = FieldType.INT64)
    private LocalDateTime f8;

    @ProtoField(order = 9, type = FieldType.INT64)
    private LocalDate f9;

    @ProtoField(order = 10, type = FieldType.INT64)
    private LocalTime f10;

    @ProtoField(order = 14, type = FieldType.STRING)
    private String f14;

    @ProtoField(order = 15, type = FieldType.FLOAT)
    private float f15;

    @ProtoField(order = 16, type = FieldType.FLOAT)
    private Float f16;

    @ProtoField(order = 17, type = FieldType.DOUBLE)
    private double f17;

    @ProtoField(order = 18, type = FieldType.DOUBLE)
    private Double f18;

    @ProtoField(order = 19, type = FieldType.UINT32)
    private int f19;

    @ProtoField(order = 20, type = FieldType.UINT32)
    private int f20;

    @ProtoField(order = 21, type = FieldType.UINT64)
    private long f21;

    @ProtoField(order = 22, type = FieldType.UINT64)
    private Long f22;

    @ProtoField(order = 23, type = FieldType.FIXED32)
    private int f23;

    @ProtoField(order = 24, type = FieldType.FIXED32)
    private int f24;

    @ProtoField(order = 25, type = FieldType.FIXED64)
    private long f25;

    @ProtoField(order = 26, type = FieldType.FIXED64)
    private Long f26;

    @ProtoField(order = 27, type = FieldType.SFIXED32)
    private int f27;

    @ProtoField(order = 28, type = FieldType.SFIXED32)
    private int f28;

    @ProtoField(order = 29, type = FieldType.SFIXED64)
    private long f29;

    @ProtoField(order = 30, type = FieldType.SFIXED64)
    private Long f30;

    @ProtoField(order = 31, type = FieldType.SINT32)
    private int f31;

    @ProtoField(order = 32, type = FieldType.SINT32)
    private int f32;

    @ProtoField(order = 33, type = FieldType.SINT64)
    private long f33;

    @ProtoField(order = 34, type = FieldType.SINT64)
    private Long f34;

    @ProtoField(order = 35, type = FieldType.BYTES)
    private byte[] f35;

    @ProtoField(order = 36, type = FieldType.ENUM)
    private UserStatus f36;

    @ProtoField(order = 37, type = FieldType.ENUM)
    private DayOfWeek f37;

    @ProtoField(order = 38, type = FieldType.MESSAGE)
    private Address f38;

    @ProtoField(order = 39, type = FieldType.INT32)
    private List<Integer> f39;

    @ProtoField(order = 40, type = FieldType.INT64)
    private List<Long> f40;

    @ProtoField(order = 41, type = FieldType.DOUBLE)
    private List<Double> f41;

    @ProtoField(order = 42, type = FieldType.FLOAT)
    private List<Float> f42;

    @ProtoField(order = 43, type = FieldType.BOOL)
    private List<Boolean> f43;

    @ProtoField(order = 44, type = FieldType.STRING)
    private List<String> f44;

    @ProtoField(order = 45, type = FieldType.BYTES)
    private List<byte[]> f45;

    @ProtoField(order = 46, type = FieldType.INT64)
    private List<Date> f46;

    @ProtoField(order = 47, type = FieldType.INT64)
    private List<LocalDateTime> f47;

    @ProtoField(order = 48, type = FieldType.INT64)
    private List<LocalDate> f48;

    @ProtoField(order = 49, type = FieldType.INT64)
    private List<LocalTime> f49;

    @ProtoField(order = 50, type = FieldType.ENUM)
    private List<DayOfWeek> f50;

    @ProtoField(order = 51, type = FieldType.ENUM)
    private List<UserStatus> f51;

    @ProtoField(order = 52, type = FieldType.FIXED32)
    private List<Integer> f52;

    @ProtoField(order = 53, type = FieldType.FIXED64)
    private List<Long> f53;

    @ProtoField(order = 54, type = FieldType.MESSAGE)
    private List<Address> f54;

    @ProtoField(order = 55, type = FieldType.STRING)
    private BigDecimal f55;

    @ProtoField(order = 56, type = FieldType.STRING)
    private List<BigDecimal> f56;

    @ProtoField(order = 57, type = FieldType.ENUM)
    private Month f57;

    @ProtoField(order = 58, type = FieldType.ENUM)
    private List<Month> f58;

    @ProtoField(order = 59, type = FieldType.INT64)
    private ZonedDateTime f59;

    @ProtoField(order = 60, type = FieldType.INT64)
    private List<ZonedDateTime> f60;

    @ProtoField(order = 1, type = FieldType.MESSAGE)
    private Map<String, Integer> f61;

    @ProtoField(order = 2, type = FieldType.MESSAGE)
    private Map<String, Address> f62;

    @ProtoField(order = 3, type = FieldType.MESSAGE)
    private Map<String, UserStatus> f63;

    @ProtoField(order = 4, type = FieldType.MESSAGE)
    private Map<String, DayOfWeek> f64;

    public static ProtoAll getSample() {
        ProtoAll p = new ProtoAll();

        p.f1 = 1;
        p.f2 = 2;
        p.f3 = 3;
        p.f4 = 4L;
        p.f5 = true;
        p.f6 = true;
        p.f7 = new Date();
        p.f8 = LocalDateTime.now();
        p.f9 = LocalDate.now();
        p.f10 = LocalTime.now();
        p.f14 = "14";
        p.f15 = 15.1f;
        p.f16 = 16.1f;
        p.f17 = 17.1;
        p.f18 = 18.1;
        p.f19 = 19;
        p.f20 = 20;
        p.f21 = 21;
        p.f22 = 22L;
        p.f23 = 23;
        p.f24 = 24;
        p.f25 = 25;
        p.f26 = 26L;
        p.f27 = 27;
        p.f28 = 28;
        p.f29 = 29;
        p.f30 = 30L;
        p.f31 = 31;
        p.f32 = 32;
        p.f33 = 33;
        p.f34 = 34L;
        p.f35 = "35".getBytes();
        p.f36 = UserStatus.INVALID;
        p.f37 = DayOfWeek.FRIDAY;
        p.f38 = new Address("北京");
        p.f39 = Collections.singletonList(39);
        p.f40 = Collections.singletonList(40L);
        p.f41 = Collections.singletonList(41D);
        p.f42 = Collections.singletonList(42F);
        p.f43 = Collections.singletonList(true);
        p.f44 = Collections.singletonList("44");
        p.f45 = Collections.singletonList("45".getBytes());
        p.f46 = Collections.singletonList(new Date());
        p.f47 = Collections.singletonList(LocalDateTime.now());
        p.f48 = Collections.singletonList(LocalDate.now());
        p.f49 = Collections.singletonList(LocalTime.now());
        p.f50 = Collections.singletonList(DayOfWeek.FRIDAY);
        p.f51 = Collections.singletonList(UserStatus.INVALID);
        p.f52 = Collections.singletonList(39);
        p.f53 = Collections.singletonList(40L);
        p.f54 = Collections.singletonList(new Address("北京"));
        p.f55 = new BigDecimal("3.14159");
        p.f56 = Collections.singletonList(new BigDecimal("3.14159"));
        p.f57 = Month.APRIL;
        p.f58 = Collections.singletonList(Month.AUGUST);
        p.f59 = ZonedDateTime.now(ZoneOffset.UTC);
        p.f60 = Collections.singletonList(ZonedDateTime.now(ZoneOffset.UTC));

        p.f61 = new HashMap<>(3);
        p.f61.put("a", 1);
        p.f61.put("b", 2);
        p.f61.put("c", 3);

        p.f62 = new HashMap<>(2);
        p.f62.put("a", new Address("北京"));
        p.f62.put("b", new Address("上海"));

        p.f63 = new HashMap<>(2);
        p.f63.put("a", UserStatus.VALID);
        p.f63.put("b", UserStatus.INVALID);

        p.f64 = new HashMap<>(2);
        p.f64.put("a", DayOfWeek.FRIDAY);
        p.f64.put("b", DayOfWeek.SATURDAY);

        return p;
    }

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

    @Getter
    @Setter
    public static class Address {
        @ProtoField(order = 1, type = FieldType.STRING)
        private String f1;

        public Address() {
            // default ctor
        }

        public Address(String f1) {
            this.f1 = f1;
        }
    }
}
