package pt.ist.photon_graal.runner.isolateutils.conversion;

import org.apache.commons.lang3.SerializationUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.io.*;
import java.nio.ByteBuffer;

public class ObjectConverter implements TypeConverter<Object> {

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate, Object o) {
          /*
        Holder for a pinned object, such that the object doesn't move until the pin is removed.
        The garbage collector treats pinned object specially to ensure that they are not moved or discarded.
        */
        try (PinnedObject pinOriginal = PinnedObject.create(o)) {
            byte[] serialized = SerializationUtils.serialize((Serializable) o);

            try (PinnedObject pin = PinnedObject.create(serialized)) {
                return toJava(targetIsolate, pin.addressOfArrayElement(0), serialized.length);
            }
        }
    }

    @CEntryPoint
    private static ObjectHandle toJava(@IsolateThreadContext IsolateThread targetIsolate,
                                       Pointer address, int length) {
        ByteBuffer direct = CTypeConversion.asByteBuffer(address, length);

        ByteBuffer copy = ByteBuffer.allocate(length);
        copy.put(direct).rewind();
        byte[] bytes = copy.array();

        /* Return handle for copied memory */
        return ObjectHandles.getGlobal().create(bytes);
    }
}
