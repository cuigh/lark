package lark.net.rpc.server;

import lark.net.rpc.RequestMessage;

/**
 * @author cuigh
 */
public interface InvokeContext {
    RequestMessage getRequest();

    Object getUser();

    void setUser(Object user);

//    private static final AttributeKey<Object> USER_ATTRIBUTE_KEY = AttributeKey.newInstance("user");
//    private Channel channel;
//    private ServiceMethod method;
//    private RequestMessage request;

//    Context interface {
//        Action() Action
//        Context() ct.Context
//        SetContext(ctx ct.Context)
}
