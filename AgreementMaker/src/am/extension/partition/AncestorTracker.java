package am.extension.partition;

import am.app.ontology.Node;

public class AncestorTracker {
	
	public CustomNode node1;
	public CustomNode node2;
	public Node commonAncestor;
	
	public AncestorTracker(CustomNode source, CustomNode target)
	{
		this.node1 = source;
		this.node2 = target;
		
		this.commonAncestor = OntoProcessing.getCommonAncestor(this.node1,this.node2);
	}
}
