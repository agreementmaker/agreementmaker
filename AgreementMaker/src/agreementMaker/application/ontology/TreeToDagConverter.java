package agreementMaker.application.ontology;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import agreementMaker.userInterface.vertex.Vertex;

/** this is used to create a fake DAG given the root of tree of vertex with duplicates and so on
 *  so you will have to do new TreeToDagConverter(Vertex root) 
 *  and then invoke methods from that
 *  It is used also to avoid using the vertex structure, so that you can only use nodes
 *  So vertex should the parameter only of the constructor, all the other methods should receive and return node
 *  IT WORKS ONLY IF WE KEEP THE STRUCTURE LIKE THIS WITH ROOTS AT SECOND LEVEL
	//0 LEVEL ontology name, 1 level class and prop hierachy fake root nodes, 2 level real roots 
 *  */
public class TreeToDagConverter {
	
	final static int REALROOTSLEVEL = 2;
	
	private Vertex root;
	
	public TreeToDagConverter(Vertex r) {
		if(r == null) {
			throw new RuntimeException("DEVELOPER ERROR, you are creating a dag from a null tree, maybe this ontology doesn't contain properties or classes, in that case the tree shouldn't be explored.");
		}
		root = r;
	}
	
	public Vertex getRoot() {
		return root;
	}

	public void setRoot(Vertex root) {
		this.root = root;
	}
	
	//*************************************** DAG TO TREE CONVERSION METHODS ********************************************************
	
	//RETURN THE LIST OF ROOTS Of this dag, it WORKS ONLY IF WE KEEP THE STRUCTURE LIKE THIS WITH ROOTS AT SECOND LEVEL
	//0 LEVEL ontology name, 1 level class and prop hierachy fake root node, 2 level real roots 
	public ArrayList<Node> getRoots(){
		ArrayList<Node> result = new ArrayList<Node>();
		Enumeration<Vertex> allVertex = root.breadthFirstEnumeration();
		Vertex v = allVertex.nextElement();
		while(v.getLevel() <= REALROOTSLEVEL && allVertex.hasMoreElements()) {
			v = allVertex.nextElement();
			if(!v.isFake()) {
				result.add(v.getNode());
				//System.out.println(v.getNode().getLocalName());
			}
		}
		return result;
	}
	

	public ArrayList<Node> getLeaves(){
		ArrayList<Node> result = new ArrayList<Node>();
		Enumeration<Vertex> allVertex = root.breadthFirstEnumeration();
		while(allVertex.hasMoreElements()) {
			Vertex v = allVertex.nextElement();
			if(!v.isFake() && v.isLeaf()) {
				result.add(v.getNode());
				//System.out.println(v.getNode().getLocalName());
			}
		}
		return result;
	}
	


	//********************************************STATIC METHOD
	/**return the list of descendants of both this two node. Could be optimezed i guess but i don't have time.
	 * it's from the highest to the lowest, but there could be more then one at the same level.
	 * 
	 * Such a pain the ass, i tested it however a lot and it seems to be correct. I don't have time to put comments needed.
	 * */
	
	public static ArrayList<Node> getOrderedCommonDescendants(Node first, Node second){
		ArrayList<Node> result = new ArrayList<Node>();

		//I need to see if any of the descendants of the lowest are descendats of  the highest
		Node higherOne = first;
		Node lowerOne = second;
		if(first.getLevel() > second.getLevel()) { //if you higher in tree the level is lower, root is 0 for example
			higherOne = second;
			lowerOne = first;
		}
		
		Vertex lowerVertex = lowerOne.getVertex();
		
		HashSet processed = new HashSet<Node>();
		Enumeration<Vertex> allVertex = lowerVertex.breadthFirstEnumeration();
		while(allVertex.hasMoreElements()) {
			Vertex v = allVertex.nextElement();
			Node n = v.getNode();
			if(higherOne.isNodeDescendant(n)) {
				if(!processed.contains(n)){
					result.add(n);
					processed.add(n);
				}
			}
		}
		
		/*
		if(!first.getLocalName().equals(second.getLocalName()) && result.size() > 0) {
			System.out.println("\n\nCommon descendants of "+first.getLocalName()+" & "+second.getLocalName());
			for(int i= 0; i < result.size(); i++) {
				Node bla = result.get(i);
				System.out.print(bla.getLocalName()+" ");
			}
		}
		*/
		/*
		if(first.getLocalName().equals("PROJECTILE-WEAPON") && second.getLocalName().equals("BOMB")) {
			System.out.println("\n\nCommon descendants of "+first.getLocalName()+" & "+second.getLocalName());
			for(int i= 0; i < result.size(); i++) {
				Node bla = result.get(i);
				System.out.print(bla.getLocalName()+" ");
			}
		}
		*/
		return result;
	}

}
