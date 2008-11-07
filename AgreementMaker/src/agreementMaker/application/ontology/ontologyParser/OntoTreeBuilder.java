package agreementMaker.application.ontology.ontologyParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.QNameProvider;


import agreementMaker.application.ontology.Node;
import agreementMaker.application.ontology.Ontology;
import agreementMaker.userInterface.vertex.Vertex;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.function.library.namespace;
import com.hp.hpl.jena.vocabulary.OWL;

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
public class OntoTreeBuilder extends TreeBuilder{
	
	
	//instance variables

	private OntModel model;
	private Set unsatConcepts;  
	private HashMap<OntResource,Node> processedSubs;
	OntClass owlThing;
	
	final static String CLASSROOTNAME = "OWL Classes Hierararchy";
	final static String PROPERTYROOTNAME = "OWL Properties Hierararchy";

	
	/*This variable has been introduced to solve a problem occurred loading OAEI test case ontology in RDF/XML format
	 * These ontologies contains some referenced classes and properties of other namespaces
	 * These classes shouldn't be considered in the matching and we don't want to load them. 
	 * The parser will skip to load this classes if this boolean variable is true, so it has to be true at least for RDF/XML format
	 * In the N3 format can't be true because the namespace organization is different 
	 * ATTENTION: we skip from loading the referenced classes but their sons in the hierarchy may be valid classes. 
	 */
	private boolean skipOtherNamespaces = true;
	/* To get the namespace of the loaded ontologies we use the method model.getNsPrefixMapping.get("")
	 * This method cannot be used with "" input for N3
	 */
	private String ns = null;
	
	/**
	 * Builds an ontology with list of classes, list of properties, classes tree and properties tree, all information are kept in the ontology istance
	 * @param fileName
	 * @param syntaxIndex
	 * @param sourceOrTarget
	 */
	public OntoTreeBuilder(String fileName, int sourceOrTarget, String language, String format) {
		super(fileName, sourceOrTarget, language, format); 
		treeCount = 0;
		
		System.out.print("Reading Model...");
		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		
		
		//TODO: Figure out if the 2nd arg in next fn call should be null or someother URI
		model.read( "file:"+fileName, null, ontology.getFormat() );
		System.out.println("done");
		
		if(skipOtherNamespaces) { //we can get this information only if we are working with RDF/XML format, using this on N3 you'll get null pointer exception you need to use an input different from ""
			try {//if we can't access the namespace of the ontology we can't skip nodes with others namespaces
				ns = model.getNsPrefixMap().get("").toString();
			}
			catch(Exception e) {
				skipOtherNamespaces = false;
			}
		}

		
		
		//Preparing model
		model.prepare();
		
		// compute the classification tree
		System.out.print("Classifying...");
		((PelletInfGraph) model.getGraph()).getKB().classify();
		//reasoner.classify();
		System.out.println("done");

		ontology.setModel(model);
		buildTree();
		
	}
	
	/**
	 * Create a root node for the given concepts and add child nodes for
	 * the subclasses. Return null for owl:Nothing 
	 * 
	 * @param concepts
	 * @return
	 */
	void buildTree() {
		// Use OntClass for convenience
        owlThing = model.getOntClass( OWL.Thing.getURI() );
        OntClass owlNothing = model.getOntClass( OWL.Nothing.getURI() );

        // Find all unsatisfiable concepts, i.e classes equivalent
        // to owl:Nothing. we are not considering this nodes for the alignment so we are not keeping this
        unsatConcepts = collect( owlNothing.listEquivalentClasses() );
        // create a tree starting with owl:Thing node as the root
        //if a node has two fathers (is possible in OWL hierarchy) it would be processed twice
        //but we don't want to add two times the node to the hierarchy, so processedSubs won't be processed again.
        processedSubs = new HashMap<OntResource, Node>();
        

        //The root of the tree is a fake vertex node, just containing the name of the ontology,
        treeRoot = new Vertex(ontology.getTitle(),ontology.getTitle(), model );
        treeCount++;
        //the root has 2 fake sons Classes and Properties
        Vertex classRoot = createClassTree(owlThing);
        treeRoot.add(classRoot);
        ontology.setClassesTree( classRoot);
        Vertex propertyRoot = createPropertyTree();
        treeRoot.add(propertyRoot);
        ontology.setClassesTree( propertyRoot);
	}
	
    public Set collect( Iterator i ) {
        Set set = new HashSet();
        while( i.hasNext() ) {
            OntResource res = (OntResource) i.next();
            if( res.isAnon() )
                continue;
            
            set.add( res );
        }
        
        return set;
    }
    
    /**
     * Create a root node for the given concepts and add child nodes for the subclasses. Return null
     * for owl:Nothing
     * 
     * @param concepts
     * @return
     */
    public Vertex createClassTree( OntClass cls ) {
    	if( unsatConcepts.contains( cls ) )
            return null;
    	Vertex root;
    	if(cls.equals(owlThing))//fake vertex with written "OWL Class hierarchy"
    		root = new Vertex(CLASSROOTNAME, CLASSROOTNAME, model);
    	else root = createNodeAndVertex(cls);//normal vertex 
		treeCount++;
		
       // If one of the sons of this class is a classes with different namespace
	   // we have to skip it, but we may still have to load his sons.
		// in that case we won't have only one iterator but more then one.
		//in all other cases iterators list will contain only the iterator on sons of this class
        ArrayList<Iterator> iterators = new ArrayList<Iterator>();
        // get only direct subclasses (true)
        Iterator firstSubs = cls.listSubClasses( true );
        iterators.add(firstSubs);
        for(int i = 0; i<iterators.size(); i++) {
        	Iterator subs = iterators.get(i);
            while( subs.hasNext() ) {
                OntClass sub = (OntClass) subs.next();

                if( sub.isAnon() )
                    continue;
                //skip non valid classes with different namespace but consider sons
                if(skipOtherNamespaces && !sub.getNameSpace().toString().equals(ns)) {
             	   // get only direct subclasses (true)
             	   Iterator moreSubs = sub.listSubClasses( true );
             	   iterators.add(moreSubs);
             	   continue;
                }
                Vertex vert = createClassTree(sub);
                if( vert != null ) {
                    root.add( vert );
            	}
            }
        }
        return root;
    }
	
	/**
	 * Create a TreeNode for the given class
	 * This method is a builder for the pair Node Vertex
	 * Nodes are element to be aligned, the structure keeps all info needed for matchings
	 * Vertex it's the graphical rapresentation. They must be created together.
	 * The node has a reference to the vertex (a node can have more than one vertex if Class has more fathers in the hierarchy)
	 * The vertex has a reference to the node.
	 * Each class is identified by a uniqueKey used as index in the ClassList structure
	 * Each prop is identified by a uniqueKey used as index in the propList structure
	 * @param entity
	 * @return
	 */
	public Vertex createNodeAndVertex(OntResource entity) {
		 
		 Node node;
         if( processedSubs.containsKey( entity ) ) {//the node has been already created, but we need only to create a new vertex;
         	node = processedSubs.get(entity); //reuse of the previous Node information for this class, but we need a new Vertex
         }
         else {
         	uniqueKey++;
         	if(entity.canAs(OntProperty.class)) {//depending if it's a class or property, ATTENTION THERE IS A JENA BUG AVOIDED HERE, ONTPROPERTY.CANAS(ONTCLASS.CLASS) RETURN TRUE, IS IMPORTANT TO CHECK CANAS WITH PROPERTY IN THE IF 
         		node = new Node(uniqueKey,entity, Node.OWLPROPERTY); //new node with a new key, with the link to the graphical Vertex representation
                ontology.getPropertiesList().add(node);
         	}
         	else {//it has to be a class
         		node = new Node(uniqueKey,entity, Node.OWLCLASS); //new node with a new key, with the link to the graphical Vertex representation
                ontology.getClassesList().add(node);
         	}
            processedSubs.put(entity, node);
         }
         Vertex vert = new Vertex(node.getLocalName(), entity.getURI(), model);
         node.addVertex(vert);
         vert.setNode(node);
		 return vert;
	}
	

    public Vertex createPropertyTree() {
    	
    	Vertex root = new Vertex(PROPERTYROOTNAME, PROPERTYROOTNAME, model);
    	treeCount++;
        uniqueKey = 0; //restart the key because properties are kept in a differnt structure with different index
        processedSubs = new HashMap();
    	Iterator itobj = model.listObjectProperties();
    	Iterator itdata = model.listDatatypeProperties();
    	while (itobj.hasNext() || itdata.hasNext() ) {//scan objprop first and then dataprop
    		OntProperty p;
    		if(itobj.hasNext()) {
        		p = (OntProperty) itobj.next();
    		}
    		else {
    			p = (OntProperty) itdata.next();
    		}

    		boolean skip = false;
    		//We need to find only valid properties, and they must be root so 
    		//there shouldn't be any valid properties between the superproperties
    		if(p.isAnon()) {
    			skip = true;
    		}
    		else if(skipOtherNamespaces && !p.getNameSpace().toString().equals(ns)) {
    			skip = true;
    		}
    		//check if there is any valid property between the superproperties, i need to check all superproperties hierarchy so i can't use listProp(true), doing this there will always be a the property itself in the list
    		Iterator it2 = p.listSuperProperties();
    		while(it2.hasNext()) {
    			OntProperty superp = (OntProperty)it2.next();
    			if(!p.equals(superp) && !superp.isAnon() && !(skipOtherNamespaces && !superp.getNameSpace().toString().equals(ns))){//if we find a valid father in the superclass hierarchy we skip this property because is not a root
    				skip = true;
    				break;
    			}
    		}
    		if(!skip) {
        		Vertex vp = createPropertySubTree(p);
        		root.add(vp);    			
    		}

    	}
        return root;
    }
    
    public Vertex createPropertySubTree( OntProperty p ) {
    	Vertex root = createNodeAndVertex(p);
		treeCount++;
		
       // If one of the sons of this prop is a prop with different namespace
	   // we have to skip it, but we may still have to load his sons.
		// in that case we won't have only one iterator but more then one.
		//in all other cases iterators list will contain only the iterator on sons of this prop
        ArrayList<Iterator> iterators = new ArrayList<Iterator>();
        // get only direct subproperties (direct = true)
        Iterator firstSubs = p.listSubProperties( true );
        iterators.add(firstSubs);
        for(int i = 0; i<iterators.size(); i++) {
        	Iterator subs = iterators.get(i);
            while( subs.hasNext() ) {
                OntProperty sub = (OntProperty) subs.next();

                if( sub.isAnon() )
                    continue;
                //skip non valid classes with different namespace but consider sons
                if(skipOtherNamespaces && !sub.getNameSpace().toString().equals(ns)) {
             	   // get only direct subclasses (true)
             	   Iterator moreSubs = sub.listSubProperties( true );
             	   iterators.add(moreSubs);
             	   continue;
                }
                Vertex vert = createPropertySubTree(sub);
                root.add( vert );
            }
        }
        return root;
    }
}