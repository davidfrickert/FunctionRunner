package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GZIPMinIORequest {
	private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

	public static boolean execInIsolate (String fileName) throws IOException {
		IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
		IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

		final boolean result = GZIPMinIORequestIsolate.execute(
				requestIsolate,
				currentIsolateThread,
				registry.createHandle(
						requestIsolate,
						fileName
				)
		);
		System.out.println(result);
		final long l = System.nanoTime();
		System.out.println("startTearDown");
		Isolates.tearDownIsolate(requestIsolate);
		System.out.println("Took " + ((l-System.nanoTime()) / (1_000_000_000)) + "secs to destroy Isolate");
		return result;
	}
}
