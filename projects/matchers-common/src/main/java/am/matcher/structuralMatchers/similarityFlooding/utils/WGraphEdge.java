/**
 * 
 */
package am.matcher.structuralMatchers.similarityFlooding.utils;

import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Michele Caci
 */
public class WGraphEdge extends DirectedGraphEdge<String, RDFNode> implements Comparable<WGraphEdge>{

	public WGraphEdge(DirectedGraphVertex<RDFNode, String> orig,
			DirectedGraphVertex<RDFNode, String> dest, String o) {
		super(orig, dest, o);
	}

	public String toString(){
		return " <" + this.getOrigin().getObject().toString() + " --- "
					+ this.getObject().toString() + " --- "
					+ this.getDestination().toString() + "> ";
	}

	@Override
	public int compareTo(WGraphEdge anotherEdge) {
		if(anotherEdge == null){
			throw new NullPointerException();
		}
		else{
			return this.getObject().compareTo(anotherEdge.getObject());
		}
	}
}
