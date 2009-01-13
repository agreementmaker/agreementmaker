package agreementMaker.application.mappingEngine.Combination;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import com.ibm.icu.lang.UCharacter.JoiningGroup;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.MatchersRegistry;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluationData;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;


/**
 * This is the Manual Combination Parameters Panel. 
 * @author Flavio
 *
 */
public class CombinationParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private CombinationParameters parameters;
	private ArrayList<AbstractMatcher> inputMatchers;
	private JLabel topLabel;
	private JLabel combinationTypeL;

	private JComboBox combOperationsCombo;
	
	
	private JLabel radioLabel;
	private ButtonGroup radioGroupButton;
	private JRadioButton noWeightsRadio;
	private JRadioButton manualWeightsRadio;
	private JRadioButton qualityWeightsRadio;
	private JRadioButton bothWeightsRadio;
	private JLabel noWeightsLabel;
	private JLabel manualWeightsLabel;
	private JLabel qualityWeightsLabel;
	private JLabel bothWeightsLabel;
	
	
	private JLabel weightsLabel;
	private JComboBox[] inputMatchersCombo;
	private JLabel[] inputMatchersLabel;
	
	private JLabel qualityLabel;
	private JComboBox qualityCombo;
	
	
	public CombinationParametersPanel(ArrayList<AbstractMatcher> matchers) {
		super();
		//init components
		inputMatchers = matchers;
		topLabel = new JLabel("Select the operation that will be used to combine alignments for each pair (Source Node, Target Node).");
		combinationTypeL = new JLabel("Combining operation: ");
		String[] operations = {CombinationParameters.SIGMOIDAVERAGECOMB, CombinationParameters.AVERAGECOMB, CombinationParameters.MAXCOMB, CombinationParameters.MINCOMB};
		combOperationsCombo = new JComboBox(operations);
		
		radioLabel = new JLabel("Operations can be weighted or not. Select weights assignment method if needed.");
		noWeightsRadio = new JRadioButton();
		noWeightsRadio.setSelected(true);
		noWeightsRadio.addItemListener(this);
		noWeightsLabel = new JLabel("Non weighted");
		manualWeightsRadio = new JRadioButton();
		manualWeightsRadio.addItemListener(this);
		manualWeightsLabel = new JLabel("Manual assignment");
	    qualityWeightsRadio = new JRadioButton();
	    qualityWeightsRadio.addItemListener(this);
	    qualityWeightsLabel = new JLabel("Quality evaluation assignment");
	    bothWeightsRadio = new JRadioButton();
	    bothWeightsRadio.addItemListener(this);
	    bothWeightsLabel = new JLabel("Manual & Quality");
		radioGroupButton = new ButtonGroup();
		radioGroupButton.add(noWeightsRadio);
		radioGroupButton.add(manualWeightsRadio);
		radioGroupButton.add(qualityWeightsRadio);
		radioGroupButton.add(bothWeightsRadio);
		
		weightsLabel = new JLabel("Select weights to assign to each matcher in input.");
		weightsLabel.setEnabled(false);
		//the list of input matchers is created dinamically in the group layout
		
		qualityLabel = new JLabel("Select the quality measure to be used as weight.");
		qualityCombo = new JComboBox(QualityEvaluator.QUALITIES);
		qualityLabel.setEnabled(false);
		qualityCombo.setEnabled(false);
		
		//LAYOUT: grouplayout is already complicated but very flexible, plus in this case the matchers list is dynamic so it's even more complicated
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//Creation of groups for the matchers list. for each input matcher name and weight , we always need horizontal and vertical groups
		ParallelGroup matchersHorizGroup = layout.createParallelGroup();
		SequentialGroup matcherVertGroup = layout.createSequentialGroup();
		inputMatchersCombo = new JComboBox[matchers.size()];
		inputMatchersLabel = new JLabel[matchers.size()];
		String[] percents = Utility.getPercentStringList();
		JLabel nameLabel;
		JComboBox weightCombo;
		AbstractMatcher a;
		for(int i = 0; i< matchers.size();i++) {
			a = matchers.get(i);
			String name =( (MatchersRegistry)a.getName()).getMatcherName();
			nameLabel = new JLabel(a.getIndex()+". "+name);
			nameLabel.setEnabled(false);
			inputMatchersLabel[i] = nameLabel;
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
					.addComponent(radioLabel)
					.addGroup(layout.createSequentialGroup()
						.addComponent(noWeightsRadio) 			
						.addComponent(noWeightsLabel)
						.addGap(10)
						.addComponent(manualWeightsRadio) 			
						.addComponent(manualWeightsLabel)
						.addGap(10)
						.addComponent(qualityWeightsRadio) 			
						.addComponent(qualityWeightsLabel)
						.addGap(10)
						.addComponent(bothWeightsRadio) 			
						.addComponent(bothWeightsLabel)
					 )
					.addComponent(weightsLabel)
					.addGroup(matchersHorizGroup)
					.addGroup(layout.createSequentialGroup()
						.addComponent(qualityLabel)
						.addComponent(qualityCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					)
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
			.addComponent(radioLabel)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(noWeightsRadio) 			
				.addComponent(noWeightsLabel)
				.addComponent(manualWeightsRadio) 			
				.addComponent(manualWeightsLabel)
				.addComponent(qualityWeightsRadio) 			
				.addComponent(qualityWeightsLabel)
				.addComponent(bothWeightsRadio) 			
				.addComponent(bothWeightsLabel)
			 )
			.addGap(30)
			.addComponent(weightsLabel)
			.addGroup(matcherVertGroup)
			.addGap(30)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(qualityLabel)
					.addComponent(qualityCombo)
			)
		);
		
	}
	
	public CombinationParameters getParameters() {
		int size = inputMatchers.size();
		parameters = new CombinationParameters();
		parameters.combinationType = (String)combOperationsCombo.getSelectedItem();
		

		parameters.manualWeighted = false;
		parameters.qualityEvaluation = false;
		//in the non-weighted case and manually weigthed case, weights are assigned here staticcaly,
		//in the quality evaluation case weights will be assigned later by the algorithm
		parameters.matchersWeights = new double[size];
		if(qualityWeightsRadio.isSelected() || bothWeightsRadio.isSelected()) {
			parameters.qualityEvaluation = true;
			parameters.quality = (String)qualityCombo.getSelectedItem();
			//weights will be assigned later by the matcher in this case, which will invoke the qualityEvaluation
		}
		if (noWeightsRadio.isSelected() || manualWeightsRadio.isSelected() || bothWeightsRadio.isSelected()) {
			//both in the non weighted and weighted case weights are assigned,but in the first case they are all 1.
			//so is not strange that manual weighte is true even in the non weighted case
			parameters.manualWeighted = true;
			for(int i = 0; i < size; i++) {
				double measure = 1;
				if(manualWeightsRadio.isSelected()) {
					measure = Utility.getDoubleFromPercent((String)inputMatchersCombo[i].getSelectedItem());
				}//else No-weighted is selected so it's correct to have a weight equal to 1 for all matchers.
				parameters.matchersWeights[i] = measure;
			}
		}
		return parameters;
	}

	public void itemStateChanged(ItemEvent e) {
		if(noWeightsRadio.isSelected()) {
			setEnableQuality(false);
			setEnableManual(false);
		}
		if(manualWeightsRadio.isSelected()) {
			setEnableQuality(false);
			setEnableManual(true);
		}
		if(qualityWeightsRadio.isSelected()) {
			setEnableQuality(true);
			setEnableManual(false);
		}
		if(bothWeightsRadio.isSelected()) {
			setEnableManual(true);
			setEnableQuality(true);
		}
		
	}

	private void setEnableManual(boolean b) {
		weightsLabel.setEnabled(b);
		for(int i = 0; i < inputMatchersCombo.length; i++) {
			inputMatchersCombo[i].setEnabled(b);
			inputMatchersLabel[i].setEnabled(b);
		}
	}

	private void setEnableQuality(boolean b) {
		qualityLabel.setEnabled(b);
		qualityCombo.setEnabled(b);
	}



	
	
	
}
