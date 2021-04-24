package pt.ist.photon_graal.settings;

public final class CurrentSettings {
    public static final Settings VALUE;

    private CurrentSettings() { }

    static {
        Configuration configuration = Configuration.get();
        VALUE = new Settings(configuration.getFunctionClassFQN(), configuration.getFunctionMethod());
    }
}
