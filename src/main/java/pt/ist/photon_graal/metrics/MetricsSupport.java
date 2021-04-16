package pt.ist.photon_graal.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.PushGateway;
import pt.ist.photon_graal.settings.CurrentSettings;

import javax.inject.Inject;
import java.io.IOException;

public class MetricsSupport {

    private final MeterRegistry registry;
    private final PushGateway pushGateway;

    @Inject
    public MetricsSupport(final MetricsConfig metricsConfig) {
        this.registry = initRegistry(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
        this.pushGateway = new PushGateway(metricsConfig.getPushAddress());
    }

    public MeterRegistry getMeterRegistry() {
        return registry;
    }

    public PrometheusMeterRegistry getPromMeterRegistry() {
        return (PrometheusMeterRegistry) getMeterRegistry();
    }

    public void push() throws IOException {
        pushGateway.pushAdd(getPromMeterRegistry().getPrometheusRegistry(), "serverless-func");
    }

    private MeterRegistry initRegistry(MeterRegistry registry) {
        registry.config().commonTags("function.name", CurrentSettings.VALUE.toString());
        return registry;
    }

}
