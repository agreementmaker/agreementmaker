package am.matcher.pra.PRAMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

public class PRAMatcher extends am.matcher.bsm.BaseSimilarityMatcher 
{
	private static final long serialVersionUID = 8241040990308110584L;

	// the Alignment Matrices from the Input Matching algorithm.
	private SimilarityMatrix inputClassesMatrix;
	private SimilarityMatrix inputPropertiesMatrix;
	private Alignment<Mapping> inputClassesAlignmentSet;
	private Alignment<Mapping> inputPropertiesAlignmentSet;
	private HashMap<Node, TreeNode> nodeToTreeNode;
	private HashMap<Integer, Node> srcClassesIdToNode;
	private HashMap<Integer, Node> targetClassesIdToNode;
	private HashMap<Integer, Node> srcPropertiesIdToNode;
	private HashMap<Integer, Node> targetPropertiesIdToNode;
	private SimilarityMatrix matrix;
	
	//the structure that holds the roots of subtrees which are matched nodes in the ontology
	private ArrayList<TreeNode> matchedClassSourceRootNodes;
	private ArrayList<TreeNode> matchedPropertySourceRootNodes;
	private ArrayList<TreeNode> unMatchedClassSourceRootNodes;
	private ArrayList<TreeNode> unMatchedPropertySourceRootNodes;
	private ArrayList<TreeNode> matchedClassTargetRootNodes;
	private ArrayList<TreeNode> matchedPropertyTargetRootNodes;
	private ArrayList<TreeNode> unMatchedClassTargetRootNodes;
	private ArrayList<TreeNode> unMatchedPropertyTargetRootNodes;
	
	//Constructor
	public PRAMatcher()
	{
		super();
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		setName("PRA Matcher");
	}
	

	@Override
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
    	inputClassesAlignmentSet = input.getClassAlignmentSet();
    	inputPropertiesAlignmentSet = input.getPropertyAlignmentSet();
    	nodeToTreeNode = new HashMap<Node, TreeNode>();
	}	
	
	@Override
	protected SimilarityMatrix alignNodesOneByOne(List<Node> sourceList, List<Node> targetList, alignType typeOfNodes) throws Exception 
    {
		List<TreeNode> srcTreeNodes = createTreeNode(sourceList);
		List<TreeNode> targetTreeNodes = createTreeNode(targetList);
		Node src = null, target = null;
		//First set matched nodes as matched, to help identifying them as roots when traversing the ontology tree
		//Also, set the nodes to which they are matched in the other ontology. This is for easy access to such nodes
		//when traversing the ontology tree, in search of the subtrees with matched nodes as the roots. 
		
		
		if(typeOfNodes == alignType.aligningClasses)
		{
			setMatchingPairs(inputClassesMatrix, srcTreeNodes, targetTreeNodes);
			srcClassesIdToNode = new HashMap<Integer, Node>();
			targetClassesIdToNode = new HashMap<Integer, Node>();
			for(int i = 0; i < sourceList.size(); i++)
			{
				src = sourceList.get(i);
				srcClassesIdToNode.put(new Integer(src.getIndex()), src);
			}
			for(int i = 0; i < targetList.size(); i++)
			{
				target = targetList.get(i);
				targetClassesIdToNode.put(new Integer(target.getIndex()), target);				
			}
		}
		else if(typeOfNodes == alignType.aligningProperties)
		{
			setMatchingPairs(inputPropertiesMatrix, srcTreeNodes, targetTreeNodes);
			srcPropertiesIdToNode = new HashMap<Integer, Node>();
			targetPropertiesIdToNode = new HashMap<Integer, Node>();
			for(int i = 0; i < sourceList.size(); i++)
			{
				src = sourceList.get(i);
				srcPropertiesIdToNode.put(new Integer(src.getIndex()), src);
			}
			for(int i = 0; i < targetList.size(); i++)
			{
				target = targetList.get(i);
				targetPropertiesIdToNode.put(new Integer(target.getIndex()), target);				
			}
		}

		createAdjacency(srcTreeNodes);
		createAdjacency(targetTreeNodes);
		createPRATrees(srcTreeNodes, targetTreeNodes, typeOfNodes);

		//Now we align nodes by considering only nodes in the subtrees of matched nodes
		//Initialize matrix before aligning nodes, cos this method will access matrix
		matrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, typeOfNodes);
		alignNodes(typeOfNodes);
		
		
		//Now fill the empty spots in the matrix with alignments just to be compatible with current version of AM
		for(int i = 0; i < sourceList.size(); i++)
		{
			src = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++)
			{
				target = targetList.get(j);
				if(matrix.get(i, j) == null)
					matrix.set(i, j, new Mapping(src, target, 0.0d, MappingRelation.EQUIVALENCE));
			}
		}
		
		return matrix;
		
    }
	
	/*
	//Set all alignment sim to a random value between 0 and 1
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) 
	{
		NormalizerParameter param = null;
		Normalizer norm = null;
		
				
		String sLocalname = source.getLocalName();
		String tLocalname = target.getLocalName();
		//System.out.println("The source and target localNames are source: "+sLocalname+" target: "+ tLocalname);
		if(sLocalname.equalsIgnoreCase(tLocalname))
			return new Alignment( source, target, 1d, Alignment.EQUIVALENCE);
		//all normalization without stemming and digits return 0.95
		param = new NormalizerParameter();
		param.setAllTrue();
		param.normalizeDigit = false;
		param.stem = false;
		norm = new Normalizer(param);
		String sProcessedLocalnames = norm.normalize(sLocalname);
		String tProcessedLocalnames = norm.normalize(tLocalname);
		if(sProcessedLocalnames.equals(tProcessedLocalnames))
			return new Alignment( source, target, 0.95d, Alignment.EQUIVALENCE);
		//all normalization without digits return 0.90
		param = new NormalizerParameter();
		param.setAllfalse();
		param.stem = true;
		norm = new Normalizer(param);
		sProcessedLocalnames = norm.normalize(sLocalname);
		tProcessedLocalnames = norm.normalize(tLocalname);
		if(sProcessedLocalnames.equals(tProcessedLocalnames))
			return new Alignment( source, target, 0.9d, Alignment.EQUIVALENCE);
		//all normalization return 0.8
		param = new NormalizerParameter();
		param.setAllfalse();
		param.normalizeDigit = true;
		sProcessedLocalnames = norm.normalize(sLocalname);
		tProcessedLocalnames = norm.normalize(tLocalname);
		if(sProcessedLocalnames.equals(tProcessedLocalnames))
			return new Alignment( source, target, 0.8d, Alignment.EQUIVALENCE);
		
		
		

		String sLabel = source.getLabel();
		String tLabel = target.getLabel();
		//System.out.println("The source and target labels are source: "+sLabel+" target: "+ tLabel);
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
	*/
	
	
	private List<TreeNode> createTreeNode(List<Node> listOfNodes) 
	{
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		TreeNode aTreeNode = null;
		Node aNode = null;
	
		for(int i = 0; i < listOfNodes.size(); i++)
		{
			aNode = listOfNodes.get(i);
			aTreeNode = new TreeNode(aNode);
			nodeToTreeNode.put(aNode, aTreeNode); //recall that there are source and target lists
			treeNodes.add(aTreeNode);
		}
		return treeNodes;
	}
	
	private void setMatchingPairs(SimilarityMatrix inputMatrix, List<TreeNode> sourceList, List<TreeNode> targetList) throws Exception
	{
		Mapping alignment = null;
		int numRows = sourceList.size();
		int numCols = targetList.size();
		TreeNode src = null, target = null;
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
	

	
	/*private void printAdjacency(ArrayList<TreeNode> nodes)
	{
		TreeNode aNode;
		TreeNode nNode;
		ArrayList<TreeNode> neighbours;
		
		for(int i = 0; i < nodes.size(); i++)
		{
			aNode = nodes.get(i);
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
	}*/
	
	
	private void createPRATrees(List<TreeNode> sourceList, List<TreeNode> targetList, alignType typeOfNodes) throws Exception
	{
		//Need to access the inputMatrices to find which nodes are matched
		//Start with inputClassesMatrix
		List<TreeNode> srcRootNodes = getRootNodes(sourceList);
		//System.out.println("The size of the srcRootNodes is "+srcRootNodes.size());
		
		List<TreeNode> targetRootNodes = getRootNodes(targetList);
		//System.out.println("The size of the targetRootNodes is "+targetRootNodes.size());
		
		//initialize class and property root nodes
		matchedClassSourceRootNodes = new ArrayList<TreeNode>();
		matchedPropertySourceRootNodes = new ArrayList<TreeNode>();
		unMatchedClassSourceRootNodes = new ArrayList<TreeNode>();
		unMatchedPropertySourceRootNodes = new ArrayList<TreeNode>();
		matchedClassTargetRootNodes = new ArrayList<TreeNode>();
		matchedPropertyTargetRootNodes = new ArrayList<TreeNode>();
		unMatchedClassTargetRootNodes = new ArrayList<TreeNode>();
		unMatchedPropertyTargetRootNodes = new ArrayList<TreeNode>();
		createPRATrees(srcRootNodes, typeOfNodes, true);
		createPRATrees(targetRootNodes, typeOfNodes, false);
		
	}
	
	
	private void createPRATrees(List<TreeNode> rootNodes, alignType typeOfNodes, boolean source)
	{
		TreeNode aRootNode = null;
		int treeDepth = 0;
		
		for(int i = 0; i < rootNodes.size(); i++)
		{
			aRootNode = rootNodes.get(i);
			createPRATrees(aRootNode, typeOfNodes, source, treeDepth);
			//Now include all root nodes in the matched list, whether matched or not. This is to be able to traverse the subtree rooted at them.
			if(aRootNode.isMatched())
			{
				//System.out.println("Node "+ aRootNode.getNode().getLocalName() +" is matched ");
				if(typeOfNodes == alignType.aligningClasses)
				{
					if(source == true)
					{
						//System.out.println("Adding a root node "+ aRootNode.getLocalName()+" to matchedClassSourceRootNodes");
						matchedClassSourceRootNodes.add(aRootNode);
					}
					else
					{
						//System.out.println("Adding a root node "+ aRootNode.getLocalName()+" to matchedClassTargetRootNodes");
						matchedClassTargetRootNodes.add(aRootNode);
					}
				}
				else if(typeOfNodes == alignType.aligningProperties)
				{
					if(source == true)
					{
						//System.out.println("Adding a root node to matchedPropertySourceRootNodes");
						matchedPropertySourceRootNodes.add(aRootNode);
					}
					else
					{
						//System.out.println("Adding a root node to matchedPropertyTargetRootNodes");
						matchedPropertyTargetRootNodes.add(aRootNode);
					}
				}
			}
			else
			{
				if(typeOfNodes == alignType.aligningClasses)
				{
					if(source == true)
					{
						//System.out.println("Adding a root node "+ aRootNode.getLocalName()+" to unMatchedClassSourceRootNodes");
						unMatchedClassSourceRootNodes.add(aRootNode);
					}
					else
					{
						//System.out.println("Adding a root node "+ aRootNode.getLocalName()+" to unMatchedClassTargetRootNodes");
						unMatchedClassTargetRootNodes.add(aRootNode);
					}
				}
				else if(typeOfNodes == alignType.aligningProperties)
				{
					if(source == true)
					{
						//System.out.println("Adding a root node to unMatchedPropertySourceRootNodes");
						unMatchedPropertySourceRootNodes.add(aRootNode);
					}
					else
					{
						//System.out.println("Adding a root node to unMatchedPropertyTargetRootNodes");
						unMatchedPropertyTargetRootNodes.add(aRootNode);
					}
				}
			}
		}
		
		//System.out.println("The size of class root nodes is "+matchedClassRootNodes.size());
		//System.out.println("The size of property root nodes is "+matchedPropertyRootNodes.size());
	}
	
	
	private void createPRATrees(TreeNode aNode, alignType typeOfNodes, boolean source, int treeDepth)
	{
		List<TreeNode> myChildren = aNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(aNode);
		TreeNode myChild = null;
		
		aNode.setColor(1);
		aNode.setDepth(treeDepth);
		treeDepth++;
		
		if(myChildren != null)
		{
			//System.out.println("Before recursing, node " + aNode.getLocalName()+ " has " + myChildren.size() +" children");
			for(int i = 0; i < myChildren.size(); i++)
			{
				myChild= myChildren.get(i);
				if(myChild.getColor() == 0)
				{
					myChild.setParent(aNode);
					createPRATrees(myChild, typeOfNodes, source, treeDepth);
				}
			}
		}
		//else
			//System.out.println("Before recursing, node " +aNode.getLocalName() +" has no children");
		
		aNode.setColor(2);
		//Now go through children nodes and remove those that are matched, to leave only those that are not matched in the ontology
		if(myChildren != null)
		{
			//System.out.println("After recursing, node " + aNode.getLocalName()+ " has some children of size " + myChildren.size());
			for(int i = 0; i < myChildren.size(); i++)
			{
				myChild = myChildren.get(i);
				//System.out.println("Examining child node "+ myChild.getLocalName()+" for a match ");
				if(myChild.isMatched())
				{
					//System.out.println("Node "+ myChild.getLocalName() +" is matched ");
					if(typeOfNodes == alignType.aligningClasses)
					{
						if(source == true)
						{
							//System.out.println("Adding a node "+ myChild.getLocalName() +" to matchedClassSourceRootNodes");
							matchedClassSourceRootNodes.add(myChild);
						}
						else
						{
							//System.out.println("Adding a node "+ myChild.getLocalName() +" to matchedClassTargetRootNodes");
							matchedClassTargetRootNodes.add(myChild);
						}
					}
					else if(typeOfNodes == alignType.aligningProperties)
					{
						if(source == true)
						{
							//System.out.println("Adding a node to matchedPropertySourceRootNodes");
							matchedPropertySourceRootNodes.add(myChild);
						}
						else
						{
							//System.out.println("Adding a node to matchedPropertyTargetRootNodes");
							matchedPropertyTargetRootNodes.add(myChild);
						}
					}
					myChildren.remove(i);
					i -= 1;
				}
			}
		}
		//else
			//System.out.println("After recursing, node " +aNode.getLocalName() +" has no children");
	}
	
	
	private void alignNodes(alignType typeOfNodes)
	{
		//Now go through the matched subtrees and align nodes in it.
		//Remember that nodes in src or target rootnodes may not be matched
		//but may yet still need to be matched
		TreeNode sourceNode = null, targetNode = null;
		ArrayList<TreeNode> matchedRootNodes = null;
		
		if(typeOfNodes == alignType.aligningClasses)
		{
			//System.out.println("Aligning classes");
			matchedRootNodes = matchedClassSourceRootNodes;
		}
		else
		{
			//System.out.println("Aligning properties");
			matchedRootNodes = matchedPropertySourceRootNodes;
		}
		
		if(matchedRootNodes != null)
		for(int i = 0; i < matchedRootNodes.size(); i++)
		{
			sourceNode = matchedRootNodes.get(i);
			targetNode = sourceNode.getMatchedTo();
			//sourceNode.resetNodeColors();
			sourceNode.resetNodeColors();
			alignNodes(sourceNode, targetNode, typeOfNodes);			
		}
		
		//Now also align nodes rootnodes that are unmatched
		if(typeOfNodes == alignType.aligningClasses)
		{
			for(int i = 0; i < unMatchedClassSourceRootNodes.size(); i++)
			{
				sourceNode = unMatchedClassSourceRootNodes.get(i);
				for(int j = 0; j < unMatchedClassTargetRootNodes.size(); j++)
				{
					targetNode = unMatchedClassTargetRootNodes.get(j);
					//System.out.println("Aligning unmatched nodes, source: "+sourceNode.getLocalName()+" and target: "+targetNode.getLocalName());
					alignNodes(sourceNode, targetNode, typeOfNodes);
				}
			}
		}
		else if(typeOfNodes == alignType.aligningProperties)
		{
			for(int i = 0; i < unMatchedPropertySourceRootNodes.size(); i++)
			{
				sourceNode = unMatchedPropertySourceRootNodes.get(i);
				for(int j = 0; j < unMatchedPropertyTargetRootNodes.size(); j++)
				{
					targetNode = unMatchedPropertyTargetRootNodes.get(j);
					alignNodes(sourceNode, targetNode, typeOfNodes);
				}
			}
		}
		
	}
	
	
	/*private void resetColors(TreeNode aNode)
	{
		aNode.setColor(0);
		ArrayList<TreeNode> anAdj = aNode.getChildren();
		TreeNode cNode;
		
		if(anAdj != null)
		{
			for(int i = 0; i < anAdj.size(); i++)
			{
				cNode = anAdj.get(i);
				resetColors(cNode);
			}
		}
	}*/
	
	
	private void alignNodes(TreeNode sourceNode, TreeNode targetNode, alignType typeOfNodes)
	{
		List<TreeNode> myChildren = sourceNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(aNode);
		TreeNode childNode = null;
				
		sourceNode.setColor(1);
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
		
		sourceNode.setColor(2);
		//targetNode.resetNodeColors();
		targetNode.resetNodeColors();
		alignNodeWithTargets(sourceNode, targetNode, typeOfNodes);
		
	}
	
	private void alignNodeWithTargets(TreeNode srcNode, TreeNode targetNode, alignType typeOfNodes)
	{
		List<TreeNode> myChildren = targetNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(targetNode);
		TreeNode childNode = null;
		Mapping alignment = null;
		
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
		try {
			alignment = alignTwoNodes(srcNode.getNode(), targetNode.getNode(), typeOfNodes, matrix);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		matrix.set(srcNode.getNode().getIndex(), targetNode.getNode().getIndex(), alignment);
	}
	
	
	private void createAdjacency(List<TreeNode> treeNodes)
	{
		Node aNode, cNode;
		TreeNode parentNode, childNode;
		List<Node> childrenNodes;
		List<TreeNode> anAdj;
		
		
		
		for(int i = 0; i < treeNodes.size(); i++)
		{
			parentNode = treeNodes.get(i);
			aNode = parentNode.getNode();
			childrenNodes = aNode.getChildren();
			if(childrenNodes != null && childrenNodes.size() > 0)
			{
				//parentNode.children = new ArrayList<TreeNode>();
				anAdj = new ArrayList<TreeNode>();
				for(int j = 0; j < childrenNodes.size(); j++)
				{					
					cNode = childrenNodes.get(j);
					childNode = nodeToTreeNode.get(cNode);
					anAdj.add(childNode);
					//parentNode.children.add(childNode);
					//System.out.println("Adding "+ cNode.getLocalName()+ " as a child of " + aNode.getLocalName());
					//System.out.println("Adding "+ childNode+ " as a child of " + parentNode);
				}
				parentNode.setChildren(anAdj);
				System.out.println();
				//System.out.println(aNode.getLocalName() +" has numchildren " + parentNode.children.size());
			}
		}
	}
	
	
	private List<TreeNode> getRootNodes(List<TreeNode> targetList)
	{
		List<TreeNode> rootNodes = new ArrayList<TreeNode>();
		TreeNode aNode = null;
		
		for(int i = 0; i < targetList.size(); i++)
		{
			aNode = targetList.get(i);
			
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
	public void setInputClassesMatrix(SimilarityMatrix inputClassesMatrix) 
	{
		this.inputClassesMatrix = inputClassesMatrix;
	}


	/**
	 * @return the inputClassesMatrix
	 */
	public SimilarityMatrix getInputClassesMatrix() 
	{
		return inputClassesMatrix;
	}

	/**
	 * @param inputPropertiesMatrix the inputPropertiesMatrix to set
	 */
	public void setInputPropertiesMatrix(SimilarityMatrix inputPropertiesMatrix) 
	{
		this.inputPropertiesMatrix = inputPropertiesMatrix;
	}

	/**
	 * @return the inputPropertiesMatrix
	 */
	public SimilarityMatrix getInputPropertiesMatrix() 
	{
		return inputPropertiesMatrix;
	}



	/**
	 * @param inputClassesAlignmentSet the inputClassesAlignmentSet to set
	 */
	public void setInputClassesAlignmentSet(Alignment<Mapping> inputClassesAlignmentSet) {
		this.inputClassesAlignmentSet = inputClassesAlignmentSet;
	}



	/**
	 * @return the inputClassesAlignmentSet
	 */
	public Alignment<Mapping> getInputClassesAlignmentSet() {
		return inputClassesAlignmentSet;
	}



	/**
	 * @param inputPropertiesAlignmentSet the inputPropertiesAlignmentSet to set
	 */
	public void setInputPropertiesAlignmentSet(
			Alignment<Mapping> inputPropertiesAlignmentSet) {
		this.inputPropertiesAlignmentSet = inputPropertiesAlignmentSet;
	}



	/**
	 * @return the inputPropertiesAlignmentSet
	 */
	public Alignment<Mapping> getInputPropertiesAlignmentSet() {
		return inputPropertiesAlignmentSet;
	}



	/**
	 * @param nodeToTreeNod the nodeToTreeNod to set
	 */
	public void setNodeToTreeNode(HashMap<Node, TreeNode> nodeToTreeNode) {
		this.nodeToTreeNode = nodeToTreeNode;
	}



	/**
	 * @return the nodeToTreeNod
	 */
	public HashMap<Node, TreeNode> getNodeToTreeNode() {
		return nodeToTreeNode;
	}



	/**
	 * @param matchedClassSourceRootNodes the matchedClassSourceRootNodes to set
	 */
	public void setMatchedClassSourceRootNodes(
			ArrayList<TreeNode> matchedClassSourceRootNodes) {
		this.matchedClassSourceRootNodes = matchedClassSourceRootNodes;
	}



	/**
	 * @return the matchedClassSourceRootNodes
	 */
	public ArrayList<TreeNode> getMatchedClassSourceRootNodes() {
		return matchedClassSourceRootNodes;
	}



	/**
	 * @param matchedPropertySourceRootNodes the matchedPropertySourceRootNodes to set
	 */
	public void setMatchedPropertySourceRootNodes(
			ArrayList<TreeNode> matchedPropertySourceRootNodes) {
		this.matchedPropertySourceRootNodes = matchedPropertySourceRootNodes;
	}



	/**
	 * @return the matchedPropertySourceRootNodes
	 */
	public ArrayList<TreeNode> getMatchedPropertySourceRootNodes() {
		return matchedPropertySourceRootNodes;
	}



	/**
	 * @param unMatchedClassSourceRootNodes the unMatchedClassSourceRootNodes to set
	 */
	public void setUnMatchedClassSourceRootNodes(
			ArrayList<TreeNode> unMatchedClassSourceRootNodes) {
		this.unMatchedClassSourceRootNodes = unMatchedClassSourceRootNodes;
	}



	/**
	 * @return the unMatchedClassSourceRootNodes
	 */
	public ArrayList<TreeNode> getUnMatchedClassSourceRootNodes() {
		return unMatchedClassSourceRootNodes;
	}



	/**
	 * @param unMatchedPropertySourceRootNodes the unMatchedPropertySourceRootNodes to set
	 */
	public void setUnMatchedPropertySourceRootNodes(
			ArrayList<TreeNode> unMatchedPropertySourceRootNodes) {
		this.unMatchedPropertySourceRootNodes = unMatchedPropertySourceRootNodes;
	}



	/**
	 * @return the unMatchedPropertySourceRootNodes
	 */
	public ArrayList<TreeNode> getUnMatchedPropertySourceRootNodes() {
		return unMatchedPropertySourceRootNodes;
	}



	/**
	 * @param matchedClassTargetRootNodes the matchedClassTargetRootNodes to set
	 */
	public void setMatchedClassTargetRootNodes(
			ArrayList<TreeNode> matchedClassTargetRootNodes) {
		this.matchedClassTargetRootNodes = matchedClassTargetRootNodes;
	}



	/**
	 * @return the matchedClassTargetRootNodes
	 */
	public ArrayList<TreeNode> getMatchedClassTargetRootNodes() {
		return matchedClassTargetRootNodes;
	}



	/**
	 * @param matchedPropertyTargetRootNodes the matchedPropertyTargetRootNodes to set
	 */
	public void setMatchedPropertyTargetRootNodes(
			ArrayList<TreeNode> matchedPropertyTargetRootNodes) {
		this.matchedPropertyTargetRootNodes = matchedPropertyTargetRootNodes;
	}



	/**
	 * @return the matchedPropertyTargetRootNodes
	 */
	public ArrayList<TreeNode> getMatchedPropertyTargetRootNodes() {
		return matchedPropertyTargetRootNodes;
	}



	/**
	 * @param unMatchedClassTargetRootNodes the unMatchedClassTargetRootNodes to set
	 */
	public void setUnMatchedClassTargetRootNodes(
			ArrayList<TreeNode> unMatchedClassTargetRootNodes) {
		this.unMatchedClassTargetRootNodes = unMatchedClassTargetRootNodes;
	}



	/**
	 * @return the unMatchedClassTargetRootNodes
	 */
	public ArrayList<TreeNode> getUnMatchedClassTargetRootNodes() {
		return unMatchedClassTargetRootNodes;
	}



	/**
	 * @param unMatchedPropertyTargetRootNodes the unMatchedPropertyTargetRootNodes to set
	 */
	public void setUnMatchedPropertyTargetRootNodes(
			ArrayList<TreeNode> unMatchedPropertyTargetRootNodes) {
		this.unMatchedPropertyTargetRootNodes = unMatchedPropertyTargetRootNodes;
	}



	/**
	 * @return the unMatchedPropertyTargetRootNodes
	 */
	public ArrayList<TreeNode> getUnMatchedPropertyTargetRootNodes() {
		return unMatchedPropertyTargetRootNodes;
	}



	/**
	 * @param srcClassesIdToNode the srcClassesIdToNode to set
	 */
	public void setSrcClassesIdToNode(HashMap<Integer, Node> srcClassesIdToNode) {
		this.srcClassesIdToNode = srcClassesIdToNode;
	}



	/**
	 * @return the srcClassesIdToNode
	 */
	public HashMap<Integer, Node> getSrcClassesIdToNode() {
		return srcClassesIdToNode;
	}



	/**
	 * @param targetClassesIdToNode the targetClassesIdToNode to set
	 */
	public void setTargetClassesIdToNode(HashMap<Integer, Node> targetClassesIdToNode) {
		this.targetClassesIdToNode = targetClassesIdToNode;
	}



	/**
	 * @return the targetClassesIdToNode
	 */
	public HashMap<Integer, Node> getTargetClassesIdToNode() {
		return targetClassesIdToNode;
	}



	/**
	 * @param srcPropertiesIdToNode the srcPropertiesIdToNode to set
	 */
	public void setSrcPropertiesIdToNode(HashMap<Integer, Node> srcPropertiesIdToNode) {
		this.srcPropertiesIdToNode = srcPropertiesIdToNode;
	}



	/**
	 * @return the srcPropertiesIdToNode
	 */
	public HashMap<Integer, Node> getSrcPropertiesIdToNode() {
		return srcPropertiesIdToNode;
	}



	/**
	 * @param targetPropertiesIdToNode the targetPropertiesIdToNode to set
	 */
	public void setTargetPropertiesIdToNode(HashMap<Integer, Node> targetPropertiesIdToNode) {
		this.targetPropertiesIdToNode = targetPropertiesIdToNode;
	}



	/**
	 * @return the targetPropertiesIdToNode
	 */
	public HashMap<Integer, Node> getTargetPropertiesIdToNode() {
		return targetPropertiesIdToNode;
	}



	
}
