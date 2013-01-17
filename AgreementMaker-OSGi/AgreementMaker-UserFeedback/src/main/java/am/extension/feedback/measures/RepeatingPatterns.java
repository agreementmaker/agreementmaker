package am.app.feedback.measures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class RepeatingPatterns extends RelevanceMeasure{

	Ontology sourceOntology;
	Ontology targetOntology;
	
	List<Node> sClasses;
	List<Node> tClasses;
	List<Node> sProps;
	List<Node> tProps;
	
	NodeComparator nc;
	ArrayList<Node> noParentNodes;
	
	//ArrayList<ArrayList<Node>> patterns;
	ArrayList<Integer> patternFreqs;
	ArrayList<Double> repetitiveMeasure;
	
	int whichOntology;
	alignType whichType;
	
	HashMap<Pattern, Integer> patterns;
	public RepeatingPatterns() {
		super();
		init();
	}
	
	private void init() {
		nc = new NodeComparator();
		Core core = Core.getInstance();
    	sourceOntology = core.getSourceOntology();
    	targetOntology = core.getTargetOntology();
    	sClasses = sourceOntology.getClassesList();
    	tClasses = targetOntology.getClassesList();
    	sProps = sourceOntology.getPropertiesList();
    	tProps = targetOntology.getPropertiesList();
    	//patterns = new ArrayList<ArrayList<Node>>();
    	patternFreqs = new ArrayList<Integer>();
    	repetitiveMeasure = new ArrayList<Double>();
    	patterns = new HashMap<Pattern, Integer>();
	}
	
	public RepeatingPatterns(double th) {
		super(th);
		init();
	}
	
	 
	public void calculateRelevances() {
		run(4,4);
		System.out.println("I m inside the calculate relevances of Repeating Patterns");
	}
	
	public void run(int k, int edgeSize){
		
		whichType = alignType.aligningClasses;
		whichOntology = Ontology.SOURCE;
		computeRelevances(k, edgeSize, sClasses);
		whichOntology = Ontology.TARGET;
		computeRelevances(k, edgeSize, tClasses);
		whichType = alignType.aligningProperties;
		whichOntology = Ontology.SOURCE;
		computeRelevances(k, edgeSize, sProps);
		whichOntology = Ontology.TARGET;
		computeRelevances(k, edgeSize, tProps);
		
		//printPatterns(finalPatterns);
	}
	
	private void computeRelevances(int k, int edgeSize, List<Node> list) {
		List<Node> newList = new ArrayList<Node>();
		newList.addAll(list);
		Collections.sort(newList, nc);
		List<Edge> start = createEdgesFromNodeList(newList);
		List<Pattern> finalPatterns = getPatternsGivenLength(start, k, edgeSize);
		double[] relevances = new double[list.size()];
		Iterator<Pattern> it = finalPatterns.iterator();
		while(it.hasNext()){
			Pattern p = it.next();
			int size = p.getEdgeSequence().size();
			int frequency = patterns.get(p);
			double relevance = (double)frequency/(double)size;
			Iterator<Edge> itEdge = p.getEdgeSequence().iterator();
			while(itEdge.hasNext()){
				Edge e = itEdge.next();
				int source = e.getSourceNode().getIndex();
				if(relevances[source] < relevance){
					relevances[source] = relevance;
				}
				int target = e.getTargetNode().getIndex();
				if(relevances[target] < relevance){
					relevances[target] = relevance;
				}	
			}
		}
		for(int i = 0; i < relevances.length; i++){
			candidateList.add(new CandidateConcept(list.get(i),relevances[i],whichOntology, whichType));
		}
	}
	
	//Returns Lex sorted children of a node
	public List<Node> getChildrenSorted(Node n){
		List<Node> sortedChildren = new ArrayList<Node>();
		sortedChildren = n.getChildren();
		if(sortedChildren != null){
			sortNodesLex(sortedChildren);
			return sortedChildren;
		}
		else{
			return null;
		}
	}
	
	//Sorts given list in Lexicographical order
	public List<Node> sortNodesLex(List<Node> nodeList){
		Collections.sort(nodeList, nc);
		return nodeList;
	}
	
	//Sorts given list in Lexicographical order
	public List<Edge> sortEdgesLex(List<Edge> edgeList){
		Collections.sort(edgeList);
		return edgeList;
	}
	
	//
	public void createCandidateList(){

	}
	
	//
	public List<Edge> createEdgesFromNodeList(List<Node> list){
		List<Edge> edges = new ArrayList<Edge>();
		for(Node n: list){
			List<Node> nAdj = n.getChildren();
			for(Node child: nAdj){
				Edge e = new Edge(n, child);
				edges.add(e);
			}			
		}
		return edges;
	}

	
	//Generate patterns of length k
	public List<Pattern> getPatternsGivenLength(List<Edge> list, int k, int edgeSize){
		
		List<Pattern> pats = new ArrayList<Pattern>();
		List<Edge> edges = sortEdgesLex(list);
		Node srcNode = null;
		Pattern aPat = null;
		
		List<Edge> edgeSeq = new ArrayList<Edge>();
		
		for(Edge a : edges){
						
			a.setSourceVisit(1);
			a.setTargetVisit(2);
			edgeSeq.add(a);
			Pattern p = new Pattern(edgeSeq, null);
			p.setLastVisit(2);
			
			if(patterns.containsKey(p)){
				Integer aVal = patterns.get(p);
				int val = aVal.intValue() + 1;
				aVal = new Integer(val);
				patterns.put(p, aVal);
			}
			else{
				Integer freq = new Integer(1);
				patterns.put(p, freq);
			}
			//get the siblings of the edge passed in, and recurse again on them
						
			growEdge(p, a, k, pats, false);
			//create edgelist of that list
			//
			srcNode = a.getSourceNode();
			List<Node> srcNodeChildren = getChildrenSorted(srcNode);
			
			List<Edge> asd = createEdgesFromNodeList(srcNodeChildren);
			if(asd.contains(a)){
				asd.remove(a);
			}
			//bu yeni pattern gerekli mi?
			List<Pattern> pp = new ArrayList<Pattern>();
			for(Edge b: asd){
				for(int i = 0; i < pats.size(); i++)
				{
					aPat = pats.get(i);
					growEdge(aPat, b, k, pp, true);
				}
			}
		}
		
		return pats;
	}
	
	
	
	//Find repeating patterns here
	public void growEdge(Pattern p, Edge a, int k, List<Pattern> pats, boolean st){

		//Decide growing source or target
		Node nodeToGrow = null;
		if(st){
			nodeToGrow = a.getSourceNode();
		}
		else{
			nodeToGrow = a.getTargetNode();
		}
		
		
		//Nodes are sorted lex
		List<Node> ntgADJ = nodeToGrow.getChildren();
		ntgADJ = sortNodesLex(ntgADJ);
		
		//ArrayList<Edge> ntgEdges = createEdgesFromNodeList(ntgADJ);
		
		while(!ntgADJ.isEmpty()){
			//Create new edge with removed node to create a new pattern 
			Node removed = ntgADJ.get(0);
			Edge passInRecursion = new Edge(nodeToGrow, removed);
			
			//Lastvisit is the variable to keep track of the number
			for(int i = 0; i < p.getEdgeSequence().size(); i++){
				Edge temp = p.getEdgeAtIndex(i);
				if(temp.getSourceNode().getIndex() == nodeToGrow.getIndex())
				{
					p.setLastVisit(temp.getSourceVisit());
				}
				else if(temp.getTargetNode().getIndex() == nodeToGrow.getIndex())
				{
					p.setLastVisit(temp.getTargetVisit());
				}
			}
			
			//p.setLastVisit(p.getLastVisit()+1);
			passInRecursion.setSourceVisit(p.getLastVisit());
			p.setLastVisit(p.getLastVisit()+1);
			passInRecursion.setTargetVisit(p.getLastVisit());
			
			ntgADJ.remove(ntgADJ.get(0));
			ArrayList<Edge> edgSeq = new ArrayList<Edge>();
			edgSeq.add(passInRecursion);
			
			Pattern nextPattern = new Pattern(edgSeq, p);
			Pattern copyOfPattern = new Pattern(nextPattern);//push ...
						
			//Add created new pattern to the list of patterns
			pats.add(nextPattern);
			
			//increment freq
			if(patterns.containsKey(nextPattern)){
				Integer aVal = patterns.get(nextPattern);
				int val = aVal.intValue() + 1;
				aVal = new Integer(val);
				patterns.put(nextPattern, aVal);
			}
			else{
				Integer freq = new Integer(1);
				patterns.put(p, freq);
			}
			
			//check max len pattern size is reached
			//first recursion
			//if less than k do recursions
			ArrayList<Pattern> pats2 = new ArrayList<Pattern>();
			
			//If pattern length is less than k than grow it
			if(copyOfPattern.getLength() < k)
				growEdge(copyOfPattern, passInRecursion, k, pats2, st);
			
			//Add found patterns into list
			pats.addAll(pats2);
			
			//Recurse again on the pattern collected from the first recursion
			//using the siblings(children) of the current edge. (if size is less than k)
			for(int i = 0; i < pats2.size(); i++)
			{
				Pattern p2 = pats2.get(i);
				//pats.add(pats2.get(i));
				
				Node curr = passInRecursion.getTargetNode();
				List<Node> currChildren = getChildrenSorted(curr);
				List<Edge> currEdge = createEdgesFromNodeList(currChildren);
				
				for(Edge ed: currEdge)
				{
					if(p2.getLength() < k)
						growEdge(p2, ed, k, pats, st); //grow also on pats2.get(i)
				}
			}
			
		}
		
		/*
		if(patterns.containsKey(p)){
			Integer aVal = patterns.get(p);
			int val = aVal.intValue() + 1;
			aVal = new Integer(val);
			patterns.put(p, aVal);
		}
		else{
			Integer freq = new Integer(1);
			patterns.put(p, freq);
		}
		*/
	}
	
	//Print patterns in the list
	public void printPatterns(){
		for(int i = 0; i < patterns.size(); i++){
			System.out.println(patterns.get(i));
		}
	}
	
	//Print patterns in the list
	public void printPatterns(ArrayList<Pattern> pats){
		for(int i = 0; i < pats.size(); i++){
			System.out.println(pats.get(i).toString());
		}
	}
	
	//Calculates frequencies for all patterns
	public double patternFreq(){
		return 0.0;
	}
	
	//Returns length of given pattern
	public double patternLen(Pattern p){
	 return p.getEdgeSequence().size();
	}
	
	//Calculate repetitive measure for each pattern in the list
	public void repetitiveMeasure(){
		
	}
	
	//Returns frequency of given pattern
	public int getFreqOfPattern(){
		return -1;
	}
	
	//Returns true if a given node is in a repeating pattern
	public boolean repeats(Node n){
		return false;
	}
	
	//
	public double averageRelevance(){
		return 0.0;
	}
	
	/*
	//
	public void calculateRelevances(int k) {

		whichOntology = Ontology.SOURCE;
		whichType     = alignType.aligningClasses;
		getPatternsGivenLength(sClasses, k);
		
		int i = 0;
		for( i = 0 ; i < patterns.size(); i++){
			createCandidateList(patterns.get(i));
		}
		
		whichOntology = Ontology.TARGET;
		getPatternsGivenLength(tClasses, k);
		
		int j = 0;
		for( j = i ; j < patterns.size(); j++){
			createCandidateList(patterns.get(j));
		}
		
		whichOntology = Ontology.SOURCE;
		whichType     = alignType.aligningProperties;
		getPatternsGivenLength(sProps, k);
		
		int m = 0;
		for( m = j ; m < patterns.size(); m++){
			createCandidateList(patterns.get(m));
		}
		
		whichOntology = Ontology.TARGET;
		getPatternsGivenLength(tProps, k);
		
		int n = 0;
		for( n = m ; n < patterns.size(); n++){
			createCandidateList(patterns.get(n));
		}
	}
	*/
}
