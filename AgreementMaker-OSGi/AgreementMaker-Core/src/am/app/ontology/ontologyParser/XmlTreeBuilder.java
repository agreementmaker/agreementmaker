package am.app.ontology.ontologyParser;

// JAXP Packages

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * XmlTreeBuilder Class -
 * 
 * This class parses the schema represented as XML file using Xalan and Xerces
 * Java API's and builds the tree structure.
 * 
 * FIXME: This class DOES NOT WORK currently. It's in bad need of a complete
 *        overhaul - Cosmin, Oct. 22, 2013
 * 
 * @author ADVIS Research Laboratory
 * @version 11/13/2004
 */
public class XmlTreeBuilder extends TreeBuilder<OntologyDefinition>
{
	// instance variables
	private Document documentRoot;
	private HashMap<String,Node> processedNodes;
	final static String XMLHIERARCHY = "XML Hierarchy";
	
	OntModel m;
	OntClass owlThing;
	private HashMap<OntResource, Node> processedSubs;
	
	/**
	 * Constructor for objects of class XmlTreeBuilder
	 * @param xmlFilename filename 
	 * @param title title of node
	 */
	public XmlTreeBuilder(OntologyDefinition definition)
	{
		super(definition);
		
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
		
		// Read the XML file and create a Jena OWL Model from it.
		
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		owlThing = m.getOntClass( OWL.Thing.getURI() );
		
		ontology.setModel(m);
		
		// create a new tree root
		//treeRoot = new Vertex(ontology.getTitle(), ontology.getSourceOrTarget());
		treeRoot = new AMNode((Resource)null, -1, ontology.getTitle(), AMNode.XMLNODE, ontology.getID() );
		Node ClassRoot = new AMNode((Resource)null, -1 , XMLHIERARCHY, AMNode.XMLNODE, ontology.getID() );
		//ClassRoot.setOntModel(m);
		
		Node rootNode = new AMNode(owlThing, uniqueKey,"OWL:Thing", AMNode.XMLNODE, ontology.getID());
		uniqueKey++;
		rootNode.setLabel("OWL:Thing");
		//ClassRoot.setNode(rootNode);
		

		treeCount=2;
		processedNodes = new HashMap<String, Node>();
		processedSubs = new HashMap<OntResource, Node>();
		if(parse(ontology.getFilename())){
			/*
			OntClass currentClass = m.createClass( name );
			currentClass.setLabel(label, null);
			currentClass.setComment(des, null);
			*/
			
			//OntClass owlThing = m.getOntClass( OWL.Thing.getURI() );
			
		
			
			createTree(ClassRoot, documentRoot);
		}
		treeRoot.addChild(ClassRoot);
		ontology.setClassesRoot( ClassRoot);
		ontology.setOntResource2NodeMap(processedSubs, alignType.aligningClasses);
	}
	
	protected void createTree(Node parentNode, org.w3c.dom.Node document){
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
				org.w3c.dom.Node currentXMLNode = nodeList.item(i);
				//String currentName = currentNode.getNodeName();//TODO: What is this used for ? FIX IT
			
				//String name = currentXMLNode.getNodeName();
				String name = getAttr(currentXMLNode, "id");
				System.out.println(name);
				String des = getAttr(currentXMLNode, "comment");
				String label = getAttr(currentXMLNode, "label");
				String seeAlso = getAttr(currentXMLNode,"seeAlso");
				String isDefBy = getAttr(currentXMLNode,"isDefinedBy");
				
				//Vertex currentVertex = new Vertex(name,ontology.getSourceOrTarget());
				//currentVertex.setOntModel(m);
				//currentVertex.setDesc(des);
				//We have to check if it is a new node or a previous processed node in a different position
				Node currentNode = processedNodes.get(name);
				OntClass currentClass;
				if(currentNode == null) {
					//if it's new create the node, add it to the class list and incr uniqueKey
					if( name.equals("") ) {
						currentClass = m.createClass( "#genid"+ uniqueKey);
					} else {
						currentClass = m.createClass( "#" + name );
					}
					currentClass.setLabel(label, null);
					
					//System.out.println("Localname: " +currentClass.getLocalName());
					currentClass.setComment(label, null);
					
					currentNode = new AMNode(currentClass, uniqueKey, name, AMNode.XMLNODE, ontology.getID());

					processedSubs.put( ((OntResource)currentClass) ,currentNode);
					currentNode.setLabel(label);
					currentNode.setComment(des);
					currentNode.setSeeAlsoLabel(seeAlso);
					currentNode.setIsDefinedByLabel(isDefBy);
					ontology.getClassesList().add(currentNode); //THE XML FILES ONLY CONTAINS CLASSES IN OUR SEMPLIFICATION
					uniqueKey++;
					processedNodes.put(name, currentNode);
				} else {
					currentClass = (OntClass) currentNode.getResource();
				}
				//currentNode.addVertex(currentVertex);
				//currentVertex.setNode(currentNode);
				// increment the number of nodes created
				treeCount++;
				// add the node created to the previous node
				
				OntClass parentClass = (OntClass) parentNode.getResource();
				parentClass.addSubClass(currentClass);
				
				// recursively create the whole tree
				createTree(currentNode, nodeList.item(i));
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
			// FIXME: XPathAPI came from the UMLS Library, but that conflicted with GATE.
			NodeList nodeList = null; //XPathAPI.selectNodeList(node, "*");
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
