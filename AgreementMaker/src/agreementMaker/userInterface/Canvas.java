package agreementMaker.userInterface;





import java.io.*;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import agreementMaker.GSM;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.ContextMapping;
import agreementMaker.application.mappingEngine.DefComparator;
import agreementMaker.application.mappingEngine.DefnMapping;
import agreementMaker.application.mappingEngine.DefnMappingOptions;
import agreementMaker.application.mappingEngine.UserMapping;
import agreementMaker.application.ontology.Ontology;
import agreementMaker.application.ontology.ontologyParser.OntoTreeBuilder;
import agreementMaker.application.ontology.ontologyParser.RdfsTreeBuilder;
import agreementMaker.application.ontology.ontologyParser.TreeBuilder;
import agreementMaker.application.ontology.ontologyParser.XmlTreeBuilder;
import agreementMaker.userInterface.vertex.Vertex;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;
import java.util.Date;




/**
 * Canvas class is responsible for all the contents inside the canvas such as
 * displaying the tree, connecting the lines, mappingByUser nodes, highlighting the nodes.
 * This class implements mouse listener for mouse events such as mouse clicks,
 * and also implements action listener for menu items.
 *
 * @author ADVIS Laboratory
 * @version 12/5/2004
 */
public class Canvas extends JPanel implements MouseListener, ActionListener
{
	
	Date start = new Date();
	Date end = new Date();

	FileOutputStream out; // declare a file output object
	FileOutputStream out2; // declare a file output object
	PrintStream p; // declare a print stream object
	PrintStream p2; // declare a print stream object
	
	private JMenuItem 	cancel;               	// cancel the mappingByUser
	private JMenuItem 	cancelPopup;				// cancel the right click pop up
	private double 		canvasHeight;				// height of the canvas
	private double 		canvasWidth;				// width of the canvas
	private JMenu 		comparative;					// comparitive menu 
	private JMenuItem 	comparativeExact;		// comparativeExact mappingByUser
	private JMenuItem 	comparativeSubset;		// comparativeSubset mappingByUser
	private JMenuItem 	comparativeSuperset;		// comparativeSuperset mappingByUser
	private int 				countStat = 0;
	private JMenuItem 	desc;						// desc JMenuItem
	private JMenuItem 	exact;               		// exact mappingByUser
	private Vector 		globalNodesSelected;			// the global nodes which are selected
	private Vertex 		globalTreeRoot;				// root of global tree
	private Vector 		localNodesSelected;      	// the local nodes which are selected
	private Vertex 		localTreeRoot;				// root of local tree
	private boolean		mapByContext;				// boolean indicating the mappingByUser is done by context
	private boolean 	mapByDefn;				// boolean indicating the mappingByUser is done by defn Muhamamd
	private boolean		mapByDefnShow ;
	private boolean 	mapByUser;					// boolean indicating the mappingByUser is done by user
	private JMenuItem 	mappingInfo;				// mappingByUser information of the node
	private JPopupMenu mappingPopup;			// mappingByUser popup menu
	private JMenu 		mappingType;          		// mappingByUser type menu
	private UI 				myUI;                       		// UI variable
	private int 				noOfLines = 100 ;				//Lines to be displayed, initial value = all = 100 (different from numRelations to be found by the algortithm that are contained in DefnMappingOptions
	private int 				displayedSimilarity = 5; //minimum Value of similarity value to be displayed (not calculated, that one is defined in defnOptions)
	private int 				oldY;							// the previous y location of left clicked node
	private	JPopupMenu popup;					// popup menu
	//private boolean previouslyMappedByContext = false;
	private Vertex 		rightClickedNode;			// right clicked node
	private JMenuItem 	subset;              		// subset mappingByUser
	private JMenuItem 	subsetComplete;			// subset complete mappingByUser
	private JMenuItem 	superset;            		// superset mappingByUser
	private JMenuItem 	supersetComplete;		// superset complete mappingByUser
	
	private DefnMappingOptions defnOptions; 
	/**Reference to the core istance, it's set in the canvas constructor, we could also avoid to keep it, but then we should always get it via Core.getIstance();*/
	private Core core;
	
	/*******************************************************************************************
	 * Default constructor for myCanvas class.
	 */
	public Canvas()
	{
		// do nothing
	}
	/*******************************************************************************************
	 /**
	  * one argument constructor for myCanvas class.
	  * @param tempUI 	UI class
	  */
	public Canvas(UI ui){
		// initialize the global and local selected nodes vector
		globalNodesSelected = new Vector();
		localNodesSelected = new Vector();                
		
		
		// add the mouse listener
		addMouseListener(this);
		
		// assign the tempUI taken as arugment to myUI variable
		myUI = ui;
		
		// initialize mapByUser variable to be false
		mapByUser = false;
		
		// initialize mapByUser variable to be false
		mapByContext = false;
		
		// initialize mapByDefnUser variable to be false
		mapByDefn = false;  // Muhammad
		
		core = Core.getInstance();
		// repaint the canvas
		//repaint();
	}
	/**
	 * This function implements the actionperformed.
	 * @param e MouseEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		// get the object which was clicked on
		Object obj = e.getSource();
		
		if (obj == desc)
		{	//TODO:Edit to work differently for OWL files
			Vertex node;
			node = getRightClickedNode();
			
			if(node.getOntNode()==GSM.XMLFILE){// this means XML file
				StringTokenizer st = new StringTokenizer(node.getDesc());
				String descript="";
				int maxChar = 50;
				
				while (st.hasMoreTokens()){
					for(int i=0; i<maxChar && st.hasMoreTokens(); i++){
						String tok = st.nextToken();
						descript += tok + " ";
						i += tok.length();
					}
					descript += '\n';
				}
				
				JOptionPane.showMessageDialog(null,"Node Name:\n" +node.getName()+"\n\n"+"Description:\n" + descript+"\n\n","Node Info", JOptionPane.PLAIN_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(null,"Node Name:\n" +node.getName()+"\n\n"+"Description:\n" + node.getOWLDesc()+"\n\n","Node Info", JOptionPane.PLAIN_MESSAGE);
				
			}
			
		}
		
		// if the user clicked on any of the mappingByUser types
		if (	(obj == exact) || (obj == subset) || (obj == subsetComplete) || 
				(obj == superset) || (obj == supersetComplete) || 
				(obj == comparativeExact) || (obj == comparativeSubset) || (obj == comparativeSuperset))	{
			//JOptionPane.showMessageDialog(null,"Hello1","Khan1", JOptionPane.PLAIN_MESSAGE);			
			// signal the JMenuItem openOntologyFile and openLocalFile to be disabled
			//myUI.disableLoadFiles();
			
			
			// create a UserMapping class variable and create a mappingByUser between local and global nodes selected
			UserMapping map = new UserMapping();
			
			for (int i =0; i < globalNodesSelected.size(); i++){
				map.getGlobalVertices().addElement(globalNodesSelected.elementAt(i));
				((Vertex)globalNodesSelected.elementAt(i)).setIsMapped(true);
			}
			for (int i =0; i < localNodesSelected.size(); i++){
				map.getLocalVertices().addElement(localNodesSelected.elementAt(i));
				setNodeSelected(((Vertex)localNodesSelected.elementAt(i)),false);
				((Vertex)localNodesSelected.elementAt(i)).setIsMapped(true);
			}
			
			// set the mappingByUser category		
			if ((globalNodesSelected.size() == 1) && (localNodesSelected.size() == 1)){
				// JOptionPane.showMessageDialog(null,"11","11", JOptionPane.PLAIN_MESSAGE);			
				map.setMappingCategory("1-to-1");	
			}else if ((globalNodesSelected.size() > 1) && (localNodesSelected.size() == 1)){
				// JOptionPane.showMessageDialog(null,"m1","m1", JOptionPane.PLAIN_MESSAGE);			
				map.setMappingCategory("Many-to-1");	
			}else if ((globalNodesSelected.size() == 1) && (localNodesSelected.size() > 1)){
				// JOptionPane.showMessageDialog(null,"1m","1m", JOptionPane.PLAIN_MESSAGE);			
				map.setMappingCategory("1-to-Many");
			}else{  //JOptionPane.showMessageDialog(null,"mm","mm", JOptionPane.PLAIN_MESSAGE);			
				map.setMappingCategory("Many-to-Many");
			}
			
			// set the mappingByUser type
			if (obj == exact)
				map.setMappingType("Exact");
			else if (obj == subset)
				map.setMappingType("Subset");
			else if (obj == subsetComplete)
				map.setMappingType("Subset complete");
			else if (obj == superset)
				map.setMappingType("Superset");
			else if (obj == supersetComplete)
				map.setMappingType("Superset complete");
			else if (obj == comparativeExact)
				map.setMappingType("Comparative exact");
			else if (obj == comparativeSubset)
				map.setMappingType("Comparative subset");
			else if (obj == comparativeSuperset)
				map.setMappingType("Comparative superset");
			
			// set the globalvertices mappingByUser point to this mappingByUser class
			for (int i =0; i < globalNodesSelected.size(); i++)
				((Vertex)map.getGlobalVertices().elementAt(i)).setUserMapping(map);
			
			// set the localvertices mappingByUser point to this mappingByUser class
			for (int i =0; i < localNodesSelected.size(); i++)
				((Vertex)map.getLocalVertices().elementAt(i)).setUserMapping(map);		
			
			// clear the globalNodesSelected and localNodesSelected vectors 
			globalNodesSelected.clear();
			localNodesSelected.clear();
		}
		
		if ((obj == cancel) || (obj == cancelPopup)){
			// set the node selection to false in global tree
			for (int i=0; i < globalNodesSelected.size(); i++)
				setNodeSelected(((Vertex)globalNodesSelected.elementAt(i)),false);
			
			// set the node selection to false in local tree
			for (int i=0; i < localNodesSelected.size(); i++)
				setNodeSelected(((Vertex)localNodesSelected.elementAt(i)),false);
			
			// clear the node selection for global and local
			globalNodesSelected.clear();
			localNodesSelected.clear();
		}
		
		repaint();
	}
	/*******************************************************************************************
	 /**
	  * This function scans through the local vertices of global node mappingByUser 
	  * and returns false if any of them are not visible, if all the local vertices that are mapped
	  * to the global node taken as argument are visible then the function returns true
	  *
	  * @param node global node that is mapped
	  */	
	public boolean areDefnLocalNodesVisible(Vertex node)
	{
		Vertex tempNode;
		
		for (int i = 0; i < node.getDefnMapping().getLocalVertices().size(); i++)
		{
			tempNode = (Vertex)node.getDefnMapping().getLocalVertices().elementAt(i);
			if(tempNode.isVisible() == false)
				return false;
		}
		return true;
	}	
	/*******************************************************************************************
	 /**
	  * This function scans through the local vertices of global node mappingByUser 
	  * and returns false if any of them are not visible, if all the local vertices that are mapped
	  * to the global node taken as argument are visible then the function returns true
	  *
	  * @param node global node that is mapped
	  */	
	public boolean areLocalNodesVisible(Vertex node)
	{
		Vertex tempNode;
		
		for (int i = 0; i < node.getUserMapping().getLocalVertices().size(); i++)
		{
			tempNode = (Vertex)node.getUserMapping().getLocalVertices().elementAt(i);
			if(tempNode.isVisible() == false)
				return false;
		}
		return true;
	}

	public void setTree(TreeBuilder tb) {
		
		Vertex treeRoot = tb.getTreeRoot();
		Ontology o = tb.getOntology();
		int nodeType;
		if(o.isSource()) {
			setGlobalTreeRoot(treeRoot);
			nodeType = GSM.SOURCENODE;
		}
		else {
			setLocalTreeRoot(treeRoot);
			nodeType = GSM.TARGETNODE;
		}
		
		//TO BE CHANGED IN THE FUTURE
		for (Enumeration e = tb.getTreeRoot().preorderEnumeration(); e.hasMoreElements() ;) 
		{
			Vertex node = (Vertex) e.nextElement();
			node.setName(node.toString());
			node.setNodeType(nodeType);
			node.setVerticalHorizontal();
		}

		int totalNodes = tb.getTreeCount();	// number of nodes created in global tree
		Dimension dim;		// dimension of the panel
		double height;		// height of the canvas
		
		// setOldY to negative number indicating that node was not clicked
		setOldY(-1);

		// get the dimension of the panel
		JPanel canvasPanel = myUI.getCanvasPanel();
		dim = canvasPanel.getPreferredSize();
		
		// figure out what the canvas height should be
		height = 70+25*totalNodes;
		
		// if the current tree height is greater than the panel's height
		// set the new height to be height of the tree
		if(dim.getHeight() > height)
			height = dim.getHeight();
		
		//	set the width of canvas properly
		//computeCanvasWidth(ontoType);
		
		// set the panel preferred size		
		canvasPanel.setPreferredSize(new Dimension((int)canvasWidth,(int)height));  // take max of the length of the two trees
		
		// create pop up menu when the user rights clicks
		createPopupMenu();
		
		// create mappingByUser pop up menu when the user mapps nodes
		createMappingPopupMenu();
		
		setMapByDefn(false);
		
		// repaint the canvas
		repaint();
	}
	/**
	 * @param ontoType
	 * @param langIndex
	 * @param syntaxIndex
	 * @return
	 */
	
	/**
	 * @param ontoType
	 */
	private void computeCanvasWidth(int ontoType) {
		Vertex node = new Vertex(null);
		int x=0;//initial value
		int y=0;
		
		if(ontoType == GSM.SOURCENODE)
			node = getGlobalTreeRoot();
		else if(ontoType == GSM.TARGETNODE)
			node = getLocalTreeRoot();
		
		if(node != null)
			for (Enumeration e = node.preorderEnumeration(); e.hasMoreElements() ;) 
			{
				// get the node
				node = (Vertex) e.nextElement();
				y = node.getLevel()*20 + node.getName().length()*7;
				if(y>x) x=y;
			}
		
		x+=30;
		if(x*2 > canvasWidth) canvasWidth=x*2;
		
	}	
	/*******************************************************************************************
	 /**
	  * This function figures out the mappingByUser for the parent based on the mappingByUser of the children
	  * 
	  * @param childrenMappings - vector of children mappingByUser types
	  * @return the parent mappingByUser based on the children mappings
	  */	
	public String contextMap(Vector childrenMappings)
	{
		String prevChildMapping, currChildMapping;
		String newMapping="Null";
		
		if(childrenMappings.size() > 1)
		{
			// get the first child mappingByUser
			prevChildMapping = (String) childrenMappings.elementAt(0);
			//System.out.println("in contextMap method");
			//System.out.println("No. Children" + childrenMappings.size());
			for (int index = 1;index < childrenMappings.size();index++ )
			{
				// get the next child mappings
				currChildMapping = (String) childrenMappings.elementAt(index);
				
				// now compare the two childs (prev and curr) get a mappingByUser based onthe context mappingByUser table
				// and set the resulting mappingByUser to "newMapping" variable
				
				
				// if one of the child is exact the new mappingByUser is the same as the other child
				if (prevChildMapping == "Exact")
				{
					newMapping = currChildMapping;
				}
				else if (currChildMapping == "Exact")
				{
					newMapping = prevChildMapping;
				}
				
				// if the 2 childs have same mappingByUser then new mappingByUser is going to be same as well
				else if (prevChildMapping == currChildMapping)
				{
					newMapping = prevChildMapping;
				}
				
				// if child1 = "Superset" and child2 = "Null" newMapping = "Superset"
				else if ((prevChildMapping == "Superset") && (currChildMapping == "Null"))
				{
					newMapping = prevChildMapping;
				}
				
				// if child2 = "Superset" and child1 = "Null" newMapping = "Superset"
				else if ((currChildMapping == "Superset") && (prevChildMapping == "Null"))
				{
					newMapping = currChildMapping;
				}
				else
				{
					newMapping = "Null";
				}
				
				// update the previous mappingByUser to be the new mappingByUser for more than 2 children
				prevChildMapping = newMapping;
				
			}
			
			// return the new mappingByUser based on the children mappings
			return newMapping;
		}
		else
		{
			if (childrenMappings.size() == 0)
			{
				// ???????????????????????????//
				//System.out.println("childrenMappings.size() == 0");
				return "Null";
			}
			else
			{
				//System.out.println("childrenMappings.size() == 1");
				return (String) childrenMappings.elementAt(0);
				
			}
		}
	}
	/**
	 * Creates a popupmenu when the user maps nodes
	 */
	public void createMappingPopupMenu()	
	{
		mappingPopup = new JPopupMenu();
		
		mappingType = new JMenu("Mapping Type");
		
		exact = new JMenuItem("Exact");
		subset = new JMenuItem("Subset");
		subsetComplete = new JMenuItem("Subset Complete");
		superset = new JMenuItem("Superset");
		supersetComplete = new JMenuItem("Superset Complete");
		comparative = new JMenu("Comparative");
		
		
		// add exact, subset, subsetComplete, superset, 
		// supersetComplete, comparitive menu items to mappingType menu
		mappingType.add(exact);
		mappingType.add(subset);
		mappingType.add(subsetComplete);
		mappingType.add(superset);
		mappingType.add(supersetComplete);
		mappingType.add(comparative);
		
		comparativeExact = new JMenuItem("Comparitive Exact");
		comparativeSubset = new JMenuItem("Comparitive Subset");
		comparativeSuperset = new JMenuItem("Comparitive Superset");
		
		comparative.add(comparativeExact);
		comparative.add(comparativeSubset);
		comparative.add(comparativeSuperset);
		
		cancel = new JMenuItem("Cancel");		
		
		// add the mappingType to mappingPopup menu
		mappingPopup.add(mappingType);
		
		// add cancel to mappingPopup menu
		mappingPopup.add(cancel);
		
		// add Listener to menuItems
		cancel.addActionListener(this);
		exact.addActionListener(this);
		subset.addActionListener(this);
		subsetComplete.addActionListener(this);
		superset.addActionListener(this);
		supersetComplete.addActionListener(this);
		comparativeExact.addActionListener(this);
		comparativeSubset.addActionListener(this);
		comparativeSuperset.addActionListener(this);
	}
	/**
	 * Creates a popupmenu when the user right clicks on a vertex
	 */
	public void createPopupMenu()
	{
		popup = new JPopupMenu();
		desc = new JMenuItem("Description");
		mappingInfo = new JMenuItem("Mapping Info");
		cancelPopup = new JMenuItem("Cancel");		
		
		// add the description to popup menu
		popup.add(desc);
		
		// add the mappingByUser information 
		popup.add(mappingInfo);
		
		// add the cancel menu item
		popup.add(cancelPopup);
		
		// add listener 
		desc.addActionListener(this);
		mappingInfo.addActionListener(this);
		cancelPopup.addActionListener(this);
		
	}
	/**
	 * This function deselects all the nodes of the local or global tree based on location clicked
	 *
	 * @param x which is the location of the node clicked
	 */
	public void deselectAllNodes(int x)
	{
		Vertex root, node;
		
		// if the user clicked on the left half of the screen then get the global root,
		// get the local root
		if (x < (canvasWidth/2))
			root = getGlobalTreeRoot();
		else
			root = getLocalTreeRoot();
		
		if (root == null)
			return;
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements() ;)
		{
			// get the node
			node = (Vertex) e.nextElement();
			
			// change the isSelected node to false
			setNodeSelected(node,false);
			
			if ((node.getNodeType() == GSM.SOURCENODE))
				globalNodesSelected.clear();
			else if ((node.getNodeType() == GSM.TARGETNODE))
				localNodesSelected.clear();
		}
	}
	
	public void selectedContextMapping() {
		mapByContext = true;
		repaint();
	}
	
	/**
	 * This function changes the mapByContext variable to false and calls the repaint method
	 */	
	public void deselectedContextMapping()
	{
		mapByContext = false;
		repaint();
	}
	/**
	 * This function changes the mapByDefn variable to false and calls the repaint method
	 */	
	public void deselectedDefnMapping()
	{
		//  JOptionPane.showMessageDialog(null,"hip hip desc","title", JOptionPane.PLAIN_MESSAGE);	
		mapByDefnShow = false;
		repaint();
	}	
	/**
	 * This function draws in the mappingByUser lines between vertices for the context mappings
	 *
	 * @param graphic graphics
	 * @root the root of the tree 
	 */	
	public void displayContextMappingLines(Graphics graphic, Vertex root)
	{
		//	 System.out.println(" display context mappingByUser line ");
		Vertex node;
		//	Vertex global, local;
		int x1,y1,x2,y2;
		
		//	global = null;
		//local = null;
		graphic.setColor(Colors.mappedByContextLineColor);
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements(); )
		{
			// get the node
			node = (Vertex) e.nextElement();
			
			// make sure that this node has been mapped
			if ((node.isLeaf() == false) && (node.getContextMapping() != null))
			{
				
				// make sure that the global and local parent nodes are visible
				// if they are then draw a mappingByUser line between these two nodes
				
				
				if ((node.isVisible() == true) && (node.getContextMapping().getLocalVertex().isVisible() == true))
				{
					x1 = node.getX2();
					y1 = (node.getY()+node.getY2())/2;
					
					x2 = node.getContextMapping().getLocalVertex().getX();
					y2 = (node.getContextMapping().getLocalVertex().getY()+node.getContextMapping().getLocalVertex().getY2())/2;
					
					// draw the line which connects the two vertices
					graphic.drawLine(x1,y1,x2,y2);
					
					// place the string (mappingByUser type) on top of the mappingByUser line						
					graphic.drawString(node.getContextMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
				}
				
			}
		}
		//repaint();
	}
	/**
	 * This function draws in the mappingByUser lines between vertices for the Definition mappings
	 *
	 * @param graphic graphics
	 * @root the root of the tree 
	 * @author Muhammad	
	 */	
	public void displayDefnMappingLines(Graphics graphic, Vertex root)
	{
		Vertex node;
		Vertex global, local;
		int x1,y1,x2,y2;
		
		global = null;
		local = null;
		graphic.setColor(Colors.mappedByDefnLineColor);
		//graphic.setColor(Colors.mappedByUserLineColor)
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements(); )
		{
			// get the node
			node = (Vertex) e.nextElement();
			// System.out.print("noman" + node.getName()); 
			
			
			
			
			if (node.getDefnMapping() != null && node.getDefnMapping().getLocalVertices().size() > 0 ) // node.defnMapping != null)
			{
				boolean localNodesVisible = areDefnLocalNodesVisible(node);
				if ((node.isVisible() == true) && (localNodesVisible == true))
				{	
					if (node.getDefnMapping().getMappingCategory().equals("1-to-M") )
					{
						
						global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
						global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
						x1 = global.getX2();
						y1 = (global.getY()+global.getY2())/2;
						
						
						Vector sim = node.getDefnMapping().getSimilarities();
						for (int j=0;j<node.getDefnMapping().getLocalVertices().size() && (Float)sim.elementAt(j) >= displayedSimilarity  && j < noOfLines && j<defnOptions.numRel ;j++)
						{
							local  = (Vertex)node.getDefnMapping().getLocalVertices().elementAt(j);
							
							x2 = local.getX();
							y2 = (local.getY()+local.getY2())/2;
							graphic.drawLine(x1,y1,x2,y2);
							//graphic.drawString(node.getDefnMapping().getMappingValue(),(x1+x2)/2,((y1+y2)/2) -5);
							graphic.drawString(node.getDefnMapping().getMappingValue1(local),(x1+x2)/2,((y1+y2)/2) -5);		
						}
						
					}
					
					
					if (node.getDefnMapping().getMappingCategory().equals("1-to-1") )
					{
						global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
						local  = (Vertex)node.getDefnMapping().getLocalVertices().elementAt(0);
						float sim = (Float) node.getDefnMapping().getSimilarities().elementAt(0);
						if(sim >= displayedSimilarity) {
							// get their location on canvas
							x1 = global.getX2();
							y1 = (global.getY()+global.getY2())/2;
							x2 = local.getX();
							y2 = (local.getY()+local.getY2())/2;
							
							// draw the line which connects the two vertices
							graphic.drawLine(x1,y1,x2,y2);
							graphic.drawString(node.getDefnMapping().getMappingValue1(local),(x1+x2)/2,((y1+y2)/2) -5);
						}
					}
				}
				
			}
			
		}
		
	}
	/**
	 * This function highlights the mapping lines for the selected node by changing the color
	 */
	public void displayHighlightedMappingLines(Graphics2D graphic2d, Vertex root){
		
		Vertex node;
		Vertex global, local;
		int x1,y1,x2,y2;
		int []x;
		int []y;
		
		
		graphic2d.setColor(Colors.mappedHighlightedLineColor );
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements(); ){
			
			// get the node
			node = (Vertex) e.nextElement();
			if(node.getIsSelected()){
				graphic2d.setStroke(new BasicStroke(2.0f));
				if (mapByUser && node.getUserMapping() != null){
					global = null;
					local = null;
					//graphic2d.setColor(Colors.mappedByUserLineColor );
					
					boolean localNodesVisible = areLocalNodesVisible(node);
					
					// only draw the mapping lines if the actual node and its local
					// nodes corresponding to this mapping are visible
					if ((node.isVisible() == true) && (localNodesVisible == true)){	
						if (node.getUserMapping().getMappingCategory() == "1-to-1"){
							// 1-to-1 Mapping
							
							// get the global and local nodes 
							global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(0);
							local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(0);
							
							// get their location on canvas
							x1 = global.getX2();
							y1 = (global.getY()+global.getY2())/2;
							x2 = local.getX();
							y2 = (local.getY()+local.getY2())/2;
							
							// draw the line which connects the two vertices
							graphic2d.draw(new Line2D.Double(x1,y1,x2,y2));
							
							// place the string (mapping type) on top of the mapping line						
							graphic2d.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
						}else if (node.getUserMapping().getMappingCategory() == "Many-to-Many"){
							// Many-to-Many mapping
							
							int [] globalX;	// keeps track of global nodes x values
							int [] globalY;	// keeps track of global nodes y values
							int [] localX;	// keeps track of local nodes x values
							int [] localY;	// keeps track of local nodes y values
							
							int minX1 = 99999999;
							int minY1 = 99999999;
							int maxX1 = 0;
							int maxY1 = 0;
							
							int minX2 = 99999999;
							int minY2 = 99999999;
							int maxX2 = 0;
							int maxY2 = 0;
							
							// initialize the arrays
							globalX = new int[node.getUserMapping().getGlobalVertices().size()];
							globalY = new int[node.getUserMapping().getGlobalVertices().size()];
							localX = new int[node.getUserMapping().getLocalVertices().size()];
							localY = new int[node.getUserMapping().getLocalVertices().size()];
							
							// for each global nodes selected get their x values y values
							for (int i =0; i< node.getUserMapping().getGlobalVertices().size(); i++){
								// get the global vertex
								global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(i);
								
								// get the x value of the global vertex
								x1 = global.getX2(); 
								
								// get the local value of the global vertex
								y1 = (global.getY()+global.getY2())/2;
								
								// place all the x1's in an globalX vector
								globalX[i] = x1;
								
								// place all the y1 in an globalY vector
								globalY[i] = y1;
								
								
								// keep track of the max of x1, and y1
								if (x1 > maxX1)
									maxX1 = x1;
								if (y1 > maxY1)
									maxY1 = y1;
								
								// keep track of the min of x1 and y1
								if (x1 < minX1)
									minX1 = x1;
								if (y1 < minY1)
									minY1 = y1;
								
							}
							
							// for each local nodes selected get their x values and y values
							for (int i =0; i<node.getUserMapping().getLocalVertices().size(); i++){
								// get the local node selected	
								local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(i);
								
								// get the x location of the local node
								x2 = local.getX();
								
								// get the y location of the local node
								y2 = (local.getY()+local.getY2())/2;
								
								// place all the x2's in an localX vector
								localX[i] = x2;
								
								// place all the y2 in an localY vector
								localY[i] = y2;
								
								// keep track of the max of x2, and y2
								if (x2 > maxX2)
									maxX2 = x2;
								if (y2 > maxY2)
									maxY2 = y2;
								
								// keep track of the min of x2, and y2
								if (x2 < minX2)
									minX2 = x2;
								if (y2 < minY2)
									minY2 = y2;
							}
							
							// draw the horizontal line from their corresponding x values
							// to max x1 value
							for (int j=0;j<node.getUserMapping().getGlobalVertices().size();j++)
								graphic2d.draw(new Line2D.Double(globalX[j],globalY[j],maxX1+20,globalY[j]));
							
							// draw the horizontal line from local vertices x value to
							// min x2 values
							for (int j=0;j<node.getUserMapping().getLocalVertices().size();j++)
								graphic2d.draw(new Line2D.Double(localX[j],localY[j],minX2-20,localY[j]));
							
							//draw the vertical line for global nodes
							graphic2d.draw(new Line2D.Double(maxX1+20,minY1,maxX1+20,maxY1));
							
							//draw the vertical line for local nodes
							graphic2d.draw(new Line2D.Double(minX2-20,minY2,minX2-20,maxY2));               		                        
							
							x1 = maxX1+20;
							y1 = (minY1+maxY1)/2;
							x2 = minX2-20;
							y2 = (minY2+maxY2)/2;
							
							// draw the mapping line between global and local nodes
							graphic2d.draw(new Line2D.Double(x1,y1,x2,y2));
							
							// place the mapping type on top of the line
							graphic2d.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);						
							
						}else if (node.getUserMapping().getMappingCategory() == "Many-to-1"){
							// Many-to-1 mapping
							
							int minX=99999999;
							int minY=99999999;
							int maxX=0;
							int maxY=0;
							
							x = new int[node.getUserMapping().getGlobalVertices().size()];
							y = new int[node.getUserMapping().getGlobalVertices().size()];
							
							// first get the local one node
							local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(0);
							
							// get the location of the local node
							x2 = local.getX();
							y2 = (local.getY()+local.getY2())/2;
							
							// for each global node get their x and y values 
							for (int i =0; i< node.getUserMapping().getGlobalVertices().size(); i++){
								// get the global node
								global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(i);
								
								// get the x value of the global node
								x1 = global.getX2();
								
								// get the y value of the global node
								y1 = (global.getY()+global.getY2())/2;
								
								// place all the x1's in an x1 vector
								x[i] = x1;
								
								// place all the y1 in an y1 vector
								y[i] = y1;
								
								// keep track of the max x1, and y1 values
								if (x1 > maxX)
									maxX = x1;
								if (y1 > maxY)
									maxY = y1;
								
								// keep track of the min x1, and y1 values
								if (x1 < minX)
									minX = x1;
								if (y1 < minY)
									minY = y1;
							}
							
							// draw a horizontal line from each global node selected
							// to the max of the x value
							for (int j=0;j<node.getUserMapping().getGlobalVertices().size();j++)
								graphic2d.draw(new Line2D.Double(x[j],y[j],maxX+10,y[j]));
							
							//draw the vertical line for global nodes selected
							graphic2d.draw(new Line2D.Double(maxX+10,minY,maxX+10,maxY));
							
							if (y2 <=maxY && y2 >=minY)
								y1 = y2;
							else
								y1 = (minY+maxY)/2;
							
							x1 = maxX+10;	
							
							// draw the mapping line from global nodes to local node
							graphic2d.draw(new Line2D.Double(x1,y1, x2,y2));						
							
							// place the mapping type ontop of the line
							graphic2d.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);							
							
						}else if (node.getUserMapping().getMappingCategory() == "1-to-Many"){
							// 1-to-Many mapping
							int minX=99999999;
							int minY=99999999;
							int maxX=0;
							int maxY=0;
							
							x = new int[node.getUserMapping().getLocalVertices().size()];
							y = new int[node.getUserMapping().getLocalVertices().size()];
							
							// first get the global one node
							global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(0);
							
							// get the location of the global node
							x1 = global.getX2();
							y1 = (global.getY()+global.getY2())/2;
							
							// for each local nodes selected get their x and y values
							for (int i =0; i<node.getUserMapping().getLocalVertices().size(); i++)				{
								local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(i);
								
								// get the x value of the local node
								x2 = local.getX();
								
								// get the y value of the local node
								y2 = (local.getY()+local.getY2())/2;
								
								// place all the x2's in an x2 vector
								x[i] = x2;
								
								// place all the y2 in an y2 vector
								y[i] = y2;
								
								// keep track of the maximum x2 and y2 values
								if (x2 > maxX)
									maxX = x2;
								if (y2 > maxY)
									maxY = y2;
								
								// keep track of the  minimum x2 and y2 values
								if (x2 < minX)
									minX = x2;
								if (y2 < minY)
									minY = y2;
							}
							
							// draw a horizontal line from the local nodes x location
							// to the mininum of x location
							for (int j=0;j<node.getUserMapping().getLocalVertices().size();j++)
								graphic2d.draw(new Line2D.Double(x[j],y[j],minX-20,y[j]));
							
							//draw the vertical line
							graphic2d.draw(new Line2D.Double(minX-20,minY,minX-20,maxY));               		
							
							if (y1 <=maxY && y1 >=minY)
								y2 = y1;
							else
								y2 = (minY+maxY)/2;
							
							x2 = minX-20;
							
							// draw the mapping line from global node to local node
							graphic2d.draw(new Line2D.Double(x1,y1, x2,y2));
							
							// place the mapping type ontop of the line
							graphic2d.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
							
						} // end of 1-to-many if statement
					} // end of if the node and local nodes are visible
				}
				
				if (mapByContext && node.isLeaf() == false && node.getContextMapping() != null){
					global = null;
					local = null;
					//graphic2d.setColor(Colors.mappedByContextLineColor );
					
					// make sure that the global and local parent nodes are visible
					// if they are then draw a mapping line between these two nodes
					if ((node.isVisible() == true) && (node.getContextMapping().getLocalVertex().isVisible() == true)){
						x1 = node.getX2();
						y1 = (node.getY()+node.getY2())/2;
						
						x2 = node.getContextMapping().getLocalVertex().getX();
						y2 = (node.getContextMapping().getLocalVertex().getY()+node.getContextMapping().getLocalVertex().getY2())/2;
						
						// draw the line which connects the two vertices
						graphic2d.draw(new Line2D.Double(x1,y1,x2,y2));
						
						// place the string (mapping type) on top of the mapping line						
						graphic2d.drawString(node.getContextMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
					}
				}
				
				if(mapByDefn && mapByDefnShow && node.getDefnMapping() != null && node.getDefnMapping().getLocalVertices().size() > 0)	{
					global = null;
					local = null;
					//graphic2d.setColor(Colors.mappedByDefnLineColor);
					//graphic2d.setColor(Colors.mappedByUserLineColor);
					
					boolean localNodesVisible = areDefnLocalNodesVisible(node);
					if ((node.isVisible() == true) && (localNodesVisible == true)){	
						if (node.getDefnMapping().getMappingCategory().equals("1-to-M") )	{
							global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
							global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
							x1 = global.getX2();
							y1 = (global.getY()+global.getY2())/2;
							Vector sim = node.getDefnMapping().getSimilarities();
							for (int j=0;j<node.getDefnMapping().getLocalVertices().size() && (Float)sim.elementAt(j) >= displayedSimilarity &&  j < noOfLines && j<defnOptions.numRel ;j++){
								local  = (Vertex)node.getDefnMapping().getLocalVertices().elementAt(j);
								
								x2 = local.getX();
								y2 = (local.getY()+local.getY2())/2;
								graphic2d.draw(new Line2D.Double(x1,y1,x2,y2));
								//graphic2d.drawString(node.getDefnMapping().getMappingValue(),(x1+x2)/2,((y1+y2)/2) -5);
								graphic2d.drawString(node.getDefnMapping().getMappingValue1(local),(x1+x2)/2,((y1+y2)/2) -5);
							}
							
						}
						
						if (node.getDefnMapping().getMappingCategory().equals("1-to-1") ){
							global = (Vertex)node.getDefnMapping().getGlobalVertices().elementAt(0);
							local  = (Vertex)node.getDefnMapping().getLocalVertices().elementAt(0);
							float sim = (Float) node.getDefnMapping().getSimilarities().elementAt(0);
							if(sim >= displayedSimilarity) {
								// get their location on canvas
								x1 = global.getX2();
								y1 = (global.getY()+global.getY2())/2;
								x2 = local.getX();
								y2 = (local.getY()+local.getY2())/2;
								
								// draw the line which connects the two vertices
								graphic2d.draw(new Line2D.Double(x1,y1,x2,y2));
								graphic2d.drawString(node.getDefnMapping().getMappingValue1(local),(x1+x2)/2,((y1+y2)/2) -5);
							}
						}
					}
				}
			}//end of if node is selected
		}//end of enumeration
	}
	/**
	 * This function displays the lines of the trees
	 *
	 * @param graphic of type Graphics
	 * @param isGlobal boolean value indicating if it is global
	 */	
	public void displayLines(Graphics graphic, boolean isGlobal)	
	{
		Vertex root, node;
		Vertex lastChild, parent;
		int x1,y1,x2,y2;
		
		// get the root of the tree
		if (isGlobal == true)
			root = getGlobalTreeRoot();
		else
			root = getLocalTreeRoot();
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements() ;)
		{
			// get the node
			node = (Vertex) e.nextElement();
			
			
			// if the node is visible then draw the lines
			if (node.isVisible() == true)
			{
				x1 = node.getX()-7;
				y1 = node.getY()+(node.getY2()-node.getY())/2;
				
				x2 =  node.getX();
				y2 = node.getY()+(node.getY2()-node.getY())/2;
				
				// draw a horizontal line to the node
				graphic.drawLine(x1,y1 ,x2, y2);
				
				// draw the vertical line 
				
				if (node.isLeaf() == false)
				{
					// but first find where the line has to be drawn upto 
					// by finding the last child of the node
					lastChild = (Vertex) node.getLastChild();
					
					// draw oval to indicate that the node is expanded or collapsed
					graphic.drawOval(x1-4,y1-4,8,8);
					
					if (lastChild.isVisible() == true)
					{
						x2 = x1;
						y2 = lastChild.getY()+(lastChild.getY2()-lastChild.getY())/2;
						
						graphic.drawLine(x1,y1+4,x2,y2);
					}
					else
					{
						// place a | bar inside the circle to indicate that 
						// the node can be expanded
						graphic.drawLine(x1,y1-4,x1,y1+4);
					}
				}
				
				if (node != root)
				{
					
					// this is a leaf node, so get the parent of this node.
					parent = (Vertex) node.getParent();
					
					x1 = parent.getX()-7;
					y1 = node.getY()+(node.getY2()-node.getY())/2;
					
					x2 = node.getX();
					y2 = y1;
					
					// draw the horizontal line
					graphic.drawLine(x1,y1,x2,y2);
				}
			} // end of if the node is visible
		} // end of enumeration
	}	
	/**
	 * This function draws in the mappingByUser lines between vertices for the manual mappings
	 *
	 * @param graphic graphics
	 * @root the root of the tree 
	 */	
	public void displayManualMappingLines(Graphics graphic, Vertex root)
	{
		Vertex node;
		Vertex global, local;
		int x1,y1,x2,y2;
		
		int []x;
		int []y;
		
		global = null;
		local = null;
		graphic.setColor(Colors.mappedByUserLineColor);
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements(); )
		{
			// get the node
			node = (Vertex) e.nextElement();
			
			// make sure that this node has been mapped
			if (node.getUserMapping() == null)
				return;
			else
			{
				boolean localNodesVisible = areLocalNodesVisible(node);
				
				// only draw the mappingByUser lines if the actual node and its local
				// nodes corresponding to this mappingByUser are visible
				if ((node.isVisible() == true) && (localNodesVisible == true))
				{	
					if (node.getUserMapping().getMappingCategory() == "1-to-1")
					{
						// 1-to-1 Mapping
						
						// get the global and local nodes 
						global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(0);
						local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(0);
						
						// get their location on canvas
						x1 = global.getX2();
						y1 = (global.getY()+global.getY2())/2;
						x2 = local.getX();
						y2 = (local.getY()+local.getY2())/2;
						
						// draw the line which connects the two vertices
						graphic.drawLine(x1,y1,x2,y2);
						
						// place the string (mappingByUser type) on top of the mappingByUser line						
						graphic.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
					}
					else if (node.getUserMapping().getMappingCategory() == "Many-to-Many")
					{
						// Many-to-Many mappingByUser
						
						int [] globalX;	// keeps track of global nodes x values
						int [] globalY;	// keeps track of global nodes y values
						int [] localX;	// keeps track of local nodes x values
						int [] localY;	// keeps track of local nodes y values
						
						int minX1 = 99999999;
						int minY1 = 99999999;
						int maxX1 = 0;
						int maxY1 = 0;
						
						int minX2 = 99999999;
						int minY2 = 99999999;
						int maxX2 = 0;
						int maxY2 = 0;
						
						// initialize the arrays
						globalX = new int[node.getUserMapping().getGlobalVertices().size()];
						globalY = new int[node.getUserMapping().getGlobalVertices().size()];
						localX = new int[node.getUserMapping().getLocalVertices().size()];
						localY = new int[node.getUserMapping().getLocalVertices().size()];
						
						// for each global nodes selected get their x values y values
						for (int i =0; i< node.getUserMapping().getGlobalVertices().size(); i++)
						{
							// get the global vertex
							global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(i);
							
							// get the x value of the global vertex
							x1 = global.getX2(); 
							
							// get the local value of the global vertex
							y1 = (global.getY()+global.getY2())/2;
							
							// place all the x1's in an globalX vector
							globalX[i] = x1;
							
							// place all the y1 in an globalY vector
							globalY[i] = y1;
							
							
							// keep track of the max of x1, and y1
							if (x1 > maxX1)
								maxX1 = x1;
							if (y1 > maxY1)
								maxY1 = y1;
							
							// keep track of the min of x1 and y1
							if (x1 < minX1)
								minX1 = x1;
							if (y1 < minY1)
								minY1 = y1;
							
						}
						
						// for each local nodes selected get their x values and y values
						for (int i =0; i<node.getUserMapping().getLocalVertices().size(); i++)
						{
							// get the local node selected	
							local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(i);
							
							// get the x location of the local node
							x2 = local.getX();
							
							// get the y location of the local node
							y2 = (local.getY()+local.getY2())/2;
							
							// place all the x2's in an localX vector
							localX[i] = x2;
							
							// place all the y2 in an localY vector
							localY[i] = y2;
							
							// keep track of the max of x2, and y2
							if (x2 > maxX2)
								maxX2 = x2;
							if (y2 > maxY2)
								maxY2 = y2;
							
							// keep track of the min of x2, and y2
							if (x2 < minX2)
								minX2 = x2;
							if (y2 < minY2)
								minY2 = y2;
							
						}
						
						// draw the horizontal line from their corresponding x values
						// to max x1 value
						for (int j=0;j<node.getUserMapping().getGlobalVertices().size();j++){
							// draw the horizontal line
							graphic.drawLine(globalX[j],globalY[j],maxX1+20,globalY[j]);
						}
						// draw the horizontal line from local vertices x value to
						// min x2 values
						for (int j=0;j<node.getUserMapping().getLocalVertices().size();j++){
							// draw the horizontal line
							graphic.drawLine(localX[j],localY[j],minX2-20,localY[j]);
						}
						
						//draw the vertical line for global nodes
						graphic.drawLine(maxX1+20,minY1,maxX1+20,maxY1);
						
						//draw the vertical line for local nodes
						graphic.drawLine(minX2-20,minY2,minX2-20,maxY2);               		                        
						
						x1 = maxX1+20;
						y1 = (minY1+maxY1)/2;
						x2 = minX2-20;
						y2 = (minY2+maxY2)/2;
						
						// draw the mappingByUser line between global and local nodes
						graphic.drawLine(x1,y1,x2,y2);
						
						// place the mappingByUser type on top of the line
						graphic.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);						
						
					}
					else if (node.getUserMapping().getMappingCategory() == "Many-to-1")
					{
						// Many-to-1 mappingByUser
						
						int minX=99999999;
						int minY=99999999;
						int maxX=0;
						int maxY=0;
						
						x = new int[node.getUserMapping().getGlobalVertices().size()];
						y = new int[node.getUserMapping().getGlobalVertices().size()];
						
						// first get the local one node
						local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(0);
						
						// get the location of the local node
						x2 = local.getX();
						y2 = (local.getY()+local.getY2())/2;
						
						// for each global node get their x and y values 
						for (int i =0; i< node.getUserMapping().getGlobalVertices().size(); i++)
						{
							// get the global node
							global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(i);
							
							// get the x value of the global node
							x1 = global.getX2();
							
							// get the y value of the global node
							y1 = (global.getY()+global.getY2())/2;
							
							// place all the x1's in an x1 vector
							x[i] = x1;
							
							// place all the y1 in an y1 vector
							y[i] = y1;
							
							// keep track of the max x1, and y1 values
							if (x1 > maxX)
								maxX = x1;
							if (y1 > maxY)
								maxY = y1;
							
							// keep track of the min x1, and y1 values
							if (x1 < minX)
								minX = x1;
							if (y1 < minY)
								minY = y1;
							
						}
						
						// draw a horizontal line from each global node selected
						// to the max of the x value
						for (int j=0;j<node.getUserMapping().getGlobalVertices().size();j++)
						{
							// draw the horizontal line
							graphic.drawLine(x[j],y[j],maxX+10,y[j]);
						}
						
						//draw the vertical line for global nodes selected
						graphic.drawLine(maxX+10,minY,maxX+10,maxY);
						
						if (y2 <=maxY && y2 >=minY)
							y1 = y2;
						else
							y1 = (minY+maxY)/2;
						
						x1 = maxX+10;	
						
						// draw the mappingByUser line from global nodes to local node
						graphic.drawLine(x1,y1, x2,y2);						
						
						// place the mappingByUser type ontop of the line
						graphic.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);							
						
					}
					else if (node.getUserMapping().getMappingCategory() == "1-to-Many")
					{
						// 1-to-Many mappingByUser
						
						int minX=99999999;
						int minY=99999999;
						int maxX=0;
						int maxY=0;
						
						x = new int[node.getUserMapping().getLocalVertices().size()];
						y = new int[node.getUserMapping().getLocalVertices().size()];
						
						// first get the global one node
						global = (Vertex)node.getUserMapping().getGlobalVertices().elementAt(0);
						
						// get the location of the global node
						x1 = global.getX2();
						y1 = (global.getY()+global.getY2())/2;
						
						// for each local nodes selected get their x and y values
						for (int i =0; i<node.getUserMapping().getLocalVertices().size(); i++)
						{
							// get the local node
							local = (Vertex)node.getUserMapping().getLocalVertices().elementAt(i);
							
							// get the x value of the local node
							x2 = local.getX();
							
							// get the y value of the local node
							y2 = (local.getY()+local.getY2())/2;
							
							// place all the x2's in an x2 vector
							x[i] = x2;
							
							// place all the y2 in an y2 vector
							y[i] = y2;
							
							// keep track of the maximum x2 and y2 values
							if (x2 > maxX)
								maxX = x2;
							if (y2 > maxY)
								maxY = y2;
							
							// keep track of the  minimum x2 and y2 values
							if (x2 < minX)
								minX = x2;
							if (y2 < minY)
								minY = y2;
							
						}
						
						// draw a horizontal line from the local nodes x location
						// to the mininum of x location
						for (int j=0;j<node.getUserMapping().getLocalVertices().size();j++)
						{
							// draw the horizontal line
							graphic.drawLine(x[j],y[j],minX-20,y[j]);
						}
						
						//draw the vertical line
						graphic.drawLine(minX-20,minY,minX-20,maxY);               		
						
						if (y1 <=maxY && y1 >=minY)
							y2 = y1;
						else
							y2 = (minY+maxY)/2;
						
						x2 = minX-20;
						
						// draw the mappingByUser line from global node to local nodes
						graphic.drawLine(x1,y1, x2,y2);
						
						// place the mappingByUser type ontop of the line
						graphic.drawString(node.getUserMapping().getMappingType(),(x1+x2)/2,((y1+y2)/2) -5);
						
					} // end of 1-to-many if statement
				} // end of if the node and local nodes are visible
			} // end of if the node is mapped
		} // end of enumeration of all nodes
	}		
	/**
	 * This function draws in the mappingByUser lines between vertices
	 *
	 * @param graphic
	 */	
	public void displayMappingLines(Graphics graphic)
	{
		Vertex root = getGlobalTreeRoot();
		
		if ((root != null) && (mapByUser == true))
		{
			displayManualMappingLines(graphic,root);
		}
		if ((root != null) && (mapByContext == true))
		{
			displayContextMappingLines(graphic,root);
		}
		if ((root != null) && (mapByDefn == true) && (mapByDefnShow == true) )
		{
			//JOptionPane.showMessageDialog(null,"desc","title", JOptionPane.PLAIN_MESSAGE);	
			displayDefnMappingLines(graphic,root);
		}
		//Muhammad
	}
	/**
	 * This function displays the tree. This function is called from paint method.
	 *
	 * @param xmlFile filename
	 * @param graphic Graphics
	 * @param isGlobal boolean value indicating if the tree is global
	 */	
	public void displayTree(Graphics graphic, boolean isGlobal)
	{
		Vertex treeRoot;
		int middle = (int)canvasWidth/2;		
		
		int x, starting_X_Value;
		int y = 0;
		int oldY = 10;
		String name;
		int width, height, arcWidth, arcHeight;
		//	int nodeType;
		
		graphic.setColor(Colors.dividers);
		
		// draw a dividing line
		graphic.fillRect(middle-2,0,2,(int)canvasHeight);
		
		// draw a horizontal line
		graphic.fillRect(0,19,(int)canvasWidth,2);
		
		graphic.setColor(Colors.foreground);
		
		graphic.setFont(new Font("Arial", Font.BOLD,12));
		
		// Label the divided screens
		graphic.drawString("Global (Source) Ontology", 10,15);
		graphic.drawString("Local (Target) Ontology",(int)(canvasWidth/2)+10, 15);
		
		graphic.setColor(Colors.foreground);
		graphic.setFont(new Font("Arial",Font.PLAIN,12));
		
		
		if (isGlobal == true)
		{
			// get the global root
			treeRoot = getGlobalTreeRoot();
			
			// if the file is global ontolgy start displaying tree from x = 15
			starting_X_Value = 15;
		}
		else
		{
			// get the local root
			treeRoot = getLocalTreeRoot();
			
			// if the file is global ontolgy start displaying tree from (width/2)+15
			starting_X_Value = (int)(canvasWidth/2)+15;
		}
		
		arcWidth = 10;
		arcHeight = 10;
		height = 20;
		
		// displaying the tree as text
		Vertex node;	
		for (Enumeration e = treeRoot.preorderEnumeration(); e.hasMoreElements() ;) 
		{
			// get the node
			node = (Vertex) e.nextElement();
			//System.out.println("Name: " +node.getName()+".");
			//System.out.println("Key: " + node.getID());
			if (node.getDesc() == "" || node.getDesc() == " ")
			{
				//System.out.println("Desc: NONE");
			}
			//else
			//System.out.println("Desc: " +node.getDesc()+".");
			name = node.getName();
			
			//TODO: use this info below to compute the initial width of the canvas
			x = starting_X_Value+(node.getLevel())*20;
			y = oldY +25; 						
			width = 20+(name.length())*7;
			
			// set the coordinate, width, height, arcwidth, archeight to the node (Vertex)
			node.setX(x);
			node.setX2(x+width);
			node.setY(y);
			node.setY2(y+height);
			node.setWidth(width);
			node.setHeight(height);
			node.setArcWidth(arcWidth);
			node.setArcHeight(arcHeight);	
			//nodeType = node.getNodeType();
			//TODO: something wrong here in this section????????????????????????????????
			if (node.isVisible() == true)
			{
				//System.out.println(	node.getIsMappedByDef() + "  "  + mapByDefn );
				if ((node.getIsMapped() == true) && (mapByUser == true))
				{
					//System.out.println("*****************8IS MAPPED****************8888888");
					// change the color to node mapped color
					graphic.setColor(Colors.mappedByUser);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());
				}
				if ((node.getIsSelected() == true) && (node.getIsMappedByContext() == true) && (mapByContext == true))
				{
					// change the color to node selection color
					graphic.setColor(Colors.mappedByContextAndSelected);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());                                                                
				} 
				else if ((node.getIsMappedByContext() == true) && (mapByContext == true))
				{
					graphic.setColor(Colors.mappedByContext);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());
				}
				else if ((node.getIsMappedByDef() == true) && (mapByDefn == true) && mapByDefnShow == true )
				{
					graphic.setColor(Colors.mappedByDefn);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());
				}
				if ((node.getIsSelected() == true) && (node.getIsMapped() == true) && (mapByUser == true))
				{
					// change the color to node selection color
					graphic.setColor(Colors.mappedByUserAndSelected);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());                                                                
				}
				else if ((node.getIsSelected() == true) && ((node.getIsMappedByContext() == false) || (mapByContext == false)) && (node.getIsMapped() == false))
				{
					// change the color to node selection color
					graphic.setColor(Colors.selected);
					graphic.fillRoundRect(node.getX(),node.getY(),node.getWidth(),node.getHeight(), node.getArcWidth(),node.getArcHeight());                        
				}
				
				
				// change the color to foreground color
				graphic.setColor(Colors.foreground);
				
				// draw a round rectangle resembling the node
				graphic.drawRoundRect(x,y,width,height, arcWidth, arcHeight);
				
				// display the node name inside the round rectangle
				graphic.drawString(node.getName(),x+5,y+15);
				
				// keep track of the previous y to display the next obj
				oldY = y;
			}
		}
		
		// display the lines
		displayLines(graphic, isGlobal);
		
		// if there is at least one global node selected AND at least one local node selected
		// then call the function called mapNodess
		if ((globalNodesSelected.size() != 0) && (localNodesSelected.size() != 0))
			mapNodes(graphic);
	}
	/**
	 * This function returns the node to expand or contrast based on the location of the mouseclick
	 *
	 * @param x	the x location of mouseclick
	 * @param y	the y location of mouseclick
	 */	
	public void expandOrContract(int x, int y)
	{
		Vertex root,node, expandOrContractNode=null;
		
		int x1,y1,x2,y2;
		
		// Check to see if the area clicked was next to the node
		
		// get the root of the tree based on the mouseclick
		if (x < (canvasWidth/2))
			root = getGlobalTreeRoot();
		else
			root = getLocalTreeRoot();
		
		if (root != null)
		{
			for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements() ;)
			{
				// get the node
				node = (Vertex) e.nextElement();
				
				// get the location of the node 
				// and set a area left of node which is indicates to expand or contrast
				x1 = node.getX()-12;
				y1 = (node.getY()+(node.getY2()-node.getY())/2)-4;
				x2 = node.getX()-4;
				y2 = (node.getY()+(node.getY2()-node.getY())/2)+4;
				
				if ((x >= x1) && (x <= x2))
				{
					if ((y >= y1) && (y <= y2))
					{
						expandOrContractNode = node;
					}
				}
			}
		}
		
		// if the mouseclick is to the left of some node
		if (expandOrContractNode != null)
		{

			
			// If the children are visible then contrast the tree
			Vertex child;
			child = (Vertex)expandOrContractNode.getFirstChild();
			
			// check to see if the node is already visible
			// if the node is already visible then make it not visible and vice versa	                       
			if (child.isVisible() == true)
			{
				expandOrContractNode.setShouldCollapse(true);
				recurseOnNode(expandOrContractNode,0);
			}
			else
			{
				expandOrContractNode.setShouldCollapse(false);                        
				recurseOnNode(expandOrContractNode,1);
			}
		}
	}
	/**
	 * This function returns the width of the canvas
	 * @return canvasWidth width of canvas
	 */
	public double getCanvasWidth()
	{
		return this.canvasWidth;
	}
	/**
	 * This function returns the global tree root
	 * @return  globalTreeRoot the global tree root
	 */
	public Vertex getGlobalTreeRoot()
	{
		return globalTreeRoot;
	}
	/**
	 * This function returns the local tree root
	 * @return localTreeRoot the local tree root
	 */
	public Vertex getLocalTreeRoot()
	{
		return localTreeRoot;
	}
	/**
	 * This function returns the max of the two arguments
	 *
	 * @param t1 of type int	
	 * @param t2 of type int
	 * @return max of t1 or t2
	 */
	public int getMax(int t1, int t2)
	{
		if (t1 >= t2)
			return t1;
		else
			return t2;
	}
	/**
	 * This function figures out which node the clicked on.
	 *	
	 * @param x	the x location of mouse click
	 * @param y	the y location of mouse click
	 * @return Vertex the node which was clicked on
	 */
	public Vertex getNodeClicked(int x,int y)
	{
		// figure out which type of node was clicked (global or local)
		// if the click was within the left half of the canvas then it is global
		// else the it is within the lcoal side
		
		Dimension dim;
		Vertex root;
		double canvasWidth;//, canvasHeight;
		
		dim = getSize();
		
		canvasWidth = dim.getWidth();
		
		// get the root of the tree based on the mouseclick 
		if (x < (canvasWidth/2))
			root = getGlobalTreeRoot();	
		else
			root = getLocalTreeRoot();
		
		
		if (root != null)
		{
			Vertex node;
			for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements() ;)
			{
				// get the node
				node = (Vertex) e.nextElement();
				if (node.isVisible() == true)
				{
					if ((x <= node.getX2()) && (x >= node.getX()))
					{
						if ((y <= node.getY2()) && (y >= node.getY()))
						{
							return node;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return the number of relation to be found for each source node. When a method scans node.getDefnMapping.getGlobalVertices() it has to look only at the first getDfnLines. 
	 */
	public DefnMappingOptions getDefnOptions() {
		return defnOptions;
	}
/**
	 * This function returns the old node's y coordinate
	 * @return oldY previous y location
	 */
	public int getOldY()
	{
		return oldY;
	}

	/**
	 * This function returns the rightClickedNode
	 * @return rightClickedNode the node which was right clicked
	 */
	public Vertex getRightClickedNode()
	{
		return rightClickedNode;
	}
	/**
	 * This function performs mappingByUser by context
	 * This is done by mappingByUser parents of the nodes based on the mappings of their children.
	 */	
	public void mapByContext()
	{
		Vertex root;
		if (mapByContext == false)
		{
			
			mapByContext = true;	
			
			// get the root of the tree based on the mouseclick
			root = getGlobalTreeRoot();
			
			
			if (root != null)
			{
				performContextMapping(root);
			}
			//repaint the canvas
			repaint();
			//previouslyMappedByContext = true;
			
			
		}
		
		
		
	}
	/**
	 * This function performs mappingByUser by Defn
	 * This is done by mappingByUser parents of the nodes based on the mappings of their children.
	 */	
	public int mapByDefn(DefnMappingOptions dmo) //throws Exception
	{
		defnOptions = dmo;
		Vertex localRoot = getLocalTreeRoot();
		if (localRoot == null)
		{
			return countStat;
		}
		showAll(localRoot);
		
		Vertex root = getGlobalTreeRoot();
		if (root == null)
		{
			return countStat;
		}
		showAll(root) ;
		mapByDefn = true;	
		
		if (root != null)
		{
			//WGS CODE HERE
			try{
				out = new FileOutputStream("outputAM.dat");
				out2 = new FileOutputStream("initial_sim.txt");
			}catch(IOException ioe) {}
			p = new PrintStream( out );
			p2 = new PrintStream( out2 );
			
			performDefnMapping(root );
			printToFileSourceTarget();
			//This method has been developed for the wisconsin testcase, it creates the "sourceDesc.txt" and target files, it's not needed in the user version.
			printToFileSourceTargetDesc();
		
			p.close();
			p2.close();
		}
		
		//repaint the canvas
		repaint();
		return countStat;
		
	}
	/**
	 * This function maps the global nodes with local nodes
	 * 
	 * @param graphic of type Graphics
	 */	
	public void mapNodes(Graphics graphic)	
	{
		// There are 4 casses of mappingByUser (1-to-1, many-to-1,1-to-many,and many-to-many)
		
		int x1,y1,x2,y2;
		Vertex global,local;
		int []x;
		int []y;
		
		global = null;
		local = null;
		graphic.setColor(Colors.lineColor);
		if ((globalNodesSelected.size() == 1) && (localNodesSelected.size() == 1))
		{
			// ONE-TO-ONE MAPPING   
			
			// get the local and global node selected
			global = (Vertex)globalNodesSelected.elementAt(0);
			local = (Vertex)localNodesSelected.elementAt(0);
			
			// get their location on their canvas
			x1 = global.getX2();
			y1 = (global.getY()+global.getY2())/2;
			x2 = local.getX();
			y2 = (local.getY()+local.getY2())/2;
			
			// draw the connecting line between the two nodes
			graphic.drawLine(x1,y1,x2,y2);
			
			// display the pop up menu
			mappingPopup.show(this,(x1+x1)/2,(y1+y2)/2);
			
		}
		else if ((globalNodesSelected.size() > 1) && (localNodesSelected.size() > 1))
		{
			// MANY-TO-MANY MAPPING
			
			int [] globalX;	// keeps track of global nodes x values
			int [] globalY;	// keeps track of global nodes y values
			int [] localX;	// keeps track of local nodes x values
			int [] localY;	// keeps track of local nodes y values
			
			int minX1 = 99999999;
			int minY1 = 99999999;
			int maxX1 = 0;
			int maxY1 = 0;
			
			int minX2 = 99999999;
			int minY2 = 99999999;
			int maxX2 = 0;
			int maxY2 = 0;
			
			// initialize the arrays
			globalX = new int[globalNodesSelected.size()];
			globalY = new int[globalNodesSelected.size()];
			localX = new int[localNodesSelected.size()];
			localY = new int[localNodesSelected.size()];
			
			// for each global node selected, get the node and its location
			for (int i =0; i< globalNodesSelected.size(); i++)
			{
				// get the global node
				global = (Vertex)globalNodesSelected.elementAt(i);
				
				// get the location of the global node
				x1 = global.getX2();
				y1 = (global.getY()+global.getY2())/2;
				
				// place all the x1's in an globalX vector
				globalX[i] = x1;
				
				// place all the y1 in an globalY vector
				globalY[i] = y1;
				
				// keep track of the max x1 and y1
				if (x1 > maxX1)
					maxX1 = x1;
				if (y1 > maxY1)
					maxY1 = y1;
				
				// keep track of the min x1 and y1
				if (x1 < minX1)
					minX1 = x1;
				if (y1 < minY1)
					minY1 = y1;
				
			}
			
			// for each local node selected, get the node and its location
			for (int i =0; i<localNodesSelected.size(); i++)
			{
				// get the local node
				local = (Vertex)localNodesSelected.elementAt(i);
				
				// get the location of the local node
				x2 = local.getX();
				y2 = (local.getY()+local.getY2())/2;
				
				// place all the x2's in an localX vector
				localX[i] = x2;
				
				// place all the y2 in an localY vector
				localY[i] = y2;
				
				// keep track of the maximum x2 and y2
				if (x2 > maxX2)
					maxX2 = x2;
				if (y2 > maxY2)
					maxY2 = y2;
				
				// keep track of the minimum x2 and y2
				if (x2 < minX2)
					minX2 = x2;
				if (y2 < minY2)
					minY2 = y2;
				
			}
			
			// draw the horizontal line from the global node x location to the max 
			// global node x location
			for (int j=0;j<globalNodesSelected.size();j++)
			{
				// draw the horizontal line
				graphic.drawLine(globalX[j],globalY[j],maxX1+20,globalY[j]);
			}
			
			// draw the horizontal line from the local node x location to the max
			// local node x location
			for (int j=0;j<localNodesSelected.size();j++)
			{
				// draw the horizontal line
				graphic.drawLine(localX[j],localY[j],minX2-20,localY[j]);
			}
			
			//draw the vertical line for global nodes
			graphic.drawLine(maxX1+20,minY1,maxX1+20,maxY1);
			
			//draw the vertical line for local nodes
			graphic.drawLine(minX2-20,minY2,minX2-20,maxY2);               		                        
			
			// draw the mappingByUser line between global nodes and local nodes
			graphic.drawLine(maxX1+20,(minY1+maxY1)/2, minX2-20,(minY2+maxY2)/2);
			
			// display the popup menu
			mappingPopup.show(this,maxX1+((maxX1+minX2)/2),(minY1+maxY1)/2);
			
		}	       
		else if ((globalNodesSelected.size() >1) && (localNodesSelected.size() ==1))
		{
			// MANY-TO-ONE MAPPING
			
			int minX=99999999;
			int minY=99999999;
			int maxX=0;
			int maxY=0;
			
			x = new int[globalNodesSelected.size()];
			y = new int[globalNodesSelected.size()];
			
			// first get the local one node
			local = (Vertex)localNodesSelected.elementAt(0);
			
			// get the location of the local node
			x2 = local.getX();
			y2 = (local.getY()+local.getY2())/2;
			
			// for each global node get the node and its location
			for (int i =0; i< globalNodesSelected.size(); i++)
			{
				// get the global node
				global = (Vertex)globalNodesSelected.elementAt(i);
				
				// get the location of the global node
				x1 = global.getX2();
				y1 = (global.getY()+global.getY2())/2;
				
				// place all the x1's in an x1 vector
				x[i] = x1;
				
				// place all the y1 in an y1 vector
				y[i] = y1;
				
				// keep track of the max x1 and y1
				if (x1 > maxX)
					maxX = x1;
				if (y1 > maxY)
					maxY = y1;
				
				// keep track of the min x1 and y1
				if (x1 < minX)
					minX = x1;
				if (y1 < minY)
					minY = y1;
				
			}
			
			// draw the horizontal line from each global node to the 
			// max x location of the global node
			for (int j=0;j<globalNodesSelected.size();j++)
			{
				// draw the horizontal line
				graphic.drawLine(x[j],y[j],maxX+10,y[j]);
			}
			
			//draw the vertical line
			graphic.drawLine(maxX+10,minY,maxX+10,maxY);
			
			if (y2 <=maxY && y2 >=minY)
				y1 = y2;
			else
				y1 = (minY+maxY)/2;
			
			// draw the mappingByUser line between global nodes and local node
			graphic.drawLine(maxX+10,y1, x2,y2);
			
			// display the popup menu
			mappingPopup.show(this,x2,y2);
			
		}
		else if ((globalNodesSelected.size() ==1) && (localNodesSelected.size() > 1))
		{
			// ONE-TO-MANY MAPPING
			
			
			int minX=99999999;
			int minY=99999999;
			int maxX=0;
			int maxY=0;
			
			x = new int[localNodesSelected.size()];
			y = new int[localNodesSelected.size()];
			
			// first get the global one node
			global = (Vertex)globalNodesSelected.elementAt(0);
			
			// get the location of the global node
			x1 = global.getX2();
			y1 = (global.getY()+global.getY2())/2;
			
			// for each local nodes get the node and its location
			for (int i =0; i<localNodesSelected.size(); i++)
			{
				// get the local node
				local = (Vertex)localNodesSelected.elementAt(i);
				
				// get the location of the local node
				x2 = local.getX();
				y2 = (local.getY()+local.getY2())/2;
				
				// place all the x2's in an x2 vector
				x[i] = x2;
				
				// place all the y2 in an y2 vector
				y[i] = y2;
				
				// keep track of the maximum x2 and y2 
				if (x2 > maxX)
					maxX = x2;
				if (y2 > maxY)
					maxY = y2;
				
				// keep track of the minimum x2 and y2
				if (x2 < minX)
					minX = x2;
				if (y2 < minY)
					minY = y2;
				
			}
			
			// draw a horizontal line from local node to the min x location of the local node 
			for (int j=0;j<localNodesSelected.size();j++)
			{
				// draw the horizontal line
				graphic.drawLine(x[j],y[j],minX-20,y[j]);
			}
			
			//draw the vertical line
			graphic.drawLine(minX-20,minY,minX-20,maxY);               		
			
			if (y1 <=maxY && y1 >=minY)
				y2 = y1;
			else
				y2 = (minY+maxY)/2;
			
			// draw the mappingByUser line from global node to local nodes
			graphic.drawLine(x1,y1, minX-20,y2);               		
			
			// display the popup menu
			mappingPopup.show(this,x1,y1);
			
		}
		// repaint the canvas
		repaint();
	}	
	/**
	 * This function imeplments the mouseClicked. 
	 * When the user clicks on a node, this function hightlights the node.
	 * If the user right clicks on a node, display popup menu.
	 * If the selects a node while pressing Shift or control key select
	 * multiple nodes.
	 *
	 * @param e MouseEvent
	 */
	public void mouseClicked( MouseEvent e )
	{
		Vertex node;
		node = null;
		
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			
			// figure out which node was clicked
			// it the node was not clicked it will return null
			node = getNodeClicked(e.getX(), e.getY());
			
			if (node == null)	{
				// check to see if the user wants to exapand or close the tree
				expandOrContract(e.getX(),e.getY());
			}else if(node !=null){ //a node has been clicked
				//System.out.println("Clicked on node "+node.getName());
				// check to see if the shift button and ctrl buttons are pressed down when clicking
				if (e.isShiftDown() && e.isControlDown()){//BOTH SHIFT & CTRL are pressed down
					if (getOldY() != -1)// select all the nodes from the previous location to current location
						selectNodes(getOldY(),e.getY(), e.getX());		
					
				}else if(e.isControlDown() && !e.isShiftDown()){//ONLY CTRL button pressed down
					//checks to see if the node clicked is selected
					boolean nodeSelected = node.getIsSelected();
					
					// change the isSelected of node clicked according to previous check
					if(!nodeSelected){
						setNodeSelected(node,true);
						if ((node.getNodeType() == GSM.SOURCENODE))
							globalNodesSelected.addElement(node);
						else if ((node.getNodeType() == GSM.TARGETNODE))
							localNodesSelected.addElement(node);
					}else{
						setNodeSelected(node,false);
						
						Vertex vertex;
						if ((node.getNodeType() == GSM.SOURCENODE)){
							for (Enumeration enumeration = globalNodesSelected.elements(); enumeration.hasMoreElements() ;){
								// get the node
								vertex= (Vertex) enumeration.nextElement();
								
								if(vertex.getY() == node.getY()){
									globalNodesSelected.removeElement(vertex);
									break;
								}
							}
							
						}else if ((node.getNodeType() == GSM.TARGETNODE)){
							for (Enumeration enumeration = localNodesSelected.elements(); enumeration.hasMoreElements() ;){
								// get the node
								vertex= (Vertex) enumeration.nextElement();
								vertex= (Vertex) enumeration.nextElement();
								
								if(vertex.getY() == node.getY()){
									localNodesSelected.removeElement(vertex);
									break;
								}
							}
						}
					}//end of else for if(!nodeSelected)
					
					//set clicked node to oldY
					setOldY(node.getY());
					
				}else if(!e.isControlDown() && e.isShiftDown()){//ONLY SHIFT button pressed down
					//deselect all nodes
					deselectAllNodes(e.getX());
					
					if (getOldY() != -1){	
						// select all the nodes from the previous location to current location
						selectNodes(getOldY(),e.getY(), e.getX());	
						// selectNodes() also takes care of adding all the nodes to the nodesSelected vectors
					}
					
				}else if(!e.isControlDown() && !e.isShiftDown()){//NEITHER SHIFT NOR CTRL button pressed down
					// checks to see if the node clicked is selected
					boolean nodeSelected = node.getIsSelected();
					
					//deselects all nodes
					deselectAllNodes(e.getX());
					
					// change the isSelected of node clicked according to previous check
					if(!nodeSelected){
						setNodeSelected(node,true);
						setOldY(node.getY());
						if ((node.getNodeType() == GSM.SOURCENODE))
							globalNodesSelected.addElement(node);
						else if ((node.getNodeType() == GSM.TARGETNODE))
							localNodesSelected.addElement(node);
					}else{
						setNodeSelected(node,false);
						setOldY(-1);
						if ((node.getNodeType() == GSM.SOURCENODE))
							globalNodesSelected.clear();
						else if ((node.getNodeType() == GSM.TARGETNODE))
							localNodesSelected.clear();
					}
				}
			}//end of node != null 
			
			repaint();			
		}
		
		if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3){
			
			if (e.isPopupTrigger()) {
				
				node = getNodeClicked(e.getX(), e.getY());
				
				// check to see if the description is to displayed
				if ((node != null) && (node.isVisible()==true)){
					setRightClickedNode(node);
					popup.show(e.getComponent(),e.getX(), e.getY());
				}
			}
		}
	}
	/**
	 * This function does nothing.
	 * @param e MouseEvent
	 */	
	public void mouseEntered( MouseEvent e )
	{
		// left blank purposefully
	}	
	/**
	 * This function does nothing.
	 * @param e MouseEvent
	 */
	public void mouseExited( MouseEvent e )
	{
		// left blank purposefully
	}
	/**
	 * This function implements the mousePressed action. When the mouse button is pressed
	 * check to see if it is right clicked and display the  popup menu
	 *
	 * @param e MouseEvent
	 */	
	public void mousePressed( MouseEvent e)
	{
		Vertex node;
		
		if (e.isPopupTrigger()) 
		{
			
			node = getNodeClicked(e.getX(), e.getY());
			
			// check to see if the description is to displayed
			if ((node != null) && (node.isVisible() == true))
			{
				setRightClickedNode(node);
				popup.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	}
	/**
	 * This function implements the mouse released method
	 *
	 * @param e MouseEvent
	 */	
	public void mouseReleased( MouseEvent e)
	{
		Vertex node;
		if (e.isPopupTrigger()) 
		{
			
			node = getNodeClicked(e.getX(), e.getY());
			
			// check to see if the description is to displayed
			if ((node != null) && (node.isVisible() == true))
			{
				setRightClickedNode(node);
				popup.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	}
	/**
	 * This function paints the background and displays global and local trees
	 *
	 * @param graphic of type Graphics
	 */
	public void paint(Graphics graphic)
	{
		
		super.paint(graphic);
		Dimension dim;		// dimension of the canvas
		
		// get the dimension of the canvas
		dim = this.getSize();
		
		// get the width and height of the canvas
		canvasWidth = dim.getWidth();
		canvasHeight = dim.getHeight();
		
		// get the middle location of the canvas screen
		int middle = (int)canvasWidth/2;		
		
		// paint the whole background by selecting color 
		graphic.setColor(Colors.background);
		
		// Fill the whole screen (rectangle)
		graphic.fillRect(0,0,(int)canvasWidth,(int)canvasHeight);
		
		if (!core.sourceIsLoaded()  && !core.targetIsLoaded())
		{
			graphic.setColor(Colors.dividers);
			
			// draw a dividing line
			graphic.fillRect(middle-2,0,2,(int)canvasHeight);
			
			// draw a horizontal line
			graphic.fillRect(0,19,(int)canvasWidth,2);
			
			graphic.setColor(Colors.foreground);
			
			graphic.setFont(new Font("Arial", Font.BOLD,12));
			
			// Label the divided screens
			graphic.drawString("Global (Source) Ontology", 10,15);
			graphic.drawString("Local (Target) Ontology",(int)(canvasWidth/2)+10, 15);
		}
		else {
			if (core.sourceIsLoaded())
				displayTree(graphic, true);
			
			if (core.targetIsLoaded())
				displayTree(graphic, false);
			
			if ((core.sourceIsLoaded() ) && (core.targetIsLoaded() )){
				displayMappingLines(graphic);
				Graphics2D graphic2d = (Graphics2D) graphic;
				displayHighlightedMappingLines(graphic2d, getGlobalTreeRoot());
			}
		}
		
		this.revalidate();
		
	}	
	/**
	 * This function figures out the mappingByUser for the parent based on the mappingByUser of the children
	 * using partial mappingByUser table
	 * @param childrenMappings - vector of children mappingByUser types
	 * @return the parent mappingByUser based on the children mappings
	 */	
	public String partialContextMap(Vector childrenMappings)
	{
		String prevChildMapping, currChildMapping;
		String newMapping="Null";
		
		if(childrenMappings.size() > 1)
		{
			// get the first child mappingByUser
			prevChildMapping = (String) childrenMappings.elementAt(0);
			for (int index = 1;index < childrenMappings.size();index++ )
			{
				// get the next child mappings
				currChildMapping = (String) childrenMappings.elementAt(index);
				
				// now compare the two childs (prev and curr) get a mappingByUser based onthe context mappingByUser table
				// and set the resulting mappingByUser to "newMapping" variable
				
				// if one of the child is exact the new mappingByUser is the same as the other child
				if ((prevChildMapping == "Exact") && (currChildMapping == "Exact"))
				{
					newMapping = "Subset";
				}
				else if ((prevChildMapping == "Exact") && (currChildMapping == "Subset"))
				{
					newMapping = "Subset";
				}
				else if ((prevChildMapping == "Subset") && (currChildMapping == "Exact"))
				{
					newMapping = "Subset";
				}
				else
				{
					newMapping = "Null";
				}
				
				// update the previousMapping to be the new mappingByUser for more than 2 children
				prevChildMapping = newMapping;
				
			}
			
			// return the new mappingByUser based on the children mappings
			return newMapping;
			
		}
		else
		{
			if (childrenMappings.size() == 0)
			{
				// ???????????????????????????//
				return "Null";
			}
			else
			{
				return (String) childrenMappings.elementAt(0);
				
			}
		}
	}
	/**
	 * @param dm
	 * @param node
	 * @param localnode
	 */
	private float DSI(float MCP, String S, String T) {

		
		float n = 0;
		DefComparator d = new DefComparator() ; 		
		 char pattern = '|';
		 int occurs_s = 0;
		for(int i = 0; i < S.length(); i++) {
		      char next = S.charAt(i);
		      if(next == pattern) {
		        occurs_s++;
		      }
		    }
		
		 int occurs_t = 0;
			for(int i = 0; i < T.length(); i++) {
			      char next = T.charAt(i);
			      if(next == pattern) {
			        occurs_t++;
			      }
			    }
			
			n = occurs_t < occurs_s ? occurs_t : occurs_s;
			float result=0f;
			if(n == 0) {
				result = MCP;
			}
			else {
				float factor = (2*MCP)/(n*(n+1));
				
				 StringTokenizer ts = new StringTokenizer(S,"| ");
				 StringTokenizer tt = new StringTokenizer(T,"| ");
				 

				for(float i=0; i<n; i++) {
					result += factor *  (n -i) * d.compare(ts.nextToken(),tt.nextToken());
				}				
			}
			return result;
			
			

	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private float SSC(float MCP, String S, String T) {

		
		int n = 0;
		DefComparator d = new DefComparator() ; 		
		 char pattern = '|';
		 int occurs_s = 0;
		for(int i = 0; i < S.length(); i++) {
		      char next = S.charAt(i);
		      if(next == pattern) {
		        occurs_s++;
		      }
		    }
		
		 int occurs_t = 0;
			for(int i = 0; i < T.length(); i++) {
			      char next = T.charAt(i);
			      if(next == pattern) {
			        occurs_t++;
			      }
			    }
			
			n =  occurs_s; 
			
			float result=0f;
			float factor = MCP/n;
			
			 StringTokenizer ts = new StringTokenizer(S,"| ");
			 StringTokenizer tt;
			 
			for(int i=0; i<occurs_s; i++) {
				String st1 = ts.nextToken();
				 tt = new StringTokenizer(T,"| ");					
				 float max=0;
				 for(int j=0; j<occurs_t; j++) {
					 float measure = d.compare( st1, tt.nextToken()  );
				     if (measure > max) max = measure;
				 }
						
				result += factor * max;
			   // if(n==2)
				//JOptionPane.showMessageDialog(null, "Factor: " + (factor /n) + "\nS: " + S + "^^^occurs " + occurs_s + "\nST1 is: " + st1 + "\nT: " + T + "^^^occurs " + occurs_t + "\nResults" + result);
				   
			}
			
			return result;
			
			

	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`
	
	public void perform(DefnMapping dm, Vertex node, Vertex localnode)
	{
//		System.out.println(node.getName() + node.getDesc());
   //  System.out.println(localnode.getName() + localnode.getDesc());
//		System.out.print(node.getName() + "   ");
//		System.out.println(localnode.getName() );
		
		
		DefComparator d = new DefComparator() ; 
		
		for (Enumeration children = localnode.children();   children.hasMoreElements() ; )
		{
			Vertex child = (Vertex) children.nextElement();
			float fl ; 
			//JOptionPane.showMessageDialog(null,"dictionary","title", JOptionPane.PLAIN_MESSAGE);	
			//fl = d.getDictSimilarity(node.getName() , child.getName() ); 
			
			//WGS
			//JOptionPane.showMessageDialog(null,"nop dictionary","title", JOptionPane.PLAIN_MESSAGE);	
			//float names = 0f, descriptions = 0f;
			
			String SNodeName = "", SNodeDesc ="", SNodeVN = "", SNodeVD = "", SNodeHN = "", SNodeHD ="";
			String TNodeName = "", TNodeDesc ="", TNodeVN = "", TNodeVD = "", TNodeHN = "", TNodeHD ="";
			
			SNodeName = node.getName();
			SNodeDesc = node.getDesc();
			SNodeVN = node.getVerticalNames();
			//JOptionPane.showMessageDialog(null,"SNODE: " + SNodeName + "\nSNODEVN: " + SNodeVN);
			SNodeHN = node.getHorizontalNames();
			SNodeVD = node.getVerticalDescs();
			SNodeHD = node.getHorizontalDescs();
			
			TNodeName = child.getName();
			TNodeDesc = child.getDesc();
			TNodeVN = child.getVerticalNames();
			//JOptionPane.showMessageDialog(null,"TNODE: " + TNodeName + "\nTNODEVN: " + TNodeVN);
			TNodeHN = child.getHorizontalNames();
			TNodeVD = child.getVerticalDescs();
			TNodeHD = child.getHorizontalDescs();
			
			
			//	float names=0f, descriptions =0f; 
			float names=0f, ND = 0f, NVn=0f, NVd=0f, NHn=0f, NHd=0f,NDVn=0f, NDVd=0f, NDHn=0f, NDHd=0f;
			
			if(defnOptions.consultDict){
					names = d.getDictSimilarity(SNodeName, TNodeName); 
			   //  	names = 0.75f*names +(0.25f * d.getDictSimilarity(SNodeName +SNodeVN, TNodeName + TNodeVN));
				//	names = 0.75f*names +(0.25f * d.getDictSimilarity(SNodeName + SNodeHN, TNodeName + TNodeHN)); 
				//	descriptions = d.getDictSimilarity(SNodeDesc, TNodeDesc);                                       
				//	ND = d.getDictSimilarity( SNodeName + SNodeDesc, TNodeName + TNodeDesc);                        
				//	NVn = d.getDictSimilarity(SNodeName +SNodeVN, TNodeName + TNodeVN);                            
				//	NVd = d.getDictSimilarity(SNodeName + SNodeVD, TNodeName + TNodeVD);                            
				//	NHn = d.getDictSimilarity(SNodeName + SNodeHN, TNodeName + TNodeHN);                            
				//	NHd = d.getDictSimilarity(SNodeName + SNodeHD, TNodeName + TNodeHD);                            
				//	NDVn = d.getDictSimilarity(SNodeName + SNodeDesc + SNodeVN, TNodeName + TNodeDesc + TNodeVN);   
				//	NDVd = d.getDictSimilarity(SNodeName + SNodeDesc + SNodeVD, TNodeName + TNodeDesc + TNodeVD);   
				//	NDHn = d.getDictSimilarity(SNodeName + SNodeDesc + SNodeHN, TNodeName + TNodeDesc + TNodeHN);   
				//	NDHd = d.getDictSimilarity(SNodeName + SNodeDesc + SNodeHD, TNodeName + TNodeDesc + TNodeHD);   
			}else{
				//Base 
				names = d.compare(SNodeName, TNodeName);
				
				// Base special
				//names = d.compare(SNodeDesc, TNodeDesc);
				//names = 0.75f*names +(0.25f * d.compare(SNodeName + " " + SNodeHN, TNodeName + " " + TNodeHN));
				//names = 0.75f*names +(0.25f * d.compare( SNodeVN, TNodeVN));
				
				//descriptions = d.compare(SNodeDesc, TNodeDesc);
				//ND = d.compare( SNodeName + " " + SNodeDesc, TNodeName + " " + TNodeDesc);
				//NVn = d.compare(SNodeName + " " + SNodeVN, TNodeName + " " + TNodeVN);
				//NVd = d.compare(SNodeName + " " + SNodeVD, TNodeName + " " + TNodeVD);
				//NHn = d.compare(SNodeName + " " + SNodeHN, TNodeName + " " + TNodeHN);
				//NHd = d.compare(SNodeName + " " + SNodeHD, TNodeName + " " + TNodeHD);
				//NDVn = d.compare(SNodeName + " " + SNodeDesc + " " + SNodeVN, TNodeName + " " + TNodeDesc + " " + TNodeVN);
				//NDVd = d.compare(SNodeName + " " + SNodeDesc + " " + SNodeVD, TNodeName + " " + TNodeDesc + " " + TNodeVD);
				//NDHn = d.compare(SNodeName + " " + SNodeDesc + " " + SNodeHN, TNodeName + " " + TNodeDesc + " " + TNodeHN);
				//NDHd = d.compare(SNodeName + " " + SNodeDesc + " " + SNodeHD, TNodeName + " " + TNodeDesc + " " + TNodeHD);
			}
			if(defnOptions.algorithm.equals(defnOptions.DSI)) {
				//DSI
				//William version, i think is incorrect
				//names = 0.75f*names + DSI(0.25f, SNodeName + "| "+ SNodeVN,  TNodeName + "| "  + TNodeVN );
				names = defnOptions.mcp*names + DSI(1-defnOptions.mcp, SNodeVN,  TNodeVN );
				
				//DSI Special
				//names = 0.75f*names +(0.25f * d.compare(SNodeDesc + " " + SNodeVD, TNodeDesc + " " + TNodeVD));
			}
			else if(defnOptions.algorithm.equals(defnOptions.SSC)) {
				//SSC
				names = defnOptions.mcp*names + SSC(1-defnOptions.mcp,  SNodeHN,   TNodeHN);
				
				//SSC Special
				//names = 0.75f*names +(0.25f * d.compare(SNodeDesc + " " + SNodeHD, TNodeDesc + " " + TNodeHD));
			}
			
			fl = names;
			
			int f = (int) (fl * 100) ;
			if(f>=50) 	countStat++; //What is this for?
			
			if ( f >= defnOptions.threshold )
			{ 	
				node.setIsMappedByDef(true);
				child.setIsMappedByDef(true);
				dm.getLocalVertices().add(child); 
				dm.addToSimilarities(new Float(f));
				dm.reSort();
				dm.putIntoMap(child , new Float(f) ) ; 
				dm.setMappingValue(f);
			}
		
			
			if (child.isLeaf() == false)
				perform(dm , node , child);
		    node.setDefnMapping(dm);
			
		}

		
	}
	/**
	 * recursive method which performs the mappingByUser on a particular node/vertex
	 * @param node - vertex
	 */	
	public void performContextMapping(Vertex node)
	{
		Vertex child,localChild = null, localParent = null;
		boolean globalChildNotMapped = false;
		boolean partiallyMapped = false;
		Vector childrenMappings;
		childrenMappings = new Vector();
		
		// if node is a parent the perfrom mappingByUser based on children
		if (node.isLeaf() == false)
		{
			for (Enumeration children = node.children();   children.hasMoreElements() ; )
			{
				child = (Vertex) children.nextElement();
				
				// if child is also a parent then performContextMapping on this child-parent
				if (child.isLeaf() == false)
					performContextMapping(child);
				
				// if the child is mapped by the user
				if(child.getIsMapped() == true)
				{
					childrenMappings.addElement(child.getUserMapping().getMappingType());
					localChild = (Vertex) (child.getUserMapping().getLocalVertices().elementAt(0));
					localParent = (Vertex) localChild.getParent();
					//System.out.println("LOCAL PARENT =  " + localParent.getName() );
				}
				// else if the child is mapped by context previously
				else if (child.getIsMappedByContext() == true)
				{
					//System.out.println("********************CHILD IS ALSO A PARENT********************");
					// case where the child is also a parent
					childrenMappings.addElement(child.getContextMapping().getMappingType());
					localChild = (Vertex) (child.getContextMapping().getLocalVertex());
					localParent = (Vertex) localChild.getParent();
					//System.out.println("LOCAL PARENT =  " + localParent.getName() );
					
				}
				else
				{
					// this child was not mapped
					childrenMappings.addElement("Null");
					globalChildNotMapped = true;
					//System.out.println("NULL MAPPING");
				}
				
			}
			
			// *************************** PARTIAL MAPPING CHECK *************************** 
			// check for partial mappingByUser - case where if there are some children in one ontology that cannot be mapped
			// to any of the children in the other ontology.
			// check to see if there are any null mappingByUser in the global ontology
			
			// one possible case of partial mappingByUser is where there is a global child which is not mapped 
			// and all local children are mapped
			if (globalChildNotMapped == true)
			{
				
				// assume partially mapped exists and we are going to disprove it by 
				// emumerating on the local children and find an local child which is not mapped
				if (localParent != null)
				{
					partiallyMapped = true;
					
					// check to see if the children of the localParent are all mapped
					// if true then partial mappingByUser exists here
					for (Enumeration localChildren = localParent.children();localChildren.hasMoreElements() ; )
					{
						localChild = (Vertex) localChildren.nextElement();
						
						// if the localChild is NOT mapped by the user and is NOT mapped by context
						if((localChild.getIsMapped() == false) && (localChild.getIsMappedByContext() == false))
						{
							partiallyMapped = false;
						}
					}
				}
			}
			else
			{
				// another possible case of partial mappingByUser is where there is a local child which is not mapped
				// and all global children are mapped
				
				partiallyMapped = false;
				if (localParent != null)
				{
					
					// check to see if any of the local children of localParent are not mapped
					for (Enumeration localChildren = localParent.children();localChildren.hasMoreElements() ; )
					{
						localChild = (Vertex) localChildren.nextElement();
						
						// if the localChild is NOT mapped by the user and is NOT mapped by context
						if((localChild.getIsMapped() == false) && (localChild.getIsMappedByContext() == false))
						{
							// local child is not mapped but the global children are all mapped
							partiallyMapped = true;
						}
					}
				}
				
			}
			String parentMappingType = "Null";
			
			if (partiallyMapped == true)
			{
				parentMappingType = partialContextMap(childrenMappings);
			}
			else
			{
				parentMappingType = contextMap(childrenMappings);
				//System.out.println("----------------------------------------");
				//System.out.println("Parent: " + node.getName() + " MappingType: " + parentMappingType);
				//System.out.println("----------------------------------------");
			}
			
			if (parentMappingType != "Null")
			{
				// Set the context mappingByUser for the parent node in global ontology
				node.setIsMappedByContext(true);
				node.setContextMapping(new ContextMapping());
				node.getContextMapping().setGlobalVertex(node);
				node.getContextMapping().setLocalVertex(localParent);
				
				// Set the context mappingByUser for the local node in local ontology
				localParent.setIsMappedByContext(true);
				localParent.setContextMapping(node.getContextMapping());
				
				// set the mappingByUser type for the parent
				node.getContextMapping().setMappingType(parentMappingType);
				
				
			}					
			
			// clear the children mappings vector
			childrenMappings.clear();
			
		}
		else
			return;
		
		//repaint();

	}
	/**
	 * recursive method which performs the mappingByUser on a particular node/vertex
	 * @param node - vertex
	 */	
	//######################################################
	public void printToFileSourceTarget() {
		

		Vertex localRoot = getLocalTreeRoot();
		Vertex globalRoot = getGlobalTreeRoot();
		
		if (localRoot == null || globalRoot == null)
		{
			return ;
		}
		


		FileOutputStream out = null; // declare a file output object
	    PrintStream p; // declare a print stream object
		
	    
	    try{
			out = new FileOutputStream("target.txt");
		}catch(IOException ioe) {}
		p = new PrintStream( out );
	    
		printVertexTree(localRoot, p);
		p.close();
		
		 try{
				out = new FileOutputStream("source.txt");
			}catch(IOException ioe) {}
			p = new PrintStream( out );
		    
			printVertexTree(globalRoot, p);
			p.close();
		
		
		
	}
	//######################################################
	public void printVertexTree(Vertex v, PrintStream p) {
		
		if(v == null) return;
		for (Enumeration e = v.children() ; e.hasMoreElements(); ) {
		  Vertex temp = (Vertex)e.nextElement();
		  printVertexTree(temp,p);
	     //System.out.println("faisaly " + v.getName() + "--->" + temp.getName());
	     p.println(v.getName());
	     p.println(temp.getName());
		}
		
		
	
	
	}
	//######################################################
	/**
	 * This method and printVertexTreeDesc() below have been created to print the source and target ontology in the sourceDesc.txt and targetDesc.txt files
	 * The difference with the printToFileSourceTarget method is that this one print nodes in this format "desc/tname" /t = tab
	 * the description is taken from node with getDesc() method, in the XML ontology description is equals to "exp" attribute
	 * this method has been developed to study the wisconsin (madison dane) case only.
	 */
	public void printToFileSourceTargetDesc() {
		

		Vertex localRoot = getLocalTreeRoot();
		Vertex globalRoot = getGlobalTreeRoot();
		
		if (localRoot == null || globalRoot == null)
		{
			return ;
		}
		


		FileOutputStream out = null; // declare a file output object
	    PrintStream p; // declare a print stream object
		
	    
	    try{
			out = new FileOutputStream("targetDesc.txt");
		}catch(IOException ioe) {}
		p = new PrintStream( out );
	    
		printVertexTreeDesc(localRoot, p);
		p.close();
		
		 try{
				out = new FileOutputStream("sourceDesc.txt");
			}catch(IOException ioe) {}
			p = new PrintStream( out );
		    
			printVertexTreeDesc(globalRoot, p);
			p.close();
		
		
		
	}
	
	/**
	 * See printToFileSourceTargetDesc comments, print nodes in "desc/tname" format, if desc is empty puts "9999999"
	 */
    public void printVertexTreeDesc(Vertex v, PrintStream p) {
		
		if(v == null) return;
		String desc = v.getDesc();
		if(desc == null || desc.equals(""))
			  desc = "9999999";
		p.println(desc+"\t"+v.getName());
		for (Enumeration e = v.children() ; e.hasMoreElements(); ) {
		  Vertex temp = (Vertex)e.nextElement();
		  printVertexTreeDesc(temp,p);
		}
	}
	//#####################################################
	
	public void performDefnMapping(Vertex node)
	{
		Vertex localRoot = getLocalTreeRoot();
		
		if (localRoot == null)
		{
			return ;
		}
	
		for (Enumeration children = node.children();   children.hasMoreElements() ; )
		{
			Vertex child = (Vertex) children.nextElement();
			//System.out.println(child.getName());
			DefnMapping d = new DefnMapping();
			d.getGlobalVertices().add(child);
			perform(d , child , localRoot);
			child.setDefnMapping(d) ;
			if (child.getDefnMapping().getLocalVertices().size() > 1)
			{
				d.setMappingCategory("1-to-M");
				//d.mappingValue = 1.0 ;
			}
			if (child.getDefnMapping().getLocalVertices().size() == 1)
			{
				d.setMappingCategory("1-to-1");	
				//d.mappingValue = 1.0 ;
			}
			if (child.getDefnMapping().getLocalVertices().size() < 1)
			{
				d.setMappingCategory("0-to-0");	
				//d.mappingValue = 1.0 ;
			}
			
			
			
			//WGS DEBUGGING START HERE
			try {
		    
			p.println(child.getName() +"---> " + child.getDefnMapping().getLocalVertices().get(0));
			
			for(int j=0; j< child.getDefnMapping().getLocalVertices().size() ; j++ ) {
				p2.println(child.getName());
				p2.println(child.getDefnMapping().getLocalVertices().get(j));
				p2.println(  ( (Float) (child.getDefnMapping().getSimilarities()).get(j)  )/100.00 ); }
			
			} catch(Exception e) {}
			//WGS DEBUGGING ENDS HERE
			if (child.isLeaf() == false)
				performDefnMapping(child);
			
		}
	}
	/**
	 * This function recursively calls  setIsMappedByDef() of every node under the initial root
	 * @param node
	 */
	public void performShowAll(Vertex node)
	{
		
		for (Enumeration children = node.children();   children.hasMoreElements() ; )
		{
			Vertex child = (Vertex) children.nextElement();
			child.setIsMappedByDef(false);
			if (child.isLeaf() == false)
				performShowAll(child);
			
		}
	}	
	
	/**
	 * This function recursivly sets the Vertex and its desendents to be visible or invisible
	 * based on the int; If int is 0 the node will collapse, else it will expand
	 *
	 * @param targetNode node to collapse or expand
	 * @param expandOrCollapse value indicating to expand or collapse the node
	 */	
	public void recurseOnNode(Vertex targetNode, int expandOrCollapse)
	{
		Vertex node;
		
		// Hide the children of expandOrContractNode children
		for (Enumeration e =targetNode.children(); e.hasMoreElements(); )
		{
			node = (Vertex) e.nextElement();
			
			if (expandOrCollapse == 0)
			{
				//set the child to be invisible
				node.setIsVisible(false);
			} 
			else if (expandOrCollapse == 1)
			{
				// set the child to be visible
				node.setIsVisible(true);
			}
			if (node.getShouldCollapse() == false)
			{
				// if the child has its own children
				if (!node.isLeaf())
				{
					// recursively set the child's child to be invisible
					recurseOnNode(node, expandOrCollapse);
				}
			}
			
		}
	}
	/**
	 * This function changes the mapByDefn variable to false and calls the repaint method
	 */	
	public void selectedDefnMapping()
	{
		//	JOptionPane.showMessageDialog(null,"hirra hurrah","title", JOptionPane.PLAIN_MESSAGE);	
		mapByDefnShow = true;
		repaint();
	}		
	/**
	 * This function selects all the nodes between the first and second argument
	 *
	 * @param y1	the first y location of the mouseclick
	 * @param y2	the second y location of the mouseclick
	 * @param x 	the x location of the mousclick (used to determine if we should use global or local)
	 */
	public void selectNodes(int y1, int y2, int x)
	{
		int temp;
		Vertex root, node;
		
		// if the user clicked on the left half of the screen then get the global root,
		// get the local root
		if (x < (canvasWidth/2)) root = getGlobalTreeRoot();
		else root = getLocalTreeRoot();
		
		if (root == null) return;
		
		if (y1 > y2){
			// swap y1 and y2
			temp = y1;
			y1 = y2-25;//25 has to be subtracted, to allow it to select the node 
			y2 = temp;
		}
		
		globalNodesSelected.clear();
		localNodesSelected.clear();
		
		for (Enumeration e = root.preorderEnumeration(); e.hasMoreElements() ;){
			// get the node
			node = (Vertex) e.nextElement();
			//TODO: decide if node.isVisible() should be used or not here... i have removed it from the 
			//if condition given below
			if (node.getY() >= y1 && node.getY() <= y2){
				setNodeSelected(node,true);
				if ((node.getNodeType() == GSM.SOURCENODE))
					globalNodesSelected.addElement(node);
				else if ((node.getNodeType() == GSM.TARGETNODE))
					localNodesSelected.addElement(node);
			}
		}
	}		
	
	public void setDisplayedLines(int lines) {
		noOfLines = lines;
	}
	
	/**
	 * Set the minimum value of similarity to be displayed different from the calculated one (threshold).
	 * @param val minimum value
	 */
	public void setDisplayedSimilarity(int val) {
		displayedSimilarity = val;
	}
	
	/**
	 * This function sets the global tree root
	 *
	 * @param node global tree root of type Vertex
	 */
	public void setGlobalTreeRoot(Vertex node)
	{
		globalTreeRoot = node;
	}
	/**
	 * This function sets the local tree root
	 *
	 * @param node local tree root of type Vertex
	 */
	public void setLocalTreeRoot(Vertex node)
	{
		localTreeRoot = node;
	}
	public void setMapByDefn(boolean flag)
	{
		mapByDefn = flag ;
	}	
	/**
	 * This function sets the mapByUser variable based on the argument
	 *
	 * @param userMapping boolean indicating that the usermapping is checked
	 */
	public void setMapByUser(boolean userMapping)
	{
		mapByUser = userMapping;
		
		// repaint the canvas based on the mapByUser
		repaint();
	}
	/**
	 * @param node
	 * @param selected
	 */
	public void setNodeSelected(Vertex node, boolean selected){
		node.setIsSelected(selected);
		if(selected){
			((VertexDescriptionPane)(myUI.getDescriptionPanel())).fillDescription(node);
		}else{
			((VertexDescriptionPane)(myUI.getDescriptionPanel())).clearDescription(node);
		}
	}
	/**
	 * This function sets the oldY
	 *
	 * @param y 	previous y location on canvas
	 */
	public void setOldY(int y)
	{
		oldY = y;
	}

	/**
	 * This function sets the rightClickedNode
	 *
	 * @param node 	right clicked vertex
	 */
	public void setRightClickedNode(Vertex node)
	{
		rightClickedNode = node;
	}
	/**
	 * @param root
	 */
	public void showAll(Vertex root)
	{
		performShowAll(root);
		
	}

	
	public void clearContextMapping() {
		// do a preorder transversal, and clear the context mapping for each node
		// on the globalroot first, then the localroot
		
		globalTreeRoot.clearContextMapping();
		for( Enumeration<Vertex> e = globalTreeRoot.preorderEnumeration(); e.hasMoreElements();) {
			e.nextElement().clearContextMapping();
		}
		
		
		localTreeRoot.clearContextMapping();
		for( Enumeration<Vertex> e = localTreeRoot.preorderEnumeration(); e.hasMoreElements();) {
			e.nextElement().clearContextMapping();
		}
		
	}
	
	
	/**
	 * This function will clear any definition mapping that was previously calculated
	 */
	public void clearDefinitionMapping() {
		
		// start at the global root
		clearDefinitionMapping(globalTreeRoot);
		clearDefinitionMapping(localTreeRoot);

	}

	/**
	 * This function will clear the definition mapping of the parent
	 * and recursively, the definition of its childen
	 * @param parent
	 * @author cosmin
	 * @date Oct 12, 2008
	 */
	private void clearDefinitionMapping( Vertex parent ) {

		// we have some sort of definition mapping here
		if(parent != null) {
			parent.clearDefnMapping();
			
			
			if( parent.isLeaf() ) {
				return;
			}
			
			Vertex child = null;
			for( Enumeration<Vertex> e = parent.children(); e.hasMoreElements(); ) {
				// iterate through this node's children
				child = e.nextElement();
				clearDefinitionMapping( child ); // clear the definition of the child
			}
		}


		
	}

}

