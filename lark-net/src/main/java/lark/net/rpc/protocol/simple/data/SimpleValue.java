package lark.net.rpc.protocol.simple.data;

import lombok.Getter;
import lombok.Setter;
import lark.pb.annotation.ProtoField;

/**
 * Created by noname on 15/12/6.
 */
@Getter
@Setter
public class SimpleValue {
    @ProtoField(order = 1, required = true)
    private int dataType;

    @ProtoField(order = 2, required = true)
    private byte[] data;

    public SimpleValue() {
        // for decode
    }

    public SimpleValue(int dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }
}
