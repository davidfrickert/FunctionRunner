package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import pt.ist.photon_graal.config.function.Settings;

public class MetricsSupport {
    private final MeterRegistry registry;
    private final Settings settings;

    public MetricsSupport(final Settings functionConfig,
                          final MeterRegistry meterRegistry) {
        this.registry = meterRegistry;
        this.settings = functionConfig;
        configureRegistryTags(registry, functionConfig);
    }

    public MeterRegistry getMeterRegistry() {
        return registry;
    }

    public Settings getSettings() {
        return settings;
    }

    private void configureRegistryTags(final MeterRegistry registry, final Settings config) {
        registry.config()
                .commonTags("function.name", config.getClassFQN() + "::" + config.getMethodName()
                    , "framework", "photons@graal");
    }

}
