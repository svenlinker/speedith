package speedith.core.reasoning.automatic.strategies;

import speedith.core.reasoning.Proof;
import speedith.core.reasoning.automatic.AutomaticProofException;
import speedith.core.reasoning.automatic.rules.PossibleRuleApplication;

import java.util.Set;

/**
 * A strategy for choosing the next rule to apply to a proof.
 *
 * @author Sven Linker [s.linker@brighton.ac.uk]
 */
public interface Strategy {


    int getCost(Proof p);

    int getHeuristic(Proof p) throws AutomaticProofException;
}
