package am.app.mappingEngine.multiWords;


import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;


public class MultiWordsPairWiseParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private MultiWordsPairWiseParameters parameters;
	
	private ArrayList<AbstractMatcher> inputMatchers;
	
	private JLabel titleLabel = new JLabel("Advanced parametrization is allowed. Non exepert users can keep default values.");
	private JLabel selectMetricLabel = new JLabel("Select multi-words string similarity metric: "); 
	private JComboBox metricsCombo;
	
	private JLabel whichTermsLabel = new JLabel("For each concept a multi-words string is built considering several terms related to it.");
	private JCheckBox chkConceptTerms = new JCheckBox("Consider concept's terms (localname, label, comment, seeAlso, isDefBy).");
	private JCheckBox  neighbourCheck = new JCheckBox("Consider neighbours terms (parents, siblings, descendants).");
	private JCheckBox indCheck = new JCheckBox("Consider individuals.");
	private JCheckBox propCheck = new JCheckBox("If the concept is a class -> consider localnames of properties declared by this class");
	private JCheckBox classCheck = new JCheckBox("If the concept is a property -> consider localnames of classes declaring this property");
	private JCheckBox localCheck = new JCheckBox("Do not consider localnames (to be selected when they are just meaningless codes. It will affect all sources).");
	private JCheckBox lexCheck = new JCheckBox("Use Lexicon definitions.");
	private JCheckBox lexSynonymsCheck = new JCheckBox("Use Lexicon synonyms.");
	private JCheckBox chkConsiderSuperClass = new JCheckBox("Consider super class labels.");
	private JCheckBox chkExtendSynonyms = new JCheckBox("Extend synonyms.");
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public MultiWordsPairWiseParametersPanel(ArrayList<AbstractMatcher> matchers) {
		
		super();
		//init components
		inputMatchers = matchers;
		String[] metricsList = {MultiWordsPairWiseParameters.TFIDF, MultiWordsPairWiseParameters.EUCLIDEAN, MultiWordsPairWiseParameters.COSINE, MultiWordsPairWiseParameters.JACCARD,  MultiWordsPairWiseParameters.DICE, MultiWordsPairWiseParameters.Overlap};
		metricsCombo = new JComboBox(metricsList);
		


		chkConceptTerms.setSelected(true);
		neighbourCheck.setSelected(false);
		indCheck.setSelected(false);
		propCheck.setSelected(false);
		classCheck.setSelected(false);
		localCheck.setSelected(true);
		lexCheck.setSelected(false);
		lexSynonymsCheck.setSelected(false);
		chkExtendSynonyms.setSelected(false);

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
			.addComponent(whichTermsLabel)
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lexCheck)
						.addComponent(lexSynonymsCheck)
						.addComponent(chkConceptTerms)
						.addComponent(neighbourCheck) 
						.addComponent(indCheck) 		
						.addComponent(propCheck) 
						.addComponent(classCheck) 
						.addComponent(localCheck) 
						.addComponent(chkConsiderSuperClass)
						.addComponent(chkExtendSynonyms)
					)
/*					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lexLabel)
						.addComponent(lexSynonymsLabel)
						.addComponent(conceptLabel) 	
						.addComponent(neighbourLabel) 	
						.addComponent(indLabel) 
						.addComponent(propLabel) 
						.addComponent(classLabel) 
						.addComponent(localLabel) 	
					)*/
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
				.addComponent(whichTermsLabel)
				.addGap(10)
				.addComponent(lexCheck)
				.addComponent(lexSynonymsCheck)
				.addComponent(chkConceptTerms) 
				.addComponent(neighbourCheck) 
				.addComponent(indCheck)
				.addComponent(propCheck)
				.addComponent(classCheck)
				.addComponent(localCheck)
				.addComponent(chkConsiderSuperClass)
				.addComponent(chkExtendSynonyms)
						
				.addGap(30)
			);
	}
	
	
	public AbstractParameters getParameters() {
		parameters = new MultiWordsPairWiseParameters();
		
		parameters.measure = (String)metricsCombo.getSelectedItem();
		parameters.considerInstances = indCheck.isSelected();
		parameters.ignoreLocalNames = localCheck.isSelected();
		parameters.considerNeighbors  = neighbourCheck.isSelected();
		parameters.considerProperties     = propCheck.isSelected();
		parameters.considerClasses   = classCheck.isSelected();
		parameters.considerConcept = chkConceptTerms.isSelected();
		
		parameters.useLexiconDefinitions = lexCheck.isSelected();
		parameters.useLexiconSynonyms = lexSynonymsCheck.isSelected();
		
		parameters.considerSuperClass = chkConsiderSuperClass.isSelected();
		
		parameters.extendSynonyms=chkExtendSynonyms.isSelected();
		//normalization parameters are set in the MultiWordsParameters() because are not user input;
		return parameters;
		
	}
	
	public String checkParameters() {
		/*if(!(indCheck.isSelected() || neighbourCheck.isSelected() || chkConceptTerms.isSelected() || propCheck.isSelected() || classCheck.isSelected())) {
			return "At least one of the three terms sources must be selected.\n Select concept's or neighbours or individuals terms.";
		}*/
		return null;
	}
}
