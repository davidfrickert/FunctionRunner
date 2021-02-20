package pt.ist.photon_graal.isolateutils.handles;

import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;

public class HandleUnwrapUtils {
    private static final ObjectHandles GLOBAL = ObjectHandles.getGlobal();
    public static <T> T get(ObjectHandle handle) {
        T t = GLOBAL.get(handle);
        GLOBAL.destroy(handle);
        return t;
    }

    public static String str(ObjectHandle handle) {
        return get(handle);
    }
}
