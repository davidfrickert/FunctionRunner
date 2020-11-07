package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class GZIPCompressionRequest {
    private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    public static byte[] compressFile(MultipartFile file) throws IOException {
        IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
        IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

        // convert file byte[] to c type
        // all data (passed to / received from) isolate must be C type in ObjectHandle
        final ObjectHandle bytesResultHandle;
        bytesResultHandle = GZIPCompressionRequestIsolate.execute(
                requestIsolate,
                currentIsolateThread,
                registry.createHandle(
                        requestIsolate,
                        file.getBytes()
                )
        );

        Isolates.tearDownIsolate(requestIsolate);
        return HandleUnwrapUtils.get(bytesResultHandle);
    }
}
