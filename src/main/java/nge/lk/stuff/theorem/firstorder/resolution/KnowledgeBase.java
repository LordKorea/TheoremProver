package nge.lk.stuff.theorem.firstorder.resolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents the knowledge as axioms and premises in CNF
 */
public class KnowledgeBase {

    /**
     * The clauses that are currently assumed to be consistent
     */
    private final List<Clause> clauses = new ArrayList<>();

    /**
     * A set of clauses to spot already existing clauses
     */
    private final Collection<Clause> clauseMarker = new HashSet<>();

    /**
     * The empty clause if it was already resolved, else null
     */
    private Clause emptyClause;

    /**
     * The index of the highest clause that every clause has been checked against
     */
    private int processed = -1;

    /**
     * Adds a clause to the knowledge base after the variables are normalized
     *
     * @param literals the literals in the clause
     */
    public void addClause(String... literals) {
        int varSuffix = clauses.size();

        // Find all variables
        Collection<String> variables = new HashSet<>();
        for (String lit : literals) {
            Matcher m = Pattern.compile("\\[([a-zA-Z0-9]+)\\]").matcher(lit);
            while (m.find()) {
                variables.add(m.group(1));
            }
        }

        // Normalize variables
        for (int i = 0; i < literals.length; i++) {
            for (String var : variables) {
                literals[i] = literals[i].replaceAll("\\s+", "");
                literals[i] = literals[i].replace(String.format("[%s],", var), String.format("[%s_%d],", var, varSuffix));
                literals[i] = literals[i].replace(String.format("[%s])", var), String.format("[%s_%d])", var, varSuffix));
            }
        }

        Clause c = new Clause(literals);
        clauses.add(c);
        clauseMarker.add(c);
    }

    /**
     * Applies factoring and resolution to the knowledge base to derive new knowledge
     *
     * @return the number of new clauses
     */
    public int resolutionStep() {
        Collection<Clause> newClauses = new ArrayList<>();

        factorStep(newClauses);
        resolveStep(newClauses);
        if (emptyClause != null) {
            // do not add to clauses, resolution ended
            return newClauses.size();
        }

        processed = clauses.size() - 1;

        int found = 0;
        for (Clause c : newClauses) {
            if (clauseMarker.contains(c)) {
                continue;
            }
            clauseMarker.add(c);
            clauses.add(c);
            found++;
        }

        return found;
    }

    /**
     * Prints the proof of the contradiction
     */
    public void printContradictionProof() {
        Queue<Clause> tellMe = new LinkedList<>();
        Stack<String> derivation = new Stack<>();
        tellMe.add(emptyClause);

        while (!tellMe.isEmpty()) {
            Clause x = tellMe.poll();
            if (x.getParents() == null) {
                derivation.push("KB ⊨ " + x);
            } else {
                derivation.push(String.join(", ", Arrays.stream(x.getParents()).map(Object::toString).collect(Collectors.toList())) + " ⊨ " + x);
                tellMe.addAll(Arrays.asList(x.getParents()));
            }
        }

        while (!derivation.isEmpty()) {
            System.out.println(derivation.pop().replaceAll("_[0-9]+", ""));
        }
    }

    /**
     * Returns true iff the knowledge base is inconsistent
     *
     * @return true iff inconsistent
     */
    public boolean hasContradiction() {
        return emptyClause != null;
    }

    /**
     * Factors all clauses in the knowledge base that have not been factored yet
     *
     * @param newClauses the collection to which the factors are added to
     */
    private void factorStep(Collection<Clause> newClauses) {
        for (int i = processed + 1; i < clauses.size(); i++) {
            newClauses.addAll(clauses.get(i).factorClause());
        }
    }

    /**
     * Resolves all clauses that have not yet been resolved together
     *
     * @param newClauses the collection to which the resolvents are added to
     */
    private void resolveStep(Collection<Clause> newClauses) {
        // Resolve every clause...
        for (int i = 0; i < clauses.size(); i++) {
            // ...with every other clause that (a) was added in the last step AND (b) comes after the first clause
            for (int j = Math.max(processed + 1, i + 1); j < clauses.size(); j++) {
                List<Clause> resolvents = clauses.get(i).resolveClauses(clauses.get(j));
                newClauses.addAll(resolvents);
                if (emptyClause == null) {
                    // Check the resolvents for the empty clause and stop if it is found
                    emptyClause = resolvents.stream().filter(Clause::isEmpty).findAny().orElse(null);
                    if (emptyClause != null) {
                        return;
                    }
                }
            }
        }
    }
}
