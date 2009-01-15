package agreementMaker.application.mappingEngine.parametricStringMatcher;


import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.StringUtil.ISub;
import agreementMaker.application.mappingEngine.StringUtil.NormalizerParameter;

public class ParametricStringParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private ParametricStringParameters parameters;
	
	private JLabel titleLabel = new JLabel("Advanced parametrization is allowed. Non exepert users can keep default values.");
	private JLabel selectMetricLabel = new JLabel("Select string similarity metric: "); 
	private JComboBox metricsCombo;
	private JLabel conceptStringsLabel = new JLabel("Select different weights to assign relevance to each Concept String.");
	private JLabel weightsNormLabel = new JLabel("Weights will be normalized if the total is different from 100%.");
	
	private JLabel localnameLabel = new JLabel("Local-name: ");
	private JComboBox localnameCombo;
	private JLabel labelLabel = new JLabel("Label: ");
	private JComboBox labelCombo;
	private JLabel commentLabel = new JLabel("Comment: ");
	private JComboBox commentCombo;
	private JLabel isDefnedByLabel = new JLabel("isDefnedBy: ");
	private JComboBox isDefnedByCombo;
	private JLabel seeAlsoLabel = new JLabel("seeAlso: ");
	private JComboBox seeAlsoCombo;


	private JLabel redistributeLabel = new JLabel("Do not consider empty concept strings and redistribute weights proportionally.");
	private JCheckBox redistributeCheck;
	
	private JLabel preprocessLabel = new JLabel("Preprocessing strings may give better results.");
	private JLabel blankLabel = new JLabel("Blank normalization (e.g. 'myHome' or 'my_home' --> 'my home').");
	private JCheckBox blankCheck;
	private JLabel punctLabel = new JLabel("Punctuation normalization (replacing . , ; : ' ! ?).") ;
	private JCheckBox  punctCheck;
	private JLabel diacLabel = new JLabel("Diacritics normalization (e.g. '� --> a' or '� --> o' ).");
	private JCheckBox diacCheck;
	private JLabel digitLabel = new JLabel("Digit suppression (removing any number from the strings).");
	private JCheckBox digitCheck;
	private JLabel stopLabel = new JLabel("Stop-words removing (e.g. 'a' 'to' 'for'...)");
	private JCheckBox stopCheck;
	private JLabel stemLabel = new JLabel("Apply stemming ( 'dogs' --> 'dog' or 'saying' --> 'say' ) ");
	private JCheckBox stemCheck;

	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public ParametricStringParametersPanel() {
		
		super();
		//init components
		String[] metricsList = {ParametricStringParameters.EDIT, ParametricStringParameters.AMSUB, ParametricStringParameters.SUB, ParametricStringParameters.JARO, ParametricStringParameters.QGRAM, ParametricStringParameters.ISUB};
		metricsCombo = new JComboBox(metricsList);
		
		String[] percents = Utility.getPercentStringList();
		localnameCombo = new JComboBox(percents);
		localnameCombo.setSelectedItem("70%");
		labelCombo = new JComboBox(percents);
		labelCombo.setSelectedItem("0%");
		commentCombo = new JComboBox(percents);
		commentCombo.setSelectedItem("20%");
		seeAlsoCombo = new JComboBox(percents);
		seeAlsoCombo.setSelectedItem("5%");
		isDefnedByCombo = new JComboBox(percents);
		isDefnedByCombo.setSelectedItem("5%");
		
		redistributeCheck = new JCheckBox();
		redistributeCheck.setSelected(true);
		
		redistributeCheck = new JCheckBox();
		redistributeCheck.setSelected(true);
		blankCheck = new JCheckBox();
		blankCheck.setSelected(true);
		punctCheck = new JCheckBox();
		punctCheck.setSelected(true);
		diacCheck = new JCheckBox();
		diacCheck.setSelected(true);
		digitCheck = new JCheckBox();
		digitCheck.setSelected(false);
		stopCheck = new JCheckBox();
		stopCheck.setSelected(true);
		stemCheck = new JCheckBox();
		stemCheck.setSelected(true);
		

		//LAYOUT: grouplayout is already complicated but very flexible, plus in this case the matchers list is dynamic so it's even more complicated
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(titleLabel)
			.addGroup(layout.createSequentialGroup()
					.addComponent(selectMetricLabel) 			
					.addComponent(metricsCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					)
			.addComponent(conceptStringsLabel)
			.addComponent(weightsNormLabel)
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(localnameLabel)
						.addComponent(labelLabel) 
						.addComponent(commentLabel) 		
						.addComponent(seeAlsoLabel) 
						.addComponent(isDefnedByLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(localnameCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						.addComponent(labelCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						.addComponent(commentCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						.addComponent(seeAlsoCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						.addComponent(isDefnedByCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					)
			)
			.addGroup(layout.createSequentialGroup()
					.addComponent(redistributeCheck) 			
					.addComponent(redistributeLabel)
					)
			.addComponent(preprocessLabel)
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(blankCheck)
						.addComponent(punctCheck) 
						.addComponent(diacCheck) 		
						.addComponent(digitCheck) 
						.addComponent(stopCheck)
						.addComponent(stemCheck) 	
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(blankLabel) 	
						.addComponent(punctLabel) 	
						.addComponent(diacLabel) 	
						.addComponent(digitLabel) 	
						.addComponent(stopLabel) 	
						.addComponent(stemLabel)
					)
			)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(titleLabel)
				.addGap(30)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(selectMetricLabel) 			
						.addComponent(metricsCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGap(30)
				.addComponent(conceptStringsLabel)
				.addComponent(weightsNormLabel)
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(localnameLabel) 			
						.addComponent(localnameCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(labelLabel) 			
						.addComponent(labelCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(commentLabel) 			
						.addComponent(commentCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(seeAlsoLabel) 			
						.addComponent(seeAlsoCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(isDefnedByLabel) 			
						.addComponent(isDefnedByCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
						)
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(redistributeCheck) 			
						.addComponent(redistributeLabel)
						)
				.addGap(30)
				.addComponent(preprocessLabel)
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(blankCheck) 			
						.addComponent(blankLabel)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(punctCheck) 			
						.addComponent(punctLabel)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(diacCheck) 			
						.addComponent(diacLabel)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(digitCheck) 			
						.addComponent(digitLabel)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(stopCheck) 			
						.addComponent(stopLabel)
						)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(stemCheck) 			
						.addComponent(stemLabel)
						)
				.addGap(30)
			);
	}
	
	
	public AbstractParameters getParameters() {
		parameters = new ParametricStringParameters();
		
		parameters.measure = (String)metricsCombo.getSelectedItem();
		parameters.redistributeWeights = redistributeCheck.isSelected();
		
		parameters.localWeight = Utility.getDoubleFromPercent((String)localnameCombo.getSelectedItem());
		parameters.labelWeight = Utility.getDoubleFromPercent((String)labelCombo.getSelectedItem());
		parameters.commentWeight = Utility.getDoubleFromPercent((String)commentCombo.getSelectedItem());
		parameters.seeAlsoWeight = Utility.getDoubleFromPercent((String)seeAlsoCombo.getSelectedItem());
		parameters.isDefinedByWeight = Utility.getDoubleFromPercent((String)isDefnedByCombo.getSelectedItem());
		
		parameters.normParameter = new NormalizerParameter();
		parameters.normParameter.normalizeBlank = blankCheck.isSelected();
		parameters.normParameter.normalizePunctuation = punctCheck.isSelected();
		parameters.normParameter.normalizeDiacritics = diacCheck.isSelected();
		parameters.normParameter.normalizeDigit = digitCheck.isSelected();
		parameters.normParameter.removeStopWords = stopCheck.isSelected();
		parameters.normParameter.stem = stemCheck.isSelected();
		
		return parameters;
		
	}
	
	public String checkParameters() {
		return null;
	}
}
