package nge.lk.stuff.theorem.firstorder.resolution;

import lombok.Getter;
import nge.lk.stuff.theorem.firstorder.symbolic.Atom;
import nge.lk.stuff.theorem.firstorder.symbolic.AtomSubstitution;
import nge.lk.stuff.theorem.firstorder.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a clause, i.e. a disjunction of literals
 */
public class Clause {

    /**
     * The literals in this clause
     */
    private final Collection<Literal> literals = new HashSet<>();

    /**
     * The parents of this clause, i.e. the clauses from which this clause was derived. {@code null} for axioms/premises.
     */
    @Getter private Clause[] parents;

    /**
     * Constructor
     *
     * @param literals the literals in this clause
     */
    public Clause(String... literals) {
        this.literals.addAll(Arrays.stream(literals).map(s -> s.startsWith("!") ? new Literal(new Atom(s.substring(1)), true) : new Literal(new Atom(s), false)).collect(Collectors.toList()));
    }

    /**
     * Resolves this clause with another clause, generating all resolvents
     *
     * @param clause the other clause
     *
     * @return all possible resolvents
     */
    public List<Clause> resolveClauses(Clause clause) {
        List<Clause> newClauses = new ArrayList<>();

        for (Literal l1 : literals) {
            for (Literal l2 : clause.literals) {
                if (l1.isNegated() == l2.isNegated()) {
                    continue;
                }

                List<AtomSubstitution> mgu = Atom.unifyAtoms(l1.getAtom(), l2.getAtom());
                if (mgu == null) {
                    continue;
                }

                Clause resolvent = new Clause();
                resolvent.parents = new Clause[]{this, clause};
                Consumer<Literal> action = l -> {
                    if (l != l1 && l != l2) {
                        resolvent.literals.add(new Literal(l.getAtom().applySubstitutions(mgu), l.isNegated()));
                    }
                };

                literals.forEach(action);
                clause.literals.forEach(action);
                newClauses.add(resolvent);
            }
        }

        return newClauses;
    }

    /**
     * Factors this clause and produces all possible factors (that are reachable in one factorization step)
     *
     * @return the possible factors
     */
    public List<Clause> factorClause() {
        List<Clause> newClauses = new ArrayList<>();

        Collection<Pair<Literal, Literal>> exclusions = new HashSet<>();
        for (Literal l1 : literals) {
            for (Literal l2 : literals) {
                if (l1 == l2 || l1.isNegated() != l2.isNegated() || exclusions.contains(Pair.of(l1, l2))) {
                    continue;
                }

                List<AtomSubstitution> mgu = Atom.unifyAtoms(l1.getAtom(), l2.getAtom());
                if (mgu == null) {
                    continue;
                }

                Clause factored = new Clause();
                factored.parents = new Clause[]{this};
                for (Literal l3 : literals) {
                    if (l3 == l2) {
                        continue;
                    }
                    factored.literals.add(new Literal(l3.getAtom().applySubstitutions(mgu), l3.isNegated()));
                }
                newClauses.add(factored);

                exclusions.add(Pair.of(l2, l1));
            }
        }
        return newClauses;
    }

    @Override
    public String toString() {
        if (literals.isEmpty()) {
            return "∎";
        }

        return "{" + String.join(" ∨ ", literals.stream().map(Object::toString).collect(Collectors.toList())) + "}";
    }

    @Override
    public int hashCode() {
        return literals.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }

        Clause other = (Clause) obj;
        return other.literals.equals(literals);
    }

    /**
     * Checks whether this clause is the empty clause
     *
     * @return true iff this is the empty clause
     */
    public boolean isEmpty() {
        return literals.isEmpty();
    }

    /**
     * Checks whether this clause is a horn clause, i.e. there is at most one positive literal
     *
     * @return true iff this is a horn clause
     */
    public boolean isHornClause() {
        return literals.stream().filter(l -> !l.isNegated()).count() <= 1;
    }
}
