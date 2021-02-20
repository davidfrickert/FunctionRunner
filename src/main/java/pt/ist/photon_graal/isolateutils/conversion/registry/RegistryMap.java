package pt.ist.photon_graal.isolateutils.conversion.registry;

import pt.ist.photon_graal.isolateutils.conversion.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, TypeConverter<?>> registry = new HashMap<>();

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
                System.out.println("missed, attempting with superclass " + superclass);
                result = registry.get(superclass);
                superclass = superclass.getSuperclass();
            }

            if (result == null) {
                System.out.println("Miss: wanted " + t.getClass() + ", but got only: \n" + registry);
            } else {
                System.out.println("Great success!");
            }
        }
        return Optional.ofNullable(result);
    }

    public <T> void put(Class<T> klass, TypeConverter<T> typeConverter) {
        registry.put(klass, typeConverter);
    }
}

