package am.userInterface.canvas2.nodes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;

import am.app.Core;
import am.userInterface.Colors;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.graphical.MappingData;
import am.userInterface.canvas2.layouts.LegacyLayout;
import am.userInterface.canvas2.utility.Canvas2Vertex;
import am.utility.DirectedGraphEdge;

/**
 * This node is used by the LegacyLayout
 * 
 * This class only implements the draw() function.
 * 
 * The legacy node displays a node as a rounded box.  It also has a circle for collapsing and expanding the subtree.
 * 
 * @author cosmin
 *
 */
public class LegacyNode extends Canvas2Vertex {

	
	private int arcWidth = 9;
	private int arcHeight = 9;
	
	private int fontMarginLeft = 5;
	private int fontMarginRight = 5;
	private int fontWidth = 100;
	private int fontHeight = 15;
	
	private boolean hideChildren = false;
	
	public static final int circlePadding = 10;  // 10 pixels of padding to the left of the node for the circle
	
	public LegacyNode( GraphicalData gr) {
		super(gr);
		recalculateWidth(); // recalculate the width
	}
	
	/**
	 * This draws a Legacy Node.
	 */
	public void draw(Graphics g) {
		
		// 3 steps to drawing a legacy node.
		
		
		// 1.  Get the node label.  This is the text that will be displayed on the node.
		
		String name = d.layout.getNodeLabel(d);
		
		
		// 2. Draw the grow/collapse circle, only if the node is not a leaf node.
		
		if( numChildren() > 0 ) { // if this node has children (i.e. is not a leaf)
			g.setColor(Colors.foreground);
			g.drawOval(d.x, d.y+(d.height/2)-4, 8, 8);
			if(hideChildren) g.drawLine( d.x+4, d.y+(d.height/2)-4,  d.x+4, d.y+(d.height/2)+4);
		}				


		// 3. Now draw the node itself.
		
		
		if( isSelected() ) {
			g.setColor(Colors.selected);
			g.fillRoundRect(d.x+circlePadding, d.y, d.width-circlePadding, d.height, arcWidth, arcHeight);
		} else if( d.hover ) {
			g.setColor(Colors.hover);
			g.fillRoundRect(d.x+circlePadding, d.y, d.width-circlePadding, d.height, arcWidth, arcHeight);
		} else {
			g.setColor(getMappingColor());
			g.fillRoundRect(d.x+circlePadding, d.y, d.width-circlePadding, d.height, arcWidth, arcHeight);
		}
		
		g.setColor(Colors.foreground);
		g.drawRoundRect(d.x+circlePadding,d.y,d.width-circlePadding,d.height, arcWidth, arcHeight);
		g.setFont(d.font);
		if( name != null ) g.drawString( name ,d.x+circlePadding+fontMarginLeft,d.y+fontHeight);
		
		
		
	}
	
	public void recalculateWidth() {
		
		String name = d.layout.getNodeLabel(d);
		
		Core.getInstance();
		FontMetrics fontMetrics = Core.getUI().getCanvas().getFontMetrics(d.font);
	    
		
        if( name == null || name.length() <= 0 ) { fontWidth = 0; } 
        else { fontWidth = fontMetrics.stringWidth(name); }
        fontHeight = fontMetrics.getHeight();

		
		d.width =  circlePadding + fontMarginLeft + fontWidth + fontMarginRight; // the width of our node has changed (because the text has changed)
		

		// update the mapping edges, since the width of our node has changed
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeIter = edgesOut.iterator();
		while( edgeIter.hasNext() ) {
			GraphicalData gd = edgeIter.next().getObject();
			if( gd.type == GraphicalData.NodeType.MAPPING ) {
				int oldX = gd.x;
				gd.x = d.x+d.width;
				gd.width -= gd.x - oldX;
			}
		}

		
	}
	
	private int numChildren() {
		int numChildren = 0;
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeIter = edgesOut.iterator();
		while( edgeIter.hasNext() ) {
			GraphicalData gd = edgeIter.next().getObject();
			if( gd.type == GraphicalData.NodeType.MAPPING ) continue; // ignore mappings
			numChildren++;
		}
		return numChildren;
	}

	// return the color top visible matcher
	private Color getMappingColor() {
		
		Color c = Colors.background;
		int matcherID = Core.ID_NONE;
		
		// check the outgoing edges (for source nodes)
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeOutIter = edgesOut.iterator();
		while( edgeOutIter.hasNext() ) {
			GraphicalData gd = edgeOutIter.next().getObject();
			if( gd.type == GraphicalData.NodeType.MAPPING && 
					( gd.visible == true || gd.layout.isViewActive(LegacyLayout.VIEW_SINGLE_MAPPING) ) ) {
				if( matcherID < ((MappingData)gd).matcherID ) {
					matcherID = ((MappingData)gd).matcherID;
					c = mixColors(c, ((MappingData)gd).color);
				}					
			}
		}
		
		// check the incoming edges (for target nodes)
		Iterator<DirectedGraphEdge<GraphicalData, GraphicalData>> edgeInIter = edgesIn.iterator();
		while( edgeInIter.hasNext() ) {
			GraphicalData gd = edgeInIter.next().getObject();
			if( gd.type == GraphicalData.NodeType.MAPPING && 
					( gd.visible == true || gd.layout.isViewActive(LegacyLayout.VIEW_SINGLE_MAPPING) ) ) {
				if( matcherID < ((MappingData)gd).matcherID ) {
					matcherID = ((MappingData)gd).matcherID;
					c = mixColors(c, ((MappingData)gd).color);
				}					
			}
		}
		
		//c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 128); // 50% opacity
		
		return c;
	}
	
	// temporarily disabled
	private Color mixColors( Color c1, Color c2 ) {
		return c2;
		//return new Color( (c1.getRed() + c2.getRed())/2, (c1.getGreen() + c2.getGreen())/2, (c1.getBlue() + c2.getBlue())/2  );
	}

	@Override
	public void clearDrawArea(Graphics g) {
		g.setColor(Colors.background);
		g.fillRect(d.x+circlePadding, d.y, d.width-circlePadding, d.height);
	}
}
