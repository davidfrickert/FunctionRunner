package isolateutils.handles;

import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;

public class HandleUnwrapUtils {
    public static <T> T get(ObjectHandle handle) {
        return ObjectHandles.getGlobal().get(handle);
    }

    public static String str(ObjectHandle handle) {
        return get(handle);
    }
}
