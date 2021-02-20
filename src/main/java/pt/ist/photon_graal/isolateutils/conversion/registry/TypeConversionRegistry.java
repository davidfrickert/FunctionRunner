package pt.ist.photon_graal.isolateutils.conversion.registry;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import pt.ist.photon_graal.isolateutils.conversion.TypeConverter;

import java.util.NoSuchElementException;

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
        TypeConverter<T> typeConverter = registry.get(t)
                .orElseThrow(() -> new NoSuchElementException("No type converter found for type " + t.getClass().getName()));

        System.out.println(typeConverter.getClass().getName());

        return typeConverter
                .createHandle(targetIsolate, t);
    }
}
