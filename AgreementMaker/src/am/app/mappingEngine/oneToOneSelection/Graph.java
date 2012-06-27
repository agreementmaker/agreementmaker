package am.app.mappingEngine.oneToOneSelection;

import java.util.List;

/**
 * Il tipo di dato Grafo, descritto dall'interfaccia <code>Grafo</code>, 
 * rappresenta un grafo orientato contenente n nodi ed m archi. 
 * Per riferirci ai nodi e agli archi del grafo usiamo rispettivamente i tipi nodo e arco,
 * specificati dalle interfacce <code>Nodo</code> ed <code>Arco</code>:
 * i riferimenti vengono generati dalle operazioni che creano nodi o archi
 * (<code>aggiungiNodo</code> e <code>aggiungiArco</code>), e possono essere
 * in seguito utilizzati dalle altre operazioni. Osserviamo che l'operazione
 * <code>sonoAdiacenti</code> riceve i riferimenti a due nodi <code>x</code>
 * e <code>y</code>, e, nel caso in cui essi siano adacenti, &egrave;
 * in grado di recuperare e restituire il riferimento all'arco corrispondente.<br>
 * Se indichiamo con <code>n</code> il numero di nodi del grafo, &egrave;
 * naturale, e spesso conveniente, pensare che i nodi siano indicizzati
 * con numeri interi in [0, n - 1]. I due metodi <code>nodo</code> ed <code>indice</code>
 * realizzano questa corrispondenza. Ciascun nodo e ciascun arco del grafo possiede
 * inoltre un contenuto informativo cui &egrave; possibile accedere (<code>infoNodo</code>
 *  e <code>InfoArco</code>) e che pu&ograve; essere modificato (<code>cambiInfoNodo</code> e <code>cambiaInfoArco</code>).
  */

public interface Graph<E> {

	/**
	 * Restituisce il numero di nodi presenti nel grafo.
	 * 
	 * @return il numero di nodi del grafo
	 */
	public int nodesCount();
	
	/**
	 * Restituisce il numero di archi presenti nel grafo.
	 * 
	 * @return il numero di archi del grafo
	 */
	public int edgesCount();
	
	/**
	 * Restituisce l'elenco dei nodi presenti nel grafo.
	 * 
	 * @return l'elenco dei nodi del grafo
	 */
	public List<NodeMWBM<E>> nodes();
	
	/**
	 * Restituisce l'elenco degli archi presenti nel grafo.
	 * 
	 * @return l'elenco degli archi nel grafo
	 */
	public List<LinkMWBM<E>> edges();
	
	/**
	 * Restituisce l'elenco degli archi uscente dal nodo di input.
	 * 
	 * @param v il nodo di cui si vuol conoscere l'elenco degli archi uscenti
	 * @return l'elenco degli archi uscenti da <code>v</code>
	 */
	public List<LinkMWBM<E>> outEdges(NodeMWBM<E> n);
	
	/**
	 * Verifica se due nodi <code>x</code> ed <code>y</code> sono adiacenti e, in caso affermativo,
	 * restituisce l'arco <code>(x,y)</code>. 
	 * 
	 * @param x il primo nodo di cui verificare l'adiacenza
	 * @param y il secondo nodo di cui verificare l'adiacenza
	 * @return l'arco <code>(x,y)</code>, se <code>x</code> ed <code>y</code> sono adiacenti. <code>null</code>, altrimenti.
	 */
	public LinkMWBM<E> areAdiacent(NodeMWBM<E> x, NodeMWBM<E> y);
	
	/**
	 * Restituisce il nodo con indice <code>i</code>.
	 * 
	 * @param i l'indice di cui si vuol conoscere il nodo
	 * @return il nodo di indice <code>i</code>
	 */
	public NodeMWBM<E> getNode(int i);
	
	/**
	 * Inserisce un nuovo nodo con contenuto informativo <code>info</code>.
	 * 
	 * @param info il contenuto informativo del nuovo nodo
	 * @return il riferimento al nodo inserito
	 */
	public NodeMWBM<E> addNode(E e);
	
	/**
	 * Inserisce un nuovo arco <code>(x,y)</code> con contenuto informativo info.
	 * 
	 * @param x il nodo origine dell'arco da inserire
	 * @param y il nodo destinazione dell'arco da inserire 
	 * @param info il contenuto informativo del nuovo arco
	 * @return il riferimento all'arco inserito
	 */
	public LinkMWBM<E> addEdge(NodeMWBM<E> x, NodeMWBM<E> y, int weight);
	
	/**
	 * Cancella dal grafo il nodo indicato da input e tutti gli archi ad 
	 * esso incidenti.
	 * 
	 * @param v il nodo da cancellare
	 */
	public void removeNode(NodeMWBM<E> n);
	
	/**
	 * Cancella dal grafo l'arco indicato da input.
	 * 
	 * @param e l'arco da cancellare
	 */
	public void removeEdge(LinkMWBM<E> e);
}