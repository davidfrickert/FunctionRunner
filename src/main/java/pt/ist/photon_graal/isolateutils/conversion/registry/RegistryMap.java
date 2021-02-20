package pt.ist.photon_graal.isolateutils.conversion.registry;

import pt.ist.photon_graal.isolateutils.conversion.BooleanConverter;
import pt.ist.photon_graal.isolateutils.conversion.ByteArrayConverter;
import pt.ist.photon_graal.isolateutils.conversion.StringConverter;
import pt.ist.photon_graal.isolateutils.conversion.TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, TypeConverter<?>> registry = new HashMap<>();

    public RegistryMap() {
        put(String.class, new StringConverter());
        put(byte[].class, new ByteArrayConverter());
        put(Boolean.class, new BooleanConverter());
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

