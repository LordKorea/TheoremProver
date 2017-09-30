package nge.lk.stuff.theorem.firstorder.symbolic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a substitution that is applied to atoms
 */
@RequiredArgsConstructor
public class AtomSubstitution {

    /**
     * The variable that is being substituted
     */
    @Getter private final String variable;

    /**
     * The term that the variable is replaced with
     */
    @Getter private final String term;

    @Override
    public String toString() {
        return variable + " -> " + term;
    }
}
