package lark.net.rpc.client.balancer;

import lark.net.rpc.client.Node;

import java.util.List;

/**
 * @author cuigh
 */
public interface Balancer {
    /**
     * 名称
     *
     * @return 名称
     */
    String name();

    /**
     * 获取节点列表
     *
     * @return 节点列表
     */
    List<Node> getNodes();

    /**
     * 设置节点列表
     *
     * @param nodes 节点列表
     */
    void setNodes(List<Node> nodes);

    /**
     * 选择节点
     *
     * @return 待请求节点
     */
    Node select();
}
