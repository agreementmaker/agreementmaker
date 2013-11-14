package am.ui.canvas2.graphical;

import java.awt.Graphics;

import am.ui.canvas2.utility.Canvas2Layout;

/**
 * This class represents a line element.
 * 
 * It draws a line between two points.
 *
 * @author cosmin
 */

public class LineElement extends GraphicalElement {

	public LineElement(int x1, int y1, int width, int height, Canvas2Layout l,
			int ontID) {
		super(x1, y1, width, height, l, ontID);
		type = NodeType.LINE_ELEMENT;
	}

	@Override
	public void draw(Graphics g) {
		g.drawLine(x,y,x+width,y+height);
	}
	
}
