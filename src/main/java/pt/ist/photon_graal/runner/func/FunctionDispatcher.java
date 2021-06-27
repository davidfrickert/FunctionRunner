package pt.ist.photon_graal.runner.func;

import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.MeterRegistry;
import io.vavr.control.Either;
import pt.ist.photon_graal.config.function.Settings;
import pt.ist.photon_graal.metrics.MetricsSupport;
import pt.ist.photon_graal.runner.utils.base.Enviroment;
import pt.ist.photon_graal.runner.api.error.IsolateError;

public class FunctionDispatcher implements FunctionRunner {

	private final FunctionRunner runner;

	public FunctionDispatcher(final Settings config,
							  final MeterRegistry registry) {
		final var metricsSupport = new MetricsSupport(config, registry);

		if (Enviroment.isNative()) {
			runner = new FunctionRunnerImpl(metricsSupport);
		} else {
			runner = new MockFunctionRunner(metricsSupport);
		}
	}

	@Override
	public <T> Either<IsolateError, T> run(String className, String methodName, JsonNode args) {
		return runner.run(className, methodName, args);
	}
}
