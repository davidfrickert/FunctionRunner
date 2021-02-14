package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

import java.lang.reflect.Method;

final class SleepIsolate {

    private static TypeConversionRegistry getRegistry() {
        return new TypeConversionRegistry();
    }

    @CEntryPoint
    static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        ObjectHandle func,
                                        ObjectHandle args) {
        TypeConversionRegistry conv = getRegistry();

        Method m = HandleUnwrapUtils.get(func);
        try {
            Object[] unwrappedArgs = HandleUnwrapUtils.get(args);

            Object result = m.invoke(null, unwrappedArgs);

            return conv.createHandle(parentIsolate, result);
        } catch (Exception e) {
            return conv.createHandle(parentIsolate, e);
        }
    }
}
