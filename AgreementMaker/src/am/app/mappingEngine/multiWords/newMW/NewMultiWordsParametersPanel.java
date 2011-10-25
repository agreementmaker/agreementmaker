package am.app.mappingEngine.multiWords.newMW;


import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;


public class NewMultiWordsParametersPanel extends AbstractMatcherParametersPanel {

	/**
	 * VMM - The Parameters Panel
	 * @author Cosmin Stroe
	 * @date Sept 14, 2011
	 * ADVIS @ UIC
	 */
	private static final long serialVersionUID = -7652636660460034435L;

	private NewMultiWordsParameters parameters;
	
	private JLabel titleLabel = new JLabel("Advanced parametrization is allowed. Non exepert users can keep default values.");
	private JLabel selectMetricLabel = new JLabel("Select multi-words string similarity metric: "); 
	private JComboBox metricsCombo;
	
	private JLabel whichTermsLabel = new JLabel("For each concept a multi-words string is built considering several terms related to it.");
	
	private JCheckBox chkAnnotationProfiling = new JCheckBox("Use Annotation Profiling strings (see Annotation Profiling tab)");
	private JCheckBox chkUseLexiconSynonyms = new JCheckBox("Use Lexicon synonyms.");
	private JCheckBox chkUseLexiconDefinitions = new JCheckBox("Use Lexicon definitions.");
	
	private JCheckBox chkIncludeParents = new JCheckBox("Include parents' strings");
	private JCheckBox chkIncludeSiblings = new JCheckBox("Include siblings' strings");
	private JCheckBox chkIncludeChildren = new JCheckBox("Include children' strings");
	
	private JCheckBox chkIncludeInstances = new JCheckBox("Include instance names.");
	
	private JCheckBox chkIncludeDeclaredProp = new JCheckBox("If the concept is a class -> consider localnames of properties declared by this class");
	private JCheckBox chkIncludeDeclaringClass = new JCheckBox("If the concept is a property -> consider localnames of classes declaring this property");
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public NewMultiWordsParametersPanel() {
		
		super();
		//init components
		String[] metricsList = {NewMultiWordsParameters.TFIDF, NewMultiWordsParameters.EUCLIDEAN, NewMultiWordsParameters.COSINE, NewMultiWordsParameters.JACCARD,  NewMultiWordsParameters.DICE};
		metricsCombo = new JComboBox(metricsList);
		

		chkAnnotationProfiling.setSelected(true);
		chkAnnotationProfiling.setEnabled(false);

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
						.addComponent(chkAnnotationProfiling)
						.addComponent(chkUseLexiconSynonyms)
						.addComponent(chkUseLexiconDefinitions)
						.addComponent(chkIncludeParents) 
						.addComponent(chkIncludeSiblings) 		
						.addComponent(chkIncludeChildren) 
						.addComponent(chkIncludeInstances) 
						.addComponent(chkIncludeDeclaredProp) 
						.addComponent(chkIncludeDeclaringClass)
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
				.addComponent(whichTermsLabel)
				.addGap(10)
				.addComponent(chkAnnotationProfiling)
				.addComponent(chkUseLexiconSynonyms)
				.addComponent(chkUseLexiconDefinitions)
				.addComponent(chkIncludeParents) 
				.addComponent(chkIncludeSiblings) 		
				.addComponent(chkIncludeChildren) 
				.addComponent(chkIncludeInstances) 
				.addComponent(chkIncludeDeclaredProp) 
				.addComponent(chkIncludeDeclaringClass)
						
				.addGap(30)
			);
	}
	
	
	public AbstractParameters getParameters() {
		parameters = new NewMultiWordsParameters();
		
		parameters.measure = (String)metricsCombo.getSelectedItem();
		
		parameters.useLexiconSynonyms = chkUseLexiconSynonyms.isSelected();
		parameters.useLexiconDefinitions = chkUseLexiconDefinitions.isSelected();
		
		parameters.includeParents = chkIncludeParents.isSelected();
		parameters.includeSiblings = chkIncludeSiblings.isSelected();
		parameters.includeChildren = chkIncludeChildren.isSelected();
		
		parameters.considerInstances = chkIncludeInstances.isSelected();
		
		parameters.considerProperties = chkIncludeDeclaredProp.isSelected();
		parameters.considerClasses = chkIncludeDeclaringClass.isSelected();
		
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
