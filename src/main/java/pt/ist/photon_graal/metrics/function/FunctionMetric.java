package pt.ist.photon_graal.metrics.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

public class FunctionMetric {
    public static final class JsonFields {
        private JsonFields() { }

        public static final String NAME = "name";
        public static final String DURATION = "value";
    }

    private final String name;
    private final Duration duration;

    @JsonCreator
    public FunctionMetric(@JsonProperty(JsonFields.NAME) final String name,
                          @JsonProperty(JsonFields.DURATION) final long duration) {
        this.name = name;
        this.duration = Duration.ofMillis(duration);
    }

    public String getName() {
        return name;
    }

    public Duration getDuration() {
        return duration;
    }
}
