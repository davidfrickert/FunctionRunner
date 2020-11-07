package isolateutils.handles;

import org.graalvm.nativeimage.ObjectHandle;

public class TypedHandle<T> {
    public final ObjectHandle handle;

    public TypedHandle(ObjectHandle handle) {
        this.handle = handle;
    }

    public static <T> TypedHandle<T> from(ObjectHandle handle) {
        return new TypedHandle<>(handle);
    }
}
