package agreementMaker.application.mappingEngine.qualityCombination;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;


/**
 * This is the preferences panel for the Quality Combination Matcher.
 * @author Flavio
 *
 */
public class QualityCombinationParametersPanel extends AbstractMatcherParametersPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3220525418599504107L;

	private QualityCombinationParameters parameters;
	private JLabel topLabel;
	private JLabel qualityMeasureLabel;

	private JComboBox qualityMeasureCombo;

	
	
	public QualityCombinationParametersPanel() {
		super();
		//init components
		topLabel = new JLabel("The matcher will perform a Weighted Average Combination of input similarity matrices.\n Weights are assigned evaluating matchers' quality.");
		qualityMeasureLabel = new JLabel("Select quality measure: ");
		String[] measures = {QualityEvaluator.LOCAL,QualityEvaluator.GLOBAL, QualityEvaluator.COMBINED};
		qualityMeasureCombo = new JComboBox(measures);

		//LAYOUT: grouplayout is already complicated but very flexible, plus in this case the matchers list is dynamic so it's even more complicated
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(topLabel) 
					.addGroup(layout.createSequentialGroup()
							.addComponent(qualityMeasureLabel) 			
							.addComponent(qualityMeasureCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
							)
		);
		
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
			 layout.createSequentialGroup()
			.addComponent(topLabel)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(qualityMeasureLabel)
					.addComponent(qualityMeasureCombo)
			)
		);
	}
	
	public QualityCombinationParameters getParameters() {
		parameters = new QualityCombinationParameters();
		parameters.qualityMeasure = (String)qualityMeasureCombo.getSelectedItem();
		return parameters;	
	}
}
