package lark.pb;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lark.pb.field.FieldType;

/**
 * @author cuigh
 */
@ProtoMessage
public class ProtoBasic2 {
    @ProtoField(order = 1, type = lark.pb.field.FieldType.DOUBLE, required = true)
    private double double1 = 1.0;

    @ProtoField(order = 2, type = lark.pb.field.FieldType.DOUBLE, required = true)
    private Double double2 = 1.0;

    @ProtoField(order = 3, type = lark.pb.field.FieldType.FLOAT, required = true)
    private float float1 = 1.0F;

    @ProtoField(order = 4, type = lark.pb.field.FieldType.FLOAT, required = true)
    private Float float2 = 1.0F;

    @ProtoField(order = 5, type = lark.pb.field.FieldType.INT64, required = true)
    private long long1 = 1;

    @ProtoField(order = 6, type = lark.pb.field.FieldType.INT64, required = true)
    private Long long2 = 1L;

    @ProtoField(order = 7, type = lark.pb.field.FieldType.UINT64, required = true)
    private long uLong1 = 1;

    @ProtoField(order = 8, type = lark.pb.field.FieldType.UINT64, required = true)
    private Long uLong2 = 1L;

    @ProtoField(order = 9, type = lark.pb.field.FieldType.INT32, required = true)
    private int integer1 = 1;

    @ProtoField(order = 10, type = lark.pb.field.FieldType.INT32, required = true)
    private Integer integer2 = 1;

    @ProtoField(order = 11, type = lark.pb.field.FieldType.FIXED64, required = true)
    private long fLong1 = 1;

    @ProtoField(order = 12, type = lark.pb.field.FieldType.FIXED64, required = true)
    private Long fLong2 = 1L;

    @ProtoField(order = 13, type = lark.pb.field.FieldType.FIXED32, required = true)
    private int fInteger1 = 1;

    @ProtoField(order = 14, type = lark.pb.field.FieldType.FIXED32, required = true)
    private Integer fInteger2 = 1;

    @ProtoField(order = 15, type = lark.pb.field.FieldType.BOOL, required = true)
    private boolean boolean1 = true;

    @ProtoField(order = 16, type = lark.pb.field.FieldType.BOOL, required = true)
    private Boolean boolean2 = true;

    @ProtoField(order = 17, type = lark.pb.field.FieldType.STRING, required = true)
    private String string = "1";

    @ProtoField(order = 19, type = lark.pb.field.FieldType.BYTES, required = true)
    private byte[] bytes = new byte[]{1};

    @ProtoField(order = 20, type = lark.pb.field.FieldType.UINT32, required = true)
    private int uInteger1 = 1;

    @ProtoField(order = 21, type = lark.pb.field.FieldType.UINT32, required = true)
    private Integer uInteger2 = 1;

    @ProtoField(order = 22, type = lark.pb.field.FieldType.SFIXED32, required = true)
    private int sFInteger1 = 1;

    @ProtoField(order = 23, type = lark.pb.field.FieldType.SFIXED32, required = true)
    private Integer sFInteger2 = 1;

    @ProtoField(order = 24, type = lark.pb.field.FieldType.SFIXED64, required = true)
    private long sFLong1 = 1;

    @ProtoField(order = 25, type = lark.pb.field.FieldType.SFIXED64, required = true)
    private Long sFLong2 = 1L;

    @ProtoField(order = 26, type = lark.pb.field.FieldType.SINT32, required = true)
    private int sInteger1 = 1;

    @ProtoField(order = 27, type = lark.pb.field.FieldType.SINT32, required = true)
    private Integer sInteger2 = 1;

    @ProtoField(order = 28, type = lark.pb.field.FieldType.SINT64, required = true)
    private long sLong1 = 1;

    @ProtoField(order = 29, type = FieldType.SINT64, required = true)
    private Long sLong2 = 1L;

    public double getDouble1() {
        return double1;
    }

    public void setDouble1(double double1) {
        this.double1 = double1;
    }

    public Double getDouble2() {
        return double2;
    }

    public void setDouble2(Double double2) {
        this.double2 = double2;
    }

    public float getFloat1() {
        return float1;
    }

    public void setFloat1(float float1) {
        this.float1 = float1;
    }

    public Float getFloat2() {
        return float2;
    }

    public void setFloat2(Float float2) {
        this.float2 = float2;
    }

    public long getLong1() {
        return long1;
    }

    public void setLong1(long long1) {
        this.long1 = long1;
    }

    public Long getLong2() {
        return long2;
    }

    public void setLong2(Long long2) {
        this.long2 = long2;
    }

    public long getULong1() {
        return uLong1;
    }

    public void setULong1(long uLong1) {
        this.uLong1 = uLong1;
    }

    public Long getULong2() {
        return uLong2;
    }

    public void setULong2(Long uLong2) {
        this.uLong2 = uLong2;
    }

    public int getInteger1() {
        return integer1;
    }

    public void setInteger1(int integer1) {
        this.integer1 = integer1;
    }

    public Integer getInteger2() {
        return integer2;
    }

    public void setInteger2(Integer integer2) {
        this.integer2 = integer2;
    }

    public long getFLong1() {
        return fLong1;
    }

    public void setFLong1(long fLong1) {
        this.fLong1 = fLong1;
    }

    public Long getFLong2() {
        return fLong2;
    }

    public void setFLong2(Long fLong2) {
        this.fLong2 = fLong2;
    }

    public int getFInteger1() {
        return fInteger1;
    }

    public void setFInteger1(int fInteger1) {
        this.fInteger1 = fInteger1;
    }

    public Integer getFInteger2() {
        return fInteger2;
    }

    public void setFInteger2(Integer fInteger2) {
        this.fInteger2 = fInteger2;
    }

    public boolean isBoolean1() {
        return boolean1;
    }

    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public Boolean getBoolean2() {
        return boolean2;
    }

    public void setBoolean2(Boolean boolean2) {
        this.boolean2 = boolean2;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getUInteger1() {
        return uInteger1;
    }

    public void setUInteger1(int uInteger1) {
        this.uInteger1 = uInteger1;
    }

    public Integer getUInteger2() {
        return uInteger2;
    }

    public void setUInteger2(Integer uInteger2) {
        this.uInteger2 = uInteger2;
    }

    public int getSFInteger1() {
        return sFInteger1;
    }

    public void setSFInteger1(int sFInteger1) {
        this.sFInteger1 = sFInteger1;
    }

    public Integer getSFInteger2() {
        return sFInteger2;
    }

    public void setSFInteger2(Integer sFInteger2) {
        this.sFInteger2 = sFInteger2;
    }

    public long getSFLong1() {
        return sFLong1;
    }

    public void setSFLong1(long sFLong1) {
        this.sFLong1 = sFLong1;
    }

    public Long getSFLong2() {
        return sFLong2;
    }

    public void setSFLong2(Long sFLong2) {
        this.sFLong2 = sFLong2;
    }

    public int getSInteger1() {
        return sInteger1;
    }

    public void setSInteger1(int sInteger1) {
        this.sInteger1 = sInteger1;
    }

    public Integer getSInteger2() {
        return sInteger2;
    }

    public void setSInteger2(Integer sInteger2) {
        this.sInteger2 = sInteger2;
    }

    public long getSLong1() {
        return sLong1;
    }

    public void setSLong1(long sLong1) {
        this.sLong1 = sLong1;
    }

    public Long getSLong2() {
        return sLong2;
    }

    public void setSLong2(Long sLong2) {
        this.sLong2 = sLong2;
    }
}
