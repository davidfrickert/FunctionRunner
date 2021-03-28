package pt.ist.photon_graal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.rest.RunnerService;
import pt.ist.photon_graal.rest.api.DTOFunctionArgs;
import pt.ist.photon_graal.rest.api.DTOFunctionExecute;
import pt.ist.photon_graal.runner.FunctionRunnerImpl;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final RunnerService runnerService = new RunnerService(new FunctionRunnerImpl());

    public static void main(String[] args) {
        String inputJson = args[0];

        LOGGER.info("Input: " + inputJson);

        String result;
        try {

            DTOFunctionArgs allArgs = mapper.readValue(inputJson, DTOFunctionArgs.class);

            DTOFunctionExecute executionInput = DTOFunctionExecute.of(allArgs);

            result = runnerService.execute(executionInput);
        } catch (Exception e) {
            result = e.getMessage();
        }

        returnValue(result);
    }

    private static void returnValue(Object value) {
        Object response;
        if (value == null) {
            response = "";
        } else if (value instanceof JsonNode) {
            response = value;
        } else {
            ObjectNode root = mapper.createObjectNode();

            root.put("result", value.toString());

            response = root;
        }
        LOGGER.info("Output: " + response.toString());
        System.out.println(response.toString());
    }
}
