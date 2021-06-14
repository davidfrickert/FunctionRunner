package pt.ist.photon_graal.metrics.function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionMetrics {
    public static final class JsonFields {
        private JsonFields() { }

        public static final String METRICS = "metrics";
    }

    private final List<FunctionMetric> metrics;

    @JsonCreator
    public FunctionMetrics(@JsonProperty(JsonFields.METRICS) List<FunctionMetric> metrics) {
        this.metrics = metrics == null ? new ArrayList<>() : metrics;
    }

    public List<FunctionMetric> getMetrics() {
        return metrics;
    }
}
