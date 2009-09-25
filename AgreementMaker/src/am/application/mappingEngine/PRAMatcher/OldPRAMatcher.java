package am.application.mappingEngine.PRAMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AlignmentMatrix;
import am.application.mappingEngine.AbstractMatcher.alignType;
import am.application.mappingEngine.StringUtil.Normalizer;
import am.application.mappingEngine.StringUtil.NormalizerParameter;
import am.application.ontology.Node;

public class OldPRAMatcher extends AbstractMatcher 
{
	// the Alignment Matrices from the Input Matching algorithm.
	private AlignmentMatrix inputClassesMatrix = null;
	private AlignmentMatrix inputPropertiesMatrix = null;
	private AlignmentMatrix matrix = null;
	
	//the structure that holds the roots of subtrees which are matched nodes in the ontology
	private ArrayList<OldTreeNode> matchedClassRootNodes = null;
	private ArrayList<OldTreeNode> matchedPropertyRootNodes = null;
	
	//private HashMap<OldTreeNode, ArrayList<OldTreeNode>> adjacency;
	private HashMap<Node, OldTreeNode> nodeToOldTreeNode;
	
	//Constructor
	public OldPRAMatcher()
	{
		super();
		minInputMatchers = 1;
		maxInputMatchers = 1;
	}
	
	
	
	protected void beforeAlignOperations()throws Exception 
	{
		super.beforeAlignOperations();
    	if( inputMatchers.size() != 1 ) 
    	{
    		throw new RuntimeException("PRA Algorithm needs to have one input matcher.");
    	}
    	//This allows us to load the input matcher for the PRA Matcher.
    	AbstractMatcher input = inputMatchers.get(0);
    	
    	inputClassesMatrix = input.getClassesMatrix();
    	inputPropertiesMatrix = input.getPropertiesMatrix();  	   	
	}	
	
	
	protected AlignmentMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception 
    {
		nodeToOldTreeNode = new HashMap<Node, OldTreeNode>();
		ArrayList<OldTreeNode> srcOldTreeNodes = createOldTreeNode(sourceList);
		ArrayList<OldTreeNode> targetOldTreeNodes = createOldTreeNode(targetList);
		Node src = null, target = null;
		/*First set matched nodes as matched, to help identifying them as roots when traversing the ontology tree
		 * Also, set the nodes to which they are matched in the other ontology. This is for easy access to such nodes
		 * when traversing the ontology tree, in search of the subtrees with matched nodes as the roots. 
		 */
		
		if(typeOfNodes == alignType.aligningClasses)
		{
			setMatchingPairs(inputClassesMatrix, srcOldTreeNodes, targetOldTreeNodes);
		}
		else if(typeOfNodes == alignType.aligningProperties)
		{
			setMatchingPairs(inputPropertiesMatrix, srcOldTreeNodes, targetOldTreeNodes);
		}
		
		createAdjacency(srcOldTreeNodes);
		
		/*
		OldTreeNode aNode = null;
		for(int i = 0; i < srcOldTreeNodes.size(); i++)
		{
			aNode = srcOldTreeNodes.get(i);
			if(aNode.getChildren() != null)
				System.out.println(aNode.getNode().getLocalName() +" has numchildren: " +aNode.getChildren().size());
			//if(adjacency.get(aNode) != null)
				//System.out.println(aNode.getNode().getLocalName() +" has numchildren: " +adjacency.get(aNode).size());
			//aNode.children = createAdjacency(aNode);
		}*/
		
		createAdjacency(targetOldTreeNodes);		
		//printAdjacency();
		/*
		for(int i = 0; i < targetOldTreeNodes.size(); i++)
		{
			aNode = targetOldTreeNodes.get(i);
			if(aNode.getChildren() != null)
				System.out.println(aNode.getNode().getLocalName() +" has numchildren: " +aNode.getChildren().size());
			//if(adjacency.get(aNode) != null)
				//System.out.println(aNode.getNode().getLocalName() +" has numchildren: " +adjacency.get(aNode).size());
			//aNode.children = createAdjacency(aNode);
		}
		*/
		printAdjacency(srcOldTreeNodes);
		createPRATrees(srcOldTreeNodes, targetOldTreeNodes, typeOfNodes);
		printAdjacency(srcOldTreeNodes);
		
		//Now we align nodes by considering only nodes in the subtrees of matched nodes
		//Initialize matrix before aligning nodes, cos this method will access matrix
		matrix = new AlignmentMatrix(sourceList.size(), targetList.size(), typeOfNodes);
		alignNodes(typeOfNodes);
		
		
		//Now fill the empty spots in the matrix with alignments just to be compatible with current version of AM
		for(int i = 0; i < sourceList.size(); i++)
		{
			src = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++)
			{
				target = targetList.get(j);
				if(matrix.get(i, j) == null)
					matrix.set(i, j, new Alignment(src, target, 0.0d, Alignment.EQUIVALENCE));
			}
		}
		
		return matrix;
		
    }
	
	
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) 
	{
		NormalizerParameter param = null;
		Normalizer norm = null;
		
		String sLabel = source.getLabel();
		String tLabel = target.getLabel();
		
			if(sLabel.equalsIgnoreCase(tLabel))
				return new Alignment( source, target, 1, Alignment.EQUIVALENCE);
			//all normalization without stemming and digits return 0.95
			
			param = new NormalizerParameter();
			param.setAllTrue();
			param.normalizeDigit = false;
			param.stem = false;
			
			norm = new Normalizer(param);
			
			String sProcessedLabel = norm.normalize(sLabel);
			String tProcessedLabel = norm.normalize(tLabel);
			
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.95d, Alignment.EQUIVALENCE);
			//apply stem return 0.90 
	
			param.setAllfalse();
			param.stem = true;
			
			norm = new Normalizer(param);
			sProcessedLabel = norm.normalize(sLabel);
			tProcessedLabel = norm.normalize(tLabel);
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.9d, Alignment.EQUIVALENCE);
			
			//apply normDigits return 0.8
			
			param.setAllfalse();
			param.normalizeDigit = true;
			norm = new Normalizer(param);
			sProcessedLabel = norm.normalize(sLabel);
			tProcessedLabel = norm.normalize(tLabel);
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.8d, Alignment.EQUIVALENCE);
		
			return new Alignment(source, target, 0.0d, Alignment.EQUIVALENCE);
	}

	
	private ArrayList<OldTreeNode> createOldTreeNode(ArrayList<Node> listOfNodes) 
	{
		ArrayList<OldTreeNode> OldTreeNodes = new ArrayList<OldTreeNode>();
		OldTreeNode aOldTreeNode = null, oldTreeNodeInMap = null;
		Node aNode = null;
	
		for(int i = 0; i < listOfNodes.size(); i++)
		{
			aNode = listOfNodes.get(i);
			aOldTreeNode = new OldTreeNode(aNode);
			oldTreeNodeInMap = nodeToOldTreeNode.put(aNode, aOldTreeNode); //recall that there are source and target lists
			if(oldTreeNodeInMap != aOldTreeNode)
				System.out.println("Incorrect HashMap entry!");
			OldTreeNodes.add(aOldTreeNode);
		}
		return OldTreeNodes;
	}
	
	private void setMatchingPairs(AlignmentMatrix inputMatrix, ArrayList<OldTreeNode> sourceList, ArrayList<OldTreeNode> targetList) throws Exception
	{
		Alignment alignment = null;
		int numRows = sourceList.size();
		int numCols = targetList.size();
		OldTreeNode src = null, target = null;
		//int numSet = 0;
		
		for(int i = 0; i <  numRows; i++)
		{
			src = sourceList.get(i);
			for (int j = 0; j < numCols; j++)
			{
				target = targetList.get(j);
				if(inputMatrix.get(src.getNode().getIndex(), target.getNode().getIndex()) != null)
				{
					alignment = inputMatrix.get(src.getNode().getIndex(), target.getNode().getIndex());
					//System.out.println("Found a match on " + src.getNode().getLocalName() +" with similarity "+alignment.getSimilarity());
					if(alignment.getSimilarity() != 0.0d)
					{
						//System.out.println(src.getNode().getLocalName() +" is matched to " + target.getNode().getLocalName());
						src.setMatched(true);
						target.setMatched(true);
						src.setMatchedTo(target);
						target.setMatchedTo(src);
						//numSet++;
					}
				}
			}
		}
		
		//System.out.println("The number of matched pairs is "+numSet);
	}
	
	/*
	private void printAdjacency()
	{
		Set<Map.Entry<OldTreeNode, ArrayList<OldTreeNode>>> entrySet = adjacency.entrySet();
		Map.Entry<OldTreeNode, ArrayList<OldTreeNode>> anEntry;
		
		for(Iterator<Map.Entry<OldTreeNode, ArrayList<OldTreeNode>>> it = entrySet.iterator(); it.hasNext();)
		{
			anEntry = it.next();
			System.out.println(anEntry.getKey().getNode().getLocalName()+" has number of children " + anEntry.getValue().size());
		}	
	}
	*/
	
	private void printAdjacency(ArrayList<OldTreeNode> OldTreeNodes)
	{
		OldTreeNode aNode, nNode;
		ArrayList<OldTreeNode> neighbours;
		
		for(int i = 0; i < OldTreeNodes.size(); i++)
		{
			aNode = OldTreeNodes.get(i);
			neighbours = aNode.getChildren();
			if(neighbours != null)
			{
				for(int j = 0; j < neighbours.size(); j++)
				{
					nNode = neighbours.get(j);
					System.out.println("Node " +aNode.getNode().getLocalName() +" has neighbour " + nNode.getNode().getLocalName());
					//System.out.println("Node " +aNode +" has neighbour " + nNode);
		
				}
				System.out.println();
			}
		}
	}
	
	private void createPRATrees(ArrayList<OldTreeNode> srcOldTreeNodes, ArrayList<OldTreeNode> targetOldTreeNodes, alignType typeOfNodes) throws Exception
	{
		//Need to access the inputMatrices to find which nodes are matched
		//Start with inputClassesMatrix
		ArrayList<OldTreeNode> srcRootNodes = getRootNodes(srcOldTreeNodes);
		//System.out.println("The size of the srcRootNodes is "+srcRootNodes.size());
		
		ArrayList<OldTreeNode> targetRootNodes = getRootNodes(targetOldTreeNodes);
		//System.out.println("The size of the targetRootNodes is "+targetRootNodes.size());
		
		//initialize class and property root nodes
		matchedClassRootNodes = new ArrayList<OldTreeNode>();
		matchedPropertyRootNodes = new ArrayList<OldTreeNode>();
		createPRATrees(srcRootNodes, typeOfNodes);
		//createPRATrees(targetRootNodes, typeOfNodes);
		
	}
	
	
	private void createPRATrees(ArrayList<OldTreeNode> rootNodes, alignType typeOfNodes)
	{
		OldTreeNode aRootNode = null;
		
		for(int i = 0; i < rootNodes.size(); i++)
		{
			aRootNode = rootNodes.get(i);
			//System.out.println("Matching for node " +aRootNode.getNode().getLocalName()+" is " +aRootNode.isMatched());
			createPRATrees(aRootNode, typeOfNodes);
			//Now include all root nodes in the matched list, whether matched or not. This is to be able to traverse the subtree rooted at them.
			if(aRootNode.isMatched())
			{
				//System.out.println("Node "+ aRootNode.getNode().getLocalName() +" is matched ");
				if(typeOfNodes == alignType.aligningClasses)
				{
					System.out.println("Adding a root node "+ aRootNode.getNode().getLocalName()+" to matchedClassRootNodes");
					matchedClassRootNodes.add(aRootNode);
				}
				else if(typeOfNodes == alignType.aligningProperties)
				{
					//System.out.println("Adding a root node to matchedPropertyRootNodes");
					matchedPropertyRootNodes.add(aRootNode);
				}
			}
		}
		
		System.out.println("The size of class root nodes is "+matchedClassRootNodes.size());
		System.out.println("The size of property root nodes is "+matchedPropertyRootNodes.size());
	}
	
	
	private void createPRATrees(OldTreeNode aNode, alignType typeOfNodes)
	{
		ArrayList<OldTreeNode> myChildren = aNode.getChildren();
		//ArrayList<OldTreeNode> myChildren = adjacency.get(aNode);
		OldTreeNode myChild = null;
		
		aNode.setColor(1);
		
		if(myChildren != null)
		{
			System.out.println("Before recursing, node " + aNode.getNode().getLocalName()+ " has " + myChildren.size() +" children");
			for(int i = 0; i < myChildren.size(); i++)
			{
				myChild = myChildren.get(i);
				if(myChild.getColor() == 0)
				{
					myChild.setParent(aNode);
					createPRATrees(myChild, typeOfNodes);
				}
			}
		}
		else
			System.out.println("Before recursing, node " +aNode.getNode().getLocalName() +" has no children");
		
		aNode.setColor(2);
		//Now go through children nodes and remove those that are matched, to leave only those that are not matched in the ontology
		if(myChildren != null)
		{
			System.out.println("After recursing, node " + aNode.getNode().getLocalName()+ " has some children of size " + myChildren.size());
			for(int i = 0; i < myChildren.size(); i++)
			{
				myChild = myChildren.get(i);
				System.out.println("Examining child node "+ myChild.getNode().getLocalName()+" for a match ");
				if(myChild.isMatched())
				{
					System.out.println("Node "+ myChild.getNode().getLocalName() +" is matched ");
					if(typeOfNodes == alignType.aligningClasses)
					{
						System.out.println("Adding a node "+ myChild.getNode().getLocalName() +" to matchedClassRootNodes");
						matchedClassRootNodes.add(myChild);
					}
					else if(typeOfNodes == alignType.aligningProperties)
					{
						//System.out.println("Adding a node to matchedPropertyRootNodes");
						matchedPropertyRootNodes.add(myChild);
					}
					myChildren.remove(myChild);
				}
			}
		}
		else
			System.out.println("After recursing, node " +aNode.getNode().getLocalName() +" has no children");
	}
	
	private void alignNodes(alignType typeOfNodes)
	{
		//Now go through the matched subtrees and align nodes in it.
		//Remember that nodes in src or target rootnodes may not be matched
		//but may yet still need to be matched
		OldTreeNode sourceNode = null, targetNode = null;
		ArrayList<OldTreeNode> matchedRootNodes = null;
		
		if(typeOfNodes == alignType.aligningClasses)
		{
			//System.out.println("Aligning classes");
			matchedRootNodes = matchedClassRootNodes;
		}
		else
		{
			//System.out.println("Aligning properties");
			matchedRootNodes = matchedPropertyRootNodes;
		}
		
		if(matchedRootNodes != null)
		for(int i = 0; i < matchedRootNodes.size(); i++)
		{
			sourceNode = matchedRootNodes.get(i);
			targetNode = sourceNode.getMatchedTo();
			sourceNode.resetNodeColors();
			//resetColors(sourceNode);
			alignNodes(sourceNode, targetNode, typeOfNodes);			
		}
	}
	
	/*
	private void resetColors(OldTreeNode aNode)
	{
		aNode.setColor(0);
		ArrayList<OldTreeNode> anAdj = adjacency.get(aNode);
		OldTreeNode aChild;
		
		if(anAdj != null)
		{
			for(int i = 0; i < anAdj.size(); i++)
			{
				aChild = anAdj.get(i);
				resetColors(aChild);
			}
		}
	}
	*/
	
	private void alignNodes(OldTreeNode aNode, OldTreeNode targetNode, alignType typeOfNodes)
	{
		ArrayList<OldTreeNode> myChildren = aNode.getChildren();
		//ArrayList<OldTreeNode> myChildren = adjacency.get(aNode);
		OldTreeNode childNode = null;
				
		aNode.setColor(1);
		if(myChildren != null)
		{
			for(int i = 0; i < myChildren.size(); i++)
			{
				childNode = myChildren.get(i);
				if(childNode.getColor() == 0)
				{
					childNode.setColor(1);
					alignNodes(childNode, targetNode, typeOfNodes);
				}
			}
		}
		
		aNode.setColor(2);
		targetNode.resetNodeColors();
		//resetColors(targetNode);
		alignNodeWithTargets(aNode, targetNode, typeOfNodes);
		
	}
	
	private void alignNodeWithTargets(OldTreeNode srcNode, OldTreeNode targetNode, alignType typeOfNodes)
	{
		ArrayList<OldTreeNode> myChildren = targetNode.getChildren();
		//ArrayList<OldTreeNode> myChildren = adjacency.get(targetNode);
		OldTreeNode childNode = null;
		Alignment alignment = null;
		
		targetNode.setColor(1);
		if(myChildren != null)
		{
			for(int i = 0; i < myChildren.size(); i++)
			{
				childNode = myChildren.get(i);
				if(childNode.getColor() == 0)
				{
					alignNodeWithTargets(srcNode, childNode, typeOfNodes);
				}
			}	
		}
		
		targetNode.setColor(2);
		//Now align the source and targetNodes
		alignment = alignTwoNodes(srcNode.getNode(), targetNode.getNode(), typeOfNodes);
		matrix.set(srcNode.getNode().getIndex(), targetNode.getNode().getIndex(), alignment);
	}
	
	private ArrayList<OldTreeNode> createAdjacency(OldTreeNode OldTreeNode)
	{
		Node aNode = null;
		OldTreeNode childNode = null;
		ArrayList<OldTreeNode> adjacentNodes = null;
		ArrayList<Node> childrenNodes = null;
		
		aNode = OldTreeNode.getNode();
		childrenNodes = aNode.getChildren();
		if(childrenNodes != null && childrenNodes.size() > 0)
		{
			adjacentNodes = new ArrayList<OldTreeNode>();
			for(int j = 0; j < childrenNodes.size(); j++)
			{
				//System.out.println("Adding "+ childrenNodes.get(j).getLocalName()+ " as a child of " + aNode.getLocalName());
				childNode = nodeToOldTreeNode.get(aNode);
				adjacentNodes.add(childNode);
			}
			//System.out.println(aNode.getLocalName() +" has numchildren " + adjacentNodes.size());
		}
		
		return adjacentNodes;
	}
	
	
	private void createAdjacency(ArrayList<OldTreeNode> OldTreeNodes)
	{
		Node aNode, cNode;
		OldTreeNode parentNode, childNode;
		ArrayList<Node> childrenNodes;
		//adjacency = new HashMap<OldTreeNode, ArrayList<OldTreeNode>>();
		//ArrayList<OldTreeNode> anAdj;
		
		
		
		for(int i = 0; i < OldTreeNodes.size(); i++)
		{
			parentNode = OldTreeNodes.get(i);
			aNode = parentNode.getNode();
			childrenNodes = aNode.getChildren();
			if(childrenNodes != null && childrenNodes.size() > 0)
			{
				parentNode.children = new ArrayList<OldTreeNode>();
				//anAdj = new ArrayList<OldTreeNode>();
				for(int j = 0; j < childrenNodes.size(); j++)
				{					
					cNode = childrenNodes.get(j);
					//childNode = new OldTreeNode(cNode);
					childNode = nodeToOldTreeNode.get(cNode);
					//anAdj.add(childNode);
					parentNode.children.add(childNode);
					System.out.println("Adding "+ cNode.getLocalName()+ " as a child of " + aNode.getLocalName());
					//System.out.println("Adding "+ childNode+ " as a child of " + parentNode);
				}
				//adjacency.put(parentNode, anAdj);
				System.out.println();
				//System.out.println(aNode.getLocalName() +" has numchildren " + parentNode.children.size());
			}
		}
	}
	
	private ArrayList<OldTreeNode> getRootNodes(ArrayList<OldTreeNode> aList)
	{
		ArrayList<OldTreeNode> rootNodes = new ArrayList<OldTreeNode>();
		OldTreeNode aNode = null;
		
		for(int i = 0; i < aList.size(); i++)
		{
			aNode = aList.get(i);
			
			//System.out.println("Examining node "+ aNode.getNode().getLocalName());
			if(aNode.getNode().getParents() == null || aNode.getNode().getParents().size() == 0)
			{
				rootNodes.add(aNode);
				//System.out.println("Found a root node " + aNode.getNode().getLocalName());
			}
			
		}
		//System.out.println("Num root nodes found is " +rootNodes.size());
		return rootNodes;
	}


		
	/**
	 * @param inputClassesMatrix the inputClassesMatrix to set
	 */
	public void setInputClassesMatrix(AlignmentMatrix inputClassesMatrix) 
	{
		this.inputClassesMatrix = inputClassesMatrix;
	}


	/**
	 * @return the inputClassesMatrix
	 */
	public AlignmentMatrix getInputClassesMatrix() 
	{
		return inputClassesMatrix;
	}

	/**
	 * @param inputPropertiesMatrix the inputPropertiesMatrix to set
	 */
	public void setInputPropertiesMatrix(AlignmentMatrix inputPropertiesMatrix) 
	{
		this.inputPropertiesMatrix = inputPropertiesMatrix;
	}

	/**
	 * @return the inputPropertiesMatrix
	 */
	public AlignmentMatrix getInputPropertiesMatrix() 
	{
		return inputPropertiesMatrix;
	}


	/**
	 * @param matchedClassRootNodes the matchedClassRootNodes to set
	 */
	public void setMatchedClassRootNodes(ArrayList<OldTreeNode> matchedClassRootNodes) 
	{
		this.matchedClassRootNodes = matchedClassRootNodes;
	}

	/**
	 * @return the matchedClassRootNodes
	 */
	public ArrayList<OldTreeNode> getMatchedClassRootNodes() 
	{
		return matchedClassRootNodes;
	}

	/**
	 * @param matchedPropertyRootNodes the matchedPropertyRootNodes to set
	 */
	public void setMatchedPropertyRootNodes(ArrayList<OldTreeNode> matchedPropertyRootNodes) 
	{
		this.matchedPropertyRootNodes = matchedPropertyRootNodes;
	}

	/**
	 * @return the matchedPropertyRootNodes
	 */
	public ArrayList<OldTreeNode> getMatchedPropertyRootNodes() 
	{
		return matchedPropertyRootNodes;
	}
	
}
