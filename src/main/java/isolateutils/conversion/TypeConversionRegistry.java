package isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

import java.util.Optional;

public class TypeConversionRegistry {

    public static Optional<ObjectHandle> get(IsolateThread targetIsolate, Object o) {
        ObjectHandle handle;
        if (o instanceof String) {
            handle = StringConversion.createHandle(targetIsolate, (String) o);
        } else {
            handle = null;
        }
        return Optional.ofNullable(handle);
    }

}
