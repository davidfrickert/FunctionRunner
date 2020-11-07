package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

public class GZIPCompressionRequest {
    private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    public static TypeConversionRegistry getRegistry() {
        return registry != null ? registry : TypeConversionRegistry.getInstance();
    }

    public static byte[] compressFile(MultipartFile file) throws IOException {
        InputStream fis = file.getInputStream();

        IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
        IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

        // convert file byte[] to c type
        // all data (passed to / received from) isolate must be C type in ObjectHandle
        final ObjectHandle bytesResultHandle = compressFileInIsolate(requestIsolate,
                currentIsolateThread,
                registry.convertToCType(
                        requestIsolate,
                        fis.readAllBytes()
                )
        );

        Isolates.tearDownIsolate(requestIsolate);
        return HandleUnwrapUtils.get(bytesResultHandle);
    }

    @CEntryPoint
    private static ObjectHandle compressFileInIsolate(@IsolateThreadContext IsolateThread requestIsolate,
                                                     IsolateThread parentIsolate,
                                                     ObjectHandle file) {
        byte[] bytes = HandleUnwrapUtils.get(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzOut = new GZIPOutputStream(out)) {
            gzOut.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return getRegistry().convertToCType(
                parentIsolate,
                out.toByteArray()
        );
    }


}
