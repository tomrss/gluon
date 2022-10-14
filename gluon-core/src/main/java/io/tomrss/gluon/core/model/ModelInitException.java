package io.tomrss.gluon.core.model;

public class ModelInitException extends RuntimeException {
    public ModelInitException() {
    }

    public ModelInitException(String message) {
        super(message);
    }

    public ModelInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelInitException(Throwable cause) {
        super(cause);
    }

    public ModelInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
