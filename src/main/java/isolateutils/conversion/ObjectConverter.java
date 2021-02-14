package isolateutils.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.PinnedObject;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;

import java.io.*;
import java.nio.ByteBuffer;

public class ObjectConverter implements TypeConverter<Serializable> {

    @Override
    public ObjectHandle createHandle(IsolateThread targetIsolate, Serializable o) {
          /*
        Holder for a pinned object, such that the object doesn't move until the pin is removed.
        The garbage collector treats pinned object specially to ensure that they are not moved or discarded.
        */
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] objAsBytes = bos.toByteArray();

        try (PinnedObject pin = PinnedObject.create(objAsBytes)) {
            return toJava(targetIsolate, pin.addressOfArrayElement(0), objAsBytes.length);
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
