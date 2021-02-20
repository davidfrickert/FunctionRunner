package pt.ist.photon_graal.rest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOFunctionExecute {
    private String classFQN;
    private String methodName;
    private Object[] args;

    private Boolean isStatic;

    public DTOFunctionExecute(@JsonProperty(value = "classFQN",required = true)
                                      String classFQN
            , @JsonProperty(value = "methodName",required = true)
                                      String methodName
            , @JsonProperty(value = "args",required = true)
                                      Object[] args
            , @JsonProperty("is_static")
                                      boolean isStatic) {
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
