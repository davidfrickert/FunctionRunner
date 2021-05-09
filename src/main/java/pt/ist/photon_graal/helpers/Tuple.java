package pt.ist.photon_graal.helpers;

public class Tuple<T1, T2> {
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
