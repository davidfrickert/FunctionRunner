package isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

public interface TypeConverter<T> {
    ObjectHandle createHandle(IsolateThread targetIsolate, T t);
}
