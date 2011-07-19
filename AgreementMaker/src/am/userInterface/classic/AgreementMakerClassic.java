package am.userInterface.classic;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;

import am.userInterface.MatchersControlPanel;
import am.userInterface.VisualizationPanel;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.graphical.MappingData;
import am.userInterface.canvas2.utility.Canvas2Edge;
import am.userInterface.canvas2.utility.Canvas2Vertex;
import am.userInterface.canvas2.utility.CanvasGraph;
import am.userInterface.canvas2.utility.GraphLocator;
import am.userInterface.find.FindInterface;

/**
 * This panel is the classic view of AgreementMaker.
 * 
 * It displays a Canvas2 next to two side panes, and a 
 * matchers control panel at the bottom of the window.
 * 
 */
public class AgreementMakerClassic extends JPanel implements FindInterface {

	private static final long serialVersionUID = -1913594055550719146L;
	
	private JSplitPane splitPane;
	private JSplitPane outerSplitPane;
	private JScrollPane scrollPane;
	private Canvas2 canvas;
	private MatchersControlPanel matcherControlPanel;
	
	/** This constructor creates a new Canvas2 object which will be contained in this panel. */
	public AgreementMakerClassic() { super(); canvas = new Canvas2(); initialize(); }
	
	/** This constructor allows the user to specify a custom Canvas2 */
	public AgreementMakerClassic(Canvas2 canvas) { super(); this.canvas = canvas; initialize(); }
	
	/** This method executes the operations common to all constructors. */
	private void initialize() {
		scrollPane = new JScrollPane();
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		setLayout( new BorderLayout() );
		
		scrollPane.setViewportView(canvas);
		canvas.setScrollPane(scrollPane);
		canvas.setFocusable(true);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1.0);
		//splitPane.setMinimumSize(new Dimension(100,300));
		//splitPane.setPreferredSize(new Dimension(640,480));
		//splitPane.getLeftComponent().setPreferredSize(new Dimension(300,300));

		matcherControlPanel = new MatchersControlPanel();

		outerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane,matcherControlPanel);
		outerSplitPane.setOneTouchExpandable(true);
		outerSplitPane.setResizeWeight(1.0);
		//outerSplitPane.setDividerLocation(0.75);
		
		add(outerSplitPane, BorderLayout.CENTER);
		
		//add(matcherControlPanel, BorderLayout.PAGE_END);
	}
	
 
	public JSplitPane getSplitPane() { return splitPane; }
	
	public VisualizationPanel getVisualizationPanel() { return canvas; }
	
	public MatchersControlPanel getMatchersControlPanel() { return matcherControlPanel; }
	public JScrollPane getScrollPane() { return scrollPane; }
	public JSplitPane getOuterSplitPane() { return outerSplitPane; }
	
	
	
	
	
	/* ****************************************************************************************************************************************
	 * ****************************************************************************************************************************************
	 * ****************************************************************************************************************************************
	 * *********************************************** FIND INTERFACE METHODS AND STATE VARIABLES *********************************************
	 * ****************************************************************************************************************************************
	 * ****************************************************************************************************************************************
	 * ****************************************************************************************************************************************
	 * ****************************************************************************************************************************************
	 */
	@Override
	public void displayCurrentStraw() {
		
		if( selectedNode != null ) {
			selectedNode.setSelected(false);
			selectedNode = null;
		}
		
		// oookk .. so, we have to display the current straw because a match was found!
		JViewport vp = scrollPane.getViewport();
		
		//vizpanel.getScrollPane().getViewport().setViewPosition( new Point(vizpanel.getScrollPane().getViewport().getLocation().x, 
		//		hoveringOver.getBounds().y - vizpanel.getScrollPane().getViewport().getHeight()/2 ));
		
		if( verticesSearched == false ) {
			// it must be a vertex that we have found
			Rectangle bounds = currentVertex.getBounds();
			int midpointX = bounds.x+ bounds.width/2;
			int midpointY = bounds.y+ bounds.height/2;
			vp.setViewPosition( new Point( Math.max( midpointX - vp.getWidth()/2, 0), Math.max(midpointY - vp.getHeight()/2,0) ));  // display the needle at the center of the viewport
			// since this is a vertex, select it.
			currentVertex.setSelected(true);
			selectedNode = currentVertex;
			canvas.repaint();
		} else {
			// it is an edge that we have found.
			int midpointX = currentEdge.getObject().x + currentEdge.getObject().width/2;
			int midpointY = currentEdge.getObject().y + currentEdge.getObject().height/2;
			vp.setViewPosition( new Point( midpointX - vp.getWidth()/2, midpointY - vp.getHeight()/2 ));  // display the needle at the center of the viewport
		}
		
	}
	
	// STATE Variables for the FindInterface: TODO: IMPLEMENT THIS AS AN ENUMERATION (convert all the booleans to an enumeration) !!!!! - cosmin
	private Iterator<CanvasGraph> graphIter = null;
	private CanvasGraph currentGraph = null;
	private Iterator<Canvas2Vertex> vertexIter = null;
	private Iterator<Canvas2Edge> edgeIter = null;
	private Canvas2Vertex currentVertex = null;
	private Canvas2Edge currentEdge = null;
	private boolean reachedEnd = false;
	private boolean verticesSearched = false;
	private boolean edgesSearched = false;
	private boolean currentVertexSearchedName = false;
	private boolean currentVertexDone = false;
	private boolean currentEdgeDone = false;
	private Canvas2Vertex selectedNode = null;
	
	@Override
	public String getNextStraw() {
		
		while(true) {  // the reason we need this while loop is for the special case when we come to the end of a graph and need to continue to a new one.
			
			if( reachedEnd == true ) {
				// we are done
				return null;
			}
			
			if( graphIter == null ) {  // graphIter is null when we first start
				// get the next graph in line
				currentGraph = null;
				graphIter = canvas.getGraphs().iterator();
				while( graphIter.hasNext() ) {
					CanvasGraph graph = graphIter.next();
					if( graph.getGraphType() == GraphLocator.GraphType.LAYOUT_GRAPH ||
						graph.getGraphType() == GraphLocator.GraphType.LAYOUT_GRAPH_IGNORE_BOUNDS ) {
						// ignore LAYOUT graphs
						continue;
					} else {
						currentGraph = graph;
						verticesSearched = false;
						edgesSearched = false;
						break;
					}
				}
				if( currentGraph == null ) {
					// we are out of graphs to search through
					reachedEnd = true;
					return null;
				}
			}
			
			
			// we have a graph to search through
			
			
			// 1) Search through the vertices.
			if( verticesSearched == false ) {
		
				// do we have a vertex iterator?
				if( vertexIter == null ) {
					vertexIter = currentGraph.vertices(); // setup the vertex iterator
					currentVertex = null;
				}
	
				// do we have a current Vertex?
				if( currentVertex == null ) {
					if( vertexIter.hasNext() ) {
						currentVertex = vertexIter.next(); // get the next vertex
						currentVertexSearchedName = false; // reset the state variables
						currentVertexDone = false;
					}
				}
				
				
				// we have a current Vertex, but have we searched through it already?
				if( currentVertexDone == true ) {
					if( vertexIter.hasNext() ) {
						currentVertex = vertexIter.next(); // get the next vertex
						currentVertexSearchedName = false; // reset the state variables
						currentVertexDone = false;
					}
					else currentVertex = null;  // we don't have anymore Vertices
				}
				
				// ok, here we either are done with the vertices .. or we continue searching 
				if( currentVertex == null ) verticesSearched = true;
				else {
					if( !currentVertexSearchedName ) {
						currentVertexSearchedName = true;
						GraphicalData gd = currentVertex.getGraphicalData();
						if( gd == null ) { return ""; } //  we have an element that has no graphical data
						if( gd.r == null ) { return ""; } // this element doesn't have a resource associated with it, ignore it.
						String name = gd.r.getLocalName();
						if( name == null ) { return ""; }
						return name;
					}
					
					if( !currentVertexDone ) {
						currentVertexDone = true;
						GraphicalData gd = currentVertex.getGraphicalData();
						if( gd == null ) { return ""; } //  we have an element that has no graphical data
						if( gd.r == null ) { return ""; } // this element doesn't have a resource associated with it, ignore it.
						String label = gd.r.getLabel(null);
						if( label == null ) return "";
						return label;
					}
						
				}
				
			}
			
			
			// 2) Since we are done with the vertices, lets search through the labels of the edges
			if( edgesSearched == false ) {
				
				// do we have an edge Iterator?
				if( edgeIter == null ) {
					edgeIter = currentGraph.edges(); // setup the edge iterator
					currentEdge = null;
				}
				
				// do we have a current edge?
				if( currentEdge == null ) {
					while( edgeIter.hasNext() ) {
						Canvas2Edge edge = edgeIter.next(); // get the next edge
						
						// does the edge have a label?? (only mappings have a label at the moment)
						if( edge.getObject().type == GraphicalData.NodeType.MAPPING ) {
							currentEdgeDone = false;
							currentEdge = edge;
							break;
						}
					}
				}
				
				// we have a currentEdge, but are we done with it?
				if( currentEdgeDone == true ) {
					while ( edgeIter.hasNext() ) {
						Canvas2Edge edge = edgeIter.next(); // get the next edge
						
						// does the edge have a label?? (only mappings have a label at the moment)
						if( edge.getObject().type == GraphicalData.NodeType.MAPPING ) {
							currentEdgeDone = false;
							currentEdge = edge;
							break;
						}
					} 
					if( currentEdgeDone = true ) currentEdge = null; // we don't have anymore edges
				}
				
				// ok, so we are either done with the edges, or we continue to the next one
				if( currentEdge == null ) edgesSearched = true;
				else {
					if( !currentEdgeDone ){
						currentEdgeDone = true;
						// does the current edge have a label??
						GraphicalData gd = currentEdge.getObject(); 
						if( gd instanceof MappingData ) {  // right now only mappings have labels
							return ((MappingData)gd).label;
						} else {
							return ""; // no label  ( should NEVER be here, since we do the checks in the previous IF statements. )
						}
					}
				}
			}
			
			// oooo kkkkk .. so, if we reach here we are at the end of a graph, we need to go to the next graph
			if( graphIter.hasNext() ) {
				currentGraph = graphIter.next();
				verticesSearched = false;  // reset all the state variables
				edgesSearched = false;
				vertexIter = null;
				edgeIter = null;
			} else {
				// no more graphs!!!!
				reachedEnd = true;
				return null;
			}
			
		} // end while(true)
	
	}
	
	@Override
	public boolean hasMoreStraw() {
		return !reachedEnd;
	}
	
	@Override
	public void resetSearch() {
		graphIter = null;
		currentGraph = null;
		vertexIter = null;
		edgeIter = null;
		currentVertex = null;
		currentEdge = null;
		reachedEnd = false;
		verticesSearched = false;
		edgesSearched = false;
		currentVertexSearchedName = false;
		currentVertexDone = false;
		currentEdgeDone = false;
		
		if( selectedNode != null ) {
			selectedNode.setSelected(false);
			selectedNode = null;
			canvas.repaint();
		}
		
	}

	
	
	
}
