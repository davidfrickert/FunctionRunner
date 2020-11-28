package rest.request.compression;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static minio.Minio.BUCKET;
import static minio.Minio.get;


public class Sleep {
    private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    public static String sleepIsolate (long time) {
        IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
        IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

        final ObjectHandle outputHandle = SleepIsolate.execute(
                requestIsolate,
                currentIsolateThread,
                time
        );

        String output = HandleUnwrapUtils.get(outputHandle);

        Isolates.tearDownIsolate(requestIsolate);

        return output;
    }

    public static String sleep (long time) {
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
        return output;
    }
}
