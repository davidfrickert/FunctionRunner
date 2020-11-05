package isolateutils.conversion.def;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

public class TypeConversionRegistryStatic {

    public static ObjectHandle get(IsolateThread targetIsolate, Object o) {
        ObjectHandle handle;
        if (o instanceof String) {
            handle = StringConversionStatic.createHandle(targetIsolate, (String) o);
        } else {
            throw new RuntimeException();
        }
        return handle;
    }

}
