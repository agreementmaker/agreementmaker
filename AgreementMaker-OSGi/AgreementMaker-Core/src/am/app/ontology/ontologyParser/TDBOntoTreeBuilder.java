package am.app.ontology.ontologyParser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.utility.RunTimer;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
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
public class TDBOntoTreeBuilder extends TreeBuilder<OntologyDefinition> {
	
	
		// Profile definitions. Used in loading ontologies in different ways
		public enum Profile {
			defaultProfile,  // Pellet reasoner
			noReasoner  // no reasoner at all.
		}

	//instance variables
	private URI ontURI = null;
	private boolean noReasoner = false;
	private OntModel model;
	private Set<OntResource> unsatConcepts;  
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
	private boolean skipOtherNamespaces;
	/* To get the namespace of the loaded ontologies we use the method model.getNsPrefixMapping.get("")
	 * This method cannot be used with "" input for N3
	 */
	private String ns = null;
	private boolean onDisk;
	private String onDiskDirectory;
	private boolean onDiskPersistent;
	
	public TDBOntoTreeBuilder( OntologyDefinition odef ) {
		super(odef);
		
		skipOtherNamespaces = false;
		noReasoner = true;
		treeCount = 0;
		this.onDisk = odef.onDiskStorage;
		this.onDiskDirectory = odef.onDiskDirectory;
		this.onDiskPersistent = odef.onDiskPersistent;
	}
	
	/**
	 * Create a root node for the given concepts and add child nodes for
	 * the subclasses. Return null for owl:Nothing 
	 * 
	 * @param concepts
	 * @return
	 */
	@Override
	protected void buildTree() throws Exception {
		if( noReasoner ) {
			buildTree( OntoTreeBuilder.Profile.noReasoner );
		} else {
			buildTree( OntoTreeBuilder.Profile.defaultProfile );
		}
		
	}
	
	// this function dispatches functions depending on the ontology loading profile selected.
	protected void buildTree( OntoTreeBuilder.Profile prof ) throws Exception {
		
		RunTimer timer = new RunTimer().start();
		
		fireEvent(ProgressEvent.CLEAR_LOG);
		fireEvent(ProgressEvent.APPEND_LINE, "Reading the ontology...");
		
		switch ( prof ) {
		
		case defaultProfile:
			buildTreeDefault();
			break;
		case noReasoner:
			buildTreeNoReasoner();
			break;
		default:
			buildTreeDefault();
			break;
		}		
		
		timer.stop();
		fireEvent(ProgressEvent.APPEND_LINE, "Done. " + timer.getFormattedRunTime());
		fireEvent(ProgressEvent.ONTOLOGY_LOADED);
	}
	
	
	protected void buildTreeDefault() throws Exception {
		// used to run the reasoner when loading the ontologies.
		buildTreeNoReasoner();
	}
	
	protected void buildTreeNoReasoner() throws Exception {
		
		if( ontURI == null ) {
			ontURI = new File(ontology.getFilename()).toURI();
		}

		//Preferences p = Preferences.userNodeForPackage(OnDiskLocationDialog.class);
		
		File directory = new File(onDiskDirectory);
		if( !directory.exists() || !directory.isDirectory() ) { throw new Exception("Path must be an existing directory."); }
		
		fireEvent(ProgressEvent.APPEND_LINE, "Creating Jena TDB Model ... ");
		Model basemodel = TDBFactory.createModel( directory.getAbsolutePath() );
		fireEvent(ProgressEvent.APPEND_LINE, "done.");
		
		fireEvent(ProgressEvent.APPEND_LINE, "Creating Jena OntModel from TDB Model ... ");
		if( ontology.getLanguage() == OntologyLanguage.RDFS )
			model = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, basemodel );
		else
			model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, basemodel );
		fireEvent(ProgressEvent.APPEND_LINE, "done.");
		
		if( !onDiskPersistent ) {
			// we're not running in persistent mode, remove everything and load the ontology.
			fireEvent(ProgressEvent.APPEND_LINE, "Disk ontology is not persistent.\nClearing on disk ontology ... ");
			model.removeAll();
			fireEvent(ProgressEvent.APPEND_LINE, "done.");
			fireEvent(ProgressEvent.APPEND_LINE, "Reading ontology onto disk ... ");
			model.read( ontURI.toString(), null, ontology.getFormat().toString() );
			model.commit();
			fireEvent(ProgressEvent.APPEND_LINE, "done.");
		} 
		else if( model.isEmpty() ) {
			// we're running in persistent mode, load the ontology only if the model is empty
			fireEvent(ProgressEvent.APPEND_LINE, "Disk ontology is persistent and empty.\nReading ontology into on disk store ... ");
			model.read( ontURI.toString(), null, ontology.getFormat().toString() );
			model.commit();
			fireEvent(ProgressEvent.APPEND_LINE, "done.");
		} else {
			fireEvent(ProgressEvent.APPEND_LINE, "Disk ontology is persistent.\nUsing existing on disk ontology.");
		}
		
		ontology.setModel(model);
		
		//we can get this information only if we are working with RDF/XML format, using this on N3 you'll get null pointer exception you need to use an input different from ""
		try {//if we can't access the namespace of the ontology we can't skip nodes with others namespaces
			ns = model.getNsPrefixMap().get("").toString();
			ontology.setURI(ns);
		}
		catch(Exception e) {
			skipOtherNamespaces = false;
			ontology.setURI("");
		}
		ontology.setSkipOtherNamespaces(skipOtherNamespaces);

		fireEvent(ProgressEvent.APPEND_LINE, "Creating AgreementMaker data structures ... ");
		
		
		//Preparing model
		model.prepare();		
		
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
        

        Node classRoot = buildClassTree();
        ontology.setOntResource2NodeMap( processedSubs, alignType.aligningClasses );
        Node propertyRoot = createPropertyTree();
        ontology.setOntResource2NodeMap( processedSubs, alignType.aligningProperties );
        
        //The root of the tree is a fake vertex node, just containing the name of the ontology,
        //treeRoot = new Vertex(ontology.getTitle(),ontology.getTitle(), model, ontology.getSourceOrTarget() );
        treeRoot = new AMNode((Resource)null, -1, ontology.getTitle(), AMNode.XMLNODE, ontology.getID() );
        treeCount++;

        treeRoot.addChild(classRoot);
        ontology.setClassesRoot( classRoot);
        
        treeRoot.addChild(propertyRoot);
        ontology.setPropertiesRoot( propertyRoot);
        
        ontology.setTreeCount(treeCount);   

        fireEvent(ProgressEvent.APPEND_LINE, "done.");
	}
	
	/**
	 * This method will build the class tree by iterating through all the classes in the ontology and
	 * filling in their parents.
	 * 
	 * If a class has no parents, then the root of the tree will be its parent.
	 * 
	 * (This method may be hard to understand. -cos)
	 * @return
	 */
	protected Node buildClassTree() {
		
		HashMap<OntClass, Node> classesMap = new HashMap<OntClass, Node>();  // this maps between ontology classes and Vertices created for the each class
		ExtendedIterator orphansItr = model.listClasses();  // right now the classes have no parents, so they are orphans.
		
		while( orphansItr.hasNext() ) { // iterate through all the classes
			
			OntClass currentOrphan = (OntClass) orphansItr.next();  // the current class we are looking at
			
			if( !currentOrphan.isAnon() ) {  // make sure this is a real class  (anoynymous classes are not real classes)
				createFosterHome( currentOrphan, classesMap );  // assign orphan classes to parent parent classes
			}
		}
		
		// this is the root node of the class tree (think of it like owl:Thing)
		//Vertex root = new Vertex(CLASSROOTNAME, CLASSROOTNAME, model, ontology.getSourceOrTarget());
		Node root = new AMNode((Resource)null, -1, CLASSROOTNAME, AMNode.XMLNODE, ontology.getID() );
		treeCount++;  // we created a new vertex, increment treeCount

		// we may have classes that still don't have a parent. these orphans will be adopted by root.
		
		adoptRemainingOrphans( root, classesMap );
		
		return root;
	}
	

	private void adoptRemainingOrphans(Node root, HashMap<OntClass, Node> classesMap) {

		/*  // Alternative way of iterating through the classes (via the classesMap that was created).
		 *   
		Set< Entry<OntClass, Vertex>> classesSet = classesMap.entrySet();
		Iterator<Entry<OntClass, Vertex>> classesItr = classesSet.iterator();
		
		while( classesItr.hasNext() ) {
			
		}
		*/
		
		// We will just iterate through the classes again, and find any remaining orphans
		ExtendedIterator classesItr = model.listClasses();
		
		while( classesItr.hasNext() ) {
			OntClass currentClass = (OntClass) classesItr.next();
			if( !currentClass.isAnon() ) {
				if( classesMap.containsKey(currentClass) ) {
					Node currentVertex = classesMap.get(currentClass);
					
					if( currentVertex.getParentCount() == 0 ) {
						// this vertex has no parent, that means root needs to adopt it
						root.addChild( currentVertex );
					}
					
				}
				else {
					// we should never get here
					// if we do, it means we _somehow_ missed a class during our first iteration in buildClassTree();
					System.err.println("Assertion failed: listClasses() returning different classes between calls.");
				}
				 
			}
			
		}
		
	}


	// TODO: Merge this with the hierarchy roots function in LegacyLayout
	private void createFosterHome( OntClass currentOrphan, HashMap<OntClass, Node> classesMap ) {
		
		//Vertex currentVertex = getVertexFromClass( classesMap, currentOrphan );
		Node currentNode = getNodeFromClass( classesMap, currentOrphan );
		
		try { 
			ExtendedIterator<?> parentsItr = currentOrphan.listSuperClasses( true );  // iterator of the current class' parents
			
			while( parentsItr.hasNext() ) {
				
				OntClass parentClass = (OntClass) parentsItr.next();

				if( !parentClass.isAnon() && !parentClass.equals(owlThing) ) {

					Node parentVertex = getNodeFromClass(classesMap, parentClass);  // create a new Vertex object or use an existing one.
					parentVertex.addChild( currentNode );  // create the parent link between the parent and the child
					currentNode.addParent(parentVertex);

				} 
			}
		} catch ( ConversionException ce ) {
			// could not convert currentOrphan to an ontClass.
			// we will assume that it has no superclass.
			return;
		}
		
	}

	private Node getNodeFromClass( HashMap<OntClass, Node> classesMap, OntClass currentClass ) {
		Node currentNode = null;
		
		if( classesMap.containsKey( currentClass ) ) {
			// we already have a Vertex for the currentClass (because it is the parent of some node)
			currentNode = classesMap.get( currentClass );
		} else {
			// we don't have a Vertex for the current class, create one;
			currentNode = createNode( currentClass, true );
			treeCount++;  // we created a new vertex, increment treeCount
			classesMap.put(currentClass, currentNode);
		}
		
		return currentNode;
	}
	
	
	public Set<OntResource> collect( ExtendedIterator<OntClass> i ) {
        Set<OntResource> set = new HashSet<OntResource>();
        while( i.hasNext() ) {
            OntResource res = i.next();
            if( res.isAnon() )
                continue;
            
            set.add( res );
        }
        
        return set;
    }
	
	/**
	 * helper Function for buildClassesTree()
	 * @param classesMap
	 * @param currentClass
	 * @return
	 */
	/*@Deprecated
	private Vertex getVertexFromClass( HashMap<OntClass, Vertex> classesMap, OntClass currentClass ) {
		Vertex currentVertex = null;
		
		if( classesMap.containsKey( currentClass ) ) {
			// we already have a Vertex for the currentClass (because it is the parent of some node)
			currentVertex = classesMap.get( currentClass );
		} else {
			// we don't have a Vertex for the current class, create one;
			currentVertex = createNodeAndVertex( currentClass, true, ontology.getSourceOrTarget());
			treeCount++;  // we created a new vertex, increment treeCount
			classesMap.put(currentClass, currentVertex);
		}
		
		return currentVertex;
	}
	
    public Set<OntResource> collect( ExtendedIterator i ) {
        Set<OntResource> set = new HashSet<OntResource>();
        while( i.hasNext() ) {
            OntResource res = (OntResource) i.next();
            if( res.isAnon() )
                continue;
            
            set.add( res );
        }
        
        return set;
    }*/
    
    /**
     * Create a root node for the given concepts and add child nodes for the subclasses. Return null
     * for owl:Nothing
     * 
     * @param concepts
     * @return
     */
   /* public Vertex createClassTree( OntClass cls, boolean isFirst) {
    	
    	
    	if( unsatConcepts.contains( cls ) ) {
    		return null;
    	}
    	Vertex root;
    	if(isFirst)//fake vertex with written "OWL Class hierarchy"
    		root = new Vertex(CLASSROOTNAME, CLASSROOTNAME, model, ontology.getSourceOrTarget());
    	else root = createNodeAndVertex(cls, true, ontology.getSourceOrTarget());//normal vertex and node creation of a class concept (true)
		treeCount++;
		
       // If one of the sons of this class is a classes with different namespace
	   // we have to skip it, but we may still have to load his sons.
		// in that case we won't have only one iterator but more then one.
		//in all other cases iterators list will contain only the iterator on sons of this class
        ArrayList<ExtendedIterator> iterators = new ArrayList<ExtendedIterator>();
        // get only direct subclasses (true)
        ExtendedIterator firstSubs = cls.listSubClasses( true );
 
    
        	
        iterators.add(firstSubs);
        for(int i = 0; i<iterators.size(); i++) {
        	ExtendedIterator subs = iterators.get(i);
            while( subs.hasNext() ) {
                OntClass sub = (OntClass) subs.next();

                if( sub.isAnon() ) {
                	continue;
                }
                    
                //skip non valid classes with different namespace but consider sons
                if(skipOtherNamespaces && !sub.getNameSpace().toString().equals(ns)) {
             	   // get only direct subclasses (true)
             	   ExtendedIterator moreSubs = sub.listSubClasses( true );
             	   iterators.add(moreSubs);
             	   continue;
                }
                Vertex vert = createClassTree(sub, false);
                if( vert != null ) {
                   	//I add this other control because of a bug in OAEI benchmark 102
                	//A concept was both a son of OWL thing (so it was a root) and also a son of another concept
                	if(root.getNode() == null && vert.getNode().getVertexList().size() > 1){
                       vert.getNode().getVertexList().remove(vert);
                	}
                	else{ root.add( vert ); }

            	}
            }
        }
        return root;
    }*/
	
    
    public Node createNode( OntResource entity, boolean isClass ) {
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
	 * Each class is identified by a uniqueKey used as index in the ClassList structure
	 * Each prop is identified by a uniqueKey used as index in the propList structure
	 * @param entity
	 * @return
	 * 
	 * * TODO: Remove this commented out code and copy the documentation over to the classes that replaced this code. - Cosmin.
	 */
   /* @Deprecated
	public Vertex createNodeAndVertex(OntResource entity, boolean isClass,int sourceOrTarget) {
		 
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
         Vertex vert = new Vertex(node.getLocalName(), entity.getURI(), model, sourceOrTarget);
         node.addVertex(vert);
         vert.setNode(node);
		 return vert;
	}*/
	

    public Node createPropertyTree() {
    	
    	//Vertex root = new Vertex(PROPERTYROOTNAME, PROPERTYROOTNAME, model, ontology.getSourceOrTarget());
    	Node root = new AMNode((Resource)null, -1, PROPERTYROOTNAME, AMNode.XMLNODE, ontology.getID() );
    	treeCount++;
        uniqueKey = 0; //restart the key because properties are kept in a differnt structure with different index
        processedSubs = new HashMap<OntResource, Node>();
    	ExtendedIterator itobj = model.listObjectProperties();
    	ExtendedIterator itdata = model.listDatatypeProperties();
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
    		try {
    			if( p instanceof OntProperty ) {
		    		ExtendedIterator it2 = p.listSuperProperties();
		    		while(it2.hasNext()) {
		    			Object prop1 = it2.next();
		    			OntProperty superp = (OntProperty)prop1;
		    			if(!p.equals(superp) && !superp.isAnon() && !(skipOtherNamespaces && !superp.getNameSpace().toString().equals(ns))){//if we find a valid father in the superclass hierarchy we skip this property because is not a root
		    				skip = true;
		    				it2.close();  // iterators must be closed if they are not iterated until the end.
		    				break;
		    			}
		    		}
    			}
    		} catch( Exception e ) {
    			// TODO: Implement a workaround here.
    			//e.printStackTrace();
    		}
    		if(!skip) {
        		Node vp = createPropertySubTree(p);
        		root.addChild(vp);    			
    		}

    	}
        return root;
    }
    
    public Node createPropertySubTree( OntProperty p ) {
    	//Vertex root = createNodeAndVertex(p, false, ontology.getSourceOrTarget());
    	Node root = createNode(p, false);
		treeCount++;
		
       // If one of the sons of this prop is a prop with different namespace
	   // we have to skip it, but we may still have to load his sons.
		// in that case we won't have only one iterator but more then one.
		//in all other cases iterators list will contain only the iterator on sons of this prop
        ArrayList<ExtendedIterator> iterators = new ArrayList<ExtendedIterator>();
        // get only direct subproperties (direct = true)
        ExtendedIterator firstSubs;
        try {
        	firstSubs = p.listSubProperties( true );
        } catch ( Exception e ) {
        	e.printStackTrace();
        	return root;
        }
        
        iterators.add(firstSubs);
        for(int i = 0; i<iterators.size(); i++) {
        	ExtendedIterator subs = iterators.get(i);
            while( subs.hasNext() ) {
                OntProperty sub = (OntProperty) subs.next();

                if( sub.isAnon() )
                    continue;
                //skip non valid classes with different namespace but consider sons
                if(skipOtherNamespaces && !sub.getNameSpace().toString().equals(ns)) {
             	   // get only direct subclasses (true)
             	   ExtendedIterator moreSubs = sub.listSubProperties( true );
             	   iterators.add(moreSubs);
             	   continue;
                }
                Node vert = createPropertySubTree(sub);
                root.addChild( vert );
            }
        }
        return root;
    }
    
    /**
     * Upon creating an OntoTreeBuilder object, you can set the URI of the ontology you want to load.  If the URI is not set, it will be constructed from the fileName passed to the constructor.
     * @param URI The URI of the ontology to load, whether it be a local file or an internet address.
     */
    public void setURI( String uri ) throws Exception {
    	ontURI = new URI(uri);
    }

    
}