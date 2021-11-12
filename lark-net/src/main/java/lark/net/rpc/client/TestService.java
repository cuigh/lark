package lark.net.rpc.client;

import lark.pb.coder.User;

/**
 * @author cuigh
 */
public interface TestService {
    String echo(String s, Integer i);

    User find(boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8);

    void test();
//    boolean foo(String s1, String s2, String s3, String s4, String s5, String s6, String s7);
}

//        if (cls == boolean.class) {
//        return Boolean.class;
//        } else if (cls == byte.class) {
//        return Byte.class;
//        } else if (cls == char.class) {
//        return Character.class;
//        } else if (cls == short.class) {
//        return Short.class;
//        } else if (cls == int.class) {
//        return Integer.class;
//        } else if (cls == long.class) {
//        return Long.class;
//        } else if (cls == float.class) {
//        return Float.class;
//        } else if (cls == double.class) {
//        return Double.class;
//        }