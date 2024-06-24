package io.dolphin.dag.exception;

/**
 * @author dolphin
 * @date 2024年03月14日 11:35
 * @description 自定义运行时异常
 */
public class DolphinException extends RuntimeException {
    public DolphinException() {}

    public DolphinException(String message) {
        super(message);
    }

    public DolphinException(String message, Throwable cause) {
        super(message, cause);
    }

    public DolphinException(Throwable cause) {
        super(cause);
    }

    public DolphinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
