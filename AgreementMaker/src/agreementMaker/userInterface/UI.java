package agreementMaker.userInterface;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import agreementMaker.GSM;
import agreementMaker.development.OntologyController;


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
	private OntologyController ontologyController;
	
	private JFrame frame;
	// variables to store the global and local filenames
	private String globalFileName, localFileName;
	
	
	private JPanel panelCanvas, panelDesc;
	private ControlPanel panelControlPanel;
	private JScrollPane scrollPane;
	
	private JSplitPane splitPane;
	private UIMenu uiMenu;
	
	/**	 * Default constructor for UI class
	 */
	public UI()
	{
		init();
	}
	
	/**
	 * @param ontoType
	 * @param langIndex
	 * @param syntaxIndex
	 */
	public void buildOntology(int ontoType, int langIndex, int syntaxIndex) {
		canvas.buildOntology(ontoType, langIndex, syntaxIndex);
	}
	/** 
	 * This method disables opening local and global files
	 */
	public void disableLoadFiles()
	{
		//TODO: Decide what to put in here.....
	}
	
	/**
	 * @return
	 */
	public Canvas getCanvas(){
		return this.canvas;
	}
	
	/**
	 * @return the ontologyController, a class containing some methods to work with canvas and ontologies
	 */
	public OntologyController getOntologyController(){
		return this.ontologyController;
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
	/**
	 * @param ontoType
	 * @return
	 */
	public String getOntoFileName(int ontoType){
		if(ontoType == GSM.SOURCENODE)
			return globalFileName;
		else if(ontoType == GSM.TARGETNODE)
			return localFileName;
		else
			return null;
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
		

		// Create a swing frame
		frame = new JFrame("Agreement Maker");
		frame.getContentPane().setLayout(new BorderLayout());	
		
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
		//Added by Flavio: this class is needed to modularize the big canvas class, basically it contains some methods which could be in canvas class which works with the ontologies
	    ontologyController = new OntologyController(canvas);
		
	    //panelDesc = new VertexDescriptionPane(this); 
		//TODO: Add tabbed panes here for displaying the properties and descriptions		
		scrollPane = new JScrollPane(panelCanvas);
		scrollPane.setWheelScrollingEnabled(true);
		//scrollPane.setPreferredSize(new Dimension((int)scrollPane.getSize().getHeight(), 5));
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1.0);
		splitPane.setMinimumSize(new Dimension(640,480));
		splitPane.setPreferredSize(new Dimension(640,480));
		splitPane.getLeftComponent().setPreferredSize(new Dimension(640,480));
		// add scrollpane to the panel and add the panel to the frame's content pane
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		
		panelControlPanel = new ControlPanel(uiMenu, canvas);
		frame.getContentPane().add(panelControlPanel, BorderLayout.PAGE_END);		
		
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

	/**
	 * @param name
	 * @param ontoType
	 */
	public void setOntoFileName(String name, int ontoType){
		if(ontoType == GSM.SOURCENODE)
			globalFileName = name;
		else if(ontoType == GSM.TARGETNODE)
			localFileName = name;
	}

	public ControlPanel getPanelControlPanel() {
		return panelControlPanel;
	}

}

/**
 * Class to close the frame and exit the application
 */
class WindowEventHandler extends WindowAdapter
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
