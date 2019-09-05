package am.ui.canvas2.utility;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import am.ui.Colors;
import am.ui.canvas2.Canvas2;
import am.ui.canvas2.graphical.GraphicalData;
import am.ui.canvas2.graphical.GraphicalData.NodeType;
import am.ui.canvas2.nodes.LegacyMapping;
import am.ui.ontology.OntologyConceptGraphics;
import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A Canvas2Node holds graphical information about an ontology Resource.
 * 
 *
 * 
 * Information like:
 * 	- What is the x, y position of this node on the Canvas2.
 *  - What is the width, height of this node on the Canvas2.
 *  - How does it get drawn on the screen?
 * 
 * @author cosmin
 *
 */

// TODO: Canvas2Vertex should not expose the getObject() method, but instead should
// 		 have getters/setters for all the data in the object().
//       But that would not allow you to use any of the classes that extend GraphicalData.
//		 So that would have to be left up to classes that extend Canvas2Vertex.

public class Canvas2Vertex extends DirectedGraphVertex<GraphicalData, GraphicalData> implements OntologyConceptGraphics {  // we have to extend the DGVertex because we need access to the adjacency lists


	//protected GraphicalData d;  // all the (d)ata is stored in this class (the name is short because it is used ALOT)
	//private CanvasGraph graph = null; // the graph that this vertex belongs to
	
	
	public Canvas2Vertex( GraphicalData g ) {
		super(g);
	}
	
	public Rectangle getBounds() { return d.getBounds(); }
	
	public void draw(Graphics g) { /* TODO: This function must be implemented in the subclass. */	}
	
	public void setSelected( boolean sel ) { if( d != null) d.selected = sel; }
	public boolean isSelected() { if( d != null ) return d.selected; return false; }
	public void setHover( boolean hover ) { d.hover = hover; }
	
	public GraphicalData getGraphicalData() { return d; }
	public Resource      getResource()      { if( d == null ) return null; return d.r; } // shortcut function
	
	public void recalculateWidth() { /* implemented in subclass */  }  // used when the width of a node changes
	
	public void updateBounds( int x, int y, int width, int height ) { d.x = x; d.y = y; d.width = width; d.height = height; };
	
	
	public void setVisible(boolean vis ) { d.visible = vis; }
	public boolean isVisible() { return d.visible; }
	public boolean isVisible(Rectangle bounds) {
		
		if( d == null ) return false;  // if we don't have any graphical data, we are not visible.
		
		//if( graph != null ) { if( !graph.visible ) return false; }  // if the graph isn't visible, any of its elements aren't visible either
		
		if( !d.visible ) return false;  // if our node was set to be invisible, so no need to check bounds
		if( bounds == null ) return true; // if no retangle is passed in and we are visible, return true.
		
		int x1 = (int) bounds.getX();
		int y1 = (int) bounds.getY();
		int w1 = (int) bounds.getWidth();
		int h1 = (int) bounds.getHeight();
		
		if( d.x+d.width < x1 ) return false; // the graph is to the left of the viewport
		if( d.y+d.height < y1 ) return false; // the graph is above the viewport
		if( d.x > x1+w1 ) return false; // the graph is to the right of the viewport
		if( d.y > y1+h1 ) return false; // the graph is below the viewport
		
		return true;
	}
	
	
	/**
	 * When someone collapses a tree, we set all the children to Visible=false.
	 */
	public void hideChildren() {
		
	}

	/*
	 * Move this vertex.
	 */
	public void move(int xoffset, int yoffset) {
		d.x += xoffset;
		d.y += yoffset;
	}

	public void removeOutEdge(Canvas2Edge edge) { edgesOut.remove(edge); }
	public void removeInEdge(Canvas2Edge edge) { edgesIn.remove(edge); }

	// returns true if the bounds of this vertex contains the given point.  False otherwise.
	public boolean contains(Point point) {
		
		if( point.x < d.x || point.x > d.x + d.width ) return false;
		if( point.y < d.y || point.y > d.y + d.height) return false;
		return true;
	}

	//public void setGraph(CanvasGraph g) { graph = g; }

	public void clearDrawArea(Graphics g) {
		g.setColor(Colors.background);
		g.fillRect(d.x, d.y, d.width, d.height);
	}
	
	@Deprecated
	public boolean hasMappings() {
		int mappings = 0;
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeIter = edgesInIter();
		while( edgeIter.hasNext() ) {
			Canvas2Edge edge = (Canvas2Edge) edgeIter.next();
			if( edge.getObject().type == NodeType.MAPPING ) mappings++;
		}
		
		edgeIter = edgesOut.iterator();
		while( edgeIter.hasNext() ) {
			Canvas2Edge edge = (Canvas2Edge) edgeIter.next();
			if( edge.getObject().type == NodeType.MAPPING ) mappings++;
		}
		
		if( mappings == 0 ) return false;
		else return true;
	}
	
	// return an ArrayList of all the edges that are MAPPINGs associated with this node
	public ArrayList<LegacyMapping> getMappings() {
		ArrayList<LegacyMapping> mappings = new ArrayList<LegacyMapping>();
		
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeIter = edgesIn.iterator();
		while( edgeIter.hasNext() ) {
			Canvas2Edge edge = (Canvas2Edge) edgeIter.next();
			if( edge.getObject().type == NodeType.MAPPING ) mappings.add( (LegacyMapping) edge);
		}
		
		edgeIter = edgesOut.iterator();
		while( edgeIter.hasNext() ) {
			Canvas2Edge edge = (Canvas2Edge) edgeIter.next();
			if( edge.getObject().type == NodeType.MAPPING ) mappings.add( (LegacyMapping) edge);
		}
		
		return mappings;
	}
	
	
	// STACK of Visibility
	private boolean pushedVisibility; // pushedVisibility is used in order to save an old visibility value, and then use a new one.
									  // the old value can then be popped back
	
	public void pushVisibility( boolean vis ) {
		pushedVisibility = d.visible;
		d.visible = vis;
	}
	public void popVisibility() {
		d.visible = pushedVisibility;
	}
	
	// stack of position
	private boolean savePosition = false; // if we push an X,Y position, we don't want to overwrite it on subsequent push calls.
	private int pushedX, pushedY;
	public void pushXY(int X, int Y) { 
		if( !savePosition ) { pushedX = d.x; pushedY = d.y;  d.x = X; d.y = Y; savePosition = true;}
	}
	public void popXY() { d.x = pushedX; d.y = pushedY; savePosition = false; }

	@Override
	public Class<?> getImplementationClass() { return Canvas2.class; }
	
}
