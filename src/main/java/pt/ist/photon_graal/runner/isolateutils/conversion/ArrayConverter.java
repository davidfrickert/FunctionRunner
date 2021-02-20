package pt.ist.photon_graal.runner.isolateutils.conversion;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class ArrayConverter implements TypeConverter<Object[]>{

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate, Object[] ts) {
        try (PinnedObject notUsed = PinnedObject.create(ts)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(ts);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] bytes = bos.toByteArray();

            try (PinnedObject pin = PinnedObject.create(bytes)) {
                return toJava(targetIsolate, pin.addressOfArrayElement(0), bytes.length);
            }
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
