package pt.ist.photon_graal.isolateutils.conversion.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.isolateutils.conversion.*;

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
        TypeConverter result = registry.get(t.getClass());

        if (result == null) {
            Class<?> superclass = t.getClass().getSuperclass();
            while (result == null && superclass != null) {
                logger.debug("missed, attempting with superclass [{}]", superclass);
                result = registry.get(superclass);
                superclass = superclass.getSuperclass();
            }
        }

        if (result == null) {
            logger.warn("Miss. wanted [{}], but got only [{}]", t.getClass(), registry);
        }
        return Optional.ofNullable(result);
    }

    public <T> void put(Class<T> klass, TypeConverter<T> typeConverter) {
        registry.put(klass, typeConverter);
    }
}

