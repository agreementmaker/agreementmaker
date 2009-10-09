package am.app.mappingEngine.oneToOneSelection;

public class LinkMWBM<E> {
	
	
	protected int weight;

	/**
	 * Il nodo di origine dell'arco
	 */
	protected NodeMWBM<E> source;
	
	/**
	 * Il nodo di destinazione dell'arco
	 */
	protected NodeMWBM<E> dest;
	
	/**
	 * Riferimento all'arco predecessore nella lista degli archi incidenti
	 * al nodo di destinazione
	 */
	protected LinkMWBM<E> pred;
	
	/**
	 * Riferimento all'arco successore nella lista degli archi incidenti
	 * al nodo di destinazione
	 */
	protected LinkMWBM<E> succ;
	
	/**
	 * Riferimento al grafo contenente l'arco
	 */
	protected Graph<E> grafo;
	
	
	/**
	 * Costruttore per l'istanziazione di nuovi archi.
	 * 
	 * @param x il nodo di origine del nuovo arco
	 * @param y il nodo di destinazione del nuovo arco
	 * @param i il contenuto informativo da associare al nuovo arco
	 */
	public LinkMWBM(NodeMWBM<E> x, NodeMWBM<E> y, int i) {
		weight = i; source = x; dest = y;
	}
	
	/**
	 * Restituisce il riferimento alla struttura dati contenente l'arco.
	 * 
	 * @return il riferimento alla struttura dati contenente l'arco
	 */
	public Object getGraph() { return grafo; }

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public NodeMWBM<E> getSource() {
		return source;
	}

	public void setSource(NodeMWBM<E> source) {
		this.source = source;
	}

	public NodeMWBM<E> getDest() {
		return dest;
	}

	public void setDest(NodeMWBM<E> dest) {
		this.dest = dest;
	}

	public LinkMWBM<E> getPred() {
		return pred;
	}

	public void setPred(LinkMWBM<E> pred) {
		this.pred = pred;
	}

	public LinkMWBM<E> getSucc() {
		return succ;
	}

	public void setSucc(LinkMWBM<E> succ) {
		this.succ = succ;
	}

	public Graph<E> getGrafo() {
		return grafo;
	}

	public void setGrafo(Graph<E> grafo) {
		this.grafo = grafo;
	}
	
	
	public String toString(){
		return "("+source+", "+dest+", "+weight+")";
	}
	
	public boolean equals(Object o){
		if(o instanceof LinkMWBM){
			LinkMWBM l = (LinkMWBM)o;
			return l.source.equals(source) && l.dest.equals(dest);
		}
		return false;
	}
	
	public int hashCode(){
		return (source.identifier()+""+dest.identifier()).hashCode();
	}

}
