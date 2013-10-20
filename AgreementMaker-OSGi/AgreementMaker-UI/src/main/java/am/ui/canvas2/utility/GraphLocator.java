package am.userInterface.canvas2.utility;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphLocator {
	
	public static enum GraphType {
		CLASSES_GRAPH,      // This graph represents the class nodes in an ontology
		PROPERTIES_GRAPH,	// This graph represents the property nodes in an ontology.
		INDIVIDUALS_GRAPH,	// This graph represents the individuals nodes in an ontology.
		
		ONTOLOGY_GRAPH,		// an ontology graph encapsulates the CLASSES, 
							// PROPERTIES, and INDIVIDUALS graphs for an ontology.
							//  It's a convenience in order to have a common root for everything.
		
		LAYOUT_GRAPH,		// a layout graph is used to draw layout artifacts
							// Things like separator lines, layout labels, etc.
							// Layout graphs may not be associated with an ontology

		LAYOUT_GRAPH_IGNORE_BOUNDS,  // same thing as a layout graph, but the canvas won't consider this graph when resizing
		
		GLOBAL_ROOT_GRAPH,	// these graphs encapsulate all the graphs for a certain ontology
		
		MATCHER_GRAPH,		// this is the graph of a matcher.
		
		
	}
	
	/**
	 * This function returns a graph that matches the graphtype and ontology id, from the 
	 * array of graphs currently used by Canvas2.
	 * 
	 * It can return null.
	 */
	public static CanvasGraph getGraph( ArrayList<CanvasGraph> ontologyGraphs,  GraphType grt, int ontologyID ) {
		
		/* Sanity checks.  Should we remove these? I say keep them, this is function is not called often. */ 
		if( ontologyGraphs == null ) return null;  // list was empty, return nothing
		
		
		Iterator<CanvasGraph> graphIter = ontologyGraphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph g = graphIter.next();
			
			if( g.getID() == ontologyID && g.getGraphType() == grt ) { // we found the matching graph
				return g;
			}
			
		}
		
		return null; // we found nothing, return null
	}
	
	/**
	 * This function returns a graph that matches the graphtype, from the 
	 * array of graphs currently used by Canvas2.
	 * 
	 * This function will not return null.  It will always return an arraylist, whether it's empty or not that depends if graphs were found.
	 */
	public static ArrayList<CanvasGraph> getGraphsByType( ArrayList<CanvasGraph> ontologyGraphs, GraphType grt ) {
		
		ArrayList<CanvasGraph> graphsFound = new ArrayList<CanvasGraph>();
		
		/* Sanity checks.  Should we remove these? I say keep them, this is function is not called often. */ 
		if( ontologyGraphs == null ) return graphsFound;  // this should not normally happen either
		
		Iterator<CanvasGraph> graphIter = ontologyGraphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph g = graphIter.next();
			
			if( g.getGraphType() == grt ) { // we found a matching graph
				graphsFound.add(g);
			}
			
		}
		
		return graphsFound; // return what we found.
	}
	
	/**
	 * This function returns a graph that matches the ontology ID, from the 
	 * array of graphs currently used by Canvas2.
	 * 
	 * This function will not return null.  It will always return an arraylist, whether it's empty or not that depends if graphs were found.
	 */
	public static ArrayList<CanvasGraph> getGraphsByID( ArrayList<CanvasGraph> ontologyGraphs, int ontID ) {
		
		ArrayList<CanvasGraph> graphsFound = new ArrayList<CanvasGraph>();
		
		/* Sanity check.  Should I remove this? I say keep it, this is function is not called often. */
		if( ontologyGraphs == null ) return graphsFound;  // this should not normally happen
		
		Iterator<CanvasGraph> graphIter = ontologyGraphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph g = graphIter.next();
			
			if( g.getID() == ontID ) { // we found a matching graph
				graphsFound.add(g);
			}
			
		}
		
		return graphsFound; // return what we found.
	}
	
}
