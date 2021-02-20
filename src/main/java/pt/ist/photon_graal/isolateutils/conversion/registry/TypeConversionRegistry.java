package pt.ist.photon_graal.isolateutils.conversion.registry;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.isolateutils.conversion.TypeConverter;

import java.util.NoSuchElementException;

public class TypeConversionRegistry {
    private final RegistryMap registry;
    private static TypeConversionRegistry INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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
        TypeConverter<T> typeConverter = registry.get(t)
                .orElseThrow(() -> new NoSuchElementException("No type converter found for type " + t.getClass().getName()));

        logger.debug("Fetched converter with type {}", typeConverter.getClass().getName());

        return typeConverter.createHandle(targetIsolate, t);
    }
}
