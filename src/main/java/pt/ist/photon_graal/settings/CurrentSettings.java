package pt.ist.photon_graal.settings;

public final class CurrentSettings {
    public static final Settings VALUE;

    private CurrentSettings() { }

    static {
        Configuration conf = Configuration.get();
        VALUE = new Settings(conf.getFunctionClassFQN(), conf.getFunctionMethod(), conf.isFunctionStatic());
    }
}
