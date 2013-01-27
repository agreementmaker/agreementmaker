
package test;

import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class MainDescribeClass {
    
    public static void main( String[] args ) {
        // read the argument file, or the default
        String source = (args.length == 0) ? "file:/root/Desktop/mgedontology.rdfs" : args[0];

        // guess if we're using a daml source
        boolean isDAML = source.endsWith( ".daml" );

        /*OntModel m = ModelFactory.createOntologyModel(
                        isDAML ? OntModelSpec.DAML_MEM : OntModelSpec.OWL_MEM, null
                     );*/
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        
        // read the source document
        m.read(source);

        DescribeClass dc = new DescribeClass();
        if(isDAML) System.out.println("??");
        
        for (Iterator<OntClass> i = m.listClasses();  i.hasNext(); ) {
            // now list the classes
          	System.out.println("//");
            dc.describeClass( System.out, (OntClass) i.next() );
        }
        
    }
}

