package test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.dig.DIGReasoner;
import com.hp.hpl.jena.reasoner.dig.DIGReasonerFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;


public class DIGExample
{
  public static void main( String[] args ) {
    // set up a configuration resource to connect to the reasoner
    // on port 2004 on the local system
    Model cModel = ModelFactory.createDefaultModel();
    Resource conf = cModel.createResource();
    conf.addProperty( ReasonerVocabulary.EXT_REASONER_URL, 
                      cModel.createResource( "http://localhost:8081" ) );
       
    // create the reasoner factory and the reasoner
    DIGReasonerFactory drf = (DIGReasonerFactory) ReasonerRegistry.theRegistry()
                                                                  .getFactory( DIGReasonerFactory.URI );
    DIGReasoner r = (DIGReasoner) drf.create( conf );
        
    // now make a model
    OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
    spec.setReasoner( r );
    OntModel ontModel = ModelFactory.createOntologyModel( spec, null );
        
    // load an input document
    //ontModel.read( "http://protege.stanford.edu/plugins/owl/owl-library/koala.owl" );
    //ontModel.read( "http://www.w3.org/2001/sw/WebOnt/guide-src/wine" );
    ontModel.read( "file:C:\\Documents and Settings\\nalin\\Desktop\\MSProj\\Ontologies\\ESG1.1.owl" );
	    
    // list the inconsistent classes
    /*StmtIterator i = m.listStatements( null, OWL.equivalentClass, OWL.Nothing );
    while (i.hasNext()) {
        System.out.println( "Class " + i.nextStatement().getSubject() + " is unsatisfiable" );
    }*/
  
      	Iterator it = ontModel.listHierarchyRootClasses();
     	  	//Iterator it = findHierarchyRootClasses(ontModel);
      	//it = getTopLevelClasses(it);
      	System.out.println("*** Inside main ***");
      	while(it.hasNext()){
      		OntClass c = (OntClass)it .next();
      		printClass(c);
      	}
      }
  	
  	public static Iterator getTopLevelClasses2 (Iterator i){
  		Set named;
  		OntClass c;
  		boolean hasNamedSuperClass;
  		named = new HashSet();
  		
  		while(i.hasNext()){
  			c = (OntClass)i.next();
      		if(!c.isAnon() && c.canAs(OntClass.class)){
      			named.add(c);
      	    }
      	    else if(c.isRestriction()){
      	        Iterator ii = c.listSubClasses(true);
      	        while(ii.hasNext()){
      	        	OntClass cc = (OntClass)ii.next();
      	        	Iterator iii = cc.listSuperClasses(true);
      	        	hasNamedSuperClass = false;
      	        	while(iii.hasNext()){
      	        		if(!((OntClass)iii.next()).isAnon())
      	        			hasNamedSuperClass = true;
      	        	}
      	        	if(!hasNamedSuperClass && !cc.isAnon() && cc.canAs(OntClass.class))
      	        		named.add(cc);
      	        }
      	        
      	    }
  		}
  		
  		return named.iterator();
  	}
  	
  	public static Iterator getTopLevelClasses (Iterator i){
  		Set anon, named;
  		OntClass c;
  		boolean hasNamedSuperClass;
  		anon = new HashSet();
  		named = new HashSet();
  		
  		while(i.hasNext()){
  			c = (OntClass)i.next();
      		if(c.isAnon()){
      			anon.add(c);
      	    }
      	    else {
      	        named.add(c);
      	    }
  		}
  		
  		if(!named.isEmpty()){
  			System.out.println("*** Inside named ***");
  			anon.clear();
  			i = named.iterator();
  			while(i.hasNext()){
  				c = (OntClass)i.next();
  				printClass(c);
  				Iterator ii = c.listSuperClasses(true);
  				hasNamedSuperClass = false;
  				while(ii.hasNext()){
  					OntClass ccc = (OntClass)ii.next();
  					printClass(ccc);
  					if(!(ccc).isAnon() && !ccc.getLocalName().equalsIgnoreCase("thing"))
  						hasNamedSuperClass = true;
  				}
  				if(!hasNamedSuperClass)
  					//anon is being reused to avoid declaration of another HashSet var
  					anon.add(c);
  			}
  			return anon.iterator();
  		}else if(anon.isEmpty()){
  			System.out.println("*** In anon ***");
  			return anon.iterator();
  		}else{
  			System.out.println("*** Repeating getTopLevelClasses ***");
  			named.clear();
  			i = anon.iterator();
  			while(i.hasNext()){
  				c = (OntClass)i.next();
  				printClass(c);
  				Iterator ii = c.listSubClasses(true);
  				while(ii.hasNext())
  					//named is being reused here to avoid creating another HashSet element
  					named.add((OntClass)ii.next());	
  			}
  			return getTopLevelClasses(named.iterator());
  		}
  		
  	}
   
  	public static Set getTopLevelClasses(Model model) {
  		
  		Set metaclasses = new HashSet();
  		//metaclasses.add(RDFS.Class);
  		metaclasses.add(OWL.Class);
  		// TODO: Add other user-defined metaclasses...
  		
  		Set results = new HashSet();
  		Iterator it = metaclasses.iterator();
  		while(it.hasNext()) {
  			Resource metaclass = (Resource) it.next();
  			Iterator classes = model.listSubjectsWithProperty(RDF.type, metaclass);
  			while(classes.hasNext()) {
  				Resource clazz = (Resource) classes.next();
  				if(clazz.isURIResource()) {
  					if(!model.contains(clazz, RDFS.subClassOf)) {
  						results.add(clazz);
  					}
  				}
  			}
  		}
  		
  		return results;
  	}

  	  /**
       * Find the root classes, skipping any anonymous union/intersections.
       */
      public static Iterator findHierarchyRootClasses(OntModel om) {
          Set roots = new HashSet();
          expandAnonIterator(om.listHierarchyRootClasses(), roots);
          return roots.iterator();
      }

      /**
       * Walk the given class iterator putting the root classes into the
  accumulator.
       * Any union or restriction classes are expanded to find the next
  level named
       * classes.
       */
      private static void expandAnonIterator(Iterator oci, Set accumulator) {
         while (oci.hasNext()) {
             OntClass oc = (OntClass)oci.next();
             if (oc.isAnon()) {
                 Iterator i = oc.isUnionClass()
                   ? oc.asUnionClass().listOperands()
                   : oc.listSubClasses(true);
                 expandAnonIterator(i, accumulator);
             } else {
                 // Filter classes which have non-anon, non thing parents
                 boolean ok = true;
                 for (Iterator i = oc.listSuperClasses(); i.hasNext(); ) {
                     OntClass soc = (OntClass)i.next();
                     if ( ! soc.isAnon() && ! soc.equals(RDFS.Resource)
                                   && ! soc.equals(OWL.Thing)) {
                         ok = false; break;
                     }
                 }
                 if (ok) accumulator.add(oc);
             }
         }
      }

      private static void printClass (OntClass c){
  		if(!c.isAnon()){
  			System.out.println("HierarchyRootClasses " +c.getLocalName());
  	    }
  	    else {
  	        System.out.println( "Anon. hierarchy root: " + c.getId() );
  	    }

      }
  }