package am.app.mappingEngine.oneToOneSelection;

public class MappingMWBM<E> {
	
	private E sourceNode; 
	private E targetNode; 
	private int weight;
	
	public MappingMWBM(E theSourceNode,E theTargetNode, int theWeight){
		sourceNode = theSourceNode;
		targetNode = theTargetNode;
		weight = theWeight;
	}
	
	public E getSourceNode() {
		return sourceNode;
	}

	public E getTargetNode() {
		return targetNode;
	}

	public int getWeight() {
		return weight;
	}
	
	public boolean equals(Object o){
		if(o instanceof MappingMWBM<?>){
			MappingMWBM<?> m = (MappingMWBM<?>)o;
			return m.getSourceNode().equals(this.getSourceNode()) && m.getTargetNode().equals(this.getTargetNode());
		}
		return false;
	}
	
	public int hashCode(){
		return (sourceNode.hashCode()+""+targetNode.hashCode()).hashCode();
	}
	
	public String toString(){
		return "("+sourceNode+", "+targetNode+", "+weight+")";
	}


}
