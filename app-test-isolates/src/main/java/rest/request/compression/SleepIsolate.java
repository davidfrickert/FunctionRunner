package rest.request.compression;

import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import isolateutils.conversion.registry.TypeConversionRegistry;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static minio.Minio.*;

final class SleepIsolate {

    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }

    @CEntryPoint
    static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        long time) {
        final Runtime runtime = Runtime.getRuntime();

        // System.out.println("(Runtime)Isolate total memory: " + runtime.totalMemory() / (1024 * 1024) + "MB");
        //System.out.println("(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

        //System.out.println("(MXBeans)Isolate committed memory: " +  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / (1024 * 1024) + "MB");
        //System.out.println("(MXBeans)Isolate used memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");
        final MinioClient minioClient = get();
        try {
            final GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET).object("file.txt").build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException | RuntimeException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = "waited " +
                time +
                "ms";

        final ObjectHandle handle = getRegistry()
                .createHandle(parentIsolate,
                        output);
        //System.out.println("(Runtime)END Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
        return handle;
    }
}
