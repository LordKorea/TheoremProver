package nge.lk.stuff.theorem.firstorder.util;

import lombok.Getter;
import lombok.Setter;

/**
 * A pair of values
 *
 * @param <A> the type of the first value
 * @param <B> the type of the second value
 */
public final class Pair<A, B> {

    /**
     * The first value
     */
    @Getter @Setter private A first;

    /**
     * The second value
     */
    @Getter @Setter private B second;

    /**
     * Factory method for creating a pair
     *
     * @param x the first value
     * @param y the second value
     * @param <X> the type of the first value
     * @param <Y> the type of the second value
     *
     * @return the pair
     */
    public static <X, Y> Pair<X, Y> of(X x, Y y) {
        return new Pair<>(x, y);
    }

    /**
     * Constructor
     *
     * @param a the first value
     * @param b the second value
     */
    private Pair(A a, B b) {
        first = a;
        second = b;
    }

    @Override
    public int hashCode() {
        return (first.hashCode() * 31) ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair && first.equals(((Pair) obj).first) && second.equals(((Pair) obj).second);
    }
}
