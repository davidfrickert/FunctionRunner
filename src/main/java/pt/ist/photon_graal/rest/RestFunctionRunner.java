package pt.ist.photon_graal.rest;

import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ist.photon_graal.rest.api.DTOFunctionExecute;
import pt.ist.photon_graal.runner.FunctionRunner;
import pt.ist.photon_graal.runner.isolateutils.IsolateError;

import java.io.IOException;

@RestController
public class RestFunctionRunner {
    @Autowired
    private FunctionRunner functionRunner;

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @PostMapping(value = "/execute")
    public @ResponseBody
    String compressNoIsolate (@RequestBody DTOFunctionExecute input) throws IOException, ClassNotFoundException {

        logger.debug("Received [{}] as input from REST", input);

        Either<IsolateError, String> result = functionRunner.run(input.getClassFQN(), input.getMethodName(), input.getArgs());

        if (result.isRight()) {
            return result.get();
        } else {
            return result.getLeft().toString();
        }
    }
}
