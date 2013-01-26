package am.app.mappingEngine.MachineLearning;



import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;


/**
 * This is the Manual Combination Parameters Panel. 
 * @author Flavio
 *
 */
public class machineLearningParametersPanel extends AbstractMatcherParametersPanel implements ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private machineLearningParameters parameters;
	private List<AbstractMatcher> inputMatchers;
	private JLabel topLabel;
	private JLabel combinationTypeL;
	private JComboBox combOperationsCombo;

	
	public Modes mode;
	

	private JComboBox[] inputMatchersCombo;
	private JLabel[] inputMatchersLabel;
	

	
	
	public machineLearningParametersPanel(List<AbstractMatcher> matchers) {
		super();
		//init components
		inputMatchers = matchers;
		topLabel = new JLabel("Select the feature vector combination to be used by machine learning matcher");
		combinationTypeL = new JLabel("select feature vector: ");
		String[] operations = {machineLearningParameters.Matsim, machineLearningParameters.Matfound,machineLearningParameters.Matvote };
		combOperationsCombo = new JComboBox(operations);
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
			String name = a.getRegistryEntry().getMatcherName();
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
							.addComponent(combOperationsCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)) 	
						
					
					.addGroup(layout.createSequentialGroup()
						
					 )
					
					.addGroup(matchersHorizGroup)
					
					);
		
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(layout.createSequentialGroup()
		
				.addComponent(topLabel)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(combinationTypeL)
						.addComponent(combOperationsCombo))
			.addGroup(matcherVertGroup)
			.addGap(30)
			
		);
		
	}
	
	public machineLearningParameters getParameters() {
		int size = inputMatchers.size();
		parameters = new machineLearningParameters();
		parameters.featureType = combOperationsCombo.getSelectedItem().toString();
		return parameters;
	}


	public void itemStateChanged(ItemEvent e) {
		
	}

	
	
}
