package am.app.mappingEngine.oneToOneSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import javax.swing.JOptionPane;

public class MaxWeightBipartiteMatching<E> {
	
	protected Graph<E> G;
	protected List<NodeMWBM<E>> sourceNodes;
	protected List<NodeMWBM<E>> targetNodes;
	
	public Graph<E> getG() {
		return G;
	}
	
	public List<NodeMWBM<E>> getSourceNodes() {
		return sourceNodes;
	}

	public List<NodeMWBM<E>> getTargetNodes() {
		return targetNodes;
	}

	protected boolean debug = false;
	
	
	public MaxWeightBipartiteMatching(double[][] matrix, double threshold){
		int[][] intMatrix = Wrapper.getIntMatrix(matrix, Wrapper.DEFAULT_MULTIPLIER);
		int intThreshold = Wrapper.getMultipliedInt(threshold, Wrapper.DEFAULT_MULTIPLIER);
		initStructures(intMatrix,intThreshold);
	}
	
	public MaxWeightBipartiteMatching(double[][] matrix, double threshold, int multiplier){
		int[][] intMatrix = Wrapper.getIntMatrix(matrix, multiplier);
		int intThreshold = Wrapper.getMultipliedInt(threshold, multiplier);
		initStructures(intMatrix,intThreshold);
	}
	
	public MaxWeightBipartiteMatching(int[][] matrix, int threshold){
		initStructures(matrix,threshold);
	}
	
	public MaxWeightBipartiteMatching(Graph<E> G,List<NodeMWBM<E>> sourceNodes, List<NodeMWBM<E>> targetNodes){
		this.G = G;
		this.sourceNodes = sourceNodes;
		this.targetNodes = targetNodes;
	}
	
	public MaxWeightBipartiteMatching(Collection<MappingMWBM<E>> mappings, int threshold){
		initStructures(mappings, threshold);
	}
	
	protected void initStructures(Collection<MappingMWBM<E>> mappings, int threshold){
		HashMap<E, NodeMWBM<E>> sources = new HashMap<E, NodeMWBM<E>>();
		HashMap<E, NodeMWBM<E>> targets = new HashMap<E, NodeMWBM<E>>();
		Iterator<MappingMWBM<E>> it = mappings.iterator();
		G = new GraphMWBM<E>();
		sourceNodes = new ArrayList<NodeMWBM<E>>();
		targetNodes = new ArrayList<NodeMWBM<E>>();
		while(it.hasNext()){
			MappingMWBM<E> m = it.next();
			if(m.getWeight() >= threshold){
				E source = m.getSourceNode();
				E target = m.getTargetNode();
				NodeMWBM<E> sourceNode = sources.get(source);
				NodeMWBM<E> targetNode = targets.get(target);
				if(sourceNode == null){
					sourceNode = G.addNode(source);
					sourceNodes.add(sourceNode);
					sources.put(source, sourceNode);
				}
				if(targetNode == null){
					targetNode = G.addNode(target);
					targetNodes.add(targetNode);
					targets.put(target, targetNode);
				}
				G.addEdge(sourceNode, targetNode, m.getWeight());
			}
		}
	}
	
	protected void initStructures(int[][] intMatrix, int intThreshold){
		G = (Graph<E>) new GraphMWBM<Integer>();
		int rows = intMatrix.length;
		int cols = 1;
		if(rows > 0);
			cols = intMatrix[0].length;
		sourceNodes = new ArrayList<NodeMWBM<E>>(rows);
		targetNodes = new ArrayList<NodeMWBM<E>>(cols);
		if(rows > 0){
			NodeMWBM<E> sourceNode;
			NodeMWBM<E> targetNode;
			int weight;
			for(int i = 0; i < rows; i++){
				sourceNode = G.addNode((E) new Integer(i));
				sourceNodes.add(sourceNode);
			}
			
			for(int j = 0; j < cols; j++){
				targetNode = G.addNode((E) new Integer(j));
				targetNodes.add(targetNode);
			}
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					weight = intMatrix[i][j];
					if(weight >= intThreshold){
						G.addEdge(sourceNodes.get(i),targetNodes.get(j), weight);
					}
				}
			}
		}
	}
	
	/**
	 * Compute the Maximum Weighted 1-1 matching on the bipartite graph. 
	 * In order to do this, it builds the bipartite graph G = < sourceNodes U targetNodes, mappings>
	 * and it applies the shortest augmenting path algorithm. 
	 * @return the set of selected mappings, such that each node is mapped at most once, and the total similarity is maximized
	 */
	public Set<MappingMWBM<E>> execute(){
		return MWBMalgorithm(G, sourceNodes, targetNodes);
	}
	
	/**
	 * The implementation of the algorithm. The pseudo-code of the algorithm can be found at http://www.mpi-inf.mpg.de/~mehlhorn/Optimization/bipartite_weighted.ps starting page 13.
	 * @param <E>
	 * @param G
	 * @param sourceNodes
	 * @param targetNodes
	 * @return
	 */
	protected static <E> Set<MappingMWBM<E>> MWBMalgorithm(Graph<E> G,List<NodeMWBM<E>> sourceNodes, List<NodeMWBM<E>> targetNodes){
		//System.out.println("Source nodes: "+sourceNodes);
		//System.out.println("Target nodes: "+targetNodes);
		//System.out.println("Graph: "+G);
		Set<MappingMWBM<E>> result = new HashSet<MappingMWBM<E>>();
		if(G.edgesCount() > 0){
			
			//Data structures and variables
			NodeMWBM<E> a,b;
			Iterator<LinkMWBM<E>> itLinks;
			Iterator<NodeMWBM<E>> itNodes;
			LinkMWBM<E> auxLink;
			//ALREADY DONE: initialization of nodes variable pot, dist, free e pred in Node constructor
			//SHOULD BE DONE: check of all edges are directed from A to B.
			PriorityQueue<NodeMWBM<E>> PQ = new PriorityQueue<NodeMWBM<E>>();
			
			//Initialization with heuristic 0
			//set pot of all nodes in A = max weight of edges
			int maxWeight = 0;
			itLinks = G.edges().iterator();
			while(itLinks.hasNext()){
				auxLink = itLinks.next();
				if(auxLink.getWeight() > maxWeight)
					maxWeight = auxLink.getWeight();
			}
			//System.out.println("initial pot = maxWeight = "+maxWeight);
			itNodes = sourceNodes.iterator();
			while(itNodes.hasNext()){
				a = itNodes.next();
				a.setPotential(maxWeight);
			}
			
			//Shortest augmenting path algorithm
			itNodes = sourceNodes.iterator();
			while(itNodes.hasNext()){
				a = itNodes.next();
				if(a.isFree()){
					//System.out.println("calling augment: "+a+" "+PQ.size());
					augment(G,a,PQ);
				}
			}
			
			//The selected mappings are all edges from B to A
			itNodes = targetNodes.iterator();
			while(itNodes.hasNext()){
				b = itNodes.next();
				Collection<LinkMWBM<E>> selectedLinks = G.outEdges(b);
				if(selectedLinks.size() > 1) System.out.println("More than one match for "+b);
				//it should be that there is at most one for each node
				itLinks = selectedLinks.iterator();
				while(itLinks.hasNext()){
					auxLink = itLinks.next();
					result.add(getReversedMappingFromLink(G, auxLink));
				}
			}
			
		}
		
		/*DEBUG
		Iterator<LinkMWBM> it = G.getEdges().iterator();
		while(it.hasNext()){
			LinkMWBM l = it.next();
			NodeMWBM<E> s = G.getSource(l);
			NodeMWBM<E> t = G.getDest(l);
			MappingMWBM<E> m = new MappingMWBM<E>(s,t,l.getWeight());
			result.add(m);
		}
		*/
		return result;
	}
	
	protected static <E> void augment(Graph<E> G,NodeMWBM<E> a,PriorityQueue<NodeMWBM<E>> PQ){
		//1) INITIALIZATION
		//distance of each node measures the distance from a to it.
		a.setDistance(0);
		//minimum pot + dist for all nodes in A seen so far
		int minA = a.getPotential();
		//node corresponding to minA
		NodeMWBM<E> bestNodeInA = a;
		int delta;//it will keep min(minA,minB)
		//RA contains all nodes in A added to PQ or with predecessor is set.
		//right now PQ is empty and the only node with the predecessor set is a which has no predecessor
		Stack<NodeMWBM<E>> RA = new Stack<NodeMWBM<E>>();
		RA.push(a);
		//RB equal to RA but in respect to B
		Stack<NodeMWBM<E>> RB = new Stack<NodeMWBM<E>>();
		NodeMWBM<E> a1 = a; 
		LinkMWBM<E> e;
		NodeMWBM<E> b;
		int db;
		//relax all edges out of a1
		relaxAllEdgesOutOfA1(G,a1, PQ, RA, RB);
		
		//LOOP
		int whichloop = 0;
		while(true){
			//retrieve the b node with minimum distance from a
			if(PQ.isEmpty()){
				//System.out.println(whichloop+" loop: pq is empty.");
				b = null;
				db = 0; //we can set to any value because we are not going to use it
			}
			else{
				b = PQ.poll();
				//System.out.println(whichloop+" loop: poll "+b);
				db = b.getDistance();
			}
			//distinguish 3 cases
			if(b == null || db >= minA){
				delta = minA;
				augmentByPathTo(G,bestNodeInA);
				a.setFree(false);
				bestNodeInA.setFree(true);//order is important if a is bestonodeina we want it to be true
				break;
			}
			else{
				if(b.isFree()){
					//b is free so augment with path from a to b
					//during the augmentation all edges are reversed
					delta = db;
					augmentByPathTo(G,b);
					a.setFree(false);
					b.setFree(false);
					break;
				}
				else{
					//b is matched
					//continue the shortest path computation
					//same chunk of code of the augment initialization
					Iterator<LinkMWBM<E>> edges = G.outEdges(b).iterator();
					if(edges.hasNext()){
						//there should always be
						e = edges.next();
						a1 = e.getDest();
						a1.setPredecessor(e);
						RA.push(a1);
						a1.setDistance(db);
						if(db + a1.getPotential() < minA){
							bestNodeInA = a1;
							minA = db + a1.getPotential();
						}
						//relax all edges out of a1
						relaxAllEdgesOutOfA1(G, a1, PQ, RA, RB);
					}
					
				}
			}
			whichloop++;
		}
		//all nodes in RA set pred to null and update potenzial with max(0, delta - v.dist)
		//pot of nodes in A is decreased and pot in b is increased
		//node in RB are removed from PQ, nodes in RA cannot be in PQ
		while(!RA.isEmpty()){
			a = RA.pop();
			a.setPredecessor(null);
			int potChange = delta - a.getDistance();
			if(potChange > 0)
				a.setPotential(a.getPotential() - potChange);			
		}
		while(!RB.isEmpty()){
			b = RB.pop();
			b.setPredecessor(null);
			PQ.remove(b);
			int potChange = delta - b.getDistance();
			if(potChange > 0)
				b.setPotential(b.getPotential() + potChange);
		}
		//distances and free are not reset
	}
	
	protected static <E> void augmentByPathTo(Graph<E> G ,NodeMWBM<E> v){
		LinkMWBM<E> e = v.getPredecessor();
		while(e != null){
			e = reverseEdge(G, e);
			e = e.getDest().getPredecessor(); //not source!!!
		}
	}
	
	public static <E> void relaxAllEdgesOutOfA1(Graph<E> G,NodeMWBM<E> a1, PriorityQueue<NodeMWBM<E>> PQ, Stack<NodeMWBM<E>> RA,Stack<NodeMWBM<E>> RB){
		Iterator<LinkMWBM<E>> itEdges = G.outEdges(a1).iterator();
		LinkMWBM<E> e;
		NodeMWBM<E> b;
		int db;
		while(itEdges.hasNext()){
			e = itEdges.next();
			b = e.getDest();
			//the distance from a to b is d(a,b) = d(a,a1) + reducedCost(a1,b) 
			//where reducedCost(a1,b) = pot(a1) + pot(b) - weight(a1,b)
			db = a1.getDistance() + (a1.getPotential() + b.getPotential() - e.getWeight());
			//now if we have not visited the node already then we set his distance and predecessor
			if(b.getPredecessor() == null){
				b.setDistance(db);
				b.setPredecessor(e);
				RB.push(b);
				PQ.add(b);
			}
			//else if we have visited already, we update it only if we found a shortest path
			else if(db < b.getDistance()){
				//there should be a decrease key here, but the heap priority queue is required
				PQ.remove(b);
				b.setDistance(db);
				b.setPredecessor(e);
				PQ.add(b);
			}
		}
	}
	
	protected static <E> LinkMWBM<E> reverseEdge(Graph<E> G, LinkMWBM<E> e){
		
		NodeMWBM<E> source = e.getSource();
		NodeMWBM<E> target = e.getDest();
		//System.out.println("to be deleted: "+e+" source: "+source+" target: "+target);
		G.removeEdge(e);
		//System.out.println("after remove "+G);
		LinkMWBM<E> result = G.addEdge(target, source, e.getWeight());
		//System.out.println("after add "+G);
		//JOptionPane.showInputDialog("blabla");
		return result;
	}
	
	protected static <E> MappingMWBM<E> getReversedMappingFromLink(Graph<E> G, LinkMWBM<E> l){
		NodeMWBM<E> s = l.getSource();
		NodeMWBM<E> t = l.getDest();
		//NODES ARE REVERSED IN RESPECT TO SELECTED LINKS. 
		MappingMWBM<E> m = new MappingMWBM<E>(t.getElement(),s.getElement(),l.getWeight());
		return m;
	}
	
}
