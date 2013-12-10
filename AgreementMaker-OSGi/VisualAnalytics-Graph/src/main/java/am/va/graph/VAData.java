package am.va.graph;

import am.app.ontology.Node;

public class VAData implements Comparable<VAData> {
	private Node sourceNode;
	private Node targetNode;
	private double Similarity;

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

	/**
	 * Return source node's local name
	 * 
	 * @return
	 */
	public String getNodeName() {
		return sourceNode.getLocalName();
	}

	/**
	 * If source node has children or not
	 * 
	 * @return
	 */
	public boolean hasChildren() {
		return sourceNode.getChildren().size() > 0;
	}

	@Override
	public int compareTo(VAData data) {
		// TODO Auto-generated method stub
		if (this.Similarity > data.Similarity)
			return 1;
		else if (this.Similarity < data.Similarity)
			return -1;
		else
			return 0;
	}

	public String toString() {
		return new String(this.getNodeName() + " " + this.Similarity);
	}

}
