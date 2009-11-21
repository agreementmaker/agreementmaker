package am.userInterface.canvas2.graphical;

import java.awt.Graphics;

import am.userInterface.canvas2.utility.Canvas2Layout;

public class RectangleElement extends GraphicalElement {

	private boolean filled = false;

	public RectangleElement(int x1, int y1, int width, int height, Canvas2Layout l,
			int ontID) {
		super(x1, y1, width, height, l, ontID);
		type = NodeType.RECTANGLE_ELEMENT;
	}

	public void setFilled( boolean f ) { filled = f; }
	
	@Override
	public void draw(Graphics g) {
		if( filled )
			g.fillRect(x,y,width,height);
		else
			g.drawRect(x,y,width,height);
	}
	
}
