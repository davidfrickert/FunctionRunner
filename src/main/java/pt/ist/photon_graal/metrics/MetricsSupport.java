package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.Optional;

public class MetricsSupport {

    private static MeterRegistry INSTANCE;

    public static MeterRegistry getMeterRegistry() {
        return Optional
                .ofNullable(INSTANCE)
                .orElse(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
    }

    public static PrometheusMeterRegistry getMeterRegistryPrometheus() {
        return (PrometheusMeterRegistry) getMeterRegistry();
    }

}
