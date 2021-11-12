package lark.net.rpc.service;

import lark.core.lang.EnumValuable;
import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcService;
import lark.pb.annotation.ProtoField;

/**
 * Created by noname on 15/12/6.
 */
@RpcService(name = "$test", description = "测试服务")
public interface TestService {
    @RpcMethod(description = "测试无参数")
    void test();

    @RpcMethod(description = "测试 byte[] 参数")
    byte[] testByteArray(byte[] value);

    @RpcMethod(description = "测试 string 参数")
    String testString(String value);

    @RpcMethod(description = "测试 bool 参数")
    boolean testBool(boolean value);

    @RpcMethod(description = "测试 int32 参数")
    int testInt32(int value);

    @RpcMethod(description = "测试 int64 参数")
    long testInt64(long value);

    @RpcMethod(description = "测试 float 参数")
    float testFloat(float value);

    @RpcMethod(description = "测试 double 参数")
    double testDouble(double value);

    @RpcMethod(description = "测试 protobuf 参数")
    TestObject testProtobuf(TestObject value);

    @RpcMethod(description = "测试 bool[] 参数")
    boolean[] testBoolArray(boolean[] value);

    @RpcMethod(description = "测试 string[] 参数")
    String[] testStringArray(String[] value);

    @RpcMethod(description = "测试 int32[] 参数")
    Integer[] testInt32Array(Integer[] value);

    @RpcMethod(description = "测试 int64[] 参数")
    long[] testInt64Array(long[] value);


    @RpcMethod(description = "测试 float[] 参数")
    float[] testFloatArray(float[] value);

    @RpcMethod(description = "测试 double[] 参数")
    double[] testDoubleArray(double[] value);

    @RpcMethod(description = "测试整数除法")
    int testDivide(int n1, int n2);

    class TestObject {
        @ProtoField(order = 1, required = true)
        public TestObjectType type;

        @ProtoField(order = 2, required = true)
        public String name;

        @ProtoField(order = 3, required = true)
        public String value;
    }

    enum TestObjectType implements EnumValuable {
        SIMPLE(1), COMPLEX(2);

        private final int value;

        TestObjectType(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }
}
