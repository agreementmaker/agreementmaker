package agreementMaker.application.mappingEngine.qualityCombination;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.acl.Group;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.border.EmptyBorder;

import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.MatcherSetting;
import agreementMaker.application.mappingEngine.MatchersRegistry;
import agreementMaker.userInterface.AppPreferences;


/**
 * This is the preferences panel for the DSI algorithm.
 * The user can set the MCP from this panel.
 * @author cosmin
 * @date Nov 25, 2008
 *
 */
public class QualityCombinationParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {
  // TO BE DONE YET
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private QualityCombinationParameters parameters;
	private ArrayList<AbstractMatcher> inputMatchers;
	private JLabel topLabel;
	private JLabel combinationTypeL;

	private JComboBox combOperationsCombo;
	
	private JLabel weightsLabel;
	private JComboBox[] inputMatchersCombo;

	
	
	public QualityCombinationParametersPanel(ArrayList<AbstractMatcher> matchers) {
		super();
		//init components
		inputMatchers = matchers;
		topLabel = new JLabel("Select the operation that will be used to combine alignments for each pair (Source Node, Target Node).");
		combinationTypeL = new JLabel("Combining operation: ");
		String[] operations = {QualityCombinationParameters.AVERAGECOMB,QualityCombinationParameters.WEIGHTAVERAGE, QualityCombinationParameters.MAXCOMB, QualityCombinationParameters.MINCOMB};
		combOperationsCombo = new JComboBox(operations);
		combOperationsCombo.addItemListener(this);
		weightsLabel = new JLabel("Select weights to assign to each matcher in input if you have selected Weighted Average Operation.");

		//LAYOUT: grouplayout is already complicated but very flexible, plus in this case the matchers list is dynamic so it's even more complicated
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//Creation of groups for the matchers list. for each input matcher name and weight , we always need horizontal and vertical groups
		ParallelGroup matchersHorizGroup = layout.createParallelGroup();
		SequentialGroup matcherVertGroup = layout.createSequentialGroup();
		inputMatchersCombo = new JComboBox[matchers.size()];
		String[] percents = Utility.getPercentStringList();
		JLabel nameLabel;
		JComboBox weightCombo;
		AbstractMatcher a;
		for(int i = 0; i< matchers.size();i++) {
			a = matchers.get(i);
			String name =( (MatchersRegistry)a.getName()).getMatcherName();
			nameLabel = new JLabel(a.getIndex()+". "+name);
			weightCombo = new JComboBox(percents);
			weightCombo.setSelectedIndex(percents.length-1);
			weightCombo.setEnabled(false);
			inputMatchersCombo[i] = weightCombo;
			matchersHorizGroup.addGroup(layout.createSequentialGroup()
					.addComponent(nameLabel)
					.addComponent(weightCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
			);
			matcherVertGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(nameLabel)
					.addComponent(weightCombo)
			);
		}
		
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(topLabel) 
					.addGroup(layout.createSequentialGroup()
							.addComponent(combinationTypeL) 			
							.addComponent(combOperationsCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
							)
					.addComponent(weightsLabel)
					.addGroup(matchersHorizGroup)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(topLabel)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(combinationTypeL)
					.addComponent(combOperationsCombo)
			)
			.addGap(20)
			.addComponent(weightsLabel)
			.addGroup(matcherVertGroup)
		);
	}
	
	public QualityCombinationParameters getParameters() {
		parameters = new QualityCombinationParameters();
		parameters.combinationType = (String)combOperationsCombo.getSelectedItem();
		double[] weights = new double[inputMatchers.size()];
		for(int i= 0; i<weights.length; i++) {
			weights[i] = Utility.getDoubleFromPercent((String)inputMatchersCombo[i].getSelectedItem());
		}
		parameters.weights = weights;
		return parameters;
		
	}

	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getItemSelectable();
		if(obj == combOperationsCombo) {
			boolean enable = false;
			if(combOperationsCombo.getSelectedItem().equals(QualityCombinationParameters.WEIGHTAVERAGE)) 
				enable = true;
			for(int i = 0; i < inputMatchersCombo.length;i++ ) {
				inputMatchersCombo[i].setEnabled(enable);
			}
		}
		
	}

	
	
	
}
