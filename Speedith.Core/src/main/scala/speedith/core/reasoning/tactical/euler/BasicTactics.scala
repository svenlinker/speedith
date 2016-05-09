package speedith.core.reasoning.tactical.euler

import speedith.core.reasoning.automatic.wrappers.{PrimarySpiderDiagramOccurrence, CompoundSpiderDiagramOccurrence}
import speedith.core.reasoning.tactical.{Tactic, Tacticals, TacticApplicationResult}
import speedith.core.reasoning.Goals
import speedith.core.reasoning.rules.util.ReasoningUtils
import speedith.core.reasoning.tactical.euler.Auxilliary._
import speedith.core.reasoning.tactical.euler.Choosers._
import speedith.core.reasoning.tactical.euler.Predicates._
import Tacticals._
import speedith.core.reasoning.tactical.euler.RuleTactics._
import scala.collection.JavaConversions._


  /**
  * Tactics to work on a proof. The Tactics chain several tactics
  * by using the [[Tacticals basic tacticals]]. Some of these
  * tactics are normally able to remove a subgoal, as long as it consists
  * of an implication, where the conclusion is a single unitary diagram (and if the
  * conclusion is a consequence of the premises)
   *
  * @author Sven Linker [s.linker@brighton.ac.uk]
  *
  */
object BasicTactics {

  def vennify: Tactic = {
    REPEAT(
      ORELSE(trivialTautology)(
        introduceShadedZone(isPrimaryAndContainsMissingZones, someMissingZone))
    )
  }

  def vennifyFast : Tactic = {
    REPEAT(ORELSE(trivialTautology)(
      introduceShadedZone(isPrimaryAndContainsMissingZones,allMissingZones))
    )
  }

  def deVennify : Tactic = {
    REPEAT(ORELSE(trivialTautology)(removeShadedZone(isPrimaryAndContainsShadedZones,someShadedZone)))
  }

  def deVennifyFast : Tactic = {
    REPEAT(ORELSE(trivialTautology)(removeShadedZone(isPrimaryAndContainsShadedZones,allShadedZones)))
  }


  def unifyContourSets: Tactic = (name: String) => (state: Goals) => (subGoalIndex: Int) => (result: TacticApplicationResult) => {
    val contours = getContoursInSubGoal(subGoalIndex, state)
    DEPTH_FIRST(equalContourSetsInEachPrimaryDiagram)(
      ORELSE(
        trivialTautology)(
        introduceContour(
          containsLessContours(contours),
          someGivenContoursButNotInDiagram(contours))))(name)(state)(subGoalIndex)(result)
  }

  def unifyContourSetsFast: Tactic = (name: String) => (state: Goals) => (subGoalIndex: Int) => (result: TacticApplicationResult) => {
    val contours = getContoursInSubGoal(subGoalIndex, state)
    DEPTH_FIRST(equalContourSetsInEachPrimaryDiagram)(
      ORELSE(trivialTautology)(introduceContour(containsLessContours(contours), allInGivenContoursButNotInDiagram(contours))))(name)(state)(subGoalIndex)(result)
  }

  def combineAll : Tactic = {
    REPEAT(ORELSE(trivialTautology)(
      combine))
  }

  def vennStyle : Tactic = {
    THEN(vennify)(THEN(unifyContourSets)(THEN(combineAll)(matchConclusion)))
  }

  def vennStyleFast : Tactic = {
    THEN(unifyContourSetsFast)(THEN(vennifyFast)(THEN(combineAll)(matchConclusionFast)))
  }


  def vennifyFocused : Tactic = (name:String) => (state:Goals) => (subGoalIndex : Int) => (result : TacticApplicationResult) => {
    val target = getDeepestNestedDiagram(subGoalIndex)(state)
    target match {
      case None => id(name)(state)(subGoalIndex)(result)
      case Some(diagram) =>
        REPEAT(
          ORELSE(
            trivialTautology)(
            introduceShadedZone(
              AND(
                isOperandOf(diagram),
                isPrimaryAndContainsMissingZones),
              someMissingZone)))(name)(state)(subGoalIndex)(result)
    }
  }

  def unifyContourSetsFocused : Tactic = (name:String) => (state:Goals) => (subGoalIndex : Int) => (result : TacticApplicationResult) => {
    val target = getDeepestNestedDiagram(subGoalIndex)(state)
    target match {
      case None => id(name)(state)(subGoalIndex)(result)
      case Some(diagram) =>
        val contours = collectContours(diagram)
        REPEAT(
          ORELSE(
            trivialTautology)(
            introduceContour(
              AND(
                isOperandOf(diagram),
                containsLessContours(contours)),
              someGivenContoursButNotInDiagram(contours))))(name)(state)(subGoalIndex)(result)
    }
  }

    /**
      * Chooses one conjunction of primary diagrams, changes both conjuncts into Venn diagrams and unifies their
      * contour sets. Afterwards combines the diagrams. Repeats this procedure as long as only a unitary
      * diagram is left.
      *
      * @return
      */
  def vennStyleFocused : Tactic = {
      THEN(
        DEPTH_FIRST(isUnitaryDiagram)(
          THEN(
            THEN(
              vennifyFocused)(
              unifyContourSetsFocused))(
            combineAll)))(
        matchConclusion)
  }


  def matchConclusionFast : Tactic = (name:String) => (state: Goals) => (subGoalIndex:Int) => (result:TacticApplicationResult) =>{
    val concContours =getContoursInConclusion(subGoalIndex,state)
    val concShadedZones = getShadedZonesInConclusion(subGoalIndex,state)
    val concUnshadedZones = getUnshadedZonesInConclusion(subGoalIndex,state)
    val concVisibleZones = getVisibleZonesInConclusion(subGoalIndex,state)
    THEN(
      THEN(
        THEN(
          THEN(
            REPEAT(ORELSE(trivialTautology)(
              introduceContour(containsLessContours(concContours), someGivenContoursButNotInDiagram(concContours)))))(
            REPEAT(ORELSE(trivialTautology)(
              eraseContour(containsOtherContours(concContours), allInDiagramButNotInGivenContours(concContours))))))(
          REPEAT(ORELSE(trivialTautology)(
            introduceShadedZone(isPrimaryAndContainsMissingZones, allMissingZonesInGivenZones(concVisibleZones))))))(
        REPEAT(ORELSE(trivialTautology)(
          eraseShading(isPrimaryAndContainsShadedZones, allVisibleShadedZonesInGivenZones(concUnshadedZones))))))(
      REPEAT(ORELSE(trivialTautology)(
        removeShadedZone(isPrimaryAndContainsShadedZones,allVisibleShadedZoneNotInGivenZones(concShadedZones)))))(name)(state)(subGoalIndex)(result)
  }

  def matchConclusion: Tactic =
    (name:String) => (state:Goals) => (subGoalIndex:Int) => (result:TacticApplicationResult) => {
    val concContours = getContoursInConclusion(subGoalIndex, state)
    val concShadedZones = getShadedZonesInConclusion(subGoalIndex, state)
    val concUnshadedZones = getUnshadedZonesInConclusion(subGoalIndex, state)
    val concVisibleZones = getVisibleZonesInConclusion(subGoalIndex, state)
    THEN(
      THEN(
        THEN(
          THEN(
            REPEAT(ORELSE(trivialTautology)(
              introduceContour(containsLessContours(concContours), someGivenContoursButNotInDiagram(concContours)))))(
            REPEAT(ORELSE(trivialTautology)(
              eraseContour(containsOtherContours(concContours), someInDiagramButNotInGivenContours(concContours))))))(
          REPEAT(ORELSE(trivialTautology)(
            introduceShadedZone(isPrimaryAndContainsMissingZones, someMissingZoneInGivenZones(concVisibleZones))))))(
        REPEAT(ORELSE(trivialTautology)(
          eraseShading(isPrimaryAndContainsShadedZones, someVisibleShadedZonesInGivenZones(concUnshadedZones))))))(
      REPEAT(ORELSE(trivialTautology)(
        removeShadedZone(isPrimaryAndContainsShadedZones, someVisibleShadedZoneNotInGivenZones(concShadedZones)))))(name)(state)(subGoalIndex)(result)
  }


    /**
      * Applies Copy Contour as often as possible. Also uses Remove Shaded Zone to increase the semantic information that
      * is copied. (If a zone is visible and shaded, this shading information is not taken into account by Copy Contour.
      * If the zone is missing however, its information is used in the copy procedure)
     *
      * @return
      */
  def copyTopologicalInformation : Tactic = {
      REPEAT(
        ORELSE(
          trivialTautology)(
          ORELSE(
            idempotency)(
            ORELSE(
              removeShadedZonesForCopyContour)(
              copyContour))))
  }


    /**
      * Tries to apply Copy Shading to the goal as often as possible (while copying maximal shaded regions). If
      * no shaded zones that can be copied exist, it tries to introduce shaded zones to create new possibilities to
      * copy shadings.
      *
      * @return
      */
  def copyShadings: Tactic = {
    REPEAT(
      ORELSE(
        trivialTautology)(
        ORELSE(
          idempotency)(
          ORELSE(
            copyShading)(
            introduceMissingZonesForCopyShading))))
  }

  def copyEveryThing: Tactic =  {
    THEN(
      DEPTH_FIRST(
        isUnitaryDiagram)(
        THEN(
          COND(isUnitaryDiagram)(id)(copyShadings))(
          COND(isUnitaryDiagram)(id)(copyTopologicalInformation))))(
      matchConclusion)
  }

    /**
      * Applies Introduce Shaded Zone to create new possibilites to for Copy Shading to be applied.
      *
      * @return
      */
  def introduceMissingZonesForCopyShading : Tactic = (name:String) => (state:Goals) => (subGoalIndex:Int) => (result:TacticApplicationResult) =>{
    val goal = getSubGoal(subGoalIndex,state)
    if (ReasoningUtils.isImplicationOfConjunctions(goal)) {
      // get a conjunction of primary diagrams, where one conjunct contains a missing region that
      // corresponds to a region in the other operand
      val target = firstMatchingDiagram(goal.asInstanceOf[CompoundSpiderDiagramOccurrence].getOperand(0), isConjunctionContainingMissingZonesToCopy).asInstanceOf[Option[CompoundSpiderDiagramOccurrence]]
      target match {
        case None => fail(name)(state)(subGoalIndex)(result)
        case Some(d) =>
          // compare both operands of the conjunction to find the operand that contains
          // "more" missing zones than the other (this only works if both contain the same set of
          // contours, otherwise the left operand will always be chosen)
          val op0 = d.getOperand(0).asInstanceOf[PrimarySpiderDiagramOccurrence]
          val op1 = d.getOperand(1).asInstanceOf[PrimarySpiderDiagramOccurrence]
          val moreMissing =
            if ((op0.getShadedZones -- op0.getPresentZones).subsetOf(op1.getShadedZones -- op1.getPresentZones)) {
              op1
            } else {
              op0
            }
          // introduce all missing zones that would be contained in a single contour at once
          introduceShadedZone(is(moreMissing), allMissingZonesContainingOneContour)(name)(state)(subGoalIndex)(result)
      }
    } else {
      fail(name)(state)(subGoalIndex)(result)
    }
  }

    def removeShadedZonesForCopyContour: Tactic = (name:String) => (state:Goals) => (subGoalIndex:Int) => (result:TacticApplicationResult) => {
      val goal = getSubGoal(subGoalIndex, state)
      if (ReasoningUtils.isImplicationOfConjunctions(goal)) {
        val target = firstMatchingDiagram(goal.asInstanceOf[CompoundSpiderDiagramOccurrence].getOperand(0), isConjunctionWithContoursToCopy).asInstanceOf[Option[CompoundSpiderDiagramOccurrence]]
        target match {
          case None => fail(name)(state)(subGoalIndex)(result)
          case Some(d) =>
            removeShadedZone(
              AND(
                isOperandOf(d),
                isPrimaryAndContainsShadedZones),
              allShadedZonesContainingOneContour)(name)(state)(subGoalIndex)(result)
        }
      }
      else {
        fail(name)(state)(subGoalIndex)(result)
      }
    }
}