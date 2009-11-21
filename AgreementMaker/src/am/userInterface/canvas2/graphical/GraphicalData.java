package am.userInterface.canvas2.graphical;

import java.awt.Font;
import java.awt.Rectangle;

import am.app.Core;
import am.userInterface.canvas2.utility.Canvas2Layout;

import com.hp.hpl.jena.ontology.OntResource;

/*
 * This Class is a container class for graphical information about an element.
 */

public class GraphicalData {
	
	public enum NodeType {
		CLASSES_ROOT,
		PROPERTIES_ROOT,
		CLASS_NODE,
		PROPERTY_NODE,
		GLOBAL_ROOT,
		INDIVIDUAL_NODE,
		UNKNOWN,
		FAKE_NODE,
		LAYOUT_EDGE,
		LAYOUT_NODE,
		
		GRAPHICAL_ELEMENT,
			LINE_ELEMENT,
			RECTANGLE_ELEMENT,
			TEXT_ELEMENT,
		
		MAPPING,
		
	}
	
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean selected = false;
	public boolean visible = true;
	public OntResource r;  // the resource in the model that this node points to
	public NodeType type;			// the type of element that this GraphicalData is attached to
	public int ontologyID;  		// the ID of the ontology that the element belongs to
	public Canvas2Layout layout;	// the layout that's using this graphical data.
	public Font font = new Font("Lucida Sans Regular", Font.PLAIN, 12);
	public boolean hover; // if the mouse is hovering over this element
	
	
	public GraphicalData( int x1, int y1, int width, int height, OntResource r1, NodeType t, Canvas2Layout l ) {
		x = x1;
		y = y1;
		this.width = width;
		this.height = height;
		r = r1;
		type = t;
		layout = l;
		ontologyID = Core.getInstance().getOntologyIDbyModel( r1.getModel() );
	}
	
	// a constructor that passes in no resource, but gives an ontology ID.  Used for the global roots
	public GraphicalData( int x1, int y1, int width, int height, NodeType t, Canvas2Layout l, int ontID ) {
		x = x1;
		y = y1;
		this.width = width;
		this.height = height;
		r = null;
		type = t;
		layout = l;
		ontologyID = ontID;
	}
	
	
	public Rectangle getBounds() {
		return new Rectangle( x, y, width, height);
	}
	
	public String toString() {
		String s = new String();
		s = "x=" + Integer.toString(x) + ", y=" + Integer.toString(y) + ", w=" + Integer.toString(width) + ", h=" + Integer.toString(height);
		return s; 
	}
	
	public void setLayout( Canvas2Layout l ) { layout = l; }
	
}
