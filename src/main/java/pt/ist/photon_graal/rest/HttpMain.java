package pt.ist.photon_graal.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.rest.api.DTOFunctionArgs;
import pt.ist.photon_graal.rest.api.DTOFunctionExecute;
import pt.ist.photon_graal.rest.function.Settings;
import pt.ist.photon_graal.runner.FunctionRunnerImpl;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpMain {
    private static final Logger logger = LoggerFactory.getLogger(HttpMain.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final HttpServer server;
    private boolean initialized;

    public HttpMain(int port, Settings functionSettings) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), -1);

        final RunnerService rs = new RunnerService(new FunctionRunnerImpl());

        this.server.createContext("/init", new InitHandler());
        this.server.createContext("/run", new RunHandler(functionSettings, rs));
        this.server.setExecutor(null);
    }

    public void start() {
        this.server.start();
    }

    private static void writeResponse(HttpExchange t, int code, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(code, bytes.length);
        OutputStream os = t.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static void writeError(HttpExchange t, String errorMessage) throws IOException {
        ObjectNode message = mapper.createObjectNode();
        message.put("error", errorMessage);
        writeResponse(t, 502, message.toString());
    }

    private class InitHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (initialized) {
                String errorMessage = "Cannot initialize the action more than once.";
                System.err.println(errorMessage);
                HttpMain.writeError(exchange, errorMessage);
            } else {
                initialized = true;
                HttpMain.writeResponse(exchange, 200, "OK");
            }
        }
    }

    private class RunHandler implements HttpHandler {

        private final Settings functionSettings;
        private final RunnerService runnerService;

        public RunHandler(Settings functionSettings, RunnerService runnerService) {
            this.functionSettings = functionSettings;
            this.runnerService = runnerService;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                InputStream is = exchange.getRequestBody();

                DTOFunctionArgs args = mapper
                        .readValue(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)), DTOFunctionArgs.class);

                logger.debug(args.toString());

                DTOFunctionExecute execute = new DTOFunctionExecute(functionSettings, args);

                logger.debug(execute.toString());

                String result = runnerService.execute(execute);
                HttpMain.writeResponse(exchange, 200, result);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                HttpMain.writeError(exchange, "An error has occurred (see logs for details): " + e);
            } finally {
                writeLogMarkers();
            }
        }

        private void writeLogMarkers() {
            logger.debug("End of invocation. " + this.functionSettings);
        }
    }

    public static void main(String[] args) throws IOException {
        Settings functionSettings = new Settings("pt.ulisboa.ist.func.BaseFunction", "reverseString");
        HttpMain proxy = new HttpMain(8080, functionSettings);
        proxy.start();
    }
}
