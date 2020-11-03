import isolateutils.conversion.TypeConversionRegistry;
import org.graalvm.nativeimage.*;
import org.graalvm.nativeimage.c.function.CEntryPoint;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static isolateutils.conversion.TypeConversionRegistry.*;
import static org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

public class Server {

    private static final int canYouSeeMeLol = 1;

    @CEntryPoint
    private static ObjectHandle thisCodeIsolates(@IsolateThreadContext IsolateThread ctx,
                                                 IsolateThread father,
                                                 ObjectHandle sharedHandle,
                                                 ObjectHandle a,
                                                 ObjectHandle b) {
        long initialMemory = printMemoryUsage("isolate initial memory usage: ", 0);

        String sharedStr = ObjectHandles.getGlobal().get(sharedHandle);
        ObjectHandles.getGlobal().destroy(sharedHandle);
        printMemoryUsage("Rendering isolate final memory usage: ", initialMemory);

        String result = String.join("+",
                Stream.of(
                        sharedStr,
                        unwrapHandle(a),
                        unwrapHandle(b),
                        String.valueOf(canYouSeeMeLol)
                ).collect(Collectors.toUnmodifiableList())
        );

        return get(father, result).orElseThrow();

    }

    private static String unwrapHandle(ObjectHandle handle) {
        Object rvalue = ObjectHandles.getGlobal().get(handle);
        ObjectHandles.getGlobal().destroy(handle);
        return rvalue.toString();
    }

    public static String doInIsolation(String a, Object b) {
        long initialMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        IsolateThread isolate = Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
        IsolateThread currentThread = CurrentIsolate.getCurrentThread();
        String notBatata = "cenoura";

        ObjectHandle stringHandle = get(isolate, notBatata).orElseThrow();
        ObjectHandle result = thisCodeIsolates(isolate, currentThread, stringHandle,
                get(isolate, a).orElseThrow(),
                get(isolate, b.toString()).orElseThrow()
        );
        String theRealResult = ObjectHandles.getGlobal().get(result);
        ObjectHandles.getGlobal().destroy(result);

        Isolates.tearDownIsolate(isolate);

        printMemoryUsage("Memory usage after isolate: ", initialMemory);


        return theRealResult;
    }



    private static long printMemoryUsage(String message, long initialMemory) {
        long currentMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        System.out.println(message + currentMemory / 1024 + " KByte" + (initialMemory == 0 ? "" : "  (difference: " + (currentMemory - initialMemory) / 1024 + " KByte)"));
        return currentMemory;
    }

    public static void main(String[] args) {
        String rst = doInIsolation("aaaaaaaa", new BigDecimal("1.0964"));
        System.out.println(rst);
    }

}
