package isolateutils.registry;

import isolateutils.conversion.StringConversion;
import isolateutils.conversion.TypeConversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryMap {
    private final Map<Class<?>, Holder<?>> registry = new HashMap<>();

    public RegistryMap() {
        put(Holder.create(new StringConversion()));
    }

    private static class Holder<T> {
        private final Class<T> type;
        private final TypeConversion<T> typeConversion;

        public Holder(Class<T> type, TypeConversion<T> typeConversion) {
            this.type = type;
            this.typeConversion = typeConversion;
        }

        public static <X> Holder<X> create(TypeConversion<X> typeConversion) {
            return new Holder<>(typeConversion.getType(), typeConversion);
        }

        public TypeConversion<T> get() {
            return typeConversion;
        }
    }

    public <T> Optional<TypeConversion<T>> get(T t) {
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

