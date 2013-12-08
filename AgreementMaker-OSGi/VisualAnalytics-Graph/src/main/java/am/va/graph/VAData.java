package am.va.graph;

import am.app.ontology.Node;

public class VAData implements Comparable<VAData>{
	Node sourceNode;
	Node targetNode;
	double Similarity;

	public VAData(Node sourceNode, Node targetNode, double similarity) {
		super();
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		Similarity = similarity;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	public Node getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(Node targetNode) {
		this.targetNode = targetNode;
	}

	public double getSimilarity() {
		return Similarity;
	}

	public void setSimilarity(double similarity) {
		Similarity = similarity;
	}

	@Override
	public int compareTo(VAData data) {
		// TODO Auto-generated method stub
		if(this.Similarity >= data.Similarity)
			return 1;
		else 
			return -1;
	}

}
