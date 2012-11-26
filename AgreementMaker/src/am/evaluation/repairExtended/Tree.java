package am.evaluation.repairExtended;

import java.util.ArrayList;
import java.util.HashSet;

public class Tree<T> {

	//private T Node;
	//private ArrayList<Tree<T>> Children;	
	
	private Bimap<T,T> relationalTable = new Bimap<T,T>();
	
	public Tree(){
		//this.Node = null;
		//Children = new ArrayList<Tree<T>>();
        relationalTable.add(null, null);
    }
	
	public Tree(T node)
    {
        //this.Node = node;       
        //Children = new ArrayList<Tree<T>>();
        relationalTable.add(node, null);
    }
	
	public void addChild(T child, T parent)
    {        
        //Children.add(new Tree<T>(child));
		try{
			relationalTable.add(parent, child);
			relationalTable.remove(parent,null);
        }
		catch(Exception ex){
			
		}
    }
	
	/*public void addChild(T child, T parent, Integer flag)
    {
		try{
			relationalTable.add(parent, child, flag);
			relationalTable.remove(parent,null);
        }
		catch(Exception ex){
			
		}
    }*/
	
	public ArrayList<T> getChildren(T node){
		
		return relationalTable.getValuesByKey(node);
    }
	
	public T getParent(T node){
		
		return relationalTable.getKeyByValue(node);
	}
	
	public ArrayList<T> getLeafNodes(){
		
		return relationalTable.getBottomKeys();
	}
	
	public ArrayList<ArrayList<T>> getAllBranches(ArrayList<T> reqdList){
		
		ArrayList<ArrayList<T>> branches = new ArrayList<ArrayList<T>>();
		
		ArrayList<T> leafNodes = getLeafNodes();
		ArrayList<T> branch = new ArrayList<T>();
		ArrayList<T> tempBranch;
		
		for(T leaf : leafNodes){
			
			tempBranch = new ArrayList<T>();
			branch = getBranch(leaf);
			
			for(T node : branch){
				if(reqdList.contains(node))
					tempBranch.add(node);
			}
			
			if(tempBranch.size() > 0)
				branches.add(tempBranch);
		}
		
		//removing duplicates
		HashSet<ArrayList<T>> hs = new HashSet<ArrayList<T>>();
		hs.addAll(branches);
		branches.clear();
		branches.addAll(hs);
		
		return branches;
	}
	
	public ArrayList<T> getBranch(T node){
		
		ArrayList<T> branch = new ArrayList<T>();
		
		branch.add(node);
		branch = getBranchRecursive(node, branch);
		
		return branch;
	}
	
	public ArrayList<T> getBranchRecursive(T node, ArrayList<T> branch){
		
		T parentNode = getParent(node);
		
		if(parentNode != null){
			branch.add(parentNode);
			branch = getBranchRecursive(parentNode,branch);
		}
		
		return branch;		
	}
	
	public void print(){
		
		System.out.println("table--");
		
		for(KeyValue<T,T> pair : relationalTable.getBimap()){
			System.out.println(pair.getKey() + "-" + pair.getValue());
		}
		
	}
	
	//get set
	/*public void setNode (T node)
    {
    	Node = node;           
    }
    public T getNode()
    {
        return Node;
    }*/
    
    /*public void setChildren (ArrayList<Tree<T>> children)
    {
    	Children = children;           
    }
    public ArrayList<Tree<T>> getChildren()
    {
        return Children;
    }*/
}
