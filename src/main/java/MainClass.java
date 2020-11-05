import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import isolateutils.registry.TypeConversionRegistry;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

import static org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

public class MainClass {

    private TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

    private <T> ObjectHandle get(IsolateThread targetIsolate, T t) { return registry.get(targetIsolate, t); }

    @CEntryPoint
    private static ObjectHandle thisCodeIsolates(@IsolateThreadContext IsolateThread isolate,
                                                 IsolateThread parentIsolate,
                                                 ObjectHandle string1,
                                                 ObjectHandle string2) {
        TypeConversionRegistry registry = TypeConversionRegistry.getInstance();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put("str1", unwrapHandle(string1));
        json.put("str2", unwrapHandle(string2));

        String result = json.toPrettyString();
        return registry.get(parentIsolate, result);

    }

    private static String unwrapHandle(ObjectHandle handle) {
        Object rvalue = ObjectHandles.getGlobal().get(handle);
        ObjectHandles.getGlobal().destroy(handle);
        return rvalue.toString();
    }

    public String doInIsolation(String a, Object b) {
        long initialMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        IsolateThread newIsolate = Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
        IsolateThread currentThread = CurrentIsolate.getCurrentThread();

        ObjectHandle result = thisCodeIsolates(newIsolate,
                currentThread,
                get(newIsolate, a),
                get(newIsolate, b.toString())
        );
        String theRealResult = ObjectHandles.getGlobal().get(result);
        ObjectHandles.getGlobal().destroy(result);

        Isolates.tearDownIsolate(newIsolate);

        printMemoryUsage("Memory usage after isolate: ", initialMemory);


        return theRealResult;
    }



    private static long printMemoryUsage(String message, long initialMemory) {
        long currentMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        System.out.println(message + currentMemory / 1024 + " KByte" + (initialMemory == 0 ? "" : "  (difference: " + (currentMemory - initialMemory) / 1024 + " KByte)"));
        return currentMemory;
    }

    public static void main(String[] args) {
        MainClass m = new MainClass();
        String rst = m.doInIsolation("aaaaaaaa", new BigDecimal("1.0964"));
        System.out.println(rst);
    }

}
