package nge.lk.stuff.theorem.firstorder.symbolic;

import nge.lk.stuff.theorem.firstorder.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents an atomic formula, i.e. a predicate
 */
public class Atom {

    /**
     * The formula as a string
     */
    private final String atom;

    /**
     * Finds a most general unifier of the given atoms, or null if the atoms are not unifyable
     *
     * @param a1 the first atom
     * @param a2 the second atom
     *
     * @return the MGU or null
     */
    public static List<AtomSubstitution> unifyAtoms(Atom a1, Atom a2) {
        // The most general unifier
        List<AtomSubstitution> mgu = new ArrayList<>();

        // The constraint set of the Martelli-Montanari algorithm
        Queue<Pair<String, String>> constraints = new LinkedList<>();
        constraints.add(Pair.of(a1.atom, a2.atom));

        while (!constraints.isEmpty()) {
            Pair<String, String> constraint = constraints.poll();
            String a = constraint.getFirst();
            String b = constraint.getSecond();

            int aFirstParen = a.indexOf('(');
            int bFirstParen = b.indexOf('(');

            if (aFirstParen != -1 && bFirstParen != -1) {
                // Both are functions
                String mapName = a.substring(0, aFirstParen);
                if (!mapName.equals(b.substring(0, bFirstParen))) {
                    return null; // f(s1, ..., sn) = g(t1, ..., tn)
                }

                a = a.substring(aFirstParen + 1, a.length() - 1);
                b = b.substring(bFirstParen + 1, b.length() - 1);

                int ptrA = 0;
                int ptrB = 0;
                while (ptrA < a.length() || ptrB < b.length()) {
                    StringBuilder collectA = new StringBuilder();
                    StringBuilder collectB = new StringBuilder();

                    // Read the next term in A
                    int depth = 0;
                    while (depth != 0 || (ptrA < a.length() && a.charAt(ptrA) != ',')) {
                        char c = a.charAt(ptrA);
                        collectA.append(c);
                        switch (c) {
                            case '(':
                                depth++;
                                break;
                            case ')':
                                depth--;
                                break;
                        }
                        ptrA++;
                    }
                    ptrA++; // Consume , or step one behind the string

                    // Read the next term in B
                    depth = 0;
                    while (depth != 0 || (ptrB < b.length() && b.charAt(ptrB) != ',')) {
                        char c = b.charAt(ptrB);
                        collectB.append(c);
                        switch (c) {
                            case '(':
                                depth++;
                                break;
                            case ')':
                                depth--;
                                break;
                        }
                        ptrB++;
                    }
                    ptrB++; // Consume , or step one behind the string

                    String newA = collectA.toString();
                    String newB = collectB.toString();

                    // Add a new constraint for unifying both terms (si, ti). If they are equal, nothing has to be done
                    if (!newA.equals(newB)) {
                        constraints.add(Pair.of(newA, newB));
                    }
                }
            } else if ((a.contains(b) || b.contains(a)) && !a.equals(b)) {
                return null; // f(x) = x
            } else {
                // x = Term
                AtomSubstitution sub = new AtomSubstitution(a.startsWith("[") ? a : b, a.startsWith("[") ? b : a);
                mgu.add(sub);
                for (Pair<String, String> pair : constraints) {
                    pair.setFirst(pair.getFirst().replace(sub.getVariable(), sub.getTerm()));
                    pair.setSecond(pair.getSecond().replace(sub.getVariable(), sub.getTerm()));
                }
            }
        }

        return mgu;
    }

    /**
     * Constructor
     *
     * @param formula the atomic formula
     */
    public Atom(String formula) {
        atom = formula.replaceAll("\\s+", "");
    }

    /**
     * Applies the given substitutions to this atom and returns a new atom containing the resulting atom.
     *
     * @param subs the substitutions
     *
     * @return the new atom
     */
    public Atom applySubstitutions(Iterable<AtomSubstitution> subs) {
        String tmp = atom;
        for (AtomSubstitution sub : subs) {
            tmp = tmp.replace(sub.getVariable(), sub.getTerm());
        }
        return new Atom(tmp);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Atom && ((Atom) obj).atom.equals(atom);
    }

    @Override
    public int hashCode() {
        return atom.hashCode();
    }

    @Override
    public String toString() {
        return atom;
    }
}
