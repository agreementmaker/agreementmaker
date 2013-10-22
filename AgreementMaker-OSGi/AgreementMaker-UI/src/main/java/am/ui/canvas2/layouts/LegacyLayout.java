package am.ui.canvas2.layouts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.ui.UICore;
import am.ui.canvas2.Canvas2;
import am.ui.canvas2.graphical.GraphicalData;
import am.ui.canvas2.graphical.GraphicalData.NodeType;
import am.ui.canvas2.graphical.MappingData;
import am.ui.canvas2.graphical.RectangleElement;
import am.ui.canvas2.graphical.TextElement;
import am.ui.canvas2.layouts.legacylayout.LegacyLayoutMouseHandler;
import am.ui.canvas2.nodes.GraphicalNode;
import am.ui.canvas2.nodes.LegacyEdge;
import am.ui.canvas2.nodes.LegacyMapping;
import am.ui.canvas2.nodes.LegacyNode;
import am.ui.canvas2.popupmenus.CreateMappingMenu;
import am.ui.canvas2.popupmenus.DeleteMappingMenu;
import am.ui.canvas2.utility.Canvas2Edge;
import am.ui.canvas2.utility.Canvas2Layout;
import am.ui.canvas2.utility.Canvas2Vertex;
import am.ui.canvas2.utility.CanvasGraph;
import am.ui.canvas2.utility.GraphLocator;
import am.ui.canvas2.utility.GraphLocator.GraphType;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;

import com.hp.hpl.jena.enhanced.UnsupportedPolymorphismException;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.ProfileException;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * This layout is resposible for placement of nodes and edges.
 * The layout algorithm is the same one as the original Canvas class.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */

public class LegacyLayout extends Canvas2Layout implements PopupMenuListener {
	
	private final Logger log = Logger.getLogger(LegacyLayout.class);
	
	public static final int VIEW_NORMAL = 0;
	public static final int VIEW_SINGLE_MAPPING = 1;
	
	/* FLAGS, and SETTINGS */
	protected boolean showLocalName = true;
	protected boolean showLabel     = false;
//	private String  language      = "EN";
	protected String  labelAndNameSeparator = " || ";

	protected boolean[] pixelColumnDrawn;  // used for a very special hack in LegacyEdge.draw();  Read about it in that method.
									     // It's done in order to avoid unnecessary draws() and speed up the paint() function. 
	protected Rectangle pixelColumnViewport;  // required to know the correct index in the pixelColumnDrawn array.

	/**
	 * An array of hashmaps that translate Jena OntResource object into AgreementMaker LegacyNode object for each individual ontology. 
	 * Used in the graph building to avoid visiting the same nodes twice, and as a general purpose lookup to translate from Node to LegacyNode.
	 * 
	 * We cannot use one hashmap for all ontologies because there can exist nodes in two separate ontologies that hash to the same key, therefore
	 * they would be considered identical in the hashmap.  Therefore we are left with the hope that every OntResource in one ontology will 
	 * hash to a unique key, even if this guarantee does not hold across multiple ontologies.
	 */
	protected ArrayList<Pair<Integer,HashMap<OntResource,LegacyNode>>> ConceptHashMaps; 

	//private HashMap<OntResource,LegacyNode> hashMap; // used in the graph building to avoid visiting the same nodes twice 
	protected LegacyNode anonymousNode;
	
	//private Dimension oldViewportDimensions;  // this variable is used in the stateChanged handler.

	/**
	 * Graph Building Variables.
	 */
	
	protected int subgraphXoffset = 20;
	protected int subgraphYoffset = 20;
	protected int depthIndent = 20;
	protected int marginBottom = 5;
	protected int nodeHeight = 20;
	
	protected int leftGraphX = 20;
	protected int leftGraphY = 42;
	protected int rightGraphX = 500;
	protected int rightGraphY = 42;
	protected int middleDividerLeftMargin = 10;
	protected int middleDividerWidth = 1;
	protected int topDividerTopMargin = 20;
	
	protected GraphicalNode middleDivider;
	protected GraphicalNode topDivider;
	protected GraphicalNode sourceOntologyText;
	protected GraphicalNode targetOntologyText;
	//private OntClass owlThing;
	
	protected boolean leftSideLoaded = false; 
	protected boolean rightSideLoaded = false;
	protected boolean leftSide;
	
	protected int leftOntologyID = Ontology.ID_NONE;  // the ontology ID of the graphs on the left side of the canvas layout
	protected int rightOntologyID = Ontology.ID_NONE; // the ontology ID of the graphs on the right side of the canvas layout 

	
	/** Mouse Event handlers and Variables */
	
	protected LegacyLayoutMouseHandler mouseHandler = new LegacyLayoutMouseHandler(this);
	
	protected ArrayList<LegacyNode> selectedNodes;  // the list of currently selected nodes
	protected boolean PopupMenuActive = false;

	protected boolean SingleMappingView = false; 	// this is used when a mapping is doubleclicked with the left mouse button
												// in order to show only that specific mapping
	protected ArrayList<LegacyMapping> MovedMappings = new ArrayList<LegacyMapping>(); // we need to keep a list of the mappings we change for the SingleMappingView
	protected ArrayList<LegacyNode> SingleMappingMovedNodes = new ArrayList<LegacyNode>(); // we need to keep a list of the nodes we moved
	
	
	public LegacyLayout() {
		super();
		ConceptHashMaps = new ArrayList<Pair<Integer,HashMap<OntResource,LegacyNode>>>();
		
		layoutArtifactGraph = buildArtifactGraph();  // build the artifact graph
		selectedNodes = new ArrayList<LegacyNode>(); 	
	}
	
	public LegacyLayout(Canvas2 vp) {
		super(vp);
		ConceptHashMaps = new ArrayList<Pair<Integer,HashMap<OntResource,LegacyNode>>>();
		//hashMap = new HashMap<OntResource, LegacyNode>();
		//oldViewportDimensions = new Dimension(0,0);
		
		layoutArtifactGraph = buildArtifactGraph();  // build the artifact graph
		selectedNodes = new ArrayList<LegacyNode>(); 		

	}		
	
	/**
	 * This function sets up the pixel column array.
	 */
	@Override
	public void getReadyForRepaint(Rectangle viewport) {
		pixelColumnDrawn = new boolean[viewport.width];  // the array has one entry for every pixel column of the viewport.  Values initialize to false.
		pixelColumnViewport = viewport;
	}
	/**
	 * When a column of the viewport has been drawn in, set the corresponding array entry to true.
	 * That way, it doesn't get draw by every edge that shares that column.
	 */
	public void setPixelColumnDrawn(int canvasColNum) {	
		int viewportColNum = canvasColNum - pixelColumnViewport.x - 1;  // translate the canvasColNum to an array index
		pixelColumnDrawn[viewportColNum] = true; /* column has been filled in. */ 
	}
	public boolean isPixelColumnDrawn(int canvasColNum) {  // LegacyEdge needs to know if a column has been drawn already.
		int viewportColNum = canvasColNum - pixelColumnViewport.x - 1;
		
		if (viewportColNum < 0 || pixelColumnDrawn.length <= viewportColNum){
			log.warn("Pixel column drawn array index is invalid.");
			return true;
		}
		return pixelColumnDrawn[viewportColNum];
		
	}
	public Rectangle getPixelColumnViewport() { return pixelColumnViewport; } // edge needs to know the viewport
	
	
	/**
	 * Utility Function. This function is called by the LegacyNodes in order to set their text label correctly.
	 */
	@Override
	public String getNodeLabel(GraphicalData d ) {
		
		if( d.r == null ) {
			if( d.type == NodeType.TEXT_ELEMENT ) return ((TextElement)d).getText();
			return "";
		}
		
		if(showLabel && showLocalName)
			return d.r.getLocalName() + labelAndNameSeparator + d.r.getLabel(null);
		else if(showLabel)
			return d.r.getLabel(null);
		else if(showLocalName) 
			return d.r.getLocalName(); 
		else 
			return "";
	}

	/* ****************** Getters and Setters **************************** */
	@Override public void setShowLabel(boolean shL ) { showLabel = shL; }
	@Override public void setShowLocalName( boolean shLN ) { showLocalName = shLN; }
	@Override public boolean getShowLabel() { return showLabel; }
	@Override public boolean getShowLocalName() { return showLocalName; }
	public Canvas2 getVizPanel() { return vizpanel; }
	public boolean isPopupMenuActive() { return PopupMenuActive; }
	public void setPopupMenuActive( boolean v ) { PopupMenuActive = v; }
	public Canvas2Vertex getHoveringOver() { return hoveringOver; }
	public void setHoveringOver(Canvas2Vertex vertex) { hoveringOver = vertex; }
	@Deprecated public boolean isSingleMappingView() { return SingleMappingView; } // Deprecated in favor of isViewActive(LegacyLayout.VIEW_SINGLE_MAPPING)
	public ArrayList<LegacyNode> getSelectedNodes() { return selectedNodes; }
	public int getLeftOntologyID() { return leftOntologyID; }
	public int getRightOntologyID() { return rightOntologyID; }
	
	public int getDepthIndent() { return depthIndent; }  // * Getter.
	
	@Override
	public boolean canDisplayMoreOntologies() {
		if( leftSideLoaded && rightSideLoaded ) { return false; }
		return true;
	}
	
	@Override
	public void displayOntology( ArrayList<CanvasGraph> graphRepository, int ontologyID) {
		//TODO: Do graph positioning here, depending on which side is loaded into the layout.
		//      Right now we assume the user will load source then target, in that order.
		
		// update the loaded info
		if( leftSideLoaded == false ) {
			leftSideLoaded = true;
			leftOntologyID = ontologyID;
		}
		else if( rightSideLoaded == false ) {
			rightSideLoaded = true;
			rightOntologyID = ontologyID;
		}
		
		// show the graphs
		ArrayList<CanvasGraph> gr = GraphLocator.getGraphsByID(graphRepository, ontologyID);
		Iterator<CanvasGraph> graphIter = gr.iterator();
		while( graphIter.hasNext() ) {
			graphIter.next().setVisible(true);
		}
			
	}
	
	@Override
	public void removeOntology(  ArrayList<CanvasGraph> graphs, int ontologyID ) {
		
		for( int i = graphs.size() - 1; i >= 0; i-- ) {
			CanvasGraph gr = graphs.get(i);
			if( gr.getGraphType() == GraphLocator.GraphType.MATCHER_GRAPH || gr.getID() == ontologyID ) {
				gr.detachEdges();  // we must detach the graph from other visible graphs before removing. 
				graphs.remove(i);
			}
			
		}
		
		if( leftOntologyID == ontologyID ) { leftOntologyID = Core.ID_NONE; leftSideLoaded = false; }
		if( rightOntologyID == ontologyID ) { rightOntologyID = Core.ID_NONE; rightSideLoaded = false; }
		
		
		// Remove the HashMap linked to the ontologyID
		Iterator<Pair<Integer,HashMap<OntResource,LegacyNode>>> pairIter = ConceptHashMaps.iterator();
		while( pairIter.hasNext() ) {
			Pair<Integer,HashMap<OntResource,LegacyNode>> pair = pairIter.next();
			if( pair.getLeft().intValue() == ontologyID ) {
				// this Pair should be removed from the arraylist
				ConceptHashMaps.remove(pair);
				break;
			}
		}
		
	}
	
/*
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 ************************************************ GRAPH BULDING METHODS **********************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************	
 */
	
	/**
	 * This function will build the global graph for an ontology (classes graph, properties graph, all under one global graph).
	 * It does different things for different types of ontologies (OWL,RDFS,XML), but in the end it should all be under the
	 * main ontology graph, which is the return value. 
	 * 
	 * This includes
	 * 		- build the Class graph.
	 * 		- build the Properties graph.
	 *		- Individuals? TODO
	 *
	 *	@param ont The Ontology that we are building the visualization graphs for.
	 *
	 */
	@Override
	public List<CanvasGraph> buildGlobalGraph( Ontology ont ) {
		
		ArrayList<CanvasGraph> ontologyGraphs = new ArrayList<CanvasGraph>();
		
		// Before we build the graph, update the preferences.
		showLabel = Core.getAppPreferences().getShowLabel();
		showLocalName = Core.getAppPreferences().getShowLocalname();
		
		if( !leftSideLoaded )	// source goes on the left.
			leftSide = true;
		else if( !rightSideLoaded )
			leftSide = false;
		else
			return ontologyGraphs;  // we have ontologies loaded on both sides, do nothing for now.
		
		if( leftSide ) {  // we're loading a graph on the left side of the canvas
			subgraphXoffset = leftGraphX + depthIndent;
			subgraphYoffset = leftGraphY + nodeHeight + marginBottom;
		} else {  // we're loading a graph on the right side of the canvas.
			rightGraphX = middleDivider.getObject().x + 10;
			subgraphXoffset = rightGraphX + depthIndent;
			subgraphYoffset = rightGraphY + nodeHeight + marginBottom;
		}
		
		/*
		if( ont.getLanguage().equals("XML") ) {
			// build the visualization from an XML ontology
			// an XML ontology will only have classes.
			
			CanvasGraph classesGraph = new CanvasGraph( GraphLocator.GraphType.CLASSES_GRAPH, ont.getID() );
			
			Vertex classRootVertex = ont.getClassesTree();
			
			// Create the root LegacyNode in order to call the recusive method correctly
			TextElement gr = new TextElement(0*depthIndent + subgraphXoffset, 
											 classesGraph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
											 0, nodeHeight, this, classesGraph.getID() );
			gr.setText("XML Classes Hierarchy");
			LegacyNode classRootNode = new LegacyNode( gr );
			classesGraph.insertVertex(classRootNode);
						
			recursiveBuildClassGraphXML( classRootVertex, classRootNode, classesGraph, 1 );
			
			
			
			
			
		} else */
		// if ( ont.getLanguage().equals("OWL") || ont.getLanguage().endsWith("XML") ) {
		OntModel m = ont.getModel();
/*		
		if( m == null ) {
			// this is an ontology that is not loaded by jena
		} else {
			owlThing = m.getOntClass( OWL.Thing.getURI() );
		}
*/		
		
		CanvasGraph classesGraph = new CanvasGraph( GraphLocator.GraphType.CLASSES_GRAPH, ont.getID() );
		anonymousNode = new LegacyNode( new GraphicalData(0, 0, 0, 0, GraphicalData.NodeType.FAKE_NODE, this, ont.getID() )); // TODO: Anonymous nodes should be displayed!
		
		// Create a new HashMap for the new Ontology
		HashMap<OntResource,LegacyNode> newHashMap = new HashMap<OntResource,LegacyNode>();
		Integer ontIntID = new Integer(ont.getID());
		Pair<Integer, HashMap<OntResource,LegacyNode>> newPair = new Pair<Integer, HashMap<OntResource,LegacyNode>>(ontIntID,newHashMap);
		ConceptHashMaps.add(newPair);
		
		LegacyNode classesRoot = buildClassGraph( m, classesGraph, ont, newHashMap );  // build the class graph here
		
		// update the offsets to put the properties graph under the class graph.
		if( leftSide ) {
			subgraphYoffset = classesGraph.getBounds().y + classesGraph.getBounds().height + nodeHeight + marginBottom;
		} else {
			subgraphYoffset = classesGraph.getBounds().y + classesGraph.getBounds().height + nodeHeight + marginBottom;
		}
		
		CanvasGraph propertiesGraph = new CanvasGraph( GraphLocator.GraphType.PROPERTIES_GRAPH, ont.getID() );
		
		LegacyNode propertiesRoot = buildPropertiesGraph(m, propertiesGraph, ont, newHashMap);  // and the properties graph here
	
		CanvasGraph globalGraph = buildOntologyGraph(classesRoot, propertiesRoot, ont);  // and put them all under a global graph
		
		int deepestY = 0;
		if( (classesGraph.getBounds().y + classesGraph.getBounds().height) > (propertiesGraph.getBounds().y+propertiesGraph.getBounds().height) )
			deepestY = classesGraph.getBounds().y + classesGraph.getBounds().height;
		else
			deepestY = propertiesGraph.getBounds().y+propertiesGraph.getBounds().height;
		
		int rightmostX = 0;
		if( (classesGraph.getBounds().x + classesGraph.getBounds().width) > (propertiesGraph.getBounds().x + propertiesGraph.getBounds().width) )
			rightmostX = classesGraph.getBounds().x + classesGraph.getBounds().width;
		else
			rightmostX = propertiesGraph.getBounds().x + propertiesGraph.getBounds().width;

		
		updateArtifactGraph(deepestY, rightmostX , leftSide);
		
		// add all the graphs created to the ontologyGraphs in the Canvas2.
		ontologyGraphs.add(classesGraph);
		ontologyGraphs.add(propertiesGraph);
		ontologyGraphs.add(globalGraph);
		

		return ontologyGraphs;
	}
	
	/**
	 * Called from the LegacyLayout constuctor.
	 * This function builds the graph that displays this layouts Artifacts:  
	 * 		- The middle divider
	 * 		- the top divider
	 * 		- the source ontology text  (on the left of the canvas)
	 * 		- the target ontology text  (on the right of the canvas)
	 */
	private CanvasGraph buildArtifactGraph() {
		
		CanvasGraph artifactGraph = new CanvasGraph(GraphLocator.GraphType.LAYOUT_GRAPH_IGNORE_BOUNDS, Ontology.ID_NONE);
		
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  // This is for treating multiple screens as one big screen.
		
		// This code gets the individual screen size even if there are multiple screens.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		Dimension screenSize = new Dimension( gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight() );
		
		// MIDDLE DIVIDER
		RectangleElement dividerData = new RectangleElement(screenSize.width/2, 0, middleDividerWidth, screenSize.height , this, Ontology.ID_NONE );
		middleDivider = new GraphicalNode( dividerData );
		
		// TOP DIVIDER
		RectangleElement topDividerData = new RectangleElement(0, topDividerTopMargin, screenSize.width, 1, this, Ontology.ID_NONE);
		topDivider = new GraphicalNode( topDividerData );
		
		// LEFT TEXT LABEL
		TextElement sourceOntologyData = new TextElement( 10, 15, 10, 20, this, Ontology.ID_NONE );
		sourceOntologyData.setBold(true);
		sourceOntologyData.setText("Source Ontology");
		sourceOntologyText = new GraphicalNode(sourceOntologyData);
		
		// RIGHT TEXT LABEL
		TextElement targetOntologyData = new TextElement( screenSize.width/2 + middleDividerWidth + 10, 15, 10, 20, this,Ontology.ID_NONE);
		targetOntologyData.setBold(true);
		targetOntologyData.setText("Target Ontology");
		targetOntologyText = new GraphicalNode(targetOntologyData);
		
		artifactGraph.insertVertex(middleDivider);
		artifactGraph.insertVertex(topDivider);
		artifactGraph.insertVertex(sourceOntologyText);
		artifactGraph.insertVertex(targetOntologyText);
		
		return artifactGraph;
	}
	
	/**
	 * Update the position of the middle divider according to this globalGraph.
	 */
	private void updateArtifactGraph(int deepestY, int rightmostX, boolean leftSide ) {
		// TODO: Make this method not depend on any arguments.
		
		if( vizpanel.getViewport() == null ) return;
		
		// update the x position of the middle divider
		Rectangle viewportDim = vizpanel.getViewport().getBounds();
		Rectangle vizpanelDim = vizpanel.getBounds();
		
		int dividerX = viewportDim.width/2;
		if( leftSide && rightmostX > dividerX )
			dividerX = rightmostX + middleDividerLeftMargin;  // move the divider over to the right so it doesn't overlap the graph
		
		// update the height of the middle divider
		int dividerH = deepestY;
		if( dividerH < vizpanelDim.height ) dividerH = vizpanelDim.height;
		if( dividerH < viewportDim.height ) dividerH = viewportDim.height;
		
		RectangleElement dividerData = new RectangleElement(dividerX, 0, middleDividerWidth, dividerH + vizpanel.Ypadding, this, Ontology.ID_NONE );		
		middleDivider.setObject(dividerData);

		// move the target ontology text in relation to the middle divider
		targetOntologyText.getObject().x = dividerX + middleDividerWidth + 10;  // move the "Target Ontology" text with the divider
				
		
		// update the width of the top divider
		// NOTE!: There's a condition that falls through here: when leftside == true and rightside == true.
		if( (leftSide && !rightSideLoaded) || !leftSide ) { // loading an ontology on the left side
			if( (rightmostX) > (viewportDim.x + viewportDim.width) ) {
				topDivider.getObject().width = rightmostX + vizpanel.Xpadding;
			} else {
				topDivider.getObject().width = viewportDim.x + viewportDim.width + vizpanel.Xpadding;
			}
		}
		
		layoutArtifactGraph.recalculateBounds();
		
	}
	
	
	/**
	 * This function puts the classes and properties graphs under one global root node
	 * @param classesGraph
	 * @param propertiesGraph
	 * @return
	 */
	protected CanvasGraph buildOntologyGraph(LegacyNode classesRoot,
			LegacyNode propertiesRoot, Ontology ont) {
		GraphicalData gr = classesRoot.getObject();
		if( gr == null ) return null;
		
		CanvasGraph globalGraph = new CanvasGraph(GraphLocator.GraphType.GLOBAL_ROOT_GRAPH, ont.getID());
		
		TextElement rootData = null;
		if( leftSide ) rootData = new TextElement( leftGraphX, leftGraphY, 0, nodeHeight, 
					   this, ont.getID() );
		else           rootData = new TextElement( rightGraphX, rightGraphY, 0, nodeHeight, 
					   this, ont.getID() );
		
		rootData.setText( ont.getTitle() );
	
		LegacyNode globalRoot = new LegacyNode(rootData);
		
		LegacyEdge globalroot2classesroot = new LegacyEdge( globalRoot, classesRoot, null, this ); 
		LegacyEdge globalroot2propertiesroot = new LegacyEdge( globalRoot, propertiesRoot, null, this );
		
		globalGraph.insertVertex(globalRoot);
		globalGraph.insertEdge(globalroot2classesroot);
		globalGraph.insertEdge(globalroot2propertiesroot);
		
		return globalGraph;
	}
	
	
	/**
	 * This function and the recursive version build the class graph.
	 * 
	 * The reason I split it into two functions is because the first level of recursion has to call 
	 * OntTools.namedHierarchyRoots(m) while the rest of the levels use superClass.listSubClasses().
	 * 
	 * Otherwise, the two functions are quite similar.
	 * 
	 * They both add to the graph, and build it up.
	 * 
	 */
	protected LegacyNode buildClassGraph( OntModel m, CanvasGraph graph, Ontology ont, HashMap<OntResource,LegacyNode> hashMap ) {

		Logger log = null;
		if( Core.DEBUG ) {
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
		}
		
		int depth = 0;
		
		// create the root node;
		TextElement gr = new TextElement(depth*depthIndent + subgraphXoffset, 
										 graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
										 0, nodeHeight, this, graph.getID() );
		gr.setText("OWL Classes Hierarchy");
		LegacyNode root = new LegacyNode( gr );
		
		graph.insertVertex(root);
		
		Node classRoot = ont.getClassesRoot();
		List<Node> rootChildren = classRoot.getChildren();
		for( int i = 0; i < rootChildren.size(); i++ ) {
			Node currentChild = rootChildren.get(i);
			recursiveBuildClassGraph( root, currentChild, depth+1, graph, ont, hashMap );
		}
		
		/*//List<OntClass> classesList = OntTools.namedHierarchyRoots(m);
		List<OntClass> classesList = getClassHierarchyRoots(m);
		
		depth++;
		Iterator<OntClass> clsIter = classesList.iterator();
		while( clsIter.hasNext() ) {
			OntClass cls = clsIter.next();  // get the current child
			
			if( cls.isAnon() ) {  // if it is anonymous, don't add it, but we still need to recurse on its children
				hashMap.put(cls, anonymousNode);  // avoid cycles between anonymous nodes
				if( Core.DEBUG ) log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
				recursiveBuildClassGraph(root, cls, depth, graph, ont, hashMap);
				continue; 
			} else if( cls.equals(OWL.Nothing) )   // if it's OWL.Nothing (i.e. we recursed to the bottom of the hierarchy) skip it.
				continue;
			
			// cycle check at the root
			if( hashMap.containsKey(cls) ) { // we have seen this node before, do NOT recurse again
				if( Core.DEBUG ) log.debug("Cycle detected.  OntClass:" + cls );
				continue;
			}
			
			// the child class is not anonymous or OWL.Nothing, add it to the graph, with the correct relationships
			GraphicalData gr1 = new GraphicalData( depth*depthIndent + subgraphXoffset, 
										           graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
										           100, nodeHeight, cls, GraphicalData.NodeType.CLASS_NODE, this, graph.getID() );
			LegacyNode node = new LegacyNode( gr1);
			
			try {
				// Try to connect this graphical represenation of an Ontology Class to the Node object that represents that class.
				Node amnode = ont.getNodefromOntResource(cls, alignType.aligningClasses);
				amnode.addGraphicalRepresentation(node);
			} catch (Exception e) {
				// An exception has been thrown by getNodefromOntResource().
				// This means that the OntClass was not found, therefore we cannot connect this LegacyNode to a Node object.
				if( Core.DEBUG ) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			
			graph.insertVertex( node );
			LegacyEdge edge = new LegacyEdge( root, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put( cls, node);
			if( Core.DEBUG ) log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
			recursiveBuildClassGraph( node, cls, depth+1, graph, ont, hashMap );
			
		}*/
	
		
		
		return root;
	}
	
	protected void recursiveBuildClassGraph(LegacyNode parentLNode, Node currentNode, int depth, CanvasGraph graph, Ontology ont, HashMap<OntResource, LegacyNode> hashMap ) {
		
		// create the LegacyNode for the current class.
		
		// get the OntResource for the current node.
		OntResource currentResource = null;
		try {
			currentResource = currentNode.getResource().as(OntResource.class);
		} catch( UnsupportedPolymorphismException e ) {
			e.printStackTrace();
		}
		
		// create the graphical data and the legacy node
		GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
				   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
				   100 , nodeHeight, currentResource, GraphicalData.NodeType.CLASS_NODE, this, graph.getID() ); 
		LegacyNode currentLNode = new LegacyNode(gr);
		
		UICore.getInstance().addGraphicalRepresentation(currentNode, currentLNode);
		
		// insert the vertex and the edge in to the graph.
		graph.insertVertex(currentLNode);
		
		LegacyEdge edge = new LegacyEdge( parentLNode, currentLNode, null, this );
		graph.insertEdge( edge );
		
		hashMap.put(currentResource, currentLNode);
		
		// recursive call for the children.
		List<Node> childrenNodes = currentNode.getChildren();
		for( int i = 0; i < childrenNodes.size(); i++ ) {
			Node currentChild = childrenNodes.get(i);
			recursiveBuildClassGraph( currentLNode, currentChild, depth+1, graph, ont, hashMap);
		}
		
	}
	
	protected void recursiveBuildClassGraph(
			LegacyNode parentNode,
			OntClass parentClass,  // this has to be passed because of anonymous classes and the special root node
			int depth, 
			CanvasGraph graph,
			Ontology ont,
			HashMap<OntResource, LegacyNode> hashMap) {

		
		
		Logger log = null;
		if( Core.DEBUG ) { 
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
			log.debug(parentClass);
		}
		
		try { 
			ExtendedIterator<?> clsIter = parentClass.listSubClasses(true);
			while( clsIter.hasNext() ) {
				OntClass cls = (OntClass) clsIter.next();
				if( cls.isAnon() ) {
					hashMap.put(cls, anonymousNode);  // avoid cycles between anonymous nodes
					if( Core.DEBUG ) log.debug(">> Inserted anonymous node " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
					recursiveBuildClassGraph( parentNode, cls, depth, graph, ont, hashMap );
					continue;
				} else if( cls.equals( OWL.Nothing ) ) 
					continue;
	
				// this is the cycle check
				if( hashMap.containsKey(cls) ) { // we have seen this node before, do NOT recurse again
					if( Core.DEBUG ) log.debug("Cycle detected.  OntClass:" + cls );
					continue;
				}
				
				GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
													   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
													   100 , nodeHeight, cls, GraphicalData.NodeType.CLASS_NODE, this, graph.getID() ); 
				LegacyNode node = new LegacyNode( gr);
				
				try {
					// Try to connect this graphical represenation of an Ontology Class to the Node object that represents that class.
					Node amnode = ont.getNodefromOntResource(cls, alignType.aligningClasses);
					UICore.getInstance().addGraphicalRepresentation(amnode, node);
				} catch (Exception e) {
					// An exception has been thrown by getNodefromOntResource().
					// This means that the OntClass was not found, therefore we cannot connect this LegacyNode to a Node object.
					if( Core.DEBUG ) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
				
				graph.insertVertex(node);
					
				LegacyEdge edge = new LegacyEdge( parentNode, node, null, this );
				graph.insertEdge( edge );
				
				hashMap.put(cls, node);
				if( Core.DEBUG ) {
					log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
					log.debug(">>   Label: " + cls.getLabel(null));
				}
				recursiveBuildClassGraph( node, cls, depth+1, graph, ont, hashMap );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	
	}


	/**
	 * This function, and the recursive version build the properties graph.  It's a copy of the Class building methods
	 * 
	 * The reason I split it into two functions is because the first level of recursion has to
	 * find the roots of the properties hierarchy, while the rest of the levels use listSubProperties() 
	 * 
	 * Otherwise, the two functions are quite similar.
	 * 
	 * They both add to the graph, and build it up.
	 * 
	 */
	private LegacyNode buildPropertiesGraph( OntModel m, CanvasGraph graph, Ontology ont, HashMap<OntResource, LegacyNode> hashMap ) {

		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		
		int depth = 0;
		
		// create the root node;
		/*
		GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
											  graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
											  GraphicalData.PROPERTIES_ROOT_NODE_WIDTH, nodeHeight, 
											  GraphicalData.NodeType.PROPERTIES_ROOT, this, Core.getInstance().getOntologyIDbyModel(m) );
		*/
		TextElement gr = new TextElement(depth*depthIndent + subgraphXoffset, 
				 		 graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
				         0, nodeHeight, this, graph.getID() );
		gr.setText("OWL Properties Hierarchy"); 
		LegacyNode root = new LegacyNode( gr );
		
		graph.insertVertex(root);
		
		List<OntProperty> propertiesList = getPropertyHierarchyRoots(m);
		
		depth++;
		Iterator<OntProperty> propIter = propertiesList.iterator();
		while( propIter.hasNext() ) {
			OntProperty prop = propIter.next();  // get the current child
			if( prop.isAnon() ) {  // if it is anonymous, don't add it, but we still need to recurse on its children
				hashMap.put(prop, anonymousNode);  // avoid cycles between anonymous nodes
				recursiveBuildPropertiesGraph(root, prop, depth, graph, ont, hashMap);
				continue; 
			} else if( prop.equals(OWL.Nothing) )   // if it's OWL.Nothing (i.e. we recursed to the bottom of the heirarchy) skip it.
				continue;
			
			// this is the cycle check
			if( hashMap.containsKey(prop) ) { // we have seen this node before, do NOT recurse again
				log.debug("Cycle detected.  OntProperty:" + prop );
				continue;
			}
			
			// so the child property is not anonymous or OWL.Nothing, add it to the graph, with the correct relationships
			GraphicalData gr1 = new GraphicalData( depth*depthIndent + subgraphXoffset, 
										           graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
										           0, nodeHeight, prop, GraphicalData.NodeType.PROPERTY_NODE, this, graph.getID() );
			LegacyNode node = new LegacyNode( gr1);
			
			try {
				// Try to connect this graphical represenation of an Ontology Class to the Node object that represents that class.
				Node amnode = ont.getNodefromOntResource(prop, alignType.aligningProperties);
				UICore.getInstance().addGraphicalRepresentation(amnode, node);
			} catch (Exception e) {
				// An exception has been thrown by getNodefromOntResource().
				// This means that the OntClass was not found, therefore we cannot connect this LegacyNode to a Node object.
				if( Core.DEBUG ) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			
			graph.insertVertex( node );
			LegacyEdge edge = new LegacyEdge( root, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put( prop, node);
			recursiveBuildPropertiesGraph( node, prop, depth+1, graph, ont, hashMap );
			
		}
		
		return root;
	}

	private void recursiveBuildPropertiesGraph(
			LegacyNode parentNode,
			OntProperty parentProperty,  // this has to be passed because of anonymous classes and the special root node
			int depth, 
			CanvasGraph graph,
			Ontology ont,
			HashMap<OntResource, LegacyNode> hashMap) {

		Logger log = null;
		
		if( Core.DEBUG ) {
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
			log.debug(parentProperty);
		}
		
		ExtendedIterator<?> clsIter = null;
		try { 
			clsIter = parentProperty.listSubProperties(true);
		} catch (ConversionException e ){
			e.printStackTrace();
			return;
		}
		
		while( clsIter.hasNext() ) {
			OntProperty prop = (OntProperty) clsIter.next();
			if( prop.isAnon() ) {
				hashMap.put(prop, anonymousNode);  // avoid cycles between anonymous nodes
				recursiveBuildPropertiesGraph( parentNode, prop, depth, graph, ont, hashMap );
				continue;
			} else if( prop.equals( OWL.Nothing ) ) 
				continue;

			// this is the cycle check
			if( hashMap.containsKey(prop) ) { // we have seen this node before, do NOT recurse again
				if( Core.DEBUG ) log.debug("Cycle detected.  OntProperty:" + prop );
				continue;
			}
			
			GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
												   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
												   100 , nodeHeight, prop, GraphicalData.NodeType.PROPERTY_NODE, this, graph.getID() ); 
			LegacyNode node = new LegacyNode( gr);
			
			try {
				// Try to connect this graphical represenation of an Ontology Class to the Node object that represents that class.
				Node amnode = ont.getNodefromOntResource(prop, alignType.aligningProperties);
				UICore.getInstance().addGraphicalRepresentation(amnode, node);
			} catch (Exception e) {
				// An exception has been thrown by getNodefromOntResource().
				// This means that the OntClass was not found, therefore we cannot connect this LegacyNode to a Node object.
				if( Core.DEBUG ) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			
			graph.insertVertex(node);
				
			LegacyEdge edge = new LegacyEdge( parentNode, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put(prop, node);
			recursiveBuildPropertiesGraph( node, prop, depth+1, graph, ont, hashMap );
		}
	
	}

	
	/**
	 * This function tries to identify the root nodes of the Class hierarchy of the ontology by 
	 * searching for properties that do no have any super properties. 
	 */
	protected List<OntClass> getClassHierarchyRoots(OntModel m) {
		
		ArrayList<OntClass> roots = new ArrayList<OntClass>();
		
		if( m.containsResource(OWL.Thing) ) {
			// OWL ontology
	    	ExtendedIterator<?> itcls = m.listClasses();
	    	
	    	while( itcls.hasNext() ) {  // look through all the object properties
	    		OntClass oclass = (OntClass) itcls.next();
	    		boolean isRoot = true;
	    		try {
		    		ExtendedIterator<?> superClassItr = oclass.listSuperClasses();
		    		while( superClassItr.hasNext() ) {
		    			OntClass superClass = (OntClass) superClassItr.next();
		    			
		    			if( !oclass.equals(superClass) && !superClass.isAnon() && !superClass.equals(OWL.Thing) ) {
		    				// this property has a valid superclass, therefore it is not a root property
		    				superClassItr.close();
		    				isRoot = false;
		    				break;
		    			}
		    		}
		    		
		    		if( isRoot ) 
		    			roots.add(oclass);
	    		} catch( Exception e ) {
	    			e.printStackTrace();
	    		}
	    		
			}    	
		} else {
			// RDF/S Ontology
			ExtendedIterator<?> itcls = m.listClasses();
	    	
	    	while( itcls.hasNext() ) {  // look through all the object properties
	    		OntClass oclass = (OntClass) itcls.next();
	    		boolean isRoot = true;
	    		
	    		ExtendedIterator<?> superClassItr = null;
	    		try {
	    			superClassItr = oclass.listSuperClasses();
		    		while( superClassItr.hasNext() ) {
		    			OntClass superClass = (OntClass) superClassItr.next();
		    			
		    			if( !oclass.equals(superClass) && !superClass.isAnon() && !superClass.getLocalName().equals("Resource")) {
		    				// this property has a valid superclass, therefore it is not a root property
		    				superClassItr.close();
		    				isRoot = false;
		    				break;
		    			}
		    		}
	    		} catch( ConversionException ce ) {
	    			// cannot list superclasses of this node.
	    			// put it as a hierarchy root
	    			// TODO: Figure out why we are getting these ConversionExceptions.
	    			if( Core.DEBUG ) {
		    			Logger log = Logger.getLogger(this.getClass());
		    			log.setLevel(Level.DEBUG);
		    			log.debug("Caught exception, but working around it.");
	    			}
	    			isRoot = true;
	    		}
	    		
	    		if( isRoot ) 
	    			roots.add(oclass);
	    		
			}
		}
    	
    	return roots;  // all the heirarchy roots
	}
	
	/**
	 * This function tries to identify the root nodes of the Property hierarchy of the ontology by 
	 * searching for properties that do no have any super properties. 
	 */
	private ArrayList<OntProperty> getPropertyHierarchyRoots(OntModel m) {
		
		ArrayList<OntProperty> roots = new ArrayList<OntProperty>(); 
		
		try {
		
			// OBJECT PROPERTIES
			
	    	ExtendedIterator<?> itobj = m.listObjectProperties();
	    	
	    	while( itobj.hasNext() ) {  // look through all the object properties
	    		OntProperty property = (OntProperty) itobj.next();
	    		boolean isRoot = true;
	    		
	    		ExtendedIterator<?> superPropItr = property.listSuperProperties();
	    		while( superPropItr.hasNext() ) {
	    			OntProperty superProperty = (OntProperty) superPropItr.next();
	    			
	    			if( !property.equals(superProperty) && !superProperty.isAnon() ) {
	    				// this property has a valid superclass, therefore it is not a root property
	    				superPropItr.close();
	    				isRoot = false;
	    				break;
	    			}
	    		}
	    		
	    		if( isRoot ) roots.add(property);
	    		
			}
		
	    	
	    	// DATATYPE PROPERTIES
	    	
	    	ExtendedIterator<?> itdata = m.listDatatypeProperties();
	    	while( itdata.hasNext() ) {  // look through all the object properties
	    		OntProperty property = (OntProperty) itdata.next();
	    		boolean isRoot = true;
	    		
	    		try {
		    		ExtendedIterator<?> superPropItr = property.listSuperProperties();
		    		while( superPropItr.hasNext() ) {
		    			OntProperty superProperty = (OntProperty) superPropItr.next();
		    			
		    			if( !property.equals(superProperty) && !superProperty.isAnon() ) {
		    				// this property has a valid superclass, therefore it is not a root property
		    				superPropItr.close();
		    				isRoot = false;
		    				break;
		    			}
		    		}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    		
	    		if( isRoot ) roots.add(property);
	    		
			}
		} catch( ConversionException e ) {
			// dealing with an RDFS ontology model
			// TODO: Fix this and the one below.
			// copied from below
			ExtendedIterator<?> itobj = m.listOntProperties();
	    	
	    	while( itobj.hasNext() ) {  // look through all the object properties
	    		OntProperty property = (OntProperty) itobj.next();
	    		try {
		    		boolean isRoot = true;
		    		
		    		ExtendedIterator<?> superPropItr = property.listSuperProperties();
		    		while( superPropItr.hasNext() ) {
		    			OntProperty superProperty = (OntProperty) superPropItr.next();
		    			
		    			if( !property.equals(superProperty) && !superProperty.isAnon() ) {
		    				// this property has a valid superclass, therefore it is not a root property
		    				superPropItr.close();
		    				isRoot = false;
		    				break;
		    			}
		    		}
		    		
		    		if( isRoot ) roots.add(property);
	    		} catch( ConversionException e2 ) {
	    			roots.add(property);
	    			continue;
	    		}
	    		
			}
		} catch( ProfileException e ) {
			// dealing with an RDFS ontology model.
			// TODO: Find a better way to do this.
			ExtendedIterator<?> itobj = m.listOntProperties();
	    	
	    	while( itobj.hasNext() ) {  // look through all the object properties
	    		OntProperty property = (OntProperty) itobj.next();
	    		try {
		    		boolean isRoot = true;
		    		
		    		ExtendedIterator<?> superPropItr = property.listSuperProperties();
		    		while( superPropItr.hasNext() ) {
		    			OntProperty superProperty = (OntProperty) superPropItr.next();
		    			
		    			if( !property.equals(superProperty) && !superProperty.isAnon() ) {
		    				// this property has a valid superclass, therefore it is not a root property
		    				superPropItr.close();
		    				isRoot = false;
		    				break;
		    			}
		    		}
		    		
		    		if( isRoot ) roots.add(property);
	    		} catch( ConversionException e2 ) {
	    			roots.add(property);
	    			continue;
	    		}
	    		
			}
		}
    	
    	
    	return roots;  // all the hierarchy roots
	}
		
	
	@Override
	public CanvasGraph buildMatcherGraph( MatchingTask m ) {
		
		if( m == null ) {
			log.warn("Cannot build matcher graph for null task.");
			return null;
		}
		
		if( m.selectionResult == null ) {
			log.warn("Cannot build matcher graph for null selection result.");
			return null;
		}
		
		CanvasGraph matcherGraph = new CanvasGraph( GraphType.MATCHER_GRAPH, m.getID() );
		
		// Get the Class alignments.
		
		Alignment<Mapping> classesMatchings = m.selectionResult.getClassAlignmentSet();
		if( classesMatchings != null ) {
			Iterator<Mapping> alignmentIter = classesMatchings.iterator();
			while( alignmentIter.hasNext() ) {
				Mapping alignment = alignmentIter.next();

				Node e1 = alignment.getEntity1();
				Node e2 = alignment.getEntity2();
				
				
				Canvas2Vertex n1 = (Canvas2Vertex) UICore.getInstance().getGraphicalRepresentation(e1, Canvas2.class);
				Canvas2Vertex n2 = (Canvas2Vertex) UICore.getInstance().getGraphicalRepresentation(e2, Canvas2.class);

				if( n1 == null || n2 == null ) { continue; }
				
				LegacyMapping edge = new LegacyMapping( n1, n2, alignment, m.getID());
	
				matcherGraph.insertEdge(edge);
				
			}
		}

		// Get the Properties alignments.
		
		Alignment<Mapping> propertiesMatchings = m.selectionResult.getPropertyAlignmentSet();
		if( propertiesMatchings != null ) {
			Iterator<Mapping> alignmentIter = propertiesMatchings.iterator();
			while( alignmentIter.hasNext() ) {
				Mapping alignment = alignmentIter.next();
				
				Node e1 = alignment.getEntity1();
				Node e2 = alignment.getEntity2();
				
				Canvas2Vertex n1 = (Canvas2Vertex) UICore.getInstance().getGraphicalRepresentation(e1, Canvas2.class);
				Canvas2Vertex n2 = (Canvas2Vertex) UICore.getInstance().getGraphicalRepresentation(e2, Canvas2.class);
				
				if( n1 == null || n2 == null ) {
					// graphical representation not updated??
					// TODO: Figure out why this is happening.  - Cosmin.
					Logger log = Logger.getLogger(this.getClass());
					log.setLevel(Level.DEBUG);
					if( n1 == null ) log.debug("Missing graphical representation! " + e1);
					if( n2 == null ) log.debug("Missing graphical representation! " + e2);	
					continue;
				}
				
				LegacyMapping edge = new LegacyMapping( n1, n2, alignment, m.getID());
					
				matcherGraph.insertEdge(edge);

			}
		}

		
		return matcherGraph;
	}
	
/*
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *************************************************** EVENT LISTENERS *************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************
 *********************************************************************************************************************************	
 */
	
	/**
	 * Handle Viewport resize events here.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {  // the state of the viewport has changed.
		
		return; // TODO: Get this resizing working.
		/*		
		JViewport vp = vizpanel.getViewport();
		Dimension vpSize = vp.getSize();
		
		
		
		if( oldViewportDimensions.height != vpSize.height || oldViewportDimensions.width != vpSize.width ) {
			// the size of the viewport has changed

			// need to know what's on the left side and what's on the right side
			ArrayList<CanvasGraph> ontologyGraphs = vizpanel.getOntologyGraphs();
			ArrayList<CanvasGraph> leftSideGraphs = GraphLocator.getGraphsByID( ontologyGraphs, leftOntologyID);
			ArrayList<CanvasGraph> rightSideGraphs = GraphLocator.getGraphsByID( ontologyGraphs, rightOntologyID);
			
			Rectangle leftBounds = CanvasGraph.getCombinedBounds(leftSideGraphs); // the combined bounds of all the graphs on the left side
			Rectangle rightBounds = CanvasGraph.getCombinedBounds(rightSideGraphs); // the combined bounds of all the graphs on the right side
			int viewportHalfwidth = vp.getBounds().width / 2;
			
			int leftBoundsLeftmostX = leftBounds.x + leftBounds.width; 
			if( leftBoundsLeftmostX > viewportHalfwidth ) { // if the left graphs move past the midpoint of the viewport even if the 
															// viewport were scrolled all the way to the left, then layout everything end to end
				
				// TODO: do this part
			} else {
				// we have space between the middle line and the left side.
				// line up the middle divider so it is at the middle of the canvas
				int newMiddleDividerX = leftBounds.x + (viewportHalfwidth - leftBoundsLeftmostX); // this should be where the middle divider should be moved to
				
				if( newMiddleDividerX != middleDivider.getObject().x ) {  // do we have to move anything?
					// yes, we have to move everything over.
					int deltaX = newMiddleDividerX - middleDivider.getObject().x;
					
					middleDivider.move(deltaX, 0);
					Iterator<CanvasGraph> graphIter = rightSideGraphs.iterator();
					while( graphIter.hasNext() ) { graphIter.next().moveGraph(deltaX, 0); } // move all the rightside graphs over by deltaX
					
				}
			}
			
		}
		*/
	}
	
	
	/**
	 * MOUSE EVENT listener functions
	 */
	
	// These methods relay the mouse events to the mouseHandler.
	@Override public void mouseClicked(MouseEvent e) { mouseHandler.mouseClicked(e); }
	@Override public void mouseMoved(MouseEvent e)    { mouseHandler.mouseMoved(e); }

	/**
	 * Used to let the layout know that a popup menu has been canceled.
	 */
	public void cancelPopupMenu(Graphics g) {
		PopupMenuActive = false;
		vizpanel.repaint();
		//if( hoveringOver != null ) {
		//	hoveringOver.setHover(false);
		//	hoveringOver.draw(g);
		//	hoveringOver = null; // clear the hover target, since the click can be anywhere and we didn't check again what we're hovering over
		//}
	}

	public void disableSingleMappingView() {
		
		// select the node that was last clicked
		for( LegacyNode n : selectedNodes ) { n.setSelected(false);	}
		selectedNodes.clear();
		
		if( hoveringOver != null && hoveringOver instanceof LegacyNode ) {
			selectedNodes.add( (LegacyNode) hoveringOver);
			hoveringOver.setSelected(true);
		}
		
		Iterator<CanvasGraph> graphIter = vizpanel.getGraphs().iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph graph = graphIter.next();
			
			// restore the vertices
			Iterator<Canvas2Vertex> nodeIter = graph.vertices();
			while( nodeIter.hasNext() ) {
				Canvas2Vertex node = nodeIter.next();
				node.popVisibility(); 
			}
			
			// restore the edges
			Iterator<Canvas2Edge> edgeIter = graph.edges();
			while( edgeIter.hasNext() ) {
				Canvas2Edge edge = edgeIter.next();
				edge.popVisibility();
			}
			
		}
		
		// move the nodes back to their places
		Iterator<LegacyNode> movedNodesIter = SingleMappingMovedNodes.iterator();
		while( movedNodesIter.hasNext() ) { movedNodesIter.next().popXY(); }
		SingleMappingMovedNodes.clear();
		
		Iterator<LegacyMapping> movedMappingsIter = MovedMappings.iterator();
		while( movedMappingsIter.hasNext() ) { 
			LegacyMapping currentMapping = movedMappingsIter.next();
			// translate the label back to only similarity if so required
			if( Core.getAppPreferences().getShowMappingsShortname() ) {
				MappingData d = (MappingData) currentMapping.getObject();
				if( d.alignment != null ) {
					String sim = Utility.getNoDecimalPercentFromDouble(d.alignment.getSimilarity());
					d.label = sim;
				}
			}
			
			currentMapping.updateBounds(); 
		}
		MovedMappings.clear();
		
		
		
		
		SingleMappingView = false; // turn off the singlemappingview
		
		// because we have moved nodes, the bounds of the graphs have changed.
		// Update the bounds of all the graphs.
		// TODO: This should not update ALL the graphs, but only the ones that contain the nodes that have moved.
		ArrayList<CanvasGraph> graphs = vizpanel.getGraphs();
		graphIter = graphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph g = graphIter.next();
			g.recalculateBounds();
		}
		
	}

	/**
	 * This method activates the SingleMappingView after the user doubleclicks a concept.
	 */
	public void enableSingleMappingView() {
		// Activate the SingleMappingView
		SingleMappingView = true;
		
		// turn off the visibility of all the nodes and edges
		Iterator<CanvasGraph> graphIter = vizpanel.getGraphs().iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph graph = graphIter.next();
			
			// hide the vertices
			Iterator<Canvas2Vertex> nodeIter = graph.vertices();
			while( nodeIter.hasNext() ) {
				Canvas2Vertex node = nodeIter.next();
				node.pushVisibility(false); 
			}
			
			// hide the edges
			Iterator<Canvas2Edge> edgeIter = graph.edges();
			while( edgeIter.hasNext() ) {
				Canvas2Edge edge = edgeIter.next();
				edge.pushVisibility(false);
			}
			
		}
		
		
		// now that all of the nodes and edges have been hidden, show only the ones we want to see
		ArrayList<Canvas2Vertex> parentNodes = new ArrayList<Canvas2Vertex>();
		ArrayList<Canvas2Vertex> childNodes = new ArrayList<Canvas2Vertex>();
		Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
		while( nodeIter.hasNext() ) {
			// we will show all edges connected to the selectedNodes, and all nodes connected to the edges of the selectedNodes
			LegacyNode selectedNode = nodeIter.next();
			selectedNode.setVisible(true);
			selectedNode.setSelected(false); // unselect the nodes
			
			for( DirectedGraphEdge<GraphicalData, GraphicalData> connectedInEdge : selectedNode.edgesInList() ) {
				Canvas2Edge connectedEdge = (Canvas2Edge) connectedInEdge;
				connectedEdge.setVisible(true);
				((Canvas2Vertex)connectedEdge.getOrigin()).setVisible(true); // the selected node is the destination, so we need to set the origin to be visible
				
				if( connectedEdge instanceof LegacyEdge ) parentNodes.add((Canvas2Vertex)connectedEdge.getOrigin());
				
				// if the node connected to this one is a mapping, we will traverse one more hop. -- TODO: Make this work.
				/*if( connectedEdge instanceof LegacyMapping && connectedEdge.getOrigin() instanceof LegacyNode ) {
					LegacyNode mappingOrigin = (LegacyNode) connectedEdge.getOrigin();
					for( DirectedGraphEdge<GraphicalData, GraphicalData> mappingInEdge : mappingOrigin.edgesInList() ) {
						Canvas2Edge mappingConnectedEdge = (Canvas2Edge) mappingInEdge;
						mappingConnectedEdge.setVisible(true);
						((Canvas2Vertex)mappingConnectedEdge.getOrigin()).setVisible(true); // the selected node is the destination, so we need to set the origin to be visible
					}
					for( DirectedGraphEdge<GraphicalData, GraphicalData> mappingOutEdge : mappingOrigin.edgesOutList() ) {
						Canvas2Edge mappingConnectedEdge = (Canvas2Edge) mappingOutEdge;
						mappingConnectedEdge.setVisible(true);
						((Canvas2Vertex)connectedEdge.getDestination()).setVisible(true); // the selected node is the the origin, so we need to set the destination to be visible
					}
				}*/
			}
			
			for( DirectedGraphEdge<GraphicalData, GraphicalData> connectedOutEdge : selectedNode.edgesOutList() ) {
				Canvas2Edge connectedEdge = (Canvas2Edge) connectedOutEdge;
				connectedEdge.setVisible(true);
				((Canvas2Vertex)connectedEdge.getDestination()).setVisible(true);
				
				
				if( connectedEdge instanceof LegacyEdge ) childNodes.add((Canvas2Vertex)connectedEdge.getDestination());
				
				// if the node connected to this one is a mapping, we will traverse one more hop. // TODO: Make this work.
				/*if( connectedEdge instanceof LegacyMapping && connectedEdge.getDestination() instanceof LegacyNode ) {
					LegacyNode mappingDestination = (LegacyNode) connectedEdge.getDestination();
					for( DirectedGraphEdge<GraphicalData, GraphicalData> mappingInEdge : mappingDestination.edgesInList() ) {
						Canvas2Edge mappingConnectedEdge = (Canvas2Edge) mappingInEdge;
						mappingConnectedEdge.setVisible(true);
						((Canvas2Vertex)mappingConnectedEdge.getOrigin()).setVisible(true); // the selected node is the destination, so we need to set the origin to be visible
					}
					for( DirectedGraphEdge<GraphicalData, GraphicalData> mappingOutEdge : mappingDestination.edgesOutList() ) {
						Canvas2Edge mappingConnectedEdge = (Canvas2Edge) mappingOutEdge;
						mappingConnectedEdge.setVisible(true);
						((Canvas2Vertex)connectedEdge.getDestination()).setVisible(true); // the selected node is the the origin, so we need to set the destination to be visible
					}
				}*/
			}
			
		}
		
		
		// we need to move the opposite side up to the side we clicked
		// Step 1. Calculate the uppermostY.
		//ArrayList<LegacyMapping> mappingList = new ArrayList<LegacyMapping>(); // we have to keep a list of all the mappings to/from this node
		int uppermostY = -1; // -1 is a dummy value.  Valid values are >= 0.
		for( LegacyNode selectedNode : selectedNodes ) {
			
			// update the uppermostY
			if( uppermostY < 0 || selectedNode.getObject().y < uppermostY ) {
				uppermostY = selectedNode.getObject().y;
			}
			
			// update the mappingList
			Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeInIter = selectedNode.edgesInIter();
			while( edgeInIter.hasNext() ) {
				Canvas2Edge connectedEdge = (Canvas2Edge) edgeInIter.next();
				if( connectedEdge instanceof LegacyMapping ) {
					MovedMappings.add( (LegacyMapping) connectedEdge );
				}
			}
			
			Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeOutIter = selectedNode.edgesOutIter();
			while( edgeOutIter.hasNext() ) {
				Canvas2Edge connectedEdge = (Canvas2Edge) edgeOutIter.next();
				if( connectedEdge instanceof LegacyMapping) {
					MovedMappings.add( (LegacyMapping) connectedEdge );
				}
			}
		}
		
		// move the parent nodes down to the child
		for( int i = 0; i < parentNodes.size(); i++ ) {
			Canvas2Vertex parent = parentNodes.get(i);
			if( parent instanceof LegacyNode ) { 
				parent.pushXY(parent.getBounds().x, uppermostY - ((nodeHeight + marginBottom) * (i+1)) );
				SingleMappingMovedNodes.add((LegacyNode)parent);
			}
		}
		
		// move the children nodes up to the child
		for( int i = 0; i < childNodes.size(); i++ ) {
			Canvas2Vertex child = childNodes.get(i);
			if( child instanceof LegacyNode ) {
				child.pushXY(child.getBounds().x, uppermostY + ((nodeHeight + marginBottom) * (i+1)) );
				SingleMappingMovedNodes.add((LegacyNode)child);
			}
		}
		
		// now we must move the mappings to the uppermostY.
		for( int i = 0; i < MovedMappings.size(); i++ ) {
			// nodeheight marginbottom
			LegacyMapping currentMapping = MovedMappings.get(i);
			
			// translate the label of the mapping to include the matcher name if the user so desires.
			if( Core.getAppPreferences().getShowMappingsShortname() ) {
				MappingData d = (MappingData) currentMapping.getObject();
				if( d.alignment != null ) {
					String sim = Utility.getNoDecimalPercentFromDouble(d.alignment.getSimilarity());
					MatchingTask m = Core.getInstance().getMatchingTaskByID( d.matcherID );
					String shortName = m.getShortLabel();
					d.label = d.alignment.getRelation().getVisualRepresentation() + " (" + sim + ")";
					if( shortName != null ) { d.label += " " + shortName; }
				}
			}
			
			if( selectedNodes.contains( currentMapping.getOrigin()) ) {
				// we doubleclicked on the origin of the mapping, so move the destination up.
				LegacyNode destinationNode = (LegacyNode) currentMapping.getDestination();
				destinationNode.pushXY( destinationNode.getGraphicalData().x , uppermostY + i*(nodeHeight+marginBottom) );
				
				SingleMappingMovedNodes.add(destinationNode);
				vizpanel.getVisibleVertices().add(destinationNode);
			} else {
				// we doubleclicked on the destination of the mapping, therefore we move the origin up
				LegacyNode originNode = (LegacyNode) currentMapping.getOrigin();
				originNode.pushXY( originNode.getGraphicalData().x , uppermostY + i*(nodeHeight+marginBottom) );
				
				SingleMappingMovedNodes.add(originNode);
				vizpanel.getVisibleVertices().add(originNode);
			}
			
			// update the bounds of the node.
			currentMapping.updateBounds();

		}
		
		selectedNodes.clear();
		
		// because we have moved nodes, the bounds of the graphs have changed.
		// Update the bounds of all the graphs.
		// TODO: This should not update ALL the graphs, but only the ones that contain the nodes that have moved.
		ArrayList<CanvasGraph> graphs = vizpanel.getGraphs();
		graphIter = graphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph g = graphIter.next();
			g.recalculateBounds();
		}
		
		
	}



	private Canvas2Vertex hoveringOver;

	@Override 
	public void actionPerformed(ActionEvent e) {

		String actionCommand = e.getActionCommand();
		
		// these commands are from the Create Mappings popup menu
		if( actionCommand.equals("CREATE_DEFAULT")          ||
			actionCommand.equals("CREATE_EQUIVALENCE")      ||
			actionCommand.equals("CREATE_SUPERCLASS")       ||
			actionCommand.equals("CREATE_SUBCLASS")         ||
			actionCommand.equals("CREATE_SUBSET")           || 
			actionCommand.equals("CREATE_SUBSETCOMPLETE")   ||
			actionCommand.equals("CREATE_SUPERSET")         || 
			actionCommand.equals("CREATE_SUPERSETCOMPLETE") ||
			actionCommand.equals("CREATE_RELATED")          ||
			actionCommand.equals("CREATE_OTHER")            ){
		
			if( PopupMenuActive ) PopupMenuActive = false; // the menu was just closed if this action was fired.
			
			if( hoveringOver == null ) {
				// this seems to happen when you click too fast
				return;  // do nothing for now
			}
			
			MappingRelation relation = MappingRelation.EQUIVALENCE;;
			double sim = 0;
			ArrayList<Mapping> userMappings = new ArrayList<Mapping>();
			
			
			if( actionCommand == "CREATE_DEFAULT" ) {
				relation = MappingRelation.EQUIVALENCE;
				sim = 1.0d;
			} else {
				// ask the user for the similarity value
				boolean correct = false;
				boolean abort = false;
				while(!correct &&  !abort) {
					
					String x = JOptionPane.showInputDialog(null, "Insert the similarity value.\nInsert a number between 0 and 100 using only numeric digits.\n Warning: the similarity should be higher than the threshold value.\nIf not, the similarity matrix will be modified but the alignment won't be selected and visualized.");
					try {
						if(x == null)
							abort = true;//USER SELECTED CANCEL
						else {
							sim = Double.parseDouble(x);
							if(sim >= 0 && sim <= 100) {
								correct = true;
								sim = sim/100;
							}
						}
					}
					catch(Exception ex) {//WRONG INPUT, ASK INPUT AGAIN
					}
				}
			}

			// TODO: Figure out how to allow the user to set a custom relation.
			/*if( actionCommand == "CREATE_OTHER" ){
				boolean correct = false;
				boolean abort = false;
				while(!correct &&  !abort) {
					String x = JOptionPane.showInputDialog(null, "Insert the relation type:");
					try {
						if(x == null)
							abort = true;//USER SELECTED CANCEL
						else {
							relation = x;
							correct = true;
						}
					}
					catch(Exception ex) {//WRONG INPUT, ASK INPUT AGAIN
					}
				}
			}*/
			
			if( actionCommand.equals("CREATE_EQUIVALENCE") )      relation = MappingRelation.EQUIVALENCE;
			if( actionCommand.equals("CREATE_SUPERCLASS") )       relation = MappingRelation.SUPERCLASS;
			if( actionCommand.equals("CREATE_SUBCLASS") )         relation = MappingRelation.SUBCLASS;
			if( actionCommand.equals("CREATE_SUBSET") )           relation = MappingRelation.SUBSET;
			if( actionCommand.equals("CREATE_SUBSETCOMPLETE") )   relation = MappingRelation.SUBSETCOMPLETE;
			if( actionCommand.equals("CREATE_SUPERSET") )         relation = MappingRelation.SUPERSET;
			if( actionCommand.equals("CREATE_SUPERSETCOMPLETE") ) relation = MappingRelation.SUPERSETCOMPLETE;
			if( actionCommand.equals("CREATE_RELATED") )          relation = MappingRelation.RELATED;
			

			// **************** create the alignments
				
			Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
	
			// what type of nodes are we mapping
			alignType type = null;
			
			if( hoveringOver.getGraphicalData().type == NodeType.CLASS_NODE ) {
				type = AbstractMatcher.alignType.aligningClasses;
			} else if( hoveringOver.getGraphicalData().type == NodeType.PROPERTY_NODE ) {
				type = AbstractMatcher.alignType.aligningProperties;
			}
			
			// this is a little bit of a mess, but we have to support legacy code (meaning the Alignment class)- 1/29/2010 Cosmin
			Ontology o2 = Core.getInstance().getOntologyByID( hoveringOver.getGraphicalData().ontologyID );
			Node n2 = null;
			try {
				n2 = o2.getNodefromOntResource( hoveringOver.getGraphicalData().r, type );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			while( nodeIter.hasNext() ) {
				
				LegacyNode ln = nodeIter.next();
				
				// again, this is necessary in order to be compatible with the way the Alignment class is at the moment - 1/29/2010 Cosmin
				Ontology o1 = Core.getInstance().getOntologyByID( ln.getGraphicalData().ontologyID );
				Node n1 = null;
				try {
					n1 = o1.getNodefromOntResource( ln.getGraphicalData().r, type );
				} catch (Exception e1) {
					// an exception usually happens when users try to match classes with properties, which is not allowed (it makes no logical sense)
					Utility.displayErrorPane( "Cannot create this mapping.  \nYou may be trying to match incompatible concepts (e.g. classes with properties).\n\n" 
											  + e1.getMessage() , "Cannot create mapping");
					//e1.printStackTrace();
					return;
				}
				
				Mapping a;
				if( ln.getGraphicalData().ontologyID == leftOntologyID ) { // this if statement fixes a small display bug
					a = new Mapping( n1, n2, sim, relation, type);
				} else {
					a = new Mapping( n2, n1, sim, relation, type);
				}
				
				userMappings.add(a);
				
			}
 
			// add the mappings created to the user
			// FIXME: Replace the call to Core with something else!
			//Core.getUI().getControlPanel().userMatching(userMappings);
			
			// clear the hover
			if( hoveringOver != null ) {
				hoveringOver.setHover(false);
				hoveringOver = null;
			}
			
		}
		
	}
	
	public int getSelectedNodesOntology() {
		int ontologyID = -1;
		Iterator<LegacyNode> nodeIter = selectedNodes.iterator();

		if( nodeIter.hasNext() ) {
			// the first item in the list
			ontologyID = nodeIter.next().getGraphicalData().ontologyID;
		}
		else { return Core.ID_NONE; } // empty list
		
		// the next items in the list
		while( nodeIter.hasNext() ) {
			if( nodeIter.next().getGraphicalData().ontologyID != ontologyID ) {
				// we have nodes that are not from the same ontology,
				// this should not happen (because if it happens, then the menu pops up, and the selectedNodes is cleared).
				return Core.ID_NONE;
			}
		}
		
		// all the nodes in the selectedNodes list are from the ontology with id "ontologyID"
		return ontologyID;
	}

	
	/************************** POPUP MENU LISTENER EVENTS *****************************************/
	// Used for properly canceling a popup menu.
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {	
		if( e.getSource() instanceof DeleteMappingMenu ||
		    e.getSource() instanceof CreateMappingMenu ) {
			// A top level popup menu is going invisible.
			Graphics g = vizpanel.getGraphics();
			cancelPopupMenu(g);
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) { 
		if( e.getSource() instanceof DeleteMappingMenu ||
			e.getSource() instanceof CreateMappingMenu ) {
			// A menu has been canceled.
			Graphics g = vizpanel.getGraphics();
			cancelPopupMenu(g);
			
		}
	}

	@Override
	public boolean isViewActive(int viewID) {
		if( viewID == VIEW_SINGLE_MAPPING ) return SingleMappingView;
		if( viewID == VIEW_NORMAL ) return !SingleMappingView;
		return false;
	}

	// TODO: Remove these unused methods (by changing Canvas2Layout)
	@Override public void mouseDragged(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void mousePressed(MouseEvent e) { }
	@Override public void mouseReleased(MouseEvent e) {	}
	@Override public void mouseWheelMoved(MouseWheelEvent e) { }
	
	/**
	 * Select the graphical representation of a node in the current layout.
	 */
	@Override
	public void selectNode(Node n) {
		
		int ontID = n.getOntologyID();
		
		// Find the HashMap for the ontology
		Iterator<Pair<Integer,HashMap<OntResource,LegacyNode>>> pairIter = ConceptHashMaps.iterator();
		while( pairIter.hasNext() ) {
			Pair<Integer,HashMap<OntResource,LegacyNode>> pair = pairIter.next();
			if( pair.getLeft().intValue() == ontID ) {
				// we have the hashmap which for the node's ontology
				HashMap<OntResource,LegacyNode> ontologyMap = pair.getRight();
				Resource r = n.getResource();
				if( !r.canAs(OntResource.class) ) return;
				OntResource or = r.as(OntResource.class);
				
				LegacyNode ln = ontologyMap.get(or);
				
				if( ln != null ) selectLegacyNode(ln);
				
				return;
			}
		}
	}
	
	@Override
	public void unselectAllNodes() {
		
		//Graphics g = vizpanel.getGraphics();   // used for any redrawing of nodes
		//ArrayList<Canvas2Vertex> visibleVertices = getVizPanel().getVisibleVertices();
		
		for( LegacyNode ln : selectedNodes ) {
			ln.setSelected(false);
			//if( visibleVertices.contains( (Canvas2Vertex) ln ) ) {
				// redraw only if it's currently visible
				//selectedNode.clearDrawArea(g);
			//	ln.draw(g);
			//}
		}
		selectedNodes.clear();
	}
	
	/** Selects a LegacyNode in the current visualization. */
	public void selectLegacyNode(LegacyNode ln) {
		
		if( SingleMappingView ) disableSingleMappingView();
		
		unselectAllNodes();
		
		JViewport vp = vizpanel.getViewport();
		Rectangle bounds = ln.getBounds();
		int midpointX = bounds.x+ bounds.width/2;
		int midpointY = bounds.y+ bounds.height/2;
		vp.setViewPosition( new Point( Math.max( midpointX - vp.getWidth()/2, 0), Math.max(midpointY - vp.getHeight()/2,0) ));  // display the needle at the center of the viewport
		// since this is a vertex, select it.
		ln.setSelected(true);
		selectedNodes.add(ln);
	}
}
