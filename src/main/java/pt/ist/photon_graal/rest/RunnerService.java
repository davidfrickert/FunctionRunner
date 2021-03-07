package pt.ist.photon_graal.rest;

import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.rest.api.DTOFunctionExecute;
import pt.ist.photon_graal.runner.FunctionRunner;
import pt.ist.photon_graal.runner.isolateutils.IsolateError;


public class RunnerService {
    private FunctionRunner functionRunner;

    public RunnerService(FunctionRunner functionRunner) {
        this.functionRunner = functionRunner;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public String execute(DTOFunctionExecute input) {

        logger.debug("Received [{}] as input from REST", input);

        Either<IsolateError, String> result = functionRunner.run(input.getClassFQN(), input.getMethodName(), input.getArgs());

        if (result.isRight()) {
            return result.get();
        } else {
            return result.getLeft().toString();
        }
    }
}
