package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.PushGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.settings.CurrentSettings;

import java.io.IOException;
import pt.ist.photon_graal.settings.Configuration;

public class MetricsSupport {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private final MeterRegistry registry;
    private final PushGateway pushGateway;

    private static MetricsSupport INSTANCE;

    private MetricsSupport(final MetricsConfig metricsConfig) {
        this.registry = initRegistry(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
        this.pushGateway = new PushGateway(metricsConfig.getPushAddress());
    }

    public static MetricsSupport get() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new MetricsSupport(Configuration.get());
        }
        return INSTANCE;
    }

    public MeterRegistry getMeterRegistry() {
        logger.debug("getting meter registry");
        return registry;
    }

    public PrometheusMeterRegistry getPromMeterRegistry() {
        return (PrometheusMeterRegistry) getMeterRegistry();
    }

    public void push() {
        try {
            pushGateway.pushAdd(getPromMeterRegistry().getPrometheusRegistry(), CurrentSettings.VALUE.toString());
        } catch (IOException e) {
            logger.warn("Couldn't push metrics to external system", e);
        }
    }

    private MeterRegistry initRegistry(MeterRegistry registry) {
        registry.config().commonTags("function.name", CurrentSettings.VALUE.toString());
        return registry;
    }

}
