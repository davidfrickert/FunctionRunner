package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.word.UnsignedWord;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressionRequest {
    private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    public static byte[] execInIsolate (MultipartFile file) throws IOException {
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

    public static byte[] exec (MultipartFile file) throws IOException {
        byte[] input = file.getBytes();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(bos)) {
            gzipOut.write(input);
        }

        return bos.toByteArray();
    }
}