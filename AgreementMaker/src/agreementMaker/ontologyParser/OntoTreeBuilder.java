package agreementMaker.ontologyParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.QNameProvider;

import agreementMaker.userInterface.vertex.Vertex;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
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
	
	static final long serialVersionUID = 1;
	//instance variables

	private QNameProvider qnames;
	private OntModel model;
	private Set unsatConcepts;  
	private Set processedSubs;

	
	/*This variable has been introduced to solve a problem occurred loading OAEI test case ontology in RDF/XML format
	 * These ontologies contains some referenced classes and properties of other namespaces
	 * These classes shouldn't be considered in the matching and we don't want to load them. 
	 * The parser will skip to load this classes if this boolean variable is true, so it has to be true at least for RDF/XML format
	 * In the N3 format can't be true because the namespace organization is different 
	 * ATTENTION: we skip from loading the referenced classes but their sons in the hierarchy may be valid classes. 
	 */
	private boolean skipOtherNamespaces = false;
	/* To get the namespace of the loaded ontologies we use the method model.getNsPrefixMapping.get("")
	 * This method cannot be used with "" input for N3
	 */
	private String ns = null;
	
	public OntoTreeBuilder(String fileName, int syntaxIndex) {
		
		treeCount = 0;
		
		
		String fileExt = "RDF/XML";
		if(syntaxIndex == 0){
			fileExt = "RDF/XML";
			skipOtherNamespaces = true;//view comments on the variable declaration
		}else if(syntaxIndex == 1){
			fileExt = "RDF/XML-ABBREV";
		}else if(syntaxIndex == 2){
			fileExt = "N-TRIPLE";
		}else if(syntaxIndex == 3){
			fileExt = "N3";
		}else if(syntaxIndex == 4){
			fileExt = "N3";
		}

		System.out.print("Reading Model...");
		model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
		
		
		//TODO: Figure out if the 2nd arg in next fn call should be null or someother URI
		model.read( "file:"+fileName, null, fileExt );
		System.out.println("done");
		
		if(skipOtherNamespaces) //we can get this information only if we are working with RDF/XML format, using this on N3 you'll get null pointer exception you need to use an input different from ""
			ns = model.getNsPrefixMap().get("").toString();
		
		
		//Preparing model
		model.prepare();
		
		// compute the classification tree
		System.out.print("Classifying...");
		((PelletInfGraph) model.getGraph()).getKB().classify();
		//reasoner.classify();
		System.out.println("done");
		
		buildTree();
		System.out.println("Total number of classes in the tree hierarchy: "+treeCount);
		
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
        OntClass owlThing = model.getOntClass( OWL.Thing.getURI() );
        OntClass owlNothing = model.getOntClass( OWL.Nothing.getURI() );

        // Find all unsatisfiable concepts, i.e classes equivalent
        // to owl:Nothing
        unsatConcepts = collect( owlNothing.listEquivalentClasses() );

        // create a tree starting with owl:Thing node as the root
        //if a node has two fathers (is possible in OWL hierarchy) it would be processed twice
        //but we don't want to add two times the node to the hierarchy, so processedSubs won't be processed again.
        processedSubs = new HashSet();
        Vertex thing = createTree(owlThing);
        
        Iterator i = unsatConcepts.iterator();
        if( i.hasNext() ) {
            // We want to display every unsatisfiable concept as a
            // different node in the tree
            Vertex nothing = createNode( owlNothing );

            // iterate through unsatisfiable concepts and add them to
            // the tree
            while( i.hasNext() ) {
                OntClass unsat = (OntClass) i.next();

                if( unsat.equals( owlNothing ) )
                    continue;

                
                Vertex node = createNode( unsat );
                nothing.add( node );
                treeCount++;
            }
            
            if(nothing.getChildCount()> 0) {
                // add nothing as a child node to owl:Thing
                thing.add( nothing );        
                treeCount++;
            }

        }

        treeRoot = thing;
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
    Vertex createTree( OntClass cls ) {
    	if( unsatConcepts.contains( cls ) )
            return null;
        processedSubs = new HashSet();
        Vertex root = createNode(cls);
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

                if( processedSubs.contains( sub ) )
                    continue;
                
                //skip non valid classes with different namespace but consider sons
               if(skipOtherNamespaces && !sub.getNameSpace().toString().equals(ns)) {
            	   // get only direct subclasses (true)
            	   Iterator moreSubs = sub.listSubClasses( true );
            	   iterators.add(moreSubs);
            	   continue;
               }
                Vertex son = createTree(sub);
                // if set contains owl:Nothing tree will be null
                if( son != null ) {
                    root.add( son );
                    
                }
            }
        }


        return root;
    }
	
	/**
	 * Create a TreeNode for the given class
	 * 
	 * @param entity
	 * @param label
	 * @return
	 */
	Vertex createNode(String label, Resource entity) {
		return new Vertex(label, entity.getURI(), model);
	}
	
	/**
	 * Create a TreeNode for the given class
	 * 
	 * @param entity
	 * @return
	 */
	Vertex createNode(Resource entity) {
		String label;
		 if(entity.canAs(OntClass.class))
				label=((OntClass)entity).getLocalName();
		else
			label= qnames.shortForm(entity.getURI());
		 
		return new Vertex(label, entity.getURI(), model);
	}
	
}