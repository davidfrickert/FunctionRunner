package isolateutils.conversion.registry;

import isolateutils.conversion.ByteArrayConverter;
import isolateutils.conversion.StringConverter;
import isolateutils.conversion.TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, TypeConverter<?>> registry = new HashMap<>();

    public RegistryMap() {
        put(String.class, new StringConverter());
        put(byte[].class, new ByteArrayConverter());
    }


    public <T> Optional<TypeConverter<T>> get(T t) {
        final var result = (TypeConverter<T>) registry.get(t.getClass());
        if (result == null) {
            System.out.println("Miss: wanted " + t.getClass() + ", but got only: \n" + registry);
        }
        return Optional.ofNullable(result);
    }

    public <T> void put(Class<T> klass, TypeConverter<T> typeConverter) {
        registry.put(klass, typeConverter);
    }
}

