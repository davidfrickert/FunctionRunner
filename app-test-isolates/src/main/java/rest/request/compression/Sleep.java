package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.Isolates.CreateIsolateParameters;
import org.graalvm.nativeimage.ObjectHandle;

import java.lang.reflect.Method;


public class Sleep {
    private static final TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    public static String sleepIsolate (long time) throws NoSuchMethodException {
        IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
        IsolateThread requestIsolate = Isolates.createIsolate(CreateIsolateParameters.getDefault());

        Method sleep = Sleep.class.getMethod("sleep", long.class);
        Object[] args = new Object[] {time};

        ObjectHandle methodHandle = registry.createHandle(requestIsolate, sleep);
        ObjectHandle timeHandle = registry.createHandle(requestIsolate, args);

        final ObjectHandle outputHandle = SleepIsolate.execute(
                requestIsolate,
                currentIsolateThread,
                methodHandle,
                timeHandle
        );

        String output = HandleUnwrapUtils.get(outputHandle);

        Isolates.tearDownIsolate(requestIsolate);

        return output;
    }

    public static boolean sleep(long n) {

        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
