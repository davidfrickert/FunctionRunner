package isolateutils.conversion.registry;

import isolateutils.conversion.TypeConverter;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;

import java.util.Optional;

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

    public <T> ObjectHandle convertToCType(IsolateThread targetIsolate, T t) {
        final var typeConversion = registry.get(t);
        return typeConversion.orElseThrow().createHandle(targetIsolate, t);
    }
}
