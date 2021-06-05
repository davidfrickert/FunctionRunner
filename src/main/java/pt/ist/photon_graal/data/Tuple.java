package pt.ist.photon_graal.data;

import java.io.Serializable;

public class Tuple<T1, T2> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T1 first;
    private T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    protected Tuple() {
        /* For Serialization Only! */
    }

    public T1 _1() {
        return first;
    }

    public T2 _2() {
        return second;
    }
}
