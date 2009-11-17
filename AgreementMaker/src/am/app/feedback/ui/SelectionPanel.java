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
	AlignmentSet<Alignment> candidateMappings;
	
	UI ui;
	
	
	public SelectionPanel(UI u) {
		
		ui = u;
		
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
				selectedMapping = null;
				showScreen_Start();
			} else if( arg0.getActionCommand() == "btn_correct" ) {
				// the user has selected a correct mapping
				if(radios.getSelection() != null){
					String selectedAlignment = radios.getSelection().getActionCommand();
					selectedMapping = candidateMappings.getAlignment( Integer.parseInt(selectedAlignment));
					displayProgressScreen();
					ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );
				}
			} else if( arg0.getActionCommand() == "btn_incorrect" ) {
				// the user cannot find any correct mappings
				selectedMapping = null;
				displayProgressScreen();
				ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );

			} else if( arg0.getActionCommand() == "btn_stop") {
				// the user has selected to stop the loop
				selectedMapping = null;
				displayProgressScreen();
				ufl.setExectionStage( FeedbackLoop.executionStage.presentFinalMappings );
			}
			 else if( arg0.getActionCommand() == "btn_ok") {
					// the user has selected to stop the loop
				 	ufl.cancel(true);
					selectedMapping = null;
					showScreen_Start();
					//ufl.setExectionStage( FeedbackLoop.executionStage.presentFinalMappings );
			 }
			 else if( arg0.getActionCommand() == "btn_display") {
					// the user has selected to display a single candidate mapping
					if(radios.getSelection() != null){
						String selectedAlignment = radios.getSelection().getActionCommand();
						selectedMapping = candidateMappings.getAlignment( Integer.parseInt(selectedAlignment));
						//TO DO
						//INVOKE UI.displayMapping(m)
						//change TAB, find the mapping and color it
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
		matcherReport.setText( ufl.getReport() );
		cancelButton.setEnabled(false);
		okButton.setEnabled(true);
		
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
		btn_start = new JButton("Start");
		btn_start.addActionListener(this);
		btn_start.setActionCommand("btn_start");
		
		JLabel lblParameters = new JLabel("Parameters:");
		JLabel lblMatcher = new JLabel("Automatic Matcher:");
		JLabel lblHighThreshold = new JLabel("High threshold:");
		JLabel lblLowThreshold = new JLabel("Low threshold:");
		JLabel lblCardinality = new JLabel("Cardinality:");
		JLabel lblConfiguration = new JLabel("Run configuration:");
		JLabel lblIterations = new JLabel("Iterations:");
		JLabel lblK = new JLabel("K:");
		JLabel lblM = new JLabel("M:");
		
		
		//matcher combo list
		String[] matcherList = MatcherFactory.getMatcherComboList();
		cmbMatcher = new JComboBox(matcherList);
		cmbMatcher.setSelectedItem(MatchersRegistry.InitialMatcher.getMatcherName());
		
		cmbIterations = new JComboBox( Utility.STEPFIVE_INT );
		cmbIterations.setSelectedIndex(Utility.STEPFIVE_INT.length -1 );
		cmbHighThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbHighThreshold.setSelectedItem("0.7");
		cmbLowThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbLowThreshold.setSelectedItem("0.05");
		
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
		
		matcherReport = new JTextArea(8, 35);
	    
		matcherReport.setText("Running...");
		//setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    
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
	
	
	public void displayMappings( AlignmentSet<Alignment> mappings) {
		
		removeAll();

		candidateMappings = mappings;
	
		//JLabel topLabel = new JLabel("Validation of candidate mappings");
		//JLabel topCandLabel = new JLabel("Select at most one candidate mapping");
		JButton btn_display = new JButton("View selected mapping");
		btn_display.setActionCommand("btn_display");
		btn_display.addActionListener(this);
		JButton btn_correct = new JButton("Selected mapping is correct");
		btn_correct.setActionCommand("btn_correct");
		btn_correct.addActionListener(this);
		
		
		
		JButton btn_incorrect = new JButton("All the mappings are incorrect");
		btn_incorrect.setActionCommand("btn_incorrect");
		btn_incorrect.addActionListener(this);
		
		JButton btn_stop = new JButton("Stop the user feedback loop");
		btn_stop.setActionCommand("btn_stop");
		btn_stop.addActionListener(this);
		
		
		//JLabel lbl_candidate = new JLabel("Candidate Mappings:");
		

		
		
		//Division of mappings into groups
		//the first two arraylists were only needed for the GroupLayout,
		//instead using the JTable we only need the list of CandidadatesTableRows
		radios = new ButtonGroup();
		ArrayList<ArrayList<Alignment>> groups = new ArrayList<ArrayList<Alignment>>();//NOT USED ANYMORE
		ArrayList<Alignment> currentGroup = new ArrayList<Alignment>();//NOT USED ANYMORE
		ArrayList<CandidatesTableRow> rows = new ArrayList<CandidatesTableRow>();
		int groupNumber = 1;
		groups.add(currentGroup);
		boolean sourceOrTarget = false;
		for(int i=0; i< mappings.size(); i++){
			Alignment m = mappings.getAlignment(i);
			boolean newGroup = false;
			if(currentGroup.size() == 0){
				//first mapping of the group, just add it
			}
			else if(currentGroup.size() == 1){
				//second mapping, I have to check if it's part of the same group.
				//I have to understand if the group is equal on source or target
				Alignment previous = currentGroup.get(0);
				if(previous.getEntity1().equals(m.getEntity1())){
					sourceOrTarget = true;
				}
				else if(previous.getEntity2().equals(m.getEntity2())){
					sourceOrTarget = false;
				}
				else{
					//different nodes, just create a new group
					newGroup = true;
				}
			}
			else{
				//currentGroup.size() >1
				Alignment previous = currentGroup.get(1);//any mapping of the group is fine
				if(sourceOrTarget){//they must have the same source
					if(previous.getEntity1().equals(m.getEntity1())){
						//add the mapping to the same group
					}
					else{
						//different nodes, just create a new group
						newGroup = true;
					}
				}
				else{//they must have the same target
					if(previous.getEntity2().equals(m.getEntity2())){
						//do nothing
					}
					else{
						//different nodes, just create a new group
						newGroup = true;
					}
				}
			}
			
			if(newGroup){
				//different nodes, just create a new group
				currentGroup = new ArrayList<Alignment>();
				groups.add(currentGroup);
				groupNumber+=1;
			}
			currentGroup.add(m);
			rows.add(createTableRow(i, m, groupNumber));
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
		topPanel.add(btn_display);
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
		
		
		/*GROUP LAYOUT HAS BEEN CHANGED TO INTRODUCE THE TABLE
		JPanel centralContainer  = new JPanel();
		GroupLayout groupLayout = new GroupLayout(centralContainer);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		
		//Creation of groups for the candidate lists.
		ParallelGroup candidateHorizGroups = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup candidateVertGroups = groupLayout.createSequentialGroup();
		int radioIndex = 0;
		for(int i=0; i< groups.size(); i++){
			ArrayList<Alignment> group = groups.get(i);
			JLabel groupLabel = new JLabel("Group "+(i+1));
			JSeparator groupSep = new JSeparator();
			//Hotizontal stuff
			SequentialGroup horizSingleGroup = groupLayout.createSequentialGroup();
			ParallelGroup horizCandidatesGroup = groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
			//vertical stuff
			ParallelGroup vertSingleGroup = groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER);
			SequentialGroup vertCandidatesGroup = groupLayout.createSequentialGroup();
			//add mappings to candidate groups
			Iterator<Alignment> it = group.iterator();
			while(it.hasNext()){
				it.next();
				JRadioButton radio = mappingsRadios.get(radioIndex);
				radioIndex+=1;
				horizCandidatesGroup.addComponent(radio);
				vertCandidatesGroup.addComponent(radio);
			}
			
			//add all items to the horizontal group
			horizSingleGroup.addGap(50);
			horizSingleGroup.addComponent(groupLabel)
							.addGroup(horizCandidatesGroup);
			candidateHorizGroups.addGroup(horizSingleGroup);
			candidateHorizGroups.addComponent(groupSep);
			//add all items to the vertical group
			vertSingleGroup.addComponent(groupLabel)
						   .addGroup(vertCandidatesGroup);
			candidateVertGroups.addGroup(vertSingleGroup);
			candidateVertGroups.addComponent(groupSep);
			
		}
		
		//create groups of candidates.
		
		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						//.addComponent(topLabel) 
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btn_correct) 			
						//.addComponent(combOperationsCombo,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,  GroupLayout.PREFERRED_SIZE) 	
								.addComponent(btn_incorrect)
								.addComponent(btn_stop)
						)
						//.addComponent(topCandLabel)
						.addGroup(candidateHorizGroups)
		);
		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
						//.addComponent(topLabel)
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(btn_correct) 	
								.addComponent(btn_incorrect)
								.addComponent(btn_stop)
						)
						//.addGap(20)
						//.addComponent(topCandLabel)
						.addGroup(candidateVertGroups)
		);
		
		

		//the grouplayout contains all the item,but without the horizBox, the group is placed on the left of the screen.
		//this is needed to put the Group in the center.
		 * 		//centralContainer.setLayout(groupLayout);
		//Box horizontalBox = Box.createHorizontalBox();
		//horizontalBox.add(Box.createHorizontalGlue());
		//horizontalBox.add(centralContainer);
		//horizontalBox.add(Box.createHorizontalGlue());

		//this.setLayout(new GridLayout(1,1));
		//this.setLayout(new FlowLayout());
		//this.add(horizontalBox);
		*/
	}







	private CandidatesTableRow createTableRow(int i, Alignment m, int groupNumber) {
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
		button.setActionCommand(Integer.toString(i));
		radios.add(button);
		return new CandidatesTableRow(i, m, groupNumber, button);
		
	}


	
}
