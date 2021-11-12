package lark.net.rpc.client;

import lark.core.lang.EnumValuable;

/**
 * @author cuigh
 */
public enum InvokeMode implements EnumValuable {
    /**
     * selects another server automatically
     */
    FAIL_OVER(1),
    /**
     * returns error immediately
     */
    FAIL_FAST(2);
//  FAIL_SAFE,
//  BROARDCAST(3);

    private int value;

    InvokeMode(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }
}
