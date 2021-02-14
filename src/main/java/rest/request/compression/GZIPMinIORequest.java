package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GZIPMinIORequest {
	private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();
	private static final Queue<Double> tearDownTimes = new ConcurrentLinkedQueue<>();

	public static boolean execInIsolate (String fileName) throws IOException {
		IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
		IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

		final ObjectHandle _result = GZIPMinIORequestIsolate.execute(
				requestIsolate,
				currentIsolateThread,
				registry.createHandle(
						requestIsolate,
						fileName
				)
		);
		boolean result = HandleUnwrapUtils.str(_result).equals("success");

		final long l = System.nanoTime();
		Isolates.tearDownIsolate(requestIsolate);
		double timeTaken = (System.nanoTime() - l) / (1_000_000_000.);
		tearDownTimes.add(timeTaken);

		System.out.println("Took " + timeTaken + "secs to destroy Isolate");
		return result;
	}

	public static Queue<Double> getLatencyValues() {
		return tearDownTimes;
	}
}
