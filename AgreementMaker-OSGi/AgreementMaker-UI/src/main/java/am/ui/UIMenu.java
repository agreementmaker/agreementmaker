package am.ui;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.ontology.Ontology;
import am.tools.LexiconLookup.LexiconLookupPanel;
import am.ui.sidebar.provenance.ProvenanceMenuItem;
import am.utility.AppPreferences;


public class UIMenu {

	// create Top Level menus
	JMenu fileMenu, editMenu, viewMenu, helpMenu, matchersMenu, toolsMenu, ontologyMenu;

	// File menu.
	JMenuItem xit, openFiles, openMostRecentPair,
	closeSource, closeTarget, closeBoth, saveAlignment, loadAlignment;
	JMenu menuRecentSource, menuRecentTarget;

	// Edit menu.
	JMenuItem itemFind;
	//private JMenuItem undo, redo;

	// View menu.
	JMenuItem colorsItem;
	ProvenanceMenuItem provenanceItem;
	JCheckBoxMenuItem 	smoMenuItem,  
	showLabelItem, 
	showLocalNameItem, 
	showMappingsShortname,
	synchronizedViews,
	disableVisualizationItem,
	duplicateView;
	JMenu menuViews;  // Views submenu.  TODO: Rename this to something more descriptive.
	JMenu menuLexicons; // the Lexicons sub menu;
	JMenuItem menuLexiconsViewOntSource, menuLexiconsViewOntTarget, menuLexiconsViewWNSource, menuLexiconsViewWNTarget, 
	menuLexiconsBuildOntSource, menuLexiconsBuildOntTarget, menuLexiconsBuildWNSource, menuLexiconsBuildWNTarget,
	menuLexiconsBuildAll;

	// Ontology menu.
	JMenuItem ontologyDetails, ontologyViewEntityList, ontologyProfiling, ontologyAlternateHierarchy;

	// Tools menu.
	JMenuItem wordnetLookupItem, sealsItem, clusteringEvaluation, instanceLookupItem;

	// Matchers menu.
	JMenuItem userFeedBack, newMatching, runMatching, copyMatching, deleteMatching, clearAll, 
	doRemoveDuplicates,
	refEvaluateMatching,
	thresholdAnalysis, TEMP_viewClassMatrix, TEMP_viewPropMatrix, TEMP_matcherAnalysisClasses, TEMP_matcherAnalysisProp,
	clusteringClasses;

	//private JMenu	menuExport;
	//private JMenuItem exportMatrixCSV;


	// Help menu.
	JMenuItem howToUse, aboutItem, mnuListBundles;		



	//creates a menu bar
	JMenuBar myMenuBar;

	UI ui;  // reference to the main ui.

	JMenuItem menuLexiconsClearAll;


	private UIMenuListener listener;

	public UIMenu(UI ui){
		this.ui=ui;
		this.listener = new UIMenuListener(this);
		init();

	}

	public void init(){

		// need AppPreferences for smoItem, to get if is checked or not.
		AppPreferences prefs = new AppPreferences();

		// building the file menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);	

		//add openFile menu item to file menu
		openFiles = createMenuItemWithIcon("Open Ontologies ...", "image/fileImage.png");
		openFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		//openSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openSource.setMnemonic(KeyEvent.VK_O);
		openFiles.addActionListener(listener);
		fileMenu.add(openFiles);

		//add openGFile menu item to file menu


		// add separator
		fileMenu.addSeparator();

		// Construct the recent files menu.
		menuRecentSource = new JMenu("Recent Sources");
		menuRecentSource.setMnemonic('u');

		menuRecentTarget = new JMenu("Recent Targets");
		menuRecentTarget.setMnemonic('a');


		listener.refreshRecentMenus(menuRecentSource, menuRecentTarget);

		fileMenu.add(menuRecentSource);
		fileMenu.add(menuRecentTarget);
		openMostRecentPair = new JMenuItem("Open most recent pair");
		openMostRecentPair.addActionListener(listener);
		openMostRecentPair.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + InputEvent.SHIFT_DOWN_MASK)); 
		fileMenu.add(openMostRecentPair);
		fileMenu.addSeparator();
		//private JMenuItem menuRecentSource, menuRecentTarget;
		//private JMenuItem menuRecentSourceList[], menuRecentTargetList[]; // the list of recent files
		closeSource = new JMenuItem("Close Source Ontology");
		closeSource.addActionListener(listener);
		closeSource.setEnabled(false); // there is no source ontology loaded at the beginning
		closeTarget = new JMenuItem("Close Target Ontology");
		closeTarget.addActionListener(listener);
		closeTarget.setEnabled(false); // there is no target ontology loaded at the beginning
		closeBoth = new JMenuItem("Close both ontologies");
		closeBoth.addActionListener(listener);
		closeBoth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		closeBoth.setEnabled(false);

		fileMenu.add(closeSource);
		fileMenu.add(closeTarget);
		fileMenu.add(closeBoth);

		fileMenu.addSeparator();
		saveAlignment = new JMenuItem("Save Selected Alignment ...");
		saveAlignment.addActionListener(listener);
		saveAlignment.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		fileMenu.add(saveAlignment);

		loadAlignment = new JMenuItem("Load Alignment ...");
		loadAlignment.addActionListener(listener);
		loadAlignment.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		fileMenu.add(loadAlignment);


		fileMenu.addSeparator();
		// add exit menu item to file menu
		xit = new JMenuItem("Exit", KeyEvent.VK_X);
		xit.addActionListener(listener);
		xit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileMenu.add(xit);


		// build the Edit menu
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);

		itemFind = new JMenuItem("Find", KeyEvent.VK_F);
		itemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		itemFind.addActionListener(listener);
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
		colorsItem.addActionListener(listener);
		viewMenu.add(colorsItem);

		provenanceItem=new ProvenanceMenuItem("Show Provenance");//,KeyEvent.VK_P);
		provenanceItem.setEnabled(false);
		Core.getInstance().addMatcherChangeListener(provenanceItem);
		provenanceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		provenanceItem.addActionListener(listener);
		viewMenu.add(provenanceItem);

		viewMenu.addSeparator();

		// add "Disable Visualization" option to the view menu
		disableVisualizationItem = new JCheckBoxMenuItem("Disable hierarchies visualization");
		disableVisualizationItem.addActionListener(listener);
		disableVisualizationItem.setSelected(prefs.getDisableVisualization());
		// viewMenu.add(disableVisualizationItem);

		// add "Selected Matchings Only" option to the view menu
		smoMenuItem = new JCheckBoxMenuItem("Selected Matchings Only");
		smoMenuItem.addActionListener(listener);
		smoMenuItem.setSelected(prefs.getSelectedMatchingsOnly());
		//viewMenu.add(smoMenuItem);

		showLocalNameItem = new JCheckBoxMenuItem("Show localnames");
		showLocalNameItem.addActionListener(listener);
		showLocalNameItem.setSelected(prefs.getShowLocalname());
		viewMenu.add(showLocalNameItem);

		showLabelItem = new JCheckBoxMenuItem("Show labels");
		showLabelItem.addActionListener(listener);
		showLabelItem.setSelected(prefs.getShowLabel());
		viewMenu.add(showLabelItem);

		showMappingsShortname = new JCheckBoxMenuItem("Mappings with Matcher name");
		showMappingsShortname.addActionListener(listener);
		showMappingsShortname.setSelected(prefs.getShowMappingsShortname());
		viewMenu.add(showMappingsShortname);
		//viewMenu.addSeparator();

		synchronizedViews = new JCheckBoxMenuItem("Synchronized Views");
		synchronizedViews.addActionListener(listener);
		synchronizedViews.setSelected(prefs.getSynchronizedViews());
		viewMenu.add(synchronizedViews);

		
		duplicateView = new JCheckBoxMenuItem("Duplicate View");
		duplicateView.addActionListener(listener);
		//duplicateView.setSelected(prefs.getSynchronizedViews());
		viewMenu.add(duplicateView);
		
		
		menuViews = new JMenu("New view");
		//menuViews.add(itemViewsCanvas2);
		//viewMenu.add(menuViews);

		// Lexicons menu.
		menuLexicons = new JMenu("Lexicons");
		menuLexicons.setMnemonic('L');

		menuLexiconsBuildAll = new JMenuItem("Build all ...");
		menuLexiconsBuildAll.addActionListener(listener);

		menuLexiconsClearAll = new JMenuItem("Delete all ...");
		menuLexiconsClearAll.addActionListener(listener);

		JMenu menuLexiconsBuild = new JMenu("Build");

		menuLexiconsBuildOntSource = new JMenuItem("Ontology Lexicon: Source ...");
		menuLexiconsBuildOntSource.addActionListener(listener);
		menuLexiconsBuildOntTarget = new JMenuItem("Ontology Lexicon: Target ...");
		menuLexiconsBuildOntTarget.addActionListener(listener);
		menuLexiconsBuildWNSource = new JMenuItem("WordNet Lexicon: Source ...");
		menuLexiconsBuildWNSource.addActionListener(listener);
		menuLexiconsBuildWNTarget = new JMenuItem("WordNet Lexicon: Target ...");
		menuLexiconsBuildWNTarget.addActionListener(listener);

		menuLexiconsBuild.add(menuLexiconsBuildOntSource);
		menuLexiconsBuild.add(menuLexiconsBuildOntTarget);
		menuLexiconsBuild.addSeparator();
		menuLexiconsBuild.add(menuLexiconsBuildWNSource);
		menuLexiconsBuild.add(menuLexiconsBuildWNTarget);


		JMenu menuLexiconsView = new JMenu("View");

		menuLexiconsViewOntSource = new JMenuItem("Ontology Lexicon: Source");
		menuLexiconsViewOntSource.addActionListener(listener);
		menuLexiconsViewOntTarget = new JMenuItem("Ontology Lexicon: Target");
		menuLexiconsViewOntTarget.addActionListener(listener);
		menuLexiconsViewWNSource = new JMenuItem("WordNet Lexicon: Source");
		menuLexiconsViewWNSource.addActionListener(listener);
		menuLexiconsViewWNTarget = new JMenuItem("WordNet Lexicon: Target");
		menuLexiconsViewWNTarget.addActionListener(listener);

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
		ontologyDetails.addActionListener(listener); 
		ontologyMenu.add(ontologyDetails);

		ontologyViewEntityList = new JMenuItem("View entity list");
		ontologyViewEntityList.addActionListener(listener);
		ontologyMenu.add(ontologyViewEntityList);

		ontologyMenu.addSeparator();

		ontologyProfiling = new JMenuItem("Profiling ...");
		ontologyProfiling.addActionListener(listener);
		ontologyMenu.add(ontologyProfiling);

		ontologyMenu.addSeparator();

		ontologyAlternateHierarchy = new JMenuItem("View alternate hierachy...");
		ontologyAlternateHierarchy.addActionListener(listener);
		ontologyMenu.add(ontologyAlternateHierarchy);


		// **************** Matchers Menu *******************
		matchersMenu = new JMenu("Matchers");
		matchersMenu.setMnemonic('M');

		runMatching = new JMenuItem("Run matcher ...");
		runMatching.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())); 
		runMatching.addActionListener(listener);
		matchersMenu.add(runMatching);

		matchersMenu.addSeparator();


		newMatching = new JMenuItem("New empty matcher");
		newMatching.addActionListener(listener);
		matchersMenu.add(newMatching);

		copyMatching = new JMenuItem("Copy selected matchings");
		copyMatching.addActionListener(listener);
		matchersMenu.add(copyMatching);
		deleteMatching = new JMenuItem("Delete selected matchings");
		deleteMatching.addActionListener(listener);
		matchersMenu.add(deleteMatching);
		clearAll = new JMenuItem("Clear All");
		clearAll.addActionListener(listener);
		matchersMenu.add(clearAll);
		matchersMenu.addSeparator();

		doRemoveDuplicates = new JMenuItem("Remove Duplicate Alignments");
		doRemoveDuplicates.addActionListener(listener);
		//matchingMenu.add(doRemoveDuplicates);
		//matchingMenu.addSeparator();

		//saveMatching = new JMenuItem("Save selected matchers into a file");
		//saveMatching.addActionListener(this);
		//matchersMenu.add(saveMatching);
		//matchersMenu.addSeparator();
		refEvaluateMatching = new JMenuItem("Evaluate with reference file");
		refEvaluateMatching.addActionListener(listener);
		refEvaluateMatching.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		matchersMenu.add(refEvaluateMatching);

		thresholdAnalysis = new JMenuItem("Threshold Analysis");
		thresholdAnalysis.addActionListener(listener);
		matchersMenu.add(thresholdAnalysis);

		TEMP_matcherAnalysisClasses = new JMenuItem("Matcher Analysis: Classes");
		TEMP_matcherAnalysisClasses.addActionListener(listener);

		TEMP_matcherAnalysisProp = new JMenuItem("Matcher Analysis: Properties");
		TEMP_matcherAnalysisProp.addActionListener(listener);

		matchersMenu.addSeparator();
		matchersMenu.add(TEMP_matcherAnalysisClasses);
		matchersMenu.add(TEMP_matcherAnalysisProp);

		userFeedBack = new JMenuItem("User Feedback Loop");
		userFeedBack.addActionListener(listener);
		matchersMenu.addSeparator();
		matchersMenu.add(userFeedBack);

		matchersMenu.addSeparator();

		clusteringClasses = new JMenuItem("Show Classes Clustering");
		clusteringClasses.addActionListener(listener);
		matchersMenu.add(clusteringClasses);

		// *************************** TOOLS MENU ****************************
		toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('T');

		// Tools -> Wordnet lookup panel...
		wordnetLookupItem = new JMenuItem("Wordnet Lookup ...");
		wordnetLookupItem.setMnemonic(KeyEvent.VK_W);
		wordnetLookupItem.addActionListener(listener);
		toolsMenu.add(wordnetLookupItem);

		// Tools -> SEALS Interface...
		sealsItem = new JMenuItem("SEALS Interface...");
		sealsItem.setMnemonic(KeyEvent.VK_S);
		sealsItem.addActionListener(listener);
		toolsMenu.add(sealsItem);


		// Tools -> Instance Lookup Panel...
		instanceLookupItem = new JMenuItem("Instance Lookup Panel...");
		instanceLookupItem.setMnemonic(KeyEvent.VK_I);
		instanceLookupItem.addActionListener(listener);
		toolsMenu.add(instanceLookupItem);

		// Build help menu in the menu bar.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);


		// add menu item to help menu
		howToUse = createMenuItemWithIcon("Help", "image/helpImage.gif");
		howToUse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                	
		howToUse.setMnemonic(KeyEvent.VK_H);
		howToUse.addActionListener(listener);
		helpMenu.add(howToUse);

		// add about item to help menu
		aboutItem = createMenuItemWithIcon("About AgreementMaker", "image/aboutImage.gif");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		//aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));                
		aboutItem.addActionListener(listener);
		helpMenu.add(aboutItem);

		mnuListBundles = new JMenuItem("Show Bundles");
		mnuListBundles.addActionListener(listener);
		helpMenu.add(mnuListBundles);


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
	 * Create a menu item that has an icon.  The icon should be on the class path.
	 * @param label The label for this menu item.
	 * @param iconPath The path on the classpath of the icon.
	 */
	public JMenuItem createMenuItemWithIcon(String label, String iconPath) {
		final URL iconURL = ClassLoader.getSystemClassLoader().getResource(iconPath);
		
		
		if( iconURL == null ) { // icon was not found on the classpath
			return new JMenuItem(label);
		}
		else {
			return new JMenuItem(label, new ImageIcon(iconURL));
		}
	}

	public JMenu getMenu(String name) {
		if( name.equals("Tools") ) {
			return toolsMenu;
		}

		return null;
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
}
