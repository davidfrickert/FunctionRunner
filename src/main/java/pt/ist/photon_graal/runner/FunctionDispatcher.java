package pt.ist.photon_graal.runner;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Either;
import pt.ist.photon_graal.runner.isolateutils.base.Enviroment;
import pt.ist.photon_graal.runner.isolateutils.error.IsolateError;

public class FunctionDispatcher implements FunctionRunner {

	private FunctionRunner runner;

	public FunctionDispatcher() {
		if (Enviroment.isNative()) {
			runner = new FunctionRunnerImpl();
		} else {
			runner = new MockFunctionRunner();
		}
	}

	@Override
	public <T> Either<IsolateError, T> run(String className, String methodName, JsonNode args) {
		return runner.run(className, methodName, args);
	}
}