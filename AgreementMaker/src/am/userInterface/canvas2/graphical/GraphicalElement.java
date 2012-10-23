package am.userInterface.canvas2.graphical;

import java.awt.Graphics;

import am.userInterface.canvas2.utility.Canvas2Layout;


/*
 * A class to describe the basic building blocks of 
 */
public class GraphicalElement extends GraphicalData {

	public GraphicalElement(int x1, int y1, int width, int height,
			Canvas2Layout l, int ontID) {
		super(x1, y1, width, height, GraphicalData.NodeType.GRAPHICAL_ELEMENT, l, ontID);
	}
	
	public void draw(Graphics g) { /* This function must be overriden in the sublcass */ };
	
}
