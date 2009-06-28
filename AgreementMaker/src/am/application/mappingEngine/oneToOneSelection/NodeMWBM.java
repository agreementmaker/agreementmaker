package am.application.mappingEngine.oneToOneSelection;


public class NodeMWBM<E> implements Comparable<NodeMWBM<E>> {
	
	/**
	 * Riferimento al primo elemento dell'elenco di archi uscenti dal nodo
	 */
	public LinkMWBM<E> firstEdge;
	
	/**
	 * Riferimento al grafo contenente il nodo
	 */
	public Graph graph;
	
	/**
	 * Indice del nodo nel grafo
	 */
	public int graphIndex;
	
	/**
	 * Numero di archi incidenti al nodo
	 */
	public int inDegree;
	
	/**
	 * Restituisce il riferimento al grafo contenente il nodo (<font color=red>Tempo O(1)</font>).
	 * 
	 * @return il riferimento al grafo contenente il nodo
	 */
	public Object getGraph() { return graph; }
	
	
	protected E element;
	
	protected int potential;
	
	protected LinkMWBM<E> predecessor;
	
	protected int distance;
	
	protected boolean free;
	
	public NodeMWBM(E e){
		element = e;
		graphIndex = e.hashCode();
	}
		
	public NodeMWBM(E e, LinkMWBM<E> a, Graph g, int graIndex){
		element = e;
		firstEdge = a; 
		graph = g;
		graphIndex = graIndex;
		initAlgorithmVariables();
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public LinkMWBM<E> getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(LinkMWBM<E> predecessor) {
		this.predecessor = predecessor;
	}

	public int getPotential() {
		return potential;
	}

	public void setPotential(int potential) {
		this.potential = potential;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}
	
	public E getElement() {
		return element;
	}
	
	protected void initAlgorithmVariables() {
		potential = 0;
		free = true;
		predecessor = null;
		distance = 0;
	}
	
	public int identifier(){
		return graphIndex;
	}
	
	public boolean equals(Object o){
		if(o instanceof NodeMWBM){
			NodeMWBM n = (NodeMWBM)o;
			return identifier() == n.identifier(); 
		}
		return false;
	}
	
	public String toString(){
		return element.toString();
	}
	
	public int hashCode(){
		return graphIndex;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public LinkMWBM<E> getFirstEdge() {
		return firstEdge;
	}

	public void setFirstEdge(LinkMWBM<E> firstEdge) {
		this.firstEdge = firstEdge;
	}

	public int getGraphIndex() {
		return graphIndex;
	}

	public void setGraphIndex(int graphIndex) {
		this.graphIndex = graphIndex;
	}

	public int getInDegree() {
		return inDegree;
	}

	public void setInDegree(int inDegree) {
		this.inDegree = inDegree;
	}

	public int compareTo(NodeMWBM<E> n) {
		Integer thisDistance = new Integer(distance);
		Integer otherDistance = new Integer(n.getDistance());
		return thisDistance.compareTo(otherDistance);
	}

}
