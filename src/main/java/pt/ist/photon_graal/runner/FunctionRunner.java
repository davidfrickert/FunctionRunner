package pt.ist.photon_graal.runner;

import io.vavr.control.Either;

public interface FunctionRunner {
    <T> Either<Throwable, T> run(String className, String methodName, Object... Args);
}
