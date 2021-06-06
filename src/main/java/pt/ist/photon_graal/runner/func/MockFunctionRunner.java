package pt.ist.photon_graal.runner.func;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.control.Either;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.data.ResultWrapper;
import pt.ist.photon_graal.data.Tuple;
import pt.ist.photon_graal.metrics.MetricsSupport;
import pt.ist.photon_graal.runner.api.error.IsolateError;

public class MockFunctionRunner implements FunctionRunner {
	private static Logger LOG = LoggerFactory.getLogger(MockFunctionRunner.class);

	public  <T> Either<IsolateError, T> run(String className, String methodName, JsonNode args) {
		LOG.warn("Running request in mock environment! This runtime is not a Native Image.");
		// serialization (Parent Isolate -> Child Isolate (that will be executing the function)
		final byte[] serializedClassName = SerializationUtils.serialize(className);
		final byte[] serializedMethodName = SerializationUtils.serialize(methodName);
		final byte[] serializedArgs = SerializationUtils.serialize((ObjectNode)args);

		final byte[] result = mockExecute(serializedClassName, serializedMethodName, serializedArgs);

		final Either<IsolateError, ResultWrapper<T>> resultDeserialized = SerializationUtils.deserialize(result);

		if (resultDeserialized.isRight()) {
			ResultWrapper<T> r = resultDeserialized.get();
			r.getStats().forEach(stat -> MetricsSupport.get().getMeterRegistry().timer(stat._1()).record(stat._2()));
		}
		return resultDeserialized.map(ResultWrapper::getResult);
	}

	private byte[] mockExecute(byte[] className,
							   byte[] methodName,
							   byte[] args) {
		try {
			// deserialization (Child Isolate deserializes the received inputs)
			final String deserializedClassName = SerializationUtils.deserialize(className);
			final String deserializedMethodName = SerializationUtils.deserialize(methodName);
			final JsonNode deserializedArgs = SerializationUtils.deserialize(args);

			List<Tuple<String, Duration>> stats = new ArrayList<>();

			Instant beforeFetchClasses = Instant.now();
			Class<?> klass = Class.forName(deserializedClassName);
			Method function = klass.getDeclaredMethod(deserializedMethodName, JsonNode.class);

			stats.add(new Tuple<>("isolate.inside.fetch_classes", Duration.between(beforeFetchClasses, Instant.now())));

			Object result = function.invoke(null, deserializedArgs);

			final Either<IsolateError, ResultWrapper> resultMock = Either.right(new ResultWrapper(stats, result));

			return SerializationUtils.serialize(resultMock);
		} catch (Throwable t) {
			return SerializationUtils.serialize(Either.left(IsolateError.fromThrowableFull(t)));
		}
	}
}
