package pt.ist.photon_graal.runner.data;

import io.vavr.Tuple2;
import pt.ist.photon_graal.helpers.Tuple;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ResultWrapper<T> implements Serializable {
    private static final long serialVersionUID = 42L;

    private final List<Tuple<String, Duration>> stats;
    private final T result;

    public ResultWrapper(List<Tuple<String, Duration>> stats, T result) {
        this.stats = stats;
        this.result = result;
    }

    public ResultWrapper(T result) {
        this(new LinkedList<>(), result);
    }

    public List<Tuple<String, Duration>> getStats() {
        return stats;
    }

    public T getResult() {
        return result;
    }
}
