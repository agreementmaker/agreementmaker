package am.app.ontology.ontologyParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.AMNode;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

public class TabbedTextBuilder extends TreeBuilder<OntologyDefinition> {
	// instance variables
	private HashMap<String,Node> processedNodes;
	final static String XMLHIERARCHY = "XML Hierarchy";
	
	OntModel m;
	OntClass owlThing;
	private HashMap<OntResource, Node> processedSubs;
	
	public TabbedTextBuilder(OntologyDefinition definition) {
		super(definition);
	}

	@Override
	protected void buildTree() {
		// Read the tabbed txt file and create a Jena OWL Model from it.
		
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		owlThing = m.getOntClass( OWL.Thing.getURI() );
		
		ontology.setModel(m);
		
		// create a new tree root
		//treeRoot = new Vertex(ontology.getTitle(), ontology.getSourceOrTarget());
		treeRoot = new AMNode((Resource)null, -1, ontology.getTitle(), AMNode.RDFNODE, ontology.getID());
		//Vertex ClassRoot = new Vertex(XMLHIERARCHY, ontology.getSourceOrTarget());
		Node ClassRoot = new AMNode((Resource)null, -1 , XMLHIERARCHY, AMNode.XMLNODE, ontology.getID() );
		//ClassRoot.setOntModel(m);
		
		Node rootNode = new AMNode(owlThing, uniqueKey,"OWL:Thing", AMNode.XMLNODE, ontology.getID());
		uniqueKey++;
		rootNode.setLabel("OWL:Thing");
		//ClassRoot.setNode(rootNode);
		

		treeCount=2;
		processedNodes = new HashMap<String, Node>();
		processedSubs = new HashMap<OntResource, Node>();
		
		//check to see if the file was read w/o errors
		File f=new File(ontology.getFilename());
		try {
			Scanner scanIn= new Scanner(f);
			TNode head;
			
			if( scanIn.hasNext() )
			{
				//head=new TNode(scanIn.nextLine(),null);
				head= new TNode(null,null);
				System.out.println(head.name);
				parse(scanIn, head, 0);
				
				//create the tree
				//put an if statment here asking for if there is children or if the 'head' is empty
				createTree(ClassRoot, head);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		treeRoot.addChild(ClassRoot);
		ontology.setClassesRoot( ClassRoot);
		ontology.setOntResource2NodeMap(processedSubs, alignType.aligningClasses);
		
	}
		protected void createTree(Node parentNode, TNode document){
			
			//System.out.println("Length: " + nodeList.getLength());
			for (int i = 0; i < document.getNumOfChildren(); i++)
			{
				TNode currentTNode = document.getChild(i);

				String name = currentTNode.getName();
				System.out.println(name);
				
				//Vertex currentVertex = new Vertex(name,ontology.getSourceOrTarget());
				//currentVertex.setOntModel(m);
				//We have to check if it is a new node or a previous processed node in a different position
				Node currentNode = processedNodes.get(name);
				OntClass currentClass;
				if(currentNode == null) {
					//if it's new create the node, add it to the class list and incr uniqueKey
					currentClass = m.createClass( "#genid"+ uniqueKey);
					currentClass.setLabel(name, null);
					
					System.out.println("Localname: " +currentClass.getLocalName());
					
					
					currentNode = new AMNode(currentClass, uniqueKey,name, AMNode.XMLNODE, ontology.getID());
					
					processedSubs.put( ((OntResource)currentClass) ,currentNode);
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
				parentNode.addChild(currentNode); // adds a child.
				
				OntClass parentClass = (OntClass) parentNode.getResource();
				if( parentClass != null ) parentClass.addSubClass(currentClass);
				
				
				// recursively create the whole tree
				createTree(currentNode, currentTNode);
			} // end of for loop
		}

	public void parse(Scanner sc, TNode current, int currentTabLevel)
	{
		String scIn;
		//check if sc has another line
		//System.out.println(currentTabLevel);
		//System.out.println("done");
		if( sc.hasNext() )
		{
			//read the line in
			scIn=sc.nextLine();
			
			int numOfTabs=scIn.lastIndexOf('\t')+1;//count the number of tabs
			//System.out.println(numOfTabs);
			if( numOfTabs == currentTabLevel)
			{
				//current.add(new TNode(scIn.substring(numOfTabs+1),current));// add the child
				current.add(new TNode(scIn.substring(numOfTabs),current));// add the child
				System.out.println(scIn);
				parse(sc, current.getChildren().get(current.getChildren().size()-1),currentTabLevel+=1);
			}
			else if( numOfTabs == currentTabLevel-1)
			{
				//first go up a level
				current=current.getParent();
				currentTabLevel--;
				
				//add a new child to the parent and keep going
				//current.add(new TNode(scIn.substring(numOfTabs+1),current));
				current.add(new TNode(scIn.substring(numOfTabs),current));// add the child
				System.out.println(scIn);
				parse(sc, current.getChildren().get(current.getChildren().size()-1),currentTabLevel+=1);
			}
			else if( numOfTabs == currentTabLevel-2)
			{
				//go up two levels
				current=current.getParent().getParent();
				currentTabLevel-=2;
				
				//current.add(new TNode(scIn.substring(numOfTabs+1),current));
				current.add(new TNode(scIn.substring(numOfTabs),current));// add the child
				System.out.println(scIn);
				parse(sc, current.getChildren().get(current.getChildren().size()-1),currentTabLevel+=1);
			}
		}
	}
	/**
	 * This function displays the JOptionPane with title and descritpion
	 * @param desc the description to display in the option pane
	 * @param title the title to display on the option pane
	 */
	public void displayOptionPaneAndExit(String desc, String title)
	{
		JOptionPane.showMessageDialog(null,desc+"\nFatal Error: Application will be closed.",title, JOptionPane.PLAIN_MESSAGE);					
	}	
	
	
	class TNode
	{
		String name;
		ArrayList<TNode> children= new ArrayList<TNode>();
		TNode parent;
		public TNode(String name, TNode parent)
		{
			this.name=name;
			this.parent=parent;
		}
		public void add(TNode newChild)
		{
			children.add( newChild );
		}
		public String getName()
		{
			return name;
		}
		public int getNumOfChildren()
		{
			return children.size();
		}
		public ArrayList<TNode> getChildren()
		{
			return children;
		}
		public TNode getChild(int index)
		{
			if( index < getNumOfChildren())
				return children.get(index);
			return null;
		}
		public TNode getParent()
		{
			return parent;
		}
	}
}

