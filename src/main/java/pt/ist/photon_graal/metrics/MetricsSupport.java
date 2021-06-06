package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.PushGateway;
import java.util.Optional;
import java.util.UUID;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.settings.CurrentSettings;

import java.io.IOException;
import pt.ist.photon_graal.settings.Configuration;

public class MetricsSupport {
    private static final Logger logger = LoggerFactory.getLogger(MetricsSupport.class);

    private final MeterRegistry registry;
    private final PushGateway pushGateway;

    private static MetricsSupport instance;

    private static final UUID runtimeIdentifier = UUID.randomUUID();

    private MetricsSupport(final MetricsConfig metricsConfig) {
        this.registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        this.pushGateway = new PushGateway(metricsConfig.getPushAddress());

        configureRegistryTags(registry);
    }

    @SneakyThrows
    public static MetricsSupport get() {
        if (instance == null) {
            instance = new MetricsSupport(Configuration.get());
        }
        return instance;
    }

    public static Optional<MetricsSupport> getIfAvailable() {
        return Optional.ofNullable(instance);
    }

    public MeterRegistry getMeterRegistry() {
        return registry;
    }

    public PrometheusMeterRegistry getPromMeterRegistry() {
        return (PrometheusMeterRegistry) getMeterRegistry();
    }

    public void push() {
        try {
            pushGateway.pushAdd(getPromMeterRegistry().getPrometheusRegistry(), runtimeIdentifier.toString());
        } catch (IOException e) {
            logger.warn("Couldn't push metrics to external system", e);
        }
    }

    public void delete() {
        try {
            pushGateway.delete(runtimeIdentifier.toString());
        } catch (IOException e) {
            logger.warn("Couldn't delete metrics from external system", e);
        }
    }

    private void configureRegistryTags(MeterRegistry registry) {
        registry.config()
                .commonTags("function.name", CurrentSettings.VALUE.simplifiedName()
                    , "framework", "photons@graal");
    }

}
