package lark.net.rpc.protocol.proto.data;

import lark.pb.annotation.ProtoField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cuigh
 */
@Getter
@Setter
public class Label {
    @ProtoField(order = 1, required = true)
    private String name;

    @ProtoField(order = 2, required = true)
    private String value;
}
