package lark.net.rpc;

import lark.core.lang.BusinessException;

/**
 * @author cuigh
 */
public interface ResponseMessage {
    /**
     * 返回一个失败的响应消息
     *
     * @param id    消息 ID
     * @param error 异常
     * @return 响应消息
     */
    static ResponseMessage failure(long id, BusinessException error) {
        return new ResponseMessage() {
            @Override
            public long getId() {
                return id;
            }

            @Override
            public Object decode(Class<?> type) {
                throw error;
            }
        };
    }

    /**
     * 获取消息 ID
     *
     * @return ID
     */
    long getId();


    /**
     * 获取返回值
     *
     * @param type 数据类型
     * @return 返回值
     */
    Object decode(Class<?> type);
}
