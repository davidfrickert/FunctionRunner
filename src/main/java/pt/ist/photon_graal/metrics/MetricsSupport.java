package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import pt.ist.photon_graal.settings.CurrentSettings;

public class MetricsSupport {

    private static MeterRegistry INSTANCE;

    public static MeterRegistry getMeterRegistry() {
        if (INSTANCE == null)
            INSTANCE = initRegistry(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
        return INSTANCE;
    }

    public static PrometheusMeterRegistry getMeterRegistryPrometheus() {
        return (PrometheusMeterRegistry) getMeterRegistry();
    }

    private static MeterRegistry initRegistry(MeterRegistry registry) {
        registry.config().commonTags("function.name", CurrentSettings.VALUE.toString());
        return registry;
    }

}
