package am.app.feedback.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.mindswap.pellet.utils.intset.IntIterator;

import sun.awt.HorizBagLayout;

import am.AMException;
import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.feedback.FeedbackLoop;
import am.app.feedback.FeedbackLoopParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;
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
	
	
	
	// Start Screen
	JButton btn_start;
	JComboBox cmbIterations;
	JComboBox cmbHighThreshold;
	JComboBox cmbLowThreshold;
	JComboBox cmbCardinality;
	JComboBox cmbConfigurations;
	JComboBox cmbK;
	JComboBox cmbM;
	JComboBox cmbMatcher;
	
	
	// Automatic Progress screen.
	JProgressBar progressBar;
    private JTextArea matcherReport;
    private JScrollPane scrollingArea;
    private JButton okButton;
    private JButton cancelButton;

	
    
    //TaBLE
    CandidatesTable table;
    ButtonGroup radios;
	
	Alignment selectedMapping;
	CandidateConcept selectedConcept;
	ArrayList<CandidateConcept> candidateMappings;
	
	UI ui;
	
	
	public SelectionPanel(UI u) {
		
		ui = u;
		//Initialized here so that we don't reset the report text at each iteration
		matcherReport = new JTextArea(8, 35);
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
			} else if( arg0.getActionCommand() == "btn_correct" ) {
				// the user has selected a correct mapping
				if(radios.getSelection() != null){
					String selectedAlignment = radios.getSelection().getActionCommand();
					//actionCommand is "candidateConcept-candidateMapping"
					String[] indexes = selectedAlignment.split("-");
					int concept = Integer.parseInt(indexes[0]);
					int mapping = Integer.parseInt(indexes[1]);
					selectedConcept = candidateMappings.get(concept);
					selectedMapping = selectedConcept.getCandidateMappings().get(mapping);
					displayProgressScreen();
					ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );
				}
				else{
					Utility.displayErrorPane("Select a candidate mapping.", null);
				}
			} else if( arg0.getActionCommand() == "btn_incorrect" ) {
				// the user cannot find any correct mappings
				//the selectedMapping is set null at the beginning of the displayMappings() and remains null
				displayProgressScreen();
				ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );

			} else if( arg0.getActionCommand() == "btn_stop") {
				// the user has selected to stop the loop
				selectedMapping = null;
				displayProgressScreen();
				ufl.setExectionStage( FeedbackLoop.executionStage.presentFinalMappings );
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
		//catch(AMException ex2) {
			//Utility.displayMessagePane(ex2.getMessage(), null);
		//}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
	}


	private String checkParameters(FeedbackLoopParameters feedbackLoopParameters) {
		String result = "";
		AbstractMatcher currentMatcher = feedbackLoopParameters.matcher;
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



	public void appendToReport(String report) {
		// TODO: Add a report display in the panel.
	}



	// gets called when a matcher finishes
	public void matchingComplete() {
		
		//if( ufl.isStage( FeedbackLoop.executionStage.runningInitialMatchers ) ) {
		//	appendToReport( "Initial Matchers finished...");
		//}
		progressBar.setIndeterminate(false);
		progressBar.setValue(100);
		matcherReport.append("\n"+ ufl.getReport() );
		cancelButton.setEnabled(false);
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
		
		fblp.iterations = Integer.parseInt(cmbIterations.getSelectedItem().toString());
		
		//get the automatic inital matcher
		String matcherName = (String) cmbMatcher.getSelectedItem();
		fblp.matcher = MatcherFactory.getMatcherInstance(MatcherFactory.getMatchersRegistryEntry(matcherName), 0); //index is not needed because this matcher is not set into the table
		fblp.matcher.setThreshold(fblp.highThreshold );
		fblp.matcher.setMaxSourceAlign(fblp.sourceNumMappings);
		fblp.matcher.setMaxTargetAlign(fblp.targetNumMappings);
			
		return fblp;
	}
	
	
	
	//****************UI Functions************************
	
	public void showScreen_Start() {
		
		removeAll();
		JLabel lblParameters = new JLabel("Parameters:");
		JLabel lblMatcher = new JLabel("Automatic Initial Matcher:");
		JLabel lblHighThreshold = new JLabel("High threshold:");
		JLabel lblLowThreshold = new JLabel("Low threshold:");
		JLabel lblCardinality = new JLabel("Cardinality:");
		JLabel lblConfiguration = new JLabel("Run configuration:");
		JLabel lblIterations = new JLabel("Maximum iteration:");
		JLabel lblK = new JLabel("Num candidate concepts K:");
		JLabel lblM = new JLabel("Num candidate mappings M:");
		btn_start = new JButton("Start");
		btn_start.addActionListener(this);
		btn_start.setActionCommand("btn_start");
	
		
		
		
		
		

		
		
		//matcher combo list
		String[] matcherList = MatcherFactory.getMatcherComboList();
		cmbMatcher = new JComboBox(matcherList);
		cmbMatcher.setSelectedItem(MatchersRegistry.InitialMatcher.getMatcherName());
		
		cmbIterations = new JComboBox( Utility.STEPFIVE_INT );
		cmbIterations.setSelectedIndex(Utility.STEPFIVE_INT.length -1 );
		cmbHighThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbHighThreshold.setSelectedItem("0.7");
		cmbLowThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbLowThreshold.setSelectedItem("0.0");
		
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
					.addComponent(lblConfiguration)
					.addComponent(lblIterations)
					.addComponent(lblHighThreshold)
					.addComponent(lblLowThreshold)
					.addComponent(lblK)
					.addComponent(lblM)
					.addComponent(lblCardinality)
				)
				//ALL COMPONENTS IN THE SECOND COLUMNS
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(cmbMatcher,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbConfigurations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE)
					.addComponent(cmbIterations,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 
					.addComponent(cmbHighThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbLowThreshold,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbK,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 			
					.addComponent(cmbM,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
					.addComponent(cmbCardinality,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
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
	    buttonPanel.add(okButton);
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
	
		//JLabel topLabel = new JLabel("Validation of candidate mappings");
		//JLabel topCandLabel = new JLabel("Select at most one candidate mapping");
		JButton btn_display_m = new JButton("Display mapping");
		btn_display_m.setActionCommand("btn_display_m");
		btn_display_m.addActionListener(this);
		
		JButton btn_display_c = new JButton("Display concept's mappings");
		btn_display_c.setActionCommand("btn_display_c");
		btn_display_c.addActionListener(this);
		
		JButton btn_correct = new JButton("Mapping correct");
		btn_correct.setActionCommand("btn_correct");
		btn_correct.addActionListener(this);
		
		
		
		JButton btn_incorrect = new JButton("Mappings incorrect");
		btn_incorrect.setActionCommand("btn_incorrect");
		btn_incorrect.addActionListener(this);
		
		JButton btn_stop = new JButton("Stop");
		btn_stop.setActionCommand("btn_stop");
		btn_stop.addActionListener(this);
		

		
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
		topPanel.add(btn_display_m);
		topPanel.add(btn_display_c);
		topPanel.add(btn_correct);
		topPanel.add(btn_incorrect);
		topPanel.add(btn_stop);
		
		JPanel centralPanel = new JPanel();
		//centralPanel.setLayout(new GridLayout());
		centralPanel.setOpaque(true); //content panes must be opaque
        centralPanel.add(scrollPane);
        
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
		if(matcherReport!= null){
			matcherReport.setText(report);
			revalidate();
		}
	}
	
	public void concatReportText(String report){
		if(matcherReport!= null){
			matcherReport.append(report);
			revalidate();
		}
	}
	
	public void appendNewLineReportText(String report){
		if(matcherReport!= null){
			matcherReport.append("\n"+report);
			revalidate();
		}
	}
	
}
