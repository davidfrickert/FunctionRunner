package pt.ist.photon_graal.isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class StringConverter implements TypeConverter<String> {

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate,
                                     String s) {
        try (CTypeConversion.CCharPointerHolder cStringHolder = CTypeConversion.toCString(s)) {
            return toJava(targetIsolate, cStringHolder.get());
        }
    }

    @CEntryPoint
    private static ObjectHandle toJava(@CEntryPoint.IsolateThreadContext IsolateThread targetIsolate,
                                       CCharPointer cString) {
        /* Convert the C string to the target Java string. */
        String targetString = CTypeConversion.toJavaString(cString);
        /* Encapsulate the target string in a handle that can be returned back to the source isolate. */
        return ObjectHandles.getGlobal().create(targetString);
    }
}
