package am.app.feedback.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Document;

//import org.mindswap.pellet.utils.intset.IntIterator;

import am.Utility;
import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.feedback.CandidateSelection;
import am.app.feedback.FeedbackLoop;
import am.app.feedback.FeedbackLoopParameters;
import am.app.feedback.CandidateSelection.MeasuresRegistry;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.userInterface.MatcherParametersDialog;
import am.userInterface.MatchingProgressDisplay;
import am.userInterface.UI;

public class SelectionPanel extends JPanel implements MatchingProgressDisplay, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -967696425990716259L;
	private FeedbackLoop ufl = null; // pointer to the user feedback loop
	
	final static String UNLIMITED = "Unlimited";
	public final static String A_MAPPING_CORRECT = "Validate selected candidate mapping";
	public final static String A_ALL_MAPPING_WRONG = "Unvalidate all candidate mappings";
	public final static String A_CONCEPT_WRONG = "Unvalidate selected candidate concept";
	public final static String A_ALL_CONCEPT_WRONG = "Unvalidate all candidate concepts";
	JComboBox cmbActions;
	
	// Start Screen
	JButton btn_start;
	JComboBox cmbIterations;
	JComboBox cmbInitialMatcherThreshold;
	JComboBox cmbHighThreshold;
	JComboBox cmbLowThreshold;
	JComboBox cmbCardinality;
	JComboBox cmbConfigurations;
	JComboBox cmbK;
	JComboBox cmbM;
	JComboBox cmbMatcher;
	JComboBox cmbMeasure;
	
	
	// Automatic Progress screen.
	JProgressBar progressBar;
    private JTextArea matcherReport;
    private JScrollPane scrollingArea;
    private JButton okButton;
    private JButton cancelButton;
    private JButton stopButton;

	
    
    //TaBLE
    CandidatesTable table;
    ButtonGroup radios;
	
	Alignment selectedMapping;
	CandidateConcept selectedConcept;
	String selectedAction;
	ArrayList<CandidateConcept> candidateMappings;

	UI ui;
	
	
	public SelectionPanel(UI u) {
		
		ui = u;
		//Initialized here so that we don't reset the report text at each iteration
		matcherReport = new JTextArea(8, 35);
		initScreenStartComponents();
	}

	public void actionPerformed(ActionEvent arg0) {
		try{
			// when a button on the user feedback loop java pane is pressed this is the action listener.
			if( arg0.getActionCommand() == "btn_start" ) {
				FeedbackLoopParameters fblp = getParameters();
				String control = checkParameters(fblp);
			    if(control.equals("")){
					ufl = (FeedbackLoop)MatcherFactory.getMatcherInstance( MatchersRegistry.UserFeedBackLoop , Core.getInstance().getMatcherInstances().size() );  // initialize the user feedback loop interface (i.e. add a new tab)
					ufl.setParam( fblp );
					ufl.setThreshold(fblp.highThreshold);
					ufl.setMaxSourceAlign(fblp.sourceNumMappings);
					ufl.setMaxSourceAlign(fblp.targetNumMappings);
					displayProgressScreen();
					ufl.setProgressDisplay(this);
					ui.getControlPanel().getTablePanel().addMatcher(ufl);
					//ui.redisplayCanvas(); nothing to display yet, because no mappings have been computed
					ufl.execute();
			    }
			    else{
					Utility.displayErrorPane(control, null);
			    }
			} else if( arg0.getActionCommand() == "screen2_cancel" ) {
			 	ufl.cancel(true);
				showScreen_Start();
			} 
			else if( arg0.getActionCommand() == "btn_continue" ) {
				selectedAction = cmbActions.getSelectedItem().toString();
				if(selectedAction.equals(A_MAPPING_CORRECT) || selectedAction.equals(A_CONCEPT_WRONG)){
					if(radios.getSelection() == null){
						Utility.displayErrorPane("Select a candidate mapping.", null);
					}
					else{
						String selectedAlignment = radios.getSelection().getActionCommand();
						//actionCommand is "candidateConcept-candidateMapping"
						String[] indexes = selectedAlignment.split("-");
						int concept = Integer.parseInt(indexes[0]);
						int mapping = Integer.parseInt(indexes[1]);
						selectedConcept = candidateMappings.get(concept);
						selectedMapping = selectedConcept.getCandidateMappings().get(mapping);
						displayProgressScreen();
						ufl.userContinued();
					}
				}
				else if(selectedAction.equals(A_ALL_MAPPING_WRONG) || selectedAction.equals(A_ALL_CONCEPT_WRONG)){
					displayProgressScreen();
					ufl.userContinued();
				}
			}
			else if( arg0.getActionCommand() == "btn_stop") {
				// the user has selected to stop the loop
				selectedMapping = null;
				selectedConcept = null;
				displayProgressScreen();
				ufl.stop();
			}
			 else if( arg0.getActionCommand() == "btn_ok") {
				 	//ufl.cancel(true);
					showScreen_Start();
					//ufl.setExectionStage( FeedbackLoop.executionStage.presentFinalMappings );
			 }
			 else if( arg0.getActionCommand() == "btn_display_m") {
					// the user has selected to display a single candidate mapping
					if(radios.getSelection() != null){
						String selectedAlignment = radios.getSelection().getActionCommand();
						//actionCommand is candidateConcept-candidateMapping
						String[] indexes = selectedAlignment.split("-");
						int concept = Integer.parseInt(indexes[0]);
						int mapping = Integer.parseInt(indexes[1]);
						selectedConcept = candidateMappings.get(concept);
						selectedMapping = selectedConcept.getCandidateMappings().get(mapping);
						//TO DO
						//INVOKE UI.displayMapping(m)
						//change TAB, find the mapping and color it
					}
					else{
						Utility.displayErrorPane("Select a candidate mapping.", null);
					}
			 }
			 else if( arg0.getActionCommand() == "btn_display_c") {
				// the user has selected to display a candidate mappings for a single candidate concept
				if(radios.getSelection() != null){
					String selectedAlignment = radios.getSelection().getActionCommand();
					//actionCommand is candidateConcept-candidateMapping
					String[] indexes = selectedAlignment.split("-");
					int concept = Integer.parseInt(indexes[0]);
					int mapping = Integer.parseInt(indexes[1]);
					selectedConcept = candidateMappings.get(concept);
					selectedMapping = selectedConcept.getCandidateMappings().get(mapping);
					//TO DO
					//INVOKE UI.displayMapping(selectedConcept.getCandidateMappings())
					//change TAB, find the mappings and color them
					//use selectedConcept.isType(alignType) to understand if it's source or target concept
				}
				else{
					Utility.displayErrorPane("Select a candidate concept.", null);
				}
			 }
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}


	private String checkParameters(FeedbackLoopParameters feedbackLoopParameters) {
		String result = "";
		AbstractMatcher currentMatcher = feedbackLoopParameters.initialMatcher;
		if(!Core.getInstance().ontologiesLoaded() ) {
			result = "You have to load Source and Target ontologies before running any matcher\nClick on File Menu and select Open Ontology functions ";
		}
		else if (currentMatcher.getMinInputMatchers() > 0){
			result = "Only FIRST layer matchers can be used in the feedback loop ";
		}
		else if(currentMatcher.needsParam()) {
			MatcherParametersDialog dialog = new MatcherParametersDialog(currentMatcher);
			if(dialog.parametersSet()) {
				currentMatcher.setParam(dialog.getParameters());
				//no error, so result must be ""
			}
			else{
				result = "Parameters for the automatic initial matcher have not been set ";
			}
			dialog.dispose();
		}
		return result;
	}

	public double getHighThreshold() {
		return Double.parseDouble( cmbHighThreshold.getSelectedItem().toString() );
	}
	
	public double getLowThreshold() {
		return Double.parseDouble( cmbLowThreshold.getSelectedItem().toString() );
	}
	
	public String getConfiguration(){
		return cmbConfigurations.getSelectedItem().toString();
	}


	
	/*********** Matcher Progress Display Methods ***********************/
	

	public void appendToReport(String report) {
		if(!ufl.isCancelled()){
			if(matcherReport!= null){
				matcherReport.append("\n"+report);
				revalidate();
			}
		}
	}



	@Override
	public void matchingStarted() {	}
	
	// gets called when a matcher finishes
	public void matchingComplete() {
		
		//if( ufl.isStage( FeedbackLoop.executionStage.runningInitialMatchers ) ) {
		//	appendToReport( "Initial Matchers finished...");
		//}
		progressBar.setIndeterminate(false);
		progressBar.setValue(100);
		matcherReport.append("\n"+ ufl.getReport() );
		cancelButton.setEnabled(false);
		stopButton.setEnabled(false);
		okButton.setEnabled(true);
		revalidate();
	}


	/**
	 * Function that is called when the progress of the matchers
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	
	




	public Alignment getUserMapping() {
		return selectedMapping;
		
	}



	public boolean isUserMappingClass() {
		if( selectedMapping == null ) { return false; }
		if( selectedMapping.getAlignmentType() != null && selectedMapping.getAlignmentType() == alignType.aligningClasses ) return true;
		if( selectedMapping.getAlignmentType() == null ) System.out.println("Assertion Failed: isUserMappingClass().");
		return false;
	}



	public int getK() {
		return Integer.parseInt( (String) cmbK.getSelectedItem() );
	}

	public int getM() {
		return Integer.parseInt( (String) cmbM.getSelectedItem() );
	}



	public FeedbackLoopParameters getParameters() {
		
		FeedbackLoopParameters fblp = new FeedbackLoopParameters();
		
		fblp.initialMatchersThreshold = Double.parseDouble( cmbInitialMatcherThreshold.getSelectedItem().toString() );
		fblp.highThreshold = Double.parseDouble( cmbHighThreshold.getSelectedItem().toString() );
		fblp.lowThreshold = Double.parseDouble( cmbLowThreshold.getSelectedItem().toString() );
		
		fblp.cardinality = cmbCardinality.getSelectedItem().toString();
		
		if( cmbCardinality.getSelectedItem().equals("1-1") ) {
			fblp.sourceNumMappings = 1;
			fblp.targetNumMappings = 1;
		}
		
		fblp.configuration = cmbConfigurations.getSelectedItem().toString();
		
		fblp.K = Integer.parseInt(cmbK.getSelectedItem().toString());
		fblp.M = Integer.parseInt(cmbM.getSelectedItem().toString());
		try{
			fblp.iterations = Integer.parseInt(cmbIterations.getSelectedItem().toString());
		}
		catch(Exception e){
			fblp.iterations = Integer.MAX_VALUE;
		}
		
		fblp.measure = cmbMeasure.getSelectedItem().toString();

		
		//get the automatic inital matcher
		String matcherName = (String) cmbMatcher.getSelectedItem();
		fblp.initialMatcher = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //index is not needed because this matcher is not set into the table
		fblp.initialMatcher.setThreshold(fblp.highThreshold );
		fblp.initialMatcher.setMaxSourceAlign(fblp.sourceNumMappings);
		fblp.initialMatcher.setMaxTargetAlign(fblp.targetNumMappings);
			
		return fblp;
	}
	
	
	
	//****************UI Functions************************
	public void initScreenStartComponents(){

		btn_start = new JButton("Start");
		btn_start.addActionListener(this);
		btn_start.setActionCommand("btn_start");
		
		//matcher combo list
		String[] matcherList = MatcherFactory.getMatcherComboList();
		cmbMatcher = new JComboBox(matcherList);
		cmbMatcher.setSelectedItem(MatchersRegistry.InitialMatcher.getMatcherName());
		
		cmbIterations = new JComboBox( Utility.STEPFIVE_INT );
		cmbIterations.addItem(UNLIMITED);
		cmbIterations.setSelectedItem(UNLIMITED);
		cmbInitialMatcherThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbInitialMatcherThreshold.setSelectedItem("0.6");
		cmbHighThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbHighThreshold.setSelectedItem("0.8");
		cmbLowThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbLowThreshold.setSelectedItem("0.6");
		
		String[] integers = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		cmbK = new JComboBox( integers );
		cmbK.setSelectedItem("4");
		cmbM = new JComboBox( integers );
		cmbM.setSelectedItem("6");
		
		cmbCardinality = new JComboBox();
		cmbCardinality.addItem("1-1");
		
		cmbConfigurations = new JComboBox();
		cmbConfigurations.addItem(FeedbackLoop.MANUAL);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_101_301);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_101_302);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_101_303);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_101_304);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_animals);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_basketball_soccer);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_comsci);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_hotel);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_network);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_people_pets);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_russia);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_weapons);
		cmbConfigurations.addItem(FeedbackLoop.AUTO_wine);
		
		cmbMeasure = new JComboBox();
		cmbMeasure.addItem(CandidateSelection.ALLMEASURES);
		MeasuresRegistry[] mrs = MeasuresRegistry.values();
		for(int i = 0; i < mrs.length; i++){
			MeasuresRegistry name = mrs[i];
			cmbMeasure.addItem(name.getMeasureName());
		}
	}
	
	public void showScreen_Start() {
		
		removeAll();
		//all other component are initialized in the initScreenStartComponents() method
		//because we want to init them in the constructor only once.
		//this way the parameters remains set when the user click cancel
		JLabel lblMatcher = new JLabel("Automatic Initial Matcher:");
		JLabel lblInitialMatcherThreshold = new JLabel("Initial Matcher threshold:");
		JLabel lblHighThreshold = new JLabel("High threshold:");
		JLabel lblLowThreshold = new JLabel("Low threshold:");
		JLabel lblCardinality = new JLabel("Cardinality:");
		JLabel lblConfiguration = new JLabel("Run configuration:");
		JLabel lblIterations = new JLabel("Maximum iteration:");
		JLabel lblK = new JLabel("Num candidate concepts K:");
		JLabel lblM = new JLabel("Num candidate mappings M:");
		JLabel lblMeasure = new JLabel("Relevance measure");
		//LAYOUT

		JPanel centralContainer  = new JPanel();
		GroupLayout groupLayout = new GroupLayout(centralContainer);
		centralContainer.setLayout( groupLayout );
		centralContainer.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "User Feedback Loop parameters"));
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			.addComponent(btn_start)
			.addGroup(groupLayout.createSequentialGroup()
				//ALL LABELS IN THE FIRST COLUMN
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(lblMatcher)
					.addComponent(lblInitialMatcherThreshold)
					.addComponent(lblConfiguration)
					.addComponent(lblIterations)
					.addComponent(lblHighThreshold)
					.addComponent(lblLowThreshold)
					.addComponent(lblK)
					.addComponent(lblM)
					.addComponent(lblCardinality)
					.addComponent(lblMeasure)
				)
				//ALL COMPONENTS IN THE SECOND COLUMNS
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(cmbMatcher,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					.addComponent(cmbInitialMatcherThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					.addComponent(cmbConfigurations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					.addComponent(cmbIterations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 
					.addComponent(cmbHighThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbLowThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbK,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 			
					.addComponent(cmbM,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbCardinality,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbMeasure,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
			)
		);
		
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(btn_start)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMatcher)
						.addComponent(cmbMatcher,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblInitialMatcherThreshold)
						.addComponent(cmbInitialMatcherThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblConfiguration)
						.addComponent(cmbConfigurations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblIterations)
						.addComponent(cmbIterations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)						
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblHighThreshold)
						.addComponent(cmbHighThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblLowThreshold)
						.addComponent(cmbLowThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblK)
						.addComponent(cmbK,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblM)
						.addComponent(cmbM,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblCardinality)
						.addComponent(cmbCardinality,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblMeasure)
						.addComponent(cmbMeasure,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
				)
			);
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(centralContainer);
		repaint();
		
	}
	
	

	private void displayProgressScreen() {
		
		removeAll();
	    
	    this.setLayout(new BorderLayout());
	    JPanel textPanel = new JPanel(new BorderLayout());

	    progressBar = new JProgressBar(0, 100);
	    progressBar.setSize(10, 4);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
	
	    this.add(progressBar, BorderLayout.PAGE_START);
	    this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    
	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    okButton = new JButton("Ok");
	    okButton.setEnabled(false);
	    okButton.addActionListener(this);
	    okButton.setActionCommand("btn_ok");
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("screen2_cancel");
		cancelButton.addActionListener(this);
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("btn_stop");
		stopButton.addActionListener(this);
	    buttonPanel.add(okButton);
	    buttonPanel.add(stopButton);
	    buttonPanel.add(cancelButton);
	    
	    scrollingArea = new JScrollPane(matcherReport);
	    textPanel.add(scrollingArea);
	    textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
	    
	    this.add(textPanel);
	    this.add(buttonPanel, BorderLayout.PAGE_END);
	    
	    
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		repaint();
	}
	
	
	public void displayMappings( ArrayList<CandidateConcept> topConceptsAndAlignments) {
		
		removeAll();
		selectedMapping = null;
		selectedConcept = null;
		candidateMappings = topConceptsAndAlignments;
		
		cmbActions = new JComboBox(new String[]{A_MAPPING_CORRECT,A_ALL_MAPPING_WRONG, A_CONCEPT_WRONG, A_ALL_CONCEPT_WRONG});
		
		JButton btn_continue = new JButton("Continue");
		btn_continue.setActionCommand("btn_continue");
		btn_continue.addActionListener(this);
		
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("btn_stop");
		stopButton.addActionListener(this);
		
		JButton btn_display_m = new JButton("Display selected candidate mapping");
		btn_display_m.setActionCommand("btn_display_m");
		btn_display_m.addActionListener(this);
		
		JButton btn_display_c = new JButton("Display selected candidate concept's mappings");
		btn_display_c.setActionCommand("btn_display_c");
		btn_display_c.addActionListener(this);
		

		
		//Division of mappings into groups and rows, each group contains multiple rows
		//the first two arraylists were only needed for the GroupLayout,
		//instead using the JTable we only need the list of CandidadatesTableRows
		radios = new ButtonGroup();
		ArrayList<CandidatesTableRow> rows = new ArrayList<CandidatesTableRow>();
		for(int i=0; i< topConceptsAndAlignments.size(); i++){
			CandidateConcept c = topConceptsAndAlignments.get(i);
			ArrayList<Alignment> candidateMappings = c.getCandidateMappings();
			if(candidateMappings!= null){
				for(int j = 0; j < candidateMappings.size(); j++){
					Alignment m = candidateMappings.get(j);
					rows.add(createTableRow(c,m, i, j));
				}
			}
			
			
		}

		//Table of candidates
        CandidatesTableModel mt = new CandidatesTableModel(rows);
        table = new  CandidatesTable(mt);
        table.initColumnSizes();
        //the height of a row is 16 on MAC at least.
        table.setPreferredScrollableViewportSize(new Dimension(table.calculateRealWidth(), Math.min( 16*25 , 16*rows.size() )));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);   
		
		JPanel topPanel = new JPanel();
		FlowLayout topPanelLayout = new FlowLayout();
		topPanelLayout.setAlignment(FlowLayout.CENTER);
		topPanel.setLayout(topPanelLayout);
		topPanel.add(new JLabel("Action: "));
		topPanel.add(cmbActions);
		topPanel.add(btn_continue);
		topPanel.add(stopButton);
		
		JPanel bottomPanel = new JPanel();
		FlowLayout bottomPanelLayout = new FlowLayout();
		bottomPanelLayout.setAlignment(FlowLayout.CENTER);
		bottomPanel.setLayout(bottomPanelLayout);
		bottomPanel.add(new JLabel("Visualization: "));
		bottomPanel.add(btn_display_m);
		bottomPanel.add(btn_display_c);
		
		JPanel centralPanel = new JPanel();
		//centralPanel.setLayout(new GridLayout());
		centralPanel.setOpaque(true); //content panes must be opaque
        centralPanel.add(scrollPane);
        centralPanel.add(bottomPanel);
        

        
		BorderLayout thisLayout = new BorderLayout();
		this.setLayout(thisLayout);
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(centralPanel, BorderLayout.CENTER);
		//REPAINT DOESN'T WORK WELL HERE I DON'T KNOW WHY
		//repaint();
		revalidate();
		
		//THIS layout has been changed
		//the grouplayout contains all the item,but without the horizBox, the group is placed on the left of the screen.
		//this is needed to put the Group in the center.
			//centralContainer.setLayout(groupLayout);
		//Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalGlue());
		//horizontalBox.add(centralContainer);
		//horizontalBox.add(Box.createHorizontalGlue());

		//this.setLayout(new GridLayout(1,1));
		//this.setLayout(new FlowLayout());
		//this.add(horizontalBox);
		
	}







	private CandidatesTableRow createTableRow(CandidateConcept c, Alignment m, int groupNumber, int indexWithinTheGroup) {
		//controls made by Cosmin
		Alignment cA = m;
		if( cA == null ) {
			System.out.println(" Null entries in the mappings matrix (the ones to display to the user)! SelectionPanel.java line 213");
			throw new RuntimeException(" Null entries in the mappings matrix (the ones to display to the user)! SelectionPanel.java line 213");
		}
		Node s = cA.getEntity1();
		Node t = cA.getEntity2();
		if( s == null || t == null ) {
			System.out.println("Bad alignments added to the list of candidate alignments.");
			throw new RuntimeException("Bad alignments added to the list of candidate alignments.");
		}
			
		JRadioButton button = new JRadioButton();
		//each button is identified by "indexOfTheGroup-indexInTheGroup
		button.setActionCommand(groupNumber+"-"+indexWithinTheGroup);
		radios.add(button);
		return new CandidatesTableRow(c, groupNumber,indexWithinTheGroup, button);
	}

	public void displayReportText(String report){
		if(!ufl.isCancelled()){
			if(matcherReport!= null){
				matcherReport.setText(report);
				revalidate();
			}
		}
	}
	
	public void concatReportText(String report){
		if(!ufl.isCancelled()){
			if(matcherReport!= null){
				matcherReport.append(report);
				revalidate();
			}
		}
	}
	
	public void appendNewLineReportText(String report){
		if(!ufl.isCancelled()){
			if(matcherReport!= null){
				matcherReport.append("\n"+report);
				revalidate();
			}
		}
	}

	public CandidateConcept getUserConcept() {
		return selectedConcept;
	}

	public String getUserAction() {
		return selectedAction;
	}

	@Override
	public void scrollToEndOfReport() {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	if( scrollingArea != null && matcherReport != null ) {
	        		// a complete hack to make the JScrollPane move to the bottom of the JTextArea
	        		Document d = matcherReport.getDocument();
	        		matcherReport.setCaretPosition(d.getLength());
	        	}
	        }
		});
		
	}

	@Override
	public void clearReport() {
		matcherReport.setText("");
	}
	
}
