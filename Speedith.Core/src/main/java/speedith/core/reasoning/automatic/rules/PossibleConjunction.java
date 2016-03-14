package speedith.core.reasoning.automatic.rules;

import speedith.core.reasoning.InferenceRule;
import speedith.core.reasoning.args.MultipleRuleArgs;
import speedith.core.reasoning.args.RuleArg;
import speedith.core.reasoning.args.SubDiagramIndexArg;
import speedith.core.reasoning.automatic.wrappers.SpiderDiagramOccurrence;
import speedith.core.reasoning.rules.ConjunctionElimination;

import java.util.ArrayList;
import java.util.List;

/**
 * A possibility to apply conjunction elimination.
 *
 * @author Sven Linker [s.linker@brighton.ac.uk]
 */
public class PossibleConjunction extends PossibleRuleApplication<MultipleRuleArgs> {
    final SpiderDiagramOccurrence child;
    public PossibleConjunction(SpiderDiagramOccurrence target, ConjunctionElimination rule, SpiderDiagramOccurrence child) {
        super(target, rule);
        this.child = child;
    }

    @Override
    public MultipleRuleArgs getArg(int subgoalindex) {
        SubDiagramIndexArg compound  = new SubDiagramIndexArg(subgoalindex, getTarget().getOccurrenceIndex());
        SubDiagramIndexArg childArg = new SubDiagramIndexArg(subgoalindex, child.getOccurrenceIndex());
        List<SubDiagramIndexArg> args = new ArrayList<>();
        args.add(compound);
        args.add(childArg);
        return new MultipleRuleArgs(args);
    }
}