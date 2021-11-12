package lark.core.lang;

/**
 * @author cuigh
 */
public interface Error {
    /**
     * Create a simple Error instance
     *
     * @param code    error code
     * @param message error message
     * @return Error instance
     */
    static Error of(int code, String message) {
        return new Error() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    /**
     * Acquire error code
     *
     * @return error code
     */
    int getCode();

    /**
     * Acquire error message
     *
     * @return error message
     */
    String getMessage();
}
