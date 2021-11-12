package lark.web.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lark.core.lang.BusinessException;
import lark.core.lang.Error;

/**
 * @author cuigh
 */
public class DataResult<T> {
    private int code;
    @JsonProperty("msg")
    private String message;
    private T data;

    public DataResult(T data) {
        this.data = data;
    }

    public DataResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public DataResult(BusinessException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public DataResult(Error e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
