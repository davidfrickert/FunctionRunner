package pt.ist.photon_graal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.rest.RunnerService;
import pt.ist.photon_graal.rest.api.DTOFunctionArgs;
import pt.ist.photon_graal.rest.api.DTOFunctionExecute;
import pt.ist.photon_graal.runner.FunctionRunnerImpl;
import pt.ist.photon_graal.settings.CurrentSettings;
import pt.ist.photon_graal.settings.Settings;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final RunnerService runnerService = new RunnerService(new FunctionRunnerImpl());

    public static void main(String[] args) {
        String inputJson = args[0];

        LOGGER.info("Input: " + inputJson);

        String result;
        try {
            ObjectNode input = mapper.readValue(inputJson, ObjectNode.class);

            DTOFunctionArgs allArgs = mapper.treeToValue(input, DTOFunctionArgs.class);

            DTOFunctionExecute executionInput = DTOFunctionExecute.of(getSettingsForInvocation(input), allArgs);

            result = runnerService.execute(executionInput);
        } catch (Exception e) {
            result = e.getMessage();
        }

        returnValue(result);
    }

    private static Settings getSettingsForInvocation(ObjectNode input) throws JsonProcessingException {
        Settings settings;

        if (input.has("settings")) {
            settings = mapper.treeToValue(input.get("settings"), Settings.class);
        } else {
            settings = CurrentSettings.VALUE;
        }

        return settings;
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
