package pt.ist.photon_graal.rest.api;

import pt.ist.photon_graal.rest.function.Settings;

public class DTOFunctionExecute {
    private final String classFQN;
    private final String methodName;
    private final Object[] args;

    private final Boolean isStatic;

    public DTOFunctionExecute(Settings functionSettings, DTOFunctionArgs args) {
        this(functionSettings.getClassFQN(), functionSettings.getMethodName(), functionSettings.isStatic(), args.getArgs());
    }

    public DTOFunctionExecute(String classFQN, String methodName, boolean isStatic, Object[] args) {
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
}
