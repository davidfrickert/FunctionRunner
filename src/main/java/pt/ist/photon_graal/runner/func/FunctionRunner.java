package pt.ist.photon_graal.runner.func;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Either;
import pt.ist.photon_graal.runner.api.error.IsolateError;

public interface FunctionRunner {
    <T> Either<IsolateError, T> run(String className, String methodName, JsonNode args);
}
