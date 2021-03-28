package pt.ist.photon_graal.rest.api;

import pt.ist.photon_graal.settings.Settings;
import pt.ist.photon_graal.settings.CurrentSettings;

public class DTOFunctionExecute {
    private final String classFQN;
    private final String methodName;
    private final Object[] args;

    private final Boolean isStatic;

    private DTOFunctionExecute(Settings functionSettings, DTOFunctionArgs args) {
        this(functionSettings.getClassFQN(), functionSettings.getMethodName(), functionSettings.isStatic(), args.getArgs());
    }

    private DTOFunctionExecute(String classFQN, String methodName, boolean isStatic, Object[] args) {
        this.classFQN = classFQN;
        this.methodName = methodName;
        this.args = args;
        this.isStatic = isStatic;
    }

    public String getClassFQN() {
        return classFQN;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    /**
     * Defines if the function to execute is static or not
     * If function is not static a public no-args constructor is required to initialize an instance of the class
     *
     * Default: false
     */

    public boolean isStatic() {
        return isStatic != null && isStatic;
    }

    public static DTOFunctionExecute of(Settings functionSettings, DTOFunctionArgs args) {
        return new DTOFunctionExecute(functionSettings, args);
    }

    public static DTOFunctionExecute of(DTOFunctionArgs args) {
        return new DTOFunctionExecute(CurrentSettings.VALUE, args);
    }

    public static DTOFunctionExecute of(String classFQN, String methodName, boolean isStatic, Object[] args) {
        return new DTOFunctionExecute(classFQN, methodName, isStatic, args);
    }
}
