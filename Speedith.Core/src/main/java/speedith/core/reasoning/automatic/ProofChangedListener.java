package speedith.core.reasoning.automatic;

import java.util.EventListener;

/**
 * TODO: Description
 *
 * @author Sven Linker [s.linker@brighton.ac.uk]
 */
public interface ProofChangedListener extends EventListener {


    void interactiveRuleApplied(InteractiveRuleAppliedEvent e);

    void tacticApplied(TacticAppliedEvent e);

    void proofReplaced(ProofReplacedEvent e);

    void proofReduced(ProofReducedEvent e);


}
