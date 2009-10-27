package am.userInterface;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.TreeBuilder;
import am.userInterface.vertex.VertexDescriptionPane;


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
	
	private Canvas canvas;
	
	private JFrame frame;
	
	
	private JPanel panelCanvas, panelDesc;
	private MatchersControlPanel matcherControlPanel;
	private JScrollPane scrollPane;
	
	private JSplitPane splitPane;
	private UIMenu uiMenu;
	
	private JTabbedPane tabbedPane;
	
	/** Application Wide preferences, that are saved to a configuration file, and can be restored at any time. */
	private AppPreferences prefs;
	
	/**	 * Default constructor for UI class
	 */
	public UI()
	{
		init();
	}

	 
	
	/** Return the AppPreferences instance */
	public AppPreferences getAppPreferences() { return prefs; }
	
	/**
	 * @return canvas
	 */
	public Canvas getCanvas(){
		return this.canvas;
	}

	
	/**
	 * @return
	 */
	public JPanel getCanvasPanel(){
		return this.panelCanvas;
	}
	/**
	 * @return
	 */
	public JPanel getDescriptionPanel(){
		return this.panelDesc;
	}

	
	public UIMenu getUIMenu(){
		return this.uiMenu;
	}
	/**
	 * @return
	 */
	public JFrame getUIFrame(){
		return this.frame;
	}
	/**
	 * @return
	 */
	public JSplitPane getUISplitPane(){
		return this.splitPane;
	}
	/**     
	 * Init method
	 * This function creates menu, canvas, and checkboxes to be displayed on the screen
	 */
	public void init()
	{
		//Setting the Look and Feel of the application to that of Windows
		//try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); }
		//catch (Exception e) { System.out.println(e); }

		//	Setting the Look and Feel of the application to that of Windows
		//try { javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		//catch (Exception e) { System.out.println(e); }
		
		// initialize the application preferences
		prefs = new AppPreferences();
		

		// Create a swing frame
		frame = new JFrame("Agreement Maker");
		frame.getContentPane().setLayout(new BorderLayout());
		// TODO: Ask the user if he wants to exit the program.  But that might be annoying. (Or they might lose unsaved data!)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // if the user closes the window from the window manager, close the application.
		
		
		// created the tabbed pane.
		JPanel agreementMaker_classic = new JPanel();
		agreementMaker_classic.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		
		
		// Create the Menu Bar and Menu Items
		uiMenu = new UIMenu(this);	
		
		// create a new panel for the canvas 
		panelCanvas = new JPanel();
		
		// set the layout of the panel to be grid labyout of 1x1 grid
		panelCanvas.setLayout(new BorderLayout());
		
		// create a canvas class
		canvas = new Canvas(this);
		canvas.setFocusable(true);
		//canvas.setMinimumSize(new Dimension(0,0));
		//canvas.setPreferredSize(new Dimension(480,320));
		
		//add canvas to panel
		panelCanvas.add(canvas);

		
	    //panelDesc = new VertexDescriptionPane(this); 
		//TODO: Add tabbed panes here for displaying the properties and descriptions		
		scrollPane = new JScrollPane(panelCanvas);
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1.0);
		splitPane.setMinimumSize(new Dimension(640,480));
		splitPane.setPreferredSize(new Dimension(640,480));
		splitPane.getLeftComponent().setPreferredSize(new Dimension(640,480));
		// add scrollpane to the panel and add the panel to the frame's content pane
		
		agreementMaker_classic.add(splitPane, BorderLayout.CENTER);
		//frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		
		//panelControlPanel = new ControlPanel(this, uiMenu, canvas);
		matcherControlPanel = new MatchersControlPanel(this, uiMenu, canvas);
		agreementMaker_classic.add(matcherControlPanel, BorderLayout.PAGE_END);
		//frame.getContentPane().add(matcherControlPanel, BorderLayout.PAGE_END);
		
		
		tabbedPane.addTab("AgreementMaker", null, agreementMaker_classic, "AgreementMaker");
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		//Add the listener to close the frame.
		frame.addWindowListener(new WindowEventHandler());
		
		// set frame size (width = 1000 height = 700)
		//frame.setSize(900,600);
		frame.pack();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); // maximize the window
		
		
		// make sure the frame is visible
		frame.setVisible(true); 
	}

	/**
	 * @param jPanel
	 */
	public void setDescriptionPanel(JPanel jPanel){
		this.panelDesc = jPanel;
	}

	public MatchersControlPanel getControlPanel() {
		return matcherControlPanel;
	}

	/** This function will open a file
	 *  Attention syntax and language are placed differently from other functions.
	 * @param ontoType the type of ontology, source or target
	 * 
	 * */

	public void openFile( String filename, int ontoType, int syntax, int language, boolean skip, boolean noReasoner) {
		try{
			JPanel jPanel = null;
			System.out.println("opening file");
			if(language == GlobalStaticVariables.RDFSFILE)//RDFS
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.RDFSFILE);//takes care of fields for XML files as well
			else if(language == GlobalStaticVariables.ONTFILE)//OWL
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.ONTFILE);//takes care of fields for XML files as well
			else if(language == GlobalStaticVariables.XMLFILE)//XML
				jPanel = new VertexDescriptionPane(GlobalStaticVariables.XMLFILE);//takes care of fields for XML files as well 
		    jPanel.setMinimumSize(new Dimension(200,480));
			getUISplitPane().setRightComponent(jPanel);
			setDescriptionPanel(jPanel);
			//This function manage the whole process of loading, parsing the ontology and building data structures: Ontology to be set in the Core and Tree and to be set in the canvas
			TreeBuilder t = TreeBuilder.buildTreeBuilder(filename, ontoType, language, syntax, skip, noReasoner);
			
			//the treebuilder is initialized now we have to execute it in a separate thread.
			// The dialog will start the treebuilder in a background thread, 
			new OntologyLoadingProgressDialog(t);  // Program flow will not continue until the dialog is dismissed. (User presses Ok or Cancel)
			if(!t.isCancelled()) {
				//Set ontology in the Core
				Ontology ont = t.getOntology();
				if(ontoType == GlobalStaticVariables.SOURCENODE) {
					Core.getInstance().setSourceOntology(ont);
				}
				else Core.getInstance().setTargetOntology(ont);
				//Set the tree in the canvas
				System.out.println("Displaying the hierarchies in the canvas");
				getCanvas().setTree(t);
				if(Core.getInstance().ontologiesLoaded()) {
					//Ogni volta che ho caricato un ontologia e le ho entrambe, devo resettare o settare se � la prima volta, tutto lo schema dei matchings
					System.out.println("Init matchings table");
					matcherControlPanel.resetMatchings();
				}
				System.out.println("Ontologies loaded succesfully");
			}
		}catch(Exception ex){
			JOptionPane.showConfirmDialog(null,"Can not parse the file '" + filename + "'. Please check the policy.","Parser Error",JOptionPane.PLAIN_MESSAGE);
			ex.printStackTrace();
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
	
	public void redisplayCanvas() {
		canvas.repaint();
	}    
	
	
	/**
	 * Tabbed Interface
	 */

	public void addTab( String tabName, ImageIcon icon, JComponent panel, String toolTip ) {
		tabbedPane.addTab( tabName, icon, panel, toolTip);
		tabbedPane.setSelectedIndex( tabbedPane.getTabCount() - 1 );
	}
}
