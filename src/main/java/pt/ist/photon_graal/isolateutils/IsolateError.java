package pt.ist.photon_graal.isolateutils;

import org.springframework.lang.Nullable;

import java.io.Serializable;

public class IsolateError implements Serializable {
    private final String message;
    private final String exceptionClass;
    private final StackTraceElement[] stackTrace;

    private IsolateError cause;

    private IsolateError(String exceptionClass, String message, StackTraceElement[] stackTrace, IsolateError cause) {
        this(exceptionClass, message, stackTrace);
        this.cause = cause;
    }

    private IsolateError(String exceptionClass, String message, StackTraceElement[] stackTrace) {
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public static IsolateError fromThrowableFull(Throwable t) {
        IsolateError cause = fromThrowable(t.getCause());

        return new IsolateError(t.getClass().getName(), t.getMessage(), t.getStackTrace(), cause);
    }

    public static IsolateError fromThrowable(@Nullable Throwable t) {
        return t != null ? new IsolateError(t.getClass().getName(), t.getMessage(), t.getStackTrace()) : null;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public IsolateError getCause() {
        return cause;
    }
}
