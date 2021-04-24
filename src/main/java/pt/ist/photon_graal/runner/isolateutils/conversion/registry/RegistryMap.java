package pt.ist.photon_graal.runner.isolateutils.conversion.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.runner.isolateutils.conversion.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, TypeConverter<?>> registry = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public RegistryMap() {
        put(String.class, new StringConverter());
        put(byte[].class, new ByteArrayConverter());
        put(Boolean.class, new BooleanConverter());
        put(Object.class, new ObjectConverter());
        put(Object[].class, new ArrayConverter());
    }

    public <T> Optional<TypeConverter<T>> get(T t) {
        Class<?> aClass = t.getClass();
        TypeConverter result;
        if (registry.containsKey(aClass)) {
           result = registry.get(t.getClass());
        } else {
            result = registry.get(Object.class);
        }
        return Optional.ofNullable(result);
    }

    public <T> void put(Class<T> klass, TypeConverter<T> typeConverter) {
        registry.put(klass, typeConverter);
    }
}

