package am.userInterface.canvas2.utility;

import java.awt.Graphics;
import java.awt.Rectangle;

import am.userInterface.canvas2.graphical.GraphicalData;
import am.utility.DirectedGraphEdge;

public class Canvas2Edge extends DirectedGraphEdge<GraphicalData> {

	//private CanvasGraph graph = null; // the graph that this edge belongs to

	public Canvas2Edge(Canvas2Vertex orig, Canvas2Vertex dest, GraphicalData o) {
		super(orig, dest, o);
	}
	
	public void draw(Graphics g) { /* This function must be implemented in the subclass. */	}
	public void setVisible( boolean v ) { d.visible = v; }
	public boolean isVisible(Rectangle bounds) {
		//if( graph != null ) { if( !graph.visible ) return false; }  // if the graph isn't visible, any of its elements aren't visible either
		if( d == null ) return false; // no GraphicalData -> no visibility
		
		if( !d.visible ) return false;  // if our node was set to be invisible, so no need to check bounds
		
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
	
	//public void setGraph(CanvasGraph g ) { graph = g; } // the graph to which this egde belongs to
	/**
	 * Move this edge, by x offset and y offset.  Offsets can be negative.
	 */
	public void move(int xoffset, int yoffset) {
		d.x += xoffset;
		d.y += yoffset;
	}
}