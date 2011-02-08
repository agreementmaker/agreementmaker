package am.userInterface.sidebar.vertex;

import am.app.mappingEngine.Mapping;

/**
 * This is just a structure used to keep the structures needed to show a line
 * it is used in the alignments display functions to display selected/highlighted matchings
 * 
 *
 */
public class VertexLine{
	public Vertex source;
	public Vertex target;
	public Mapping alignment;
	
	public boolean equals(Object o) {
		if(o instanceof VertexLine) {
			Vertex v = (Vertex)o;
			return source.equals(v) && target.equals(v);
		}
		return false;
	}
	
	public Vertex getHighlightedNode() {
		if(source.getIsSelected()) {
			return target;
		}
		else {
			return source;
		}
	}
}
