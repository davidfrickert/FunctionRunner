package isolateutils.conversion.registry;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

public class TypeConversionRegistry {
    private final RegistryMap registry;
    private static TypeConversionRegistry INSTANCE;

    public static synchronized TypeConversionRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TypeConversionRegistry();
        }
        return INSTANCE;
    }

    private TypeConversionRegistry() {
        this.registry = new RegistryMap();
    }

    public <T> ObjectHandle createHandle(IsolateThread targetIsolate, T t) {
        final var typeConversion = registry.get(t);
        return typeConversion.orElseThrow().createHandle(targetIsolate, t);
    }
}
