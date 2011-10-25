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
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Test4 {

	public static void main (String args[]) {

		//String ontResourceURL="http://www.w3.org/2001/sw/WebOnt/guide-src/wine";
		String ontResourceURL="file:C:\\Documents and Settings\\nalin\\Desktop\\MSProj\\Ontologies\\resume.owl";
    	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, null );


   	  	ontModel.read( ontResourceURL);
   	  	//Iterator it = getTopLevelClasses((Model)ontModel).iterator();
    	ExtendedIterator it = ontModel.listHierarchyRootClasses();
   	  	//Iterator it = findHierarchyRootClasses(ontModel);
    	Iterator<OntClass>it2 = getTopLevelClasses(it);
    	System.out.println("*** Inside main ***");
    	while(it2.hasNext()){
    		OntClass c = it2.next();
    		printClass(c);
    	}
    }
	
	public static Iterator<OntClass> getTopLevelClasses2 (Iterator i){
		Set<OntClass> named;
		OntClass c;
		boolean hasNamedSuperClass;
		named = new HashSet<OntClass>();
		
		while(i.hasNext()){
			c = (OntClass)i.next();
    		if(!c.isAnon() && c.canAs(OntClass.class)){
    			named.add(c);
    	    }
    	    else if(c.isRestriction()){
    	        ExtendedIterator ii = c.listSubClasses(true);
    	        while(ii.hasNext()){
    	        	OntClass cc = (OntClass)ii.next();
    	        	ExtendedIterator iii = cc.listSuperClasses(true);
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
	
	public static Iterator<OntClass> getTopLevelClasses (Iterator i){
		Set<OntClass> anon, named;
		OntClass c;
		boolean hasNamedSuperClass;
		anon = new HashSet<OntClass>();
		named = new HashSet<OntClass>();
		
		while(i.hasNext()){
			c = (OntClass) i.next();
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
			Iterator<OntClass> i1 = named.iterator();
			while(i1.hasNext()){
				c = i1.next();
				printClass(c);
				ExtendedIterator ii = c.listSuperClasses(true);
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
			Iterator<OntClass> i1 = anon.iterator();
			while(i1.hasNext()){
				c = i1.next();
				printClass(c);
				ExtendedIterator ii = c.listSubClasses(true);
				while(ii.hasNext())
					//named is being reused here to avoid creating another HashSet element
					named.add((OntClass)ii.next());	
			}
			return getTopLevelClasses(named.iterator());
		}
		
	}
 
	public static Set<Resource> getTopLevelClasses(Model model) {
		
		Set<Resource> metaclasses = new HashSet<Resource>();
		//metaclasses.add(RDFS.Class);
		metaclasses.add(OWL.Class);
		// TODO: Add other user-defined metaclasses...
		
		Set<Resource> results = new HashSet<Resource>();
		Iterator<Resource> it = metaclasses.iterator();
		while(it.hasNext()) {
			Resource metaclass = it.next();
			ExtendedIterator classes = model.listSubjectsWithProperty(RDF.type, metaclass);
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
    public static Iterator<OntClass> findHierarchyRootClasses(OntModel om) {
        Set<OntClass> roots = new HashSet<OntClass>();
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
    private static void expandAnonIterator(ExtendedIterator oci, Set<OntClass> accumulator) {
       while (oci.hasNext()) {
           OntClass oc = (OntClass) oci.next();
           if (oc.isAnon()) {
               ExtendedIterator i = oc.isUnionClass()
                 ? oc.asUnionClass().listOperands()
                 : oc.listSubClasses(true);
               expandAnonIterator(i, accumulator);
           } else {
               // Filter classes which have non-anon, non thing parents
               boolean ok = true;
               for (ExtendedIterator i = oc.listSuperClasses(); i.hasNext(); ) {
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