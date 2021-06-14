package pt.ist.photon_graal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.config.function.CurrentSettings;
import pt.ist.photon_graal.config.function.Settings;
import pt.ist.photon_graal.metrics.MemoryHelper;
import pt.ist.photon_graal.metrics.MetricsPusher;
import pt.ist.photon_graal.metrics.MetricsSupport;
import pt.ist.photon_graal.runner.api.DTOFunctionExecute;
import pt.ist.photon_graal.runner.api.RunnerService;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static pt.ist.photon_graal.rest.utils.Constants.mapper;

public class HttpMain {
	private static final Logger logger = LoggerFactory.getLogger(HttpMain.class);

	private final HttpServer server;
	private final MetricsPusher metricsPusher;
	private boolean initialized;
	private final MetricsSupport metricsSupport;

	private final Timer invocationTimer;
	private final AtomicInteger concurrentExecutions;
	private final AtomicInteger maxConcurrentExecutions;

	public HttpMain(int port, Settings functionSettings) throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(port), -1);

		final RunnerService rs = new RunnerService();

		this.server.createContext("/init", new InitHandler());
		this.server.createContext("/run", new RunHandler(functionSettings, rs));

		this.server.setExecutor(Executors.newCachedThreadPool());

		this.metricsSupport = MetricsSupport.get();
		this.metricsPusher = new MetricsPusher(metricsSupport);
		this.invocationTimer = metricsSupport.getMeterRegistry().timer("exec_time");

		metricsSupport.getMeterRegistry().gauge("memory_after", Collections.emptyList(), Runtime.getRuntime(),
												MemoryHelper::heapMemory);
		metricsSupport.getMeterRegistry().gauge("memory_rss", Collections.emptyList(), Runtime.getRuntime(),
												MemoryHelper::rssMemory);

		this.concurrentExecutions = metricsSupport.getMeterRegistry().gauge("concurrent_executions", new AtomicInteger(0));
		this.maxConcurrentExecutions = metricsSupport.getMeterRegistry().gauge("max_concurrent_executions", new AtomicInteger(0));

		Runtime.getRuntime().addShutdownHook(new Thread(() -> MetricsSupport.getIfAvailable().ifPresent(MetricsSupport::delete)));
	}

	public void start() {
		this.server.start();
		this.metricsPusher.start();
	}

	private static void writeResponse(HttpExchange t, int code, String content) {
		byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
		try {
			t.sendResponseHeaders(code, bytes.length);
			OutputStream os = t.getResponseBody();
			os.write(bytes);
			os.close();
		} catch (IOException e) {
			logger.error("Couldn't send response to caller", e);
		}
	}

	private static void writeError(HttpExchange t, String errorMessage) {
		ObjectNode message = mapper.createObjectNode();
		message.put("error", errorMessage);
		writeResponse(t, 502, message.toString());
	}

	private class InitHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) {
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
		public void handle(HttpExchange exchange) {
			concurrentExecutions.incrementAndGet();

			invocationTimer.record(() -> doHandle(exchange));
		}

		private void doHandle(HttpExchange exchange) {
			try {
				HttpMain.this.setMax();
				InputStream is = exchange.getRequestBody();

				Timer.Sample beforeExec = Timer.start();

				JsonNode inputJSON = mapper.readTree(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)))
										   // adhere to openwhisk specification
										   // input payload comes in "value" field
										   .get("value");
				is.close();

				DTOFunctionExecute execute = DTOFunctionExecute.of(functionSettings, inputJSON);

				logger.debug(execute.toString());
				beforeExec.stop(metricsSupport.getMeterRegistry().timer("proxy.parse_request"));

				Object invocationResult = runnerService.execute(execute);

				String restResult = metricsSupport.getMeterRegistry().timer("proxy.parse_response").record(() -> returnValue(invocationResult));

				HttpMain.this.concurrentExecutions.decrementAndGet();
				HttpMain.writeResponse(exchange, 200, restResult);
			} catch (Exception e) {
				logger.error("Fatal error occured while executing function!", e);
				HttpMain.writeError(exchange, "An error has occurred (see logs for details): " + e);
			} finally {
				writeLogMarkers();
			}
		}

		private void writeLogMarkers() {
			logger.debug("End of invocation. " + this.functionSettings);
		}
	}

	private void setMax() {
		final int current = concurrentExecutions.get();
		final int max = maxConcurrentExecutions.get();

		if (current > max)
			maxConcurrentExecutions.compareAndSet(max, current);
	}

	public static void main(String[] args) throws IOException {
		Settings functionSettings = CurrentSettings.VALUE;
		HttpMain proxy = new HttpMain(8080, functionSettings);
		proxy.start();
	}

	// TODO - use this function to dynamically use settings if they come in request
	private static Settings getSettingsForInvocation(ObjectNode input) throws JsonProcessingException {
		Settings settings;

		if (input.has("settings")) {
			settings = mapper.treeToValue(input.get("settings"), Settings.class);
		} else {
			settings = CurrentSettings.VALUE;
		}

		return settings;
	}

	private static String returnValue(Object value) {
		Object response;
		if (value == null) {
			ObjectNode root = mapper.createObjectNode();
			root.put("error", "Function return value is null");

			response = root;
		} else if (value instanceof JsonNode) {
			response = value;
		} else {
			ObjectNode root = mapper.createObjectNode();

			root.put("result", value.toString());

			response = root;
		}
		logger.info("Output: {}", response);
		return response.toString();
	}
}
