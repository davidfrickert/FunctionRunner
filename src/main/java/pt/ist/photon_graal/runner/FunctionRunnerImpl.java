package pt.ist.photon_graal.runner;

import io.vavr.control.Either;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
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
import pt.ist.photon_graal.metrics.MetricsSupport;
import pt.ist.photon_graal.runner.isolateutils.IsolateError;
import pt.ist.photon_graal.runner.isolateutils.conversion.registry.TypeConversionRegistry;
import pt.ist.photon_graal.runner.isolateutils.handles.HandleUnwrapUtils;

public class FunctionRunnerImpl implements FunctionRunner {

    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }

    private static MetricsSupport ms;

    static {
        init();
    }

    @SneakyThrows
    private static void init() {
        ms = MetricsSupport.get();
    }

    @Override
    @SneakyThrows
    public <T> Either<IsolateError, T> run(String className, String methodName, Object... args) {
        var currentIsolateThread = CurrentIsolate.getCurrentThread();

        final IsolateThread requestIsolate = ms.getMeterRegistry().timer("isolate.create")
                                               .record(this::doCreateIsolate);

        final ObjectHandle result;
        Instant before = Instant.now();
        final ObjectHandle classNameHandle = getRegistry().createHandle(requestIsolate, className);
        final ObjectHandle methodNameHandle = getRegistry().createHandle(requestIsolate, methodName);
        final ObjectHandle argsHandle = getRegistry().createHandle(requestIsolate, args);
        Instant after = Instant.now();
        ms.getMeterRegistry().timer("isolate.input_conversion").record(Duration.between(before, after));

        result = ms.getMeterRegistry().timer("isolate.execution").record(() -> FunctionRunnerImpl.execute(
            requestIsolate,
            currentIsolateThread,
            classNameHandle,
            methodNameHandle,
            argsHandle));

        ms.getMeterRegistry().timer("isolate.teardown").record(() -> doTearDownIsolate(requestIsolate));

        return ms.getMeterRegistry().timer("isolate.output_conversion").record(() -> {
            byte[] bytes = HandleUnwrapUtils.get(result);
            return SerializationUtils.deserialize(bytes);
        });
    }

    private IsolateThread doCreateIsolate() {
        return Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
    }

    private void doTearDownIsolate(IsolateThread requestIsolate) {
        Isolates.tearDownIsolate(requestIsolate);
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

            //Object functionInstance = klass.getConstructor().newInstance();

            // TODO update this to run with instance if non-static method
            Object result = function.invoke(null, args);

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
