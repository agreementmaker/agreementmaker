package am.app.feedback.measures;

import am.app.ontology.Node;

public class Edge implements Comparable<Edge>{
	private int sourceVisit;
	private int targetVisit;
	private Node sourceNode;
	private Node targetNode;
	
	
	
	public Edge(Node s, Node t){
		sourceNode = s;
		targetNode = t;
	}
	
	public Edge(Node s, Node t, int sVisit, int tVisit){
		sourceNode = s;
		targetNode = t;
		sourceVisit = sVisit;
		targetVisit = tVisit;
	}
	
	public void setSourceVisit(int sourceVisit) {
		this.sourceVisit = sourceVisit;
	}
	public int getSourceVisit() {
		return sourceVisit;
	}
	public void setTargetVisit(int targetVisit) {
		this.targetVisit = targetVisit;
	}
	public int getTargetVisit() {
		return targetVisit;
	}
	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}
	public Node getSourceNode() {
		return sourceNode;
	}
	public void setTargetNode(Node targetNode) {
		this.targetNode = targetNode;
	}
	public Node getTargetNode() {
		return targetNode;
	}

	public int compareTo(Edge o) {
		if(this.sourceVisit < o.sourceVisit)
			return -1;
		else if(this.sourceVisit > o.sourceVisit){
			return 1;
		}
		else{
			if(this.targetVisit < o.targetVisit)
				return -1;
			else if(this.targetVisit > o.targetVisit){
				return 1;
			}
			else{
				if(this.sourceNode.getIndex() < o.sourceNode.getIndex())
					return -1;
				else if(this.targetNode.getIndex() > o.targetNode.getIndex()){
					return 1;
				}
				else{
					return 0;
				}
			}
		}
	}
	
	
}
