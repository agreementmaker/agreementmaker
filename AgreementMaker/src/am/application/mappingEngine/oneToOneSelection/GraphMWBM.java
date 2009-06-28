package am.application.mappingEngine.oneToOneSelection;

import java.util.*;


/**
 * La classe <code>GrafoLA</code> implementa l'interfaccia <code>Grafo</code>
 * usando una rappresentazione basata su liste di adiacenza. A tale scopo,
 * essa mantiene un array, <code>nodi</code>, nel quale viene memorizzato
 * l'elenco dei nodi attualmente presenti nel grafo. Ciascun nodo <code>v</code>, 
 * implementato mediante la classe <code>NodiLA</code>, memorizza
 * il puntatore alla lista degli archi ad esso adiacenti, il grafo di appartenenza,
 * l'indice intero corrispondente e il numero di archi entranti. L'array <code>nodi</code>
 * viene dimensionato secondo la tecnica del raddoppiamento-dimezzamento (doubling-halving).<br>
 *  Durante la vita della struttura dati, viene mantenuta la propriet&agrave;
 *  invariante che il riferimento al nodo <code>v</code> di indice <code>i</code>
 *  &egrave; memorizzato in <code>nodi[i]</code>. E' quindi 
 *  possibile ottenere l'indice di un nodo <code>v</code> accedendo
 *  alla variabile di istanza <code>v.indice</code>, ed &egrave; possibile conoscere
 *  il nodo di indice <code>i</code> accedendo al valore <code>nodi[i]</code>.<br>
 * 
 */

public class GraphMWBM<E> implements Graph<E> {

	/**
	 * Il numero di nodi attualmente presenti nel grafo
	 */
	protected int n;
	
	/**
	 *  Il numero di archi attualmente presenti nel grafo
	 */
	protected int m;
	
	/**
	 * Array utilizzato per memorizzare l'elenco dei nodi
	 * attualmente presenti nel grafo. La sua dimensione
	 * iniziale &egrave; pari ad 1.
	 */
	protected ArrayList<NodeMWBM<E>> nodes = new ArrayList<NodeMWBM<E>>(100);

	/**
	 * Restituisce il numero di nodi presenti nel grafo (<font color=red>Tempo O(1)</font>).
	 * 
	 * @return il numero di nodi del grafo
	 */
	public int nodesCount() { return n; }

	/**
	 * Restituisce il numero di archi presenti nel grafo (<font color=red>Tempo O(1)</font>).
	 * 
	 * @return il numero di archi del grafo
	 */
	public int edgesCount() { return m; }

	/**
	 * Restituisce l'elenco dei nodi presenti nel grafo (<font color=red>Tempo O(n)</font>).
	 * L'elenco viene costruito come copia dell'array <code>nodi</code>.
	 * 
	 * @return l'elenco dei nodi del grafo
	 */
	public List<NodeMWBM<E>> nodes() { 
		return nodes;
	}
	
	/**
	 * Restituisce l'elenco degli archi presenti nel grafo (<font color=red>Tempo O(m)</font>).
	 * L'elenco viene costruito scorrendo l'elenco dei nodi 
	 * del grafo ed includendo tutti gli archi originanti
	 * da ciascuno di essi.
	 * 
	 * @return l'elenco degli archi nel grafo
	 */
	public List<LinkMWBM<E>> edges(){
		List<LinkMWBM<E>> l = new ArrayList<LinkMWBM<E>>();
		Iterator<NodeMWBM<E>> it = nodes.iterator();
		while(it.hasNext()){
			l.addAll(outEdges(it.next()));
		}
		return l;
	}

	/**
	 * Restituisce l'elenco degli archi uscenti dal nodo <code>v</code> (<font color=red>Tempo O(grado uscente di v)</font>).
	 * L'elenco viene determinato scorrendo la lista degli archi uscenti
	 * da <code>v</code> e verificando, per ciascuno di essi, se il nodo di
	 * destinazione &egrave; stato marcato come cancellato dal metodo {@link GraphMWBM#removeNode(Nodo)} .
	 * In caso affermativo, l'arco viene rimosso dal grafo mediante il metodo <code>rimuoviArco</code>
	 * altrimenti, l'arco viene aggiunto all'elenco degli archi uscenti da <code>v</code>.
	 * 
	 * @param v il nodo di cui si vuol conoscere l'elenco degli archi uscenti
	 * @return l'elenco degli archi uscenti da <code>v</code>
	 */	
	public List<LinkMWBM<E>> outEdges(NodeMWBM<E> v){
		List<LinkMWBM<E>> l = new ArrayList<LinkMWBM<E>>();
		LinkMWBM<E> a = v.getFirstEdge();
		while (a != null) {
			LinkMWBM<E> succ = a.getSucc();
			if( a.getDest().getGraph() != null){
				l.add(a);
			}
			else{
				//System.out.println("remove in out edges");
				removeEdge(a);
			}
			a = succ;
		}
		return l;
	}
	
	/**
	 * Verifica se due nodi <code>x</code> ed <code>y</code> sono adiacenti e, in caso affermativo,
	 * restituisce l'arco <code>(x,y)</code> (<font color=red>Tempo O(grado uscente di x)</font>). L'operazione viene
	 * realizzata ottenendo l'elenco degli archi uscenti da <code>x</code>
	 * e verificando se, tra questi, ne esiste uno entrante in <code>y</code>.
	 * 
	 * @param x il primo nodo di cui verificare l'adiacenza
	 * @param y il secondo nodo di cui verificare l'adiacenza
	 * @return l'arco <code>(x,y)</code>, se <code>x</code> ed <code>y</code> sono adiacenti. <code>null</code>, altrimenti.
	 */
	public LinkMWBM<E> areAdiacent(NodeMWBM<E> x, NodeMWBM<E> y){
		Iterator<LinkMWBM<E>> i = outEdges(x).iterator();
		while (i.hasNext()){
			LinkMWBM<E> a = i.next();
			if (a.getDest() == y) return a;
		}
		return null;
	}

	/**
	 * Restituisce il nodo con indice <code>i</code> (<font color=red>Tempo O(1)</font>). L'operazione
	 * viene realizzata restituendo il nodo esistente
	 * alla posizione <code>i</code> di <code>nodi</code>.
	 * 
	 * @param i l'indice di cui si vuol conoscere il nodo
	 * @return il nodo di indice <code>i</code>
	 */
	public NodeMWBM<E> getNode(int i){
		if (i < 0 || i >= n) return null;
		return nodes.get(i);
	}

	/**
	 * Inserisce un nuovo nodo con contenuto informativo <code>info</code> (<font color=red>Tempo O(1)</font>).
	 * L'inserimento avviene creando una nuova istanza di <code>NodoLA</code>
	 * e collocandola nella prima posizione libera di <code>nodi</code>.
	 * Nel caso in cui l'array <code>nodi</code> sia pieno, si esegue il metodo
	 * <code>ridimensiona</code> per raddoppiarne la taglia. 
	 * 
	 * @param info il contenuto informativo del nuovo nodo
	 * @return il riferimento al nodo inserito
	 */
	public NodeMWBM<E> addNode(E e) {
		NodeMWBM<E> v = new NodeMWBM<E>(e, null, this, n);
		nodes.add(v);
		n = n + 1;
		return v;
	}

	/**
	 * Inserisce un nuovo arco <code>(x,y)</code> con contenuto informativo <code>info</code> (<font color=red>Tempo O(1)</font>).
	 * L'operazione avviene creando una nuova istanza di <code>ArcoLA</code>,
	 * aggiungendo il nuovo arco alla lista di archi uscenti da <code>x</code>
	 * ed incrementando il conteggio degli archi entranti in <code>y</code>.
	 * 
	 * @param x il nodo origine dell'arco da inserire
	 * @param y il nodo destinazione dell'arco da inserire 
	 * @param info il contenuto informativo del nuovo arco
	 * @return il riferimento all'arco inserito
	 */	
	public LinkMWBM<E> addEdge(NodeMWBM<E> x, NodeMWBM<E> y, int weight) {
		LinkMWBM<E> a = new LinkMWBM<E>(x, y, weight);
		//System.out.println("a "+a);
		if (x.getFirstEdge() != null){
			x.getFirstEdge().setPred(a);
			//System.out.println("firstedge: "+x.getFirstEdge());
			//System.out.println("first.pred: "+x.getFirstEdge().pred);
		}
		a.setSucc(x.getFirstEdge());
		//System.out.println("a.succ: "+a.getSucc());
		x.setFirstEdge(a);
		//System.out.println("new first: "+x.getFirstEdge());
		y.setInDegree(y.getInDegree()+1);
		//System.out.println("a.pred: "+a.getPred());
		m = m + 1;
		return a;
	}

	/**
	 * Cancella dal grafo il nodo <code>v</code> e tutti gli archi ad 
	 * esso incidenti (<font color=red>Tempo O(grado di v)</font>). L'eliminazione di <code>v</code> dalle liste di adiacenza
	 * dei nodi cui &egrave; connesso da un arco in uscita avviene solo virtualmente
	 * ed &egrave; effettivamente portata a termine solo durante 
	 * l'invocazione del metodo {@link GraphMWBM#outEdges(Nodo)} su ciascuno di questi nodi. 
	 * Tuttavia, il conteggio <code>m</code> del numero di archi presenti nel grafo
	 * viene aggiornato gi&agrave; in questa fase decrementandolo del numero di
	 * archi entranti nel nodo <code>v</code>. La rimozione avviene:
	 * <ul>
	 * <li> scorrendo la lista degli archi incidenti a <code>v</code>
	 * ed eliminandoli tramite il metodo <code>rimuoviArco</code></li>
	 * <li> marcando come cancellato <code>v</code> attraverso l'assegnamento a <code>null</code> del suo campo <code>grafo</code> </li>
	 * <li> sostituendo nell'array <code>nodi</code> l'elemento <code>v</code>
	 * con l'ultimo elemento ad occupare lo stesso array <code>nodi</code> e
	 * riducendo la taglia di <code>nodi</code> per escluderne l'ultimo elemento </li>
	 * <li> aggiornando di conseguenza il conteggio del numero di nodi e di archi presenti nel grafo</li>
	 * <ul>
	 * 
	 * @param v il nodo da cancellare
	 */
	public void removeNode(NodeMWBM<E> v){
		Iterator<LinkMWBM<E>> i = outEdges(v).iterator();
		while (i.hasNext()) 
			removeEdge(i.next());
		m = m - v.getInDegree();
		v.setGraph(null);
		int lastIndex = nodes.size() -1;
		int myIndex = v.getGraphIndex();
		NodeMWBM<E> last = nodes.get(lastIndex);
		last.setGraphIndex(myIndex);
		nodes.remove(nodes.size()-1);
		if(myIndex != lastIndex){
			nodes.remove(v.getGraphIndex());
			nodes.add(v.getGraphIndex(), last);
		}
		n = n - 1;
	}

	/**
	 * Cancella dal grafo l'arco <code>a=(x,y)</code> indicato da input (<font color=red>Tempo O(grado uscente di x)</font>). L'operazione
	 * viene realizzata rimuovendo <code>a</code> dalla lista degli archi
	 * uscenti da <code>x</code> e decrementando di 1 il conteggio
	 * degli archi entranti in <code>y</code>. Il conteggio
	 * del numero complessivo di archi presenti nel grafo viene decrementato
	 * di 1 nel solo caso in cui il nodo  <code>y</code> non sia stato in precedenza marcato come cancellato. 
	 * 
	 * @param a l'arco da cancellare
	 */
	public void removeEdge(LinkMWBM<E> a){
		NodeMWBM<E> x = a.getSource();
		NodeMWBM<E> y = a.getDest();
		if (x.getFirstEdge()==a){
			x.setFirstEdge(a.getSucc());
			//System.out.println("1");
		}
		if (a.getSucc() != null){
			//System.out.println("2");
			a.getSucc().setPred(a.getPred());
		}
		if (a.getPred()!= null){
			//System.out.println("3");
			//System.out.println("a.pred: "+a.getPred());
			//System.out.println("a.pred.succ: "+a.getPred().getSucc());
			//System.out.println("a.succ: "+a.getSucc());
			a.getPred().setSucc(a.getSucc());
			//System.out.println("a.pred.succ: "+a.getPred().getSucc());

		}
		y.setInDegree(y.getInDegree() -1);
		if (y.getGraph() != null) m = m - 1;
	}
	
	public String toString(){
		String result = "Graph "+n+"x"+m+"\n";
		Iterator<LinkMWBM<E>> it = edges().iterator();
		while(it.hasNext()){
			LinkMWBM<E> l = it.next();
			result+= l.toString()+"\n";
		}
		return result;
	}
	
}

/*
 * Copyright (C) 2007 Camil Demetrescu, Umberto Ferraro Petrillo, Irene
 * Finocchi, Giuseppe F. Italiano
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */