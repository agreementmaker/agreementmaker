package am.application.ontology.ontologyParser;

// JAXP Packages

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import am.application.ontology.Node;
import am.userInterface.vertex.Vertex;

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
	private HashMap<String,Node> processedNodes;
	final static String XMLHIERARCHY = "XML Hierarchy";
	
	/**
	 * Constructor for objects of class XmlTreeBuilder
	 * @param xmlFilename filename 
	 * @param title title of node
	 */
	public XmlTreeBuilder(String xmlFilename, int sourceOrTarget, String language, String format)
	{
		super(xmlFilename, sourceOrTarget, language, format);

		
		// read an XML file and generate a DOM document object from it

	}
	/**
	 * This function builds the tree and returns the number of vertices created.
	 * 
	 * @param  currentTreeNode		tree root
	 * @param  document				document root
	 * @return treeCount			number of vertices created
	 */
	protected void buildTree()
	{
		// create a new tree root
		treeRoot = new Vertex(ontology.getTitle(), ontology.getSourceOrTarget());
		Vertex ClassRoot = new Vertex(XMLHIERARCHY, ontology.getSourceOrTarget());
		

		treeCount=2;
		processedNodes = new HashMap<String, Node>();
		if(parse(ontology.getFilename())){
			createTree(ClassRoot, documentRoot);
		}
		treeRoot.add(ClassRoot);
		ontology.setClassesTree( ClassRoot);
	}
	
	protected void createTree(Vertex currentTreeNode, org.w3c.dom.Node document){
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
				org.w3c.dom.Node currentNode = nodeList.item(i);
				//String currentName = currentNode.getNodeName();//TODO: What is this used for ? FIX IT
				String name = getAttr(currentNode, "id");
				String des = getAttr(currentNode, "exp");
				String label = getAttr(currentNode, "label");
				String seeAlso = getAttr(currentNode,"seeAlso");
				String isDefBy = getAttr(currentNode,"isDefinedBy");
				Vertex childNode = new Vertex(name,ontology.getSourceOrTarget());
				childNode.setDesc(label);
				//We have to check if it is a new node or a previous processed node in a different position
				Node node = processedNodes.get(name);
				if(node == null) {
					//if it's new create the node, add it to the class list and incr uniqueKey
					node = new Node(uniqueKey,name, Node.XMLNODE);
					node.setLabel(label);
					node.setComment(des);
					node.setSeeAlsoLabel(seeAlso);
					node.setIsDefinedByLabel(isDefBy);
					ontology.getClassesList().add(node); //THE XML FILES ONLY CONTAINS CLASSES IN OUR SEMPLIFICATION
					uniqueKey++;
					processedNodes.put(name, node);
				}
				node.addVertex(childNode);
				childNode.setNode(node);
				// increment the number of nodes created
				treeCount++;
				// add the node created to the previous node
				currentTreeNode.add(childNode);
				// recursively create the whole tree
				createTree(childNode, nodeList.item(i));
			} // end of for loop
		} // end of if nodeList ! = null
	}

	
	/**
	 * This function returns the nodeList
	 * @param node	the node or vertex
	 * @return NodeList the node list
	 */
	private NodeList getNodeList(org.w3c.dom.Node node)
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
	public String getAttr(org.w3c.dom.Node node, String attrName)
	{
		return ((Element)node).getAttribute(attrName);
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
	 * This function sets the document root
	 *
	 * @param root root of the document
	 */
	public void setDocumentRoot(Document root)
	{
		documentRoot = root;
	}
}
