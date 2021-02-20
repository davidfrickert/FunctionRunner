package test.func;

import java.util.function.Function;

public class ToUpperCase implements Function<String, String> {
    @Override
    public String apply(String s) {
        return s.toUpperCase();
    }
}
