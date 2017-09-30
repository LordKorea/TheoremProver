package nge.lk.stuff.theorem.firstorder.resolution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nge.lk.stuff.theorem.firstorder.symbolic.Atom;

/**
 * Represents a literal, i.e. a possibly negated atom
 */
@RequiredArgsConstructor
public class Literal {

    /**
     * The atom
     */
    @Getter private final Atom atom;

    /**
     * Whether this literal is negative
     */
    @Getter private final boolean isNegated;

    @Override
    public String toString() {
        return (isNegated ? "\u00AC" : "") + atom;
    }

    @Override
    public int hashCode() {
        return (isNegated ? 0xFFFFFFFF : 0) ^ atom.hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }

        Literal other = (Literal) obj;
        return other.isNegated == isNegated && other.atom.equals(atom);
    }
}
