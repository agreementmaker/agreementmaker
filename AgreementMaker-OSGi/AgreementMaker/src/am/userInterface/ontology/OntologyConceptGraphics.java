package am.userInterface.ontology;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * This class represents a graphical representation of an Ontology Concept (classes, properties, individuals, ...)
 * which are displayed to the user in the AgreementMaker user interface.
 * It is meant for the specific purpose of graphical representation, and can be associated 
 * with a datatype that is supposed to have a graphical representation. 
 * @author Cosmin Stroe
 * @date Sept 3, 2010.
 */
public interface OntologyConceptGraphics {

	/**
	 * Return a rectangle representing the graphical bounds of the drawn representation.
	 * @return
	 */
	public Rectangle getBounds();
	
	/**
	 * Draw the representation on the graphics object.
	 * @param g
	 */
	public void draw(Graphics g);
	
	/**
	 * Change the position and size of the graphical representation.
	 * @param x The horizontal axis coordinate.
	 * @param y The vertical axis coordinate.
	 * @param width
	 * @param height
	 */
	public void updateBounds( int x, int y, int width, int height );
	
	/**
	 * Return the class that implements this graphical representation.
	 * Used to distinguish between multiple representations of the same data.
	 * @return
	 */
	public Class<?> getImplementationClass();
	
}
