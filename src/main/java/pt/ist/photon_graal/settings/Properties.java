package pt.ist.photon_graal.settings;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import pt.ist.photon_graal.metrics.MetricsConfig;

import java.io.File;

public class Properties implements MetricsConfig {

    private final Configuration config;

    public Properties() throws ConfigurationException {
        config = new Configurations().properties(new File("config.properties"));
    }

    @Override
    public String getPushHost() {
        return config.getString("prometheus.push.host", "localhost");
    }

    @Override
    public int getPushPort() {
        return config.getInt("prometheus.push.port", 8778);
    }

    @Override
    public String getPushAddress() {
        return getPushHost() + ":" + getPushPort();
    }
}
