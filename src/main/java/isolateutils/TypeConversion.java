package isolateutils;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

public interface TypeConversion<T> {
    ObjectHandle createHandle(IsolateThread targetIsolate, T t);
    Class<T> getType();
}
