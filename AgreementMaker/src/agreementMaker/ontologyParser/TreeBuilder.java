package agreementMaker.ontologyParser;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import agreementMaker.userInterface.vertex.Vertex;

public abstract class TreeBuilder {

	// instance variables
	private Document documentRoot;
	protected int treeCount;
	protected Vertex treeRoot;
	
	//abstract public int buildTree(Vertex currentTreeNode, Node document);
	/**
	 * This function displays the JOptionPane with title and descritpion
	 * @param desc the description to display in the option pane
	 * @param title the title to display on the option pane
	 */
	public void displayOptionPaneAndExit(String desc, String title)
	{
		JOptionPane.showMessageDialog(null,desc+"\nFatal Error: Application will be closed.",title, JOptionPane.PLAIN_MESSAGE);					
	}	
	/**
	 * This function returns the attribute name of the node
	 *
	 * @param  node   	node which you want the attribute name
	 * @param attrName	the atrribute name
	 * @return attribute name the attribute name
	 */
	public String getAttr(Node node, String attrName)
	{
		return ((Element)node).getAttribute(attrName);
	}
	/**
	 * This function returns the description
	 *
	 * @param node	node which you want the description from
	 * @param des attribute name
	 */
	public String getDes(Node node, String des)
	{
		return ((Element)node).getAttribute(des);
	}
	/**
	 * This function returns the document root 
	 * @return documentRoot	the document root
	 */
	public Document getDocumentRoot()
	{
		return documentRoot;
	}
	/**
	 * This function returns the number of nodes created by the tree
	 * @return int the number of nodes created by the tree
	 */
	public int getTreeCount()
	{
		return treeCount;
	}  
	/**
	 * This function returns the tree root
	 * @return treeRoot	root of the tree
	 */
	public Vertex getTreeRoot()
	{
		return treeRoot;
	}
	/********************************************************************************************/
	/*                              MODIFIER METHODS                                            */
	/********************************************************************************************/
	/**
	 * This function sets the document root
	 *
	 * @param root root of the document
	 */
	public void setDocumentRoot(Document root)
	{
		documentRoot = root;
	}
	/********************************************************************************************/
	/**
	 * This function sets the tree root
	 *
	 * @param root root of the tree
	 */
	public void setTreeRoot(Vertex root)    
	{
		treeRoot = root;
	}
	/********************************************************************************************/	
}
