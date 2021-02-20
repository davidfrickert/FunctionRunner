package pt.ist.photon_graal.runner.isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.nio.ByteBuffer;

public class ByteArrayConverter implements TypeConverter<byte[]> {

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate, byte[] bytes) {
        /*
        Holder for a pinned object, such that the object doesn't move until the pin is removed.
        The garbage collector treats pinned object specially to ensure that they are not moved or discarded.
        */
        try (PinnedObject pin = PinnedObject.create(bytes)) {
            return toJava(targetIsolate, pin.addressOfArrayElement(0), bytes.length);
        }
    }

    @CEntryPoint
    private static ObjectHandle toJava(@CEntryPoint.IsolateThreadContext IsolateThread targetIsolate,
                                       Pointer address, int length) {
        ByteBuffer direct = CTypeConversion.asByteBuffer(address, length);

        ByteBuffer copy = ByteBuffer.allocate(length);
        copy.put(direct).rewind();
        byte[] bytes = copy.array();

        /* Return handle for copied memory */
        return ObjectHandles.getGlobal().create(bytes);
    }
}
