package pt.ist.photon_graal.rest.function;

import lombok.Data;

@Data
public class Settings {
    private final String classFQN;
    private final String methodName;
    private final Boolean isStatic;

    public Settings(String classFQN, String methodName) {
        this(classFQN, methodName, null);
    }

    public Settings(String classFQN, String methodName, Boolean isStatic) {
        this.classFQN = classFQN;
        this.methodName = methodName;
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic != null && isStatic;
    }
}
