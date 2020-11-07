package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import isolateutils.handles.TypedHandle;
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
        final TypedHandle<byte[]> typedHandle = registry.convertToCType(
                requestIsolate,
                fis.readAllBytes()
        );
        // all data (passed to / received from) isolate must be C type in ObjectHandle
        final ObjectHandle handle = compressFileInIsolate(requestIsolate,
                currentIsolateThread,
                typedHandle.handle
        );

        Isolates.tearDownIsolate(requestIsolate);
        return ((ByteBuffer) HandleUnwrapUtils.get(handle)).array();
    }

    @CEntryPoint
    private static ObjectHandle compressFileInIsolate(@IsolateThreadContext IsolateThread requestIsolate,
                                                     IsolateThread parentIsolate,
                                                     ObjectHandle file) {
        ByteBuffer buf = HandleUnwrapUtils.get(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzOut = new GZIPOutputStream(out)) {
            gzOut.write(buf.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return getRegistry().convertToCType(
                parentIsolate,
                out.toByteArray()
        ).handle;
    }


}
