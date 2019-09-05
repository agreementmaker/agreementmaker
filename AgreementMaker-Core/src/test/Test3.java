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

public class Test3 {

	public static void main (String args[]) {

		//String ontResourceURL="http://www.bpiresearch.com/BPMO/2004/03/03/cdl/Countries";
		String ontResourceURL="file:C:\\Documents and Settings\\itlmaint\\Desktop\\Ontologies\\not-galen.owl";
    	OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, null );


   	  	ontModel.read( ontResourceURL);
   	  	//Iterator it = getTopLevelClasses((Model)ontModel).iterator();
    	ExtendedIterator it = ontModel.listHierarchyRootClasses();
   	  	while(it .hasNext()){
    		OntClass c = (OntClass)it .next();
    		if(!c.isAnon()){
    			System.out.println("HierarchyRootClasses " +c.getLocalName());
    	    }
    	    else {
    	        System.out.println( "Anon. hierarchy root: " + c.getId() );
    	    }
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
}