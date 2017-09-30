package nge.lk.stuff.theorem;

import nge.lk.stuff.theorem.firstorder.resolution.KnowledgeBase;

public final class TheoremProver {

    public static void main(String[] args) {
        KnowledgeBase kb = new KnowledgeBase();

        // Equality axioms
        kb.addClause("Eq([x], [x])");
        kb.addClause("!Eq([x], [y])", "Eq([y], [x])");
        kb.addClause("!Eq([x], [y])", "!Eq([y], [z])", "Eq([x], [z])");
        kb.addClause("!Eq([x], [xs])", "!Eq([y], [ys])", "Eq(add([x], [y]), add([xs], [ys]))");

        // Commutativity, Associativity of 'add'
        kb.addClause("Eq(add([x], [y]), add([y], [x]))");
        kb.addClause("Eq(add([x], add([y], [z])), add(add([x], [y]), [z]))");

        // Claim: d=c -> a+(b+c)=a+(b+d)
        kb.addClause("Eq(d(), c())");
        kb.addClause("!Eq(add(a(), add(b(), c())), add(a(), add(b(), d())))");

        assert !kb.hasContradiction() : "Knowledge base contains the empty clause before resolution";
        for (int i = 0; i < 10; i++) {
            if (kb.resolutionStep() == 0) {
                System.out.println("Knowledge base is definitely consistent");
                return;
            }
            if (kb.hasContradiction()) {
                System.out.println("Proof found!");
                System.out.println();
                kb.printContradictionProof();
                return;
            }
        }
        System.out.println("No proof found (Timeout)");
    }

    private TheoremProver() {
    }
}
