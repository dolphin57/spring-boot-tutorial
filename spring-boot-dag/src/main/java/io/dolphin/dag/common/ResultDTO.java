package io.dolphin.dag.common;

import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;

/**
 * @author dolphin
 * @date 2024年03月14日 11:09
 * @description
 */
@Data
public class ResultDTO<T> implements Serializable {
    private boolean success;
    private T data;
    private String message;

    public static <T> ResultDTO<T> success(T data) {
        ResultDTO<T> r = new ResultDTO<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ResultDTO<T> failed(String message) {
        ResultDTO<T> r = new ResultDTO<>();
        r.success = false;
        r.message = message;
        return r;
    }

    public static <T> ResultDTO<T> failed(Throwable t) {
        return failed(ExceptionUtils.getStackTrace(t));
    }

}
