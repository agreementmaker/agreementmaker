package agreementMaker.application.mappingEngine.manualCombination;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.MatcherSetting;
import agreementMaker.userInterface.AppPreferences;


/**
 * This is the preferences panel for the DSI algorithm.
 * The user can set the MCP from this panel.
 * @author cosmin
 * @date Nov 25, 2008
 *
 */
public class ManualCombinationParametersPanel extends AbstractMatcherParametersPanel {
  // TO BE DONE YET
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private ManualCombinationParameters parameters;
	private ArrayList<AbstractMatcher> inputMatchers;
	private JLabel topLabel;
	private JLabel combinationTypeL;
	private JComboBox combOperationsCombo;
	private JLabel[] inputMatchersL;

	
	
	public ManualCombinationParametersPanel(ArrayList<AbstractMatcher> matchers) {
		super();
		inputMatchers = matchers;
		topLabel = new JLabel("Select the operation that will be used to combine alignments for each pair (Source Node, Target Node).");
		combinationTypeL = new JLabel("Combining operation: ");
		String[] operations = {ManualCombinationParameters.AVERAGECOMB,ManualCombinationParameters.WEIGHTAVERAGE, ManualCombinationParameters.MAXCOMB, ManualCombinationParameters.MINCOMB};
		combOperationsCombo = new JComboBox(operations);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(topLabel) 					// fileType label
					.addGroup(layout.createSequentialGroup()
							.addComponent(combinationTypeL) 			// filepath text
							.addComponent(combOperationsCombo) 	
							)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
					.addComponent(topLabel)
					.addGroup(layout.createParallelGroup()
							.addComponent(combinationTypeL)
							.addComponent(combOperationsCombo)
							)
				)
		);
	}
	
	public ManualCombinationParameters getParameters() {
		parameters = new ManualCombinationParameters();
		parameters.combinationType = (String)combOperationsCombo.getSelectedItem();
		double[] weights = new double[inputMatchers.size()];
		parameters.weights = weights;
		return parameters;
		
	}

	
	
	
}
