package am.userInterface.canvas2.utility;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.Core;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.utility.GraphLocator.GraphType;
import am.utility.DirectedGraph;

public class CanvasGraph extends DirectedGraph<Canvas2Edge, Canvas2Vertex> {

	// The graphical bounds of this graph.  Initially set so isVisible() will return false.
	int x = 1;
	int y = 1;
	int w = 0;
	int h = 0;
	
	// Besides bounds checking, we can also set this graph to be invisible.
	boolean visible = true;

	private int ontologyID;		// the ID of the ontology that this graph is associated with
	private GraphLocator.GraphType graphType; // the type of concepts this graph depicts
	
	
	public CanvasGraph( GraphLocator.GraphType gT, int ontID ) {
		ontologyID = ontID;
		graphType = gT;
	}
	
	public int getID() { return ontologyID; }
	public GraphLocator.GraphType getGraphType() { return graphType; }
	
	public void setVisible( boolean vis ) {	
		visible = vis; 
		
		// the visibility of all the graph's elements must be changed also
		// not because of Canvas2.paintComponent() but because of LegacyNode
		Iterator<Canvas2Vertex> vertIter = vertices();
		while( vertIter.hasNext() ) {
			vertIter.next().setVisible(vis);
		}
		
		Iterator<Canvas2Edge> edgeIter = edges();
		while( edgeIter.hasNext() ) {
			edgeIter.next().setVisible(vis);
		}
		
	}
	
	public boolean isVisible(Rectangle bounds) {
		
		if( graphType == GraphType.MATCHER_GRAPH ) return true; // matcher graphs are always visible (fixes a display bug, with positive sloping mapping lines) - Cosmin 2/13/2010
		
		if( !visible ) return false;  // if our graph was set to be invisible, so no need to check bounds
		
		int x1 = (int) bounds.getX();
		int y1 = (int) bounds.getY();
		int w1 = (int) bounds.getWidth();
		int h1 = (int) bounds.getHeight();
		
		if( x+w < x1 ) return false; // the graph is to the left of the viewport
		if( y+h < y1 ) return false; // the graph is above the viewport
		if( x > x1+w1 ) return false; // the graph is to the right of the viewport
		if( y > y1+h1 ) return false; // the graph is below the viewport
		
		return true;
	}


	
	public void insertDirectedEdge( Canvas2Vertex source, Canvas2Vertex target, GraphicalData o) {
		Canvas2Edge edge = new Canvas2Edge(source, target, o);
		//edge.setGraph(this);
		edges.add(edge);
		source.addOutEdge(edge);
		target.addInEdge(edge);
		updateBounds(o);
	}
	
	public void insertEdge( Canvas2Edge edge ) {
		//edge.setGraph(this);
		edges.add(edge);
		edge.getOrigin().addOutEdge(edge);
		edge.getDestination().addInEdge(edge);
		updateBounds(edge.getObject());
	}
	
	public void insertVertex( Canvas2Vertex vert ) {
		//vert.setGraph(this);
		vertices.add(vert);
		updateBounds(vert.getObject());
	}
	
	public Canvas2Vertex insertVertex( GraphicalData o ) {
		Canvas2Vertex vert = new Canvas2Vertex(o);
		//vert.setGraph(this);
		vertices.add(vert);
		updateBounds(o);
		return vert;
	}

	/*
	 * Usually the recalculateBounds() method is called when the elements of a graph have been resized or moved. 
	 */
	public void recalculateBounds() {
		
		// reset the bounds
		x = 0; y = 0; w = 0; h = 0;
		
		// and recalculate them from the vertices and edges
		Iterator<Canvas2Vertex> vertIter = vertices.iterator();
		while( vertIter.hasNext() ) updateBounds(vertIter.next().getObject());
		
		Iterator<Canvas2Edge> edgeIter = edges.iterator();
		while( edgeIter.hasNext() ) updateBounds(edgeIter.next().getObject());
	}
	
	/**
	 * Everytime a vertex or edge is added to the graph, we update the graphical bounds of this graph,
	 * which are used in the isVisible() calculation.
	 * 
	 * If a vertex or edge is added that is out of the current bounds, the bounds grow in the direction
	 * of the vertex or edge. 
	 */
	private void updateBounds( GraphicalData o ) {
		Logger logger = null;
		
		if( Core.DEBUG ) {
			logger = Logger.getLogger(this.getClass());
			logger.setLevel(Level.DEBUG);
		}
		
		if( o == null ) return;
		Rectangle bounds = o.getBounds();
		
		
		int x1 = (int) bounds.getX();
		int y1 = (int) bounds.getY();
		int w1 = (int) bounds.getWidth();
		int h1 = (int) bounds.getHeight();

		if( vertices.size() + edges.size() == 1 ) {
			// we have only one element in the graph, the element is the bounds
			x = x1; y = y1; w = w1; h = h1;
			if( Core.DEBUG ) logger.debug("Graph bounds reset to: (x = " + Integer.toString(x) + ", " +
					   											  "y = " + Integer.toString(y) + ", " +
					   											  "w = " + Integer.toString(w) + ", " +
					   											  "h = " + Integer.toString(h) + ")");
			return;
		}

		
		int l  = x + w;  // the highest x coordinate of the current bounds
		int l1 = x1 + w1;  // the highest x coordinate of the element
		int m  = y + h;  // the highest y coordinate of the current bounds
		int m1 = y1 + h1; // the highest y coordinate of the element
		
		boolean mod = false;
		if( x1 < x ) { // if the new element is to the left of the current bounds.
			x = x1; // move the bounds to the left
			w += x - x1;  // expand the graph so the right bound is where it was before
			mod=true; 
		}  
		if( y1 < y ) { // if the new element is above the current bounds.
			y = y1; // move the graph up
			h += y - y1; // expand the graph down
			mod=true; 
		} 
		if( l1 > l ) { // if the new element is to the right of the bounds
			w += l1 - l; // expand the graph to the right
			mod=true; 
		}  
		if( m1 > m ) { // if the new element is below the current bounds
			h += m1 - m; // expand the graph down
			mod=true; 
		}  
		
		if( Core.DEBUG )
			if(mod) logger.debug("Graph bounds updated to: (x = " + Integer.toString(x) + ", " +
											   "y = " + Integer.toString(y) + ", " +
											   "w = " + Integer.toString(w) + ", " +
											   "h = " + Integer.toString(h) + ")");
	}
	
	public Rectangle getBounds() {	return new Rectangle( x, y, w, h);	}
	
	
	/**
	 * This function moves all the elements of a graph by an xoffset and yoffset.
	 */
	public void moveGraph( int xoffset, int yoffset ) {
		
		Iterator<Canvas2Vertex> vertIter = vertices.iterator();  // move the vertices
		while( vertIter.hasNext() ) {  vertIter.next().move(xoffset, yoffset);	}
		
		Iterator<Canvas2Edge> edgeIter = edges.iterator(); // move the edges
		while( edgeIter.hasNext() ) {  edgeIter.next().move(xoffset, yoffset);	}
	}
	
	/**
	 * Get the combined bounds of all the graphs in the list passed to the method.
	 * This function is very similar to updateBounds() in its code.
	 */
	public static Rectangle getCombinedBounds( ArrayList<CanvasGraph> graphList ) {
		Rectangle combinedBounds;
		
		if( graphList.size() == 0 ) return new Rectangle(0,0,0,0); // if we're passed in an empty list, return zero filled bounds
		
		
		Iterator<CanvasGraph> graphIter = graphList.iterator();
		combinedBounds = graphIter.next().getBounds();  // the first element sets up the combinedBounds.
		
		while( graphIter.hasNext() ) { // onward from the second element
			CanvasGraph currentGraph = graphIter.next();
			Rectangle graphBounds = currentGraph.getBounds();
			if( graphBounds.x < combinedBounds.x ) { // if the currentGraph is to the left of the combinedBounds
				combinedBounds.x = graphBounds.x; // move the bounds to the the left
				combinedBounds.width += combinedBounds.x - graphBounds.x; // and expand to the right to where the bounds were before
			} 
			if( graphBounds.y < combinedBounds.y ) {  // if the currentGraph is above the combinedBounds
				combinedBounds.y = graphBounds.y; // move the bounds up
				combinedBounds.height += combinedBounds.y - graphBounds.y; // and expand down to be where the bounds were before
			}
			
			if( (graphBounds.x + graphBounds.width) > (combinedBounds.x + combinedBounds.width) ) { // if the currentGraph lies to the right of the combinedBounds 
				combinedBounds.width += (graphBounds.x + graphBounds.width) - (combinedBounds.x + combinedBounds.width); // expand the bounds right
			}
			
			if( (graphBounds.y + graphBounds.height) > (combinedBounds.y + combinedBounds.height) ) { // if the currentGraph lies below the combinedBounds 
				combinedBounds.height += (graphBounds.y + graphBounds.height) - (combinedBounds.y + combinedBounds.height) ;  // expand the bounds down
			}
		}
		
		return combinedBounds;
	}

	public void detachEdges() {
		// detach the edges 
		Iterator<Canvas2Edge> edgeIter = edges();
		while( edgeIter.hasNext() ) {
			Canvas2Edge edge = edgeIter.next();
			Canvas2Vertex source = (Canvas2Vertex) edge.getOrigin();
			Canvas2Vertex target = (Canvas2Vertex) edge.getDestination();
			source.removeOutEdge(edge);
			target.removeInEdge(edge);
		}
	}
}
