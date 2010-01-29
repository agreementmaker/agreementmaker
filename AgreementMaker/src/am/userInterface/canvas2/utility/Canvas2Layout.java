package am.userInterface.canvas2.utility;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import com.hp.hpl.jena.ontology.OntResource;


import am.app.mappingEngine.AbstractMatcher;
import am.app.ontology.Ontology;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.graphical.GraphicalData;
import am.userInterface.canvas2.utility.GraphLocator.GraphType;

public class Canvas2Layout implements MouseInputListener, 
									  MouseWheelListener,
									  ChangeListener,		// used to know when the viewport is resized
									  ActionListener
	{

	
	protected Canvas2 vizpanel;
	protected CanvasGraph layoutArtifactGraph;  // The artifact graph must be created in the constructor.
	
	public Canvas2Layout( Canvas2 vp ) {
		if( vp == null ) {
			throw new RuntimeException("Canvas2Layout cannot be initialized without a Canvas2");
		}
		vizpanel = vp;
		layoutArtifactGraph = buildArtifactGraph();
	}
	
	/**
	 * MouseEvent Listener methods.
	 */
	public void mouseClicked(MouseEvent e)         { /* This function must be implemented in the subclass. */ }
	public void mouseEntered(MouseEvent e)         { /* This function must be implemented in the subclass. */ }
	public void mouseExited(MouseEvent e)          { /* This function must be implemented in the subclass. */ }
	public void mousePressed(MouseEvent e)         { /* This function must be implemented in the subclass. */ }
	public void mouseReleased(MouseEvent e)        { /* This function must be implemented in the subclass. */ }
	public void mouseDragged(MouseEvent e)         { /* This function must be implemented in the subclass. */ }
	public void mouseMoved(MouseEvent e)           { /* This function must be implemented in the subclass. */ }
	public void mouseWheelMoved(MouseWheelEvent e) { 
		//vizpanel.getS 
	}
	
	public void actionPerformed(ActionEvent e)  { /* This function must be implemented in the subclass. */ }
	
	public void stateChanged(ChangeEvent e)        { /* This function must be implemented in the subclass. */ }
	
	
	
	/*************** Canvas Specific methods *******************/
	
	public void getReadyForRepaint(Rectangle viewport) { /* getReadyForRepaint() is called from Canvas2.paint() just before that method iterates through
															all the vertices and all the edges calling isVisible() and draw().
															It's called because the layout may have some special things to do just before a repaint. */ }
	/** Allow for node name manipulation */
	public String getNodeLabel(GraphicalData d ) { return ""; } /* must be implemented in the subclass */
	
	/**
	 * Called from Canvas2.
	 * This method will build the layout graph with the default layout for each ontology.
	 */
	public ArrayList<CanvasGraph> buildInitialGraphs() {
		/* This function must be implemented in the subclass. */
		return new ArrayList<CanvasGraph>();
	}

	/**
	 * This function produces the Classes, Properties, and Layout graphs for the given ontology,
	 * and returns them in an ArrayList.
	 * @param ont
	 * @return
	 */
	public ArrayList<CanvasGraph> buildGlobalGraph( Ontology ont) {
		/* This function must be implemented in the subclass. */
		return new ArrayList<CanvasGraph>();
	}

	// TODO: Do we really need the method canDisplayMoreOntologies() ?  Everything can be taken care of by displayOntology()
	public boolean canDisplayMoreOntologies() { return false; } /* must be implemented in the subclass */
	public void displayOntology( ArrayList<CanvasGraph> graphs, int ontologyID) { } /* must be implemented in the subclass */
	public void removeOntology( ArrayList<CanvasGraph> graphs, int ontologyID ) { } /* must be implemented in the subclass */

	/* buildMatcherGraph must be implemented in the subclass */
	public CanvasGraph buildMatcherGraph(AbstractMatcher m) { return new CanvasGraph(GraphType.MATCHER_GRAPH, m.getID()); }

	private CanvasGraph buildArtifactGraph() { return null; };  // builds the artifact graph and returns it to the constructor
	public CanvasGraph getArtifactsGraph() { return layoutArtifactGraph; } // getArtifactsGraph() returns null if there is no artifact graph for this layout  
}
