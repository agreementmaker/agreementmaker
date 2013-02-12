package am.app.ontology.ontologyParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * <p>Title: </p>
 *
 * <p>Description: Ontology to Tree Builder</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ADVIS LAB</p>
 *
 * @author Nalin Makar
 * @version 1.5
 */
public class RdfsTreeBuilder extends TreeBuilder{
	
	final static String RDFCLASSROOTNAME = "RDFS Classes Hierarchy";
	final static String RDFPROPERTIESROOTNAME = "RDFS Properties Hierarchy";
	
	//instance variables
	private OntModel ontModel;
	private HashMap<OntResource,Node> processedSubs;
	
	/*This variable has been introduced to solve a problem occurred loading OAEI test case ontology in RDF/XML format
	 * These ontologies contains some referenced classes and properties of other namespaces
	 * These classes shouldn't be considered in the matching and we don't want to load them. 
	 * The parser will skip to load this classes if this boolean variable is true, so it has to be true at least for RDF/XML format
	 * In the N3 format can't be true because the namespace organization is different 
	 * ATTENTION: we skip from loading the referenced classes but their sons in the hierarchy may be valid classes. 
	 */
	private boolean skipOtherNamespaces;
	/* To get the namespace of the loaded ontologies we use the method model.getNsPrefixMapping.get("")
	 * This method cannot be used with "" input for N3
	 */
	private String ns = null;
	
	public RdfsTreeBuilder(String fileName, String language, String format,boolean skip) {
		super(fileName, language, format);
		skipOtherNamespaces = skip;
		
	}
	
	protected void buildTree() {
		
		try {
			
			System.out.print("Reading Model...");
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM, null);
			//TODO: Figure out if the 2nd arg in next fn call should be null or someother URI
			
			File ontFile = new File(ontology.getFilename());
			FileInputStream fileInStream = new FileInputStream(ontFile);
			ontModel.read(fileInStream, null); // null == RDF/XML
			System.out.println("done");
			
			ontology = new Ontology(ontModel);
			
			if(skipOtherNamespaces) { //we can get this information only if we are working with RDF/XML format, using this on N3 you'll get null pointer exception you need to use an input different from ""
				try {//if we can't access the namespace of the ontology we can't skip nodes with others namespaces
					ns = ontModel.getNsPrefixMap().get("").toString();
				}
				catch(Exception e) {
					skipOtherNamespaces = false;
				}
			}
			ontology.setSkipOtherNamespaces(skipOtherNamespaces);

			//treeRoot = new Vertex(ontology.getTitle(),ontology.getTitle(),ontModel,  ontology.getSourceOrTarget());//Creates the root of type Vertex for the tree, is a fake vertex with no corresponding node
			treeRoot = new AMNode((Resource)null, -1, ontology.getTitle(), AMNode.RDFNODE, ontology.getID());
			//Vertex classRoot = new Vertex(RDFCLASSROOTNAME,RDFCLASSROOTNAME,ontModel,  ontology.getSourceOrTarget());
			Node classRoot = new AMNode((Resource)null, -1, RDFCLASSROOTNAME, AMNode.OWLCLASS, ontology.getID());
			processedSubs = new HashMap<OntResource, Node>();
			ExtendedIterator<OntClass> i = ontModel.listHierarchyRootClasses();
			classRoot = createTree(classRoot, i);//should add all valid classes and subclasses in the iterator to the classRoot
			ontology.setOntResource2NodeMap( processedSubs, alignType.aligningClasses );
			treeRoot.addChild(classRoot);
			treeCount = 2;
			ontology.setClassesRoot(classRoot);
			
			
			
			
			uniqueKey = 0; //restart the key because properties are kept in a different structure with different index
	        processedSubs = new HashMap<OntResource, Node>();
	        
			//Vertex propertiesRoot = new Vertex(RDFPROPERTIESROOTNAME, RDFPROPERTIESROOTNAME, ontModel, ontology.getSourceOrTarget());
	        Node propertiesRoot = new AMNode((Resource)null, -1, RDFPROPERTIESROOTNAME, AMNode.OWLPROPERTY, ontology.getID() );
			propertiesRoot = createPropertiesTree( propertiesRoot, listHierarchyRootProperties(ontModel) );
			ontology.setOntResource2NodeMap( processedSubs, alignType.aligningClasses );
			
			treeRoot.addChild(propertiesRoot);
			treeCount++;
			
			ontology.setPropertiesRoot(propertiesRoot);
			
			
			
			if( progressDialog != null ) {
				progressDialog.appendLine("Building visualization graphs.");
				Core.getUI().getCanvas().buildLayoutGraphs(ontology);
			} 
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
	}
	
	private ArrayList<OntProperty> listHierarchyRootProperties(OntModel m) {
		ArrayList<OntProperty> roots = new ArrayList<OntProperty>();
		ExtendedIterator<OntProperty> itobj = m.listOntProperties();
    	
    	while( itobj.hasNext() ) {  // look through all the properties
    		Property rdfproperty = (Property) itobj.next();
    		if( !rdfproperty.canAs(OntProperty.class) ) continue; // not interested in RDF properties (todo?)
    		OntProperty property = rdfproperty.as(OntProperty.class);

    		try {
	    		
	    		boolean isRoot = true;
	    		
	    		ExtendedIterator<? extends OntProperty> superPropItr = property.listSuperProperties();
	    		while( superPropItr.hasNext() ) {
	    			OntProperty superProperty = (OntProperty) superPropItr.next();
	    			
	    			if( !property.equals(superProperty) && !superProperty.isAnon() ) {
	    				// this property has a valid superclass, therefore it is not a root property
	    				superPropItr.close();
	    				isRoot = false;
	    				break;
	    			}
	    		}
	    		
	    		if( isRoot ) roots.add(property);
    		} catch( ConversionException e) {
    			roots.add(property);
    			continue;
    		}
    		
		}
    	
    	return roots;
	}
	
	protected Node createTree(Node root, ExtendedIterator<OntClass> i){
		try {
			while (i.hasNext()) {
				OntClass cls = i.next();
				if( cls.isAnon() ) continue; //skip
				
			    if(skipOtherNamespaces && !cls.getNameSpace().toString().equals(ns)) {
	          	   //If a node has a different namespace must be jumped , so sons of that node must be added to the grandfather
	          	   ExtendedIterator<OntClass> moreSubs = cls.listSubClasses( true );
	          	   root = createTree(root, moreSubs);       	   
	            }
			    else {
					Node newNode = createNode(cls, true);
					ExtendedIterator<OntClass> subs = cls.listSubClasses( true );
					newNode = createTree(newNode, subs);
					root.addChild(newNode);
					treeCount++;
				}
			}
		}
		catch( ConversionException cexc ) {
			// This conversion exception is thrown by 
			cexc.printStackTrace();
		}
		return root;
	}
	
	protected Node createPropertiesTree(Node root, List<? extends OntProperty> subChildren ) {
		
		for( OntProperty prop : subChildren ) {
			if( prop.isAnon() ) continue; // skip anonymous properties
			else if( skipOtherNamespaces && !prop.getNameSpace().toString().equals(ns) ) {
				// we jump nodes of different name spaces.
				ExtendedIterator<? extends OntProperty> moreSubProperties = prop.listSubProperties(true);
				root = createPropertiesTree( root, moreSubProperties.toList() );
			} 
			else {
				Node newNode = createNode( prop, false );
				ExtendedIterator<? extends OntProperty> subProperties = prop.listSubProperties(true);
				newNode = createPropertiesTree(newNode, subProperties.toList() );
				root.addChild(newNode);
				treeCount++;
			}
		}
		
		return root;
	}
	
	
	public Node createNode( OntResource entity, boolean isClass) {
		Node node;
		if( processedSubs.containsKey( entity ) ) {//the node has been already created, but we need only to create a new vertex;
			node = processedSubs.get(entity); //reuse of the previous Node information for this class, but we need a new Vertex
		}
		else { 
			if(isClass) {
				node = new AMNode(uniqueKey,entity, AMNode.OWLCLASS, ontology.getID()); //new node with a new key, with the link to the graphical Vertex representation
				ontology.getClassesList().add(node);
			}
			else {//it has to be a prop
				node = new AMNode(uniqueKey,entity, AMNode.OWLPROPERTY, ontology.getID()); //new node with a new key, with the link to the graphical Vertex representation
				ontology.getPropertiesList().add(node);
			}
			processedSubs.put(entity, node);
			uniqueKey++;  // uniqueKey starts from 0, then gets incremented.
		}
		return node;
	}
	
	/**
	 * Create a TreeNode for the given class
	 * This method is a builder for the pair Node Vertex
	 * Nodes are element to be aligned, the structure keeps all info needed for matchings
	 * Vertex it's the graphical rapresentation. They must be created together.
	 * The node has a reference to the vertex (a node can have more than one vertex if Class has more fathers in the hierarchy)
	 * The vertex has a reference to the node.
	 * Each node is identified by a uniqueKey used as index in the ClassList structure
	 * @param entity
	 * @return
	 * 
	 * TODO: Remove this commented out code and copy the documentation over to the classes that replaced this code. - Cosmin.
	 */
	/*@Deprecated
	public Vertex createNodeAndVertex(OntResource entity, boolean isClass, int sourceOrTarget) {
		 
		 Node node;
         if( processedSubs.containsKey( entity ) ) {//the node has been already created, but we need only to create a new vertex;
         	node = processedSubs.get(entity); //reuse of the previous Node information for this class, but we need a new Vertex
         }
         else { 
        	if(isClass) {
                 node = new Node(uniqueKey,entity, Node.OWLCLASS, ontology.getID()); //new node with a new key, with the link to the graphical Vertex representation
                 ontology.getClassesList().add(node);
          	}
          	else {//it has to be a prop
          		node = new Node(uniqueKey,entity, Node.OWLPROPERTY, ontology.getID()); //new node with a new key, with the link to the graphical Vertex representation
                 ontology.getPropertiesList().add(node);
          	}
             processedSubs.put(entity, node);
             uniqueKey++;  // uniqueKey starts from 0, then gets incremented.
          }
 	 
	// uniqueKey == 0 here, no need to increment it.
		node = new Node(uniqueKey,entity, Node.OWLCLASS, ontology.getIndex()); //new node with a new key, with the link to the graphical Vertex representation
    ontology.getClassesList().add(node);
    processedSubs.put(entity, node);
    uniqueKey++;  // here is where we increment the uniqueKey.
    
	
         Vertex vert = new Vertex(node.getLocalName(), entity.getURI(), ontModel, sourceOrTarget);
         node.addVertex(vert);
         vert.setNode(node);
		 return vert;
	}*/
}