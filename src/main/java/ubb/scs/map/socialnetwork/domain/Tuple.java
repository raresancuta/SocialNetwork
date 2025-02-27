package ubb.scs.map.socialnetwork.domain;

import java.util.Objects;
import java.util.Objects;

/**
 * The Tuple class represents a generic container for a pair of values.
 * It holds two elements of potentially different types, allowing
 * you to associate two objects together.
 *
 * @param <E1> the type of the first element
 * @param <E2> the type of the second element
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;


    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Retrieves the first element of the tuple.
     *
     * @return the first element of the tuple
     */
    public E1 getE1() {
        return e1;
    }

    /**
     * Retrieves the second element of the tuple.
     *
     * @return the second element of the tuple
     */
    public E2 getE2() {
        return e2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return (Objects.equals(e1, tuple.e1) && Objects.equals(e2, tuple.e2)) ||
                (Objects.equals(e1, tuple.e2) && Objects.equals(e2, tuple.e1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
