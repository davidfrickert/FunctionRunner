package pt.ist.photon_graal.config.function;

import lombok.Data;
import pt.ist.photon_graal.runner.utils.management.IsolateStrategy;

@Data
public class Settings {
    private final String classFQN;
    private final String methodName;
    private final Boolean isStatic;
    private final IsolateStrategy isolateStrategy;

    public Settings(String classFQN, String methodName) {
        this(classFQN, methodName, false, IsolateStrategy.DEFAULT);
    }

    public Settings(String classFQN, String methodName, Boolean isStatic) {
        this(classFQN, methodName, isStatic, IsolateStrategy.DEFAULT);
    }

    public Settings(String classFQN, String methodName, Boolean isStatic, IsolateStrategy isolateStrategy) {
        this.classFQN = classFQN;
        this.methodName = methodName;
        this.isStatic = isStatic;
        this.isolateStrategy = isolateStrategy;
    }

    public boolean isStatic() {
        return isStatic != null && isStatic;
    }

    @Override
    public String toString() {
        return classFQN + "::" + methodName;
    }

    public IsolateStrategy getIsolateStrategy() {
        return isolateStrategy;
    }

    public String simplifiedName() {
        final String[] split = classFQN.split("\\.");
        return split[split.length - 1];
    }
}
