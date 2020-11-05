package isolateutils.conversion.registry;

import isolateutils.conversion.StringConverter;
import isolateutils.conversion.TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, Holder<?>> registry = new HashMap<>();

    public RegistryMap() {
        put(Holder.create(new StringConverter()));
    }

    private static class Holder<T> {
        private final Class<T> type;
        private final TypeConverter<T> typeConverter;

        public Holder(Class<T> type, TypeConverter<T> typeConverter) {
            this.type = type;
            this.typeConverter = typeConverter;
        }

        public static <X> Holder<X> create(TypeConverter<X> typeConverter) {
            return new Holder<>(typeConverter.getType(), typeConverter);
        }

        public TypeConverter<T> get() {
            return typeConverter;
        }
    }

    public <T> Optional<TypeConverter<T>> get(T t) {
        final var result = (Holder<T>) registry.get(t.getClass());
        return Optional.ofNullable(result.get());
    }

    public void put(Holder<?> holder) {
        registry.put(holder.type, holder);
    }

    public static void main(String[] args) {
        new RegistryMap().get(String.class);
    }
}

