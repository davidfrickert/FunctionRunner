package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.zip.GZIPOutputStream;

final class GZIPCompressionRequestIsolate {

    private static TypeConversionRegistry getRegistry() {
        return TypeConversionRegistry.getInstance();
    }

    @CEntryPoint
    static ObjectHandle execute(@IsolateThreadContext IsolateThread requestIsolate,
                                        IsolateThread parentIsolate,
                                        ObjectHandle byteArrayHandlerFile) {
        final Runtime runtime = Runtime.getRuntime();

        System.out.println("(Runtime)Isolate total memory: " + runtime.totalMemory() / (1024 * 1024) + "MB");
        System.out.println("(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

        System.out.println("(MXBeans)Isolate committed memory: " +  ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / (1024 * 1024) + "MB");
        System.out.println("(MXBeans)Isolate used memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");

        // Unwrap handles
        byte[] input = HandleUnwrapUtils.get(byteArrayHandlerFile);

        // Do computation logic
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(bos)) {
            gzipOut.write(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final byte[] output = bos.toByteArray();

        // return result as handle
        return getRegistry().createHandle(
                parentIsolate,
                output
        );
    }
}
