package io.tomrss.gluon.core.template;

public class GluonTemplateException extends RuntimeException {
    public GluonTemplateException() {
    }

    public GluonTemplateException(String message) {
        super(message);
    }

    public GluonTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GluonTemplateException(Throwable cause) {
        super(cause);
    }

    public GluonTemplateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
