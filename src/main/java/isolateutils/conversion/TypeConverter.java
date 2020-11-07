package isolateutils.conversion;

import isolateutils.handles.TypedHandle;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

public interface TypeConverter<T> {
    TypedHandle<T> createHandle(IsolateThread targetIsolate, T t);
}
