/*
 *   Project: Speedith
 * 
 * File name: ProofPanel.java
 *    Author: Matej Urbas [matej.urbas@gmail.com]
 * 
 *  Copyright © 2012 Matej Urbas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package speedith.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;

import speedith.core.lang.SpiderDiagram;
import speedith.core.reasoning.*;
import speedith.core.reasoning.args.RuleArg;
import speedith.core.reasoning.args.SubgoalIndexArg;
import speedith.core.reasoning.automatic.*;
import speedith.core.reasoning.automatic.strategies.NoStrategy;
import speedith.core.reasoning.automatic.strategies.Strategies;
import speedith.core.reasoning.rules.util.AutomaticUtils;
import speedith.core.reasoning.rules.util.ReasoningUtils;

import static speedith.i18n.Translations.i18n;

/**
 * This panel displays a spider-diagrammatic proof. This is the main way to
 * display and work with proofs in Speedith.
 * 
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public class ProofPanel extends javax.swing.JPanel implements Proof, AutomaticProof {

    private static final long serialVersionUID = 6560236682608445666L;

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private ProofTrace proof;

    private AutomaticProver prover;

    private List<SubgoalsPanel> subgoals;

    private SpiderDiagram selected;

    private int selectedNumber;
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Creates a new proof panel with no goals.
     */
    public ProofPanel() {
        this(null);
    }

    /**
     * Creates a new proof panel with the given goals.
     *
     * @param initialGoals the initial goals (the theorem we want to prove).
     * <p><span style="font-weight:bold">Note</span>: this parameter may be {@code null}
     * in which case no goals will be displayed and no proof will be
     * applicable.</p>
     */
    public ProofPanel(Goals initialGoals) {
        subgoals = new ArrayList<>();
        initComponents();
        resetProof(initialGoals, false);
        initialiseProver();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generated Code">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrlGoals = new javax.swing.JScrollPane();
        pnlGoals = new javax.swing.JPanel();

        scrlGoals.setBackground(new java.awt.Color(255, 255, 255));

        pnlGoals.setBackground(new java.awt.Color(255, 255, 255));
        pnlGoals.setLayout(new java.awt.GridBagLayout());
        scrlGoals.setViewportView(pnlGoals);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlGoals, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlGoals, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlGoals;
    private javax.swing.JScrollPane scrlGoals;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    /**
     * Removes the previous proof entirely and starts a new proof with the given
     * initial goals.
     *
     * @param initialGoals the initial goals of the new proof.
     */
    public void newProof(Goals initialGoals) {
        resetProof(initialGoals, true);
    }

    public AutomaticProver getProver() {
        return prover;
    }

    public void setProver(AutomaticProver prover) {
        this.prover = prover;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Proof Interface Implementation">
    public <TRuleArg extends RuleArg> RuleApplicationResult applyRule(InferenceRule<TRuleArg> rule, RuleApplicationType type) throws RuleApplicationException {
        return applyRule(rule, null, type);
    }

    public <TRuleArg extends RuleArg> RuleApplicationResult applyRule(InferenceRule<? super TRuleArg> rule, TRuleArg args, RuleApplicationType type) throws RuleApplicationException {
        RuleApplicationResult appResult = proof.applyRule(rule, args, type);
        if (proof.isFinished()) {
            addProofFinished(rule, args, type);
        } else {
            addGoals(proof.getGoalsCount() - 1, appResult.getGoals(), rule, args, proof.getRuleApplicationAt(proof.getRuleApplicationCount()-1));
        }
        // Scroll the last component into view:
        scrlGoals.getVerticalScrollBar().setValue(scrlGoals.getVerticalScrollBar().getMaximum());
        return appResult;
    }

    public boolean undoStep() {
        final boolean didUndo = proof.undoStep();
        if (didUndo) {
            SubgoalsPanel lastGoals = subgoals.remove(subgoals.size()-1);
            pnlGoals.remove(lastGoals);
            validate();
        }
        return didUndo;
    }

    public Goals getGoalsAt(int index) {
        return proof.getGoalsAt(index);
    }

    public int getGoalsCount() {
        return proof.getGoalsCount();
    }

    public Goals getInitialGoals() {
        return proof.getInitialGoals();
    }

    public Goals getLastGoals() {
        return proof.getLastGoals();
    }

    public List<Goals> getGoals() {
        return proof.getGoals();
    }

    public List<RuleApplication> getRuleApplications() {
        return proof.getRuleApplications();
    }

    public RuleApplication getRuleApplicationAt(int index) {
        return proof.getRuleApplicationAt(index);
    }

    public int getRuleApplicationCount() {
        return proof.getRuleApplicationCount();
    }

    public boolean isFinished() {
        return proof.isFinished();
    }

    public SpiderDiagram getSelected() { return selected; }


    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="AutomaticProof Interface Implementation">

    @Override
    public Proof generateProof(Goals initialGoals) throws AutomaticProofException {
        if (initialGoals == null) {
            throw  new AutomaticProofException("No subgoal to prove");
        }
        Goals normalised = ReasoningUtils.normalize(initialGoals);
        Proof tempProof = prover.generateProof(normalised);
        if (!(tempProof == null)) {
            newProof(normalised);
            for (RuleApplication appl : tempProof.getRuleApplications()) {
                try {
                    applyRule((InferenceRule<? super RuleArg>) appl.getInferenceRule(), appl.getRuleArguments(), RuleApplicationType.AUTOMATIC);
                } catch (RuleApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
        return proof;
    }

    @Override
    public Proof extendProof(Proof proof) throws AutomaticProofException {
        if (proof == null || proof.getLastGoals() == null) {
            throw  new AutomaticProofException("No subgoal to prove");
        }
        Proof extendedProof = prover.extendProof(proof);
        if (!(extendedProof==null)) {
            int currentLength = this.proof.getRuleApplicationCount();
            int targetLength  = extendedProof.getRuleApplicationCount();
            for (RuleApplication appl : extendedProof.getRuleApplications().subList(currentLength, targetLength)) {
                try {
                    applyRule((InferenceRule<? super RuleArg>) appl.getInferenceRule(), appl.getRuleArguments(), appl.getType());
                } catch (RuleApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.proof;
    }

    public void reduceToSelected() {
        int currentLength = proof.getGoalsCount()-1;
        for (int i = 0; i < currentLength - selectedNumber; i++) {
            undoStep();
        }
    }

    public void replaceCurrentProof( Proof proof) {
        if (!(proof==null)) {
            newProof(proof.getInitialGoals());
            for (RuleApplication appl : proof.getRuleApplications()) {
                try {
                    applyRule((InferenceRule<? super RuleArg>) appl.getInferenceRule(), appl.getRuleArguments(), appl.getType());
                } catch (RuleApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void extendCurrentProofTo(Proof proof) {
        if (!(proof == null)) {
            int currentLength = this.proof.getRuleApplicationCount();
            int targetLength  = proof.getRuleApplicationCount();
            for (RuleApplication appl : proof.getRuleApplications().subList(currentLength, targetLength)) {
                try {
                    applyRule((InferenceRule<? super RuleArg>) appl.getInferenceRule(), appl.getRuleArguments(), appl.getType());
                } catch (RuleApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //</editor-fold>

    public Proof getProof() {
        return proof;
    }

    // <editor-fold defaultstate="collapsed" desc="UI Related Methods">
    private void displayInitialGoals() {
        Goals initialGoals = proof.getInitialGoals();
        if (initialGoals == null || initialGoals.isEmpty()) {
            // If there are no goal, just put up a title saying this.
            displayEmptyGoals();
        } else {
            displayInitialGoals(initialGoals);
        }
    }

    private void displayInitialGoals(Goals initialGoals) {
        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        SubgoalsPanel sgp = new SubgoalsPanel(initialGoals, getSubgoalsTitle(0), (String) null, Color.BLUE);
        addSubgoal(gbc, sgp);
        registerMouseSelectionListener(initialGoals, sgp);
    }

    /**
     * Adds the given {@link SubgoalsPanel panel} to the UI of this ProofPanel and updates the
     * internal list of subgoals.
     *
     * @param gbc the constraints for the layout of the panel
     * @param sgp the subgoalspanel to be added to the proof panel
     */
    private void addSubgoal(GridBagConstraints gbc, SubgoalsPanel sgp) {
        pnlGoals.add(sgp, gbc);
        subgoals.add(sgp);
    }

    private <TRuleArg extends RuleArg> void addGoals(int stepIndex, Goals goals, InferenceRule<? super TRuleArg> rule, TRuleArg args, RuleApplication application) {
        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        final SubgoalsPanel sgp = new SubgoalsPanel(goals, getSubgoalsTitle(stepIndex), getStepDescription(rule, args, application.getType()), application.getType().getColor());
        addSubgoal(gbc, sgp);
        registerMouseSelectionListener(goals, sgp);
        validate();
    }

    private void registerMouseSelectionListener(final Goals goals, final SubgoalsPanel sgp) {
        if (!goals.isEmpty()) {
            sgp.addMouseListener(new MouseListener() {
                final int stepNumber = subgoals.size()-1;
                private Goals goal = goals;

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (mouseEvent.getComponent() == sgp) {
                        for (SubgoalsPanel s : subgoals) {
                            s.setBorder(BorderFactory.createEmptyBorder());
                            s.setTitleBackground(s.getColor());
                        }
                        sgp.setTitleBackground(Color.RED);
                        sgp.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        selected = goal.getGoalAt(0);
                        selectedNumber = stepNumber;
                        pnlGoals.repaint();
                    }
                }


                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                }
            });
        }
    }


    private String getSubgoalsTitle(int stepIndex) {
        if (stepIndex < 1) {
            return i18n("PROOF_PANEL_INIT_GOAL_TITLE");
        } else {
            return i18n("PROOF_PANEL_GOAL_TITLE", stepIndex);
        }
    }

    private <TRuleArg extends RuleArg> String getStepDescription(InferenceRule<? super TRuleArg> rule, TRuleArg args, RuleApplicationType type) {
        if (args instanceof SubgoalIndexArg) {
            return i18n("PROOF_PANEL_STEP_DESC_SUBGOAL", rule.getProvider().getPrettyName(), ((SubgoalIndexArg) args).getSubgoalIndex() + 1, type.getName());
        } else {
            return i18n("PROOF_PANEL_STEP_DESC_GENERAL", rule.getProvider().getPrettyName(), type.getName());
        }
    }



    /**
     * Puts a header saying there are no goals on which one could apply
     * inference rules.
     */
    private void displayEmptyGoals() {
        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        SubgoalsPanel sgp = new SubgoalsPanel(i18n("PROOF_PANEL_NO_GOALS"));
        pnlGoals.add(sgp, gbc);
        subgoals = new ArrayList<>();
        selected = null;
    }

    private <TRuleArg extends RuleArg> void addProofFinished(InferenceRule<? super TRuleArg> rule, TRuleArg args, RuleApplicationType type) {
        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        SubgoalsPanel sgp = new SubgoalsPanel(i18n("PROOF_PANEL_PROOF_FINISHED"), getStepDescription(rule, args, type), Color.GREEN);
        addSubgoal(gbc, sgp);
        validate();
    }

    /**
     *
     * @param initialGoals
     * @param forceValidation tells whether the panel should be {@link JPanel#validate() validated}
     * just after the UI has been updated.
     */
    private void resetProof(Goals initialGoals, boolean forceValidation) {
        proof = new ProofTrace(initialGoals);
        // Clean the current user interface
        cleanProofPanel();
        // And display the initial goals.
        displayInitialGoals();
        if (forceValidation) {
            validate();
        }
    }

    private void cleanProofPanel() {
        pnlGoals.removeAll();
        subgoals.clear();
        selected = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Initialisation Methods">
    private void initialiseProver() {
        Preferences prefs = Preferences.userNodeForPackage(SettingsDialog.class);
        String proverName = prefs.get(AutomaticProvers.prover_preference, null);
        if (proverName == null) {
            prover = new BreadthFirstProver();
        } else {
            prover = AutomaticProvers.getAutomaticProver(proverName);
        }
        String strategyName = prefs.get(Strategies.strategy_preference, null);
        if (strategyName == null) {
            prover.setStrategy(new NoStrategy());
        } else {
            prover.setStrategy(Strategies.getStrategy(strategyName));
        }
    }
    // </editor-fold>

}
