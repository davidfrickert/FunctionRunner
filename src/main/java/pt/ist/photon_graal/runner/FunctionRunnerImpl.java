package pt.ist.photon_graal.runner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.Timer;
import io.vavr.control.Either;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.data.ResultWrapper;
import pt.ist.photon_graal.data.Tuple;
import pt.ist.photon_graal.metrics.MetricsSupport;
import pt.ist.photon_graal.runner.isolateutils.conversion.registry.TypeConversionRegistry;
import pt.ist.photon_graal.runner.isolateutils.error.IsolateError;
import pt.ist.photon_graal.runner.isolateutils.handles.HandleUnwrapUtils;
import pt.ist.photon_graal.runner.isolateutils.management.IsolateStrategy;

public class FunctionRunnerImpl implements FunctionRunner {

    private final IsolateStrategy isolateStrategy;

    public FunctionRunnerImpl(IsolateStrategy isolateStrategy) {
        this.isolateStrategy = isolateStrategy;
    }

    public FunctionRunnerImpl() {
        this.isolateStrategy = IsolateStrategy.DEFAULT;
    }

    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }

    private static final MetricsSupport ms = MetricsSupport.get();

    public <T> Either<IsolateError, T> run(String className, String methodName, JsonNode args) {
        getLogger().info("Running request in Native environment! This runtime is a Native Image.");

        final var currentIsolateThread = CurrentIsolate.getCurrentThread();

        Timer.Sample isolateCreation = Timer.start();
        var requestIsolate = isolateStrategy.get();
        isolateCreation.stop(ms.getMeterRegistry().timer("isolate.create"));

        final ObjectHandle result;

        Timer.Sample inputConversion = Timer.start();
        final ObjectHandle classNameHandle = getRegistry().createHandle(requestIsolate, className);
        final ObjectHandle methodNameHandle = getRegistry().createHandle(requestIsolate, methodName);
        final ObjectHandle argsHandle = getRegistry().createHandle(requestIsolate, args);

        inputConversion.stop(ms.getMeterRegistry().timer("isolate.input_conversion"));

        Timer.Sample execution = Timer.start();
        result = FunctionRunnerImpl.execute(
            requestIsolate,
            currentIsolateThread,
            classNameHandle,
            methodNameHandle,
            argsHandle);
        execution.stop(ms.getMeterRegistry().timer("isolate.execution"));

        Timer.Sample tearDown = Timer.start();
        isolateStrategy.release(requestIsolate);
        tearDown.stop(ms.getMeterRegistry().timer("isolate.teardown"));

        Timer.Sample outputConversion = Timer.start();
        final Either<IsolateError, ResultWrapper<T>> output =
            SerializationUtils.deserialize((byte[]) HandleUnwrapUtils.get(result));
        outputConversion.stop(ms.getMeterRegistry().timer("isolate.output_conversion"));

        if (output.isRight()) {
            ResultWrapper<T> r = output.get();
            r.getStats().forEach(stat -> ms.getMeterRegistry().timer(stat._1()).record(stat._2()));
        }

        return output.map(ResultWrapper::getResult);
    }

    @CEntryPoint
    private static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        ObjectHandle classNameHandle,
                                        ObjectHandle methodNameHandle,
                                        ObjectHandle argsHandle) {
        try {
            List<Tuple<String, Duration>> stats = new ArrayList<>();

            Instant beforeUnwrap = Instant.now();

            String className = HandleUnwrapUtils.get(classNameHandle);
            String methodName = HandleUnwrapUtils.get(methodNameHandle);
            ObjectNode args = SerializationUtils.deserialize((byte[]) HandleUnwrapUtils.get(argsHandle));

            stats.add(new Tuple<>("isolate.inside.unwrap", Duration.between(beforeUnwrap, Instant.now())));
            /*
            Logger logger = getLogger();
            logger.debug("Received [{}] as argument", Arrays.toString(args));
            logger.debug("Function argument classes are [{}]", Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()));
             */

            Instant beforeFetchClasses = Instant.now();

            Class<?> klass = Class.forName(className);
            Method function = klass
                    .getDeclaredMethod(methodName, JsonNode.class);

            stats.add(new Tuple<>("isolate.inside.fetch_classes", Duration.between(beforeFetchClasses, Instant.now())));

            //Object functionInstance = klass.getConstructor().newInstance();

            Instant beforeExec = Instant.now();
            // TODO update this to run with instance if non-static method
            Object result = function.invoke(null, args);

            stats.add(new Tuple<>("isolate.inside.exec", Duration.between(beforeExec, Instant.now())));

            return success(parentIsolate, result, stats);
        } catch (Throwable t) {
            return error(parentIsolate, t);
        }

    }

    private static ObjectHandle error(IsolateThread receivingIsolate, Throwable error) {
        // getLogger().debug("Error: ", error);
        return getRegistry().createHandle(
                receivingIsolate,
                Either.left(IsolateError.fromThrowableFull(error))
        );
    }

    private static ObjectHandle success(IsolateThread receivingIsolate, Object returnVal, List<Tuple<String, Duration>> stats) {
        // getLogger().debug("Success return: {}", returnVal);
        return getRegistry().createHandle(
                receivingIsolate,
                Either.right(new ResultWrapper(stats, returnVal))
        );
    }

    private static Logger getLogger() {
       return LoggerFactory.getLogger(FunctionRunnerImpl.class);
    }
}
