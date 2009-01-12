package agreementMaker.application.mappingEngine.qualityEvaluation;

import java.util.ArrayList;
import java.util.Iterator;

import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AlignmentSet;
import agreementMaker.application.ontology.Node;
import agreementMaker.application.ontology.Ontology;
import agreementMaker.application.ontology.TreeToDagConverter;
import agreementMaker.userInterface.vertex.Vertex;



/**
 * FROM:
 * Evaluating the Structural Quality of
	Semantic Hierarchy Alignments
	Cliff Joslyn, Alex Donaldson, and Patrick Paulson
	 */
public class JoslynStructuralQuality {
	
	private String quality;
	private AbstractMatcher matcher;
	
	public final static int HIGHER = 1;
	public final static int NONCOMPARABLE = 0;
	public final static int LOWER = -1;
	
	
	public enum evaType {
		classes,
		properties
	}
	
	//SOURCE ONTOLOGY STRUCTURES
	//an array num of descendants of each node. sourceDescendants[node.getIndex()] = num of  descendants of node
	//it will be used first for properties and then for classes nodes
	int[] sourceDescendants; 
	int[][] sourceDistances;
	
	//TARGET ONTOLOGY STRUCTURES
	int[] targetDescendants;
	int[][] targetDistances;
	
	//TEMP STRUCTURES USED BY RECURSIVE METHODS
	int[] tempDescendants;
	
	public JoslynStructuralQuality(AbstractMatcher m, String q) {
		quality = q;
		matcher = m;
	}

	public QualityEvaluationData getQuality() {
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(false);
		q.setLocalForSource(true); //It doesn't matter because is global
		
		if(matcher.areClassesAligned()) {
			double measure = evaluate(matcher.getClassAlignmentSet(),sourceOntology.getClassesList(),targetOntology.getClassesList(), sourceOntology.getClassesTree(), targetOntology.getClassesTree());
			q.setGlobalClassMeasure(measure);
		}
		if(matcher.arePropertiesAligned()) {
			double measure = evaluate(matcher.getPropertyAlignmentSet(),sourceOntology.getPropertiesList(),targetOntology.getPropertiesList(), sourceOntology.getPropertiesTree(), targetOntology.getPropertiesTree());
			q.setGlobalPropMeasure(measure);
		}
		
		return q;
	}

	private double evaluate(AlignmentSet set,
			ArrayList<Node> sourceList, ArrayList<Node> targetList,
			Vertex sourceTree, Vertex targetTree) {
		
		TreeToDagConverter sourceDag = new TreeToDagConverter(sourceTree);
		TreeToDagConverter targetDag = new TreeToDagConverter(targetTree);
		
		//create the array for target and source with the numver of descendants of each node
		sourceDescendants = createDescendantsArray(sourceList,sourceDag );
		/* DEBUG
		System.out.println("\nsoruce descendants");
		for(int i = 0; i < sourceDescendants.length; i++) {
			System.out.println(sourceList.get(i).getLocalName()+" "+sourceDescendants[i]);
		}
		*/
		targetDescendants = createDescendantsArray(targetList,targetDag );
		
		//I need to calculate which is the distance between each pair of node
		sourceDistances = createDistances(sourceList, sourceDescendants);
		targetDistances = createDistances(targetList, targetDescendants);
		

		double result = 0;
		return result;
	}
	
	/**The formula is d(a,b) = numOfDescendants(a) + numOfDescendants(b) - 2 numOfDescendants(maxCommonDescendants(a,b))
	 * then it has to be normalized dividing it for the diameter that is the max distance
	 * The max commonDescendats is the commonDescendants between a and b, which has highest num of descendants itself
	 * The highest commonDescendants are all at the same level in the hierarchy. I was thinking of using the total num of common descendants but is not exatly the same.
	 */
	private int[][] createDistances(ArrayList<Node> nodesList, int[] descendants) {
		int[][] distances = new int[nodesList.size()][nodesList.size()];
		
		for(int i = 0; i < nodesList.size(); i++) {
			for(int j = 0; j < nodesList.size(); j++) {
				
				int descendantsA = descendants[i];
				int descendantsB = descendants[j];
				int maxCommonDescendants = 0;
				ArrayList<Node> commonDescendants = TreeToDagConverter.getOrderedCommonDescendants(nodesList.get(i), nodesList.get(j));
				if(commonDescendants.size() > 0) {
					//I need to find which is the common descendant with highest num of descendants
					//so it can only be one of the common descendants in the highest level
					//since the commonDescendants list is ordered, i just need stop scanning when i finish to scan the highest level that is also the first
					//remember that the level goes from 0, the root is 0 for example, so the highest level is the lowest number in real
					int highestLevel = commonDescendants.get(0).getLevel();
					for(int k = 0; k < commonDescendants.size(); k++) {
						Node common = commonDescendants.get(k);
						if(common.getLevel() != highestLevel) {
							//i've finished the highest level so it's useless to look in the lower descendants
							break;
						}
						else {
							//find the max
							int toBeCompared = descendants[common.getIndex()];
							if(maxCommonDescendants < toBeCompared) {
								maxCommonDescendants = toBeCompared;
							}
						}
					}
				}
				
				//finally the distance formula
				distances[i][j] = descendantsA + descendantsB - ( 2 * maxCommonDescendants);
				System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" "+distances[i][j]);
			}
		}
		
		//the diameter is the max distance
		int diameter = Utility.getMaxOfIntMatrix(distances);
		System.out.println("diameter: "+diameter);
		
		//normalize distances
		
		return distances;
	}

	private int[] createDescendantsArray(ArrayList<Node> sourceList, TreeToDagConverter sourceTree) {
		//for each node we will have a list of descendants
		tempDescendants = new int[sourceList.size()]; //to be used only by recursive descendants
		ArrayList<Node> roots = sourceTree.getRoots();
		Iterator<Node> it = roots.iterator();
		while(it.hasNext()) {
			Node n = it.next();
			recursiveDescendantsCount(n, sourceTree);
		}
		int[] result = tempDescendants;
		tempDescendants = null;
		return result;
	}

	private void recursiveDescendantsCount(Node n,  TreeToDagConverter tree) {
		//I'm one of my descendants by definition
		int val = 1;
		if(!n.isLeaf()) {
			ArrayList<Node> parents = n.getChildren();
			Iterator<Node> it = parents.iterator();
			while(it.hasNext()) {
				//my number of descendants: me + the descendants of my son
				Node child = it.next();
				if(tempDescendants[child.getIndex()] == 0) {// if a node is the child of two node then it could be already being processes so we don't need to process it again
					recursiveDescendantsCount(child, tree); //this set the number of descendants of my son
				}
				val += tempDescendants[child.getIndex()];
			}
		}
		tempDescendants[n.getIndex()] = val;
		
	}
	

}
