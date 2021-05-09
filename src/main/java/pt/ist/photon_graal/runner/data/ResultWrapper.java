package pt.ist.photon_graal.runner.data;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ResultWrapper<T> {
    private final Map<String, Duration> stats;
    private final T result;

    public ResultWrapper(Map<String, Duration> stats, T result) {
        this.stats = stats;
        this.result = result;
    }

    public ResultWrapper(T result) {
        this(new HashMap<>(), result);
    }

    public Map<String, Duration> getStats() {
        return stats;
    }

    public T getResult() {
        return result;
    }
}
