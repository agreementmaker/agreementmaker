package am.userInterface;



import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import am.AMException;
import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.feedback.FeedbackLoop;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.ontology.Ontology;
import am.userInterface.table.MatchersTablePanel;


public class UIMenu implements ActionListener {
	
	// create 4 menus
	private JMenu fileMenu, viewMenu, helpMenu, ontologyMenu;
	// private JMenu editMenu; // not used yet
	//fake menus
	private JMenu matchingMenu;
	
	// menu items for helpMenu
	private JMenuItem howToUse, aboutItem;		
	//menu items for matching menu
	private JMenuItem manualMapping, newMatching, runMatching, copyMatching, deleteMatching, saveMatching, refEvaluateMatching, clearAll, doRemoveDuplicates;

	// menu items for the View Menu
	private JMenuItem keyItem;
	private JCheckBoxMenuItem smoMenuItem;  // Menu item for toggling "Selected Matchings Only" view mode.
	
	// menu items for the Ontology Menu
	private JMenuItem ontologyDetails;
	
	//creates a menu bar
	private JMenuBar myMenuBar;
	
	private UI ui;
	
	// menu items for edit menu
	//private JMenuItem undo, redo;
	// menu itmes for fileMenu
	private JMenuItem xit, openSource, openTarget, openMostRecentPair;
	
	private JMenu menuRecentSource, menuRecentTarget;
	//private JMenuItem menuRecentSourceList[], menuRecentTargetList[]; // the list of recent files
	private JCheckBoxMenuItem disableVisualizationItem;
	private JCheckBoxMenuItem showLabelItem;
	private JCheckBoxMenuItem showLocalNameItem;
	
	private JMenuItem userFeedBack;
	
	public UIMenu(UI ui){
		this.ui=ui;
		init();
		
	}
	
	
	public void refreshRecentMenus() {
		refreshRecentMenus( menuRecentSource, menuRecentTarget);
	}
	
	/**
	 * This function will update the Recent File Menus with the most up to date recent files
	 * @param recentsource
	 * @param recenttarget
	 */
	private void refreshRecentMenus( JMenu recentsource, JMenu recenttarget ) {
		
		AppPreferences prefs = ui.getAppPreferences();
		
		// first we start by removing all sub menus
		recentsource.removeAll();
		recenttarget.removeAll();
		
		// then populate the menus again.
		for( int i = 0; i < prefs.countRecentSources(); i++) {
			JMenuItem menuitem = new JMenuItem(i + ".  " + prefs.getRecentSourceFileName(i));
			menuitem.setActionCommand("source" + i);
			menuitem.setMnemonic( 48 + i);
			menuitem.addActionListener(this);
			menuRecentSource.add(menuitem);
		}
		
		for( int i = 0; i < prefs.countRecentTargets(); i++) {
			JMenuItem menuitem = new JMenuItem(i + ".  " + prefs.getRecentTargetFileName(i));
			menuitem.setActionCommand("target" + i);
			menuitem.setMnemonic( 48 + i);
			menuitem.addActionListener(this);
			menuRecentTarget.add(menuitem);
		}
		
	}
	
	
	
	public void actionPerformed (ActionEvent ae){
		try {
			Object obj = ae.getSource();
			MatchersControlPanel controlPanel = ui.getControlPanel();
			if (obj == xit){
				// confirm exit
				confirmExit();
				// if it is no, then do nothing		
			}else if (obj == keyItem){
				new Legend(ui);	
			}else if (obj == howToUse){
				Utility.displayTextAreaPane(Help.getHelpMenuString(), "Help");
			}else if (obj == openSource){
				openAndReadFilesForMapping(GlobalStaticVariables.SOURCENODE);
			}else if (obj == openTarget){
				openAndReadFilesForMapping(GlobalStaticVariables.TARGETNODE);
			}else if (obj == openMostRecentPair){
				AppPreferences prefs = new AppPreferences();
				int position = 0;
				ui.openFile( prefs.getRecentSourceFileName(position), GlobalStaticVariables.SOURCENODE, 
						prefs.getRecentSourceSyntax(position), prefs.getRecentSourceLanguage(position), prefs.getRecentSourceSkipNamespace(position), prefs.getRecentSourceNoReasoner(position));
				ui.openFile( prefs.getRecentTargetFileName(position), GlobalStaticVariables.TARGETNODE, 
						prefs.getRecentTargetSyntax(position), prefs.getRecentTargetLanguage(position), prefs.getRecentTargetSkipNamespace(position), prefs.getRecentTargetNoReasoner(position));
			}else if (obj == aboutItem){
				new AboutDialog();
				//displayOptionPane("Agreement Maker 3.0\nAdvis research group\nThe University of Illinois at Chicago 2004","About Agreement Maker");
			}
			else if( obj == disableVisualizationItem ) {
				// Save the SMO setting that has been changed
				AppPreferences prefs = ui.getAppPreferences();
				boolean disableVis = disableVisualizationItem.isSelected();
				prefs.saveSelectedMatchingsOnly(disableVis);
				ui.getCanvas().setDisableVisualization(disableVis);
				ui.redisplayCanvas();
			}
			else if( obj == smoMenuItem ) {
				// Save the SMO setting that has been changed
				AppPreferences prefs = ui.getAppPreferences();
				boolean smoStatus = smoMenuItem.isSelected();
				prefs.saveSelectedMatchingsOnly(smoStatus);
				ui.getCanvas().setSMO(smoStatus);
				ui.redisplayCanvas();
			}
			else if( obj == showLabelItem || obj == showLocalNameItem ) {
				// Save the setting that has been changed
				AppPreferences prefs = ui.getAppPreferences();
				boolean showLabel = showLabelItem.isSelected();
				prefs.saveShowLabel(showLabel);
				ui.getCanvas().setShowLabel(showLabel);
				boolean showLocalname = showLocalNameItem.isSelected();
				prefs.saveShowLocalname(showLocalname);
				ui.getCanvas().setShowLocalName(showLocalname);
				ui.redisplayCanvas();
			}
			else if( obj == userFeedBack ) {
				// the user has to load the ontologies.
				if( Core.getInstance().getSourceOntology() == null || Core.getInstance().getTargetOntology() == null ) {
					Utility.displayErrorPane("Two ontologies must be loaded into AgreementMaker before the matching can begin.", "Ontologies not loaded." );
				}
				else{
					AbstractMatcher ufl = MatcherFactory.getMatcherInstance( MatchersRegistry.UserFeedBackLoop , Core.getInstance().getMatcherInstances().size() );  // initialize the user feedback loop interface (i.e. add a new tab)
					ui.getControlPanel().getTablePanel().addMatcher(ufl);
					ui.redisplayCanvas();
				}			
			}
			else if( obj == manualMapping) {
				Utility.displayMessagePane("To edit or create a manual mapping select any number of source and target nodes.\nLeft click on a node to select it, use Ctrl and/or Shift for multiple selections.", "Manual Mapping");
			}
			else if(obj == newMatching) {
				controlPanel.newManual();
			}
			else if(obj == runMatching) {
				controlPanel.matchSelected();
			}
			else if(obj == copyMatching) {
				controlPanel.copy();
			}
			else if(obj == deleteMatching) {
				controlPanel.delete();
			}
			else if(obj == saveMatching) {
				controlPanel.export();
			}
			else if(obj == refEvaluateMatching) {
				controlPanel.evaluate();
			}
			else if(obj == clearAll) {
				controlPanel.clearAll();
			}
			else if(obj == ontologyDetails) {
				ontologyDetails();
			}
			else if( obj == doRemoveDuplicates ) {
				MatchersTablePanel m = controlPanel.getTablePanel();
				
				int[] selectedRows =  m.getTable().getSelectedRows();
				
				if(selectedRows.length != 2) {
					Utility.displayErrorPane("You must select two matchers.", null);
				}
				else {
					
					int i, j;
					
					Core core = Core.getInstance();
					
					AbstractMatcher firstMatcher = core.getMatcherInstances().get(selectedRows[0]);
					AbstractMatcher secondMatcher = core.getMatcherInstances().get(selectedRows[1]);
					
					AlignmentSet firstClassSet = firstMatcher.getClassAlignmentSet();
					AlignmentSet secondClassSet = secondMatcher.getClassAlignmentSet();
					
					AlignmentSet firstPropertiesSet = firstMatcher.getPropertyAlignmentSet();
					AlignmentSet secondPropertiesSet = secondMatcher.getPropertyAlignmentSet();
					
					AlignmentSet combinedClassSet = new AlignmentSet();
					AlignmentSet combinedPropertiesSet = new AlignmentSet();

					// double nested loop, later I will write a better algorithm -cos
					for( i = 0; i < firstClassSet.size(); i++ ) {
						Alignment candidate = firstClassSet.getAlignment(i);
						boolean foundDuplicate = false;
						for( j = 0; j < secondClassSet.size(); j++ ) {
							Alignment test = secondClassSet.getAlignment(j);							
						
						
							int sourceNode1 = candidate.getEntity1().getIndex();
							int targetNode1 = candidate.getEntity2().getIndex();
						
							int sourceNode2 = test.getEntity1().getIndex();
							int targetNode2 = test.getEntity2().getIndex();
						
							if(sourceNode1 == sourceNode2 && targetNode1 == targetNode2 ) {
								// we found a duplicate
								foundDuplicate = true;
								break;	
							}
						}
						
						if( !foundDuplicate ) combinedClassSet.addAlignment(candidate);
						
					}

					for( i = 0; i < secondClassSet.size(); i++ ) {
						Alignment candidate = secondClassSet.getAlignment(i);
						boolean foundDuplicate = false;
						for( j = 0; j < firstClassSet.size(); j++ ) {
							Alignment test = firstClassSet.getAlignment(j);							
						
						
							int sourceNode1 = candidate.getEntity1().getIndex();
							int targetNode1 = candidate.getEntity2().getIndex();
						
							int sourceNode2 = test.getEntity1().getIndex();
							int targetNode2 = test.getEntity2().getIndex();
						
							if(sourceNode1 == sourceNode2 && targetNode1 == targetNode2 ) {
								// we found a duplicate
								foundDuplicate = true;
								break;	
							}
						}
						
						if( !foundDuplicate ) combinedClassSet.addAlignment(candidate);
						
					}
					
					
					// now the properties.
					
					// double nested loop, later I will write a better algorithm -cos
					for( i = 0; i < firstPropertiesSet.size(); i++ ) {
						Alignment candidate = firstPropertiesSet.getAlignment(i);
						boolean foundDuplicate = false;
						for( j = 0; j < secondPropertiesSet.size(); j++ ) {
							Alignment test = secondPropertiesSet.getAlignment(j);							
						
						
							int sourceNode1 = candidate.getEntity1().getIndex();
							int targetNode1 = candidate.getEntity2().getIndex();
						
							int sourceNode2 = test.getEntity1().getIndex();
							int targetNode2 = test.getEntity2().getIndex();
						
							if(sourceNode1 == sourceNode2 && targetNode1 == targetNode2 ) {
								// we found a duplicate
								foundDuplicate = true;
								break;	
							}
						}
						
						if( !foundDuplicate ) combinedPropertiesSet.addAlignment(candidate);
						
					}

					for( i = 0; i < secondPropertiesSet.size(); i++ ) {
						Alignment candidate = secondPropertiesSet.getAlignment(i);
						boolean foundDuplicate = false;
						for( j = 0; j < firstPropertiesSet.size(); j++ ) {
							Alignment test = firstPropertiesSet.getAlignment(j);							
						
						
							int sourceNode1 = candidate.getEntity1().getIndex();
							int targetNode1 = candidate.getEntity2().getIndex();
						
							int sourceNode2 = test.getEntity1().getIndex();
							int targetNode2 = test.getEntity2().getIndex();
						
							if(sourceNode1 == sourceNode2 && targetNode1 == targetNode2 ) {
								// we found a duplicate
								foundDuplicate = true;
								break;	
							}
						}
						
						if( !foundDuplicate ) combinedPropertiesSet.addAlignment(candidate);
						
					}
					
					
					AbstractMatcher newMatcher = new UserManualMatcher();
					
					newMatcher.setClassesAlignmentSet(combinedClassSet);
					newMatcher.setPropertiesAlignmentSet(combinedClassSet);
					newMatcher.setName(MatchersRegistry.UniqueMatchings);
					
					m.addMatcher(newMatcher);
					
					
					
				}
				
				
				
			}
			
			
			
			// TODO: find a Better way to do this
			
			String command = ae.getActionCommand();  // get the command string we set
			if( command.length() == 7 ) { // the only menus that set an action command  are the recent menus, so we're ok.
				
				AppPreferences prefs = new AppPreferences();
				
				char index[] = new char[1];  // '0' - '9'
				char ontotype[] = new char[1]; // 's' or 't' (source or target)
				
				command.getChars(0, 1 , ontotype, 0);  // get the first character of the sting
				command.getChars(command.length() - 1, command.length(), index, 0); // get the last character of the string
				
				// based on the first and last characters of the action command, we can tell which menu was clicked.
				// the rest is easy
				
				int position = index[0] - 48; // 0 - 9
				switch( ontotype[0] ) {
					
					case 's':
						ui.openFile( prefs.getRecentSourceFileName(position), GlobalStaticVariables.SOURCENODE, 
								prefs.getRecentSourceSyntax(position), prefs.getRecentSourceLanguage(position), prefs.getRecentSourceSkipNamespace(position), prefs.getRecentSourceNoReasoner(position));
						prefs.saveRecentFile(prefs.getRecentSourceFileName(position), GlobalStaticVariables.SOURCENODE, 
								prefs.getRecentSourceSyntax(position), prefs.getRecentSourceLanguage(position), prefs.getRecentSourceSkipNamespace(position), prefs.getRecentSourceNoReasoner(position));
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
						break;
					case 't':
						ui.openFile( prefs.getRecentTargetFileName(position), GlobalStaticVariables.TARGETNODE, 
								prefs.getRecentTargetSyntax(position), prefs.getRecentTargetLanguage(position), prefs.getRecentTargetSkipNamespace(position), prefs.getRecentTargetNoReasoner(position));
						prefs.saveRecentFile(prefs.getRecentTargetFileName(position), GlobalStaticVariables.TARGETNODE, 
								prefs.getRecentTargetSyntax(position), prefs.getRecentTargetLanguage(position), prefs.getRecentTargetSkipNamespace(position), prefs.getRecentTargetNoReasoner(position));
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
						break;
					default:
						break;
				}
				
			}
		}
		catch(AMException ex2) {
			Utility.displayMessagePane(ex2.getMessage(), null);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Utility.displayErrorPane(Utility.UNEXPECTED_ERROR, null);
		}
		
	}
	
	public void ontologyDetails() {
		Core c = Core.getInstance();
		Ontology sourceO = c.getSourceOntology();
		Ontology targetO = c.getTargetOntology();
		String sourceClassString = "Not loaded\n";
		String sourcePropString = "Not loaded\n";
		String targetClassString = "Not loaded\n";
		String targetPropString = "Not loaded\n";
		if(c.sourceIsLoaded()) {
			sourceClassString = sourceO.getClassDetails();
			sourcePropString = sourceO.getPropDetails();
		}
		if(c.targetIsLoaded()) {
			targetClassString = targetO.getClassDetails();
			targetPropString = targetO.getPropDetails();
		}
		String report = "Ontology details\n\n";
		report+= "Hierarchies             \t#concepts\tdepth\tUC-diameter\tLC-diameter\t#roots\t#leaves\n";
		report+= "Source Classes:\t"+sourceClassString;
		report+= "Target Classes:\t"+targetClassString;
		report+= "Source Properties:\t"+sourcePropString;
		report+= "Target Properties:\t"+targetPropString;
		Utility.displayTextAreaWithDim(report,"Reference Evaluation Report", 10, 60);
	}


	public void displayOptionPane(String desc, String title){
			JOptionPane.showMessageDialog(null,desc,title, JOptionPane.PLAIN_MESSAGE);					
	}
	
	
	public void init(){
		
		// need AppPreferences for smoItem, to get if is checked or not.
		AppPreferences prefs = new AppPreferences();
		
		//Creating the menu bar
		myMenuBar = new JMenuBar();
		ui.getUIFrame().setJMenuBar(myMenuBar);

		// building the file menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		myMenuBar.add(fileMenu);	

		//add openGFile menu item to file menu
		openSource = new JMenuItem("Open Source Ontology...",new ImageIcon("../images/fileImage.gif"));
		//openSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openSource.setMnemonic(KeyEvent.VK_O);
		openSource.addActionListener(this);
		fileMenu.add(openSource);
		
		//add openGFile menu item to file menu
		openTarget = new JMenuItem("Open Target Ontology...",new ImageIcon("../images/fileImage.gif"));
		//openTarget.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openTarget.setMnemonic(KeyEvent.VK_O);
		openTarget.addActionListener(this);
		fileMenu.add(openTarget);

		// add separator
		fileMenu.addSeparator();
		
		// Construct the recent files menu.
		menuRecentSource = new JMenu("Recent Sources...");
		menuRecentSource.setMnemonic('u');
		
		menuRecentTarget = new JMenu("Recent Targets...");
		menuRecentTarget.setMnemonic('a');
		
		
		refreshRecentMenus(menuRecentSource, menuRecentTarget);
		
/*		
		menuRecentSourceList = new JMenuItem[10];
		Preferences prefs = Preferences.userRoot().node("/com/advis/agreementMaker");
		int lastsynt = prefs.getInt(PREF_LASTSYNT, 0);
		int lastlang = prefs.getInt(PREF_LASTLANG, 1);
		*/
		//menuRecentSource.add( new JMenu());
		
		fileMenu.add(menuRecentSource);
		fileMenu.add(menuRecentTarget);
		openMostRecentPair = new JMenuItem("Open most recent pair");
		openMostRecentPair.addActionListener(this);
		fileMenu.add(openMostRecentPair);
		fileMenu.addSeparator();
		//private JMenuItem menuRecentSource, menuRecentTarget;
		//private JMenuItem menuRecentSourceList[], menuRecentTargetList[]; // the list of recent files

		// add exit menu item to file menu
		xit = new JMenuItem("Exit", KeyEvent.VK_X);
		xit.addActionListener(this);
		fileMenu.add(xit);
		
		
		// Build view menu in the menu bar: TODO
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		myMenuBar.add(viewMenu);

		//All show and hide details has been removed right now
		// add separator
		//viewMenu.addSeparator();

		// add keyItem 
		keyItem = new JMenuItem("Colors",KeyEvent.VK_K);
		keyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK)); 	                
		keyItem.addActionListener(this);
		viewMenu.add(keyItem);
		
		viewMenu.addSeparator();
		
		// add "Disable Visualization" option to the view menu
		disableVisualizationItem = new JCheckBoxMenuItem("Disable hierarchies visualization");
		disableVisualizationItem.addActionListener(this);
		disableVisualizationItem.setSelected(prefs.getDisableVisualization());
		viewMenu.add(disableVisualizationItem);
		viewMenu.addSeparator();
		
		// add "Selected Matchings Only" option to the view menu
		smoMenuItem = new JCheckBoxMenuItem("Selected Matchings Only");
		smoMenuItem.addActionListener(this);
		smoMenuItem.setSelected(prefs.getSelectedMatchingsOnly());
		viewMenu.add(smoMenuItem);
		viewMenu.addSeparator();
		
		showLocalNameItem = new JCheckBoxMenuItem("Show localnames");
		showLocalNameItem.addActionListener(this);
		showLocalNameItem.setSelected(prefs.getShowLocalname());
		viewMenu.add(showLocalNameItem);
		
		showLabelItem = new JCheckBoxMenuItem("Show labels");
		showLabelItem.addActionListener(this);
		showLabelItem.setSelected(prefs.getShowLabel());
		viewMenu.add(showLabelItem);
		//Fake menus..********************************.
		/*
		ontologyMenu = new JMenu("Ontology");

		evaluationMenu = new JMenu("Evaluation");
		myMenuBar.add(ontologyMenu);


		*/
		
		//ontology menu
		ontologyMenu = new JMenu("Ontology");
		ontologyMenu.setMnemonic('O');
		ontologyDetails = new JMenuItem("Ontology details");
		ontologyDetails.addActionListener(this); 
		ontologyMenu.add(ontologyDetails);
		myMenuBar.add(ontologyMenu);
		
		matchingMenu = new JMenu("Matching");
		matchingMenu.setMnemonic('M');
		manualMapping = new JMenuItem("Manual Mapping"); 
		manualMapping.addActionListener(this);
		matchingMenu.add(manualMapping);
		
		userFeedBack = new JMenuItem("User Feedback Loop");
		userFeedBack.addActionListener(this);
		matchingMenu.add(userFeedBack);
		
		matchingMenu.addSeparator();
		newMatching = new JMenuItem("New empty matching");
		newMatching.addActionListener(this);
		matchingMenu.add(newMatching);
		runMatching = new JMenuItem("Run selected matcher");
		runMatching.addActionListener(this);
		matchingMenu.add(runMatching);
		copyMatching = new JMenuItem("Copy selected matchings");
		copyMatching.addActionListener(this);
		matchingMenu.add(copyMatching);
		deleteMatching = new JMenuItem("Delete selected matchings");
		deleteMatching.addActionListener(this);
		matchingMenu.add(deleteMatching);
		clearAll = new JMenuItem("Clear All");
		clearAll.addActionListener(this);
		matchingMenu.add(clearAll);
		matchingMenu.addSeparator();
		
		doRemoveDuplicates = new JMenuItem("Remove Duplicate Alignments");
		doRemoveDuplicates.addActionListener(this);
		matchingMenu.add(doRemoveDuplicates);
		matchingMenu.addSeparator();
		
		saveMatching = new JMenuItem("Save selected matchings into a file");
		saveMatching.addActionListener(this);
		matchingMenu.add(saveMatching);
		matchingMenu.addSeparator();
		refEvaluateMatching = new JMenuItem("Evaluate with reference file");
		refEvaluateMatching.addActionListener(this);
		matchingMenu.add(refEvaluateMatching);
		myMenuBar.add(matchingMenu);
		
		
		// Build help menu in the menu bar.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		myMenuBar.add(helpMenu);

		// add menu item to help menu
		howToUse = new JMenuItem("Help", new ImageIcon("images/helpImage.gif"));
		howToUse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));                	
		howToUse.setMnemonic(KeyEvent.VK_H);
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);

		// add about item to help menu
		aboutItem = new JMenuItem("About Agreement Maker", new ImageIcon("images/aboutImage.gif"));
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));                
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);
		

	}
	

	/**
	 * This method reads the XML or OWL files and creates trees for mapping
	 */	
	 public void openAndReadFilesForMapping(int fileType){
		new OpenOntologyFileDialog(fileType, ui);
	 }

	
	 
	 /**
	  * Function that is called when to user wants to close the program. 
	  */
	 public void confirmExit() {
		int n = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit ?","Exit Agreement Maker",JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			System.out.println("Exiting the program.\n");
			System.exit(0);   
		}
	 }
}
