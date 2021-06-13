package pt.ist.photon_graal.config.framework;


import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.photon_graal.metrics.MetricsConfig;

public class Configuration implements MetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private final TypedProperties config;

    private static Configuration INSTANCE;

    private Configuration()  {
        config = new TypedProperties();
        try (InputStream fis = Configuration.class.getClassLoader().getResourceAsStream("config.properties")) {
            config.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't initialize application configuration!", e);
        }
        logger.info("Successful load of properties file");
    }

    public static synchronized Configuration get() {
        if(INSTANCE == null) {
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    @Override
    public String getPushHost() {
        return config.getProperty("prometheus.push.host", "localhost");
    }

    @Override
    public int getPushPort() {
        return config.getIntOrDefault("prometheus.push.port", 8778);
    }

    @Override
    public String getPushAddress() {
        return getPushHost() + ":" + getPushPort();
    }

    public String getFunctionClassFQN() {
        return config.getProperty("function.class");
    }

    public String getFunctionMethod() {
        return config.getProperty("function.method");
    }

    public boolean isFunctionStatic() {
        return Optional.ofNullable(config.getBoolean("function.static")).orElse(false);
    }
}
