package isolateutils.conversion;

import isolateutils.handles.TypedHandle;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class StringConverter implements TypeConverter<String> {

    @Override
    public TypedHandle<String> createHandle(IsolateThread targetIsolate,
                                    String s) {
        try (CTypeConversion.CCharPointerHolder cStringHolder = CTypeConversion.toCString(s)) {
            return toJava(targetIsolate, cStringHolder.get());
        }
    }

    @CEntryPoint
    private static TypedHandle<String> toJava(@CEntryPoint.IsolateThreadContext IsolateThread targetIsolate,
                                       CCharPointer cString) {
        /* Convert the C string to the target Java string. */
        String targetString = CTypeConversion.toJavaString(cString);
        /* Encapsulate the target string in a handle that can be returned back to the source isolate. */
        return TypedHandle.from(ObjectHandles.getGlobal().create(targetString));
    }
}
