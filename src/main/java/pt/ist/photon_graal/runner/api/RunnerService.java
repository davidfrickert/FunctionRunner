package pt.ist.photon_graal.runner.api;

import io.micrometer.core.instrument.MeterRegistry;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.config.function.Settings;
import pt.ist.photon_graal.runner.api.data.DTOFunctionExecute;
import pt.ist.photon_graal.runner.api.error.IsolateError;
import pt.ist.photon_graal.runner.func.FunctionDispatcher;
import pt.ist.photon_graal.runner.func.FunctionRunner;

public class RunnerService {
    private final FunctionRunner functionRunner;

    public RunnerService(final Settings config,
                         final MeterRegistry registry) {
        this.functionRunner = new FunctionDispatcher(config, registry);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public Object execute(DTOFunctionExecute input) {

        logger.debug("Received [{}] as input from REST", input);

        Either<IsolateError, Object> result = functionRunner.run(input.getClassFQN(), input.getMethodName(), input.getArgs());

        return result.isRight() ? result.get() : result.getLeft().toString();
    }
}
