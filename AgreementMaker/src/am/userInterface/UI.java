package am.userInterface;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.TreeBuilder;
import am.userInterface.classic.AgreementMakerClassic;
import am.userInterface.sidebar.vertex.VertexDescriptionPane;


/**
 * UI Class - 
 *
 * This class is responsible for creating the menu bar, displaying the canvas,  
 * the buttons, and checkboxes at botton of the screen.
 *
 * @author ADVIS Research Laboratory
 * @version 12/5/2004
 */
public class UI {
	
	static final long serialVersionUID = 1;
	
	// this is the current UI Panel
	private AgreementMakerClassic classicAM;
	
	private JFrame frame;
	
	private JPanel panelDesc;  // This variable is initialized by UI.openFile().  It instantiates a VertexDescriptionPane

	//private JScrollPane scrollPane;
	
	private UIMenu uiMenu;
	
	private JTabbedPane tabbedPane;
	
	/** Application Wide preferences, that are saved to a configuration file, and can be restored at any time. */
	//private AppPreferences prefs;
	
	/**	 * Default constructor for UI class
	 */
	public UI()
	{
		init();
		
	}

	 
	
	/** Return the AppPreferences instance */
	//public AppPreferences getAppPreferences() { return prefs; }
	
	/**
	 * @return canvas
	 */
	public VisualizationPanel getCanvas(){
		return classicAM.getVisualizationPanel();
	}

	public UIMenu getUIMenu(){ return this.uiMenu; }
	public JFrame getUIFrame(){ return this.frame; }
	
	// TODO: getUISplitPane shouldn't be part of the UI
	//@Deprecated
	public JSplitPane getUISplitPane(){ return classicAM.getSplitPane(); }
	
	/**     
	 * Init method
	 * This function creates menu, canvas, and checkboxes to be displayed on the screen
	 */
	private void init()
	{
		//Setting the Look and Feel of the application to that of Windows
		//try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); }
		//catch (Exception e) { System.out.println(e); }
			
		//	Setting the Look and Feel of the application to that of Windows
		//try { javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		//catch (Exception e) { System.out.println(e); }
		
		// Create a swing frame
		frame = new JFrame("AgreementMaker");
		frame.getContentPane().setLayout(new BorderLayout());
		
		// TODO: Ask the user if he wants to exit the program.  But that might be annoying. (Or they might lose unsaved data!)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // if the user closes the window from the window manager, close the application.
		
		
		// created the tabbed pane.
		tabbedPane = new JTabbedPane();
		
		
		// Create the Menu Bar and Menu Items
		uiMenu = new UIMenu(this);	
		
		// The first view is the Classic AgreementMaker
		classicAM = new AgreementMakerClassic();
		
		tabbedPane.addTab("AgreementMaker", null, classicAM, "AgreementMaker");
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		//Add the listener to close the frame.
		frame.addWindowListener(new WindowEventHandler());
		
		// set frame size (width = 1000 height = 700)
		//frame.setSize(900,600);
		frame.pack();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); // maximize the window

		
		
		//Dimension size = frame.getSize();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		int percent = (7 * size.height) / 20 ;
		int location = size.height - percent;
		classicAM.getOuterSplitPane().setDividerLocation(location);
		
		// make sure the frame is visible
		frame.setVisible(true); 
	}

	/**
	 * setDescriptionPanel is used in UI.openFile().
	 * @return
	 */
	@Deprecated
	public JPanel getDescriptionPanel(){ return this.panelDesc; }
	
	/**
	 * Used in UI.openFile()
	 * @param jPanel
	 */
	@Deprecated
	public void setDescriptionPanel(JPanel jPanel){ this.panelDesc = jPanel; }

	/** 
	 * This function will open an ontology given a file.
	 * Attention syntax and language are placed differently from other functions.
	 * 
	 * TODO: Find a better way to pass in all the parameters.
	 *  
	 * @param filename The full path to the ontology file.
	 * @param ontoType Type of ontology: GlobalStaticVariables.SOURCENODE (source) or GlobalStaticVariables.TARGETNODE (target)
	 * @param syntax The ontology syntax: GlobalStaticVariables.{SYNTAX_RDFXML, SYNTAX_RDFXMLABBREV, SYNTAX_N3, SYNTAX_NTRIPLE, SYNTAX_TURTLE}
	 * @param language The ontology language: GlobalStaticVariables.{LANG_OWL, LANG_RDFS, LANG_XML, LANG_TABBEDTEXT}    
	 * @param skip Skip concepts with different namespace?
	 * @param noReasoner Don't use a reasoner?
	 * @param onDisk Load using Jena TDB, into a directory?
	 * @param onDiskDirectory The directory for Jena TDB.
	 * @param onDiskPersistent Is the Jena TDB ontology persistent?
	 * @return Return true on successful loading of the ontology, false otherwise.
	 * 
	 * */
	public boolean openFile( String filename, int ontoType, int syntax, int language, boolean skip, boolean noReasoner, boolean onDisk, String onDiskDirectory, boolean onDiskPersistent) {
		try{
			JPanel jPanel = null;
			System.out.println("opening file");
			if(language == GlobalStaticVariables.RDFSFILE)//RDFS
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.RDFSFILE);//takes care of fields for XML files as well
			else if(language == GlobalStaticVariables.OWLFILE)//OWL
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.OWLFILE);//takes care of fields for XML files as well
			else if(language == GlobalStaticVariables.XMLFILE)//XML
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.XMLFILE);//takes care of fields for XML files as well
			else if(language == GlobalStaticVariables.TABBEDTEXT)
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.XMLFILE); // TODO: Figure out if we need to pass in the correct language type to VertexDescriptionPane constructor.
		    jPanel.setMinimumSize(new Dimension(200,200));
			getUISplitPane().setRightComponent(jPanel);
			setDescriptionPanel(jPanel);
			System.out.println("Before treebuilder.buildTreeBuilder in am.userinterface.ui.openFile()...");
			//This function manage the whole process of loading, parsing the ontology and building data structures: Ontology to be set in the Core and Tree and to be set in the canvas
			TreeBuilder t = TreeBuilder.buildTreeBuilder(filename, ontoType, language, syntax, skip, noReasoner, onDisk, onDiskDirectory, onDiskPersistent);
			//System.out.println("after treebuilder.buildTreeBuilder before progress dialog treebuilder.buildTreeBuilder in am.userinterface.ui.openFile()...");
			//the treebuilder is initialized now we have to execute it in a separate thread.
			// The dialog will start the treebuilder in a background thread, 
			new OntologyLoadingProgressDialog(t);  // Program flow will not continue until the dialog is dismissed. (User presses Ok or Cancel)
			if(!t.isCancelled()) {
				//System.out.println("after t.isCancelled before Core.getInstancein am.userinterface.ui.openFile()...");
				
				//Set ontology in the Core
				Ontology ont = t.getOntology();
				if(ontoType == GlobalStaticVariables.SOURCENODE) {
					Core.getInstance().setSourceOntology(ont);
				}
				else Core.getInstance().setTargetOntology(ont);
				//System.out.println("after after Core.getInstancein am.userinterface.ui.openFile()...");
				//Set the tree in the canvas
				if( Core.DEBUG ) System.out.println("Displaying the hierarchies in the canvas");
				ont.setDeepRoot(t.getTreeRoot());
				ont.setTreeCount(t.getTreeCount());
				getCanvas().setTree(t);  // legacy calls?
				if(Core.getInstance().ontologiesLoaded()) {
					//Ogni volta che ho caricato un ontologia e le ho entrambe, devo resettare o settare se � la prima volta, tutto lo schema dei matchings
					if( Core.DEBUG ) System.out.println("Init matchings table");
					classicAM.getMatchersControlPanel().resetMatchings();
					
				}
				if( Core.DEBUG ) System.out.println("Ontologies loaded succesfully");
				return true;
			}
			return false;
		}catch(Exception ex){
			JOptionPane.showConfirmDialog(getUIFrame(),"Can not parse the file '" + filename + "'. Please check the policy.","Parser Error\n\n" + ex.getMessage(),JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Class to close the frame and exit the application
	 */
	public class WindowEventHandler extends WindowAdapter
	{
		/**
		 * Function which closes the window
		 * @param e WindowEvent Object
		 */
		public void windowClosing(WindowEvent e)
	{
		e.getWindow().dispose();
		//System.exit(0);   
	}
	}
	
	public void redisplayCanvas() {	classicAM.getVisualizationPanel().repaint(); }    
	
	/**
	 * Adds a tab to the main AgreementMaker window and selects it.
	 * @param tabName The displayed name of the tab.
	 * @param icon An icon shown in the tab before the name.
	 * @param panel The panel that the tab will contain.
	 * @param toolTip The tooltip text for this tab.
	 */
	public void addTab( String tabName, ImageIcon icon, JComponent panel, String toolTip ) {
		tabbedPane.addTab( tabName, icon, panel, toolTip);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1 , new ButtonTabComponent(tabbedPane));
		tabbedPane.setSelectedIndex( tabbedPane.getTabCount() - 1 );
	}
	
	public void addTab( String tabName, ImageIcon icon, JComponent panel, String toolTip, Runnable callOnClose ) {
		tabbedPane.addTab( tabName, icon, panel, toolTip);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1 , new ButtonTabComponent(tabbedPane, callOnClose));
		tabbedPane.setSelectedIndex( tabbedPane.getTabCount() - 1 );
	}
	
	/** 
	 * Returns the currently selected tab from the main AgreementMaker window.
	 * @return Currently selected tab.
	 */
	public Component getCurrentTab() { return tabbedPane.getSelectedComponent(); }
	
	/**
	 * @return The main tabbed pane of the UI.
	 */
	public JTabbedPane getTabbedPane() { return tabbedPane; }
	
	@Deprecated
	public JViewport getViewport() { // don't need this, it should be passed on the constructor of the VisualzationPanel
		return classicAM.getScrollPane().getViewport();
	}

	public MatchersControlPanel getControlPanel() {	return classicAM.getMatchersControlPanel();	}
	
}
