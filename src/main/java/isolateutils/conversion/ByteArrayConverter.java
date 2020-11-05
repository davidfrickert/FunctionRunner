package isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.nio.ByteBuffer;

public class ByteArrayConverter implements TypeConverter<Byte[]> {

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate, Byte[] bytes) {
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
        return ObjectHandles.getGlobal().create(copy);
    }

    @Override
    public Class<Byte[]> getType() {
        return Byte[].class;
    }
}
