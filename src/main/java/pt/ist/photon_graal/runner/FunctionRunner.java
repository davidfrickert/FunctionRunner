package pt.ist.photon_graal.runner;

import io.vavr.control.Either;
import pt.ist.photon_graal.isolateutils.IsolateError;

public interface FunctionRunner {
    <T> Either<IsolateError, T> run(String className, String methodName, Object... Args);
}
