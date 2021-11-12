package lark.net.rpc.protocol.simple.data;

import lombok.Getter;
import lombok.Setter;
import lark.pb.annotation.ProtoField;

/**
 * @author cuigh
 */
@Getter
@Setter
public class SimpleLabel {
    @ProtoField(order = 1, required = true)
    private String name;

    @ProtoField(order = 2, required = true)
    private String value;
}
