package am.userInterface.canvas2.nodes;

import java.awt.Graphics;

import am.userInterface.Colors;
import am.userInterface.canvas2.graphical.GraphicalElement;
import am.userInterface.canvas2.utility.Canvas2Vertex;

public class GraphicalNode extends Canvas2Vertex {

	public GraphicalNode(GraphicalElement g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw( Graphics g ) {
		g.setColor(Colors.foreground);
		((GraphicalElement)d).draw(g);
	}
	
}
