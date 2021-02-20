package pt.ist.photon_graal.runner;

import io.vavr.control.Either;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import pt.ist.photon_graal.isolateutils.conversion.registry.TypeConversionRegistry;
import pt.ist.photon_graal.isolateutils.handles.HandleUnwrapUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.IntFunction;

public class FunctionRunnerImpl implements FunctionRunner {
    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }


    @Override
    public <T> Either<Throwable, T> run(String className, String methodName, Object... args) {
        IsolateThread currentIsolateThread = CurrentIsolate.getCurrentThread();
        IsolateThread requestIsolate = Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());

        final ObjectHandle result;

        result = FunctionRunnerImpl.execute(
                requestIsolate,
                currentIsolateThread,
                getRegistry().createHandle(
                        requestIsolate,
                        className
                ),
                getRegistry().createHandle(
                        requestIsolate,
                        className
                ),
                getRegistry().createHandle(
                        requestIsolate,
                        args
                ));

        Isolates.tearDownIsolate(requestIsolate);
        return HandleUnwrapUtils.get(result);
    }

    @CEntryPoint
    private static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        ObjectHandle classNameHandle,
                                        ObjectHandle methodNameHandle,
                                        ObjectHandle argsHandle) {
        String className = HandleUnwrapUtils.get(classNameHandle);
        String methodName = HandleUnwrapUtils.get(methodNameHandle);

        Object[] args = HandleUnwrapUtils.get(argsHandle);
        Class<?>[] argTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray((IntFunction<Class<?>[]>) Class[]::new);

        try {
            Method function = Class.forName(className)
                    .getDeclaredMethod(methodName, argTypes);

            Object result = function.invoke(null, args);

            return success(parentIsolate, result);
        } catch (Throwable t) {
            return error(parentIsolate, t);
        }

    }

    private static ObjectHandle error(IsolateThread receivingIsolate, Throwable error) {
        return getRegistry().createHandle(
                receivingIsolate,
                Either.left(error)
        );
    }

    private static ObjectHandle success(IsolateThread receivingIsolate, Object returnVal) {
        return getRegistry().createHandle(
                receivingIsolate,
                Either.right(returnVal)
        );
    }
}
