package am.app.mappingEngine.multiWords;


import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;

import com.hp.hpl.jena.ontology.OntProperty;


public class MultiWordsParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * Base Similarity Matcher - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Nov 22, 2008
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private MultiWordsParameters parameters;
	
	private JLabel titleLabel = new JLabel("Advanced parametrization is allowed. Non exepert users can keep default values.");
	private JLabel selectMetricLabel = new JLabel("Select multi-words string similarity metric: "); 
	private JComboBox metricsCombo;
	
	private JLabel whichTermsLabel = new JLabel("For each concept a multi-words string is built considering several terms related to it.");
	private JCheckBox chkConceptTerms = new JCheckBox("Consider concept's terms (localname, label, comment, seeAlso, isDefBy).");
	private JCheckBox  neighbourCheck = new JCheckBox("Consider neighbours terms (parents, siblings, descendants).");
	private JCheckBox  subclassesCheck = new JCheckBox("Consider subclasses terms.");
	private JCheckBox indCheck = new JCheckBox("Consider individuals.");
	private JCheckBox propCheck = new JCheckBox("If the concept is a class -> consider localnames of properties declared by this class");
	private JCheckBox classCheck = new JCheckBox("If the concept is a property -> consider localnames of classes declaring this property");
	private JCheckBox localCheck = new JCheckBox("Do not consider localnames (to be selected when they are just meaningless codes. It will affect all sources).");
	private JCheckBox lexCheck = new JCheckBox("Use Lexicon definitions.");
	private JCheckBox lexSynonymsCheck = new JCheckBox("Use Lexicon synonyms.");
	private JCheckBox chkConsiderSuperClass = new JCheckBox("Consider super class labels.");
	
	
	private JCheckBox chkSourceHierarchies = new JCheckBox("Source alternate hierarchy parents:"); 
	private JCheckBox chkTargetHierarchies = new JCheckBox("Target alternate hierarchy parents:");
	private JCheckBox chkSourceChildrenHierarchies = new JCheckBox("Source alternate hierarchy children:"); 
	private JCheckBox chkTargetChildrenHierarchies = new JCheckBox("Target alternate hierarchy children:");
	private JComboBox cmbSourceHierarchies = new JComboBox();
	private JComboBox cmbTargetHierarchies = new JComboBox();
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public MultiWordsParametersPanel() {
		
		super();
		//init components
		String[] metricsList = {MultiWordsParameters.TFIDF, MultiWordsParameters.EUCLIDEAN, MultiWordsParameters.COSINE, MultiWordsParameters.JACCARD,  MultiWordsParameters.DICE};
		metricsCombo = new JComboBox(metricsList);
		


		chkConceptTerms.setSelected(true);
		neighbourCheck.setSelected(false);
		subclassesCheck.setSelected(false);
		indCheck.setSelected(false);
		propCheck.setSelected(false);
		classCheck.setSelected(false);
		localCheck.setSelected(true);
		lexCheck.setSelected(false);
		lexSynonymsCheck.setSelected(false);

		Set<OntProperty> sourceHierarchyProperties = Core.getInstance().getSourceOntology().getHierarchyProperties();
		for( OntProperty prop : sourceHierarchyProperties ) {
			cmbSourceHierarchies.addItem(prop);
		}
		if( sourceHierarchyProperties.isEmpty() ) {
			chkSourceHierarchies.setEnabled(false);
			chkSourceChildrenHierarchies.setEnabled(false);
			cmbSourceHierarchies.setEnabled(false);
		}
		
		Set<OntProperty> targetHierarchyProperties = Core.getInstance().getTargetOntology().getHierarchyProperties();
		for( OntProperty prop : targetHierarchyProperties ) {
			cmbTargetHierarchies.addItem(prop);
		}
		if( targetHierarchyProperties.isEmpty() ) {
			chkTargetHierarchies.setEnabled(false);
			chkTargetChildrenHierarchies.setEnabled(false);
			cmbTargetHierarchies.setEnabled(false);
		}
		
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
						.addComponent(subclassesCheck) 
						.addComponent(indCheck) 		
						.addComponent(propCheck) 
						.addComponent(classCheck) 
						.addComponent(localCheck) 
						.addComponent(chkConsiderSuperClass)
						.addComponent(chkSourceHierarchies)
						.addComponent(chkSourceChildrenHierarchies)
						.addComponent(cmbSourceHierarchies)
						.addComponent(chkTargetHierarchies)
						.addComponent(chkTargetChildrenHierarchies)
						.addComponent(cmbTargetHierarchies)
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
				.addComponent(subclassesCheck) 
				.addComponent(indCheck)
				.addComponent(propCheck)
				.addComponent(classCheck)
				.addComponent(localCheck)
				.addComponent(chkConsiderSuperClass)
				.addGap(30)
				.addComponent(chkSourceHierarchies)
				.addComponent(chkSourceChildrenHierarchies)
				.addComponent(cmbSourceHierarchies)
				.addComponent(chkTargetHierarchies)
				.addComponent(chkTargetChildrenHierarchies)
				.addComponent(cmbTargetHierarchies)
			);
	}
	
	
	public DefaultMatcherParameters getParameters() {
		parameters = new MultiWordsParameters();
		
		parameters.measure = (String)metricsCombo.getSelectedItem();
		parameters.considerInstances = indCheck.isSelected();
		parameters.ignoreLocalNames = localCheck.isSelected();
		parameters.considerNeighbors  = neighbourCheck.isSelected();
		parameters.considerSubclasses = subclassesCheck.isSelected();
		parameters.considerProperties     = propCheck.isSelected();
		parameters.considerClasses   = classCheck.isSelected();
		parameters.considerConcept = chkConceptTerms.isSelected();
		
		parameters.useLexiconDefinitions = lexCheck.isSelected();
		parameters.useLexiconSynonyms = lexSynonymsCheck.isSelected();
		
		parameters.considerSuperClass = chkConsiderSuperClass.isSelected();
		
		if( chkSourceHierarchies.isSelected() && cmbSourceHierarchies.isEnabled() ) {
			parameters.sourceAlternateHierarchy = (OntProperty) cmbSourceHierarchies.getSelectedItem();
		}
		
		if( chkSourceChildrenHierarchies.isSelected() && cmbSourceHierarchies.isEnabled() ) {
			parameters.sourceAlternateChildren = true;
		}
		
		if( chkTargetHierarchies.isSelected() && cmbTargetHierarchies.isEnabled() ) {
			parameters.targetAlternateHierarchy = (OntProperty) cmbTargetHierarchies.getSelectedItem();
		}
		
		if( chkTargetChildrenHierarchies.isSelected() && cmbTargetHierarchies.isEnabled() ) {
			parameters.targetAlternateChildren = true;
		}
		
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
