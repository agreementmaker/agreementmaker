/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________          
 * 
 *  
 */

package am.app.mappingEngine.qualityEvaluation.metrics.joslyn;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.ontology.Node;
import am.app.ontology.NodeUtility;
import am.app.ontology.Ontology;
import am.app.ontology.TreeToDagConverter;
import am.utility.parameters.AMParameter;



/**
 * FROM:
 * Evaluating the Structural Quality of
	Semantic Hierarchy Alignments
	Cliff Joslyn, Alex Donaldson, and Patrick Paulson
	 */
public class JoslynStructuralQuality extends AbstractQualityMetric {
	
	//private AbstractMatcher matcher;
	
	/**True if a upper or lower distance or discrepancy is required,
	 * false if the order ones are required
	 * **/
	//private boolean useDistance;
	
	/**True if the upper distance is used, false for the lower distance**/
	//private boolean useUpperDistance; //used only of the distance preservation measure
	
	/**True if the preservation = 1 - discrepancy measure is used**/
	//private boolean usePreservation; 
	
	public final static int HIGHER = 1;
	public final static int NONCOMPARABLE = 0;
	public final static int LOWER = -1;
	
	public static final String PREF_USE_DISTANCE = "USE_DISTANCE";
	public static final String PREF_USE_PRESERVATION = "USE_PRESERVATION";
	public static final String PREF_UPPER_DISTANCE = "USE_PRESERVATION";
	
	public enum evaType {
		classes,
		properties
	}
	
	//TEMP STRUCTURES USED BY RECURSIVE METHODS
	//int[] tempRecursiveNum;
	int[][] tempOrder;
	
	/*public JoslynStructuralQuality(boolean distance,boolean preservation, boolean upper) {
		params.put(new AMParameter(PREF_USE_DISTANCE, distance));
		params.put(new AMParameter(PREF_USE_PRESERVATION, preservation));
		params.put(new AMParameter(PREF_UPPER_DISTANCE, upper));
	}*/

	public void setParameters(boolean distance, boolean preservation, boolean upper) {
		params.put(new AMParameter(PREF_USE_DISTANCE, distance));
		params.put(new AMParameter(PREF_USE_PRESERVATION, preservation));
		params.put(new AMParameter(PREF_UPPER_DISTANCE, upper));
	}
	
	public QualityEvaluationData getQuality(AbstractMatcher m) throws Exception {
		if( params.getBit(PREF_USE_DISTANCE) )
			return getDistanceQuality(m);
		else return getOrderQuality(m);
	}
	
	public QualityEvaluationData getOrderQuality(AbstractMatcher matcher) throws Exception {
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(false);
		q.setSource(true); //It doesn't matter because is global
		
		double classQuality = 0;
		double propQuality = 0;
		if(matcher.areClassesAligned()) {
			
				classQuality = orderPreservation(matcher.getClassAlignmentSet(),sourceOntology.getClassesList(),targetOntology.getClassesList(), sourceOntology.getClassesRoot(), targetOntology.getClassesRoot());
		}
		if(matcher.arePropertiesAligned()) {
				propQuality = orderPreservation(matcher.getPropertyAlignmentSet(),sourceOntology.getPropertiesList(),targetOntology.getPropertiesList(), sourceOntology.getPropertiesRoot(), targetOntology.getPropertiesRoot());			
		}
		q.setGlobalClassMeasure(classQuality);
		q.setGlobalPropMeasure(propQuality);
		
		return q;
	}
	
	public QualityEvaluationData getDistanceQuality(AbstractMatcher matcher) throws Exception {
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(false);
		q.setSource(true); //It doesn't matter because is global
		
		double classQuality = 0;
		double propQuality = 0;
		if(matcher.areClassesAligned()) {
		
				classQuality = distancePreservation(matcher.getClassAlignmentSet(),sourceOntology.getClassesList(),targetOntology.getClassesList(), sourceOntology.getClassesRoot(), targetOntology.getClassesRoot());
				System.out.println("Class alignment quality: "+classQuality);
				
		}
		if(matcher.arePropertiesAligned()) {
		
				propQuality = distancePreservation(matcher.getPropertyAlignmentSet(),sourceOntology.getPropertiesList(),targetOntology.getPropertiesList(), sourceOntology.getPropertiesRoot(), targetOntology.getPropertiesRoot());			
				System.out.println("Property alignment quality: "+propQuality);
		}
		q.setGlobalClassMeasure(classQuality);
		q.setGlobalPropMeasure(propQuality);
		
		return q;
	}
	
	
	/**
	 * Order preservation quality that is 1- oderDiscrepancy by joslyn
	 * @param propertyAlignmentSet
	 * @param propertiesList
	 * @param propertiesList2
	 * @param propertiesTree
	 * @param propertiesTree2
	 * @return
	 */
	private double orderPreservation(Alignment<Mapping> set,
			List<Node> sourceList, List<Node> targetList,
			Node sourceTree, Node targetTree) throws Exception {
		
		TreeToDagConverter sourceDag = new TreeToDagConverter(sourceTree);
		TreeToDagConverter targetDag = new TreeToDagConverter(targetTree);
		
		//for each pair of node i need to set if they are >= <= or non ordered
		//the matrix is initially set to non ordered
		// each node is >= of himself and of his descendants
		// then simmetrically a >= b then b <= a so the <= relations are set copying simmetrically the matrix
		int[][] sourceOrderMatrix = calculateOrderMatrix(sourceList, sourceDag);
		
		/*
		//DEBUG
		for(int i = 0; i < sourceOrderMatrix.length; i++ ) {
			for(int j = 0; j < sourceOrderMatrix[i].length; j++) {
				if(sourceOrderMatrix[i][j] == LOWER) {
					System.out.println(sourceList.get(i).getLocalName()+" "+sourceList.get(j).getLocalName()+" "+sourceOrderMatrix[i][j]);
				}
			}
		}
		*/
		int[][] targetOrderMatrix = calculateOrderMatrix(targetList, targetDag);
		//build the matrix of orderdiscrepancy between alignemnts a1 = (a, a') a2 = (b, b') 
		//orderdiscrepancy(a1, a2) = {if order(a,b) = order(a',b') then 1 else 0
		int[][] orderDescrepancies = calculateOrderDiscrepancies(set, sourceOrderMatrix, targetOrderMatrix);
		
		//final sim = sumOfOrderdiscr / binomial(nunOfAlignments , 2);
		//we have to calculate the average of all discrepancies
		//if we have n alignments, since we have a descrepancy for each pair, it would be:
		// sumOfDescrepancies/ n*n
		//but we don't have to consider the pairs like (a,a) or (b,b). and we have to consider (a,b) but not (b,a)
		//so it will be binomial operation: sumOfDescrepancies / binom(n,2)      binom(n,2)  = n! / 2! (n-2)! = n(n-1) / 2 
		int size = set.size();
	
		double binom = ( size * (size -1) ) / (double) 2;
		
		//calculate the sum
		int sum = Utility.getSumOfHalfIntMatrix(orderDescrepancies);
		double totalDescrepancy = (double)sum / binom;
		
		//the discrepancy is a measure of dissimilarity, between 1 and 0. so the quality should be 1 - totalDescrepancy
		double quality = totalDescrepancy;
		if(params.getBit(PREF_USE_PRESERVATION))
			quality = 1 - totalDescrepancy;
		
		//System.out.println("quality: "+quality+" discrepancy: "+totalDescrepancy+" sum: "+sum+" binom: "+binom+" size: "+size);
		return quality;
	}
	
	private int[][] calculateOrderDiscrepancies(Alignment<Mapping> set,
			int[][] sourceOrderMatrix, int[][] targetOrderMatrix) {
		
		//build the matrix of orderdiscrepancy between alignemnts a1 = (a, a') a2 = (b, b') 
		//orderdiscrepancy(a1, a2) = {if order(a,b) = order(a',b') then 1 else 0
		int size = set.size();
		//for each pair of alignments we have a discrepancy value
		int[][] result = new int[size][size];
		
		for(int i= 0; i < size; i++) {
			//a1
			Mapping first = set.get(i);
			for(int j = 0; j < size; j++) {
				//a2
				Mapping second = set.get(j);
				//a
				int firstSource = first.getEntity1().getIndex();
				//a'
				int firstTarget = first.getEntity2().getIndex();
				//b
				int secondSource = second.getEntity1().getIndex();
				//b'
				int secondTarget = second.getEntity2().getIndex();
				//order(a,b)
				int sourceOrder = sourceOrderMatrix[firstSource][secondSource];
			
				//order(a',b')
				int targetOrder = targetOrderMatrix[firstTarget][secondTarget];
				//discrepancy, if they have a different order then they have highest discrepancy
				result[i][j] = 0;
				if(sourceOrder != targetOrder) {
					result[i][j] = 1;
				}
				
			}
		}
		
		return result;
	}

	private int[][] calculateOrderMatrix(List<Node> list,
			TreeToDagConverter dag) {
		
		//for each pair of node i need to set if they are >= <= or non ordered
		//the matrix is initially set to non ordered
		// each node is >= of himself and of his descendants
		// then simmetrically a >= b then b <= a so the <= relations are set copying simmetrically the matrix
		tempOrder = new int[list.size()][list.size()]; //to be used only by recursive method
		List<Node> roots = dag.getRoots();
		Iterator<Node> it = roots.iterator();
		while(it.hasNext()) {
			Node n = it.next();
			recursiveOrderSet(n, dag);
		}
		//SET SIMMETRICALLY THE LOWER ORDER
		//a higher than b ---> b higher than a
		for(int i = 0; i < tempOrder.length; i++ ) {
			for(int j = 0; j < tempOrder[i].length; j++) {
				if(i != j && tempOrder[i][j] == HIGHER) {
					tempOrder[j][i] = LOWER;
				}
			}
		}
		int[][] result = tempOrder;
		tempOrder = null;
		return result;
	}

	private void recursiveOrderSet(Node n, TreeToDagConverter dag) {
		//If a node is not an ancestor or a descendants of mine I'm not ordered with him so the matrix value is not going to be set
		//I'm higher of my self and all my descendants
		//I'm lower than my ancestors but this is calculated later simmetrically;
		int myIndex = n.getIndex();
		tempOrder[myIndex][myIndex] = HIGHER;
		if(!n.isLeaf()) {
			List<Node> children = n.getChildren();
			Iterator<Node> it = children.iterator();
			while(it.hasNext()) {
				Node child = it.next();
				int childIndex = child.getIndex();
				//if the child was not processed yet, let's process it.
				//to be processed his own order must be higher
				if(tempOrder[childIndex][childIndex] != HIGHER) {
					recursiveOrderSet(child, dag);
				}
				tempOrder[myIndex][childIndex] = HIGHER; //I'm higher than my children
				//I'm higher to all nodes lower then my children
				for(int j = 0; j < tempOrder[childIndex].length; j++) {
					if(tempOrder[childIndex][j] == HIGHER) {//if my child is higher than node j, this node is higher too
						tempOrder[myIndex][j] = HIGHER;
					}
				}
			}
		}
	}

	/**
	 * Distance preservation that is 1 - distanceDiscrepancy by joslyn
	 * @param set
	 * @param sourceList
	 * @param targetList
	 * @param sourceTree
	 * @param targetTree
	 * @return
	 */
	private double distancePreservation(Alignment<Mapping> set,
			List<Node> sourceList, List<Node> targetList,
			Node sourceTree, Node targetTree) throws Exception {
		
		//SOURCE ONTOLOGY STRUCTURES
		//LOWER DISTANCE: an array num of descendants of each node. sourceDescendants[node.getIndex()] = num of  descendants of node
		//UPPER DISTANCE: an array num of ancestors of each node. sourceDescendants[node.getIndex()] = num of  ancestors of node
		int[] sourceDescendants; 
		//the normalized distance between each pair of nodes
		double[][] sourceDistances;
		
		//TARGET ONTOLOGY STRUCTURES
		int[] targetDescendants;
		double[][] targetDistances;
		
		TreeToDagConverter sourceDag = new TreeToDagConverter(sourceTree);
		TreeToDagConverter targetDag = new TreeToDagConverter(targetTree);
		
		if(params.getBit(PREF_UPPER_DISTANCE)){
			sourceDescendants = createAncestorsArray(sourceList);
			targetDescendants = createAncestorsArray(targetList);
		}
		else{
			sourceDescendants = createDescendantsArray(sourceList);
			targetDescendants = createDescendantsArray(targetList);
		}
		//create the array for target and source with the numver of descendants of each node
		
		
		
		//I need to calculate which is the distance between each pair of node
		if(params.getBit(PREF_UPPER_DISTANCE)){
			sourceDistances = createUCDistances(sourceList, sourceDescendants);
			targetDistances = createUCDistances(targetList, targetDescendants);
		}
		else{
			sourceDistances = createLCDistances(sourceList, sourceDescendants);
			targetDistances = createLCDistances(targetList, targetDescendants);
		}
		//double sourceDiameter = calculateTopBottomDiameter(sourceList, sourceDag);
		//double targetDiameter = calculateTopBottomDiameter(targetList, targetDag);
		sourceDistances = normalizeDistances(sourceList, sourceDistances, sourceDag);
		targetDistances = normalizeDistances(targetList, targetDistances, sourceDag);
		
		//calculate the link distance discrepancy, look at the example on the paper to understand it
		//given two alignments  a1( a, a') & a2(b, b')
		// f(a1, a2) = | d(a,b) - d(a',b') | 
		double[][] linkDistanceDiscrepancies = calculateDistanceDiscrepancies(set, sourceDistances, targetDistances);
		
		//   sumOfDiscrepancies/ binomial(numOfalignments, 2)
		//we have to calculate the average of all discrepancies
		//if we have n alignments, since we have a descrepancy for each pair, it would be:
		// sumOfDescrepancies/ n*n
		//but we don't have to consider the pairs like (a,a) or (b,b). and we have to consider (a,b) but not (b,a)
		//so it will be binomial operation: sumOfDescrepancies / binom(n,2)      binom(n,2)  = n! / 2! (n-2)! = n(n-1) / 2 
		int size = set.size();
	
		double binom = ( size * (size -1) ) / (double) 2;
		
		//calculate the sum
		double sum = Utility.getSumOfHalfMatrix(linkDistanceDiscrepancies);
		double totalDescrepancy = sum / binom;
		
		//the discrepancy is a measure of dissimilarity, between 1 and 0. so the quality should be 1 - totalDescrepancy
		
		double quality = totalDescrepancy;
		if(params.getBit(PREF_USE_PRESERVATION))
			quality = 1 - totalDescrepancy;
	
		return quality;
	}
	
	/** This method is used in the getDetails() method of the Ontology class. */
	public double getDiameter(List<Node> list, TreeToDagConverter dag) {
		//the diameter is now forced to be always N - 1 where N = nodes + top + bottom if they are not already included.
		return calculateTopBottomDiameter(list, dag);
	}
	
	/** FIXME: This method needs to be checked for correctness. */
	private double calculateTopBottomDiameter(List<Node> sourceList, TreeToDagConverter sourceTree) {
		
		double diameter = sourceList.size() - 1;
		if(sourceTree.getRoots().size() != 1)
			diameter+=1 ;
		if(sourceTree.getLeaves().size() != 1)
			diameter +=1 ;
		return diameter;
	}

	private double[][] calculateDistanceDiscrepancies(Alignment<Mapping> set, double[][] sourceDistances, double[][] targetDistances) {
		//calculate the link distance discrepancy, look at the example on the paper to understand it
		//given two alignments  a1( a, a') & a2(b, b')
		// f(a1, a2) = | d(a,b) - d(a',b') | 
		int size = set.size();
		//for each pair of alignments we have a discrepancy value
		double[][] result = new double[size][size];
		
		for(int i= 0; i < size; i++) {
			//a1
			Mapping first = set.get(i);
			for(int j = 0; j < size; j++) {
				//a2
				Mapping second = set.get(j);
				//a
				int firstSource = first.getEntity1().getIndex();
				//a'
				int firstTarget = first.getEntity2().getIndex();
				//b
				int secondSource = second.getEntity1().getIndex();
				//b'
				int secondTarget = second.getEntity2().getIndex();
				//d(a,b)
				double sourceDistance = sourceDistances[firstSource][secondSource];
				//d(a',b')
				double targetDistance = targetDistances[firstTarget][secondTarget];
				//discrepancy = d(a,b) - d(a',b')
				result[i][j] = sourceDistance - targetDistance;
				// |descrepancy|
				if(result[i][j] < 0) {
					result[i][j] *= -1;
				}

				//DEBUG
				/*
				if(first.getEntity1().getLocalName().equalsIgnoreCase("WEAPON")) {
					if(second.getEntity1().getLocalName().equalsIgnoreCase("PROJECTILE-WEAPON")) {
				*/
				/*
						System.out.println("*** "+i+" "+j);
						System.out.println("sources: "+first.getEntity1().getLocalName()+" "+second.getEntity1().getLocalName()+" "+sourceDistance);
						System.out.println("target: "+first.getEntity2().getLocalName()+" "+second.getEntity2().getLocalName()+" "+targetDistance);
						System.out.println("discrepancy: "+result[i][j]);
				*/		
			/*	
			}
				}
				*/
				
			}
		}
		
		return result;
	}

	/**The formula is d(a,b) = numOfDescendants(a) + numOfDescendants(b) - 2 numOfDescendants(maxCommonDescendants(a,b))
	 * then it has to be normalized dividing it for the diameter that is the max distance
	 * The max commonDescendats is the commonDescendants between a and b, which has highest num of descendants itself
	 * The highest commonDescendants are all at the same level in the hierarchy. I was thinking of using the total num of common descendants but is not exatly the same.
	 */
	private double[][] createLCDistances(List<Node> nodesList, int[] descendants) {
		double[][] distances = new double[nodesList.size()][nodesList.size()];
		
		for(int i = 0; i < nodesList.size(); i++) {
			for(int j = 0; j < nodesList.size(); j++) {
				
				int descendantsA = descendants[i];
				int descendantsB = descendants[j];
				int maxCommonDescendants = 0;
				List<Node> commonDescendants = NodeUtility.getCommonDescendants(nodesList.get(i), nodesList.get(j));
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
				distances[i][j] = (double)(descendantsA + descendantsB - ( 2 * maxCommonDescendants));
				//if(nodesList.get(i).getLocalName().equalsIgnoreCase("school"))
				//System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" dist: "+distances[i][j]+" descA: "+descendantsA+" B: "+descendantsB+" AB: "+maxCommonDescendants);
			}
		}
		
		/*Moved into its own method normalizeDistances
		//the diameter is the max distance
		double diameter = Utility.getMaxOfMatrix(distances);
		//System.out.println("diameter: "+diameter);
		
		//normalize distances
		for(int i = 0; i < nodesList.size(); i++) {
			for(int j = 0; j < nodesList.size(); j++) {
				distances[i][j] /= diameter;
				//System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" "+distances[i][j]);
			}
		}
		*/
		return distances;
	}
	
	/**The formula is d(a,b) = numOfAncestors(a) + numOfancestors(b) - 2 numOfAncestors(minCommonAncestors(a,b))
	 * then it has to be normalized dividing it for the diameter that is the max distance
	 * The max commonAncestors is the commonAncestors between a and b, which has highest num of ancestors
	 * The possible highest commonAncestors are all at the same level in the hierarchy, that is the lowest level of the ancestors.
	 */
	private double[][] createUCDistances(List<Node> nodesList, int[] ancestors) {
		double[][] distances = new double[nodesList.size()][nodesList.size()];
		
		for(int i = 0; i < nodesList.size(); i++) {
			for(int j = 0; j < nodesList.size(); j++) {
				
				int ancestorsA = ancestors[i];
				int ancestorsB = ancestors[j];
				int maxCommonAncestors = 0;
				List<Node> commonAncestors = NodeUtility.getCommonAncestors(nodesList.get(i), nodesList.get(j));
				if(commonAncestors.size() > 0) {
					//I need to find which is the common with highest num of anc
					//so it can only be one of the common anc in the lowest level
					//since the commonAnc list is ordered, i just need stop scanning when i finish to scan the lowest level that is also the first
					//remember that the level goes from 0, the root is 0 for example, so the highest level is the lowest number in real
					int lowestLevel = commonAncestors.get(0).getLevel();
					for(int k = 0; k < commonAncestors.size(); k++) {
						Node common = commonAncestors.get(k);
						if(common.getLevel() != lowestLevel) {
							//i've finished the highest level so it's useless to look in the lower descendants
							break;
						}
						else {
							//find the max
							int toBeCompared = ancestors[common.getIndex()];
							if(maxCommonAncestors < toBeCompared) {
								maxCommonAncestors = toBeCompared;
							}
						}
					}
				}
				
				//finally the distance formula
				distances[i][j] = (double)(ancestorsA + ancestorsB - ( 2 * maxCommonAncestors));
				//System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" "+distances[i][j]);
			}
		}
		
		/*Moved into its own method normalizeDistances
		//the diameter is the max distance
		double diameter = Utility.getMaxOfMatrix(distances);
		System.out.println("diameter: "+diameter);
		
		//normalize distances
		for(int i = 0; i < nodesList.size(); i++) {
			for(int j = 0; j < nodesList.size(); j++) {
				distances[i][j] /= diameter;
				//System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" "+distances[i][j]);
			}
		}
		*/
		return distances;
	}
	
	private double[][] normalizeDistances(List<Node> nodesList, double[][] dist, TreeToDagConverter dag) {
		double[][] distances = new double[nodesList.size()][nodesList.size()];
		//the diameter is the max distance, but we consider that there is always a top and bottom node, so the distance is always distance(top,bpttom)
		double diameter = calculateTopBottomDiameter(nodesList, dag); 
		//double diameter = Utility.getMaxOfMatrix(dist);
		//System.out.println("diameter: "+diameter);
		if(diameter != 0) {
			//normalize distances
			for(int i = 0; i < nodesList.size(); i++) {
				for(int j = 0; j < nodesList.size(); j++) {
					distances[i][j] = dist[i][j] / diameter;
					//System.out.println(nodesList.get(i).getLocalName()+" "+nodesList.get(j).getLocalName()+" "+distances[i][j]);
				}
			}
		}
		return distances;
	}
	
	/**
	 * Create the array containing the num of descendants of each node. In order to do that it invokes a recursive method
	 * @param sourceList
	 * @param sourceTree
	 * @return
	 */
	private int[] createDescendantsArray(List<Node> sourceList) {
		//LOWER DISTANCE: for each node we will have the num of descendants
		int[] result = new int[sourceList.size()]; //to be used only by recursive descendants
		
		for( int i = 0; i < sourceList.size(); i++ ) {
			Node sourceNode = sourceList.get(i);
			Set<Node> currentNodeDescentants = sourceNode.getDescendants();
			result[i] = currentNodeDescentants.size();
		}
		
		return result;
	}
	
	/**
	 * Create the array containing the num of ancestors of each node. In order to do that it invokes a recursive method
	 * @param sourceList
	 * @param sourceTree
	 * @return
	 */
	private int[] createAncestorsArray(List<Node> sourceList) {
		//UPPER DISTANCE: for each node we will have the num of ancestors
		int[] result = new int[sourceList.size()]; //to be used only by recursive method
		
		for( int i = 0; i < sourceList.size(); i++ ) {
			Node sourceNode = sourceList.get(i);
			Set<Node> currentNodeAncestors = sourceNode.getAncestors();
			result[i] = currentNodeAncestors.size();
		}
		
		return result;
	}
	
}
