package agreementMaker.ontologyParser;

// JAXP Packages

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import agreementMaker.userInterface.vertex.Vertex;

/**
 * XmlTreeBuilder Class - 
 * 
 * This class parses the schema represented as XML file using Xalan and Xerces 
 * Java API's and builds the tree structure.
 *
 * @author ADVIS Research Laboratory
 * @version 11/13/2004
 */
public class XmlTreeBuilder extends TreeBuilder
{
	// instance variables
	private Document documentRoot;
	//private int treeCount = 0;
	//private Vertex treeRoot = null;
	
	/**
	 * Constructor for objects of class XmlTreeBuilder
	 * @param xmlFilename filename 
	 * @param title title of node
	 */
	public XmlTreeBuilder(String xmlFilename, String title)
	{
		// create a new tree root
		treeRoot = new Vertex(title);
		treeCount=1;
		// read an XML file and generate a DOM document object from it
		if(parse(xmlFilename))
			buildTree(treeRoot, (Node)documentRoot);
	}
	/**
	 * This function builds the tree and returns the number of vertices created.
	 * 
	 * @param  currentTreeNode		tree root
	 * @param  document				document root
	 * @return treeCount			number of vertices created
	 */
	private int buildTree(Vertex currentTreeNode, Node document)
	{
		// get the node list from the document
		NodeList nodeList = getNodeList(document);

		if (nodeList != null)
		{
			//System.out.println("Length: " + nodeList.getLength());
			for (int i = 0; i <nodeList.getLength(); i++)
			{
				// get the node name (database, table, tuple, attrname, attrvalue,...)
				//String nodeName = nodeList.item(i).getNodeName();
				//String nodeName = this.getNodeName(nodeList.item(i));
				Node currentNode = nodeList.item(i);
				//String currentName = currentNode.getNodeName();//TODO: What is this used for ? FIX IT
				String currentID = getAttr(currentNode, "id");
				String des = getDes(currentNode, "exp");

				Vertex childNode = new Vertex(currentID);

				// increment the number of nodes created
				treeCount++;

				// set the description of the node
				childNode.setDesc(des);

				// add the node created to the previous node
				currentTreeNode.add(childNode);

				// recursively create the whole tree
				buildTree(childNode, nodeList.item(i));
			} // end of for loop
		} // end of if nodeList ! = null

		return getTreeCount();
	}

	
	/**
	 * This function returns the nodeList
	 * @param node	the node or vertex
	 * @return NodeList the node list
	 */
	private NodeList getNodeList(Node node)
	{
		try
		{
			NodeList nodeList = XPathAPI.selectNodeList(node, "*");
			if(nodeList != null)
			{
				return nodeList;
			}else{
				return null;
			}
		}catch (Exception e){
			System.err.println("Exception at XmlTreeBuilder:getNodeList(Node): "+ e.getMessage());
			e.printStackTrace();
			displayOptionPaneAndExit("Exception at XmlTreeBuilder:getNodeList(Node): "+ e.getMessage(),"Error");
		}
		
		return null;
	}
	 
	
	/**
	 * This function returns the name of the node
	 * @param node
	 * @return nodeName String
	 */
	/*private String getNodeName(Node node)
	{
		try
		{
			return node.getNodeName();
		}
		catch (Exception e)
		{
			System.err.println("Exception at XmlTreeBuilder:getNodeName(Node): "+ e.getMessage());
			e.printStackTrace();
			displayOptionPaneAndExit("Exception at XmlTreeBuilder:getNodeName(Node): "+ e.getMessage(),"Error");
		}
		return null;
	} */
	/********************************************************************************************/
	/**
	 * This function reads an XML file and generate a DOM document object from it.
	 *
	 * @param  filename   file to be read from
	 * @return boolean if the file is parsed correctly
	 */
	private boolean parse(String filename)
	{
		try	{
			// Create a DocumentBuilderFactory and configure it
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			// Create a DocumentBuilder
			DocumentBuilder db = dbFactory.newDocumentBuilder();

			// Parse the input file to get a Document object
			documentRoot = db.parse(new File(filename));
		}catch(ParserConfigurationException pce){
			System.err.println("ParserConfigurationException: " + pce.getMessage());
			pce.printStackTrace();
			displayOptionPaneAndExit("ParserConfigurationException: " + pce.getMessage(),"ParserConfigurationException");
			return false;
		}catch(SAXException se)	{
			System.err.println("SAXException: " + se.getMessage());
			se.printStackTrace();
			displayOptionPaneAndExit("SAXException: " + se.getMessage(),"SAXException");
			return false;
		}catch(IOException ioe)	{
			System.err.println("IOException: "+ ioe.getMessage());
			ioe.printStackTrace();
			displayOptionPaneAndExit("IOException: "+ ioe.getMessage(),"IOException");
			return false;
		}catch(Exception e)	{
			System.err.println("Parsing Error: "+ e.getMessage());
			e.printStackTrace();
			displayOptionPaneAndExit("Parsing Error: "+ e.getMessage(),"Parsing Error");
			return false;
		}	
		
		return true;
	}

}
