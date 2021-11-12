package lark.net.rpc.client;

import lark.pb.coder.User;

/**
 * @author cuigh
 */
public class TestServiceProxy extends BaseService implements TestService {
    protected TestServiceProxy(Client client) {
        super(client, TestService.class);
    }

    @Override
    public String echo(String s, Integer i) {
        return (String) invoke("echo", new Object[]{s, i});
    }

    @Override
    public User find(boolean p1, byte p2, char p3, short p4, int p5, long p6, float p7, double p8) {
        return (User) invoke("find", new Object[]{p1, p2, p3, p4, p5, p6, p7, p8});
    }

    @Override
    public void test() {
        invoke("test", null);
    }

//    @Override
//    public boolean foo(String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
//        return (boolean) invoke("echo", new Object[]{s1, s2, s3, s4, s5, s6, s7});
//    }
}
