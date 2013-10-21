package am.ui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.TreeBuilder;
import am.ui.classic.AgreementMakerClassic;
import am.ui.controlpanel.MatchersControlPanel;
import am.ui.sidebar.vertex.VertexDescriptionPane;
import am.ui.table.MatchersControlPanelTableModel;


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

	private final Logger log = Logger.getLogger(UI.class);
	
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

		// created the tabbed pane.
		tabbedPane = new JTabbedPane();


		// Create the Menu Bar and Menu Items
		uiMenu = new UIMenu(this);	

		// The first view is the Classic AgreementMaker
		classicAM = new AgreementMakerClassic();

		tabbedPane.addTab("AgreementMaker", null, classicAM, "AgreementMaker");
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// confirmExit if the user is trying to close the window.
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowClosing(WindowEvent e) {
				UI.this.confirmExit();
			}
			@Override public void windowClosed(WindowEvent e) {}
			@Override public void windowActivated(WindowEvent e) {}
		});

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

	public Ontology openFile( OntologyDefinition odef ) {
		try{
			JPanel jPanel = null;

			log.info("Opening file: " + odef.ontologyURI );

			if(odef.ontologyLanguage == OntologyLanguage.RDFS)//RDFS
				jPanel = new VertexDescriptionPane(Ontology.RDFSFILE);//takes care of fields for XML files as well
			else if(odef.ontologyLanguage == OntologyLanguage.OWL)//OWL
				jPanel = new VertexDescriptionPane(Ontology.OWLFILE);//takes care of fields for XML files as well
			else if(odef.ontologyLanguage == OntologyLanguage.XML)//XML
				jPanel = new VertexDescriptionPane(Ontology.XMLFILE);//takes care of fields for XML files as well
			else if(odef.ontologyLanguage == OntologyLanguage.TABBEDTEXT)
				jPanel = new VertexDescriptionPane(Ontology.XMLFILE); // TODO: Figure out if we need to pass in the correct language type to VertexDescriptionPane constructor.
			jPanel.setMinimumSize(new Dimension(200,200));
			getUISplitPane().setRightComponent(jPanel);
			setDescriptionPanel(jPanel);
			//System.out.println("Before treebuilder.buildTreeBuilder in am.userinterface.ui.openFile()...");
			//This function manage the whole process of loading, parsing the ontology and building data structures: Ontology to be set in the Core and Tree and to be set in the canvas
			TreeBuilder t = TreeBuilder.buildTreeBuilder(odef);
			//System.out.println("after treebuilder.buildTreeBuilder before progress dialog treebuilder.buildTreeBuilder in am.userinterface.ui.openFile()...");
			//the treebuilder is initialized now we have to execute it in a separate thread.
			// The dialog will start the treebuilder in a background thread, 
			new OntologyLoadingProgressDialog(t);  // Program flow will not continue until the dialog is dismissed. (User presses Ok or Cancel)
			if(!t.isCancelled()) {
				//System.out.println("after t.isCancelled before Core.getInstancein am.userinterface.ui.openFile()...");

				//Set ontology in the Core
				Ontology ont = t.getOntology();
				log.debug("Displaying the hierarchies in the canvas.");
				if(Core.getInstance().ontologiesLoaded()) {
					//Ogni volta che ho caricato un ontologia e le ho entrambe, devo resettare o settare se � la prima volta, tutto lo schema dei matchings
					//Every time I loaded an ontology and I have both, I have to reset or set if it's the first time, all the matching schemas - Translation by Federico
					log.debug("Init matchings table");
					classicAM.getMatchersControlPanel().resetMatchings();

				}
				log.debug("Ontologies loaded succesfully.");
				return ont;
			}
			return null;
		}catch(Exception ex){
			JOptionPane.showConfirmDialog(getUIFrame(),"Can not parse the file '" + odef.ontologyURI + "'. Please check the policy.","Parser Error\n\n" + ex.getMessage(),JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return null;
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

	/**
	 * @return A list of MatcherResults which are currently selected in the user interface.
	 */
	public List<MatchingTask> getSelectedTasks() {
		List<MatchingTask> selectedResults = new LinkedList<MatchingTask>();

		int[] rowsIndex = getControlPanel().getTablePanel().getTable().getSelectedRows();
		List<MatchingTask> allResults = 
				((MatchersControlPanelTableModel)getControlPanel().getTablePanel().getTable().getModel()).getData();

		for( int index : rowsIndex ) {
			selectedResults.add(allResults.get(index));
		}

		return selectedResults;
	}

	/**
	 * Function that is called when to user wants to close the program. 
	 */
	public void confirmExit() {
		int n = JOptionPane.showConfirmDialog(UICore.getUI().getUIFrame(),"Are you sure you want to exit ?\n\nYou will lose any unsaved alignments!\n","Exit AgreementMaker",JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			UICore.getUI().getUIFrame().setVisible(false);
			UICore.getUI().getUIFrame().dispose();
			UICore.setUI(null);
			Core.getInstance().getRegistry().initializeShutdown();
		}
	}
}
