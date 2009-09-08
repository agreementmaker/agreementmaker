package am.application.mappingEngine.PRAMatcher;

import java.util.ArrayList;


import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.AbstractMatcherParametersPanel;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AlignmentMatrix;
import am.application.mappingEngine.StringUtil.Normalizer;
import am.application.mappingEngine.StringUtil.NormalizerParameter;
import am.application.mappingEngine.referenceAlignment.ReferenceAlignmentParametersPanel;
import am.application.ontology.Node;

public class PRAMatcher extends AbstractMatcher 
{
	// the Alignment Matrices from the Input Matching algorithm.
	private AlignmentMatrix inputClassesMatrix = null;
	private AlignmentMatrix inputPropertiesMatrix = null;
	private AlignmentMatrix matrix = null;
	
	//the structure that holds the roots of subtrees which are matched nodes in the ontology
	private ArrayList<Node> matchedClassSourceRootNodes = null;
	private ArrayList<Node> matchedPropertySourceRootNodes = null;
	private ArrayList<Node> unMatchedClassSourceRootNodes = null;
	private ArrayList<Node> unMatchedPropertySourceRootNodes = null;
	private ArrayList<Node> matchedClassTargetRootNodes = null;
	private ArrayList<Node> matchedPropertyTargetRootNodes = null;
	private ArrayList<Node> unMatchedClassTargetRootNodes = null;
	private ArrayList<Node> unMatchedPropertyTargetRootNodes = null;
	
	//Constructor
	public PRAMatcher()
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
	
	/*
	protected AlignmentMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception 
    {
		Node src = null, target = null;
		//First set matched nodes as matched, to help identifying them as roots when traversing the ontology tree
		//Also, set the nodes to which they are matched in the other ontology. This is for easy access to such nodes
		//when traversing the ontology tree, in search of the subtrees with matched nodes as the roots. 
		
		
		if(typeOfNodes == alignType.aligningClasses)
		{
			setMatchingPairs(inputClassesMatrix, sourceList, targetList);
		}
		else if(typeOfNodes == alignType.aligningProperties)
		{
			setMatchingPairs(inputPropertiesMatrix, sourceList, targetList);
		}

		createPRATrees(sourceList, targetList, typeOfNodes);

		//Now we align nodes by considering only nodes in the subtrees of matched nodes
		//Initialize matrix before aligning nodes, cos this method will access matrix
		matrix = new AlignmentMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
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
	
	
	private void setMatchingPairs(AlignmentMatrix inputMatrix, ArrayList<Node> sourceList, ArrayList<Node> targetList) throws Exception
	{
		Alignment alignment = null;
		int numRows = sourceList.size();
		int numCols = targetList.size();
		Node src = null, target = null;
		//int numSet = 0;
		
		for(int i = 0; i <  numRows; i++)
		{
			src = sourceList.get(i);
			for (int j = 0; j < numCols; j++)
			{
				target = targetList.get(j);
				if(inputMatrix.get(src.getIndex(), target.getIndex()) != null)
				{
					alignment = inputMatrix.get(src.getIndex(), target.getIndex());
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
	

	
	private void printAdjacency(ArrayList<Node> nodes)
	{
		Node aNode;
		Node nNode;
		ArrayList<Node> neighbours;
		
		for(int i = 0; i < nodes.size(); i++)
		{
			aNode = nodes.get(i);
			neighbours = aNode.getChildren();
			if(neighbours != null)
			{
				for(int j = 0; j < neighbours.size(); j++)
				{
					nNode = neighbours.get(j);
					System.out.println("Node " +aNode.getLocalName() +" has neighbour " + nNode.getLocalName());
					//System.out.println("Node " +aNode +" has neighbour " + nNode);		
				}
				System.out.println();
			}
		}
	}
	
	
	private void createPRATrees(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception
	{
		//Need to access the inputMatrices to find which nodes are matched
		//Start with inputClassesMatrix
		ArrayList<Node> srcRootNodes = getRootNodes(sourceList);
		//System.out.println("The size of the srcRootNodes is "+srcRootNodes.size());
		
		ArrayList<Node> targetRootNodes = getRootNodes(targetList);
		//System.out.println("The size of the targetRootNodes is "+targetRootNodes.size());
		
		//initialize class and property root nodes
		matchedClassSourceRootNodes = new ArrayList<Node>();
		matchedPropertySourceRootNodes = new ArrayList<Node>();
		unMatchedClassSourceRootNodes = new ArrayList<Node>();
		unMatchedPropertySourceRootNodes = new ArrayList<Node>();
		matchedClassTargetRootNodes = new ArrayList<Node>();
		matchedPropertyTargetRootNodes = new ArrayList<Node>();
		unMatchedClassTargetRootNodes = new ArrayList<Node>();
		unMatchedPropertyTargetRootNodes = new ArrayList<Node>();
		createPRATrees(srcRootNodes, typeOfNodes, true);
		createPRATrees(targetRootNodes, typeOfNodes, false);
		
	}
	
	
	private void createPRATrees(ArrayList<Node> rootNodes, alignType typeOfNodes, boolean source)
	{
		Node aRootNode = null;
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
	
	
	private void createPRATrees(Node aNode, alignType typeOfNodes, boolean source, int treeDepth)
	{
		ArrayList<Node> myChildren = aNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(aNode);
		Node myChild = null;
		
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
	
	/*
	private void alignNodes(alignType typeOfNodes)
	{
		//Now go through the matched subtrees and align nodes in it.
		//Remember that nodes in src or target rootnodes may not be matched
		//but may yet still need to be matched
		Node sourceNode = null, targetNode = null;
		ArrayList<Node> matchedRootNodes = null;
		
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
			resetColors(sourceNode);
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
	*/
	
	private void resetColors(Node aNode)
	{
		aNode.setColor(0);
		ArrayList<Node> anAdj = aNode.getChildren();
		Node cNode;
		
		if(anAdj != null)
		{
			for(int i = 0; i < anAdj.size(); i++)
			{
				cNode = anAdj.get(i);
				resetColors(cNode);
			}
		}
	}
	
	/*
	private void alignNodes(Node sourceNode, Node targetNode, alignType typeOfNodes)
	{
		ArrayList<Node> myChildren = sourceNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(aNode);
		Node childNode = null;
				
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
		resetColors(targetNode);
		alignNodeWithTargets(sourceNode, targetNode, typeOfNodes);
		
	}
	
	private void alignNodeWithTargets(Node srcNode, Node targetNode, alignType typeOfNodes)
	{
		ArrayList<Node> myChildren = targetNode.getChildren();
		//ArrayList<TreeNode> myChildren = adjacency.get(targetNode);
		Node childNode = null;
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
		alignment = alignTwoNodes(srcNode, targetNode, typeOfNodes);
		matrix.set(srcNode.getIndex(), targetNode.getIndex(), alignment);
	}
	*/
	
	
	
	private ArrayList<Node> getRootNodes(ArrayList<Node> targetList)
	{
		ArrayList<Node> rootNodes = new ArrayList<Node>();
		Node aNode = null;
		
		for(int i = 0; i < targetList.size(); i++)
		{
			aNode = targetList.get(i);
			
			//System.out.println("Examining node "+ aNode.getNode().getLocalName());
			if(aNode.getParents() == null || aNode.getParents().size() == 0)
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
	 * @param matchedClassSourceRootNodes the matchedClassSourceRootNodes to set
	 */
	public void setMatchedClassSourceRootNodes(
			ArrayList<Node> matchedClassSourceRootNodes) {
		this.matchedClassSourceRootNodes = matchedClassSourceRootNodes;
	}



	/**
	 * @return the matchedClassSourceRootNodes
	 */
	public ArrayList<Node> getMatchedClassSourceRootNodes() {
		return matchedClassSourceRootNodes;
	}



	/**
	 * @param matchedPropertySourceRootNodes the matchedPropertySourceRootNodes to set
	 */
	public void setMatchedPropertySourceRootNodes(
			ArrayList<Node> matchedPropertySourceRootNodes) {
		this.matchedPropertySourceRootNodes = matchedPropertySourceRootNodes;
	}



	/**
	 * @return the matchedPropertySourceRootNodes
	 */
	public ArrayList<Node> getMatchedPropertySourceRootNodes() {
		return matchedPropertySourceRootNodes;
	}



	/**
	 * @param unMatchedClassSourceRootNodes the unMatchedClassSourceRootNodes to set
	 */
	public void setUnMatchedClassSourceRootNodes(
			ArrayList<Node> unMatchedClassSourceRootNodes) {
		this.unMatchedClassSourceRootNodes = unMatchedClassSourceRootNodes;
	}



	/**
	 * @return the unMatchedClassSourceRootNodes
	 */
	public ArrayList<Node> getUnMatchedClassSourceRootNodes() {
		return unMatchedClassSourceRootNodes;
	}



	/**
	 * @param unMatchedPropertySourceRootNodes the unMatchedPropertySourceRootNodes to set
	 */
	public void setUnMatchedPropertySourceRootNodes(
			ArrayList<Node> unMatchedPropertySourceRootNodes) {
		this.unMatchedPropertySourceRootNodes = unMatchedPropertySourceRootNodes;
	}



	/**
	 * @return the unMatchedPropertySourceRootNodes
	 */
	public ArrayList<Node> getUnMatchedPropertySourceRootNodes() {
		return unMatchedPropertySourceRootNodes;
	}



	/**
	 * @param matchedClassTargetRootNodes the matchedClassTargetRootNodes to set
	 */
	public void setMatchedClassTargetRootNodes(
			ArrayList<Node> matchedClassTargetRootNodes) {
		this.matchedClassTargetRootNodes = matchedClassTargetRootNodes;
	}



	/**
	 * @return the matchedClassTargetRootNodes
	 */
	public ArrayList<Node> getMatchedClassTargetRootNodes() {
		return matchedClassTargetRootNodes;
	}



	/**
	 * @param matchedPropertyTargetRootNodes the matchedPropertyTargetRootNodes to set
	 */
	public void setMatchedPropertyTargetRootNodes(
			ArrayList<Node> matchedPropertyTargetRootNodes) {
		this.matchedPropertyTargetRootNodes = matchedPropertyTargetRootNodes;
	}



	/**
	 * @return the matchedPropertyTargetRootNodes
	 */
	public ArrayList<Node> getMatchedPropertyTargetRootNodes() {
		return matchedPropertyTargetRootNodes;
	}



	/**
	 * @param unMatchedClassTargetRootNodes the unMatchedClassTargetRootNodes to set
	 */
	public void setUnMatchedClassTargetRootNodes(
			ArrayList<Node> unMatchedClassTargetRootNodes) {
		this.unMatchedClassTargetRootNodes = unMatchedClassTargetRootNodes;
	}



	/**
	 * @return the unMatchedClassTargetRootNodes
	 */
	public ArrayList<Node> getUnMatchedClassTargetRootNodes() {
		return unMatchedClassTargetRootNodes;
	}



	/**
	 * @param unMatchedPropertyTargetRootNodes the unMatchedPropertyTargetRootNodes to set
	 */
	public void setUnMatchedPropertyTargetRootNodes(
			ArrayList<Node> unMatchedPropertyTargetRootNodes) {
		this.unMatchedPropertyTargetRootNodes = unMatchedPropertyTargetRootNodes;
	}



	/**
	 * @return the unMatchedPropertyTargetRootNodes
	 */
	public ArrayList<Node> getUnMatchedPropertyTargetRootNodes() {
		return unMatchedPropertyTargetRootNodes;
	}



	
}
