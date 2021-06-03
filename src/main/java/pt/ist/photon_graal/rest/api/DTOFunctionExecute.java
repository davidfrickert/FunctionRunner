package pt.ist.photon_graal.rest.api;

import com.fasterxml.jackson.databind.JsonNode;
import pt.ist.photon_graal.settings.Settings;
import pt.ist.photon_graal.settings.CurrentSettings;

public class DTOFunctionExecute {
    private final String classFQN;
    private final String methodName;
    private final JsonNode args;

    private final Boolean isStatic;

    private DTOFunctionExecute(Settings functionSettings, JsonNode args) {
        this(functionSettings.getClassFQN(), functionSettings.getMethodName(), functionSettings.isStatic(), args);
    }

    private DTOFunctionExecute(String classFQN, String methodName, boolean isStatic, JsonNode args) {
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

    public JsonNode getArgs() {
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

    public static DTOFunctionExecute of(Settings functionSettings, JsonNode args) {
        return new DTOFunctionExecute(functionSettings, args);
    }

    public static DTOFunctionExecute of(JsonNode args) {
        return new DTOFunctionExecute(CurrentSettings.VALUE, args);
    }

    public static DTOFunctionExecute of(String classFQN, String methodName, boolean isStatic, JsonNode args) {
        return new DTOFunctionExecute(classFQN, methodName, isStatic, args);
    }
}
