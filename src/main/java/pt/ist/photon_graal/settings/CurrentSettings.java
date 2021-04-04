package pt.ist.photon_graal.settings;

public final class CurrentSettings {
    private CurrentSettings() { }

    public static final Settings VALUE = new Settings("{{classFQN}}", "{{methodName}}");
}
