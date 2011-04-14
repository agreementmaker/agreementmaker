package am.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import am.AMException;
import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.ProfilingDialog;
import am.app.userfeedbackloop.ui.UFLControlGUI;
import am.extension.ClusteringEvaluation.ClusteringEvaluationPanel;
import am.tools.LexiconLookup.LexiconLookupPanel;
import am.tools.ThresholdAnalysis.ThresholdAnalysis;
import am.tools.WordNetLookup.WordNetLookupPanel;
import am.tools.seals.SealsPanel;
import am.userInterface.VisualizationChangeEvent.VisualizationEventType;
import am.userInterface.find.FindDialog;
import am.userInterface.find.FindInterface;
import am.userInterface.sidebar.provenance.ProvenanceMenuItem;
import am.userInterface.sidebar.provenance.ProvenanceSidebar;
import am.userInterface.sidebar.vertex.VertexDescriptionPane;
import am.userInterface.table.MatchersTablePanel;
import am.visualization.MatcherAnalyticsPanel;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;
import am.visualization.matrixplot.MatrixPlotPanel;


public class UIMenu implements ActionListener {
	
	// create Top Level menus
	private JMenu fileMenu, editMenu, viewMenu, helpMenu, matchersMenu, toolsMenu, ontologyMenu;
	
	// File menu.
	private JMenuItem xit, openFiles, openMostRecentPair,
					  closeSource, closeTarget, closeBoth, saveAlignment, loadAlignment;
	private JMenu menuRecentSource, menuRecentTarget;
	
	// Edit menu.
	private JMenuItem itemFind;
	//private JMenuItem undo, redo;
	
	// View menu.
	private JMenuItem colorsItem, itemViewsCanvas;
	private ProvenanceMenuItem provenanceItem;
	private JCheckBoxMenuItem 	smoMenuItem,  
								showLabelItem, 
								showLocalNameItem, 
								showMappingsShortname, 
								disableVisualizationItem;
	private JMenu menuViews;  // Views submenu.  TODO: Rename this to something more descriptive.
	private JMenu menuLexicons; // the Lexicons sub menu;
	private JMenuItem menuLexiconsViewOntSource, menuLexiconsViewOntTarget, menuLexiconsViewWNSource, menuLexiconsViewWNTarget, 
					  menuLexiconsBuildOntSource, menuLexiconsBuildOntTarget, menuLexiconsBuildWNSource, menuLexiconsBuildWNTarget,
					  menuLexiconsBuildAll;
	
	// Ontology menu.
	private JMenuItem ontologyDetails, ontologyProfiling;
	
	// Tools menu.
	private JMenuItem wordnetLookupItem, sealsItem, clusteringEvaluation;//, informationMatching;
	
	// Matchers menu.
	private JMenuItem userFeedBack, newMatching, runMatching, copyMatching, deleteMatching, clearAll, 
					  doRemoveDuplicates,
					  refEvaluateMatching,
					  thresholdAnalysis, TEMP_viewClassMatrix, TEMP_viewPropMatrix, TEMP_matcherAnalysisClasses, TEMP_matcherAnalysisProp;
	
	//private JMenu	menuExport;
	//private JMenuItem exportMatrixCSV;
	
	
	// Help menu.
	private JMenuItem howToUse, aboutItem;		
	
	

	//creates a menu bar
	private JMenuBar myMenuBar;
	
	private UI ui;  // reference to the main ui.

	private JMenuItem menuLexiconsClearAll;
	
	
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
		
		AppPreferences prefs = Core.getAppPreferences();
		
		// first we start by removing all sub menus
		recentsource.removeAll();
		recenttarget.removeAll();
		
		// then populate the menus again.
		for( int i = 0; i < prefs.countRecentSources(); i++) {
			JMenuItem menuitem;
			String extraNote = new String();
			if( prefs.getRecentSourceOnDisk(i) ) {
				if( prefs.getRecentSourceOnDiskPersistent(i) ) extraNote += "on disk, persistent";
				else extraNote += "on disk, not persistent";
			}
			
			if( !extraNote.isEmpty() ) {
				menuitem = new JMenuItem(i + ".  " + prefs.getRecentSourceFileName(i) + " (" + extraNote + ")");
			} else { 
				menuitem = new JMenuItem(i + ".  " + prefs.getRecentSourceFileName(i));
			}
			menuitem.setActionCommand("source" + i);
			menuitem.setMnemonic( 48 + i);
			menuitem.addActionListener(this);
			menuRecentSource.add(menuitem);
		}
		
		for( int i = 0; i < prefs.countRecentTargets(); i++) {
			JMenuItem menuitem;
			
			String extraNote = new String();
			if( prefs.getRecentTargetOnDisk(i) ) {
				if( prefs.getRecentSourceOnDiskPersistent(i) ) extraNote += "persistent on disk";
				else extraNote += "on disk";
			}
			
			if( !extraNote.isEmpty() ) {
				menuitem = new JMenuItem(i + ".  " + prefs.getRecentTargetFileName(i) + " (" + extraNote + ")");
			} else { 
				menuitem = new JMenuItem(i + ".  " + prefs.getRecentTargetFileName(i));
			}
			menuitem.setActionCommand("target" + i);
			menuitem.setMnemonic( 48 + i);
			menuitem.addActionListener(this);
			menuRecentTarget.add(menuitem);
		}
		
	}
	
	
	
	public void actionPerformed (ActionEvent ae){
		AppPreferences prefs = Core.getAppPreferences();
		
		try {
			Object obj = ae.getSource();
			MatchersControlPanel controlPanel = ui.getControlPanel();
			if (obj == xit){
				// confirm exit
				confirmExit();
				// if it is no, then do nothing
				return;
			} else if ( obj == itemFind ) {
				// we are going to be searching throught the currently visible tab
				Object visibleTab = Core.getUI().getCurrentTab();
				if( visibleTab instanceof FindInterface ) {
					FindDialog fd = new FindDialog( (FindInterface) visibleTab);
					fd.setVisible(true);
				}
			}else if (obj == colorsItem){
				new Legend();	
			}else if(obj == provenanceItem){//TODO: when all he matchers are gone need to switch back to the other pane
				JSplitPane uiPane=Core.getUI().getUISplitPane();
				if(uiPane.getRightComponent() instanceof ProvenanceSidebar)
				{
					ProvenanceSidebar p=(ProvenanceSidebar)uiPane.getRightComponent();
					uiPane.setRightComponent(p.getOldComponent());
					provenanceItem.setText("Show Provenance");
					//provenanceItem.set
				}
				else{
					ProvenanceSidebar p= new ProvenanceSidebar();
					
					p.setOldComponent(Core.getUI().getUISplitPane().getRightComponent());
					Core.getUI().getUISplitPane().setRightComponent(p);
					provenanceItem.setText("Hide Provenance");
				}
			}else if (obj == howToUse){
				Utility.displayTextAreaPane(Help.getHelpMenuString(), "Help");
			}else if (obj == openFiles){
				new OpenOntologyFileDialogCombined(ui);
				if( Core.getInstance().sourceIsLoaded() ) {
					menuRecentSource.setEnabled(false);
					closeSource.setEnabled(true);
				}
				if( Core.getInstance().targetIsLoaded() ) {
					menuRecentTarget.setEnabled(false);
					closeTarget.setEnabled(true);
				}
			}else if (obj == openMostRecentPair){
				int position = 0;
				boolean loadedSecond = false;
				//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
				boolean loadedFirst = false;
				if( !Core.getInstance().sourceIsLoaded() ) { 
					loadedFirst = ui.openFile( prefs.getRecentSourceFileName(position), 
												GlobalStaticVariables.SOURCENODE, 
												prefs.getRecentSourceSyntax(position), 
												prefs.getRecentSourceLanguage(position), 
												prefs.getRecentSourceSkipNamespace(position), 
												prefs.getRecentSourceNoReasoner(position),
												prefs.getRecentSourceOnDisk(position),
												prefs.getRecentSourceOnDiskDirectory(position),
												prefs.getRecentSourceOnDiskPersistent(position));
				} else {
					loadedFirst = true;
				}
				
				if( loadedFirst ) {
					closeBoth.setEnabled(true);
					// load the second ontology
					//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
					if( !Core.getInstance().targetIsLoaded() ) {
						loadedSecond = ui.openFile( prefs.getRecentTargetFileName(position), 
													GlobalStaticVariables.TARGETNODE,
													prefs.getRecentTargetSyntax(position), 
													prefs.getRecentTargetLanguage(position), 
													prefs.getRecentTargetSkipNamespace(position), 
													prefs.getRecentTargetNoReasoner(position),
													prefs.getRecentTargetOnDisk(position),
													prefs.getRecentTargetOnDiskDirectory(position),
													prefs.getRecentSourceOnDiskPersistent(position));
					} else {
						loadedSecond = true;
					}
						
				}
				else { return; }
				
				if( !loadedSecond ) {
					// user canceled the second ontology
					if( loadedFirst ) {
						closeSource.setEnabled(true);
						menuRecentSource.setEnabled(false);
					}
					return;
				}
				
				// grey out menus
				openFiles.setEnabled(false);
				closeSource.setEnabled(true);
				menuRecentSource.setEnabled(false);
				
				closeTarget.setEnabled(true);
				menuRecentTarget.setEnabled(false);
				
				openMostRecentPair.setEnabled(false);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Core.getUI().getCanvas().repaint();
					}
				});
				
			}else if (obj == aboutItem){
				new AboutDialog();
				//displayOptionPane("Agreement Maker 3.0\nAdvis research group\nThe University of Illinois at Chicago 2004","About Agreement Maker");
			}
			else if( obj == disableVisualizationItem ) {
				// Save the setting that has been changed
				boolean disableVis = disableVisualizationItem.isSelected();
				prefs.saveDisableVisualization(disableVis);
				ui.getCanvas().setDisableVisualization(disableVis);
				ui.redisplayCanvas();
			}
			else if( obj == smoMenuItem ) {
				// Save the SMO setting that has been changed
				boolean smoStatus = smoMenuItem.isSelected();
				prefs.saveSelectedMatchingsOnly(smoStatus);
				ui.getCanvas().setSMO(smoStatus);
				ui.redisplayCanvas();
			}
			else if( obj == showLabelItem || obj == showLocalNameItem ) {
				// Save the setting that has been changed
				boolean showLabel = showLabelItem.isSelected();
				prefs.saveShowLabel(showLabel);
				ui.getCanvas().setShowLabel(showLabel);
				boolean showLocalname = showLocalNameItem.isSelected();
				prefs.saveShowLocalname(showLocalname);
				ui.getCanvas().setShowLocalName(showLocalname);
				ui.redisplayCanvas();
			} else if( obj == showMappingsShortname ) {
				
				// thread safe event firing
		    	Runnable fireNewEvent = new Runnable() {
		    	    public void run() {
						VisualizationChangeEvent vce = new VisualizationChangeEvent(showMappingsShortname, 
								   VisualizationEventType.TOGGLE_SHOWMAPPINGSSHORTNAME );

				    	Core.getInstance().fireEvent(vce);
		    	    }
		    	};
		    	SwingUtilities.invokeLater(fireNewEvent);
				
		    	// save the setting in preferences
		    	prefs.saveShowMappingsShortname(showMappingsShortname.isSelected());
			}
			else if( obj == sealsItem ) {
				// open up the SEALS Interface tab
				SealsPanel sp = new SealsPanel();
				ui.addTab("SEALS", null, sp, "SEALS Interface");
			}
			else if( obj == wordnetLookupItem ) {
				// open up the WordNet lookup interface
				WordNetLookupPanel wnlp = new WordNetLookupPanel();
				ui.addTab("WordNet", null, wnlp, "Query the WordNet dictionary.");
			}
			else if( obj == userFeedBack ) {
				UFLControlGUI ufl_control = new UFLControlGUI(ui);
				ufl_control.displayInitialScreen();
				ui.addTab("User Feedback Loop", null, ufl_control, "User Feedback Loop");	
			}
			else if( obj == clusteringEvaluation ) {
				ui.addTab("Clustering Evaluation",null, new ClusteringEvaluationPanel(), "Clustering Evaluation");
			}
			else if(obj == newMatching) {
				controlPanel.newManual();
			}
			else if(obj == runMatching) {
				controlPanel.match();
			}
			else if(obj == copyMatching) {
				controlPanel.copy();
			}
			else if(obj == deleteMatching) {
				controlPanel.delete();
			}
			else if( obj == saveAlignment) {
				controlPanel.export();
			}
			else if( obj == loadAlignment ) {
				controlPanel.importa();
			}
			else if(obj == refEvaluateMatching) {
				controlPanel.evaluate();
			}
			else if(obj == clearAll) {
				controlPanel.clearAll();
			}
			else if(obj == ontologyDetails) {
				ontologyDetails();
			} else if( obj == ontologyProfiling ) {
				if(!Core.getInstance().ontologiesLoaded() ) {
					Utility.displayErrorPane("You have to load Source and Target ontologies before selecting an ontology profiling algorithm.\nClick on File Menu and select Open Ontology functions.", null);
					return;
				}

				// show profiling dialog
				ProfilingDialog pd = new ProfilingDialog();
			}
			else if( obj == itemViewsCanvas ) {

				JPanel CanvasPanel = new JPanel();
				CanvasPanel.setLayout(new BorderLayout());
				
				Canvas goodOldCanvas = new Canvas(ui);
				goodOldCanvas.setFocusable(true);
				
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setWheelScrollingEnabled(true);
				scrollPane.getVerticalScrollBar().setUnitIncrement(20);
				
				scrollPane.setViewportView(goodOldCanvas);
				goodOldCanvas.setScrollPane(scrollPane);
				
				JPanel jPanel = null;
				System.out.println("opening file");
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.OWLFILE);//takes care of fields for XML files as well
			    jPanel.setMinimumSize(new Dimension(200,480));
		
				JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, jPanel);
				splitPane.setOneTouchExpandable(true);
				splitPane.setResizeWeight(1.0);
				splitPane.setMinimumSize(new Dimension(640,480));
				splitPane.setPreferredSize(new Dimension(640,480));
				splitPane.getLeftComponent().setPreferredSize(new Dimension(640,480));
				// add scrollpane to the panel and add the panel to the frame's content pane
				
				CanvasPanel.add(splitPane, BorderLayout.CENTER);
				//frame.getContentPane().add(splitPane, BorderLayout.CENTER);

				
				//panelControlPanel = new ControlPanel(this, uiMenu, canvas);
				MatchersControlPanel matcherControlPanel = new MatchersControlPanel();
				CanvasPanel.add(matcherControlPanel, BorderLayout.PAGE_END);
				//frame.getContentPane().add(matcherControlPanel, BorderLayout.PAGE_END);
				
				
				ui.addTab("Canvas View", null, CanvasPanel, "Canvas View");
				
			}
			else if( obj == doRemoveDuplicates ) {
				// TODO: Get rid of this from here.
				
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
					
					Alignment<Mapping> firstClassSet = firstMatcher.getClassAlignmentSet();
					Alignment<Mapping> secondClassSet = secondMatcher.getClassAlignmentSet();
					
					Alignment<Mapping> firstPropertiesSet = firstMatcher.getPropertyAlignmentSet();
					Alignment<Mapping> secondPropertiesSet = secondMatcher.getPropertyAlignmentSet();
					
					Alignment<Mapping> combinedClassSet = new Alignment<Mapping>(Ontology.ID_NONE,Ontology.ID_NONE);
					Alignment<Mapping> combinedPropertiesSet = new Alignment<Mapping>(Ontology.ID_NONE,Ontology.ID_NONE);

					// double nested loop, later I will write a better algorithm -cos
					for( i = 0; i < firstClassSet.size(); i++ ) {
						Mapping candidate = firstClassSet.get(i);
						boolean foundDuplicate = false;
						for( j = 0; j < secondClassSet.size(); j++ ) {
							Mapping test = secondClassSet.get(j);							
						
						
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
						
						if( !foundDuplicate ) combinedClassSet.add(candidate);
						
					}

					for( i = 0; i < secondClassSet.size(); i++ ) {
						Mapping candidate = secondClassSet.get(i);
						boolean foundDuplicate = false;
						for( j = 0; j < firstClassSet.size(); j++ ) {
							Mapping test = firstClassSet.get(j);							
						
						
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
						
						if( !foundDuplicate ) combinedClassSet.add(candidate);
						
					}
					
					
					// now the properties.
					
					// double nested loop, later I will write a better algorithm -cos
					for( i = 0; i < firstPropertiesSet.size(); i++ ) {
						Mapping candidate = firstPropertiesSet.get(i);
						boolean foundDuplicate = false;
						for( j = 0; j < secondPropertiesSet.size(); j++ ) {
							Mapping test = secondPropertiesSet.get(j);							
						
						
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
						
						if( !foundDuplicate ) combinedPropertiesSet.add(candidate);
						
					}

					for( i = 0; i < secondPropertiesSet.size(); i++ ) {
						Mapping candidate = secondPropertiesSet.get(i);
						boolean foundDuplicate = false;
						for( j = 0; j < firstPropertiesSet.size(); j++ ) {
							Mapping test = firstPropertiesSet.get(j);							
						
						
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
						
						if( !foundDuplicate ) combinedPropertiesSet.add(candidate);
						
					}
					
					
					AbstractMatcher newMatcher = new UserManualMatcher();
					
					newMatcher.setClassesAlignmentSet(combinedClassSet);
					newMatcher.setPropertiesAlignmentSet(combinedClassSet);
					newMatcher.setRegistryEntry(MatchersRegistry.UniqueMatchings);
					newMatcher.setID( Core.getInstance().getNextMatcherID());
					
					m.addMatcher(newMatcher);
					
					
					
				}
				
				
				
			} else if( obj == closeSource ) {
				if( Core.getInstance().targetIsLoaded() ) {
					// confirm with the user that we should reset matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						closeSource.setEnabled(false); // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						openFiles.setEnabled(true);
						menuRecentSource.setEnabled(true);
					}
				} else {
					// if there is no target loaded, we don't have to reset matchings.
					//controlPanel.resetMatchings();
					Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
					closeSource.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
					// and we need to enable the source ontology loading menu entries
					openFiles.setEnabled(true);
					menuRecentSource.setEnabled(true);
				}
				if( !Core.getInstance().sourceIsLoaded() && !Core.getInstance().targetIsLoaded() ) openMostRecentPair.setEnabled(true);
			} else if( obj == closeTarget ) {
				if( Core.getInstance().sourceIsLoaded() ) {
					// confirm with the user that we should reset any matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						closeTarget.setEnabled(false); // the target ontology has been removed, grey out the menu entry
						// and we need to enable the target ontology loading menu entries
						//openTarget.setEnabled(true);
						menuRecentTarget.setEnabled(true);
					}
				} else {
					// if there is no source ontology loaded, we don't have to ask the user
					Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
					closeTarget.setEnabled(false);  // the target ontology has been removed, grey out the menu entrys
					// and we need to enable the target ontology loading menu entries
					//openTarget.setEnabled(true);
					menuRecentTarget.setEnabled(true);
				}
				if( !Core.getInstance().sourceIsLoaded() && !Core.getInstance().targetIsLoaded() ) openMostRecentPair.setEnabled(true);
			} else if( obj == closeBoth ) {
				if( Core.getInstance().ontologiesLoaded() ) {
					// confirm with the user that we should reset matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						// and we need to enable the source ontology loading menu entries
						openFiles.setEnabled(true);
						openMostRecentPair.setEnabled(true);
						//openTarget.setEnabled(true);
						menuRecentSource.setEnabled(true);
						menuRecentTarget.setEnabled(true);
						closeSource.setEnabled(false); // the source ontology has been removed, grey out the menu entry
						closeTarget.setEnabled(false); // the target ontology has been removed, grey out the menu entry
						closeBoth.setEnabled(false);
					}
				} else {
					if( Core.getInstance().sourceIsLoaded() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						openMostRecentPair.setEnabled(true);
						closeSource.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						openFiles.setEnabled(true);
						menuRecentSource.setEnabled(true);
						if( !Core.getInstance().targetIsLoaded() ) closeBoth.setEnabled(false);
					}
					if( Core.getInstance().targetIsLoaded() ) {
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						openMostRecentPair.setEnabled(true);
						closeTarget.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						//openTarget.setEnabled(true);
						menuRecentTarget.setEnabled(true);
						if( !Core.getInstance().sourceIsLoaded() ) closeBoth.setEnabled(false);
					}
				}
			} else if( obj == thresholdAnalysis ) {
				// decide if we are running in a single mode or batch mode
				if( Utility.displayConfirmPane("Are you running a batch mode?", "Batch mode?") ) {
					String batchFile = JOptionPane.showInputDialog(null, "Batch File?");
					String outputDirectory = JOptionPane.showInputDialog(null, "Output Directory?");
					String matcherName = (new MatcherParametersDialog()).getMatcher().getRegistryEntry().getMatcherName();
					MatchersRegistry matcher = MatcherFactory.getMatchersRegistryEntry(matcherName);
					if( Utility.displayConfirmPane("Using matcher: " + matcherName, "Ok?") ) {
						ThresholdAnalysis than = new ThresholdAnalysis(matcher);
						than.setBatchFile(batchFile);
						than.setOutputDirectory(outputDirectory);
						than.execute();
					}
				} else {
					// single mode
					String referenceAlignment = JOptionPane.showInputDialog(null, "Reference Alignment?");
					String outputDirectory = JOptionPane.showInputDialog(null, "Output Directory?");
					String prefix = JOptionPane.showInputDialog(null, "File name? (leave empty to use matcher name)");
					if( prefix != null ) prefix.trim();
					int[] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
					if( rowsIndex.length == 0 ) { Utility.displayErrorPane("You must select a matcher from the control panel when running in single mode.", "Error"); return; }
					AbstractMatcher matcherToBeAnalyzed = Core.getInstance().getMatcherInstances().get(rowsIndex[0]);
					if( Utility.displayConfirmPane("Using matcher: " + matcherToBeAnalyzed.getRegistryEntry().getMatcherName(), "Ok?") ) {
						ThresholdAnalysis than = new ThresholdAnalysis(matcherToBeAnalyzed);
						than.setReferenceAlignment(referenceAlignment);
						than.setOutputDirectory(outputDirectory);
						if( prefix != null && !prefix.isEmpty() ) than.setOutputPrefix(prefix);
						else { than.setOutputPrefix(matcherToBeAnalyzed.getClass().getSimpleName() + "_" + System.currentTimeMillis() + "_"); };
						than.execute();
					}
				}
			} else if( obj == menuLexiconsViewOntSource ) {
				showLexiconLookupPanel( Core.getInstance().getSourceOntology(), LexiconRegistry.ONTOLOGY_LEXICON);
			} else if( obj == menuLexiconsViewOntTarget ) {
				showLexiconLookupPanel( Core.getInstance().getTargetOntology(), LexiconRegistry.ONTOLOGY_LEXICON );
			} else if( obj == menuLexiconsViewWNSource ) {
				showLexiconLookupPanel( Core.getInstance().getSourceOntology(), LexiconRegistry.WORDNET_LEXICON);
			} else if( obj == menuLexiconsViewWNTarget ) {
				showLexiconLookupPanel( Core.getInstance().getTargetOntology(), LexiconRegistry.WORDNET_LEXICON);
			} else if( obj == TEMP_viewClassMatrix ) {
				// get the currently selected matcher
				ArrayList<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
				AbstractMatcher selectedMatcher;
				int[] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
				if( rowsIndex.length == 0 ) { Utility.displayErrorPane("No matcher is selected.", "Error"); return; }
				selectedMatcher = list.get(rowsIndex[0]); // we only care about the first matcher selected
				
				if( selectedMatcher.getClassesMatrix() == null ) { Utility.displayErrorPane("The matcher has not computed a classes similarity matrix.", "Error"); return; }
				
				MatrixPlotPanel mp = new MatrixPlotPanel( selectedMatcher, selectedMatcher.getClassesMatrix(), null);
				
				mp.getPlot().draw(false);
				JPanel plotPanel = new JPanel();
				plotPanel.add(mp);
				Core.getUI().addTab("MatrixPlot Class", null , plotPanel , selectedMatcher.getRegistryEntry().getMatcherName());
			} else if( obj == TEMP_viewPropMatrix ) {
				// get the currently selected matcher
				ArrayList<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
				AbstractMatcher selectedMatcher;
				int[] rowsIndex = Core.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
				if( rowsIndex.length == 0 ) { Utility.displayErrorPane("No matcher is selected.", "Error"); return; }
				selectedMatcher = list.get(rowsIndex[0]); // we only care about the first matcher selected
				
				if( selectedMatcher.getPropertiesMatrix() == null ) { Utility.displayErrorPane("The matcher has not computed a classes similarity matrix.", "Error"); return; }
				
				MatrixPlotPanel mp = new MatrixPlotPanel( selectedMatcher, selectedMatcher.getPropertiesMatrix(), null);
				
				mp.getPlot().draw(false);
				JPanel plotPanel = new JPanel();
				plotPanel.add(mp);
				Core.getUI().addTab("MatrixPlot Prop", null , plotPanel , selectedMatcher.getRegistryEntry().getMatcherName());
			} else if( obj == TEMP_matcherAnalysisClasses ) {
				final MatcherAnalyticsPanel ma = new MatcherAnalyticsPanel( VisualizationType.CLASS_MATRIX );
				
				Runnable callOnExit = new Runnable() {
					public void run() {
						Core.getInstance().removeMatcherChangeListener(ma);
					}
				};
				
				Core.getUI().addTab("Matcher Analytics: Classes", null, ma, "Classes", callOnExit);
				Core.getInstance().addMatcherChangeListener(ma);
				
			} else if( obj == TEMP_matcherAnalysisProp ) {
				final MatcherAnalyticsPanel ma = new MatcherAnalyticsPanel( VisualizationType.PROPERTIES_MATRIX );
				
				Runnable callOnExit = new Runnable() {
					public void run() {
						Core.getInstance().removeMatcherChangeListener(ma);
					}
				};
				
				Core.getUI().addTab("Matcher Analytics: Properties", null, ma, "Properties", callOnExit);
				Core.getInstance().addMatcherChangeListener(ma);
			} else if ( obj == menuLexiconsBuildAll ) {
				// Lexicons -> Build all ...
				LexiconBuilderDialog d = new LexiconBuilderDialog();
				d.setVisible(true);
			} else if ( obj == menuLexiconsClearAll ) {
				// Lexicons -> Clear all ...
				if( Utility.displayConfirmPane("Are you sure you want to clear the existing lexicons?\nYou will lose any parameters that were set.", "Clear lexicons?") )
					Core.getLexiconStore().clear();
			}
			
			
			// TODO: find a Better way to do this
			
			String command = ae.getActionCommand();  // get the command string we set
			if( command.length() == 7 ) { // the only menus that set an action command  are the recent menus, so we're ok.
				
				char index[] = new char[1];  // '0' - '9'
				char ontotype[] = new char[1]; // 's' or 't' (source or target)
				
				command.getChars(0, 1 , ontotype, 0);  // get the first character of the sting
				command.getChars(command.length() - 1, command.length(), index, 0); // get the last character of the string
				
				// based on the first and last characters of the action command, we can tell which menu was clicked.
				// the rest is easy
				
				int position = index[0] - 48; // 0 - 9
				switch( ontotype[0] ) {
					case 's':
						//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
						ui.openFile( 
								prefs.getRecentSourceFileName(position), 
								GlobalStaticVariables.SOURCENODE, 
								prefs.getRecentSourceSyntax(position), 
								prefs.getRecentSourceLanguage(position), 
								prefs.getRecentSourceSkipNamespace(position), 
								prefs.getRecentSourceNoReasoner(position),
								prefs.getRecentSourceOnDisk(position),
								prefs.getRecentSourceOnDiskDirectory(position),
								prefs.getRecentSourceOnDiskPersistent(position));
						prefs.saveRecentFile(prefs.getRecentSourceFileName(position), 
											 GlobalStaticVariables.SOURCENODE, 
											 prefs.getRecentSourceSyntax(position), 
											 prefs.getRecentSourceLanguage(position), 
											 prefs.getRecentSourceSkipNamespace(position), 
											 prefs.getRecentSourceNoReasoner(position),
											 prefs.getRecentSourceOnDisk(position),
											 prefs.getRecentSourceOnDiskDirectory(position),
											 prefs.getRecentSourceOnDiskPersistent(position));
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
						
						// Now that we have loaded a source ontology, disable all the source ontology loading menu entries ...
						if( Core.getInstance().ontologiesLoaded() )openFiles.setEnabled(false);
						else openFiles.setEnabled(true);
						if( Core.getInstance().sourceIsLoaded() || Core.getInstance().targetIsLoaded() ) closeBoth.setEnabled(true);
						menuRecentSource.setEnabled(false);
						openMostRecentPair.setEnabled(false);
						// ... and enable the close menu entry 
						closeSource.setEnabled(true);
						
						break;
					case 't':
						//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
						ui.openFile(prefs.getRecentTargetFileName(position), 
									GlobalStaticVariables.TARGETNODE, 
									prefs.getRecentTargetSyntax(position), 
									prefs.getRecentTargetLanguage(position), 
									prefs.getRecentTargetSkipNamespace(position), 
									prefs.getRecentTargetNoReasoner(position),
									prefs.getRecentTargetOnDisk(position),
									prefs.getRecentTargetOnDiskDirectory(position),
									prefs.getRecentTargetOnDiskPersistent(position));
						prefs.saveRecentFile(prefs.getRecentTargetFileName(position), 
											 GlobalStaticVariables.TARGETNODE, 
											 prefs.getRecentTargetSyntax(position), 
											 prefs.getRecentTargetLanguage(position), 
											 prefs.getRecentTargetSkipNamespace(position), 
											 prefs.getRecentTargetNoReasoner(position),
											 prefs.getRecentTargetOnDisk(position),
											 prefs.getRecentTargetOnDiskDirectory(position),
											 prefs.getRecentTargetOnDiskPersistent(position));
						ui.getUIMenu().refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.
						
						// Now that we have loaded a source ontology, disable all the source ontology loading menu entries ...
						if( Core.getInstance().ontologiesLoaded() )openFiles.setEnabled(false);
						else openFiles.setEnabled(true);
						if( Core.getInstance().sourceIsLoaded() || Core.getInstance().targetIsLoaded() ) closeBoth.setEnabled(true);
						menuRecentTarget.setEnabled(false);
						openMostRecentPair.setEnabled(false);
						// ... and enable the close menu entry 
						closeTarget.setEnabled(true);

						
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
			Utility.displayErrorPane(ex.getMessage() + "\n\n" + Utility.UNEXPECTED_ERROR, null);
		}
		
	}
	
	/**
	 * This method adds a new tab to the UI with a lexicon lookup panel on it.
	 * @param o
	 * @param reg
	 * @param tabTitle
	 * @param tabTip
	 * @return Returns null on error.
	 */
	public LexiconLookupPanel showLexiconLookupPanel(Ontology o,	LexiconRegistry reg) {
		if( o != null ) {
			String tabTitle = new String();
			if( Core.getInstance().getSourceOntology() == o ) tabTitle += "Source ";
			else if( Core.getInstance().getTargetOntology() == o ) tabTitle += "Target ";
			else tabTitle += "Ontology " + o.getID() + " ";
			
			tabTitle += reg.getShortName() + " Lexicon";
			
			try {
				final Lexicon lex = Core.getLexiconStore().getLexicon(o.getID(), reg);
				final LexiconLookupPanel lexPanel;
				if( lex.getLookupPanel() == null ) {
					lexPanel = new LexiconLookupPanel(lex);
					ui.addTab(tabTitle, null, lexPanel, null, new Runnable() {
						@Override
						public void run() {
							lex.setLookupPanel(null);
						}
					});
				} else {
					lexPanel = lex.getLookupPanel();
					ui.getTabbedPane().setSelectedComponent(lexPanel);
				}
				return lexPanel;
			} catch (Exception e) {
				e.printStackTrace();
				Utility.displayErrorPane("Could not display lexicon lookup panel.\n\n"+e.getMessage(), "Error");
			}
		} else {
			Utility.displayErrorPane("The ontology cannot be null.", "Error");
		}	
		return null;
	}
	

	public void ontologyDetails() {
		Core c = Core.getInstance();
		Ontology sourceO = c.getSourceOntology();
		Ontology targetO = c.getTargetOntology();
		String sourceTitleString = "Not loaded\n";
		String sourceClassString = "Not loaded\n";
		String sourcePropString = "Not loaded\n";
		String targetTitleString = "Not loaded\n";
		String targetClassString = "Not loaded\n";
		String targetPropString = "Not loaded\n";
		if(sourceO != null) {
			sourceClassString = sourceO.getClassDetails();
			sourcePropString = sourceO.getPropDetails();
			sourceTitleString = sourceO.getTitle();
		}
		if(targetO != null) {
			targetClassString = targetO.getClassDetails();
			targetPropString = targetO.getPropDetails();
			targetTitleString = targetO.getTitle();
		}
		String report = new String();
		
		report+= "Source Ontology:\t" + sourceTitleString +"\n";
		report+= "Target Ontology:\t" + targetTitleString +"\n";
		report+= "\n";
		report+= "Hierarchies             \t#concepts\tdepth\tUC-diameter\tLC-diameter\t#roots\t#leaves\n";
		report+= "Source Classes:\t"+sourceClassString;
		report+= "Source Properties:\t"+sourcePropString;
		report+= "\n";
		report+= "Target Classes:\t"+targetClassString;
		report+= "Target Properties:\t"+targetPropString;
		Utility.displayTextAreaWithDim(report,"Ontology Details", 12, 60);
	}


	public void displayOptionPane(String desc, String title){
			JOptionPane.showMessageDialog(null,desc,title, JOptionPane.PLAIN_MESSAGE);					
	}
	
	
	public void init(){
		
		// need AppPreferences for smoItem, to get if is checked or not.
		AppPreferences prefs = new AppPreferences();

		// building the file menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);	

		//add openGFile menu item to file menu
		openFiles = new JMenuItem("Open Ontologies ...",new ImageIcon("images"+File.separator+"fileImage.png"));
		openFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		//openSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openSource.setMnemonic(KeyEvent.VK_O);
		openFiles.addActionListener(this);
		fileMenu.add(openFiles);
		
		//add openGFile menu item to file menu
	

		// add separator
		fileMenu.addSeparator();
		
		// Construct the recent files menu.
		menuRecentSource = new JMenu("Recent Sources");
		menuRecentSource.setMnemonic('u');
		
		menuRecentTarget = new JMenu("Recent Targets");
		menuRecentTarget.setMnemonic('a');
		
		
		refreshRecentMenus(menuRecentSource, menuRecentTarget);
		
		fileMenu.add(menuRecentSource);
		fileMenu.add(menuRecentTarget);
		openMostRecentPair = new JMenuItem("Open most recent pair");
		openMostRecentPair.addActionListener(this);
		openMostRecentPair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + InputEvent.SHIFT_DOWN_MASK)); 
		fileMenu.add(openMostRecentPair);
		fileMenu.addSeparator();
		//private JMenuItem menuRecentSource, menuRecentTarget;
		//private JMenuItem menuRecentSourceList[], menuRecentTargetList[]; // the list of recent files
		closeSource = new JMenuItem("Close Source Ontology");
		closeSource.addActionListener(this);
		closeSource.setEnabled(false); // there is no source ontology loaded at the beginning
		closeTarget = new JMenuItem("Close Target Ontology");
		closeTarget.addActionListener(this);
		closeTarget.setEnabled(false); // there is no target ontology loaded at the beginning
		closeBoth = new JMenuItem("Close both ontologies");
		closeBoth.addActionListener(this);
		closeBoth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		closeBoth.setEnabled(false);
		
		fileMenu.add(closeSource);
		fileMenu.add(closeTarget);
		fileMenu.add(closeBoth);
		
		fileMenu.addSeparator();
		saveAlignment = new JMenuItem("Save Selected Alignment ...");
		saveAlignment.addActionListener(this);
		saveAlignment.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		fileMenu.add(saveAlignment);
		
		loadAlignment = new JMenuItem("Load Alignment ...");
		loadAlignment.addActionListener(this);
		loadAlignment.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		fileMenu.add(loadAlignment);
		
		
		fileMenu.addSeparator();
		// add exit menu item to file menu
		xit = new JMenuItem("Exit", KeyEvent.VK_X);
		xit.addActionListener(this);
		xit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(xit);
		

		// build the Edit menu
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		itemFind = new JMenuItem("Find", KeyEvent.VK_F);
		itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		itemFind.addActionListener(this);
		editMenu.add(itemFind);
		
		
		
		// Build view menu in the menu bar: TODO
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);

		//All show and hide details has been removed right now
		// add separator
		//viewMenu.addSeparator();

		// add keyItem 
		colorsItem = new JMenuItem("Colors",KeyEvent.VK_K);
		colorsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 	                
		colorsItem.addActionListener(this);
		viewMenu.add(colorsItem);
		
		provenanceItem=new ProvenanceMenuItem("Show Provenance");//,KeyEvent.VK_P);
		provenanceItem.setEnabled(false);
		Core.getInstance().addMatcherChangeListener(provenanceItem);
		provenanceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		provenanceItem.addActionListener(this);
		viewMenu.add(provenanceItem);
		
		viewMenu.addSeparator();
		
		// add "Disable Visualization" option to the view menu
		disableVisualizationItem = new JCheckBoxMenuItem("Disable hierarchies visualization");
		disableVisualizationItem.addActionListener(this);
		disableVisualizationItem.setSelected(prefs.getDisableVisualization());
		// viewMenu.add(disableVisualizationItem);
		
		// add "Selected Matchings Only" option to the view menu
		smoMenuItem = new JCheckBoxMenuItem("Selected Matchings Only");
		smoMenuItem.addActionListener(this);
		smoMenuItem.setSelected(prefs.getSelectedMatchingsOnly());
		//viewMenu.add(smoMenuItem);
		
		showLocalNameItem = new JCheckBoxMenuItem("Show localnames");
		showLocalNameItem.addActionListener(this);
		showLocalNameItem.setSelected(prefs.getShowLocalname());
		viewMenu.add(showLocalNameItem);
		
		showLabelItem = new JCheckBoxMenuItem("Show labels");
		showLabelItem.addActionListener(this);
		showLabelItem.setSelected(prefs.getShowLabel());
		viewMenu.add(showLabelItem);
		
		showMappingsShortname = new JCheckBoxMenuItem("Mappings with Matcher name");
		showMappingsShortname.addActionListener(this);
		showMappingsShortname.setSelected(prefs.getShowMappingsShortname());
		viewMenu.add(showMappingsShortname);
		//viewMenu.addSeparator();
		
		menuViews = new JMenu("New view");
		itemViewsCanvas = new JMenuItem("Canvas");
		itemViewsCanvas.addActionListener(this);
		//itemViewsCanvas2 = new JMenuItem("Canvas2");
		//itemViewsCanvas2.addActionListener(this);
		menuViews.add(itemViewsCanvas);
		//menuViews.add(itemViewsCanvas2);
		//viewMenu.add(menuViews);
		
		// Lexicons menu.
		menuLexicons = new JMenu("Lexicons");
		menuLexicons.setMnemonic('L');
		
		menuLexiconsBuildAll = new JMenuItem("Build all ...");
		menuLexiconsBuildAll.addActionListener(this);
		
		menuLexiconsClearAll = new JMenuItem("Delete all ...");
		menuLexiconsClearAll.addActionListener(this);
		
		JMenu menuLexiconsBuild = new JMenu("Build");
		
		menuLexiconsBuildOntSource = new JMenuItem("Ontology Lexicon: Source ...");
		menuLexiconsBuildOntSource.addActionListener(this);
		menuLexiconsBuildOntTarget = new JMenuItem("Ontology Lexicon: Target ...");
		menuLexiconsBuildOntTarget.addActionListener(this);
		menuLexiconsBuildWNSource = new JMenuItem("WordNet Lexicon: Source ...");
		menuLexiconsBuildWNSource.addActionListener(this);
		menuLexiconsBuildWNTarget = new JMenuItem("WordNet Lexicon: Target ...");
		menuLexiconsBuildWNTarget.addActionListener(this);
		
		menuLexiconsBuild.add(menuLexiconsBuildOntSource);
		menuLexiconsBuild.add(menuLexiconsBuildOntTarget);
		menuLexiconsBuild.addSeparator();
		menuLexiconsBuild.add(menuLexiconsBuildWNSource);
		menuLexiconsBuild.add(menuLexiconsBuildWNTarget);
		
		
		JMenu menuLexiconsView = new JMenu("View");
		
		menuLexiconsViewOntSource = new JMenuItem("Ontology Lexicon: Source");
		menuLexiconsViewOntSource.addActionListener(this);
		menuLexiconsViewOntTarget = new JMenuItem("Ontology Lexicon: Target");
		menuLexiconsViewOntTarget.addActionListener(this);
		menuLexiconsViewWNSource = new JMenuItem("WordNet Lexicon: Source");
		menuLexiconsViewWNSource.addActionListener(this);
		menuLexiconsViewWNTarget = new JMenuItem("WordNet Lexicon: Target");
		menuLexiconsViewWNTarget.addActionListener(this);
		
		menuLexiconsView.add(menuLexiconsViewOntSource);
		menuLexiconsView.add(menuLexiconsViewOntTarget);
		menuLexiconsView.addSeparator();
		menuLexiconsView.add(menuLexiconsViewWNSource);
		menuLexiconsView.add(menuLexiconsViewWNTarget);
		
		menuLexicons.add(menuLexiconsBuildAll);
		menuLexicons.add(menuLexiconsClearAll);
		menuLexicons.addSeparator();
		//menuLexicons.add(menuLexiconsBuild);
		menuLexicons.add(menuLexiconsView);
		
		
		
		/*

		evaluationMenu = new JMenu("Evaluation");
		myMenuBar.add(ontologyMenu);


		*/
		
		//ontology menu
		ontologyMenu = new JMenu("Ontology");
		ontologyMenu.setMnemonic('O');
		ontologyDetails = new JMenuItem("Ontology details");
		ontologyDetails.addActionListener(this); 
		ontologyMenu.add(ontologyDetails);
		
		ontologyMenu.addSeparator();
		
		ontologyProfiling = new JMenuItem("Profiling ...");
		ontologyProfiling.addActionListener(this);
		ontologyMenu.add(ontologyProfiling);
		
		
		
		// **************** Matchers Menu *******************
		matchersMenu = new JMenu("Matchers");
		matchersMenu.setMnemonic('M');
		
		runMatching = new JMenuItem("Run matcher ...");
		runMatching.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		runMatching.addActionListener(this);
		matchersMenu.add(runMatching);
		
		matchersMenu.addSeparator();
		
		
		newMatching = new JMenuItem("New empty matcher");
		newMatching.addActionListener(this);
		matchersMenu.add(newMatching);
		
		copyMatching = new JMenuItem("Copy selected matchings");
		copyMatching.addActionListener(this);
		matchersMenu.add(copyMatching);
		deleteMatching = new JMenuItem("Delete selected matchings");
		deleteMatching.addActionListener(this);
		matchersMenu.add(deleteMatching);
		clearAll = new JMenuItem("Clear All");
		clearAll.addActionListener(this);
		matchersMenu.add(clearAll);
		matchersMenu.addSeparator();
		
		doRemoveDuplicates = new JMenuItem("Remove Duplicate Alignments");
		doRemoveDuplicates.addActionListener(this);
		//matchingMenu.add(doRemoveDuplicates);
		//matchingMenu.addSeparator();
		
		//saveMatching = new JMenuItem("Save selected matchers into a file");
		//saveMatching.addActionListener(this);
		//matchersMenu.add(saveMatching);
		//matchersMenu.addSeparator();
		refEvaluateMatching = new JMenuItem("Evaluate with reference file");
		refEvaluateMatching.addActionListener(this);
		matchersMenu.add(refEvaluateMatching);
		
		thresholdAnalysis = new JMenuItem("Threshold Analysis");
		thresholdAnalysis.addActionListener(this);
		matchersMenu.add(thresholdAnalysis);
		
		
		// *************************** TOOLS MENU ****************************
		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('T');
		
		// Tools -> Wordnet lookup panel...
		wordnetLookupItem = new JMenuItem("Wordnet Lookup ...");
		wordnetLookupItem.setMnemonic(KeyEvent.VK_W);
		wordnetLookupItem.addActionListener(this);
		toolsMenu.add(wordnetLookupItem);
		
		// Tools -> SEALS Interface...
		sealsItem = new JMenuItem("SEALS Interface...");
		sealsItem.setMnemonic(KeyEvent.VK_S);
		sealsItem.addActionListener(this);
		toolsMenu.add(sealsItem);
		
		
		// Tools -> Information Matching
		//JMenu informationMatching = new InformationMatchingMenu(ui);
		//toolsMenu.add(informationMatching);
		
		// Build help menu in the menu bar.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		

		// add menu item to help menu
		howToUse = new JMenuItem("Help", new ImageIcon("images"+File.pathSeparator+"helpImage.gif"));
		howToUse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                	
		howToUse.setMnemonic(KeyEvent.VK_H);
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);

		// add about item to help menu
		aboutItem = new JMenuItem("About AgreementMaker", new ImageIcon("images/aboutImage.gif"));
		aboutItem.setMnemonic(KeyEvent.VK_A);
		//aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));                
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);
		
		
		myMenuBar = new JMenuBar();

		myMenuBar.add(fileMenu);
		myMenuBar.add(editMenu);
		myMenuBar.add(viewMenu);
		myMenuBar.add(menuLexicons);
		myMenuBar.add(ontologyMenu);
		myMenuBar.add(matchersMenu);
		myMenuBar.add(toolsMenu);
		myMenuBar.add(helpMenu);

		ui.getUIFrame().setJMenuBar(myMenuBar);
	}
	

	/**
	 * This method reads the XML or OWL files and creates trees for mapping
	 */	
	 public void openAndReadFilesForMapping(int fileType){
		new OpenOntologyFileDialogCombined(ui);
	 }

	
	 
	 /**
	  * Function that is called when to user wants to close the program. 
	  */
	 public void confirmExit() {
		int n = JOptionPane.showConfirmDialog(Core.getUI().getUIFrame(),"Are you sure you want to exit ?","Exit Agreement Maker",JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			System.out.println("Exiting the program.\n");
			System.exit(0);   
		}
	 }
}
