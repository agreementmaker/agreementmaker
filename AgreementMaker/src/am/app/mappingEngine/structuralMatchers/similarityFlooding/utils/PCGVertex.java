package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.EOntNodeType.EOntologyNodeType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.utility.DirectedGraphVertex;
import am.utility.Pair;

import com.hp.hpl.jena.rdf.model.RDFNode;
/**
 * Pairwise Connectivity Graph Vertex.
 * @author cosmin
 *
 */
public class PCGVertex extends DirectedGraphVertex<PCGVertexData, PCGEdgeData>{

	private boolean inserted;
	private boolean visited;
	
	public PCGVertex(PCGVertexData object) {
		super(object);
		setVisited(false);
		setInserted(false);
	}
	
	/**
	 * get the vertex from two WGraphEdges
	 */
	public PCGVertex(WGraphVertex s, WGraphVertex t) {
		super(new PCGVertexData(new Pair<WGraphVertex, WGraphVertex>(s, t)));
		setVisited(false);
		setInserted(false);
	}

	public Mapping toMapping(Ontology sourceOnt, Ontology targetOnt){
		WGraphVertex s = this.getObject().getStCouple().getLeft();
		WGraphVertex t = this.getObject().getStCouple().getRight();
		
		// take both source and target ontResources (values can be null, means not possible to take resources
		Node sourceNode = null, targetNode = null;
		if(this.representsClass()){
			sourceNode = Node.getNodefromRDFNode(sourceOnt, s.getObject(), alignType.aligningClasses);
			targetNode = Node.getNodefromRDFNode(targetOnt, t.getObject(), alignType.aligningClasses);
		}
		else if(this.representsProperty()){
			sourceNode = Node.getNodefromRDFNode(sourceOnt, s.getObject(), alignType.aligningProperties);
			targetNode = Node.getNodefromRDFNode(targetOnt, t.getObject(), alignType.aligningProperties);
		}
		else{
			// TODO: manage type error
			System.out.println("Type error found.");
		}
		
		if((sourceNode != null) && (targetNode != null)){
			return new Mapping(sourceNode, targetNode, this.getObject().getNewSimilarityValue());
		}
		else{
			return null;
		}
	}
	
	/**
	 * @param visited the visited to set
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * @return the visited
	 */
	public boolean isVisited() {
		return visited;
	}
	
	/**
	 * @param inserted the inserted to set
	 */
	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	/**
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}
	
	/**
	 * @return the inserted
	 */
	public boolean representsClass() {
		return this.getObject().getStCouple().getLeft().getNodeType().equals(EOntologyNodeType.CLASS) &&
				this.getObject().getStCouple().getRight().getNodeType().equals(EOntologyNodeType.CLASS);
	}
	
	/**
	 * @return the inserted
	 */
	public boolean representsProperty() {
		return this.getObject().getStCouple().getLeft().getNodeType().equals(EOntologyNodeType.PROPERTY) &&
				this.getObject().getStCouple().getRight().getNodeType().equals(EOntologyNodeType.PROPERTY);
	}

	/**
	 * String format: ( leftNode, rightNode )
	 */
	@Override
	public String toString() {
		String s = new String();
		
		s += "( " + getObject().getStCouple().getLeft().getObject().asResource().getLocalName().toString() + ", " + getObject().getStCouple().getRight().getObject().asResource().getLocalName().toString() + " )" +
		 		" - " + getObject().getOldSimilarityValue() + " - " + getObject().getNewSimilarityValue();
		
		return s;
	}
	
	// SUPPORT FUNCTIONS
	

}
