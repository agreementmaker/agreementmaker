/**
 * 
 */
package am.application.mappingEngine.PRAMatcher;

//import java.util.ArrayList;

import java.util.ArrayList;

import am.application.ontology.Node;

/**
 * @author Angie
 *
 */
public class TreeNode 
{
	private Node node;
	private boolean matched;
	private TreeNode matchedTo;
	public ArrayList<Node> children;
	private TreeNode parent;
	private int color;
	
	public TreeNode(Node aNode)
	{
		node = aNode;
		setColor(0);
	}
	
	/*
	public void resetNodeColors()
	{
		TreeNode aChild = null;
		this.color = 0;
		
		if(this.children != null)
		{
			for(int i = 0; i < this.children.size(); i++)
			{
				aChild = this.children.get(i);
				aChild.resetNodeColors();
			}
		}
	}
	*/
	
	/**
	 * @param matched the matched to set
	 */
	public void setMatched(boolean matched) 
	{
		this.matched = matched;
	}

	/**
	 * @return the matched
	 */
	public boolean isMatched()
	{
		return matched;
	}

	/**
	 * @param matchedTo the matchedTo to set
	 */
	public void setMatchedTo(TreeNode matchedTo) 
	{
		this.matchedTo = matchedTo;
	}

	/**
	 * @return the matchedTo
	 */
	public TreeNode getMatchedTo() 
	{
		return matchedTo;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) 
	{
		this.node = node;
	}

	/**
	 * @return the node
	 */
	public Node getNode() 
	{
		return node;
	}

	/**
	 * @param children the children to set
	 */
	
	public void setChildren(ArrayList<Node> children) 
	{
		this.children = children;
	}
	
	/**
	 * @return the children
	 */
	
	public ArrayList<Node> getChildren() 
	{
		return children;
	}
	

	/**
	 * @param color the color to set
	 */
	public void setColor(int color) 
	{
		this.color = color;
	}

	/**
	 * @return the color
	 */
	public int getColor() 
	{
		return color;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(TreeNode parent) 
	{
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public TreeNode getParent() 
	{
		return parent;
	}
	
	
	
	
}
