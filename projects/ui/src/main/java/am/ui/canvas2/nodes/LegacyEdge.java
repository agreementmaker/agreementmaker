package am.ui.canvas2.nodes;

import java.awt.Graphics;
import java.awt.Rectangle;

import am.ui.Colors;
import am.ui.canvas2.graphical.GraphicalData;
import am.ui.canvas2.layouts.LegacyLayout;
import am.ui.canvas2.utility.Canvas2Edge;

/**
 * This is a legacy edge.  It forms the link between two Legacy Nodes.
 * 
 * It also draws the edge on the screen.
 * @author cosmin
 *
 */

public class LegacyEdge extends Canvas2Edge {
		
	public LegacyEdge(LegacyNode orig, LegacyNode dest, GraphicalData o, LegacyLayout layout) {
		super(orig, dest, o);
		
		if( o == null ) {
			GraphicalData parentd = orig.getGraphicalData();
			GraphicalData d = dest.getGraphicalData();
			if( parentd == null || d == null ) { return; }
		
			int startX = parentd.x+4;
			int startY = parentd.y+(parentd.height/2)-4;
			
			int endX = d.x+LegacyNode.circlePadding;
			int endY = d.y+(d.height/2);
			
			int width = endX - startX;
			int height = endY - startY;
			
			if( width < 0 && height >= 0 ) { // flip the points vertically to make the width positive
				int temp = startX;  // swap Xs
				startX = endX;
				endX = temp;
				width = endX - startX;
			} else if ( width >= 0 && height < 0 ) {
				int temp = startY;  // swap Ys
				startY = endY;
				endY = temp;
				height = endY - startY;
			} else if( width < 0 && height < 0 ) {
				int temp = startX;  // swap both
				startX = endX;
				endX = temp;
				temp = startY;
				startY = endY;
				endY = temp;
				width = endX - startX;
				height = endY - startY;
			}

			GraphicalData gr = new GraphicalData(startX, startY, width, height, GraphicalData.NodeType.LAYOUT_EDGE, layout, parentd.ontologyID );
			super.setObject(gr);
		}

	}
	
	public LegacyNode getDestination() { return (LegacyNode) super.getDestination(); }
	public LegacyNode getOrigin()      { return (LegacyNode) super.getOrigin(); }
	
	@Override
	public void draw(Graphics g) {
		
		// Draw the edge lines. (2 lines)

		LegacyNode origin = getOrigin();
		LegacyNode destination = getDestination();
		
		if( origin == null || destination == null ) { return; }
		
		GraphicalData parent = origin.getGraphicalData();
		GraphicalData child = destination.getGraphicalData();
		
		
		
		int parentXstart = parent.x+4;
		int parentYstart = parent.y+(parent.height/2)+4;
		
		int cornerX = child.x-((LegacyLayout)child.layout).getDepthIndent()+4; 
		int cornerY = child.y+(child.height/2);
		
		int childXend = child.x+LegacyNode.circlePadding;
		int childYend = child.y+(child.height/2);

		g.setColor(Colors.foreground); // TODO: This color should be separate from the foreground (maybe)
		g.drawLine( parentXstart, parentYstart , cornerX , cornerY  );  // line from parent to the level of the child
		g.drawLine( cornerX, cornerY, childXend, childYend);  // line going to the left to the child
		
		// This last step is a hack in order to make sure we don't waste time drawing the same area of the canvas over and over.
		// It only happens with edges that connect nodes that are not visible, but the edge is visible, because the vertical line passes through the viewport.  
		// In this case, the edge needs to be redrawn even if its vertices aren't visible.  But, because of the way the LegacyLayout is,
		// when the edges draw the vertical line, many edges will draw the very same pixel column of the viewport.
		// There's no need for multiple edges to fill in that pixel column, only the first one.
		
		// This hack also has the corresponding part in isVisible().
		// Once the pixel column corresponding to this edge has been filled in, 
		// any other edges that would redraw the same exact pixel column (and only that column) of the screen
		// will now return false, so that the redraw doesn't happen.
		
		// Now enough explanation, onward!
		
		// If this edge only drew a column on the viewport, then let the LegacyLayout know that this column is filled in.
		Rectangle viewport = ((LegacyLayout)getObject().layout).getPixelColumnViewport();
		if( viewport == null ) {
			return;
		}
		if( parentXstart > viewport.x && parentXstart < viewport.x+viewport.width ) // if we are drawing through the viewport
			if( ( parentYstart < viewport.y && cornerY > viewport.y+viewport.height ) ||   // both vertices of the edge are invisible    OR
					( parentYstart > viewport.y && parentYstart < viewport.y+viewport.height && cornerY > viewport.y+viewport.height  ) ) {   // the first vertex is visible, but the second is not, we count this as a column also
				// we only drew a pixel column on the viewport, let the layout know.
				((LegacyLayout)getObject().layout).setPixelColumnDrawn(parentXstart);
			}
		
	}
	
	@Override
	public boolean isVisible(Rectangle bounds) {
		
		if( !super.isVisible(bounds) ) return false;  // the visibility of this edge was turned off by the user.
		
		GraphicalData gr = getObject();
		
		int startX = gr.x;
		int startY = gr.y;
		
		int cornerX = startX;
		int cornerY = gr.y+gr.height;
		
		int endX = gr.x+gr.width;
		
		
		if( cornerY < bounds.y ) return false;  // the edge is above the viewport
		if( startY > bounds.y+bounds.height ) return false;  // the edge is below the viewport
		if( cornerX > bounds.x+bounds.width) return false; // the edge is to the right of the viewport
		if( endX < bounds.x ) return false;  // the edge is to the left of the the viewport
		if( cornerX < bounds.x && cornerY > bounds.y+bounds.height ) return false;  // corner case.
		
		// check if we are only drawing a column on the canvas. 
		if( startX > bounds.x && startX < bounds.x+bounds.width ) // if we are drawing through the viewport
			if( (startY < bounds.y && cornerY > bounds.y+bounds.height) || 
					( startY > bounds.y && startY < bounds.y+bounds.height && cornerY > bounds.y+bounds.height  ) ) // and if we are drawing only a column  
				if( ((LegacyLayout)gr.layout).isPixelColumnDrawn(startX) ) // and if this column is already drawn
					return false;  // then we don't need to draw anything else
			
		return true;
	}

}
