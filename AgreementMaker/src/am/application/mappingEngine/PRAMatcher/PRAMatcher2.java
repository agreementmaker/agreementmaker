package am.application.mappingEngine.PRAMatcher;

import java.util.ArrayList;
import java.util.HashMap;

import am.application.Core;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AlignmentMatrix;
import am.application.mappingEngine.AlignmentSet;
import am.application.mappingEngine.MatchersRegistry;
import am.application.ontology.Node;
import am.application.mappingEngine.PRAMatcher.PRAMatcher;

public class PRAMatcher2 extends AbstractMatcher
{
	// the Alignment Matrices from the Input Matching algorithm.
	private AlignmentSet inputPropertiesAlignmentSet;
	private AlignmentSet inputClassesAlignmentSet;
	private AlignmentSet praClassesAlignmentSet;
	private AlignmentSet praPropertiesAlignmentSet;
	private HashMap<Node, TreeNode> nodeToTreeNode;

	
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
		PRAMatcher myMatcher = null;
		AbstractMatcher aMatcher = null, nMatcher = null;
		for(int i = 0; i < inputMatchers.size(); i++)
		{
			aMatcher = inputMatchers.get(i);
			//System.out.println("Class of matcher is "+aMatcher.getClass().getName());
			if(aMatcher.getClass().getName().equals("am.application.mappingEngine.PRAMatcher.PRAMatcher"))
			{
				//System.out.println("Found PRAMatcher at index "+i);
				myMatcher = (PRAMatcher)aMatcher;
				if(i == 0)
					nMatcher = inputMatchers.get(1);
				else
					nMatcher = inputMatchers.get(0);
			}
		}
		
		inputClassesAlignmentSet = nMatcher.getClassAlignmentSet();
		inputPropertiesAlignmentSet = nMatcher.getPropertyAlignmentSet();
		
		nodeToTreeNode = myMatcher.getNodeToTreeNode();
		praClassesAlignmentSet = myMatcher.getInputClassesAlignmentSet();
		praPropertiesAlignmentSet = myMatcher.getInputPropertiesAlignmentSet();
		matchedClassSourceRootNodes = myMatcher.getMatchedClassSourceRootNodes();
		matchedPropertySourceRootNodes = myMatcher.getMatchedPropertySourceRootNodes();
		unMatchedClassSourceRootNodes = myMatcher.getUnMatchedClassSourceRootNodes();
		unMatchedPropertySourceRootNodes = myMatcher.getUnMatchedPropertySourceRootNodes();
		matchedClassTargetRootNodes = myMatcher.getMatchedClassTargetRootNodes();
		matchedPropertyTargetRootNodes = myMatcher.getMatchedPropertyTargetRootNodes();
		unMatchedClassTargetRootNodes = myMatcher.getUnMatchedClassTargetRootNodes();
		unMatchedPropertyTargetRootNodes = myMatcher.getUnMatchedPropertyTargetRootNodes();
		
		removeIncorrectMappings(alignType.aligningClasses);
		removeIncorrectMappings(alignType.aligningProperties);
		addUndiscoveredMatchings();
		
		matchEnd();
	}
	
	
	private void removeIncorrectMappings(alignType typeOfNodes)
	{
		int numSources = 0;		
		int numTargets = 0;
	
		AlignmentSet resultSet = new AlignmentSet();
		AlignmentMatrix resultMatrix = null;
		Alignment anAlignment = null;
		Node aNode = null, src = null, target = null;
		TreeNode aTreeNode = null, matchedNode = null;
		
		if(typeOfNodes.equals(alignType.aligningClasses))
		{
			numSources = sourceOntology.getClassesList().size();
			numTargets = targetOntology.getClassesList().size();
			resultMatrix = new AlignmentMatrix(numSources, numTargets, alignType.aligningClasses);

			for(int i = 0; i < praClassesAlignmentSet.size(); i++)
			{
				anAlignment = praClassesAlignmentSet.getAlignment(i);
				src = anAlignment.getEntity1();
				target = anAlignment.getEntity2();
				//System.out.println("The node to treeNode hashmap has size "+nodeToTreeNode.size());
				aTreeNode = nodeToTreeNode.get(src);
				//matchedNode = aTreeNode.getMatchedTo();
				//System.out.println("Found src "+aTreeNode.getNode().getLocalName()+" and target "+ matchedNode.getNode().getLocalName());
				//System.out.println("Found src "+src.getLocalName()+" and target "+ target.getLocalName());
			}
			
			//Now look at the alignments in the input alignment and see if there is any that has to be removed
			//or any that is not in the partial reference alignment that has to be added
			for(int i = 0; i < matchedClassSourceRootNodes.size(); i++)
			{
				aTreeNode = matchedClassSourceRootNodes.get(i);
				matchedNode = aTreeNode.getMatchedTo();
				checkMatchings(aTreeNode, matchedNode);
			}
			//add everything in the partial reference alignment in the alignmentSet
			for(int i = 0; i < praClassesAlignmentSet.size(); i++)
			{
				anAlignment = praClassesAlignmentSet.getAlignment(i);
				resultSet.addAlignment(anAlignment);
				src = anAlignment.getEntity1();
				target = anAlignment.getEntity2();
				//mappings.add(anAlignment);
				resultMatrix.set(src.getIndex(), target.getIndex(), anAlignment);
			}
		}
		else if(typeOfNodes.equals(alignType.aligningProperties))
		{
			numSources = sourceOntology.getPropertiesList().size();			
			numTargets = targetOntology.getPropertiesList().size();
			resultMatrix = new AlignmentMatrix(numSources, numTargets, alignType.aligningProperties);
		
			//add everything in the partial reference alignment in the alignmentSet
			for(int i = 0; i < praPropertiesAlignmentSet.size(); i++)
			{
				anAlignment = praPropertiesAlignmentSet.getAlignment(i);
				resultSet.addAlignment(anAlignment);
				src = anAlignment.getEntity1();
				target = anAlignment.getEntity2();
				//mappings.add(anAlignment);
				resultMatrix.set(src.getIndex(), target.getIndex(), anAlignment);
			}
		}
	}
	
	
	private void checkMatchings(TreeNode aTreeNode, TreeNode matchedNode)
	{
		aTreeNode.resetNodeColors();
		matchedNode.resetNodeColors();
	}

	private void addUndiscoveredMatchings()
	{
		
	}
	
}

