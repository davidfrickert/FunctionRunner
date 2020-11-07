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
        put(new StringConverter());
        put(new ByteArrayConverter());
    }


    public <T> Optional<TypeConverter<T>> get(T t) {
        final var result = (TypeConverter<T>) registry.get(t.getClass());
        return Optional.ofNullable(result);
    }

    public void put(TypeConverter<?> typeConverter) {
        registry.put(typeConverter.getClass(), typeConverter);
    }
}

