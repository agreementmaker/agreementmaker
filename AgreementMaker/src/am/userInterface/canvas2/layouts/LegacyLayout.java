package am.userInterface.canvas2.layouts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.OntTools;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;
import am.app.ontology.Ontology;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.graphical.RectangleElement;
import am.userInterface.canvas2.graphical.TextElement;
import am.userInterface.canvas2.graphical.GraphicalData.NodeType;
import am.userInterface.canvas2.nodes.GraphicalNode;
import am.userInterface.canvas2.nodes.LegacyEdge;
import am.userInterface.canvas2.nodes.LegacyMapping;
import am.userInterface.canvas2.nodes.LegacyNode;
import am.userInterface.canvas2.utility.Canvas2Layout;
import am.userInterface.canvas2.utility.Canvas2Vertex;
import am.userInterface.canvas2.utility.CanvasGraph;
import am.userInterface.canvas2.utility.GraphLocator;
import am.userInterface.canvas2.utility.GraphLocator.GraphType;

/**
 * This layout is resposible for placement of nodes and edges.
 * The layout algorithm is the same one as the original Canvas class.
 * 
 * @author cosmin
 *
 */

public class LegacyLayout extends Canvas2Layout {
	
	/* FLAGS, and SETTINGS */
	private boolean showLocalName = true;
	private boolean showLabel     = false;
	private String  language      = "EN";
	private String  labelAndNameSeparator = " || ";

	private boolean[] pixelColumnDrawn;  // used for a very special hack in LegacyEdge.draw();  Read about it in that method.
									     // It's done in order to avoid unnecessary draws() and speed up the paint() function. 
	private Rectangle pixelColumnViewport;  // required to know the correct index in the pixelColumnDrawn array.

	private HashMap<OntResource,LegacyNode> hashMap; // used in the graph building to avoid visiting the same nodes twice 
	private LegacyNode anonymousNode;
	
	private Dimension oldViewportDimensions;  // this variable is used in the stateChanged handler.

	/**
	 * Graph Building Variables.
	 */
	
	private int subgraphXoffset = 20;
	private int subgraphYoffset = 20;
	private int depthIndent = 20;
	private int marginBottom = 5;
	private int nodeHeight = 20;
	
	private int leftGraphX = 20;
	private int leftGraphY = 42;
	private int rightGraphX = 500;
	private int rightGraphY = 42;
	private int middleDividerLeftMargin = 10;
	private int middleDividerWidth = 1;
	private int topDividerTopMargin = 20;
	
	private GraphicalNode middleDivider;
	private GraphicalNode topDivider;
	private GraphicalNode sourceOntologyText;
	private GraphicalNode targetOntologyText;
	private OntClass owlThing;
	
	private boolean leftSideLoaded = false; 
	private boolean rightSideLoaded = false;
	private boolean leftSide;
	
	private int leftOntologyID = Ontology.ID_NONE;  // the ontology ID of the graphs on the left side of the canvas layout
	private int rightOntologyID = Ontology.ID_NONE; // the ontology ID of the graphs on the right side of the canvas layout 

	
	/** Mouse Event handlers Variables */
	
	private ArrayList<LegacyNode> selectedNodes;  // the list of currently selected nodes

	
	
	
	public LegacyLayout(Canvas2 vp) {
		super(vp);
		hashMap = new HashMap<OntResource, LegacyNode>();
		oldViewportDimensions = new Dimension(0,0);
		
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
		return pixelColumnDrawn[viewportColNum];
	}
	public Rectangle getPixelColumnViewport() { return pixelColumnViewport; } // edge needs to know the viewport
	
	
	/**
	 * Utility Function. This function is called by the LegacyNodes in order to set their text label correctly.
	 */
	public String getNodeLabel(GraphicalData d ) {
		
		if( d.r == null ) {
			if( d.type == NodeType.TEXT_ELEMENT ) return ((TextElement)d).getText();
			return "";
		}
		
		if(showLabel && showLocalName)
			return d.r.getLocalName() + labelAndNameSeparator + d.r.getLabel(language);
		else if(showLabel)
			return d.r.getLabel(language);
		else if(showLocalName) 
			return d.r.getLocalName(); 
		else 
			return "";
	}
	
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
	 * 
	 * This includes
	 * 		- build the Class graph.
	 * 		- build the Properties graph.
	 *		- Individuals? TODO
	 */
	@Override
	public ArrayList<CanvasGraph> buildGlobalGraph( Ontology ont ) {
		
		ArrayList<CanvasGraph> ontologyGraphs = new ArrayList<CanvasGraph>();
		
		
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
		
		
		OntModel m = ont.getModel();
		
		owlThing = m.getOntClass( OWL.Thing.getURI() );
		
		CanvasGraph classesGraph = new CanvasGraph( GraphLocator.GraphType.CLASSES_GRAPH, ont.getID() );
		anonymousNode = new LegacyNode( new GraphicalData(0, 0, 0, 0, GraphicalData.NodeType.FAKE_NODE, this, Core.getInstance().getOntologyIDbyModel(m) ));
		
		LegacyNode classesRoot = buildClassGraph( m, classesGraph );  // build the class graph here
		
		// update the offsets to put the properties graph under the class graph.
		if( leftSide ) {
			subgraphYoffset = classesGraph.getBounds().y + classesGraph.getBounds().height + nodeHeight + marginBottom;
		} else {
			subgraphYoffset = classesGraph.getBounds().y + classesGraph.getBounds().height + nodeHeight + marginBottom;
		}
		
		CanvasGraph propertiesGraph = new CanvasGraph( GraphLocator.GraphType.PROPERTIES_GRAPH, ont.getID() );
		
		LegacyNode propertiesRoot = buildPropertiesGraph(m, propertiesGraph);  // and the properties graph here
	
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
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
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
	private CanvasGraph buildOntologyGraph(LegacyNode classesRoot,
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
	@SuppressWarnings("unchecked")   // this comes from OntTools.namedHierarchyRoots()
	private LegacyNode buildClassGraph( OntModel m, CanvasGraph graph ) {

		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		
		int depth = 0;
		
		// create the root node;
		TextElement gr = new TextElement(depth*depthIndent + subgraphXoffset, 
										 graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
										 0, nodeHeight, this, Core.getInstance().getOntologyIDbyModel(m));
		gr.setText("OWL Classes Hierarchy");
		LegacyNode root = new LegacyNode( gr );
		
		graph.insertVertex(root);
		
		List<OntClass> classesList = OntTools.namedHierarchyRoots(m);
		
		depth++;
		Iterator<OntClass> clsIter = classesList.iterator();
		while( clsIter.hasNext() ) {
			OntClass cls = clsIter.next();  // get the current child
			if( cls.isAnon() ) {  // if it is anonymous, don't add it, but we still need to recurse on its children
				hashMap.put(cls, anonymousNode);  // avoid cycles between anonymous nodes
				log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
				recursiveBuildClassGraph(root, cls, depth, graph);
				continue; 
			} else if( cls.equals(OWL.Nothing) )   // if it's OWL.Nothing (i.e. we recursed to the bottom of the heirarchy) skip it.
				continue;
			
			// cycle check at the root
			if( hashMap.containsKey(cls) ) { // we have seen this node before, do NOT recurse again
				log.debug("Cycle detected.  OntClass:" + cls );
				continue;
			}
			
			// so the child class is not anonymous or OWL.Nothing, add it to the graph, with the correct relationships
			GraphicalData gr1 = new GraphicalData( depth*depthIndent + subgraphXoffset, 
										           graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
										           100, nodeHeight, cls, GraphicalData.NodeType.CLASS_NODE, this );
			LegacyNode node = new LegacyNode( gr1);
			
			graph.insertVertex( node );
			LegacyEdge edge = new LegacyEdge( root, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put( cls, node);
			log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
			recursiveBuildClassGraph( node, cls, depth+1, graph );
			
		}
		
		return root;
	}
	
	
	private void recursiveBuildClassGraph(
			LegacyNode parentNode,
			OntClass parentClass,  // this has to be passed because of anonymous classes and the special root node
			int depth, 
			CanvasGraph graph) {

		
		
		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		log.debug(parentClass);
		
		ExtendedIterator clsIter = parentClass.listSubClasses(true);
		while( clsIter.hasNext() ) {
			OntClass cls = (OntClass) clsIter.next();
			if( cls.isAnon() ) {
				hashMap.put(cls, anonymousNode);  // avoid cycles between anonymous nodes
				log.debug(">> Inserted anonymous node " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
				recursiveBuildClassGraph( parentNode, cls, depth, graph );
				continue;
			} else if( cls.equals( OWL.Nothing ) ) 
				continue;

			// this is the cycle check
			if( hashMap.containsKey(cls) ) { // we have seen this node before, do NOT recurse again
				log.debug("Cycle detected.  OntClass:" + cls );
				continue;
			}
			
			GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
												   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
												   100 , nodeHeight, cls, GraphicalData.NodeType.CLASS_NODE, this ); 
			LegacyNode node = new LegacyNode( gr); 
			graph.insertVertex(node);
				
			LegacyEdge edge = new LegacyEdge( parentNode, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put(cls, node);
			log.debug(">> Inserted " + cls + " into hashmap. HASHCODE: " + cls.hashCode());
			recursiveBuildClassGraph( node, cls, depth+1, graph );
		}
	
	}


/*

	/**
	 * buildClassTree(), createFosterHome() and adoptRemainingOrphans(), and getVertexFromClass() are ported from OntoTree builder
	 * 
	 * fixDepthDFS() is written because the depthIndent information cannot be passed with these functions, so it has to be set
	 * after the heirarchy has been finished.
	 * @return
	 /
	protected LegacyNode buildClassTree( OntModel m, CanvasGraph graph) {
		
		//HashMap<OntClass, Vertex> classesMap = new HashMap<OntClass, Vertex>();  // this maps between ontology classes and Vertices created for the each class
		ExtendedIterator orphansItr = m.listClasses();  // right now the classes have no parents, so they are orphans.
		
		while( orphansItr.hasNext() ) { // iterate through all the classes
			
			OntClass currentOrphan = (OntClass) orphansItr.next();  // the current class we are looking at
			
			if( !currentOrphan.isAnon() ) {  // make sure this is a real class  (anoynymous classes are not real classes)
				createFosterHome( currentOrphan, graph );  // assign orphan classes to parent parent classes
			}
		}
		
		// this is the root node of the class tree (think of it like owl:Thing)
		// create the root node;
		TextElement gr = new TextElement(0,	 0,	 0, nodeHeight, this, Core.getInstance().getOntologyIDbyModel(m));
		gr.setText("OWL Classes Hierarchy");
		LegacyNode root = new LegacyNode( gr );

		// we may have classes that still don't have a parent. these orphans will be adopted by root.
		
		adoptRemainingOrphans( root, graph );
		
		fixDepthHeightDFS( root, 0, 0);  // because the heirarchy was not built in any order, the height and depth must be fixed after it is built (not during).
		
		return root;
	}

	private void createFosterHome( OntClass currentOrphan, CanvasGraph graph ) {
		
		LegacyNode currentVertex = getVertexFromClass( currentOrphan );
		
		ExtendedIterator parentsItr = currentOrphan.listSuperClasses( true );  // iterator of the current class' parents
		
		while( parentsItr.hasNext() ) {
			
			OntClass parentClass = (OntClass) parentsItr.next();

			
			if( !parentClass.isAnon() && !parentClass.equals(owlThing) ) {

				LegacyNode parentVertex = getVertexFromClass(parentClass);  // create a new Vertex object or use an existing one.
				
				//parentVertex.add( currentVertex );  // create the parent link between the parent and the child

			} 
		}
		
	}	

	private void adoptRemainingOrphans(LegacyNode root, CanvasGraph graph) {

		/*  // Alternative way of iterating through the classes (via the classesMap that was created).
		 *   
		Set< Entry<OntClass, Vertex>> classesSet = classesMap.entrySet();
		Iterator<Entry<OntClass, Vertex>> classesItr = classesSet.iterator();
		
		while( classesItr.hasNext() ) {
			
		}
		/
		
		// We will just iterate through the classes again, and find any remaining orphans
		ExtendedIterator classesItr = model.listClasses();
		
		while( classesItr.hasNext() ) {
			OntClass currentClass = (OntClass) classesItr.next();
			if( !currentClass.isAnon() ) {
				if( classesMap.containsKey(currentClass) ) {
					Vertex currentVertex = classesMap.get(currentClass);
					
					if( currentVertex.getParent() == null ) {
						// this vertex has no parent, that means root needs to adopt it
						root.add( currentVertex );
					}
					
				}
				else {
					// we should never get here
					// if we do, it means we _somehow_ missed a class during our first iteration in buildClassTree();
					System.err.println("Assertion failed: listClasses() returning different classes between calls.");
				}
				 
			}
			
		}
		
	}	
	
	
	/**
	 * helper Function for buildClassesTree()
	 * @param classesMap
	 * @param currentClass
	 * @return
	 /
	private LegacyNode getVertexFromClass( OntClass currentClass ) {
		LegacyNode currentVertex = null;
		
		if( hashMap.containsKey( currentClass ) ) { // we already have a Vertex for the currentClass (because it is the parent of some node)
			currentVertex = hashMap.get( currentClass );
		} else {
			// we don't have a Vertex for the current class, create one;
			//currentVertex = createNodeAndVertex( currentClass, true, ontology.getSourceOrTarget());
			
			GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
					   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
					   100 , nodeHeight, cls, GraphicalData.NodeType.CLASS_NODE, this ); 
			LegacyNode node = new LegacyNode( gr); 
			
			
			hashMap.put(currentClass, currentVertex);
		}
		
		return currentVertex;
	}

	// fix the positions of all the nodes linked to this graph
	private int fixDepthHeightDFS( DirectedGraphVertex<GraphicalData> root, int depth, int height ) {
		
		root.getObject().x = depth*depthIndent + subgraphXoffset;
		root.getObject().y = height * (nodeHeight+marginBottom) + subgraphYoffset;
		height = height+1;
		
		Iterator<DirectedGraphEdge<GraphicalData>> edgeIter = root.edgesOut();
		while( edgeIter.hasNext() ) { height = fixDepthHeightDFS( edgeIter.next().getDestination(), depth+1, height );	} // DFS call
		return height;
	}
	
(((((((((((((((((((((((((((((((((((((((((((())))))))))))))))))))))))))))))))))))))))))))
*/
	
	
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
	private LegacyNode buildPropertiesGraph( OntModel m, CanvasGraph graph ) {

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
				         0, nodeHeight, this, Core.getInstance().getOntologyIDbyModel(m));
		gr.setText("OWL Properties Hierarchy"); 
		LegacyNode root = new LegacyNode( gr );
		
		graph.insertVertex(root);
		
		List<OntProperty> propertiesList = getPropertyHeirarchyRoots(m);
		
		depth++;
		Iterator<OntProperty> propIter = propertiesList.iterator();
		while( propIter.hasNext() ) {
			OntProperty prop = propIter.next();  // get the current child
			if( prop.isAnon() ) {  // if it is anonymous, don't add it, but we still need to recurse on its children
				hashMap.put(prop, anonymousNode);  // avoid cycles between anonymous nodes
				recursiveBuildPropertiesGraph(root, prop, depth, graph);
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
										           0, nodeHeight, prop, GraphicalData.NodeType.PROPERTY_NODE, this );
			LegacyNode node = new LegacyNode( gr1);
			
			graph.insertVertex( node );
			LegacyEdge edge = new LegacyEdge( root, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put( prop, node);
			recursiveBuildPropertiesGraph( node, prop, depth+1, graph );
			
		}
		
		return root;
	}

	private void recursiveBuildPropertiesGraph(
			LegacyNode parentNode,
			OntProperty parentProperty,  // this has to be passed because of anonymous classes and the special root node
			int depth, 
			CanvasGraph graph) {

		
		
		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		log.debug(parentProperty);
		
		ExtendedIterator clsIter = null;
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
				recursiveBuildPropertiesGraph( parentNode, prop, depth, graph );
				continue;
			} else if( prop.equals( OWL.Nothing ) ) 
				continue;

			// this is the cycle check
			if( hashMap.containsKey(prop) ) { // we have seen this node before, do NOT recurse again
				log.debug("Cycle detected.  OntProperty:" + prop );
				continue;
			}
			
			GraphicalData gr = new GraphicalData( depth*depthIndent + subgraphXoffset, 
												   graph.numVertices() * (nodeHeight+marginBottom) + subgraphYoffset, 
												   100 , nodeHeight, prop, GraphicalData.NodeType.PROPERTY_NODE, this ); 
			LegacyNode node = new LegacyNode( gr); 
			graph.insertVertex(node);
				
			LegacyEdge edge = new LegacyEdge( parentNode, node, null, this );
			graph.insertEdge( edge );
			
			hashMap.put(prop, node);
			recursiveBuildPropertiesGraph( node, prop, depth+1, graph );
		}
	
	}
	
	/**
	 * This function tries to identify the root nodes of the Property hierarchy of the ontology by 
	 * searching for properties that do no have any super properties. 
	 */
	private ArrayList<OntProperty> getPropertyHeirarchyRoots(OntModel m) {
		
		ArrayList<OntProperty> roots = new ArrayList<OntProperty>(); 
		
		// OBJECT PROPERTIES
		
    	ExtendedIterator itobj = m.listObjectProperties();
    	
    	while( itobj.hasNext() ) {  // look through all the object properties
    		OntProperty property = (OntProperty) itobj.next();
    		boolean isRoot = true;
    		
    		ExtendedIterator superPropItr = property.listSuperProperties();
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
    	
    	ExtendedIterator itdata = m.listDatatypeProperties();
    	while( itdata.hasNext() ) {  // look through all the object properties
    		OntProperty property = (OntProperty) itdata.next();
    		boolean isRoot = true;
    		
    		ExtendedIterator superPropItr = property.listSuperProperties();
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
    	
    	
    	
    	return roots;  // all the heirarchy roots
	}
		
	
	@Override
	public CanvasGraph buildMatcherGraph(AbstractMatcher m) {
		
		CanvasGraph matcherGraph = new CanvasGraph( GraphType.MATCHER_GRAPH, m.getID() );
		
		// Get the Class alignments.
		
		AlignmentSet<Alignment> classesMatchings = m.getClassAlignmentSet();
		if( classesMatchings != null ) {
			Iterator<Alignment> alignmentIter = classesMatchings.iterator();
			while( alignmentIter.hasNext() ) {
				Alignment alignment = alignmentIter.next();
				// TODO: Make AbstractMatchers work on Resource instead of Node.
				OntResource e1 = (OntResource) alignment.getEntity1().getResource().as(OntResource.class);  // translate from Node to OntResource
				OntResource e2 = (OntResource) alignment.getEntity2().getResource().as(OntResource.class);  // translate from Node to OntResource
				if( hashMap.containsKey(e1) && hashMap.containsKey(e2) ) {
					// great, our hashmap contains both entities
					Canvas2Vertex n1 = hashMap.get(e1);
					Canvas2Vertex n2 = hashMap.get(e2);
					
					
					LegacyMapping edge = new LegacyMapping( n1, n2, null, m.getID(),  Utility.getNoDecimalPercentFromDouble(alignment.getSimilarity()) );
					
					matcherGraph.insertEdge(edge);
					
					
				} else {
					// the hashMap doesn't contain the source or the target node.
					// something is wrong.
					// no idea how to fix this problem.
					
					// log it
					Logger log = Logger.getLogger(this.getClass());
					log.setLevel(Level.WARN);
					if( !hashMap.containsKey(e1) ) log.warn("Cannot find OntResource: " + e1.toString() + ".  Node container is: " + alignment.getEntity1().toString() );
					if( !hashMap.containsKey(e2) ) log.warn("Cannot find OntResource: " + e2.toString() + ".  Node container is: " + alignment.getEntity2().toString() );
					
				}
			}
		}

		// Get the Properties alignments.
		
		AlignmentSet<Alignment> propertiesMatchings = m.getPropertyAlignmentSet();
		if( propertiesMatchings != null ) {
			Iterator<Alignment> alignmentIter = propertiesMatchings.iterator();
			while( alignmentIter.hasNext() ) {
				Alignment alignment = alignmentIter.next();
				// TODO: Make AbstractMatchers work on Resource instead of Node.
				OntResource e1 = (OntResource) alignment.getEntity1().getResource().as(OntResource.class);  // translate from Node to OntResource
				OntResource e2 = (OntResource) alignment.getEntity2().getResource().as(OntResource.class);  // translate from Node to OntResource
				if( hashMap.containsKey(e1) && hashMap.containsKey(e2) ) {
					// great, our hashmap contains both entities
					Canvas2Vertex n1 = hashMap.get(e1);
					Canvas2Vertex n2 = hashMap.get(e2);
					
					
					LegacyMapping edge = new LegacyMapping( n1, n2, null, m.getID(), Utility.getNoDecimalPercentFromDouble(alignment.getSimilarity()) );
					
					matcherGraph.insertEdge(edge);
					
					
				} else {
					// the hashMap doesn't contain the source or the target node.
					// something is wrong.
					// no idea how to fix this problem.
					
					// log it
					Logger log = Logger.getLogger(this.getClass());
					log.setLevel(Level.WARN);
					if( !hashMap.containsKey(e1) ) log.warn("Cannot find OntResource: " + e1.toString() + ".  Node container is: " + alignment.getEntity1().toString() );
					if( !hashMap.containsKey(e2) ) log.warn("Cannot find OntResource: " + e2.toString() + ".  Node container is: " + alignment.getEntity2().toString() );
					
				}
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
	@Override
	public void mouseClicked( MouseEvent e ) {
		// BUTTON1 = Left Click Button, BUTTON2 = Middle Click Button, BUTTON3 = Right Click Button

		Graphics g = vizpanel.getGraphics();   // used for any redrawing of nodes
		ArrayList<Canvas2Vertex> visibleVertices = vizpanel.getVisibleVertices();
		
		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		
		switch( e.getButton() ) {
		
			// because of the way Java (and most any platform) handles the difference between single and double clicks,
			// the single click action must be "complementary" to the double click action, as when you double click a 
			// single click is always fired just before the double click is detected.  
			// There is no way around this.  A single click event will *always* be fired just before a double click.
			
			// So then:
			//		- LEFT button SINGLE click = select NODE (or deselect if clicking empty space)
			//		- LEFT button DOUBLE click = line up two nodes by their mapping (do nothing if it's empty space)<- TODO  

		case MouseEvent.BUTTON1:
			if( e.getClickCount() == 2 ) {  // double click with the left mouse button
				log.debug("Double click with the LEFT mouse button detected.");
				//do stuff
			} else if( e.getClickCount() == 1 ) {  // single click with left mouse button
				if( hoveringOver == null ) {
					// we have clicked in an empty area, clear all the selected nodes
					Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
					while( nodeIter.hasNext() ) {
						LegacyNode selectedNode = nodeIter.next();
						selectedNode.setSelected(false); // deselect the node
						if( visibleVertices.contains( (Canvas2Vertex) selectedNode ) ) {
							// redraw only if it's currently visible
							selectedNode.clearDrawArea(g);
							selectedNode.draw(g);
						}
					}
					selectedNodes.clear();
				} else {
					// user clicked over a node.
					if( e.isControlDown() ) {
						// if the user control clicked (CTRL+LEFTCLICK), we have to add this node to the list of selected nodes.
						if( selectedNodes.contains(hoveringOver) ) { // if it already is in the list, remove it
							selectedNodes.remove(hoveringOver);
							hoveringOver.setSelected(false);
						} else { // it's not in the list already, add it
							hoveringOver.setSelected(true);
							selectedNodes.add((LegacyNode) hoveringOver);
						}
						
						hoveringOver.clearDrawArea(g);
						hoveringOver.draw(g);
					} else { // control is not pressed, clear any selections that there may be, and select single node
						
						Iterator<LegacyNode> nodeIter = selectedNodes.iterator();
						while( nodeIter.hasNext() ) {
							LegacyNode selectedNode = nodeIter.next();
							selectedNode.setSelected(false); // deselect the node
							if( visibleVertices.contains( (Canvas2Vertex) selectedNode ) ) {
								// redraw only if it's currently visible
								selectedNode.clearDrawArea(g);
								selectedNode.draw(g);
							}
						}
						selectedNodes.clear();
						
						// select single node
						hoveringOver.setSelected(true);
						selectedNodes.add( (LegacyNode)hoveringOver);
						hoveringOver.clearDrawArea(g);
						hoveringOver.draw(g);
					}
				}
				
			}
			break;
			
		case MouseEvent.BUTTON2:
			if( e.getClickCount() == 2 ) {
				// double click with the middle mouse button.
				log.debug("Double click with the MIDDLE mouse button detected.");
				//do stuff
			} else if( e.getClickCount() == 1 ) {
				// middle click, print out debugging info
				if( hoveringOver != null ) { // relying on the hover code in MouseMove
					log.debug("\nResource: " + hoveringOver.getObject().r + 
							"\nHashCode: " + hoveringOver.getObject().r.hashCode());
					
				}
				//log.debug("Single click with the MIDDLE mouse button detected.");
			}
			break;
		
		
		case MouseEvent.BUTTON3:
			if( e.getClickCount() == 2 ) {
				// double click with the right mouse button.
				log.debug("Double click with the RIGHT mouse button detected.");
				//do stuff
			} else if( e.getClickCount() == 1 ) {
				// singleclick
				log.debug("Single click with the RIGHT mouse button detected.");
			}
			break;
		}

		g.dispose(); // dispose of this graphics element, we don't need it anymore
	}
		

	
	private Canvas2Vertex hoveringOver;
	
	public void mouseMoved(MouseEvent e)    {
		Graphics g = vizpanel.getGraphics();
		ArrayList<Canvas2Vertex> visibleVertices = vizpanel.getVisibleVertices();
		Iterator<Canvas2Vertex> vertIter = visibleVertices.iterator();
		boolean hoveringOverEmptySpace = true;
		while( vertIter.hasNext() ) {
			Canvas2Vertex vertex = vertIter.next();
			if( vertex instanceof LegacyNode )    // we only care about legacy nodes
			if( vertex.contains(e.getPoint()) ) {
				// we are hovering over vertex
				hoveringOverEmptySpace = false;
				// first, remove the hover from the last element we were hovering over
				if( hoveringOver == vertex ) {
					// we are still hoovering over this element, do nothing
					break;
				} else if( hoveringOver != null ) {
					// we had been hovering over something, but now we're not
					hoveringOver.setHover(false);
					hoveringOver.clearDrawArea(g);
					hoveringOver.draw(g);
				}
				
				hoveringOver = vertex;
				hoveringOver.setHover(true);
				hoveringOver.clearDrawArea(g);
				hoveringOver.draw(g);
				break;
				
			}
		}
		
		if( hoveringOverEmptySpace && hoveringOver != null) {
			// clear the hover
			hoveringOver.setHover(false);
			hoveringOver.clearDrawArea(g);
			hoveringOver.draw(g);
			hoveringOver = null;
		}
		
		g.dispose();
		
		
	}
		

}
