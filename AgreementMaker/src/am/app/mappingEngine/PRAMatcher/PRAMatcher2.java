package am.app.mappingEngine.PRAMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.PRAMatcher.PRAMatcher;
import am.app.ontology.Node;

public class PRAMatcher2 extends AbstractMatcher
{
	// the Alignment Matrices from the Input Matching algorithm.
	private Alignment inputPropertiesAlignmentSet;
	private Alignment inputClassesAlignmentSet;
	private Alignment praClassesAlignmentSet;
	private Alignment praPropertiesAlignmentSet;
	private HashMap<Node, TreeNode> nodeToTreeNode;
	private HashMap<Integer, Node> srcClassesIdToNode;
	private HashMap<Integer, Node> targetClassesIdToNode;
	private HashMap<Integer, Node> srcPropertiesIdToNode;
	private HashMap<Integer, Node> targetPropertiesIdToNode;

	
	//the structure that holds the roots of subtrees which are matched nodes in the ontology
	private ArrayList<TreeNode> matchedClassSourceRootNodes;
	private ArrayList<TreeNode> matchedPropertySourceRootNodes;
	private ArrayList<TreeNode> unMatchedClassSourceRootNodes;
	private ArrayList<TreeNode> unMatchedPropertySourceRootNodes;
	private ArrayList<TreeNode> matchedClassTargetRootNodes;
	private ArrayList<TreeNode> matchedPropertyTargetRootNodes;
	private ArrayList<TreeNode> unMatchedClassTargetRootNodes;
	private ArrayList<TreeNode> unMatchedPropertyTargetRootNodes;

	public PRAMatcher2()
	{
		super();
		minInputMatchers = 2;
		maxInputMatchers = 2;
	}
	
	
	public void match() throws Exception
	{
		matchStart();
		super.beforeAlignOperations();
		//This is for accessing the PRAMatcher instance that already exists in the control panel.
		//PRAMatcher myMatcher = (PRAMatcher) Core.getInstance().getMatcherInstance( MatchersRegistry.PRAMatcher );
		AbstractMatcher aMatcher = null, abMatcher = null;
		PRAMatcher praMatcher = null;
		for(int i = 0; i < inputMatchers.size(); i++)
		{
			aMatcher = inputMatchers.get(i);
			//System.out.println("Class of matcher is "+aMatcher.getClass().getName());
			if(aMatcher.getClass().getName().equals("am.app.mappingEngine.PRAMatcher.PRAMatcher"))
			{
				//System.out.println("Found PRAMatcher at index "+i);
				praMatcher = (PRAMatcher)aMatcher;
				if(i == 0)
					abMatcher = inputMatchers.get(1);
				else
					abMatcher = inputMatchers.get(0);
			}
		}
		
		inputClassesAlignmentSet = abMatcher.getClassAlignmentSet();
		inputPropertiesAlignmentSet = abMatcher.getPropertyAlignmentSet();
			
		nodeToTreeNode = praMatcher.getNodeToTreeNode();
		praClassesAlignmentSet = praMatcher.getInputClassesAlignmentSet();
		praPropertiesAlignmentSet = praMatcher.getInputPropertiesAlignmentSet();
		matchedClassSourceRootNodes = praMatcher.getMatchedClassSourceRootNodes();
		matchedPropertySourceRootNodes = praMatcher.getMatchedPropertySourceRootNodes();
		unMatchedClassSourceRootNodes = praMatcher.getUnMatchedClassSourceRootNodes();
		unMatchedPropertySourceRootNodes = praMatcher.getUnMatchedPropertySourceRootNodes();
		matchedClassTargetRootNodes = praMatcher.getMatchedClassTargetRootNodes();
		matchedPropertyTargetRootNodes = praMatcher.getMatchedPropertyTargetRootNodes();
		unMatchedClassTargetRootNodes = praMatcher.getUnMatchedClassTargetRootNodes();
		unMatchedPropertyTargetRootNodes = praMatcher.getUnMatchedPropertyTargetRootNodes();
		srcClassesIdToNode = praMatcher.getSrcClassesIdToNode();
		targetClassesIdToNode = praMatcher.getTargetClassesIdToNode();
		srcPropertiesIdToNode = praMatcher.getSrcPropertiesIdToNode();
		targetPropertiesIdToNode = praMatcher.getTargetPropertiesIdToNode();
		
		removeIncorrectMappings(alignType.aligningClasses);
		removeIncorrectMappings(alignType.aligningProperties);
		addUndiscoveredMappings(alignType.aligningClasses);
		addUndiscoveredMappings(alignType.aligningProperties);
		
		matchEnd();
	}
	
	
	private void removeIncorrectMappings(alignType typeOfNodes)
	{
		int numSources = 0;		
		int numTargets = 0;
	
		Alignment resultSet = new Alignment();
		SimilarityMatrix resultMatrix = null;
		Mapping anAlignment = null;
		HashSet<Mapping> mappedNodes = new HashSet<Mapping>();
		Node praSrc = null, praTarget = null, inputSrc = null, inputTarget = null;
		TreeNode aTreeNode = null, matchedNode = null;

		Integer anInt= null, nextInt = null;
		
		if(typeOfNodes.equals(alignType.aligningClasses))
		{	
			numSources = sourceOntology.getClassesList().size();
			numTargets = targetOntology.getClassesList().size();
			resultMatrix = new SimilarityMatrix(numSources, numTargets, alignType.aligningClasses);

			System.out.println("Num of entries in input class alignment set is " + inputClassesAlignmentSet.size());
			for(int i = 0; i < inputClassesAlignmentSet.size(); i++)
			{
				anAlignment = inputClassesAlignmentSet.getAlignment(i);
				inputSrc = anAlignment.getEntity1();
				inputTarget = anAlignment.getEntity2();
				anInt = new Integer(inputSrc.getIndex());
				nextInt = new Integer(inputTarget.getIndex());
				praSrc = srcClassesIdToNode.get(anInt);
				praTarget = targetClassesIdToNode.get(nextInt);
				aTreeNode = nodeToTreeNode.get(praSrc);	
				matchedNode = nodeToTreeNode.get(praTarget);
				
				//System.out.println("Src node depth is "+aTreeNode.getDepth()+" and dest node depth is "+matchedNode.getDepth());
				if(aTreeNode.getDepth() == matchedNode.getDepth())
				{
					//System.out.println("Adding a result from the input classes alignmentSet");
					resultMatrix.set(inputSrc.getIndex(), inputTarget.getIndex(), anAlignment);
					resultSet.addAlignment(anAlignment);
					mappedNodes.add(anAlignment);
				}
				//else
					//System.out.println("\nNot adding, src and dest are at different depths\n");
				//System.out.println("Found src "+aTreeNode.getNode().getLocalName()+" and target "+ matchedNode.getNode().getLocalName());
				//System.out.println("Found src "+src.getLocalName()+" and target "+ target.getLocalName());
			}
			
			//add every other alignment in the partial reference alignment not yet in the result
			for(int i = 0; i < praClassesAlignmentSet.size(); i++)
			{
				anAlignment = praClassesAlignmentSet.getAlignment(i);
				if(!mappedNodes.contains(anAlignment))
				{
					//System.out.println("Adding a result from the pra classes alignment set");
					resultSet.addAlignment(anAlignment);
					praSrc = anAlignment.getEntity1();
					praTarget = anAlignment.getEntity2();
					mappedNodes.add(anAlignment);
					resultMatrix.set(praSrc.getIndex(), praTarget.getIndex(), anAlignment);
				}
			}
			classesAlignmentSet = resultSet;
			classesMatrix = resultMatrix;
		}
		else if(typeOfNodes.equals(alignType.aligningProperties))
		{
			numSources = sourceOntology.getPropertiesList().size();			
			numTargets = targetOntology.getPropertiesList().size();
			resultMatrix = new SimilarityMatrix(numSources, numTargets, alignType.aligningProperties);
		
			//add everything in the partial reference alignment in the alignmentSet
			for(int i = 0; i < inputPropertiesAlignmentSet.size(); i++)
			{
				anAlignment = inputPropertiesAlignmentSet.getAlignment(i); 
				inputSrc = anAlignment.getEntity1();
				inputTarget = anAlignment.getEntity2();
				anInt = new Integer(inputSrc.getIndex());
				nextInt = new Integer(inputTarget.getIndex());
				praSrc = srcPropertiesIdToNode.get(anInt);
				praTarget = targetPropertiesIdToNode.get(nextInt);
				aTreeNode = nodeToTreeNode.get(praSrc);			
				matchedNode = nodeToTreeNode.get(praTarget);
				
				if(aTreeNode.getDepth() == matchedNode.getDepth())
				{
					resultMatrix.set(inputSrc.getIndex(), inputTarget.getIndex(), anAlignment);
					resultSet.addAlignment(anAlignment);
					mappedNodes.add(anAlignment);
				}
			}
			
			//add every other alignment in the partial reference alignment not yet in the result
			for(int i = 0; i < praPropertiesAlignmentSet.size(); i++)
			{
				anAlignment = praPropertiesAlignmentSet.getAlignment(i);
				if(!mappedNodes.contains(anAlignment))
				{
					resultSet.addAlignment(anAlignment);
					praSrc = anAlignment.getEntity1();
					praTarget = anAlignment.getEntity2();
					mappedNodes.add(anAlignment);
					resultMatrix.set(praSrc.getIndex(), praTarget.getIndex(), anAlignment);
				}
			}
			propertiesAlignmentSet = resultSet;
			propertiesMatrix = resultMatrix;
		}
	}
	


	private void addUndiscoveredMappings(alignType typeOfNodes)
	{
		if(typeOfNodes.equals(alignType.aligningClasses))
		{
			
		}
		else if(typeOfNodes.equals(alignType.aligningProperties))
		{
			
		}
	}
	
}

