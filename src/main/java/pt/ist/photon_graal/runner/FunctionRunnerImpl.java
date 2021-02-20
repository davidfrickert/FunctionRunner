package pt.ist.photon_graal.runner;

import io.vavr.control.Either;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SerializationUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.ist.photon_graal.runner.isolateutils.IsolateError;
import pt.ist.photon_graal.runner.isolateutils.conversion.registry.TypeConversionRegistry;
import pt.ist.photon_graal.runner.isolateutils.handles.HandleUnwrapUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@Component
public class FunctionRunnerImpl implements FunctionRunner {

    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }

    @Override
    @SneakyThrows
    public <T> Either<IsolateError, T> run(String className, String methodName, Object... args) {
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
                        methodName
                ),
                getRegistry().createHandle(
                        requestIsolate,
                        args
                ));

        Isolates.tearDownIsolate(requestIsolate);
        byte[] bytes = HandleUnwrapUtils.get(result);

        return SerializationUtils.deserialize(bytes);
    }

    @CEntryPoint
    private static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        ObjectHandle classNameHandle,
                                        ObjectHandle methodNameHandle,
                                        ObjectHandle argsHandle) {
        try {

            String className = HandleUnwrapUtils.get(classNameHandle);
            String methodName = HandleUnwrapUtils.get(methodNameHandle);

            byte[] argsBytes = HandleUnwrapUtils.get(argsHandle);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(argsBytes);
            ObjectInputStream objStream = new ObjectInputStream(byteStream);

            Object[] args = (Object[]) objStream.readObject();

            Logger logger = getLogger();
            logger.debug("Received [{}] as argument", Arrays.toString(args));
            logger.debug("Function argument classes are [{}]", Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()));

            Class<?>[] argTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray((IntFunction<Class<?>[]>) Class[]::new);

            Class<?> klass = Class.forName(className);
            Method function = klass
                    .getDeclaredMethod(methodName, argTypes);

            Object functionInstance = klass.getConstructor().newInstance();

            Object result = function.invoke(functionInstance, args);

            return success(parentIsolate, result);
        } catch (Throwable t) {
            return error(parentIsolate, t);
        }

    }

    private static ObjectHandle error(IsolateThread receivingIsolate, Throwable error) {
        getLogger().debug("Error: ", error);
        return getRegistry().createHandle(
                receivingIsolate,
                Either.left(IsolateError.fromThrowableFull(error))
        );
    }

    private static ObjectHandle success(IsolateThread receivingIsolate, Object returnVal) {
        getLogger().debug("Success return: {}", returnVal);
        return getRegistry().createHandle(
                receivingIsolate,
                Either.right(returnVal)
        );
    }

    private static final Logger getLogger() {
       return LoggerFactory.getLogger(FunctionRunnerImpl.class);
    }
}
