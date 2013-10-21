package am.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.osgi.framework.Bundle;

import am.AMException;
import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.manualMatcher.UserManualMatcher;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.ontology.profiling.ProfilingDialog;
import am.app.ontology.profiling.metrics.OntologyMetric;
import am.app.ontology.profiling.metrics.OntologyMetricsRegistry;
import am.evaluation.clustering.gvm.GVM_Clustering_Panel;
import am.tools.ThresholdAnalysis.ThresholdAnalysis;
import am.ui.VisualizationChangeEvent.VisualizationEventType;
import am.ui.canvas2.Canvas2;
import am.ui.controlpanel.MatchersControlPanel;
import am.ui.find.FindDialog;
import am.ui.find.FindInterface;
import am.ui.instance.InstanceLookupPanel;
import am.ui.sidebar.duplicatepane.DuplicateSidebar;
import am.ui.sidebar.provenance.ProvenanceSidebar;
import am.ui.table.MatchersTablePanel;
import am.utility.AppPreferences;
import am.utility.numeric.AvgMinMaxNumber;
import am.visualization.MatcherAnalyticsPanel;
import am.visualization.ClusteringEvaluation.ClusteringEvaluationPanel;
import am.visualization.WordNetLookup.WordNetLookupPanel;
import am.visualization.matrixplot.MatrixPlotPanel;
import am.visualization.seals.SealsPanel;

public class UIMenuListener implements ActionListener {

	private UIMenu menu;

	public UIMenuListener(UIMenu menu) {
		this.menu = menu;
	}

	public void actionPerformed (ActionEvent ae){
		AppPreferences prefs = Core.getAppPreferences();

		try {
			Object obj = ae.getSource();
			MatchersControlPanel controlPanel = menu.ui.getControlPanel();
			if (obj == menu.xit){
				// confirm exit
				UICore.getUI().confirmExit();
				// if it is no, then do nothing
				return;
			} else if ( obj == menu.itemFind ) {
				// we are going to be searching throught the currently visible tab
				Object visibleTab = UICore.getUI().getCurrentTab();
				if( visibleTab instanceof FindInterface ) {
					FindDialog fd = new FindDialog( (FindInterface) visibleTab);
					fd.setVisible(true);
				}
			}
			else if (obj == menu.colorsItem) {
				new Legend();	
			}
			else if(obj == menu.provenanceItem) {//TODO: when all he matchers are gone need to switch back to the other pane
				JSplitPane uiPane = UICore.getUI().getUISplitPane();
				if(uiPane.getRightComponent() instanceof ProvenanceSidebar)
				{
					ProvenanceSidebar p=(ProvenanceSidebar)uiPane.getRightComponent();
					uiPane.setRightComponent(p.getOldComponent());
					menu.provenanceItem.setText("Show Provenance");
					//provenanceItem.set
				}
				else{
					ProvenanceSidebar p= new ProvenanceSidebar();

					p.setOldComponent(UICore.getUI().getUISplitPane().getRightComponent());
					UICore.getUI().getUISplitPane().setRightComponent(p);
					menu.provenanceItem.setText("Hide Provenance");
				}
			}
			else if (obj == menu.howToUse) {
				Utility.displayTextAreaPane(Help.getHelpMenuString(), "Help");
			}
			else if (obj == menu.openFiles) {
				new OpenOntologyFileDialogCombined(menu.ui);
				if( Core.getInstance().ontologiesLoaded() ) {
					// create the User Manual Matcher
					DefaultMatcherParameters matcherParams = new DefaultMatcherParameters();
					AbstractMatcher matcher = new UserManualMatcher();
					
					DefaultSelectionParameters selectionParams = new DefaultSelectionParameters();
					SelectionAlgorithm selectionAlgorithm = new MwbmSelection();
					
					MatchingTask task = new MatchingTask(matcher, matcherParams, selectionAlgorithm, selectionParams);
					task.match();
					task.select();
					Core.getInstance().addMatchingTask(task);
				}
				
				if( Core.getInstance().sourceIsLoaded() ) {
					menu.menuRecentSource.setEnabled(false);
					menu.closeSource.setEnabled(true);
				}
				
				if( Core.getInstance().targetIsLoaded() ) {
					menu.menuRecentTarget.setEnabled(false);
					menu.closeTarget.setEnabled(true);
				}
			}
			else if (obj == menu.openMostRecentPair) {
				int position = 0;
				//TODO: make a prefs for loading in db and use the prefs instead of hardcoding a value
				boolean loadedFirst = Core.getInstance().sourceIsLoaded();
				boolean loadedSecond = Core.getInstance().targetIsLoaded();
				if( !loadedFirst ) {
					OntologyDefinition odef = new OntologyDefinition(true, 
							prefs.getRecentSourceFileName(position), 
							OntologyLanguage.getLanguage(prefs.getRecentSourceLanguage(position)),
							OntologySyntax.getSyntax(prefs.getRecentSourceSyntax(position)));
					
					odef.onDiskStorage = prefs.getRecentSourceOnDisk(position);
					odef.onDiskDirectory = prefs.getRecentSourceOnDiskDirectory(position);
					odef.onDiskPersistent = prefs.getRecentSourceOnDiskPersistent(position);
					
					Ontology ont = menu.ui.openFile(odef);
					if( ont != null ) {
						Core.getInstance().setSourceOntology(ont);
						loadedFirst = true;
					}
				}

				if( loadedFirst && !loadedSecond ) {
					// load the second ontology
					OntologyDefinition odef = new OntologyDefinition(true, 
							prefs.getRecentTargetFileName(position), 
							OntologyLanguage.getLanguage(prefs.getRecentTargetLanguage(position)),
							OntologySyntax.getSyntax(prefs.getRecentTargetSyntax(position)));
					
					odef.onDiskStorage = prefs.getRecentTargetOnDisk(position);
					odef.onDiskDirectory = prefs.getRecentTargetOnDiskDirectory(position);
					odef.onDiskPersistent = prefs.getRecentTargetOnDiskPersistent(position);
					
					Ontology ont = menu.ui.openFile( odef );
					
					if( ont != null ) {
						Core.getInstance().setTargetOntology(ont);
						menu.closeBoth.setEnabled(true);
					}

				}				
				else { return; }
				
				if( loadedSecond && loadedFirst ){
					menu.closeBoth.setEnabled(true);
				}

				if( !loadedSecond ) {
					// user canceled the second ontology
					if( loadedFirst ) {
						menu.closeSource.setEnabled(true);
						menu.menuRecentSource.setEnabled(false);
					}
					return;
				}

				// grey out menus
				menu.openFiles.setEnabled(false);
				menu.closeSource.setEnabled(true);
				menu.menuRecentSource.setEnabled(false);

				menu.closeTarget.setEnabled(true);
				menu.menuRecentTarget.setEnabled(false);

				menu.openMostRecentPair.setEnabled(false);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						UICore.getUI().getCanvas().repaint();
					}
				});

			}
			else if (obj == menu.aboutItem){
				new AboutDialog(menu.ui.getUIFrame());
			}
			else if( obj == menu.disableVisualizationItem ) {
				// Save the setting that has been changed
				boolean disableVis = menu.disableVisualizationItem.isSelected();
				prefs.saveDisableVisualization(disableVis);
				menu.ui.getCanvas().setDisableVisualization(disableVis);
				menu.ui.redisplayCanvas();
			}
			else if( obj == menu.smoMenuItem ) {
				// Save the SMO setting that has been changed
				boolean smoStatus = menu.smoMenuItem.isSelected();
				prefs.saveSelectedMatchingsOnly(smoStatus);
				menu.ui.getCanvas().setSMO(smoStatus);
				menu.ui.redisplayCanvas();
			}
			else if( obj == menu.showLabelItem || obj == menu.showLocalNameItem ) {
				// Save the setting that has been changed
				boolean showLabel = menu.showLabelItem.isSelected();
				prefs.saveShowLabel(showLabel);
				menu.ui.getCanvas().setShowLabel(showLabel);
				boolean showLocalname = menu.showLocalNameItem.isSelected();
				prefs.saveShowLocalname(showLocalname);
				menu.ui.getCanvas().setShowLocalName(showLocalname);
				menu.ui.redisplayCanvas();
			} else if( obj == menu.showMappingsShortname ) {

				// thread safe event firing
				Runnable fireNewEvent = new Runnable() {
					public void run() {
						VisualizationChangeEvent vce = new VisualizationChangeEvent(menu.showMappingsShortname, 
								VisualizationEventType.TOGGLE_SHOWMAPPINGSSHORTNAME, null );

						UICore.getInstance().fireEvent(vce);
					}
				};
				SwingUtilities.invokeLater(fireNewEvent);

				// save the setting in preferences
				prefs.saveShowMappingsShortname(menu.showMappingsShortname.isSelected());
			} else if (obj == menu.duplicateView) {
				// Canvas2 wholeCanvas = new Canvas2();

				JSplitPane uiPane = UICore.getUI().getUISplitPane();
				if (uiPane.getRightComponent() instanceof DuplicateSidebar) {
					// view is open, close it now.
					DuplicateSidebar dupSidebar = (DuplicateSidebar) uiPane
							.getRightComponent();

					uiPane.setRightComponent(dupSidebar.getOldComponent());
					menu.duplicateView.setSelected(false);
					// provenanceItem.set
				} else {

					final DuplicateSidebar dupSide = new DuplicateSidebar();
					// JDialog dialog = new JDialog();
					final JScrollPane scroll = (JScrollPane) UICore.getUI().getUISplitPane().getLeftComponent();
					int newWidth = uiPane.getWidth() / 2;
					uiPane.setDividerLocation(newWidth);

					Canvas2 oldCanvas = (Canvas2) scroll.getViewport().getView();
					Canvas2 newCanvas = new Canvas2(dupSide,oldCanvas.getGraphs());

					dupSide.setOldComponent(UICore.getUI().getUISplitPane().getRightComponent());
					Rectangle viewRec = UICore.getUI().getViewport().getBounds();
					
					

					dupSide.getViewport().setView(newCanvas);
					/*viewRec.width = newWidth;
					viewRec.x += newWidth;*/
					dupSide.getViewport().setBounds(viewRec);
					
					final int Pos = newWidth -147;
					
					SwingUtilities.invokeLater(new Runnable (){

						@Override
						public void run() {
							dupSide.getHorizontalScrollBar().setValue(Pos);
						}
					});
					
					dupSide.setBorder(new LineBorder(Color.red));
					// dialog.getContentPane().add(dupSide,
					// BorderLayout.CENTER);

					// dialog.setVisible(true);
					UICore.getUI().getUISplitPane().setRightComponent(dupSide);
					menu.duplicateView.setSelected(true);
				}
				
			}else if( obj == menu.synchronizedViews ) {
				// thread safe event firing
				Runnable fireNewEvent = new Runnable() {
					public void run() {
						VisualizationChangeEvent vce = new VisualizationChangeEvent(menu.synchronizedViews, 
								VisualizationEventType.TOGGLE_SYNCHRONIZATION, null );

						UICore.getInstance().fireEvent(vce);
					}
				};
				SwingUtilities.invokeLater(fireNewEvent);

				// save the setting
				prefs.saveSynchronizedViews(menu.synchronizedViews.isSelected());
			}
			else if( obj == menu.sealsItem ) {
				// open up the SEALS Interface tab
				SealsPanel sp = new SealsPanel();
				menu.ui.addTab("SEALS", null, sp, "SEALS Interface");
			}
			else if( obj == menu.wordnetLookupItem ) {
				// open up the WordNet lookup interface
				WordNetLookupPanel wnlp = new WordNetLookupPanel();
				menu.ui.addTab("WordNet", null, wnlp, "Query the WordNet dictionary.");
			}
			else if( obj == menu.userFeedBack ) {
				throw new RuntimeException("Fix the UI code to interact with OSGi bundles.");
				//UFLControlGUI ufl_control = new UFLControlGUI(menu.ui);
				//ufl_control.displayInitialScreen();
				//menu.ui.addTab("User Feedback Loop", null, ufl_control, "User Feedback Loop");	
			}
			else if( obj == menu.clusteringEvaluation ) {
				menu.ui.addTab("Clustering Evaluation",null, new ClusteringEvaluationPanel(), "Clustering Evaluation");
			}
			else if(obj == menu.newMatching) {
				controlPanel.newManual();
			}
			else if(obj == menu.runMatching) {
				controlPanel.btnMatchClick();
			}
			else if(obj == menu.copyMatching) {
				controlPanel.copy();
			}
			else if(obj == menu.deleteMatching) {
				controlPanel.btnDeleteClick();
			}
			else if( obj == menu.saveAlignment) {
				controlPanel.export();
			}
			else if( obj == menu.loadAlignment ) {
				controlPanel.btnImportClick();
			}
			else if(obj == menu.refEvaluateMatching) {
				controlPanel.btnEvaluateClick();
			}
			else if( obj == menu.clusteringClasses ) {
				/** Clustering with GVM for classes */
				MatchersTablePanel m = controlPanel.getTablePanel();

				int[] selectedRows =  m.getTable().getSelectedRows();

				if(selectedRows.length < 2) {
					Utility.displayErrorPane("You must select at least two matchers in the Matchers Control Panel.", null);
					return;
				}

				List<AbstractMatcher> selectedMatchers = new ArrayList<AbstractMatcher>();
				List<AbstractMatcher> matcherInstances = Core.getInstance().getMatcherInstances();

				for( int i : selectedRows ) {
					selectedMatchers.add(matcherInstances.get(i));
				}

				GVM_Clustering_Panel gvm = new GVM_Clustering_Panel(selectedMatchers);

				JFrame frm = new JFrame();
				frm.setLayout(new BorderLayout());
				frm.add(gvm, BorderLayout.CENTER);
				frm.pack();
				frm.setLocationRelativeTo(null);
				frm.setVisible(true);

			}
			else if(obj == menu.clearAll) {
				controlPanel.clearAll();
			}
			else if(obj == menu.ontologyDetails) {
				ontologyDetails();
			} else if( obj == menu.ontologyProfiling ) {
				if(!Core.getInstance().ontologiesLoaded() ) {
					Utility.displayErrorPane("You have to load Source and Target ontologies before selecting an ontology profiling algorithm.\nClick on File Menu and select Open Ontology functions.", null);
					return;
				}

				// show profiling dialog
				ProfilingDialog pd = new ProfilingDialog(UICore.getUI().getUIFrame());
			}
			else if( obj == menu.doRemoveDuplicates ) {
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
					newMatcher.setID( Core.getInstance().getNextMatcherID());

					//m.addMatcher(newMatcher);
					
					MatchingTask t = new MatchingTask(newMatcher, newMatcher.getParam(), 
							new MwbmSelection(), new DefaultSelectionParameters());
					Core.getInstance().addMatchingTask(t);

				}



			} else if( obj == menu.closeSource ) {
				if( Core.getInstance().targetIsLoaded() ) {
					// confirm with the user that we should reset matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						menu.closeSource.setEnabled(false); // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						menu.openFiles.setEnabled(true);
						menu.menuRecentSource.setEnabled(true);
					}
				} else {
					// if there is no target loaded, we don't have to reset matchings.
					//controlPanel.resetMatchings();
					Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
					menu.closeSource.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
					// and we need to enable the source ontology loading menu entries
					menu.openFiles.setEnabled(true);
					menu.menuRecentSource.setEnabled(true);
				}
				if( !Core.getInstance().sourceIsLoaded() && !Core.getInstance().targetIsLoaded() ) menu.openMostRecentPair.setEnabled(true);
			} else if( obj == menu.closeTarget ) {
				if( Core.getInstance().sourceIsLoaded() ) {
					// confirm with the user that we should reset any matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						menu.closeTarget.setEnabled(false); // the target ontology has been removed, grey out the menu entry
						// and we need to enable the target ontology loading menu entries
						//openTarget.setEnabled(true);
						menu.menuRecentTarget.setEnabled(true);
					}
				} else {
					// if there is no source ontology loaded, we don't have to ask the user
					Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
					menu.closeTarget.setEnabled(false);  // the target ontology has been removed, grey out the menu entrys
					// and we need to enable the target ontology loading menu entries
					//openTarget.setEnabled(true);
					menu.menuRecentTarget.setEnabled(true);
				}
				if( !Core.getInstance().sourceIsLoaded() && !Core.getInstance().targetIsLoaded() ) menu.openMostRecentPair.setEnabled(true);
			} else if( obj == menu.closeBoth ) {
				if( Core.getInstance().ontologiesLoaded() ) {
					// confirm with the user that we should reset matchings
					if( controlPanel.clearAll() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						// and we need to enable the source ontology loading menu entries
						menu.openFiles.setEnabled(true);
						menu.openMostRecentPair.setEnabled(true);
						//openTarget.setEnabled(true);
						menu.menuRecentSource.setEnabled(true);
						menu.menuRecentTarget.setEnabled(true);
						menu.closeSource.setEnabled(false); // the source ontology has been removed, grey out the menu entry
						menu.closeTarget.setEnabled(false); // the target ontology has been removed, grey out the menu entry
						menu.closeBoth.setEnabled(false);
					}
				} else {
					if( Core.getInstance().sourceIsLoaded() ) {
						Core.getInstance().removeOntology( Core.getInstance().getSourceOntology() );
						menu.openMostRecentPair.setEnabled(true);
						menu.closeSource.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						menu.openFiles.setEnabled(true);
						menu.menuRecentSource.setEnabled(true);
						if( !Core.getInstance().targetIsLoaded() ) menu.closeBoth.setEnabled(false);
					}
					if( Core.getInstance().targetIsLoaded() ) {
						Core.getInstance().removeOntology( Core.getInstance().getTargetOntology() );
						menu.openMostRecentPair.setEnabled(true);
						menu.closeTarget.setEnabled(false);  // the source ontology has been removed, grey out the menu entry
						// and we need to enable the source ontology loading menu entries
						//openTarget.setEnabled(true);
						menu.menuRecentTarget.setEnabled(true);
						if( !Core.getInstance().sourceIsLoaded() ) menu.closeBoth.setEnabled(false);
					}
				}
			} else if( obj == menu.thresholdAnalysis ) {
				// decide if we are running in a single mode or batch mode
				if( Utility.displayConfirmPane("Are you running a batch mode?", "Batch mode?") ) {
					String batchFile = JOptionPane.showInputDialog(null, "Batch File?");
					String outputDirectory = JOptionPane.showInputDialog(null, "Output Directory?");
					AbstractMatcher matcher = (new MatcherParametersDialog()).getMatcher();
					if( Utility.displayConfirmPane("Using matcher: " + matcher, "Ok?") ) {
						MatcherParametersDialog dialog = new MatcherParametersDialog(matcher, false, false);
						if( dialog.getParameters() == null ) return;
						ThresholdAnalysis than = new ThresholdAnalysis(matcher, true, dialog.getParameters());
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
					int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
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
			} else if( obj == menu.menuLexiconsViewOntSource ) {
				menu.showLexiconLookupPanel( Core.getInstance().getSourceOntology(), LexiconRegistry.ONTOLOGY_LEXICON);
			} else if( obj == menu.menuLexiconsViewOntTarget ) {
				menu.showLexiconLookupPanel( Core.getInstance().getTargetOntology(), LexiconRegistry.ONTOLOGY_LEXICON );
			} else if( obj == menu.menuLexiconsViewWNSource ) {
				menu.showLexiconLookupPanel( Core.getInstance().getSourceOntology(), LexiconRegistry.WORDNET_LEXICON);
			} else if( obj == menu.menuLexiconsViewWNTarget ) {
				menu.showLexiconLookupPanel( Core.getInstance().getTargetOntology(), LexiconRegistry.WORDNET_LEXICON);
			} else if( obj == menu.TEMP_viewClassMatrix ) {
				// get the currently selected matcher
				List<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
				AbstractMatcher selectedMatcher;
				int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
				if( rowsIndex.length == 0 ) { Utility.displayErrorPane("No matcher is selected.", "Error"); return; }
				selectedMatcher = list.get(rowsIndex[0]); // we only care about the first matcher selected

				if( selectedMatcher.getClassesMatrix() == null ) { Utility.displayErrorPane("The matcher has not computed a classes similarity matrix.", "Error"); return; }

				MatrixPlotPanel mp = new MatrixPlotPanel( selectedMatcher, selectedMatcher.getClassesMatrix(), null);

				mp.getPlot().draw(false);
				JPanel plotPanel = new JPanel();
				plotPanel.add(mp);
				UICore.getUI().addTab("MatrixPlot Class", null , plotPanel , selectedMatcher.getRegistryEntry().getMatcherName());
			} else if( obj == menu.TEMP_viewPropMatrix ) {
				// get the currently selected matcher
				List<AbstractMatcher> list = Core.getInstance().getMatcherInstances();
				AbstractMatcher selectedMatcher;
				int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
				if( rowsIndex.length == 0 ) { Utility.displayErrorPane("No matcher is selected.", "Error"); return; }
				selectedMatcher = list.get(rowsIndex[0]); // we only care about the first matcher selected

				if( selectedMatcher.getPropertiesMatrix() == null ) { Utility.displayErrorPane("The matcher has not computed a classes similarity matrix.", "Error"); return; }

				MatrixPlotPanel mp = new MatrixPlotPanel( selectedMatcher, selectedMatcher.getPropertiesMatrix(), null);

				mp.getPlot().draw(false);
				JPanel plotPanel = new JPanel();
				plotPanel.add(mp);
				UICore.getUI().addTab("MatrixPlot Prop", null , plotPanel , selectedMatcher.getRegistryEntry().getMatcherName());
			} else if( obj == menu.TEMP_matcherAnalysisClasses ) {
				final MatcherAnalyticsPanel ma = new MatcherAnalyticsPanel( alignType.aligningClasses );

				Runnable callOnExit = new Runnable() {
					public void run() {
						Core.getInstance().removeMatcherChangeListener(ma);
					}
				};

				UICore.getUI().addTab("Matcher Analytics: Classes", null, ma, "Classes", callOnExit);
				Core.getInstance().addMatcherChangeListener(ma);

			} else if( obj == menu.TEMP_matcherAnalysisProp ) {
				final MatcherAnalyticsPanel ma = new MatcherAnalyticsPanel( alignType.aligningProperties );

				Runnable callOnExit = new Runnable() {
					public void run() {
						Core.getInstance().removeMatcherChangeListener(ma);
					}
				};

				UICore.getUI().addTab("Matcher Analytics: Properties", null, ma, "Properties", callOnExit);
				Core.getInstance().addMatcherChangeListener(ma);
			} else if ( obj == menu.menuLexiconsBuildAll ) {
				// Lexicons -> Build all ...
				LexiconBuilderDialog d = new LexiconBuilderDialog();
				d.setVisible(true);
			} else if ( obj == menu.menuLexiconsClearAll ) {
				// Lexicons -> Clear all ...
				if( Utility.displayConfirmPane("Are you sure you want to clear the existing lexicons?\nYou will lose any parameters that were set.", "Clear lexicons?") )
					Core.getLexiconStore().clear();
			} else if( obj == menu.ontologyViewEntityList ) {
				Ontology sourceOntology = Core.getInstance().getSourceOntology();
				System.out.println("Source Classes:");
				for( Node currentClass : sourceOntology.getClassesList() ) {
					System.out.println(currentClass.toString());
				}
				System.out.println("Source Properties:");
				for( Node currentProp : sourceOntology.getPropertiesList() ) {
					System.out.println(currentProp.toString());
				}
				Ontology targetOntology = Core.getInstance().getTargetOntology();
				System.out.println("Target Classes:");
				for( Node currentClass : targetOntology.getClassesList() ) {
					System.out.println(currentClass.toString());
				}
				System.out.println("Taget Properties:");
				for( Node currentProp : targetOntology.getPropertiesList() ) {
					System.out.println(currentProp.toString());
				}
			} else if( obj == menu.ontologyAlternateHierarchy ) {
				AlternateHierachyDialog dialog = new AlternateHierachyDialog();
				dialog.setVisible(true); // blocks here until the dialog is dismissed.
			} else if (obj == menu.instanceLookupItem ) {

				Ontology sourceOntology = Core.getInstance().getSourceOntology();
				Ontology targetOntology = Core.getInstance().getTargetOntology();

				if(sourceOntology == null || targetOntology == null ){
					JOptionPane.showMessageDialog(null, "You have to first load ontologies");
					return;
				}

				InstanceLookupPanel lookupPanel = new InstanceLookupPanel(sourceOntology.getInstances(), targetOntology.getInstances());
				UICore.getUI().addTab("Instances Lookup", null , lookupPanel , "Instances Lookup Panel");

			}
			else if( obj == menu.mnuListBundles ) {
				Bundle[] bundles = Core.getInstance().getRegistry().getInstalledBundles();
				StringBuilder strBuilder = new StringBuilder("Installed bundles in the embedded framework:\n");
				for( Bundle b : bundles ) {
					strBuilder.append(b.getSymbolicName());
					strBuilder.append("\n");
				}
				strBuilder.append("\n\nMatcher names:\n");
				List<String> matcherNames=Core.getInstance().getRegistry().getMatcherNames();
				for(String s:matcherNames){
					strBuilder.append(s);
					strBuilder.append("\n");
				}
				//strBuilder.append("existing services in the system\n");
				//Core.getInstance().getContext().getServiceReference(AbstractMatcher.class);

				JOptionPane.showMessageDialog(null, strBuilder.toString());
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
				{
					//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
					
					OntologyDefinition odef = new OntologyDefinition(true, 
							prefs.getRecentSourceFileName(position), 
							OntologyLanguage.getLanguage(prefs.getRecentSourceLanguage(position)),
							OntologySyntax.getSyntax(prefs.getRecentSourceSyntax(position)));
					
					odef.onDiskStorage = prefs.getRecentSourceOnDisk(position);
					odef.onDiskDirectory = prefs.getRecentSourceOnDiskDirectory(position);
					odef.onDiskPersistent = prefs.getRecentSourceOnDiskPersistent(position);
					
					Ontology ont = menu.ui.openFile(odef);
					
					if( ont == null ) return; // ont not loaded
					prefs.saveRecentFile(prefs.getRecentSourceFileName(position), 
							GlobalStaticVariables.SOURCENODE, 
							prefs.getRecentSourceSyntax(position), 
							prefs.getRecentSourceLanguage(position), 
							prefs.getRecentSourceSkipNamespace(position), 
							prefs.getRecentSourceNoReasoner(position),
							prefs.getRecentSourceOnDisk(position),
							prefs.getRecentSourceOnDiskDirectory(position),
							prefs.getRecentSourceOnDiskPersistent(position));
					refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.

					// Now that we have loaded a source ontology, disable all the source ontology loading menu entries ...
					if( Core.getInstance().ontologiesLoaded() ) menu.openFiles.setEnabled(false);
					else menu.openFiles.setEnabled(true);
					if( Core.getInstance().sourceIsLoaded() || Core.getInstance().targetIsLoaded() ) menu.closeBoth.setEnabled(true);
					menu.menuRecentSource.setEnabled(false);
					menu.openMostRecentPair.setEnabled(false);
					// ... and enable the close menu entry 
					menu.closeSource.setEnabled(true);

					break;
				}
				case 't':
				{
					//TODO:make a prefs for loading in db and use the prefs instead of hardcoding a value
					
					OntologyDefinition odef = new OntologyDefinition(true, 
							prefs.getRecentTargetFileName(position), 
							OntologyLanguage.getLanguage(prefs.getRecentTargetLanguage(position)),
							OntologySyntax.getSyntax(prefs.getRecentTargetSyntax(position)));
					
					odef.onDiskStorage = prefs.getRecentTargetOnDisk(position);
					odef.onDiskDirectory = prefs.getRecentTargetOnDiskDirectory(position);
					odef.onDiskPersistent = prefs.getRecentTargetOnDiskPersistent(position);
					
					Ontology ont = menu.ui.openFile( odef );
					
					if( ont == null ) return;
					prefs.saveRecentFile(prefs.getRecentTargetFileName(position), 
							GlobalStaticVariables.TARGETNODE, 
							prefs.getRecentTargetSyntax(position), 
							prefs.getRecentTargetLanguage(position), 
							prefs.getRecentTargetSkipNamespace(position), 
							prefs.getRecentTargetNoReasoner(position),
							prefs.getRecentTargetOnDisk(position),
							prefs.getRecentTargetOnDiskDirectory(position),
							prefs.getRecentTargetOnDiskPersistent(position));
					refreshRecentMenus(); // after we update the recent files, refresh the contents of the recent menus.

					// Now that we have loaded a source ontology, disable all the source ontology loading menu entries ...
					if( Core.getInstance().ontologiesLoaded() ) menu.openFiles.setEnabled(false);
					else menu.openFiles.setEnabled(true);
					if( Core.getInstance().sourceIsLoaded() || Core.getInstance().targetIsLoaded() ) menu.closeBoth.setEnabled(true);
					menu.menuRecentTarget.setEnabled(false);
					menu.openMostRecentPair.setEnabled(false);
					// ... and enable the close menu entry 
					menu.closeTarget.setEnabled(true);


					break;
				}
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
	 * This method reads the XML or OWL files and creates trees for mapping
	 */	
	public void openAndReadFilesForMapping(int fileType){
		new OpenOntologyFileDialogCombined(menu.ui);
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
			sourceTitleString = sourceO.getTitle() + "\n";
		}
		if(targetO != null) {
			targetClassString = targetO.getClassDetails();
			targetPropString = targetO.getPropDetails();
			targetTitleString = targetO.getTitle() + "\n";
		}
		String report = new String();

		report+= "Source Ontology:\t" + sourceTitleString;
		report+= "Target Ontology:\t" + targetTitleString;
		report+= "\n";
		report+= "Hierarchies             \t#concepts\tdepth\tUC-diameter\tLC-diameter\t#roots\t#leaves\n";
		report+= "Source Classes:\t"+sourceClassString;
		report+= "Source Properties:\t"+sourcePropString;
		report+= "\n";
		report+= "Target Classes:\t"+targetClassString;
		report+= "Target Properties:\t"+targetPropString;

		report += "\n\nOntology Metrics:\n\n";

		for( OntologyMetricsRegistry currentOntMetric :  OntologyMetricsRegistry.values() ) {
			Constructor<?>[] constructors = currentOntMetric.getMetricClass().getConstructors();
			report += currentOntMetric.getMetricName() + "\n\n";
			for( Constructor<?> constructor : constructors ) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if( parameterTypes.length == 1 && parameterTypes[0].equals(Ontology.class) ) {
					// we found the right constructor, run this metric
					try {
						{
							OntologyMetric sourceMetric = (OntologyMetric) constructor.newInstance(sourceO);
							sourceMetric.runMetric();
							List<AvgMinMaxNumber> result = sourceMetric.getResult();
							if( result.size() != 0 ) report += "Source Ontology: \n";
							for( AvgMinMaxNumber num : result ) {
								report += num.toString() + "\n";
							}
						}
						//report += "\n";
						{
							OntologyMetric targetMetric = (OntologyMetric) constructor.newInstance(targetO);
							targetMetric.runMetric();
							List<AvgMinMaxNumber> result = targetMetric.getResult();
							if( result.size() != 0 ) report += "Target Ontology: \n";
							for( AvgMinMaxNumber num : result ) {
								report += num.toString() + "\n";
							}
						}

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
				report += "\n";
			}
		}

		Utility.displayTextAreaWithDim(report,"Ontology Details", 12, 60);
	}

	public void displayOptionPane(String desc, String title){
		JOptionPane.showMessageDialog(null,desc,title, JOptionPane.PLAIN_MESSAGE);					
	}
	
	public void refreshRecentMenus() {
		refreshRecentMenus( menu.menuRecentSource, menu.menuRecentTarget);
	}
	
	/**
	 * This function will update the Recent File Menus with the most up to date recent files
	 * @param recentsource
	 * @param recenttarget
	 */
	public void refreshRecentMenus( JMenu recentsource, JMenu recenttarget ) {
		
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
			menu.menuRecentSource.add(menuitem);
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
			menu.menuRecentTarget.add(menuitem);
		}
		
	}
}
