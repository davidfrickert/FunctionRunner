package pt.ist.photon_graal.runner.isolateutils.error;

import java.io.Serializable;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class IsolateError implements Serializable {
    private final String message;
    private final String exceptionClass;
    private final String stackTrace;

    private IsolateError cause;

    private IsolateError(String exceptionClass, String message, String stackTrace, IsolateError cause) {
        this(exceptionClass, message, stackTrace);
        this.cause = cause;
    }

    private IsolateError(String exceptionClass, String message, String stackTrace) {
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public static IsolateError fromThrowableFull(Throwable t) {
        IsolateError cause = fromThrowable(t.getCause());

        return new IsolateError(t.getClass().getName(), t.getMessage(), ExceptionUtils.getStackTrace(t), cause);
    }

    public static IsolateError fromThrowable(Throwable t) {
        return t != null ? new IsolateError(t.getClass().getName(), t.getMessage(), ExceptionUtils.getStackTrace(t)) : null;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public IsolateError getCause() {
        return cause;
    }

    public String getMessageWithExceptionClass() {
        return getExceptionClass() + " - " + getMessage();
    }

    @Override
    public String toString() {
        return "IsolateError{" +
                "message='" + message + '\'' +
                ", exceptionClass='" + exceptionClass + '\'' +
                ", stackTrace=" + stackTrace +
                ", cause=" + cause +
                '}';
    }
}
