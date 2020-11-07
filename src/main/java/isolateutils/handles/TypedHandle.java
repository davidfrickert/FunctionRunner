package isolateutils.handles;

import lombok.Data;
import org.graalvm.nativeimage.ObjectHandle;

@Data
public class TypedHandle<T> {
    private final ObjectHandle handle;
    private final Class<? extends T> type;

    public static <T> TypedHandle<T> from(ObjectHandle handle, Class<? extends T> type) {
        return new TypedHandle<>(handle, type);
    }
}
